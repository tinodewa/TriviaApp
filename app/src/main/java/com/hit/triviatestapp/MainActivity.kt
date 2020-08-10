package com.hit.triviatestapp

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import com.hit.triviatestapp.controller.ServiceController
import com.hit.triviatestapp.controller.apiRequest
import com.hit.triviatestapp.controller.httpClient
import com.hit.triviatestapp.model.Question
import com.hit.triviatestapp.model.Result
import com.hit.triviatestapp.utils.dismissLoading
import com.hit.triviatestapp.utils.showLoading
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var questionNumber: Int = 1
    var score: Int = 0
    lateinit var questionList: List<Result>
    val txtTrue: String = "True"
    val txtFalse: String = "False"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        callApiGetQuestion()

        btn_true.setOnClickListener(this)
        btn_false.setOnClickListener(this)
        btn_next.setOnClickListener(this)

    }

    private fun callApiGetQuestion() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        showLoading(this, layout_srl)

        val httpClient = httpClient()
        val apiRequest = apiRequest<ServiceController>(httpClient)

        val call = apiRequest.getQuestion()
        call.enqueue(object : Callback<Question> {

            override fun onFailure(call: Call<Question>, t: Throwable) {
                Log.e("Error", "onFailure: $t")
                Toast.makeText(this@MainActivity, "Gagal :(", Toast.LENGTH_SHORT).show()
                dismissLoading(layout_srl)
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            override fun onResponse(
                call: Call<Question>,
                response: Response<Question>
            ) {
                dismissLoading(layout_srl)
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                when {
                    response.isSuccessful ->
//                        setQuestion(response.body()!!.results)
                        setData(response.body()!!.results)
                    else -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Gagal mendapatkan data :(",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun setData(quest: List<Result>) {
        questionList = quest
        Html.fromHtml(questionList.toString())
        txt_question.text = Html.fromHtml(questionList[0].question)
        setQuestionNumber()
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_true -> {
                checkQuestion(txtTrue)
            }
            btn_false -> {
                checkQuestion(txtFalse)
            }
            btn_next -> {
                incrementQuestionNumber()
                setQuestion()
                setQuestionNumber()
                enableButton()
                enableRadioButton(true)
                navigateToResult()
            }
        }
    }

    private fun navigateToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        val bundle = Bundle()
        bundle.putString("score", score.toString())

        intent.putExtras(bundle)
        if (questionNumber == 20) {
            btn_next.setOnClickListener {
                startActivity(intent)
                onDestroy()
            }
        }
    }

    private fun checkQuestion(answerState: String) {
        when (val answer = questionList[questionNumber - 1].correctAnswer) {
            answerState -> {
                score += 5
                Toast.makeText(this, "Correct answer! :)", Toast.LENGTH_SHORT).show()
                fadeAnimation()
                setQuestion()
            }
            else -> {
                Toast.makeText(this, "Wrong answer :(", Toast.LENGTH_SHORT).show()
                shakeAnimation()
                setQuestion()
            }
        }
        enableRadioButton(false)
    }

    private fun setQuestion() {
        txt_question.text = Html.fromHtml(questionList[questionNumber - 1].question)
    }

    private fun setQuestionNumber() {
        txt_question_number.text = questionNumber.toString()
        progressBar.progress = questionNumber * 5
    }

    private fun incrementQuestionNumber() {
        if (questionNumber < 20) {
            questionNumber++
        }
    }

    private fun enableButton() {
        group_radio.clearCheck()
//        btn_next.isEnabled = questionNumber != 20
        if (questionNumber == 20) {
            btn_next.text = "Finish"
        }
    }

    private fun enableRadioButton(boolean: Boolean) {
        btn_true.isEnabled = boolean
        btn_false.isEnabled = boolean
    }

    private fun fadeAnimation() {
        val alphaAnimation: AlphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration = 250
        alphaAnimation.repeatCount = 1
        alphaAnimation.repeatMode = Animation.REVERSE
        box_question.animation = alphaAnimation

        alphaAnimation.setAnimationListener(object : AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                box_question.setCardBackgroundColor(getResources().getColor(R.color.colorWhite))
            }

            override fun onAnimationStart(p0: Animation?) {
                box_question.setCardBackgroundColor(getResources().getColor(R.color.colorGreen))
            }

        })

    }

    private fun shakeAnimation() {
        val shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation)
        box_question.animation = shake
        shake.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                box_question.setCardBackgroundColor(getResources().getColor(R.color.colorWhite))
            }

            override fun onAnimationStart(p0: Animation?) {
                box_question.setCardBackgroundColor(getResources().getColor(R.color.colorRed))
            }

        })
    }
}

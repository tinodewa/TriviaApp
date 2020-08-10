package com.hit.triviatestapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        supportActionBar?.hide()
        setScore()

        btn_restart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            onDestroy()
        }
    }

    private fun setScore() {
        val result = intent.extras
        txt_final_score.text = result?.getString("score").toString()
    }
}
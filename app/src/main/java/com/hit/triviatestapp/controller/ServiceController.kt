package com.hit.triviatestapp.controller

import com.hit.triviatestapp.model.Question
import retrofit2.Call
import retrofit2.http.GET

interface ServiceController {

    @GET("api.php?amount=20&type=boolean")
    fun getQuestion(): Call<Question>
}

package com.hit.triviatestapp.model


import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("results")
    val results: List<Result>
)
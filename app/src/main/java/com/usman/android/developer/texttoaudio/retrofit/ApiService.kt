package com.usman.android.developer.texttoaudio.retrofit

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/tts/stream")
    fun generateSpeech(@Body requestBody: RequestBody): Call<ResponseBody>
}
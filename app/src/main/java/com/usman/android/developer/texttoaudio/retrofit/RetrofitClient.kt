package com.usman.android.developer.texttoaudio.retrofit

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.usman.android.developer.texttoaudio.utils.AppFunctions
import com.usman.android.developer.texttoaudio.utils.Constants
import java.util.concurrent.TimeUnit


/**
 Text
 */
object RetrofitClient {



    private val instance: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun generateSpeech(
        context: Context,
        requestBody: RequestBody,
        onAudioCompleted: () -> Unit
    ) {
        if (!AppFunctions.isNetworkAvailable(context)) {
            Log.e("API Error", "No internet connection available")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "No internet connection available", Toast.LENGTH_SHORT)
                    .show()
            }
            return
        }

        val startTime = System.currentTimeMillis()

        try {
            val response = withContext(Dispatchers.IO) {
                instance.generateSpeech(requestBody).execute()
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.e("API Response Time", "Time taken for API response: $elapsedTime ms")

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.e("API isSuccessful", "Successfully generated speech: $response")
                    response.body()?.byteStream()?.let {
                        AppFunctions.playAudio(it, context) { onAudioCompleted() }
                    } ?: Toast.makeText(context, "No audio received", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(
                        "API Error",
                        "Failed to generate speech: ${response.errorBody()?.string()}"
                    )
                    Toast.makeText(context, "Failed to generate speech", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("API Error", "Request failed: ${e.message}")
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to generate speech", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createRequestBody(text: String, voice: String): RequestBody {
        val json = JSONObject().apply {
            put("text", text)
            put("voice", voice)
        }
        return json.toString().toRequestBody("application/json".toMediaTypeOrNull())
    }
}
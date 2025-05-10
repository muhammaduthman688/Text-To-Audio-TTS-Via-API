package com.usman.android.developer.texttoaudio.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.usman.android.developer.texttoaudio.retrofit.RetrofitClient
import com.usman.android.developer.texttoaudio.databinding.ActivityMainBinding
import com.usman.android.developer.texttoaudio.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.speakButton.setOnClickListener {


            val textToRead = binding.tv.text.toString()

            if (textToRead.isEmpty()) {
                Toast.makeText(this, "Please enter text to speak", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create request body
            val requestBody = RetrofitClient.createRequestBody(textToRead, Constants.voice)
            lifecycleScope.launch(Dispatchers.Main) {
                RetrofitClient.generateSpeech(this@MainActivity,
                    requestBody
                ) {}
            }
            binding.btnVoice1.setOnClickListener {
                Constants.voice="Enter Voice Code 1 which you want to select"
                //Like this
                Constants.voice="Willem (Male, ZA)"
            }
            binding.btnVoice2.setOnClickListener {
                Constants.voice="Enter Voice Code 2 which you want to select"
            }
            binding.btnVoice3.setOnClickListener {
                Constants.voice="Enter Voice Code 3 which you want to select"
            }
            binding.btnVoice4.setOnClickListener {
                Constants.voice="Enter Voice Code 4 which you want to select"
            }

        }
    }


}
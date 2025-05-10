package com.usman.android.developer.texttoaudio.utils

import android.content.Context
import android.widget.Toast

import java.io.File
import android.app.Activity
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class AppFunctions {

    companion object {

        // To play the Voice
        fun playAudio(
            audioStream: InputStream, context: Context,
            onAudioCompleted: () -> Unit
        ) {
            val playStartTime = System.currentTimeMillis()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tempFile = File.createTempFile("audio", ".tmp", context.cacheDir)
                    FileOutputStream(tempFile).use { outputStream ->
                        audioStream.use { input ->
                            input.copyTo(outputStream)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val mediaPlayer = MediaPlayer()
                        mediaPlayer.setDataSource(tempFile.absolutePath)
                        mediaPlayer.prepare()
                        mediaPlayer.setOnPreparedListener {
                            val playEndTime = System.currentTimeMillis()
                            Log.e(
                                "Audio Playback Time",
                                "Time taken to start playing: ${playEndTime - playStartTime} ms"
                            )
                            it.start()
                        }
                        mediaPlayer.setOnCompletionListener {
                            onAudioCompleted()
                            tempFile.delete()
                            mediaPlayer.release()
                        }
                        mediaPlayer.setOnErrorListener { mp, _, _ ->
                            tempFile.delete()
                            mp.release()
                            if (context is Activity) {
                                context.runOnUiThread {
                                    Toast.makeText(
                                        context,
                                        "Failed to play audio",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            true
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    if (context is Activity) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Failed to play audio", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        //to check the internet connection available or not for API Response
        // It need permission in manifest  android.permission.ACCESS_NETWORK_STATE
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }


    }


}
package com.example.k2025_03_25_basic_radio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.k2025_03_25_basic_radio.ui.theme.K2025_03_25_basic_radioTheme

val url = "http://stream.whus.org:8000/whusfm"

class MainActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private var radioOn: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setUpRadio()
        setContent {
            K2025_03_25_basic_radioTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Radio!!",
                        )
                    Button(onClick = {
                        Log.i("PGB", "Radio start");
                        mediaPlayer.start() }) {
                        Text(text = "Start Radio")
                    }

                }
            }
        }
    }

    private fun setUpRadio() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepare()
        }
    }
}


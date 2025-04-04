package com.example.k2025_03_25_basic_radio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.k2025_03_25_basic_radio.ui.theme.K2025_03_25_basic_radioTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp


data class RadioStation(
    val name: String,
    val url: String,
    val imageRes: Int  // Icon Image
)

class MainActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    // A HandlerThread and Handler to run MediaPlayer
    private lateinit var radioThread: HandlerThread
    private lateinit var radioHandler: Handler

    private val radioStations = listOf(
        RadioStation("UCONN radio", "http://stream.whus.org:8000/whusfm", R.drawable.whus),
        RadioStation("Classic Vinyl HD", "https://icecast.walmradio.com:8443/classic", R.drawable.classic_vinyl),
        RadioStation("Dance Wave", "http://stream.dancewave.online:8080/", R.drawable.dance_wave),
        RadioStation("2000s", "https://0n-2000s.radionetz.de/0n-2000s.mp3", R.drawable.y2k),
        RadioStation("Europa Plus", "http://ep256.hostingradio.ru:8052/europaplus256.mp3", R.drawable.europa_plus),
        RadioStation("Deep House", "http://198.15.94.34:8006/stream", R.drawable.deep_house),
        RadioStation("Sleeping Pill", "http://radio.stereoscenic.com/asp-h", R.drawable.sleeping_pill),
        RadioStation("Outlaw Country", "http://ice10.outlaw.fm/KIEV2", R.drawable.outlaw_country),
        RadioStation("CNN", "https://tunein.cdnstream1.com/2868_96.mp3", R.drawable.cnn),
        RadioStation("Radio Bollywood", "https://stream.zeno.fm/rm4i9pdex3cuv/", R.drawable.radio_bollywood)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the background thread
        radioThread = HandlerThread("RadioThread").apply { start() }
        radioHandler = Handler(radioThread.looper)

        mediaPlayer = MediaPlayer()

        setContent {
            K2025_03_25_basic_radioTheme {
                RadioScreen(
                    stations = radioStations,
                    onStationSelected = { station ->
                        // Switch to the selected station
                        radioHandler.post { playRadio(station.url) }
                    },
                    onStop = {
                        radioHandler.post { stopRadio() }
                    }
                )
            }
        }
    }

    private fun playRadio(url: String) {
        try {
            // Stop current playback
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()

            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { mp ->
                mp.start()
            }
            // Prepare asynch so the UI thread is not blocked
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRadio() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        radioHandler.post { mediaPlayer.release() }
        radioThread.quitSafely()
    }
}

@Composable
fun RadioScreen(
    stations: List<RadioStation>,
    onStationSelected: (RadioStation) -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Radio Station",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(stations) { station ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStationSelected(station) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = station.imageRes),
                        contentDescription = station.name,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 24.sp
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onStop,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Stop Radio")
        }
    }
}

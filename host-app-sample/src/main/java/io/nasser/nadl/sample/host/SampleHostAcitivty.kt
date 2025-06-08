package io.nasser.nadl.sample.host

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.nasser.compose.ui.ComposeStarterImpl

class SampleHostAcitivty : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeStarterImpl().getStarter().invoke()
        }
    }
}
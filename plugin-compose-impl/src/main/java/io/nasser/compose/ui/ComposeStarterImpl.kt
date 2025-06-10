package io.nasser.compose.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import io.nasser.compose.ui.theme.NadlProjTheme
import io.nasser.mylib.api.plugin.ComposeStarter

class ComposeStarterImpl : ComposeStarter {

    override fun getStarter(bundle: Bundle?): @Composable () -> Unit {
        return {
            Start(bundle)
        }
    }

    @Composable
    fun Start(bundle: Bundle?) {
        NadlProjTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
//                MyComposeScreen(arguments = bundle)
                ResumeCounter(bundle)
            }
        }
    }
}

@Composable
fun ResumeCounter(arguments: Bundle?) {
//    var counter by remember { mutableIntStateOf(0) }
//
//    // This will observe lifecycle events and increment counter when resumed
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(lifecycleOwner) {
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_RESUME) {
//                counter++
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = "Welcome compose!: ${arguments?.getString("centerTextMessage")} "
//        + "\n onResume count: $counter"
        Text(
            text = text,
            color = Color.Gray
        )
    }
}

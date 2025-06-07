package io.nasser.composesamplemodule.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import io.nasser.composesamplemodule.ui.theme.NadlProjTheme

class ComposeSampleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("ComposeSampleFragment", "onCreateView")
        return ComposeView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

            Log.d("ComposeSampleFragment", "setContent")
            setContent {
                NadlProjTheme {
                    MyComposeScreen(arguments = Bundle(arguments))
                }
            }
        }
    }
}

@Composable
fun MyComposeScreen(arguments: Bundle?) {
    Column(Modifier
        .fillMaxSize()
        .background(color = Color.Blue)) {
        Text(text = "Welcome in compose!: ${arguments?.getString("centerTextMessage")}")
    }
}
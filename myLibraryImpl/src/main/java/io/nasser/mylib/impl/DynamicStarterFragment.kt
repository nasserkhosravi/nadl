package io.nasser.mylib.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.nasser.mylibrary.R

class DynamicStarterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_dynamic_starter, container, false).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.zzBackColor))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_text)?.apply {
            text = arguments?.getString("centerTextMessage")
        }
    }
}
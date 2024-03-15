package io.nasser.mylib.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.nasser.mylibrary.R

class DynamicStarterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //xml layout test
        return inflater.inflate(R.layout.fragment_dynamic_starter, container, false).apply {
            //color test
            setBackgroundColor(ContextCompat.getColor(context, R.color.zzBackColor))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //id resource test
        view.findViewById<TextView>(R.id.tv_text)?.apply {
            // bundle string test
            text = arguments?.getString("centerTextMessage")
                //string resource test
                .plus("\n in: ").plus(getString(R.string.lib_name))
        }

        view.findViewById<ImageView>(R.id.img)?.apply {
            //loading drawable test
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_arrow_back_ios_new_24))
        }
    }
}
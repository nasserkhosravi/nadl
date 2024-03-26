package io.nasser.mylib.impl

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import androidx.fragment.app.Fragment
import io.nasser.mylibrary.R

class StarterFragmentBasic : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //xml layout test
        return LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            //color test
            setBackgroundColor(ContextCompat.getColor(context, R.color.zzBackColor))

            addView(TextView(context).apply {
                layoutParams =
                    LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                        .apply {
                            gravity = Gravity.CENTER
                        }
                id = R.id.tv_text
            })
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

            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            val font = getFont(R.font.inter_medium)
            setTypeface(font)
        }
    }
}
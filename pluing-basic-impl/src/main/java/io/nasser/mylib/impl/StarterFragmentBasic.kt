package io.nasser.mylib.impl

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.core.graphics.toColorInt
import io.nasser.mylib.api.host.HostFont
import io.nasser.mylib.api.host.HostBridgeApi
import io.nasser.mylib.api.host.HostDeeplinkValues

/**
 * This starter is a sample that show android views without xml, and resources
 */
class StarterFragmentBasic : Fragment() {

    private var resumeNumber: Int = 0
    private val resourceManager = SelfResourceManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            //color test
            setBackgroundColor(getColorValue(Colors.zzBackColor))

            addView(TextView(context).apply {
                layoutParams =
                    LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                        .apply {
                            gravity = Gravity.CENTER
                        }
                setTextColor(getColorValue(Colors.white))

                id = ViewIds.tvText
            })
            addView(AppCompatButton(context).apply {
                layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                id = ViewIds.btnCallback
                setTextColor(getColorValue(Colors.black))
            })

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //id resource test
        view.findViewById<TextView>(ViewIds.tvText)?.apply {
            text = createText(0)
            val font = getFontFromHost(HostFont.InterMedium)
            setTypeface(font)
        }
        view.findViewById<TextView>(ViewIds.btnCallback)?.apply {
            // bundle string test
            text = "call finish-activity deeplink action"

            val font = getFontFromHost(HostFont.InterMedium)
            setTypeface(font)

            setOnClickListener {
                (context as HostBridgeApi).handleDeeplink(context, HostDeeplinkValues.finishActivity())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeNumber++
        view?.findViewById<TextView>(ViewIds.tvText)?.apply {
            text = createText(resumeNumber)
        }
    }

    private fun createText(onResumeNumber: Int): String {
        return arguments?.getString("centerTextMessage")
            //string resource test
            .plus("\n lib_name res: ").plus(getStringValue(StringIds.libName))
            .plus("\n onResume count: ").plus(onResumeNumber)

    }

    fun getColorValue(resId: ColorId): Int {
        return resourceManager.getColor(resId)
    }

    fun getStringValue(id: Int): String {
        return resourceManager.getString(id)
    }

}
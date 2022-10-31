package com.anubis.module_view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_view.eView.Companion.eIView
import com.tamsiree.rxkit.eWeb
import kotlinx.android.synthetic.main.activity_test_view.*
import kotlinx.android.synthetic.main.sample_web_view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_view)


        eIView.eInitDrawer(this, R.layout.sample_web_view) {
            eIView.eInitWeb(this,sample_wvBase!!, object : eWeb.OnWebViewLoad {
                override fun onPageStarted() {
                    sample_pbBase.visibility = View.VISIBLE
                }

                override fun onReceivedTitle(title: String) {
                    eLog("title:$title")
                }

                override fun onProgressChanged(newProgress: Int) {
                    sample_pbBase.progress = newProgress
                }

                override fun shouldOverrideUrlLoading() {
                }

                override fun onPageFinished() {
                    sample_pbBase.visibility = View.GONE
                }
            },"https://github.com/AnubisASN/KT_SAK")
        }
        eIView.eInitWave(wave, 0.3f, 1f)
        eIView.eInitShineButtonView(this, sbt)
        eIView.eInitBubble(imageView2, bubble_layout)
    }

    fun onClick(v: View) {
        var i = 99f
        when (v) {
            button8 -> GlobalScope.launch {
                while (i != 100f) {
                    eLog(i)
                    i--
                    runOnUiThread { wave.eWaterLevelRatio = i / 100f }
                    delay(500)
                }
            }
            button9-> eShowTip(bt.textNum)
            imageView -> eIView.eInitCaptchaCode(imageView)
        }
    }


}

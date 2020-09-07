package com.anubis.module_lottie

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.anubis.kt_extends.eLog
import kotlinx.android.synthetic.main.demo.*
import org.jetbrains.anko.onClick
import android.animation.ValueAnimator
import android.os.Handler
import androidx.core.content.ContextCompat.getSystemService
import com.airbnb.lottie.model.layer.Layer
import com.airbnb.lottie.value.LottieValueCallback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.airbnb.lottie.value.LottieFrameInfo
import com.airbnb.lottie.value.SimpleLottieValueCallback
import android.text.AndroidCharacter.mirror
import com.airbnb.lottie.utils.MiscUtils.resolveKeyPath
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieOnCompositionLoadedListener




class Demo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo)
        init()
    }

    fun init() {
        animationView.addLottieOnCompositionLoadedListener {
            val list = animationView.resolveKeyPath(
                    KeyPath("**"))
            //这段代码就是为了打印出所有的keypath 让你判断哪些需要修改颜色
            for (path in list) {
                eLog("KEY", path.keysToString())
            }
            val keyPath2 = KeyPath("DAY")
            animationView.addValueCallback(keyPath2,
                    //修改对应keypath的填充色的属性值
                    LottieProperty.COLOR,
                    { return@addValueCallback  Color.RED})
        }
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener { animation ->
            animationView.onClick {
                if (animationView.tag == "0") {
                    animationView.tag = "1"
                    GlobalScope.launch {
                        repeat(50) {
                            runOnUiThread { animationView.progress = it / 100f }
                            delay(30)
                        }
                    }

                } else {
                    animationView.tag = "0"
                    GlobalScope.launch {
                        repeat(50) {
                            runOnUiThread { animationView.progress = it / 100f+0.5f }
                            delay(30)
                        }
                    }
                }

            }
        }
        animator.start()
    }
}

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
import android.view.View
import com.anubis.kt_extends.eString
import com.anubis.module_portMSG.ePortMSG
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Demo : AppCompatActivity() {
    private lateinit var mPortMSG:ePortMSG
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo)
//        init()
        /*串口通信*/
        mPortMSG = ePortMSG.eInit(this, "/dev/ttyS4",9600, object : ePortMSG.ICallBack {
            override fun IonLockerDataReceived(buffer: ByteArray, size: Int, path: String) {
                eLog("串口数据接收:${eString.eInit.eGetByteArrToHexStr(buffer)}--$path")
            }
        })
    }
    fun onClick(v:View){
        GlobalScope.launch {
            mPortMSG.eOpenSendMSG("BD0A20010000000000000001E9")
        }
    }

//    fun init() {
//        animationView.addLottieOnCompositionLoadedListener {
//            val list = animationView.resolveKeyPath(
//                    KeyPath("**"))
//            //这段代码就是为了打印出所有的keypath 让你判断哪些需要修改颜色
//            for (path in list) {
//                eLog("KEY", path.keysToString())
//            }
//            val keyPath2 = KeyPath("DAY")
//            animationView.addValueCallback(keyPath2,
//                    //修改对应keypath的填充色的属性值
//                    LottieProperty.COLOR,
//                    { return@addValueCallback  Color.RED})
//        }
//        val animator = ValueAnimator.ofFloat(0f, 1f)
//        animator.addUpdateListener { animation ->
//            animationView.onClick {
//                if (animationView.tag == "0") {
//                    animationView.tag = "1"
//                    GlobalScope.launch {
//                        repeat(50) {
//                            runOnUiThread { animationView.progress = it / 100f }
//                            delay(30)
//                        }
//                    }
//
//                } else {
//                    animationView.tag = "0"
//                    GlobalScope.launch {
//                        repeat(50) {
//                            runOnUiThread { animationView.progress = it / 100f+0.5f }
//                            delay(30)
//                        }
//                    }
//                }
//
//            }
//        }
//        animator.start()
//    }
}

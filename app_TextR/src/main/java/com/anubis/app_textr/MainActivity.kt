package com.anubis.app_textr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_qrcode.eQRCodeCreate
import com.anubis.module_qrcode.eQRCodeScan
import com.anubis.module_textrecognition.eTextRecognition
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.custom.async


class MainActivity : AppCompatActivity() {
    private lateinit var mQRCodeScan: eQRCodeScan
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eTextRecognition.eInit(this@MainActivity)
        mQRCodeScan = eQRCodeScan.eInit(application)
    }

    fun onClick(v: View) {
        var bitmap: Bitmap? = null
        when (v.id) {
            button0.id -> {
                bitmap = eBitmap.eInit.eDrawableToBitmap(resources.getDrawable(R.drawable.m0))
                val bit = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
                imageView.setImageBitmap(bit)
                async {
                    val text = eTextRecognition.eRecognition(bit)
                    textView.post { textView.text = text }
                }
            }
            button1.id -> imageView2.setImageBitmap(eQRCodeCreate.eInit.eCreateQRCode(editText.text.toString(), 200))

            bt1.id -> imageView2.setImageBitmap(eQRCodeCreate.eInit.eCreateQRCodeWithLogo2(editText.text.toString(), 100, BitmapFactory.decodeResource(resources, R.drawable.m0)))

            bt2.id -> imageView2.setImageBitmap(eQRCodeCreate.eInit.eCreateQRCodeWithLogo3(editText.text.toString(), 100, BitmapFactory.decodeResource(resources, R.drawable.m0)))

            bt3.id -> imageView2.setImageBitmap(eQRCodeCreate.eInit.eCreateQRCodeWithLogo4(editText.text.toString(), 100, BitmapFactory.decodeResource(resources, R.drawable.m0)))

            bt4.id -> imageView2.setImageBitmap(eQRCodeCreate.eInit.eCreateQRCode(editText.text.toString(), 100, -0xFF00CE, BitmapFactory.decodeResource(resources, R.drawable.m0)))

            bt5.id -> imageView2.setImageBitmap(eQRCodeCreate.eInit.eCreateQRCodeWithLogo6(editText.text.toString(), 100, BitmapFactory.decodeResource(resources, R.drawable.logo)))
            bt6.id -> mQRCodeScan.eScanActivity(this)
            bt7.id -> mQRCodeScan.eSlsectImgAnalyze(this)
            bt8.id -> mQRCodeScan.eScanActivity(this,MyQrcodeScan::class.java)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        eLog("requestCode:$requestCode")
       val sss= mQRCodeScan.eResult(requestCode, resultCode, data)
        eShowTip("扫码完成：$sss")
    }

}

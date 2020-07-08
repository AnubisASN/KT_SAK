package com.anubis.app_piceker

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eTime
import com.anubis.module_picker.ePicker
import com.anubis.module_picker.eTimePicker
import com.guoxiaoxing.phoenix.core.model.MediaEntity
import kotlinx.android.synthetic.main.picker.*
import org.jetbrains.anko.imageBitmap

@Route(path = "/app/piceker")
class PicekerActivity : AppCompatActivity() {
    var timeSelector: eTimePicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.picker)
        timeSelector = eTimePicker.eInit(this, object : eTimePicker.ResultHandler {
            override fun handle(time: String) {
                Toast.makeText(applicationContext, time, Toast.LENGTH_LONG).show()
            }
        })

    }

    fun onClick(v: View) {
        when (v.id) {
            picker_btPX.id ->ePicker.eInit.eImageStart(this@PicekerActivity)

            picker_btFile.id ->ePicker.eInit.eFileStart(this@PicekerActivity)

            picker_btTime.id ->timeSelector?.eShowTimeSelect("2000-01-01 00:00")

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val datas = ePicker.eInit.eResult(this@PicekerActivity, requestCode, resultCode, data)
        if (datas != null) {
            when (requestCode) {
                ePicker.eInit.IMAGE_REQUEST_CODE -> for (data in datas) {
                    Hint("data :${(data as MediaEntity).localPath}")
                    val bitmap = BitmapFactory.decodeFile(data.localPath)
                    imageView.imageBitmap = bitmap
                }
                ePicker.eInit.FILE_REQUEST_CODE-> for (batch in datas)
                    Hint("batch :$batch}")
            }
        }

    }

    private fun Hint(str: String) {
        val Str = "${eTime.eInit.eGetCurrentTime("MM-dd HH:mm:ss")}： $str\n\n\n"
        eLog(Str, "SAK")
        tv_Hint.append(Str)
        sv_Hint.fullScroll(ScrollView.FOCUS_DOWN)
    }
}

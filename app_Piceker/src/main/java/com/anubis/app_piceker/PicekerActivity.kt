package com.anubis.app_piceker

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eTime
import com.anubis.module_picker.ePiceker
import com.guoxiaoxing.phoenix.core.model.MediaEntity
import kotlinx.android.synthetic.main.picker.*
@Route(path = "/app/piceker")
class PicekerActivity : AppCompatActivity() {
val REQUEST_CODE=0x000111
    var type=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.picker)
    }

    fun onClick(v: View) {
        when (v.id) {
            picker_btPX.id ->{
                type=0
                ePiceker.eImageStart(this@PicekerActivity,1000)
            }


            picker_btFile.id ->{
                type=1
                ePiceker.eFileStart(this@PicekerActivity,2000)
            }


        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       val datas= ePiceker.eResult(this@PicekerActivity,requestCode,resultCode,data)
        if (datas!=null){
            if (type==0 ) {
                for (data in datas)
                    Hint("data :${(data as MediaEntity).localPath}")
            }else{
                for (batch in datas)
                    Hint("batch :$batch}")
            }
        }

    }

    private fun Hint(str: String) {
        val Str = "${eTime.eGetCurrentTime("MM-dd HH:mm:ss")}： $str\n\n\n"
        eLog(Str, "SAK")
        tv_Hint.append(Str)
        sv_Hint.fullScroll(ScrollView.FOCUS_DOWN)
    }
}

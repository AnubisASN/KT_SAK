package com.anubis.app_float_ball

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.ePermissions
import com.anubis.kt_extends.eShowTip
import com.anubis.module_dialog.eForegroundService
import com.anubis.module_dialog.eForegroundService.Companion.initStart
import kotlinx.android.synthetic.main.layout_float_window.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.onClick
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_float_window)
        eForegroundService.initParam(R.drawable.logo,"斗图提示","正在后台运行","",true)
        init()
    }

    private fun init(){
        if (! ePermissions.eInit.eOverlayPermissions(this))
            return eShowTip("请开启悬浮窗权限")
        if (!ePermissions.eInit.eSetPermissions(
                        this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                ))
            return eShowTip("请开启权限,否则无法使用")
        val intent=Intent(this, FloatWindowService::class.java)
        initStart(this,intent,FloatWindowService::class.java,null,true,R.layout.layout_notification){
            when (it){
                R.id.notif_ivClose.toString()->stopService(intent)
            }
        }
        finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        requestCode.eLog("resultCode")
        if (requestCode==1)
            init()
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        ePermissions.eInit.eSetOnRequestPermissionsResult(
                this,
                requestCode,
                permissions,
                grantResults,
                okBlock = {
                    val intent=Intent(this, FloatWindowService::class.java)
                    initStart(this,intent,FloatWindowService::class.java,null,true,R.layout.layout_notification){
                        when (it){
                            R.id.notif_ivClose.toString()->stopService(intent)
                        }
                    }
                    finish()
                }
        )
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


}

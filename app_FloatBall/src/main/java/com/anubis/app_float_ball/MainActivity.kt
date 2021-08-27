package com.anubis.app_float_ball

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eApp.Companion.eIApp
import com.anubis.kt_extends.ePermissions.Companion.eIPermissions
import com.anubis.kt_extends.eShell.Companion.eIShell
import com.anubis.kt_extends.eShell.Companion.restart
import com.anubis.kt_extends.eTime.Companion.eITime
import com.anubis.module_dialog.eForegroundService
import com.anubis.module_dialog.eForegroundService.Companion.initStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_float_window)
        eForegroundService.initParam(R.drawable.logo,"136守护","正在后台运行","",true)
        init()
    }

    private fun init(){
        if (! eIPermissions.eOverlayPermissions(this))
            return eShowTip("请开启悬浮窗权限")
        if (!eIPermissions.eSetPermissions(
                        this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                ))
            return eShowTip("请开启权限,否则无法使用")
        val intent=Intent(this, FloatWindowService::class.java)
        initStart(this,intent,FloatWindowService::class.java,GlobalScope.launch {
            while (isActive){
                delay(10000)
              if (  !eIApp.eIsServiceRunning(applicationContext,"com.anubis.sxk_facedetection.MyService")){
               eIShell.eExecShell(restart+" com.sxk_huibo_community/readsense.face.view.MainActivity")
              }
                if (eITime.eGetTime("HHmmss").toInt().eLog("eGetTime")in 33000..33009){
                    val romSize =getAvailSpace(Environment.getDataDirectory().absolutePath);//手机内部存储大小
                if (romSize <1041102976){
                    eIShell.eExecShell("rm -rf /sdcard/img/output")
                }
                }
            }
        },true,R.layout.layout_notification){
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
        eIPermissions.eSetOnRequestPermissionsResult(
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

    fun  getAvailSpace(path: String?):Long {
        val statfs = StatFs(path)
      val    size = statfs.blockSizeLong //获取分区的大小
       val count = statfs.availableBlocksLong //获取可用分区块的个数
        return size * count
    }
}

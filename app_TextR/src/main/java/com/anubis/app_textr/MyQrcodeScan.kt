package com.anubis.app_textr

import android.os.Bundle
import com.anubis.kt_extends.eLog
import com.anubis.uuzuche.lib_zxing.activity.eCodeUtils
import com.anubis.uuzuche.lib_zxing.activity.eDefaultCaptureActivity
import kotlinx.android.synthetic.main.activity_my_qrcode_scan.*

class MyQrcodeScan : eDefaultCaptureActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
         eLayoutId = R.layout.activity_my_qrcode_scan
        super.onCreate(savedInstanceState)
    }

    override fun eInit() {
        eLayoutFragmentID=R.layout.my_camera
        eFragmentId = R.id.fl_my_container
        super.eInit()
    }
 private var isOpen=false
    override fun eExtend() {
        eLog("eInitExtends")
        linear1.setOnClickListener {
            if (!isOpen) {
                eCodeUtils.isLightEnable(true)
                isOpen = true
            } else {
                eCodeUtils.isLightEnable(false)
                isOpen = false
            }
        }
    }
}

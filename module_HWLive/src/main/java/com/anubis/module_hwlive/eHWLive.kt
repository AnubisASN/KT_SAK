package com.anubis.module_hwlive

import android.app.Activity
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture

open class eHWLive internal constructor() {
    companion object {
        private lateinit var mICallBack:  MLLivenessCapture.Callback
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eHWLive() }
        fun eInit(callback:  MLLivenessCapture.Callback): eHWLive {
            mICallBack = callback
            return eInit
        }
    }

    fun eStart(activity: Activity) {
        val capture = MLLivenessCapture.getInstance()
        capture.startDetect(activity, mICallBack)
    }
}

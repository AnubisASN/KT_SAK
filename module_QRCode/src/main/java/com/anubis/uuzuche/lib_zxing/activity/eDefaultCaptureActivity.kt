package com.anubis.uuzuche.lib_zxing.activity

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.anubis.module_qrcode.R
import com.anubis.module_qrcode.eQRCodeScan
import com.anubis.module_qrcode.eQRCodeScan.AnalyzeCallback

/**
 * Initial the camera
 *
 *
 * 默认的二维码扫描Activity
 */
open class eDefaultCaptureActivity : AppCompatActivity() {
    protected var eLayoutId: Int?=null
    protected  var eLayoutFragmentID: Int?=null
    protected var eFragmentId: Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(eLayoutId?: R.layout.camera)
        eInit()
    }

    protected open fun eInit() {
        val captureFragment = CaptureFragment()
        eLayoutFragmentID?.let {
            eCodeUtils.setFragmentArgs(captureFragment,it)
        }
        captureFragment.setAnalyzeCallback(analyzeCallback, intent.getIntExtra(eQRCodeScan.CAMERA_ID_CODE, Camera.CameraInfo.CAMERA_FACING_BACK))
        supportFragmentManager.beginTransaction().replace(eFragmentId?:R.id.fl_zxing_container, captureFragment).commit()
        captureFragment.setCameraInitCallBack { e ->
            if (e == null) {
            } else {
                Log.e("TAG", "callBack: ", e)
            }
        }
        eExtend()
    }

  protected  open fun eExtend(){

    }
    /**
     * 二维码解析回调函数
     */
    open var analyzeCallback: AnalyzeCallback = object : AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(eCodeUtils.RESULT_TYPE, eCodeUtils.RESULT_SUCCESS)
            bundle.putString(eCodeUtils.RESULT_STRING, result)
            resultIntent.putExtras(bundle)
            this@eDefaultCaptureActivity.setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        override fun onAnalyzeFailed() {
            val resultIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(eCodeUtils.RESULT_TYPE, eCodeUtils.RESULT_FAILED)
            bundle.putString(eCodeUtils.RESULT_STRING, "")
            resultIntent.putExtras(bundle)
            this@eDefaultCaptureActivity.setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}

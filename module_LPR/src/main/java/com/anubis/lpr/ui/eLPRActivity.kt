package com.anubis.lpr.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import com.anubis.lpr.R
import com.anubis.lpr.scanner.ScannerOptions
import com.anubis.lpr.scanner.ScannerView
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

open class eLPRActivity : AppCompatActivity() {
    internal var camreaId = CameraSelector.DEFAULT_BACK_CAMERA
    private var scannerView: ScannerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lpr)
        try {
            camreaId=  if (intent.getIntExtra(INTENT_CODE,1)==1) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
        } catch (e: Exception) {
        }
        scannerView = findViewById(R.id.scanner_view)
        startCamera()
    }

    public override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback)
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val builder = ScannerOptions.Builder()
                .setTipText("请将识别车牌放入框内")
                .setFrameCornerColor(-0xd93101)
                .setLaserLineColor(-0xd93101)
                .build()
        scannerView!!.setScannerOptions(builder, camreaId)
        scannerView!!.setOnScannerOCRListener { cardNum: String? ->
            Log.d("OCRListener", cardNum)
            Log.d("OCRListener", Thread.currentThread().name)
            AlertDialog.Builder(this@eLPRActivity)
                    .setMessage(cardNum)
                    .setNegativeButton("重新识别") { dialogInterface: DialogInterface?, i: Int -> scannerView!!.start() }
                    .setPositiveButton("确定") { dialogInterface: DialogInterface?, i: Int -> finishValue(cardNum) }
                    .show()
        }
    }

    private fun finishValue(card: String?) {
        val intent = Intent()
        intent.putExtra("card", card)
        setResult(Activity.RESULT_OK, intent)
    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        @SuppressLint("StaticFieldLeak")
        override fun onManagerConnected(status: Int) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                System.loadLibrary("lpr")
            } else {
                super.onManagerConnected(status)
            }
        }
    }

    companion object {
        const val REQUEST_LPR_CODE = 1001
        val INTENT_CODE="cameraId"
    }
}

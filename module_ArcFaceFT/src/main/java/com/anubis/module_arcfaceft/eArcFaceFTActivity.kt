package com.anubis.module_arcfaceft

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eGetPhoneBitmap
import com.arcsoft.facetracking.AFT_FSDKEngine
import com.arcsoft.facetracking.AFT_FSDKFace
import com.arcsoft.facetracking.AFT_FSDKVersion

import com.guo.android_extend.tools.CameraHelper
import com.guo.android_extend.widget.CameraFrameData
import com.guo.android_extend.widget.CameraGLSurfaceView
import com.guo.android_extend.widget.CameraSurfaceView
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener

import kotlin.collections.ArrayList

/**
 * Created by gqj3375 on 2017/4/28.
 */

object eArcFaceFTActivity : OnCameraListener, Camera.AutoFocusCallback {
    private val TAG = "TAG"
    private val appid = "EDqqPgtie4x6yQvqH2gfCRkcyq4H3RPYFxa9btSu7kX1"
    private val ft_key = "2QBBSXZns1ffdAbZGQa7rCEn3ex7bVCZR3Vru4ucqTaQ"
    private val fd_key = "2QBBSXZns1ffdAbZGQa7rCEuD4DHEcXqQ4GVLPYRN4yB"
    private val fr_key = "2QBBSXZns1ffdAbZGQa7rCF2NTURdFTYbQ4bG7XoqtvA"
    private val age_key = "2QBBSXZns1ffdAbZGQa7rCFeBTnGmTGxUdDdEAzQYFMw"
    private val gender_key = "2QBBSXZns1ffdAbZGQa7rCFmLs3StNPR7xEiRfohsVHQ"
    private var mWidth: Int = 640
    private var mHeight: Int = 480
    private var mSurfaceView: CameraSurfaceView? = null
    private var mGLSurfaceView: CameraGLSurfaceView? = null
    private var mCamera: Camera? = null
    private var version = AFT_FSDKVersion()
    private var engine = AFT_FSDKEngine()
    private var mFaceResult: ArrayList<AFT_FSDKFace> = ArrayList()
    private var mCameraID: Int = 1
    private var mCameraRotate: Int = 0
    private var mCameraMirror: Boolean = false
    private  var mImageNV21: ByteArray? = null
    var mFaceNum: Int = 0
    var mBitmap: Bitmap? = null
    private var mAFT_FSDKFace: AFT_FSDKFace? = null

    fun init(GLSurfaceView: CameraGLSurfaceView, SurfaceView: CameraSurfaceView, cameraId: Int = 1, onClickCameraSwitch: View? = null): eArcFaceFTActivity {
        mGLSurfaceView = GLSurfaceView
        mSurfaceView = SurfaceView
        mCameraID = if (cameraId == 0) Camera.CameraInfo.CAMERA_FACING_BACK else Camera.CameraInfo.CAMERA_FACING_FRONT
        mCameraRotate = if (cameraId == 0) 90 else 270
        mCameraMirror = cameraId != 0
        mSurfaceView!!.setOnCameraListener(this)
        mSurfaceView!!.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate)
        mSurfaceView!!.debug_print_fps(true, false)
        var err = engine.AFT_FSDK_InitialFaceEngine(appid, ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5)
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.code)
        err = engine.AFT_FSDK_GetVersion(version)
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.code)
        onClickCameraSwitch?.setOnClickListener {
            onClickCameraSwitch()
        }
        return this
    }


    override fun setupCamera(): Camera? {
        mCamera = Camera.open(mCameraID)
        try {
            val parameters = mCamera!!.parameters
            parameters.previewFormat = ImageFormat.NV21
            parameters.setPreviewSize(mWidth, mHeight)
            mCamera!!.parameters = parameters
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (mCamera != null) {
            mWidth = mCamera!!.parameters.previewSize.width
            mHeight = mCamera!!.parameters.previewSize.height
        }
        return mCamera
    }

    override fun setupChanged(format: Int, width: Int, height: Int) {

    }

    override fun startPreviewImmediately(): Boolean {
        return true
    }
    override fun onPreview(data: ByteArray, width: Int, height: Int, format: Int, timestamp: Long): Any {
        val err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, mFaceResult)
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.code)
        Log.d(TAG, "Face=" + mFaceResult.size)
        mFaceNum = mFaceResult.size
        if (mFaceNum != 0 && mImageNV21 != null) {
            val size = mCamera!!.parameters.previewSize
            mBitmap = eGetPhoneBitmap(mImageNV21!!,size.width, size.height, mCameraID)
        }
        if (mFaceNum==0){
            mImageNV21 = null
        }
        for (face in mFaceResult) {
            Log.d(TAG, "Face:" + face.toString())
        }

        if (mImageNV21 == null) {
            if (!mFaceResult.isEmpty()) {
                mAFT_FSDKFace = mFaceResult[0].clone()
                mImageNV21 = data.clone()
            }
        }
        //copy rects
        val rects = arrayOfNulls<Rect>(mFaceResult.size)
        for (i in mFaceResult.indices) {
            rects[i] = Rect(mFaceResult.get(i).getRect());
        }
        //clear mFaceResult.
        mFaceResult.clear()
        //return the rects for render.
        return rects
    }

    override fun onBeforeRender(data: CameraFrameData) {

    }

    override fun onAfterRender(data: CameraFrameData) {
        mGLSurfaceView!!.gleS2Render.draw_rect(data.params as Array<Rect>, Color.GREEN, 2)
    }

    override fun onAutoFocus(success: Boolean, camera: Camera) {
        if (success) {
            Log.d(TAG, "Camera Focus SUCCESS!")
        }
    }

    private fun onClickCameraSwitch() {
        if (mCameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT
            mCameraRotate = 270
            mCameraMirror = true
        } else {
            mCameraID = Camera.CameraInfo.CAMERA_FACING_BACK
            mCameraRotate = 90
            mCameraMirror = false
        }
        mSurfaceView!!.resetCamera()
        mGLSurfaceView!!.setRenderConfig(mCameraRotate, mCameraMirror)
        mGLSurfaceView!!.gleS2Render.setViewAngle(mCameraMirror, mCameraRotate)
    }

}

package com.anubis.module_arcfaceft

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.Camera
import android.util.Log
import android.view.View
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLogE
import com.arcsoft.facetracking.AFT_FSDKEngine
import com.arcsoft.facetracking.AFT_FSDKFace
import com.arcsoft.facetracking.AFT_FSDKVersion
import com.guo.android_extend.widget.CameraFrameData
import com.guo.android_extend.widget.CameraGLSurfaceView
import com.guo.android_extend.widget.CameraSurfaceView
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener

/**
 * Author  ： AnubisASN   on 2018-07-23 9:12.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 * Router :  /'Module'/'Function'
 * 说明：人脸跟踪封装开发库
 * @初始化方法：init(){}
 * @param GLSurfaceView ：CameraGLSurfaceView；相机视图
 * @param urfaceView：CameraSurfaceView；表面视图
 * @param CameraState:Boolean;设置返回，true返回图片
 * @param  color: Int;矩阵颜色
 * @param stroke: Int;矩阵线宽度
 * @param isReturmFaceBitmap：Boolean=false; 返回人脸矩阵截图
 * @param shearNum:Int=50:人脸矩阵范围
 * @param cameraId: Int = 1;相机前后镜头
 * @param onClickCameraSwitch: View? = null；点击控件切换相机
 * @return: eArcFaceFT {
 *                       mFaceNum: Int;人脸数
 *                       mBitmap: Bitmap；人脸BitMap图片
 *                       mAFT_FSDKFace: AFT_FSDKFace；人脸信息集
 *                              }
 */
object eArcFaceFT : OnCameraListener, Camera.AutoFocusCallback {
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
    private var mImageNV21: ByteArray? = null
    var mFaceNum: Int = 0
    var mBitmap: Bitmap? = null
    var mAFT_FSDKFace: AFT_FSDKFace? = null
    var mIsState = true
    private var isShearFaceBitmap = false
    private var shearNum = 50
    private var color: Int = Color.GREEN
    private var stroke: Int = 2
    fun init(GLSurfaceView: CameraGLSurfaceView, SurfaceView: CameraSurfaceView, CameraState: Boolean = true, color: Int = Color.GREEN, stroke: Int = 2, isShearFaceBitmap: Boolean = false, shearNum: Int = 50, cameraId: Int = 1, onClickCameraSwitch: View? = null): eArcFaceFT {
        mGLSurfaceView = GLSurfaceView
        mSurfaceView = SurfaceView
        mIsState = CameraState
        this.color = color
        this.stroke = stroke
        this.isShearFaceBitmap = isShearFaceBitmap
        this.shearNum = shearNum
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

    fun setParameters ( color: Int = this.color, stroke: Int = this.stroke, isShearFaceBitmap: Boolean =  this.isShearFaceBitmap, shearNum: Int = this.shearNum, cameraId: Int = mCameraID) {
        this.color = color
        this.stroke = stroke
        this.isShearFaceBitmap = isShearFaceBitmap
        this.shearNum = shearNum
        if (cameraId!= mCameraID){
            mCameraID=cameraId
            onClickCameraSwitch()
        }

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
        if (mFaceResult.size == 0) {
            mImageNV21 = null
        }
        mFaceNum = mFaceResult.size
        if (mFaceNum != 0 && mImageNV21 != null && mIsState) {
            mIsState = false
            val size = mCamera!!.parameters.previewSize
            if (isShearFaceBitmap) {
                try {
                    val left = mAFT_FSDKFace!!.rect.left - shearNum
                    val top = mAFT_FSDKFace!!.rect.top - shearNum
                    val right = mAFT_FSDKFace!!.rect.right + shearNum
                    val bottom = mAFT_FSDKFace!!.rect.bottom + shearNum
                    mBitmap = eBitmap.eGetPhoneBitmap(mImageNV21!!, size.width, size.height, Rect(if (left < 0) 1 else left,
                            if (top < 0) 1 else top,
                            if (right > size.width) size.width - 1 else right,
                            if (bottom > size.height) size.height - 1 else bottom
                    ))
                } catch (e: Exception) {
                    mBitmap = eBitmap.eGetPhoneBitmap(mImageNV21!!, size.width, size.height)
                    eLogE("矩阵截取失败$e")
                }
            } else {
                mBitmap = eBitmap.eGetPhoneBitmap(mImageNV21!!, size.width, size.height)
            }

        }
        if (mFaceNum == 0 || !mIsState) {
            mIsState = true
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
        mGLSurfaceView!!.gleS2Render.draw_rect(data.params as Array<Rect>, color, stroke)
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

    fun restartCamera() {
        mCamera?.startPreview()
    }

    fun closeCamera() {
        if (null != mCamera) {
            mCamera!!.setPreviewCallback(null)
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

}

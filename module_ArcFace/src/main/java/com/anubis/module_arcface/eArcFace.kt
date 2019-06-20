package com.anubis.module_arcface

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Toast

import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.eBitmap
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.anubis.module_arcface.eArcFace0.SDK_KEY
import com.anubis.module_arcface.model.DrawInfo
import com.anubis.module_arcface.util.ConfigUtil
import com.anubis.module_arcface.util.DrawHelper
import com.anubis.module_arcface.util.camera.CameraHelper
import com.anubis.module_arcface.util.camera.CameraListener
import com.anubis.module_arcface.widget.FaceRectView
import com.arcsoft.face.AgeInfo
import com.arcsoft.face.ErrorInfo
import com.arcsoft.face.Face3DAngle
import com.arcsoft.face.FaceEngine
import com.arcsoft.face.FaceInfo
import com.arcsoft.face.GenderInfo
import com.arcsoft.face.LivenessInfo
import com.arcsoft.face.VersionInfo

import java.util.ArrayList

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.custom.async
/**
 * Author  ： AnubisASN   on 18-7-16 上午8:37.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * HomePage： www.anubisasn.me
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *类说明：活体检测+人脸跟踪
 *  @初始化方法：init()
 * @param mAcitvity: Activity；活动
 * @param  previewView: View；预览视图
 * @param faceRectView: FaceRectView；人脸矩形视图
 * @param rotation: Int;预览角度
 * @param cameraId: Int；相机ID
 * @param isMirror: Boolean;镜像设置
 * @return: eArcFace
 * @获取捕获 mBitmap：Bitmao；获取抓捕图像
 * @抓捕控制 mIsStatem：Boolean ；true返回,false不反回
 */
@SuppressLint("StaticFieldLeak")
object eArcFace : ViewTreeObserver.OnGlobalLayoutListener {
    private var cameraHelper: CameraHelper? = null
    private var drawHelper: DrawHelper? = null
    private var previewSize: Camera.Size? = null
    private var mCameraID = Camera.CameraInfo.CAMERA_FACING_FRONT
    private var faceEngine: FaceEngine? = null
    private var mActivity: Activity? = null
    private var afCode = -1
    private var mIsMirror = false
    private val APP_ID = "5Q5zxyFtDAa8fRG6je1YVY7jKv5Gsq7tNEfdYQWaZQG8"
    private val SDK_KEY = "5BQBRJ2PnWWKAS7629dwdeTtp91p6jDJmDc153xUK5sK"
    private val processMask = FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS
    private val ACTION_REQUEST_PERMISSIONS = 0x001
    private val NEEDED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE)
    private var mImageNV21: ByteArray? = null
    var mBitmap: Bitmap? = null
    //    var mAFT_FSDKFace: AFT_FSDKFace? = null
    var mIsState = true
    //    /**
//     * 相机预览显示的控件，可为SurfaceView或TextureView
//     */
    private var mPreviewView: View? = null
    private var mFaceRectView: FaceRectView? = null
    private var mRotation: Int = 0


    fun init(activity: Activity, previewView: View, faceRectView: FaceRectView, rotation: Int = 0, cameraId: Int? = null, isMirror: Boolean? = null): eArcFace {
        mCameraID = cameraId ?: mCameraID
        mIsMirror = isMirror ?: mIsMirror
        mActivity = activity
        mPreviewView = previewView
        mFaceRectView = faceRectView
        mRotation = rotation

        activeEngine()
        mPreviewView!!.viewTreeObserver.addOnGlobalLayoutListener(this)
        initEngine()
        initCamera()
        cameraHelper!!.start()
        return this
    }


    /**
     * 激活引擎
     *
     * @param view
     */
    fun activeEngine(view: View? = null) {
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(mActivity!!, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS)
            return
        }
        if (view != null) {
            view.isClickable = false
        }
        Observable.create(ObservableOnSubscribe<Int> { emitter ->
            val faceEngine = FaceEngine()
            val activeCode = faceEngine.active(mActivity, APP_ID, SDK_KEY)
            emitter.onNext(activeCode)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onNext(t: Int) {
                        if (t == ErrorInfo.MOK) {
                            mActivity!!.eShowTip(mActivity!!.getString(R.string.active_success))
                        } else if (t == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            mActivity!!.eShowTip(mActivity!!.getString(R.string.already_activated))
                        } else {
                            mActivity!!.eShowTip(mActivity!!.getString(R.string.active_failed))
                        }

                        if (view != null) {
                            view.isClickable = true
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }


                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })

    }


    private fun initEngine() {
        faceEngine = FaceEngine()
        afCode = faceEngine!!.init(mActivity, FaceEngine.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(mActivity),
                16, 20, FaceEngine.ASF_FACE_DETECT or FaceEngine.ASF_AGE or FaceEngine.ASF_FACE3DANGLE or FaceEngine.ASF_GENDER or FaceEngine.ASF_LIVENESS)
        val versionInfo = VersionInfo()
        faceEngine!!.getVersion(versionInfo)
        eLog("initEngine:  init: $afCode  version:$versionInfo")
        if (afCode != ErrorInfo.MOK) {
            mActivity!!.eShowTip(mActivity!!.resources.getString(R.string.init_failed, afCode))
        }
    }

    private fun unInitEngine() {

        if (afCode == 0) {
            afCode = faceEngine!!.unInit()
            eLog("unInitEngine: $afCode")
        }
    }


    fun onDestroy() {
        if (cameraHelper != null) {
            cameraHelper!!.release()
            cameraHelper = null
        }
        unInitEngine()
    }

    private fun checkPermissions(neededPermissions: Array<String>?): Boolean {
        if (neededPermissions == null || neededPermissions.size == 0) {
            return true
        }
        var allGranted = true
        for (neededPermission in neededPermissions) {
            allGranted = allGranted and (ContextCompat.checkSelfPermission(mActivity!!, neededPermission) == PackageManager.PERMISSION_GRANTED)
        }
        return allGranted
    }

    private fun initCamera() {
        val metrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(metrics)

        val cameraListener = object : CameraListener {
            override fun onCameraOpened(camera: Camera, cameraId: Int, displayOrientation: Int, isMirror: Boolean) {

                eLog("onCameraOpened: $cameraId  $displayOrientation $isMirror")
                previewSize = camera.parameters.previewSize
                drawHelper = DrawHelper(previewSize!!.width, previewSize!!.height, mPreviewView!!.width, mPreviewView!!.height, displayOrientation, cameraId, isMirror)
            }

            override fun onPreview(nv21: ByteArray, camera: Camera) {
                if (mFaceRectView != null) {
                    mFaceRectView!!.clearFaceInfo()
                }
                val faceInfoList = ArrayList<FaceInfo>()
                var code = faceEngine!!.detectFaces(nv21, previewSize!!.width, previewSize!!.height, FaceEngine.CP_PAF_NV21, faceInfoList)
                if (code == ErrorInfo.MOK && faceInfoList.size > 0) {
                    code = faceEngine!!.process(nv21, previewSize!!.width, previewSize!!.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask)
                    if (code != ErrorInfo.MOK) {
                        return
                    }
                } else {
                    return
                }
                val ageInfoList = ArrayList<AgeInfo>()
                val genderInfoList = ArrayList<GenderInfo>()
                val face3DAngleList = ArrayList<Face3DAngle>()
                val faceLivenessInfoList = ArrayList<LivenessInfo>()
                val ageCode = faceEngine!!.getAge(ageInfoList)
                val genderCode = faceEngine!!.getGender(genderInfoList)
                val face3DAngleCode = faceEngine!!.getFace3DAngle(face3DAngleList)
                val livenessCode = faceEngine!!.getLiveness(faceLivenessInfoList)
                //有其中一个的错误码不为0，return
                if (ageCode or genderCode or face3DAngleCode or livenessCode != ErrorInfo.MOK) {
                    return
                }
                eLog("livenessCode:$livenessCode")
                if (mIsState) {
                    mIsState=false
                    mBitmap = eBitmap.eGetPhoneBitmap(nv21, previewSize!!.width, previewSize!!.height)
                }


                if (mFaceRectView != null && drawHelper != null) {
                    val drawInfoList = ArrayList<DrawInfo>()
                    for (i in faceInfoList.indices) {
                        drawInfoList.add(DrawInfo(faceInfoList[i].rect, genderInfoList[i].gender, ageInfoList[i].age, faceLivenessInfoList[i].liveness, null))
                    }
                    drawHelper!!.draw(mFaceRectView, drawInfoList)
                }
            }

            override fun onCameraClosed() {
                eLog("onCameraClosed: ")
            }

            override fun onCameraError(e: Exception) {
                eLog("onCameraError: " + e.message)
            }

            override fun onCameraConfigurationChanged(cameraID: Int, displayOrientation: Int) {
                if (drawHelper != null) {
                    drawHelper!!.setCameraDisplayOrientation(displayOrientation)
                }
                eLog("onCameraConfigurationChanged: $cameraID  $displayOrientation")
            }
        }
        cameraHelper = CameraHelper.Builder()
                .previewViewSize(Point(mPreviewView!!.measuredWidth, mPreviewView!!.measuredHeight))
                .rotation(mRotation)
                .specificCameraId(mCameraID)
                .isMirror(mIsMirror)
                .previewOn(mPreviewView)
                .cameraListener(cameraListener)
                .build()
        cameraHelper!!.init()
    }


    /**
     * 在[.mPreviewView]第一次布局完成后，去除该监听，并且进行引擎和相机的初始化
     */
    override fun onGlobalLayout() {
        mPreviewView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(mActivity!!, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS)
        } else {
            initEngine()
            initCamera()
        }
    }


}

package com.anubis.module_face_recognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.collection.SimpleArrayMap
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eShowTip
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mobile.ReadFace.YMFace
import mobile.ReadFace.YMFaceTrack

/**
 * Author  ： AnubisASN   on 21-3-13 下午3:03.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 * Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 * Router :  /'Module'/'Function'
 * 说明：
 */
open class eFaceRe internal constructor() {
    protected var trackingMap: SimpleArrayMap<Int, YMFace>? = null
    private var AnalyseJob: Job? = null

    companion object {
        private lateinit var mContext: Context
        fun eInit(mContext: Context): eFaceRe {
            this.mContext = mContext
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eFaceRe() }
    }

    /**初始化追踪器
     *@param mDistance: Int = YMFaceTrack.DISTANCE_TYPE_FARTHESTER, 检测距离
     *@param recognitionConfidence: Int = 85，分析阀值
     *@return  YMFaceTrack?，追踪器
     * */
    protected var eFaceTrack: YMFaceTrack? = null
    open fun eStartTrack(mDistance: Int = YMFaceTrack.DISTANCE_TYPE_FARTHESTER, recognitionConfidence: Int = 85,orientation:Int=YMFaceTrack.FACE_0,resizeScale:Int=YMFaceTrack.RESIZE_WIDTH_640): YMFaceTrack? {
        eFaceTrack?.let { return it }
        eFaceTrack = YMFaceTrack()
        trackingMap = SimpleArrayMap()
        eFaceTrack ?: return null.apply { eLogE("检测器构建失败") }
        eFaceTrack!!.setDistanceType(mDistance)
        val result: Int = eFaceTrack!!.initTrack(mContext, orientation, resizeScale)
        return if ((result == 0)) {
            eFaceTrack!!.recognitionConfidence = recognitionConfidence
            eFaceTrack
        } else {
            mContext.eShowTip("授权失败：$result")
            null
        }

    }

    /**默认识别解析器
     * @param  i: Int, 连指针
     * @param ymFace：YMFace， 分析信息
     * */
    open val eDefaultBlock: (Int, YMFace) -> Unit = { i: Int, ymFace: YMFace ->
        /*活体检测*/
        ymFace.liveness = eGetLiveness()
        /*性别识别*/
        ymFace.personId = eFaceRecond()
        /*人脸识别*/
        ymFace.personId = eFaceRecond()
        /*性别*/
        ymFace.gender = eGetGender()
        /*眼镜*/
        ymFace.isHasGlass = eGetGlass()
    }
    protected var isInTrack = false

    /*×停止追踪器*/
    open fun eStopTrack(): Boolean {
        eFaceTrack ?: return false.apply { eLogE("already release track") }
        eFaceTrack!!.onRelease()
        eFaceTrack = null
        return true
    }

    /**单一分析 确保追踪唯一*/
    open fun eSoleAnaltse(bitmap: Bitmap): List<YMFace>? {
        if (isInTrack)
            return null
        isInTrack = true
        val faces: List<YMFace>? = eFaceTrack?.trackMulti(bitmap)
        isInTrack = false
        return faces
    }

    /**扩展分析 确保追踪唯一
     * @param bitmap: Bitmap,分析位图
     * @param mFaceQuality: Int = 5,图片质量阀值
     * @param  block: (Int, YMFace) -> Unit = eDefaultBlock,扩展分析器
     * @param  result: (YMFace) -> Unit，扩展分析结果
     * @return  List<YMFace>?,分析信息
     * */
    open fun eAnalyse(bitmap: Bitmap, mFaceQuality: Int = 5, block: (Int, YMFace) -> Unit = eDefaultBlock, result: (YMFace) -> Unit): List<YMFace>? {
        eFaceTrack ?: return null
        if (isInTrack)
            return null
        isInTrack = true
        val faces: List<YMFace> = eFaceTrack!!.trackMulti(bitmap)
        if (faces.isNotEmpty()) {
            if (AnalyseJob?.isActive != true) {
                if (trackingMap!!.size() > 20) trackingMap!!.clear()
                //找到最大人脸框
                var maxIndex = eGetMaxFace(faces)
                val ymFace = faces[maxIndex]
                AnalyseJob = GlobalScope.launch {
                    try {
                        /*质量检测*/
                        val faceQuality: Int = eGetQuality(maxIndex)
                        ymFace.faceQuality = faceQuality
                        if (faceQuality >= mFaceQuality) {
                            block(maxIndex, ymFace)
                            result(ymFace)
                        }
                    } catch (e: Exception) {
                        e.eLogE("eAnalyse")
                    } finally {
                        AnalyseJob = null
                    }
                }
            }
        }
        isInTrack = false
        return faces
    }

    /*YMFace的Rect转标准Rect */
    open fun eYMFaceToRect(floatArray: FloatArray) = Rect(floatArray[0].toInt(), floatArray[1].toInt(), floatArray[0].toInt() + floatArray[2].toInt(), floatArray[1].toInt() + floatArray[3].toInt())

    /*获取最大脸ID*/
    open fun eGetMaxFace(faces: List<YMFace>): Int {
        var maxIndex = 0
        for (i in 1 until faces.size) {
            if (faces[maxIndex].rect[2] <= faces[i].rect[2]) {
                maxIndex = i
            }
        }
        return maxIndex
    }

    /**人脸注册
     * @param bitmap: Bitmap,注册位图
     * @param  index: Int = 0,人脸指针
     * @param result: (Int, List<YMFace>?) -> Unit，注册结果  Int: 0-存在  -1-不存在人脸  >0 成功（人脸ID）
     * */
    open fun eFaceRegister(bitmap: Bitmap, index: Int = 0, result: (Int, List<YMFace>?) -> Unit) {
        val faces = eFaceTrack?.detectMultiBitmap(bitmap)
        if (eFaceTrack?.identifyPerson(index) ?: 1 > 0)
            return result(0, faces).eLog("已存在")
        return result(eFaceTrack?.addPerson(index) ?: -1, faces)
    }

    /*获取总数*/
    open fun eGetAlbumSize() = eFaceTrack?.albumSize ?: -1

    /**删除人脸
     * @param faceId: Int? = null, 人脸ID  ==null 全清
     * */
    open fun eFaceDelete(faceId: Int? = null) = faceId?.let { eFaceTrack?.deletePerson(it) }
            ?: eFaceTrack?.resetAlbum()

    /*获取所有已注册ID*/
    open fun eGetEnrolledPersonIds() = eFaceTrack?.enrolledPersonIds

    /**获取某指针位已注册ID
     * @param index: Int=0, 注册顺序指针
     * */
    open fun eGetEnrolledPersonId(index: Int = 0) = eFaceTrack?.enrolledPersonIds?.get(index)

    /**人脸识别 与分析器关联 eAnalyse*/
    open fun eFaceRecond(index: Int = 0) = eFaceTrack?.identifyPerson(index) ?: -1

    /*根据分析获取特征值*/
    open fun eGetFeature(index: Int = 0): FloatArray? = eFaceTrack?.getFaceFeature(index)

    /*根据Bitmap获取特征值*/
    open fun eGetBitMapFeature(bitmap: Bitmap): FloatArray? = eFaceTrack?.getFaceFeatureFromBitmap(bitmap)

    /*特征值对比*/
    open fun eCompareFeature(feature0: FloatArray, feature1: FloatArray) = eFaceTrack?.compareFaceFeature(feature0, feature1)
            ?: -1

    /*活体检测 0-非活体  1-活体*/
    open fun eGetLiveness(index: Int = 0) = eFaceTrack?.livenessDetect(index)?.get(0) ?: -1

    /*质量检测 0-10 */
    open fun eGetQuality(index: Int = 0) = eFaceTrack?.getFaceQuality(index) ?: -1

    /*性别检测 1-男 0-女性 */
    open fun eGetGender(index: Int = 0) = eFaceTrack?.getGender(index) ?: -1

    /*眼镜检测 0-不戴 1-戴*/
    open fun eGetGlass(index: Int = 0) = eFaceTrack?.getGlassValue(index) == 1

    /*红外活体检测 0-非活体 1-活体*/
    open fun eGetInfraredLivenessDetect(yuv: ByteArray, width: Int, height: Int, rect: FloatArray) = eFaceTrack?.livenessDetectFrame(yuv, width, height, rect) == 1
}


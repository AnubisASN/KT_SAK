package com.anubis.module_detection.face_mnn

import com.anubis.kt_extends.eLog

object FaceSDKNative {
    init {
        eLog("loadLibrary")
        System.loadLibrary("facedetect")
    }
    //SDK初始化
    external fun FaceDetectionModelInit(faceDetectionModelPath: String): Boolean

    //SDK人脸检测接口
    external fun FaceDetect(imageDate: ByteArray, imageWidth: Int, imageHeight: Int, imageChannel: Int): IntArray

    //SDK销毁
    external fun FaceDetectionModelUnInit(): Boolean



}

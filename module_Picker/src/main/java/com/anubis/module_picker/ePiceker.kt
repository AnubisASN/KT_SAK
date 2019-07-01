package com.anubis.module_picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eShowTip
import com.guoxiaoxing.phoenix.core.PhoenixOption
import com.guoxiaoxing.phoenix.core.model.MimeType
import com.guoxiaoxing.phoenix.picker.Phoenix
import me.rosuh.filepicker.config.FilePickerConfig
import me.rosuh.filepicker.config.FilePickerManager
import me.rosuh.filepicker.config.FilePickerManager.REQUEST_CODE

/**
 * Author  ： AnubisASN   on 19-6-29 下午4:13.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  Q Q： 773506352
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 *Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 *Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 *Router :  /'Module'/'Function'
 *说明：
 */
object ePicker{
    private var IMAGE_REQUEST_CODE = 0x000111
    private var FILE_REQUEST_CODE = FilePickerManager.REQUEST_CODE
    fun eImageStart(activity: Activity,  REQUEST_CODE: Int = 0x000111,Type: Int = PhoenixOption.TYPE_PICK_MEDIA, phoenix: PhoenixOption = Phoenix.with()) {
        phoenix.theme(PhoenixOption.THEME_BLUE)// 主题
                .fileType(MimeType.ofImage())//显示的文件类型图片、视频、图片和视频
                .maxPickNumber(3)// 最大选择数量
                .minPickNumber(0)// 最小选择数量
                .spanCount(4)// 每行显示个数
                .enablePreview(false)// 是否开启预览
                .enableCamera(true)// 是否开启拍照
                .enableAnimation(true)// 选择界面图片点击效果
                .enableCompress(false)// 是否开启压缩
                .compressPictureFilterSize(1024)//多少kb以下的图片不压缩
                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(false)// 是否开启点击声音
//                        .pickedMediaList(pic)// 已选图片数据
                .videoFilterTime(0)//显示多少秒以内的视频
                .mediaFilterSize(10000)//显示多少kb以下的图片/视频，默认为0，表示不限制
                .start(activity, Type, REQUEST_CODE)
        IMAGE_REQUEST_CODE = REQUEST_CODE
    }


    fun eFileStart(activity: Activity, REQUEST_CODE: Int = FilePickerManager.REQUEST_CODE, filePicker: FilePickerConfig = FilePickerManager.from(activity)) {
        filePicker.forResult(REQUEST_CODE)
        FILE_REQUEST_CODE = REQUEST_CODE
    }

    fun eResult(activity: Activity,requestCode: Int, resultCode: Int, data: Intent?) = if (resultCode === Activity.RESULT_OK) {
        when (requestCode) {
            IMAGE_REQUEST_CODE -> {
                //返回的数据
                Phoenix.result(data)
            }
            FILE_REQUEST_CODE -> {
                FilePickerManager.obtainData()
            }
            else ->null
        }
    } else {
        activity.eShowTip("你没有选择任何~")
        null
    }

}

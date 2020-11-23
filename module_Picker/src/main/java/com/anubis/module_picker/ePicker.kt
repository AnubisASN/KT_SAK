package com.anubis.module_picker

import android.app.Activity
import android.content.Intent
import com.anubis.kt_extends.eShowTip
import px_core.model.MimeType
import file_picker.bean.FileItemBeanImpl
import file_picker.config.*
import file_picker.filetype.*
import px_core.PhoenixOption
import px_picker.Phoenix

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
 */
open class ePicker internal constructor() {
    var IMAGE_REQUEST_CODE = 101
    var FILE_REQUEST_CODE = 102

    companion object {
        val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ePicker() }
    }

    /**
     *说明： 图片选择器框架
     * @调用方法：eImageStart()
     * @param activity: Application；应用程序
     * @param REQUEST_CODE: Int=101；成功回调代码
     * @param Type: Int = PhoenixOption.TYPE_PICK_MEDIA；显示类型（TYPE_PICK_MEDIA or TYPE_TAKE_PICTURE or TYPE_BROWSER_PICTURE）
     * @param phoenix: PhoenixOption = Phoenix.with();默认初始化与主动初始化
     * @return: void
     */
    open fun eImageStart(activity: Activity, REQUEST_CODE: Int = IMAGE_REQUEST_CODE, Type: Int = PhoenixOption.TYPE_PICK_MEDIA, phoenix: PhoenixOption = Phoenix.with(), editBlock: (PhoenixOption) -> Unit = {}) {
        phoenix.theme(PhoenixOption.THEME_BLUE)// 主题
                .fileType(MimeType.ofImage())//显示的文件类型图片、视频、图片和视频
                .maxPickNumber(1)// 最大选择数量
                .minPickNumber(0)// 最小选择数量
                .spanCount(5)// 每行显示个数
                .enablePreview(true)// 是否开启预览
                .enableShowPreview(true)// 是否开启预览按钮显示
                .previewEggs(true)
                .enableCamera(true)// 是否开启拍照
                .enableAnimation(true)// 选择界面图片点击效果
                .enableCompress(false)// 是否开启压缩
                .compressPictureFilterSize(1024)//多少kb以下的图片不压缩
                .compressVideoFilterSize(2018)//多少kb以下的视频不压缩
                .thumbnailHeight(160)// 选择界面图片高度
                .thumbnailWidth(160)// 选择界面图片宽度
                .enableClickSound(true)// 是否开启点击声音
                .videoFilterTime(0)//显示多少秒以内的视频
                .mediaFilterSize(10000)//显示多少kb以下的图片/视频，默认为0，表示不限制
        editBlock(phoenix)
        phoenix.start(activity, Type, REQUEST_CODE)
        IMAGE_REQUEST_CODE = REQUEST_CODE
    }


    /**
     *说明： 文件选择器
     * @调用方法：eFileStart()
     * @param activity: Activity；界面活动
     * @param REQUEST_CODE: Int=101；成功回调代码
     * @param filePicker: FilePickerConfig = FilePickerManager.from(activity)；默认初始化与主动初始化
     * @return: void
     */
    fun eFileStart(activity: Activity, allDefaultFileType: ArrayList<FileType>? = null, REQUEST_CODE: Int = FILE_REQUEST_CODE, filePicker: FilePickerConfig = FilePickerManager.from(activity), editBlock: (FilePickerConfig) -> Unit = {}) {
        allDefaultFileType?.let {
        filePicker.customDetector(object :AbstractFileDetector() {
            override fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl {
                for (type in allDefaultFileType) {
                    if (type.verify(itemBeanImpl.fileName)) {
                        itemBeanImpl.fileType = type
                        break
                    }
                }
                return itemBeanImpl
            }
        }) }
        filePicker.filter(object :AbstractFileFilter(){
            val tDataList= arrayListOf<FileItemBeanImpl>()
            override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                tDataList.clear()
                listData.forEach {
                    if (it.isDir || it.fileType!=null)
                        tDataList.add(it)
                }
                return tDataList
            }

        })
        filePicker.maxSelectable(1)
        editBlock(filePicker)
        filePicker.forResult(REQUEST_CODE)
        FILE_REQUEST_CODE = REQUEST_CODE
    }

    /**
     *说明：选择器结果回调
     * @调用方法：eResult()
     * @param activity: Activity；界面活动
     * @param requestCode: Int；请求回调代码
     * @param resultCode: Int；结果回调代码
     * @param data: Intent;回调数据
     * @return: List<Any>
     */
    fun eResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) = if (resultCode === Activity.RESULT_OK) {
        when (requestCode) {
            IMAGE_REQUEST_CODE -> Phoenix.result(data)
            FILE_REQUEST_CODE -> FilePickerManager.obtainData()
            else -> null
        }
    } else {
        activity.eShowTip("NULL~")
        null
    }

}

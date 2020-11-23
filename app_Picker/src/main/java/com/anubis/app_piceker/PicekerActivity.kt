package com.anubis.app_piceker

import android.app.Dialog
import android.content.Intent
import android.content.UriMatcher
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.anubis.kt_extends.*
import com.anubis.module_dialog.eDiaAlert
import com.anubis.module_extends.DataItemInfo
import com.anubis.module_extends.eRvAdapter
import com.anubis.module_picker.ePicker
import com.anubis.module_picker.eTimePicker
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.lcw.library.imagepicker.ImagePicker
import file_picker.filetype.FileType
import file_picker.filetype.RasterImageFileType
import kotlinx.android.synthetic.main.picker.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.textColor
import px_core.model.MediaEntity


@Route(path = "/app/piceker")
class PicekerActivity : AppCompatActivity() {
    var timeSelector: eTimePicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.picker)
        picekerAPP.mPicekerActivity = this
        timeSelector = eTimePicker.eInit(this, object : eTimePicker.ResultHandler {
            override fun handle(time: String) {
                Toast.makeText(applicationContext, time, Toast.LENGTH_LONG).show()
            }
        })
    }

    private var mFilepath = ""
    private var mEditFilepath = ""
    private var viewPair: Pair<TextView, Int>? = null
    private var adapterList = arrayListOf<eRvAdapter<DataItemInfo>>()
    fun onClick(v: View) {
        when (v.id) {
            picker_btPX.id -> ePicker.eInit.eImageStart(this@PicekerActivity)
            picker_btTest.id -> eSysTemCameraTake("/sdcard/IMG", "IMG_${eTime.eInit.eGetCuoTime()}.jpg") { intent: Intent, s: String ->
                mFilepath=s
            }
            button.id -> {
                val mDia = eDiaAlert.eInit(this)
                val list1 = arrayListOf(DataItemInfo(str1 = "1"), DataItemInfo(str1 = "2"), DataItemInfo(str1 = "3"))
                val list2 = arrayListOf(DataItemInfo(str1 = "11"), DataItemInfo(str1 = "22"), DataItemInfo(str1 = "33"))
                val list3 = arrayListOf(DataItemInfo(str1 = "111"), DataItemInfo(str1 = "222"), DataItemInfo(str1 = "333"))
                val dataList = arrayListOf(list1, list2, list3)
                mDia.eDiaGradeSelect(arrayOf("请选择小区1", "请选择小区2", "请选择小区3"), dataList, {
                    adapterList = it
                }, { view1: View, i1: Int, view2: View, dataItemInfo: DataItemInfo, i2: Int ->
                    view1.findViewById<TextView>(R.id.gradedia_tvTitle).text = dataItemInfo.str1
                    view1.findViewById<TextView>(R.id.gradedia_tvTitle).textColor = Color.parseColor("#25CEFB")
                    view1.findViewById<TextView>(R.id.gradedia_tvDynamic).backgroundColor = Color.parseColor("#25CEFB")
                    viewPair?.first?.textColor = viewPair?.second ?: 0
                    with(view2.findViewById<TextView>(R.id.table_item_tvStr1)) {
                        textColor = Color.parseColor("#25CEFB")
                        viewPair = Pair(this, Color.parseColor("#A2A2A2"))
                    }
                    adapterList.getOrNull(i1 + 1)?.eSetData(arrayListOf(DataItemInfo(str1 = "0"), DataItemInfo(str1 = "00"), DataItemInfo(str1 = "000")))
                }) {
                    eShowTip("return:${it.joinToString()}")
                }

            }
            picker_btFile.id -> {
                val array: ArrayList<FileType>? = arrayListOf()
                array?.add(RasterImageFileType())
                ePicker.eInit.eFileStart(this@PicekerActivity, array)
            }
            picker_btTime.id -> timeSelector?.eShowTimeSelect("2000-01-01 00:00")
            imageView.id -> eDiaAlert.eInit(this).eDefaultShow(ICallBackEdit = object : eDiaAlert.ICallBackEdit {
                override fun onEditInput(dia: Dialog, editText: EditText, iv: View?) {
                    eShowTip(editText.text)
                }

            }, btOK = "确定", btCancel = "取消")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        eLog("requestCode:$requestCode")
        when (requestCode) {
            ePicker.eInit.IMAGE_REQUEST_CODE -> {
                val datas = ePicker.eInit.eResult(this@PicekerActivity, requestCode, resultCode, data)
                datas?.forEach {
                    Hint("data :${(it as MediaEntity).localPath}")
                    val bitmap = BitmapFactory.decodeFile(it.localPath)
                    imageView.imageBitmap = bitmap
                }
            }
            ePicker.eInit.FILE_REQUEST_CODE -> {
                val datas = ePicker.eInit.eResult(this@PicekerActivity, requestCode, resultCode, data)
                datas?.forEach {
                    Hint("batch :$it")
                }
            }
            REQUEST_CODE_CAMERA_TAKE -> {
                Hint("data :$mFilepath")
                val bitmap = BitmapFactory.decodeFile(mFilepath)
                imageView.imageBitmap = bitmap
            }
        }

    }

    private fun Hint(str: String) {
        val Str = "${eTime.eInit.eGetTime("MM-dd HH:mm:ss")}： $str\n\n\n"
        eLog(Str, "SAK")
        tv_Hint.append(Str)
        sv_Hint.fullScroll(ScrollView.FOCUS_DOWN)
    }
}

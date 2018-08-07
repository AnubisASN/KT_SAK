package com.anubis.sxk_quickmark.Excel

/**
 * Author  ： AnubisASN   on 2018-08-07 11:07.
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
import android.content.Context
import android.os.Environment
import com.anubis.sxk_quickmark.DataBean.User


import java.io.File
import java.util.ArrayList

class ExportExcel(val mContext: Context) {
    private var recordList: ArrayList<ArrayList<String>>? = null
    private var mUserInfos: MutableList<User>? = null
    private var file: File? = null
    private var fileName: String? = null
    /**
     * 导出excel
     * @param view
     */
    fun exportExcel(userInfos:MutableList<User>) {
        this.mUserInfos=userInfos
        file = File("$sdPath/Record")
        makeDir(file!!)
        ExcelUtils.initExcel(file!!.toString() + "/二维码打卡表.xls", title)
        fileName = "$sdPath/Record/二维码打卡表.xls"
        ExcelUtils.writeObjListToExcel(recordData, fileName!!, mContext)
    }



    /**
     * 将数据集合 转化成ArrayList<ArrayList></ArrayList><String>>
     * @return
    </String> */
    private val recordData: ArrayList<ArrayList<String>>
        get() {
            recordList = ArrayList()
            for (i in mUserInfos!!.indices) {
                val student = mUserInfos!![i]
                val beanList = ArrayList<String>()
                beanList.add(student.time)
                beanList.add(student.name)
                beanList.add(student.qrCode)
                recordList!!.add(beanList)
            }
            return recordList!!
        }

    private val sdPath: String
        get() {
            var sdDir: File? = null
            val sdCardExist = Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory()
            }
            return sdDir!!.toString()
        }




    fun makeDir(dir: File) {
        if (!dir.parentFile.exists()) {
            makeDir(dir.parentFile)
        }
        dir.mkdir()
    }

    companion object {
        private val title = arrayOf("时间", "姓名", "二维码")
    }
}


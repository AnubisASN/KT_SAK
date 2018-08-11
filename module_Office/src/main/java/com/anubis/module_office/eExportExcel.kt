package com.anubis.module_office

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
import com.anubis.kt_extends.eLog


import java.io.File
import java.util.*


class eExportExcel(val mContext: Context, val mTitle: Array<String>, val mDatas: MutableList<Any>, val pathName: String = "Record", val fileName: String = "记录", val sheetName: String = "记录") {
    private var recordList: ArrayList<ArrayList<String>> = ArrayList()
    private var file: File? = null

    init {
        exportExcel()
    }

    /**
     * 导出excel
     * @param view
     */
    fun exportExcel() {
        file = File("$sdPath/$pathName")
        makeDir(file!!)
        eLog("文件路径：" + file)
        ExcelUtils.initExcel(file!!.toString() + "/$fileName.xls", mTitle, sheetName)
        eLog("初始化成功")
        ExcelUtils.writeObjListToExcel(recordData, "$sdPath/$pathName/$fileName.xls", mContext)
        eLog("导出成功")
    }


    /**
     * 将数据集合 转化成ArrayList<ArrayList></ArrayList><String>>
     * @return
    </String> */
    private val recordData: ArrayList<ArrayList<String>>
        get() {
            val clazz = Class.forName("com.anubis.module_office.dataTest")
            for (i in mDatas.indices) {
                val student = mDatas[i]
                val clazz = student::class.java
                val beanList = ArrayList<String>()
                for (j in mTitle.indices) {
                    val method  = clazz.getDeclaredMethod("getData", Array<String>::class.java)
                    eLog("data:"+method.invoke(student).toString())
                  val data=  method.invoke(student) as Array<String>
                    eLog("field:" + data[j])
                    beanList.add(data.toString())
                }
                recordList.add(beanList)
            }
            return recordList
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
        eLog("文件夹创建成功")
    }

}


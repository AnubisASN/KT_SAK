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
 * 类说明：Excel表封装开发库
 * @初始化方法：eOffice()
 * @param mAcitvity: Activity；活动
 * @param mTitle: Array<String>；标题名
 * @param mDatas: MutableList<Any>；数据
 * @param pathName:String = "Record";存储文件夹名
 * @param fileName: String = "记录"；文件名
 * @param sheetName: String = "记录";表内标题
 */


import android.content.Context
import android.os.Environment
import com.anubis.kt_extends.*
import com.anubis.module_office.Excel.ExcelUtils
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


open class eOffice internal constructor() {
    companion object {
        private lateinit var mContext: Context
        fun eInit(mContext: Context): eOffice {
            this.mContext = mContext
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eOffice() }
    }

    /**
     * eExportExcel 说明：导出单页excel---------------------------------------------------
     * @param mTitle: Array<String>; 标题组
     * @param  mData: List<Any>; 数据组
     * @param mSavePath: String = Environment.getExternalStorageDirectory().path； 保存路径
     * @param mFileName: String = eTime.eInit.eGetTime("yyyy-MM-dd HHmmss"); 保存文件名
     * @param mSheetName: String = "记录"; 单页标题
     * @return Boolean
     */
    open  fun eExportExcel(mTitle: Array<String>, mData: List<Any>, mSavePath: String = Environment.getExternalStorageDirectory().path, mFileName: String = eTime.eInit.eGetTime("yyyy-MM-dd HHmmss"), mSheetName: String = "记录"): Boolean {
        eExportExcel(arrayOf(mTitle), arrayOf(mData), mSavePath, mFileName, arrayOf(mSheetName))
        return true
    }

    /**
     * eExportExcel 说明：导出多页excel
     * @param mTitle: Array<Array<String>>; 多页标题组
     * @param  mData: Array<List<Any>>; 多页数据组
     * @param mSavePath: String = Environment.getExternalStorageDirectory().path； 保存路径
     * @param mFileName: String = eTime.eInit.eGetTime("yyyy-MM-dd HHmmss"); 保存文件名
     * @param mSheetName: Array<String> = "记录"; 多页标题
     * @return Boolean
     */
    open  fun eExportExcel(mTitles: Array<Array<String>>, mDatas: Array<List<Any>>, mSavePath: String = Environment.getExternalStorageDirectory().path, mFileName: String = eTime.eInit.eGetTime("yyyy-MM-dd HHmmss"), mSheetNames: Array<String>): Boolean {
        ExcelUtils.initExcel("$mSavePath/$mFileName.xls", mTitles, mSheetNames)
        try {
            mSheetNames.forEachIndexed { i, s ->
                val mTitle = try {
                    mTitles[i]
                } catch (e: Exception) {
                    arrayOf("")
                }
                val mData = try {
                    mDatas[i]
                } catch (e: Exception) {
                    listOf<Any>()
                }
                var result: Boolean
                try {
                    ExcelUtils.writeObjListToExcel(recordData(mTitle, mData), "$mSavePath/$mFileName.xls", i)
                    result = true
                } catch (e: Exception) {
                    e.eLogE("数据读取错误  \n @MethodName：getData ; @Return：Array<String>(@param..) ")
                    mContext.eShowTip("数据读取错误，请在实体类中添加getData方法，详情请看Log")
                    result = false
                }
                if (!result)
                    return false
            }
        } catch (e: Exception) {
            eLogE("eExportExcel", e)
            return false
        }
        mContext.eShowTip("Export Path:$mSavePath/$mFileName.xls")
        return true
    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList></ArrayList><String>>
     * @return
    </String> */
    private fun recordData(titles: Array<String>, datas: List<Any>): ArrayList<ArrayList<String>> {
        var recordList: ArrayList<ArrayList<String>> = ArrayList()
        for (i in datas.indices) {
            val datas = datas[i]
            val clazz = datas::class.java
            val beanList = ArrayList<String>()
            for (j in titles.indices) {
                // val method  = clazz.getDeclaredMethod("getData", Array<String>::class.java)
                val method = clazz.getDeclaredMethod("getData")
                val data = method.invoke(datas) as Array<String>
                beanList.add(data[j])
            }
            recordList.add(beanList)
        }
        return recordList
    }

}


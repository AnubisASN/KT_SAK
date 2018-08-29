package com.anubis.module_office.Excel

import android.content.Context
import com.anubis.kt_extends.eShowTip
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream



/**
 * Author  ： AnubisASN   on 2018-08-07 10:45.
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
internal object ExcelUtils {
    var arial14font: WritableFont? = null
    var arial14format: WritableCellFormat? = null
    var arial10font: WritableFont? = null
    var arial10format: WritableCellFormat? = null
    var arial12font: WritableFont? = null
    var arial12format: WritableCellFormat? = null

    val UTF8_ENCODING = "UTF-8"
    val GBK_ENCODING = "GBK"


    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    fun format() {
        try {
            arial14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
            arial14font!!.colour = jxl.format.Colour.LIGHT_BLUE
            arial14format = WritableCellFormat(arial14font)
            arial14format!!.alignment = jxl.format.Alignment.CENTRE
            arial14format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN)
            arial14format!!.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW)

            arial10font = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
            arial10format = WritableCellFormat(arial10font)
            arial10format!!.alignment = jxl.format.Alignment.CENTRE
            arial10format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN)
            arial10format!!.setBackground(Colour.GRAY_25)

            arial12font = WritableFont(WritableFont.ARIAL, 10)
            arial12format = WritableCellFormat(arial12font)
            arial10format!!.alignment = jxl.format.Alignment.CENTRE//对齐格式
            arial12format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN) //设置边框

        } catch (e: WriteException) {
            e.printStackTrace()
        }

    }

    /**
     * 初始化Excel
     * @param fileName
     * @param colName
     */
    fun initExcel(fileName: String, colName: Array<String>,sheetName:String) {
        format()
        var workbook: WritableWorkbook? = null
        try {
            val file = File(fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            workbook = Workbook.createWorkbook(file)
            val sheet = workbook!!.createSheet(sheetName, 0)
            //创建标题栏
            sheet.addCell(Label(0, 0, fileName, arial14format) as WritableCell)
            for (col in colName.indices) {
                sheet.addCell(Label(col, 0, colName[col], arial10format))
            }
            sheet.setRowView(0, 350) //设置行高

            workbook.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (workbook != null) {
                try {
                    workbook.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun <T> writeObjListToExcel(objList: List<T>?, fileName: String, c: Context) {
        val hint = fileName.split("/")
        if (objList != null && objList.isNotEmpty()) {
            var writebook: WritableWorkbook? = null
            var `in`: InputStream? = null
            try {
                val setEncode = WorkbookSettings()
                setEncode.encoding = UTF8_ENCODING
                `in` = FileInputStream(File(fileName))
                val workbook = Workbook.getWorkbook(`in`)
                writebook = Workbook.createWorkbook(File(fileName), workbook)
                val sheet = writebook!!.getSheet(0)

                //              sheet.mergeCells(0,1,0,objList.size()); //合并单元格
                //              sheet.mergeCells()

                for (j in objList.indices) {
                    val list = objList[j] as ArrayList<String>
                    for (i in list.indices) {
                        sheet.addCell(Label(i, j + 1, list[i], arial12format))
                        if (list[i].length <= 5) {
                            sheet.setColumnView(i, list[i].length + 8) //设置列宽
                        } else {
                            sheet.setColumnView(i, list[i].length + 5) //设置列宽
                        }
                    }
                    sheet.setRowView(j + 1, 350) //设置行高
                }

                writebook.write()
                c.eShowTip("导出到手机存储中文件夹${hint[hint.size - 1]}成功")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }

        }
    }
}


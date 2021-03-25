package com.anubis.module_xpath

import android.os.Environment
import com.anubis.kt_extends.eGetExternalStorageDirectory
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.lang.StringBuilder
import javax.xml.namespace.QName
import javax.xml.xpath.*

/**
 * Author  ： AnubisASN   on 20-6-30 下午2:19.
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
 *说明： eXpath 解析封装
 */
open class eXpath  internal constructor(){
     companion object {
        val eInit by lazy (LazyThreadSafetyMode.SYNCHRONIZED){ eXpath() }
        var xpath: XPath = XPathFactory.newInstance().newXPath()
        var filePath: String? = "$eGetExternalStorageDirectory/html.html"
        var httpUrl: String? = null
    }

    /**
     * eEvaluate 评估方法（执行）
     * @param expression：String;xpath 语法
     * @param url：String; 网络地址
     * @param iResult：IResult；结果回调
     * @param isCorrect:Boolean=false ;是否格式修正
     * @param returnType:QNmae=XPathConstants.NODESET; 结果类型
     */
    fun eEvaluate(expression: String, url: String, iResult: IResult, isCorrect: Boolean = false, returnType: QName = XPathConstants.NODESET) {
        httpUrl = url
        OkGo.post<String>(url)
                .execute(object : StringCallback() {
                    override fun onSuccess(response: Response<String>?) {
                        if (response != null) {
                            val file = File(filePath)
                            if (!file.exists())
                                file.createNewFile()
                            file.writeText(response.body())
                            eEvaluate(expression, file, iResult, isCorrect, returnType)
                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        iResult.error(exception = "网络地址无法访问")
                    }
                }
                )
    }

    /**
     * eEvaluate 评估方法
     * @param XX；XX
     * @param file：File; 文件
     * @param XX：XX
     */
    fun eEvaluate(expression: String, file: File, iResult: IResult, isCorrect: Boolean = false, returnType: QName = XPathConstants.NODESET) {

        filePath = file.path
        file.readText().eLog()
        eEvaluate(expression, InputSource(file.inputStream()), iResult, isCorrect, returnType)
    }

    /**
     * eEvaluate 评估方法
     * @param XX；XX
     * @param inputSource：InputSource; 输入流
     * @param XX：XX
     */
    fun eEvaluate(expression: String, inputSource: InputSource, iResult: IResult, isCorrect: Boolean = false, returnType: QName = XPathConstants.NODESET) {
        try {
            iResult.result(resultList = eParsing(expression, inputSource, iResult, isCorrect, returnType))
            httpUrl=null
        } catch (e: XPathExpressionException) {
            eLogE("eEvaluate:$e")
            if (isCorrect) {
                val file = eCorrect(File(filePath), e)
                if (file != null)
                    eEvaluate(expression, file, iResult, isCorrect, returnType)
                else
                    iResult.error(exception = "格式修正失败")
            }
        }
    }

    /**
     * eCorrect 修正方法
     * @param file：File; 待修正文件
     * @param e：XPathExpressionException；节点格式错误
     * @return File?; 输出文件
     */
    fun eCorrect(file: File, e: XPathExpressionException): File? {
        file.writeText(eCorrect(file.readText(), e) ?: return null)
        return file
    }

    /**
     * @oen eCorrect 修正方法（可重写）
     * @param strHT：String; 待修正字符内容
     * @param e：XPathExpressionException；节点格式错误
     * @param pointerIndex；Int；修正内容指针
     * @return String?; 输出字符内容
     */
    open fun eCorrect(strHT: String, e: XPathExpressionException, pointerIndex: Int = 0): String? {
        var errorCode = e.toString().split(":")[3]
        val errorIndex = errorCode.indexOf("/") + 1
        errorCode = errorCode.substring(errorIndex, errorCode.indexOf(" ", errorIndex))
        val metaIndex = strHT.indexOf("<$errorCode", pointerIndex)
        if (metaIndex == -1)
            return null

        val endIndex = strHT.indexOf(">", metaIndex)
        return if (strHT.substring(endIndex - 1, endIndex).eLog("!=/") != "/") {
            StringBuilder(strHT).replace(endIndex, endIndex + 1, "/>").toString()
        } else {
            eCorrect(strHT, e, endIndex + 3)
        }
    }

    /**
     * eParsing 解析方法（可重写）
     * @param expression：String;xpath 语法
     * @param inputSource：InputSource; 输入流
     * @param iResult：IResult；结果回调
     * @param isCorrect:Boolean=false ;是否格式修正
     * @param returnType:QNmae=XPathConstants.NODESET; 结果类型
     * @return ArrayList<String>?；解析内容
     */
    open fun eParsing(expression: String, inputSource: InputSource, iResult: IResult, isCorrect: Boolean = false, returnType: QName = XPathConstants.NODESET): ArrayList<String>? {
        val arrayList: ArrayList<String>? = ArrayList()
        val results = xpath.evaluate(expression, inputSource, returnType) as NodeList
        for (i in 0 until results.length) {
            arrayList?.add(results.item(i).textContent)
        }
        return arrayList
    }

    /**
     * IResult 结果回调
     */
    interface IResult {
        fun result(target: String = httpUrl ?: filePath ?: "", resultList: ArrayList<String>?)
        fun error(target: String = httpUrl ?: filePath ?: "", exception: String)
    }
}

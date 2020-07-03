package com.anubis.module_xpath

import com.anubis.kt_extends.eLog
import com.anubis.module_xpath.eXpath.Companion.xpath
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import javax.xml.namespace.QName

/**
 * Author  ： AnubisASN   on 20-7-2 下午2:00.
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

class Test private constructor() : eXpath() {
companion object{
    val eInit by lazy (LazyThreadSafetyMode.SYNCHRONIZED){ Test() }
}
    init {
        eInit.eEvaluate("//tr", "http://119.23.77.41:8081/face", object : IResulr {
            override fun error(target: String, exception: String) {
                eLog(target + "--" + exception)
            }

            override fun result(target: String, resultList: ArrayList<String>?) {
                eLog(target + "--")
                resultList?.forEach {
                    it.eLog("result")
                }
            }

        }, true)
        eEvaluate("//tr", "http://119.23.77.41:8081/face", object : IResulr {
            override fun error(target: String, exception: String) {
                eLog(target + "--" + exception)
            }

            override fun result(target: String, resultList: ArrayList<String>?) {
                eLog(target + "--")
                resultList?.forEach {
                    it.eLog("result")
                }
            }

        }, true)
    }

    override fun eParsing(expression: String, inputSource: InputSource, iResult: IResulr, isCorrect: Boolean, returnType: QName): ArrayList<String>? {
        val arrayList: ArrayList<String>? = ArrayList()
        val results = xpath.evaluate(expression, inputSource, returnType) as NodeList
        for (i in 0 until results.length) {
            arrayList?.add(results.item(i).textContent + "----------------00")
        }
        return arrayList
    }
}

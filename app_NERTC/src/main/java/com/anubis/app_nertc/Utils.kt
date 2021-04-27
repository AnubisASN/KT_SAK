package com.anubis.app_nertc

import com.anubis.kt_extends.eJson
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogE
import com.anubis.kt_extends.eString
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import java.security.MessageDigest
import java.util.*

/**
 * Author  ： AnubisASN   on 21-6-7 下午5:22.
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
object Utils {
    var  roomId:String="123"
    var userId=123L
   val appKey="2ac19ec9a7e3be4ecb85d4e41aba3049"
    val   appSecret = "8418fbb21521"
    var token =""
    fun getToken(uId:Long,expireAt:Int=86400,block:((String)->Unit)?=null){
        val nonce = "12345"
        val curTime: String =( Date().time / 1000L).toString()
        val checkSum: String = CheckSumBuilder.getCheckSum(appSecret, nonce, curTime) ///checkSum的计算方式请参考《服务端API-调用方式-请求结构》
        OkGo.post<String>("https://api.netease.im/nimserver/user/getToken.action")
                .headers("appKey",appKey)
                .headers("Nonce","12345")
                .headers("CurTime",curTime)
                .headers("CheckSum",checkSum)
                .headers("Content-Type","application/x-www-form-urlencoded;charset=utf-8")
                .params("uid",uId)
                .params("expireAt",expireAt)
                .execute(object :StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        if (eJson.eInit.eGetJson(response?.body().toString(),"code",400)==200){
                            token=eJson.eInit.eGetJson(response?.body().toString(),"token","")
                            block?.invoke(token)
                            userId=uId
                        }
                    }
                })
}

}

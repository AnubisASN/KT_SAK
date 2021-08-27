package com.anubis.app_skin

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eString.Companion.eIString
import com.anubis.module_extends.eRvAdapter
import com.anubis.module_tcp.eTCP
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val dataArray = arrayListOf<Int>()
    lateinit var mAdapter: eRvAdapter<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tcp()
    }

    val str= byteArrayOf(
        0x01.toByte(),
        0x53.toByte(),
        0x12.toByte(),
        0x11.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x40.toByte(),
        0x3F.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x40.toByte(),
        0x3F.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x0C.toByte(),
        0x42.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0xC8.toByte(),
        0x42.toByte(),
        0xc7.toByte(),
        0x8f.toByte()
    )

    fun tcp() {
        val mTCP = eTCP.eInit(Handler())
        GlobalScope.launch {
            mTCP?.eServerSocket(10001,condition = object : eTCP.ICallBack {
                override fun callCondition(data:ByteArray?,receiveData: String?,int: Int): String? {
                    data?:return null
                    return  eIString.eGetByteArrToHexStr(data).substring(0,int*2)
                }
            }) { add, code, msg, hashap ->
                eLog("add:$add--code:$code--msg:$msg")
                when(code){
                    mTCP.SHANDLER_RECEIV_SUCC_CODE-> mTCP?.eSocketSend("") {
                        it.write(str)
                    }.eLog("eSocketSend")
                    mTCP.SHANDLER_MSG_CODE->if (msg.length>6 && msg.slice(0..5) =="015312" ) {
                        println(  dataCheck(msg).toString())
                        mTCP?.eSocketSend("") {
                            it.write(str)
                        }.eLog("eSocketSend")
                    }
                }
            }
        }
    }
data class DataStaticElectricity(var s:Boolean=false,var v1:Float=0.0f,var v2:Float=0.0f,var v3:Float=0.0f,var v4:Float=0.0f)
 fun dataCheck(string: String):DataStaticElectricity{
     val data=DataStaticElectricity()
     with(string) {
         eLog("data")
         val head=slice(0..5)
         val p0=slice(6..9)
         val p1=slice(10..17)
         val p2=slice(18..25)
         val p3=slice(26..33)
         val p4=slice(34..41)
         val p5=slice(42..45)
         eLog("$head+$p0+$p1+$p2+$p3+$p4+$p5")
         with(p0.slice(0..1)){
             if (this=="a2" || this=="aa"){
                 data.s=true
             }
         }
         data.v1= eIString.eHexStringToFloat(p1.chunked(2).reversed().joinToString(""))
         data.v3= eIString.eHexStringToFloat(p3.chunked(2).reversed().joinToString(""))
         data.v2= eIString.eHexStringToFloat(p2.chunked(2).reversed().joinToString(""))
         data.v4= eIString.eHexStringToFloat(p4.chunked(2).reversed().joinToString(""))
     }
return  data
 }
}
/*记录*/
//GO->TCP助手（HE显示）：【2021-8-18 15:27:08】
//1 53 12 11 0 0 0 40 3f 0 0 40 3f 0 0 c 42 0 0 ffffffffffffffc8 42 ffffffffffffffc7 ffffffffffffff8f
//XX->GO:015312F300004003450000000000400345E3B5B54490A6
//b0329142  72.59

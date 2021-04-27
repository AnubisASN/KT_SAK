package readsense.face.util;

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.anubis.kt_extends.eBReceiver
import com.anubis.kt_extends.eLog
import kotlinx.coroutines.*


/**
 * Author  ： AnubisASN   on 21-7-6 下午2:22.
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
class A905Board {

    companion object {
        private lateinit var mContext: Context
        fun eInit(context: Context): A905Board {
            mContext = context
            return eInit
        }

        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            A905Board()
        }
    }

    /*继电器*/
    fun ctrlPelay(enable: Boolean = true) {
        val intent = Intent("com.xbh.action.RELAY_CTRL")
        intent.putExtra("port", 0)//0xff为所有,0 1为第0和第1个继电器,905D3.A内置为0
        intent.putExtra("enable", enable)//true打开,false关闭
        mContext.sendBroadcast(intent)
    }

    /*WIFI热点*/
    fun ctrlWifi(enable: Boolean = true) {
        val intent = Intent("com.xbh.action.ENABLE_HOTSPOT")
        intent.putExtra("enable", enable)
        mContext.sendBroadcast(intent)
    }

    /*导航栏*/
    fun ctrlHideNAV(enable: Boolean = true) {
        val intent = Intent("com.xbh.action.HIDE_NAV_BAR")
        intent.putExtra("hide", enable)
        mContext.sendBroadcast(intent)
    }

    /*LED补光灯*/
    fun ctrlLED(ir: Int = 1, type: String = "B") {
        val intent = Intent("com.xbh.action.LED_CTRL")
        intent.putExtra("port", when (type) {
            "R" -> 1
            "G" -> 2
            "B" -> 3
            else -> 3
        })//0xff为所有,0为led 12V供电控制(需要先打开供电),然后1 2 3分别是r g b, 4为恒流IR接口
        intent.putExtra("status", ir)//1打开,0关闭,port为恒流IR接口则值为0-100
        mContext.sendBroadcast(intent)
    }

    /*伟根输出*/
    fun outWG(data: String, mode: Int = 34) {
        val intent = Intent("com.xbh.action.WIEGAND_OUT")
        intent.putExtra("mode", mode)//支持26和34协议
        intent.putExtra("data", data)//要输出的卡号
        mContext.sendBroadcast(intent)
    }

    data class dataNET(var auto: Boolean = true, var ip: String?, var gateway: String?, var netMask: String?, var dns1: String? = "8.8.8.8", var dns2: String? = "8.8.4.4")

    /*以太网设置*/
    fun setEthernet(data: dataNET) {
//设置以太网为动态获取IP模式
        val intent = Intent("com.xbh.action.SET_ETHERNET_MODE")
        if (data.auto) {
            intent.putExtra("mode", "auto")
            mContext.sendBroadcast(intent)
        } else {
            intent.putExtra("mode", "static")
            intent.putExtra("ip", data.ip ?: "")
            intent.putExtra("gateway", data.gateway ?: "")
            intent.putExtra("netMask", data.netMask ?: "")
            intent.putExtra("dns1", data.dns1 ?: "")
            intent.putExtra("dns2", data.dns2 ?: "")
        }
        mContext.sendBroadcast(intent)
    }

    fun eSetRegisterReceiver( filter:IntentFilter = IntentFilter(),block: (context: Context, intent: Intent,BroadcastReceiver) -> Unit):BroadcastReceiver {
        filter.addAction("com.xbh.action.NET_INFO")
           var mReceiver: BroadcastReceiver?=null
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                block(context,intent,mReceiver!!)
            }
        }
        mContext.registerReceiver(mReceiver, filter)
        return  mReceiver
    }

    fun getEthernet(block:(dataNET)->Unit){
        eSetRegisterReceiver{ context: Context, intent: Intent,mReceiver: BroadcastReceiver ->
            val type = intent?.getStringExtra("type");//wifi,mobile,ethString
                type?.let {
                    val mode = intent.getStringExtra("mode");//static, dhcp
                    val ip = intent.getStringExtra("ip");
                    val gateway = intent.getStringExtra("gateway");
                    val netmask = intent.getStringExtra("netmask");
                    val dns1 = intent.getStringExtra("dns1");
                    val dns2 = intent.getStringExtra("dns2");
                    block(dataNET(mode != "static", ip, gateway, netmask, dns1, dns2))
                    mContext.unregisterReceiver(mReceiver)
                }
        }
        val intent = Intent("com.xbh.action.GET_NET_INFO")
        mContext.sendBroadcast(intent)
    }


    /*看门狗*/
    //启动APP看门狗,时间窗口为系统默认。启动狗后要定时喂狗,否则系统重启
    private val START_APPDOG = "com.xbh.action.START_APPDOG";

    //停止APP看门狗
    private val STOP_APPDOG = "com.xbh.action.STOP_APPDOG";

    //喂狗广播
    private val FEED_APPDOG = "com.xbh.action.FEED_APPDOG";
    fun xbhDog(enable: Boolean? = true) {
        val intent = Intent(enable?.let { if (it) START_APPDOG else STOP_APPDOG } ?: FEED_APPDOG)
        if (enable == true)
            intent.putExtra("time", 150)//true打开,false关闭
        mContext.sendBroadcast(intent)
    }

    var xbhJob: Job? = null
    fun dog(state: Boolean = true) {
//      eLog("看门狗模式","DOG")
        if (state) {
            xbhDog(true)
            xbhJob?.cancel()
            xbhJob = GlobalScope.launch {
                while (isActive) {
                    xbhDog(null)
                    delay(60)
                }
            }
        } else {
            xbhJob?.cancel()
            xbhDog(false)
        }
    }
}

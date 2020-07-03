package com.anubis.module_cardotg

import android.annotation.SuppressLint
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat


import com.huashi.otg.sdk.HSIDCardInfo
import com.huashi.otg.sdk.HandlerMsg
import com.huashi.otg.sdk.HsOtgApi
import com.huashi.otg.sdk.HsSerialPortSDK
import com.huashi.otg.sdk.Test

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.anubis.kt_extends.eShowTip
import com.anubis.module_cardotg.R.id.iv_photo
import com.anubis.module_cardotg.R.id.tv_info
import com.tencent.bugly.Bugly.applicationContext
/**
 * Author  ： AnubisASN   on 18-7-16 上午8:37.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * HomePage： www.anubisasn.me
 *命名规则定义：
 *Module :  module_'ModuleName'
 *Library :  lib_'LibraryName'
 *Package :  'PackageName'_'Module'
 *Class :  'Mark'_'Function'_'Tier'
 *Layout :  'Module'_'Function'
 *Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 *Router :  /'Module'/'Function'
 *类说明：身份证阅读器
 *  @初始化连接方法：otgInit()
 * @param app: Application; APP
 * @param appHandler: Handler；消息回调
 * @return: null
 */
@SuppressLint("StaticFieldLeak")
object test {
    private var m_Auto = false
    private var api: HsOtgApi? = null
    private var ComApi: HsSerialPortSDK? = null
    private var mAPP: Application? = null
    private var filepath = ""
    private var df = SimpleDateFormat("yyyy年MM月dd日")// 设置日期格式

    private var appHandler: Handler? = null

//    object : Handler() {
//        override fun handleMessage(msg: android.os.Message) {
//            if (msg.what == 99 || msg.what == 100) {
//                statu!!.text = msg.obj as String
//            }
//            //第一次授权时候的判断是利用handler判断，授权过后就不用这个判断了
//            if (msg.what == HandlerMsg.CONNECT_SUCCESS) {
//                statu!!.text = "连接成功"
//                sam!!.text = api!!.GetSAMID()
//            }
//            if (msg.what == HandlerMsg.CONNECT_ERROR) {
//                statu!!.text = "连接失败"
//            }
//            if (msg.what == HandlerMsg.READ_ERROR) {
//                //cz();
//                //statu.setText("卡认证失败");
//                statu!!.text = "请放卡..."
//            }
//            if (msg.what == HandlerMsg.READ_SUCCESS) {
//                statu!!.text = "读卡成功"
//                val info = msg.obj
//            }
//        }
//    }

    class CPUThread : Thread() {
        override fun run() {
            super.run()
            var ici: HSIDCardInfo
            var msg: Message
            while (m_Auto) {
                /////////////////循环读卡，不拿开身份证
                if (api!!.NotAuthenticate(200, 200) != 1) {
                    //////////////////循环读卡，需要重新拿开身份证
                    //if (api.Authenticate(200, 200) != 1) {
                    msg = Message.obtain()
                    msg.what = HandlerMsg.READ_ERROR
                    appHandler?.sendMessage(msg)
                } else {
                    ici = HSIDCardInfo()
                    if (api!!.ReadCard(ici, 200, 1300) == 1) {
                        msg = Message.obtain()
                        var fp = ByteArray(1024)
                        fp = ici.fpDate
                        val m_FristPFInfo = if (fp[4] == 0x01.toByte()) {
                            String.format("指纹  信息：第一枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[5].toInt()), fp[6])
                        } else {
                            "身份证无指纹 \n"
                        }
                        val m_SecondPFInfo = if (fp[512 + 4] == 0x01.toByte()) {
                            String.format("指纹  信息：第二枚指纹注册成功。指位：%s。指纹质量：%d \n", GetFPcode(fp[512 + 5].toInt()),
                                    fp[512 + 6])
                        } else {
                            "身份证无指纹 \n"
                        }
                        var info = ""
                        if (ici.getcertType() === "J") {
                            info = ("证件类型：港澳台居住证（J）\n"
                                    + "姓名：" + ici.peopleName + "\n" + "性别："
                                    + ici.sex + "\n"
                                    + "签发次数：" + ici.getissuesNum() + "\n"
                                    + "通行证号码：" + ici.passCheckID + "\n"
                                    + "出生日期：" + df.format(ici.birthDay)
                                    + "\n" + "地址：" + ici.addr + "\n" + "身份号码："
                                    + ici.idCard + "\n" + "签发机关："
                                    + ici.department + "\n" + "有效期限："
                                    + ici.strartDate + "-" + ici.endDate + "\n"
                                    + m_FristPFInfo + "\n" + m_SecondPFInfo)
                        } else {
                            if (ici.getcertType() === "I") {
                                info = ("证件类型：外国人永久居留证（I）\n"
                                        + "英文名称：" + ici.peopleName + "\n"
                                        + "中文名称：" + ici.getstrChineseName() + "\n"
                                        + "性别：" + ici.sex + "\n"
                                        + "永久居留证号：" + ici.idCard + "\n"
                                        + "国籍：" + ici.getstrNationCode() + "\n"
                                        + "出生日期：" + df.format(ici.birthDay)
                                        + "\n" + "证件版本号：" + ici.getstrCertVer() + "\n"
                                        + "申请受理机关：" + ici.department + "\n"
                                        + "有效期限：" + ici.strartDate + "-" + ici.endDate + "\n"
                                        + m_FristPFInfo + "\n" + m_SecondPFInfo)
                            }else{
                                info = ("证件类型：身份证\n" + "姓名："
                                        + ici.peopleName + "\n" + "性别：" + ici.sex
                                        + "\n" + "民族：" + ici.people + "\n" + "出生日期："
                                        + df.format(ici.birthDay) + "\n" + "地址："
                                        + ici.addr + "\n" + "身份号码：" + ici.idCard
                                        + "\n" + "签发机关：" + ici.department + "\n"
                                        + "有效期限：" + ici.strartDate + "-"
                                        + ici.endDate + "\n" + m_FristPFInfo + "\n"
                                        + m_SecondPFInfo)
                            }
                        }
                        Test.test("/mnt/sdcard/test.txt4", ici.toString())
                        try {
                            val ret = api!!.Unpack(filepath, ici.getwltdata())// 照片解码
                            Test.test("/mnt/sdcard/test3.txt", "解码中")
                            if (ret != 0) {// 读卡失败
                                return
                            }
                            val fis = FileInputStream("$filepath/zp.bmp")
                            val bmp = BitmapFactory.decodeStream(fis)
                            fis.close()
                        } catch (e: FileNotFoundException) {
                            mAPP!!.eShowTip("头像不存在!")
                        } catch (e: IOException) {
                            mAPP!!.eShowTip("头像读取错误!")
                        } catch (e: Exception) {
                            mAPP!!.eShowTip("头像解码失败!")
                        }
                        msg.what = HandlerMsg.READ_SUCCESS
                        msg.obj = info + "\n${"证件图片：" + "$filepath/zp.bmp"}"
                        appHandler?.sendMessage(msg)
                    }
                }
                SystemClock.sleep(300)
                msg = Message.obtain()
                msg.what = HandlerMsg.READ_ERROR
                appHandler?.sendMessage(msg)
                SystemClock.sleep(300)
            }

        }
    }

    fun otgInit(app: Application, appHandler: Handler) {
        this.appHandler = appHandler
        mAPP = app
        filepath = Environment.getExternalStorageDirectory().absolutePath + "/wltlib"// 授权目录
        //	filepath = "/mnt/sdcard/wltlib";// 授权目录
        val msg = Message()
        Log.e("LJFDJ", filepath)
        if (otgConn() == "连接成功") {
            msg.what = HandlerMsg.CONNECT_SUCCESS
            msg.obj = "${otgAutoread()}---${api!!.GetSAMID()}"
        } else {
            m_Auto = false
            msg.what = HandlerMsg.CONNECT_ERROR
        }
        appHandler.sendMessage(msg)
    }

    //连接
    fun otgConn(): String {
        copy(mAPP!!.applicationContext, "base.dat", "base.dat", filepath)
        copy(mAPP!!.applicationContext, "license.lic", "license.lic", filepath)
        api = HsOtgApi(appHandler, mAPP!!.applicationContext)
        val ret = api!!.init()// 因为第一次需要点击授权，所以第一次点击时候的返回是-1所以我利用了广播接受到授权后用handler发送消息
        if (ret == 1) {
            return "连接成功"
        } else {
            return "连接失败"
        }

    }

    //读卡
    fun otgRead() {
        if (api!!.Authenticate(200, 200) != 1) {
            Log.i("TAG", "卡认证失败")
            return
        }
        val ici = HSIDCardInfo()
        if (api!!.ReadCard(ici, 200, 1300) == 1) {
            val msg = Message.obtain()
            msg.obj = ici
            msg.what = HandlerMsg.READ_SUCCESS
            appHandler?.sendMessage(msg)
        }
    }
    //自动读卡

    fun otgAutoread(): String {
        if (m_Auto) {
            m_Auto = false
            return "停止读卡"
        } else {
            m_Auto = true
            Thread(CPUThread()).start()
            return "自动读卡"
        }
    }


    private fun copy(context: Context, fileName: String, saveName: String,
                     savePath: String) {
        val path = File(savePath)
        if (!path.exists()) {
            path.mkdir()
        }

        try {
            val e = File("$savePath/$saveName")
            if (e.exists() && e.length() > 0L) {
                Log.i("LU", saveName + "存在了")
                return
            }

            val fos = FileOutputStream(e)
            val inputStream = context.resources.assets
                    .open(fileName)
            val buf = ByteArray(1024)
            val len = false

            var len1: Int = 0
            while ((inputStream.read(buf).apply { len1 = this }) != -1) {
                fos.write(buf, 0, len1)
            }

            fos.close()
            inputStream.close()
        } catch (var11: Exception) {
            Log.i("LU", "IO异常")
        }

    }


    fun onDestroy() {
        if (api == null) {
            return
        }
        api!!.unInit()
    }

    /**
     * 指纹 指位代码
     *
     * @param FPcode
     * @return
     */
    internal fun GetFPcode(FPcode: Int): String {
        when (FPcode) {
            11 -> return "右手拇指"
            12 -> return "右手食指"
            13 -> return "右手中指"
            14 -> return "右手环指"
            15 -> return "右手小指"
            16 -> return "左手拇指"
            17 -> return "左手食指"
            18 -> return "左手中指"
            19 -> return "左手环指"
            20 -> return "左手小指"
            97 -> return "右手不确定指位"
            98 -> return "左手不确定指位"
            99 -> return "其他不确定指位"
            else -> return "未知"
        }
    }

//    data class infoData(
//
//    )

    /*
     * HsOtgApi api = new HsOtgApi(h, eCardOTG.this);初始化
     * api.init()连接
     * api.Authenticate(200, 200) 卡认证  1为成功然后才可以读卡
     * api.ReadCard(ici, 200, 1300) ici为身份证类   "姓名：" + ic.getPeopleName() + "\n" + "性别：" + ic.getSex() + "\n" + "民族：" + ic.getPeople()
                + "\n" + "出生日期：" + df.format(ic.getBirthDay()) + "\n" + "地址：" + ic.getAddr() + "\n" + "身份号码："
                + ic.getIDCard() + "\n" + "签发机关：" + ic.getDepartment() + "\n" + "有效期限：" + ic.getStrartDate()
                + "-" + ic.getEndDate()
                200 为发送数据时长  1300为接收数据时长
                返回1为正确
        api.Unpack(filepath, ic.getwltdata())在读卡成功后调用  filepath 为解码库的绝对路径
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
          ic.getwltdata()为身份证的照片数据
        返回1为解码数据成功照片存在 filepath + "/zp.bmp"
     */

}

package com.anubis.module_ftp

import androidx.appcompat.app.AppCompatActivity
import android.app.Application
import android.os.Handler

/**
 * Author  ： AnubisASN   on 18-11-5 下午3:11.
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
object eDataFTP {
    val CONNECTION_FAILURE=0 //连接失败    what
    val CONNECTION_SUCCEED=1 //连接成功  what
    val CONNECTION_DISCONNECT=-1 //连接断开  what
    val DATA_TRANSFER=2 //连接断开  what
    var mFTPUI: androidx.appcompat.app.AppCompatActivity?=null
    var hint: String? = "0"
    var type:String="break"
    var UserName: String = "anubis"
    var PassWord: String = "anubis"
    var ChrootDir: String = "/sdcard/"
    var Port: Int = 21
    var StayAwake:Boolean=true
    var AllowAnonymous: Boolean = false
    var ShowPassword=false
    var mAPP: Application? = null
    var mBulkRegister: Class<*>? = null
    var mHndler:Handler?=null
//    var mState = true


    fun init(application:Application,Port: Int = 21, UserName: String = "anubis", PassWord: String = "anubis", ChrootDir: String = "/sdcard/", AllowAnonymous: Boolean = false,ShowPassword:Boolean=false,StayAwake:Boolean=true) {
        mAPP=application
        this.StayAwake=StayAwake
        this.UserName = UserName
        this.PassWord = PassWord
        this.ShowPassword=ShowPassword
        this.ChrootDir = ChrootDir
        this.AllowAnonymous = AllowAnonymous
        this.Port = Port

    }

}

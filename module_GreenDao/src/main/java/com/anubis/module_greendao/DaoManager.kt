package com.anubis.module_greendao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.greenrobot.greendao.query.QueryBuilder

/**
 * Author  ： AnubisASN   on 2018-08-07 11:49.
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
internal class DaoManager( var context: Context,val greenDaoClassName: String,val DB_NAME:String) {
    private var sDaoMaster:  Any? = null
    private var sHelper: Class<*>? = null
    private var sDaoSession: Any? = null
    /**
     * 判断是否有存在数据库，如果没有则创建
     * @return
     */
    val daoMaster: Any
        get() {
            if (sDaoMaster == null) {
                val helper = Class.forName("$greenDaoClassName.DaoMaster\$DevOpenHelper").getDeclaredConstructor(Context::class.java, String::class.java, SQLiteDatabase.CursorFactory::class.java).newInstance(context, DB_NAME, null)
                sHelper= Class.forName("$greenDaoClassName.DaoMaster\$DevOpenHelper")
                sDaoMaster=Class.forName("$greenDaoClassName.DaoMaster").getDeclaredConstructor(SQLiteDatabase::class.java).newInstance(helper ::class.java.getMethod("getWritableDatabase").invoke(helper))
            }
            return sDaoMaster!!
        }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口q
     * @return
     */
    val daoSession: Any
        get() {
            if (sDaoSession == null) {
                if (sDaoMaster == null) {
                    sDaoMaster = daoMaster
                }
                sDaoSession =sDaoMaster!!::class.java.getMethod("newSession").invoke(sDaoMaster)
            }
            return sDaoSession!!
        }


    /**
     * 打开输出日志，默认关闭
     */
    fun setDebug() {
        QueryBuilder.LOG_SQL = true
        QueryBuilder.LOG_VALUES = true
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    fun closeConnection() {
        closeHelper()
        closeDaoSession()
    }

    fun closeHelper() {
        if (sHelper != null) {
            sHelper!!.getMethod("close").invoke(sHelper!!.newInstance())
            sHelper = null
        }
    }

    fun closeDaoSession() {
        if (sDaoSession != null) {
            sDaoSession!!::class.java.getMethod("clear").invoke(sDaoSession!!)
            sDaoSession = null
        }
    }
}

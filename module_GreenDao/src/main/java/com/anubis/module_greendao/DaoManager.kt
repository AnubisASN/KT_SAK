package com.anubis.module_greendao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.anubis.module_greendao.Gen.DaoMaster
import java.lang.reflect.Modifier
import java.util.*

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
internal class DaoManager(val context: Context, val greenDaoClassName: String, val DB_NAME: String) {
    private var sDaoMaster: Any? = null
    private var sDaoSession: Any? = null
    private var sHelper: Any? = null


    init {
//        sDaoMaster = Class.forName("$greenDaoClassName.DaoMaster")
//        sDaoSession = Class.forName("$greenDaoClassName.DaoSession")
        val clzzs = Class.forName("$greenDaoClassName.DaoMaster").declaredClasses
        for (cls in clzzs!!) {
            val mod = cls.modifiers
            val modifier = Modifier.toString(mod)
            if (modifier.contains("static")) {
                sHelper = cls
                print(cls.name + "\n")
            }
        }
    }

    /**
     * 判断是否有存在数据库，如果没有则创建
     * @return
     */
    private val daoMaster: Any
        get() {
            if (sDaoMaster == null) {

//                val helper = Class.forName("$greenDaoClassName.DaoMaster\$DevOpenHelper").getDeclaredConstructor(Context::class.java, String::class.java, SQLiteDatabase.CursorFactory::class.java).newInstance(context, DB_NAME, null)

                val helper= DaoMaster.DevOpenHelper(context, DB_NAME, null)
             val mh=   helper.writableDatabase
//                val mHelper = helper::class.java.getMethod("getWritableDatabase").invoke(SQLiteOpenHelper)
//                helper::class.java.getMethod("getWritableDatabase").invoke(helper)
//                sDaoMaster = DaoMaster(helper.getWritableDatabase())
                sDaoMaster =Class.forName("$greenDaoClassName.DaoSession")!!::class.java.getDeclaredConstructor(SQLiteDatabase::class.java).newInstance(helper::class.java.getMethod("getWritableDatabase").invoke(mh))
            }
            return sDaoMaster!!
        }


    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     * @return
     */
    val daoSession: Any
        get() {
            if (sDaoSession == null) {
                if (sDaoMaster == null) {
                    sDaoMaster = daoMaster
                }
                sDaoSession = Class.forName("$greenDaoClassName.DaoMaster").getMethod("newSession").invoke(sDaoMaster)
            }
            return sDaoSession!!
        }


    /**
     * 打开输出日志，默认关闭
     */
    fun setDebug() {
//        QueryBuilder.LOG_SQL = true
//        QueryBuilder.LOG_VALUES = true
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
            (sHelper as Class<*>).getMethod("close").invoke(sHelper)
            sHelper = null
        }
    }

    fun closeDaoSession() {
        if (sDaoSession != null) {
            (sDaoSession as Class<*>).getMethod("clear").invoke(sDaoSession)
            sDaoSession = null
        }
    }

}


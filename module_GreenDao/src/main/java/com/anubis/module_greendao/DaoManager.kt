package com.anubis.module_greendao

import android.content.Context
import modlue_greendao.Gen.DaoMaster
import modlue_greendao.Gen.DaoSession

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
internal class DaoManager {

    private var context: Context? = null

    val DaoMaster=Class.forName("com.anubis.modlue_greendao.Gen.DaoMaster")

    /**
     * 判断是否有存在数据库，如果没有则创建
     * @return
     */
    val daoMaster: DaoMaster
        get() {
            if (sDaoMaster == null) {
                val helper = DaoMaster.DevOpenHelper(context, DB_NAME, null)
                sDaoMaster = DaoMaster(helper.getWritableDatabase())
            }
            return sDaoMaster
        }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     * @return
     */
    val daoSession: DaoSession
        get() {
            if (sDaoSession == null) {
                if (sDaoMaster == null) {
                    sDaoMaster = daoMaster
                }
                sDaoSession = sDaoMaster!!.newSession()
            }
            return sDaoSession
        }

    fun init(context: Context) {
        this.context = context
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
            sHelper!!.close()
            sHelper = null
        }
    }

    fun closeDaoSession() {
        if (sDaoSession != null) {
            sDaoSession!!.clear()
            sDaoSession = null
        }
    }

    companion object {
        private val TAG = DaoManager::class.java.simpleName
        private val DB_NAME = "greendaotest"

        //多线程中要被共享的使用volatile关键字修饰
        /**
         * 单例模式获得操作数据库对象
         * @return
         */
        @Volatile
        val instance = DaoManager()
        private var sDaoMaster: DaoMaster? = null
        private var sHelper: DaoMaster.DevOpenHelper? = null
        private var sDaoSession: DaoSession? = null
    }
}

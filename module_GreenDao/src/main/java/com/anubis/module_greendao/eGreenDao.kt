package com.anubis.module_greendao

import android.content.Context
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.AbstractDaoSession

/**
 * Author  ： AnubisASN   on 2018-08-07 11:52.
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
 * 说明：数据库
 */
class eGreenDao private constructor(){
    private val mManager: DaoManager = DaoManager(mContext, mGreenDaoClassName, mDB_NAME)
    private var daoSession: AbstractDaoSession? = null
    companion object {
        private lateinit var mContext: Context
        private lateinit var mGreenDaoClassName: String
        private lateinit var mDB_NAME: String
        fun eInit(mContext: Context, mGreenDaoClassName: String = "com.anubis.modlue_greendao.Gen", mDB_NAME: String = "DB_ASN"): eGreenDao {
            this.mContext = mContext
            this.mGreenDaoClassName = mGreenDaoClassName
            this.mDB_NAME = mDB_NAME
            return eInit
        }
        private val eInit by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { eGreenDao() }
    }

    init {
        daoSession = mManager.daoSession as AbstractDaoSession
    }

    /**
     * 完成user记录的插入，如果表未创建，先创建User表
     * @param user
     * @return
     */
    fun <T> eInsertUser(user: T): Boolean {
        var flag = false
        val dataDao = daoSession!!::class.java.getMethod("get${(user as Any)::class.java.simpleName}Dao").invoke(daoSession) as AbstractDao<Any, Any>
        flag = dataDao.insert(user) != (-1).toLong()
        return flag
    }

    /**
     * 插入多条数据，在子线程操作
     * @param userList
     * @return
     */
    fun <T>eInsertMultUser(userList: List<T>): Boolean {
        var flag = false
        try {
            daoSession!!.runInTx {
                for (user in userList) {
                    daoSession!!.insertOrReplace(user)
                }
            }
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return flag
    }

    /**
     * 修改一条数据
     * @param user
     * @return
     */
    fun <T>eUpdateUser(user: T): Boolean {
        var flag = false
        try {
            daoSession!!.update(user)
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return flag
    }

    /**
     * 删除单条记录
     * @param user
     * @return
     */
    fun <T>eDeleteUser(user: T): Boolean {
        var flag = false
        try {
            //按照id删除
            daoSession!!.delete(user)
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return flag
    }

    /**
     * 删除所有记录
     * @return
     */
    fun <T>eDeleteAll(user: T): Boolean {
        var flag = false
        try {
            //按照id删除
            daoSession!!.deleteAll((user as Any)::class.java)
            flag = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return flag
    }

    /**
     * 查询所有记录
     * @return
     */
    fun <T>eQueryAllUser(user: T): List<T> {
        return daoSession!!.loadAll<Any, Any>((user as Any)::class.java as Class<Any>?) as List<T>
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    fun <T>eQueryUserById(key: Long, user: Any): T {
        return daoSession!!.load((user as Any)::class.java, key) as T
    }

    /**
     * 使用native sql进行查询操作
     * sql 例如：where NAME=?
     */
    fun <T>eQueryUserByNativeSql(user: T, sql: String, conditions: Array<String>): List<T> {
        return daoSession!!.queryRaw<Any, Any>((user as Any)::class.java as Class<Any>?, sql, *conditions) as List<T>
    }


    /**
     * 关闭数据库连接
     * @return
     */
    fun eCloseConnection() {
        mManager.closeConnection()
    }

}

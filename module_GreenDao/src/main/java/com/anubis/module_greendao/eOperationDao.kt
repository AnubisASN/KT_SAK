package com.anubis.module_greendao

import android.content.Context
import android.util.Log

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
class eOperationDao(context: Context, val greenDaoClassName: String = "com.anubis.modlue_greendao.Gen", DB_NAME: String = "DB_NAME") {
    private val mManager: DaoManager = DaoManager(context, greenDaoClassName, DB_NAME)
    private var daoSession: AbstractDaoSession? = null
    private val TAG = "TAG"
    init {
        daoSession = mManager.daoSession as AbstractDaoSession
    }

    /**
     * 完成user记录的插入，如果表未创建，先创建User表
     * @param user
     * @return
     */
    fun insertUser(user: Any): Boolean {
        var flag = false
        val dataDao = daoSession!!::class.java.getMethod("get${user::class.java.simpleName}Dao").invoke(daoSession) as AbstractDao<Any, Any>
        flag = dataDao.insert(user) != (-1).toLong()
        Log.i(TAG, "insert Any :" + flag + "-->" + user.toString())
        return flag
    }

    /**
     * 插入多条数据，在子线程操作
     * @param userList
     * @return
     */
    fun insertMultUser(userList: List<Any>): Boolean {
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
    fun updateUser(user: Any): Boolean {
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
    fun deleteUser(user: Any): Boolean {
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
    fun deleteAll(user: Any): Boolean {
        var flag = false
        try {
            //按照id删除
            daoSession!!.deleteAll(user::class.java)
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
    fun queryAllUser(user: Any): List<Any> {
        return daoSession!!.loadAll<Any, Any>(user::class.java as Class<Any>?) as List<Any>
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    fun queryUserById(key: Long, user: Any): Any {
        return daoSession!!.load(user::class.java, key)
    }

    /**
     * 使用native sql进行查询操作
     */
    fun queryUserByNativeSql(sql: String, conditions: Array<String>, user: Any): List<Any> {
        return daoSession!!.queryRaw<Any, Any>(user::class.java as Class<Any>?, sql, *conditions)
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
//    fun queryUserByQueryBuilder(id: Long,user: Any): List<Any> {
//        val queryBuilder =daoSession!!.queryBuilder(Any::class.java)
//        //反射
//      val clazz=  Class.forName("$greenDaoClassName.${user::class.java.simpleName}Dao\$Properties")
//        return queryBuilder.where(DataDao.Properties.Name.eq(id)).list()
//    }

    /**
     * 关闭数据库连接
     * @return
     */
    fun closeConnection() {
        mManager.closeConnection()
    }

}

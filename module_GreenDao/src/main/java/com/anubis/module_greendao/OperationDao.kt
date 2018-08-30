package com.anubis.module_greendao

import android.content.Context
import android.util.Log

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
 * 说明：
 */
class OperationDao(val context: Context, val greenDaoClassName: String = "com.anubis.modlue_greendao.Gen", val DB_NAME: String = "DB_NAME") {
    private var mManager: DaoManager = DaoManager(context, greenDaoClassName, DB_NAME)
    private var daoSession: Any?=null

    init {
        daoSession = mManager.daoSession
        print(daoSession!!::class.java)
    }

    /**
     * 完成user记录的插入，如果表未创建，先创建User表
     * @param user
     * @return
     */
    fun insertUser(data: Class<*>): Boolean {
        var flag = false
        print("daoSession:"+daoSession!!::class.java.name)
        val dataDao = daoSession!!::class.java.getMethod("get${data.simpleName}Dao").invoke(daoSession)
        flag = dataDao::class.java.getMethod("insert", Class::class.java).invoke(dataDao, data) !== -1
        Log.i(TAG, "insert ExcelDao :" + flag + "-->" + data.toString())
        return flag
    }

    /**
     * 插入多条数据，在子线程操作
     * @param userList
     * @return
     */
    fun insertMultUser(dataList: List<Any>): Boolean {
        var flag = false
        try {

            daoSession!!::class.java.getMethod("runInTx", Runnable::class.java).invoke(daoSession, Runnable {
                for (user in dataList) {
                    daoSession!!::class.java.getMethod("insertOrReplace", Class::class.java).invoke(daoSession, user)
                }
            })
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
    fun updateUser(data: Class<*>): Boolean {
        var flag = false
        try {
            daoSession!!::class.java.getMethod("update",Class::class.java).invoke(daoSession,data)
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
    fun deleteUser(data: Class<*>): Boolean {
        var flag = false
        try {
            //按照id删除
            daoSession!!::class.java.getMethod("delete",Class::class.java).invoke(daoSession,data)
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
    fun deleteAll(data:Class<*>): Boolean {
        var flag = false
        try {
            //按照id删除
            daoSession!!::class.java.getMethod("deleteAll",Class::class.java).invoke(daoSession,data)
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
    fun queryAllUser(data:Class<*>)= daoSession!!::class.java.getMethod("loadAll",Class::class.java).invoke(daoSession,data) as List<*>

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    fun queryUserById(data:Class<*>,key: Any)=daoSession!!::class.java.getMethod("load",Class::class.java,Any::class.java).invoke(daoSession,data,key)


    /**
     * 使用native sql进行查询操作
     */
    fun queryUserByNativeSql(data: Class<*>,sql: String, conditions: Array<String>)=daoSession!!::class.java.getMethod("queryRaw",Class::class.java,String::class.java,Array<String>::class.java).invoke(data, sql, conditions)


    /**
     * 使用queryBuilder进行查询
     * @return
     */
//    fun queryUserByQueryBuilder(data: Class<*>,id: Long): List<Any> {
//        val queryBuilder = daoSession!!::class.java.getMethod("queryBuilder",Class::class.java).invoke(data)
//        return queryBuilder.where(ExcelDaoDao.Properties.Name.eq(id)).list()
//    }

    companion object {
        private val TAG = OperationDao::class.java.simpleName
    }

}

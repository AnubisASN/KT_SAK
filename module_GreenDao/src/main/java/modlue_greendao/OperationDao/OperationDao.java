package modlue_greendao.OperationDao;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import modlue_greendao.Gen.ExcelDaoDao;

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
public class OperationDao {
    private static final String TAG = OperationDao.class.getSimpleName();
    private DaoManager mManager;

    public OperationDao(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成user记录的插入，如果表未创建，先创建User表
     * @param user
     * @return
     */
    public boolean insertUser(ExcelDao user){
        boolean flag = false;
        flag = mManager.getDaoSession().getExcelDaoDao().insert(user) != -1;
        Log.i(TAG, "insert ExcelDao :" + flag + "-->" + user.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param userList
     * @return
     */
    public boolean insertMultUser(final List<ExcelDao> userList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (ExcelDao user : userList) {
                        mManager.getDaoSession().insertOrReplace(user);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     * @param user
     * @return
     */
    public boolean updateUser(ExcelDao user){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(user);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param user
     * @return
     */
    public boolean deleteUser(ExcelDao user){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(user);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(ExcelDao.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<ExcelDao> queryAllUser(){
        return mManager.getDaoSession().loadAll(ExcelDao.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public ExcelDao queryUserById(long key){
        return mManager.getDaoSession().load(ExcelDao.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<ExcelDao> queryUserByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(ExcelDao.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<ExcelDao> queryUserByQueryBuilder(long id){
        QueryBuilder<ExcelDao> queryBuilder = mManager.getDaoSession().queryBuilder(ExcelDao.class);
        return queryBuilder.where(ExcelDaoDao.Properties.Name.eq(id)).list();
    }

}

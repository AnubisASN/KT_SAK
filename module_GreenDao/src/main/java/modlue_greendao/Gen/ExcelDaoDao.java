package modlue_greendao.Gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.anubis.sxk_facedetection.dataBean.ExcelDao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "EXCEL_DAO".
*/
public class ExcelDaoDao extends AbstractDao<ExcelDao, String> {

    public static final String TABLENAME = "EXCEL_DAO";

    /**
     * Properties of entity ExcelDao.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Time = new Property(0, String.class, "time", true, "TIME");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Msg = new Property(2, String.class, "msg", false, "MSG");
        public final static Property Group_id = new Property(3, String.class, "group_id", false, "GROUP_ID");
        public final static Property Scores = new Property(4, String.class, "scores", false, "SCORES");
        public final static Property Living_body = new Property(5, String.class, "living_body", false, "LIVING_BODY");
    }


    public ExcelDaoDao(DaoConfig config) {
        super(config);
    }
    
    public ExcelDaoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EXCEL_DAO\" (" + //
                "\"TIME\" TEXT PRIMARY KEY NOT NULL ," + // 0: time
                "\"NAME\" TEXT," + // 1: name
                "\"MSG\" TEXT," + // 2: msg
                "\"GROUP_ID\" TEXT," + // 3: group_id
                "\"SCORES\" TEXT," + // 4: scores
                "\"LIVING_BODY\" TEXT);"); // 5: living_body
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EXCEL_DAO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ExcelDao entity) {
        stmt.clearBindings();
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(1, time);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String msg = entity.getMsg();
        if (msg != null) {
            stmt.bindString(3, msg);
        }
 
        String group_id = entity.getGroup_id();
        if (group_id != null) {
            stmt.bindString(4, group_id);
        }
 
        String scores = entity.getScores();
        if (scores != null) {
            stmt.bindString(5, scores);
        }
 
        String living_body = entity.getLiving_body();
        if (living_body != null) {
            stmt.bindString(6, living_body);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ExcelDao entity) {
        stmt.clearBindings();
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(1, time);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String msg = entity.getMsg();
        if (msg != null) {
            stmt.bindString(3, msg);
        }
 
        String group_id = entity.getGroup_id();
        if (group_id != null) {
            stmt.bindString(4, group_id);
        }
 
        String scores = entity.getScores();
        if (scores != null) {
            stmt.bindString(5, scores);
        }
 
        String living_body = entity.getLiving_body();
        if (living_body != null) {
            stmt.bindString(6, living_body);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public ExcelDao readEntity(Cursor cursor, int offset) {
        ExcelDao entity = new ExcelDao( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // time
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // msg
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // group_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // scores
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // living_body
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ExcelDao entity, int offset) {
        entity.setTime(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMsg(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGroup_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setScores(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLiving_body(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final String updateKeyAfterInsert(ExcelDao entity, long rowId) {
        return entity.getTime();
    }
    
    @Override
    public String getKey(ExcelDao entity) {
        if(entity != null) {
            return entity.getTime();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ExcelDao entity) {
        return entity.getTime() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

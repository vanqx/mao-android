package com.ichangmao.app.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ichangmao.app.MaoApp;
import com.ichangmao.commons.MaoLog;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Created by yangchangmao on 2016/5/5.
 * 数据库操作工具类
 */
public class MaoDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mao.db";
    //每次修改数据库表结构之后，将DB_VERSION +1
    //1 增加UserDao
    private static final int DB_VERSION = 1;

    private static MaoDbHelper mJunkDbHelper = null;

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    private Context mContext;

    public static synchronized MaoDbHelper getInstance() {
        if (mJunkDbHelper == null) {
            mJunkDbHelper = new MaoDbHelper(MaoApp.getContext());
        }
        return mJunkDbHelper;
    }

    private MaoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Class<? extends BaseDao> daoClass : getDaoClasses()) {
            try {
                Constructor<? extends BaseDao> daoConstructor = daoClass.getConstructor();
                BaseDao dao = daoConstructor.newInstance();
                dao.onCreate(db);
            } catch (Exception e) {
                log.e("create table error:  " + daoClass.getName());
                log.e(e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Class<? extends BaseDao> daoClass : getDaoClasses()) {
            try {
                Constructor<? extends BaseDao> daoConstructor = daoClass.getConstructor();
                BaseDao dao = daoConstructor.newInstance();
                dao.onUpgrade(db, oldVersion, newVersion);
            } catch (Exception e) {
                log.e("upgrade table error:  " + daoClass.getName());
                log.e(e);
            }
        }
    }


    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = null;
        try {
            db = super.getWritableDatabase();
        } catch (SQLException se) {
            // 数据库可能被破坏，删除原始数据库，然后重新创建
            try {
                log.d("getWritableDatabase error, delete and recreate");
                mContext.deleteDatabase(DB_NAME);
                db = super.getWritableDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return db;
    }

    private ArrayList<Class<? extends BaseDao>> getDaoClasses() {
        ArrayList<Class<? extends BaseDao>> daoClasses = new ArrayList<>();
        //在这里注册表
        daoClasses.add(UserDao.class);

        return daoClasses;
    }
}

package com.hjhq.teamface.basis.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hjhq.teamface.basis.database.gen.DaoMaster;
import com.hjhq.teamface.basis.database.gen.DaoSession;
import com.hjhq.teamface.basis.util.AppManager;
import com.hjhq.teamface.basis.util.file.SPHelper;

/**
 * Created by Administrator on 2018/9/25.
 * Describe：数据初始化,操作
 */

public class DbCreator {
    private static DbHelper helper;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static Context mContext;
    private static DaoSession daoSession;

    private static class SingletonHolder {
        private static final DbCreator INSTANCE = new DbCreator();
    }

    public static DbCreator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 初始数据库
     */
    public void setupDatabase(Context mContext) {
        this.mContext = mContext;
        if (mContext == null) {
            AppManager.restartApp();
            return;
        }
        //创建数据库
        //DaoMaster.DevOpenHelper helper1 = new DaoMaster.DevOpenHelper(this, Constants.DATABASE_NAME);
        //自定义的 OpenHelper
        //DbHelper helper = new DbHelper(mContext, MsgConstant.DATABASE_NAME);
        helper = new DbHelper(mContext, SPHelper.getCompanyId() + "_" + SPHelper.getUserId());
        //获取可写数据库
        db = helper.getWritableDatabase();
        //获取数据库对象
        daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    /**
     * 初始化数据库会话
     *
     * @return
     */
    public  DaoSession getDaoInstant() {
        if (daoSession == null) {
            getInstance().setupDatabase(mContext);
        }
        return daoSession;
    }
}

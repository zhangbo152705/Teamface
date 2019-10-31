package com.hjhq.teamface.basis.database;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.database.gen.CacheDataDao;
import com.hjhq.teamface.basis.database.gen.DaoMaster;
import com.hjhq.teamface.basis.database.gen.MemberDao;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/25.
 * Describe：缓存操作类
 */

public class CacheDataHelper {
    /**
     * 工作台缓存
     */
    public static final String WORKBENCH_CACHE_DATA = "workbench_cache_data";
    /**
     * 同事圈缓存
     */
    public static final String CCCHAT_CACHE_DATA = "ccchat_cache_data";
    /**
     * 邮件列表缓存
     */
    public static final String EMAIL_CACHE_DATA = "email_cache_data";
    /**
     * 最近联系人缓存
     */
    public static final String RECENT_CONTACTS_CACHE_DATA = "recent_contacts_cache_data";
    /**
     * 文件库缓存
     */
    public static final String FILE_LIB_CACHE_DATA = "file_lib_cache_data";
    /**
     * 项目文件夹缓存
     */
    public static final String PROJECT_FILE_LIB_CACHE_DATA = "project_file_lib_cache_data";
    /**
     * 移动/复制文件(夹)目录缓存
     */
    public static final String FILE_LIB_FOLDER_CACHE_DATA = "file_lib_folder_cache_data";
    /**
     * 文件库选择文件数据缓存
     */
    public static final String CHOOSE_FILE_CACHE_DATA = "choose_file_cache_data";
    /**
     * 系统应用
     */
    public static final String SYSTEM_APP_CACHE_DATA = "system_app_cache_data";
    /**
     * 自定义应用
     */
    public static final String CUSTOM_APP_CACHE_DATA = "custom_app_cache_data";
    /**
     * 自定义应用下的模块
     */
    public static final String CUSTOM_MODULE_CACHE_DATA = "custom_module_cache_data";
    public static final int CUSTOM_MODULE_CACHE_DATA_NUM = 0x500005;
    public static final int CUSTOM_MODULE_CACHE_DATA_NUM2 = 0x500006;
    /**
     * 项目列表缓存
     */
    public static final String PROJECT_LIST_CACHE_DATA = "project_list_cache_data";
    /**
     * 项目任务分组缓存
     */
    public static final String PROJECT_TASK_GROUP_CACHE_DATA = "project_task_group_cache_data";
    /**
     * 项目任务列表缓存
     */
    public static final String PROJECT_TASK_CACHE_DATA = "project_task_cache_data";
    /**
     * 项目任务列表数据缓存
     */
    public static final String PROJECT_TASK_LIST_CACHE_DATA = "project_task_list_cache_data";
    /**
     * 项目分享列表数据缓存
     */
    public static final String PROJECT_SHARE_LIST_CACHE_DATA = "project_share_list_cache_data";
    /**
     * 项目文件夹列表数据缓存
     */
    public static final String PROJECT_FOLDER_LIST_CACHE_DATA = "project_folder_list_cache_data";

    private static DbHelper helper;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private static CacheData cacheData;

    /**
     * 创建缓存数据
     *
     * @return
     */
    public static CacheData buildCacheData() {
        cacheData = new CacheData();
        cacheData.setCompanyId(SPHelper.getCompanyId());
        cacheData.setEmployeeId(SPHelper.getEmployeeId());
        return cacheData;
    }

    /**
     * 创建缓存数据
     *
     * @param type
     * @param key
     * @return
     */
    public static CacheData buildCacheData(String type, String key) {
        cacheData = buildCacheData();
        cacheData.setType(type);
        cacheData.setKey(key);

        return cacheData;
    }

    /**
     * 创建缓存数据
     *
     * @param type
     * @param key
     * @param content
     * @return
     */
    public static CacheData buildCacheData(String type, String key, String content) {
        cacheData = buildCacheData(type, key);
        cacheData.setContent(content);

        return cacheData;
    }

    /**
     * 删除数据
     *
     * @return the query result list
     */
    public <T> boolean delete(T object) {
        boolean flag = false;
        try {
            DbCreator.getInstance().getDaoInstant().delete(object);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 添加数据
     *
     * @return
     */
    public static <T> boolean saveOrReplace(T object) {

        if (null == object || DbCreator.getInstance() ==null || DbCreator.getInstance().getDaoInstant() ==null) {//zzh:增加不为空判断
            return false;
        }
        return DbCreator.getInstance().getDaoInstant().insertOrReplace(object) != -1;
    }

    /**
     * 保存缓存数据
     *
     * @param data
     * @return
     */
    public static boolean saveCacheData(CacheData data) {
        if (DbCreator.getInstance() == null || DbCreator.getInstance().getDaoInstant() ==null){
            Log.e("saveCacheData","获取缓存数据失败");
            return false;
        }
        MemberDao dao = DbCreator.getInstance().getDaoInstant().getMemberDao();
        List<Member> list = new ArrayList<>();
        QueryBuilder<Member> qb = dao
                .queryBuilder();
        qb.where(MemberDao.Properties.Company_id.eq(SPHelper.getCompanyId()), MemberDao.Properties.Sign_id.eq(SPHelper.getUserId()));
        list = qb.list();
        if (list == null || list.size() > 0) {

        }
        return true;
    }


    /**
     * 获取缓存数据
     *
     * @param type
     * @param key
     * @return
     */
    public static String getCacheData(String type, String key) {
        if (DbCreator.getInstance() == null || DbCreator.getInstance().getDaoInstant() ==null){//zzh:增加不为空判断
            Log.e("saveCacheData","获取缓存数据失败");
            return "";
        }
        String result = "";
        CacheDataDao dao = DbCreator.getInstance().getDaoInstant().getCacheDataDao();
        List<CacheData> list = new ArrayList<>();
        QueryBuilder<CacheData> qb = dao
                .queryBuilder();
        qb.where(CacheDataDao.Properties.CompanyId.eq(SPHelper.getCompanyId()),
                CacheDataDao.Properties.EmployeeId.eq(SPHelper.getEmployeeId()),
                CacheDataDao.Properties.Type.eq(type),
                CacheDataDao.Properties.Key.eq(key));
        list = qb.list();
        if (list == null || list.size() > 0) {
            result = list.get(0).getContent();
        }
        return result;
    }

    /**
     * 获取缓存(列表类型)
     *
     * @param clazz
     * @param type
     * @param key
     * @param <T>
     * @return
     */
    public static <T> List<T> getCacheDataList(Class<T> clazz, String type, String key) {
        String str = CacheDataHelper.getCacheData(type, key);
        if (TextUtils.isEmpty(str)) {
            return new ArrayList<>();
        }
        try {
            return new Gson().fromJson(str, new TypeToken<ArrayList<T>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return new ArrayList<T>();
    }

    /**
     * 保存缓存数据
     *
     * @param type
     * @param key
     * @param content
     * @return
     */
    public static boolean saveCacheData(String type, String key, String content) {
        if (DbCreator.getInstance() == null || DbCreator.getInstance().getDaoInstant() ==null
                || DbCreator.getInstance().getDaoInstant().getCacheDataDao() ==null){
            Log.e("saveCacheData","保存缓存数据失败");
            return false;
        }
        CacheDataDao dao = DbCreator.getInstance().getDaoInstant().getCacheDataDao();
        List<CacheData> list = new ArrayList<>();
        QueryBuilder<CacheData> qb = dao
                .queryBuilder();
        qb.where(CacheDataDao.Properties.CompanyId.eq(SPHelper.getCompanyId()),
                CacheDataDao.Properties.EmployeeId.eq(SPHelper.getEmployeeId()),
                CacheDataDao.Properties.Type.eq(type),
                CacheDataDao.Properties.Key.eq(key));
        list = qb.list();
        if (list != null && list.size() > 0) {
            list.get(0).setContent(content);
            return true;
        } else {
            saveOrReplace(buildCacheData(type, key, content));
            return true;
        }
    }

    public static boolean saveCacheDataList(String type, String bean, String num) {
        CacheDataDao dao = DbCreator.getInstance().getDaoInstant().getCacheDataDao();
        List<CacheData> list = new ArrayList<>();
        QueryBuilder<CacheData> qb = dao
                .queryBuilder();
        qb.where(CacheDataDao.Properties.CompanyId.eq(SPHelper.getCompanyId()),
                CacheDataDao.Properties.EmployeeId.eq(SPHelper.getEmployeeId()),
                CacheDataDao.Properties.Type.eq(type));
        list = qb.list();
        JSONArray jsonArray = JSONObject.parseArray(num);
        JSONObject o = jsonArray.getJSONObject((int) Math.round(jsonArray.size() * Math.random() - 1));
        String b = o.getString("english_name");
        String d = o.getString("id");
                EventBusUtils.sendEvent(new MessageBean(CUSTOM_MODULE_CACHE_DATA_NUM2, b, d));
        if (list != null || list.size() > 0) {
            list.get(0).setContent(num);
            saveOrReplace(list.get(0));
            return true;
        } else {
            saveOrReplace(buildCacheData(type, bean, num));
            return true;
        }
    }

    public static List<CacheData> getCacheDataList(String type) {
        CacheDataDao dao = DbCreator.getInstance().getDaoInstant().getCacheDataDao();
        List<CacheData> list = new ArrayList<>();
        QueryBuilder<CacheData> qb = dao
                .queryBuilder();
        qb.where(CacheDataDao.Properties.CompanyId.eq(SPHelper.getCompanyId()),
                CacheDataDao.Properties.EmployeeId.eq(SPHelper.getEmployeeId()),
                CacheDataDao.Properties.Type.eq(type));
        list = qb.list();
        return list;
    }
}

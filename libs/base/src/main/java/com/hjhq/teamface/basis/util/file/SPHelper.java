package com.hjhq.teamface.basis.util.file;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.bean.EmailBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.database.Member;

import java.util.ArrayList;
import java.util.List;


/**
 * SP帮助类
 * 帮助处理SP业务，与业务逻辑耦合
 * Created by Administrator on 2018/2/1.
 */

public class SPHelper {
    private static Context context;

    public static void init(Application application) {
        context = application;
    }

    /**
     * 根据员工id来获取 保存常用联系人的SP名称
     *
     * @return 联系人的SharedPreferences 名称
     */
    private static String getContactsName() {
        return SPUtils.getString(context, AppConst.EMPLOYEE_ID);
    }

    /**
     * 获取最近联系人
     */
    public static List<Member> getRecentContacts() {
        String recentStr = SPUtils.getString(context, getContactsName(), AppConst.RECENTLY_CONTACTS);
        recentStr = CacheDataHelper.getCacheData(CacheDataHelper.RECENT_CONTACTS_CACHE_DATA, AppConst.RECENTLY_CONTACTS);
        if (TextUtils.isEmpty(recentStr)) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(recentStr, new TypeToken<List<Member>>() {
        }.getType());
    }

    /**
     * 保存最近联系人
     *
     * @param datalist
     * @param nullable
     * @param <T>
     */
    public static <T> boolean saveRecentContacts(List<T> datalist, boolean nullable) {
        if (!nullable && (null == datalist || datalist.size() <= 0)) {
            return false;
        }
        return CacheDataHelper.saveCacheData(CacheDataHelper.RECENT_CONTACTS_CACHE_DATA, AppConst.RECENTLY_CONTACTS, JSONObject.toJSONString(datalist));
    }

    /**
     * 得到用户信息
     *
     * @param type : 类型
     */
    public static <T> T getUserInfo(Class<T> type) {
        return SPUtils.getObject(context, AppConst.USER_INFO, type);
    }

    /**
     * 保存用户信息
     *
     * @param value : 用户信息
     * @return 是否成功
     */
    public static boolean setUserInfo(Object value) {
        return SPUtils.setObject(context, AppConst.USER_INFO, value);
    }

    /**
     * 获取同事圈缓存
     *
     * @param type : 类型
     */
    public static <T> T getCCChatInfo(Class<T> type) {
        String str = CacheDataHelper.getCacheData(CacheDataHelper.CCCHAT_CACHE_DATA, AppConst.CCCHAT);
        return type.cast(new Gson().fromJson(str, type));
    }

    /**
     * 获取缓存
     *
     * @param clazz
     * @param type
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getCacheData(Class<T> clazz, String type, String key) {
        String str = CacheDataHelper.getCacheData(type, key);
        return clazz.cast(new Gson().fromJson(str, clazz));
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
     * 保存同事圈缓存
     *
     * @param value
     * @return
     */
    public static boolean setCCChatInfo(Object value) {
        return CacheDataHelper.saveCacheData(CacheDataHelper.CCCHAT_CACHE_DATA, AppConst.CCCHAT, JSONObject.toJSONString(value));
    }

    /**
     * 获取数据分析缓存
     */
    public static String getChartCache() {
        return SPUtils.getString(context, getCompanyId() + "_" + getEmployeeId(), AppConst.CHART_CACHE);
    }

    /**
     * 保存数据分析缓存
     *
     * @param value : 用户帐号
     * @return 是否成功
     */
    public static boolean setChartCache(String value) {
        return SPUtils.setString(context, getCompanyId() + "_" + getEmployeeId(), AppConst.CHART_CACHE, value);
    }

    /**
     * 得到用户帐号
     */
    public static String getUserAccount() {
        return SPUtils.getString(context, AppConst.USER_ACCOUNT);
    }

    /**
     * 保存用户帐号
     *
     * @param value : 用户帐号
     * @return 是否成功
     */
    public static boolean setUserAccount(String value) {
        return SPUtils.setString(context, AppConst.USER_ACCOUNT, value);
    }

    /**
     * 获取域名
     */
    public static String getDomain() {
        return SPUtils.getString(context, AppConst.DOMAIN);
    }

    /**
     * 保存域名
     *
     * @param value : 用户帐号
     * @return 是否成功
     */
    public static boolean setDomain(String value) {
        /*String newValue = value;
        if (!TextUtils.isEmpty(value) && !value.endsWith("/")) {
            newValue = newValue + "/";
        }*/
        return SPUtils.setString(context, AppConst.DOMAIN, value);
    }

    /**
     * 获取登录设置域名
     */
    public static String getLoginSettingDomain() {
        return SPUtils.getString(context, AppConst.LOGINGSETTING_DOMAIN);
    }

    /**
     * 保存登录设置域名
     */
    public static boolean setLoginSettingDomain(String value) {
        /*String newValue = value;
        if (!TextUtils.isEmpty(value) && !value.endsWith("/")) {
            newValue = newValue + "/";
        }*/
        return SPUtils.setString(context, AppConst.LOGINGSETTING_DOMAIN, value);
    }

    /**
     * 获取socket设置地址
     */
    public static String getSocketUrl() {
        return SPUtils.getString(context, AppConst.SOCKET_URL);
    }

    /**
     * 保存socket设置域名
     */
    public static boolean setSocketUrl(String value) {
        /*String newValue = value;
        if (!TextUtils.isEmpty(value) && !value.endsWith("/")) {
            newValue = newValue + "/";
        }*/
        return SPUtils.setString(context, AppConst.SOCKET_URL, value);
    }


    /**
     * 获取是否记住IP地址
     */
    public static boolean getIsRemenberIp() {
        return SPUtils.getBoolean(context, AppConst.IS_REMENBER_SOCKET_URL);
    }

    /**
     * 保存记住IP地址
     */
    public static boolean setIsRemenberIp(boolean value) {

        return SPUtils.setBoolean(context, AppConst.IS_REMENBER_SOCKET_URL, value);
    }

    /**
     * 保存历史socket地址
     */
    public static boolean setHistorySocketUrl(String value) {
        /*String newValue = value;
        if (!TextUtils.isEmpty(value) && !value.endsWith("/")) {
            newValue = newValue + "/";
        }*/
        return SPUtils.setString(context, AppConst.HISTORY_SOCKET_URL, value);
    }


    /**
     * 获取是否记住IP地址
     */
    public static String getHistorySocketUrl() {
        return SPUtils.getString(context, AppConst.HISTORY_SOCKET_URL);
    }

    /**
     * 缓存同事圈背景地址
     *
     * @param value : 同事圈背景链接
     * @return 是否成功
     */
    public static boolean setCCChatBg(String value) {
        return SPUtils.setString(context, getCompanyId() + "_" + getEmployeeId(), AppConst.CCCHAT_BG, value);
    }

    /**
     * 获取缓存的同事圈背景地址
     *
     * @return
     */
    public static String getCCChatBg() {
        return SPUtils.getString(context, getCompanyId() + "_" + getEmployeeId(), AppConst.CCCHAT_BG);
    }

    /**
     * 保存检测版本时间
     *
     * @param value
     * @return
     */
    public static boolean setUpdateCheckTime(Long value) {
        return SPUtils.setLong(context, getCompanyId() + "_" + getEmployeeId(), AppConst.CHECK_UPDATE_TIME, value);
    }

    /**
     * 获取检测版本时间
     *
     * @return
     */
    public static Long getUpdateCheckTime() {
        return SPUtils.getLong(context, getCompanyId() + "_" + getEmployeeId(), AppConst.CHECK_UPDATE_TIME);
    }


    /**
     * 得到用户密码
     */
    public static String getUserPassword() {
        return SPUtils.getString(context, AppConst.USER_PASSCODE);
    }

    /**
     * 保存用户密码
     *
     * @param value : 用户密码
     * @return 是否成功
     */
    public static boolean setUserPassword(String value) {
        return SPUtils.setString(context, AppConst.USER_PASSCODE, value);
    }

    /**
     * 得到用户ID
     */
    public static String getUserId() {
        return SPUtils.getString(context, AppConst.USER_ID);
    }

    /**
     * 保存用户ID
     *
     * @param value : 用户ID
     * @return 是否成功
     */
    public static boolean setUserId(String value) {
        return SPUtils.setString(context, AppConst.USER_ID, value);
    }

    /**
     * 保存聊天socket uri
     *
     * @param value
     * @return
     */
    public static boolean setImSocketUri(String value) {
        return SPUtils.setString(context, AppConst.SOCKET_URI, value);
    }

    /**
     * 获取Socket uri
     *
     * @return
     */
    public static String getImSocketUri() {
        return SPUtils.getString(context, AppConst.SOCKET_URI);
    }

    /**
     * 得到用户名称
     */
    public static String getUserName() {
        return SPUtils.getString(context, AppConst.USER_NAME);
    }

    /**
     * 保存用户名称
     *
     * @param value : 用户名称
     * @return 是否成功
     */
    public static boolean setUserName(String value) {
        return SPUtils.setString(context, AppConst.USER_NAME, value);
    }

    /**
     * 得到用户头像
     */
    public static String getUserAvatar() {
        return SPUtils.getString(context, AppConst.USER_AVATAR);
    }

    /**
     * @param value
     * @return
     */
    public static boolean setRole(String value) {
        return SPUtils.setString(context, AppConst.ROLE_TYPE, value);
    }

    /**
     * 得到用户头像
     */
    public static String getRole() {
        return SPUtils.getString(context, AppConst.ROLE_TYPE);
    }

    /**
     * 保存用户头像
     *
     * @param value : 用户头像
     * @return 是否成功
     */
    public static boolean setUserAvatar(String value) {
        return SPUtils.setString(context, AppConst.USER_AVATAR, value);
    }

    /**
     * 得到公司ID
     */
    public static String getCompanyId() {
        return SPUtils.getString(context, AppConst.COMPANY_ID);
    }

    /**
     * 保存公司ID
     *
     * @return 是否成功
     */
    public static boolean setCompanyId(String value) {
        return SPUtils.setString(context, AppConst.COMPANY_ID, value);
    }


    /**
     * 得到部门ID
     */
    public static String getDepartmentId() {
        return SPUtils.getString(context, AppConst.DEPARTMENT_ID);
    }

    /**
     * 保存部门ID
     *
     * @return 是否成功
     */
    public static boolean setDepartmentId(String value) {
        return SPUtils.setString(context, AppConst.DEPARTMENT_ID, value);
    }

    /**
     * 得到员工ID
     */
    public static String getEmployeeId() {
        return SPUtils.getString(context, AppConst.EMPLOYEE_ID);
    }

    /**
     * 保存员工ID
     *
     * @param value : 员工ID
     * @return 是否成功
     */
    public static boolean setEmployeeId(String value) {
        return SPUtils.setString(context, AppConst.EMPLOYEE_ID, value);
    }

    /**
     * 得到token
     */
    public static String getToken() {
        return SPUtils.getString(context, AppConst.TOKEN_KEY);
    }

    /**
     * 保存token
     *
     * @param value : token
     * @return 是否成功
     */
    public static boolean setToken(String value) {
        return SPUtils.setString(context, AppConst.TOKEN_KEY, value);
    }


    /**
     * 得到当前公司名称
     */
    public static String getCompanyName() {
        return SPUtils.getString(context, AppConst.LATELY_COMPANY_NAME);
    }

    /**
     * 设置当前公司名称
     *
     * @param value : 公司名称
     * @return 是否成功
     */
    public static boolean setCompanyName(String value) {
        return SPUtils.setString(context, AppConst.LATELY_COMPANY_NAME, value);
    }

    /**
     * 得到当前公司名称
     */
    public static String getCompanyAddress() {
        return SPUtils.getString(context, AppConst.LATELY_COMPANY_ADDRESS);
    }

    /**
     * 设置当前公司名称
     *
     * @param value : 公司名称
     * @return 是否成功
     */
    public static boolean setCompanyAddress(String value) {
        return SPUtils.setString(context, AppConst.LATELY_COMPANY_ADDRESS, value);
    }

    /**
     * 得到当前设备IMEI
     */
    public static String getImei() {
        return SPUtils.getString(context, AppConst.IMEI);
    }

    /**
     * 设置当前设备IMEI
     *
     * @param value : 当前设备IMEI
     * @return 是否成功
     */
    public static boolean setImei(String value) {
        return SPUtils.setString(context, AppConst.IMEI, value);
    }

    /**
     * 保存工作台缓存
     *
     * @param value
     * @return
     */
    public static boolean setWorkbenchData(int type, String value) {
        String key = "";
        switch (type) {
            case 1:
                key = AppConst.WORKBENCH_DATA1;
                break;
            case 2:
                key = AppConst.WORKBENCH_DATA2;

                break;
            case 3:
                key = AppConst.WORKBENCH_DATA3;

                break;
            case 4:
                key = AppConst.WORKBENCH_DATA4;

                break;
        }
        return CacheDataHelper.saveCacheData(CacheDataHelper.WORKBENCH_CACHE_DATA, key, value);
        //  return SPUtils.setString(context, getCompanyId() + "_" + getEmployeeId(), key, value);
    }

    /**
     * 获取工作台缓存
     *
     * @return
     */
    public static List<TaskInfoBean> getWorkbenchData(int type) {
        String key = "";
        switch (type) {
            case 1:
                key = AppConst.WORKBENCH_DATA1;
                break;
            case 2:
                key = AppConst.WORKBENCH_DATA2;

                break;
            case 3:
                key = AppConst.WORKBENCH_DATA3;

                break;
            case 4:
                key = AppConst.WORKBENCH_DATA4;

                break;
        }
        // String str = SPUtils.getString(context, getCompanyId() + "_" + getEmployeeId(), key);
        String str = CacheDataHelper.getCacheData(CacheDataHelper.WORKBENCH_CACHE_DATA, key);
        if (null == str) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(str, new TypeToken<ArrayList<TaskInfoBean>>() {
        }.getType());
    }

    /**
     * 缓存邮件
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean setEmailCacheData(String key, String value) {
        return CacheDataHelper.saveCacheData(CacheDataHelper.EMAIL_CACHE_DATA, key, value);
        // return SPUtils.setString(context, EmailConstant.BEAN_NAME + getCompanyId() + "_" + getEmployeeId(), type, value);
        //  CacheDataHelper.getCacheData(CacheDataHelper.EMAIL_CACHE_DATA,)
    }

    /**
     * 获取邮件缓存
     *
     * @return
     */
    public static List<EmailBean> getEmailCacheData(String type) {

        //String str = SPUtils.getString(context, EmailConstant.BEAN_NAME + getCompanyId() + "_" + getEmployeeId(), type);
        String str = CacheDataHelper.getCacheData(CacheDataHelper.EMAIL_CACHE_DATA, type);
        Log.e("getEmailCacheData:","str:"+str);
        if (null == str) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(str, new TypeToken<ArrayList<EmailBean>>() {
        }.getType());
    }

    /**
     * 得到当前部门名称
     */
    public static String getDepartmentName() {
        return SPUtils.getString(context, AppConst.MAIN_DEPARTMENT_NAME);
    }

    /**
     * 设置当前部门名称
     *
     * @return 是否成功
     */
    public static boolean setDepartmentName(String value) {
        return SPUtils.setString(context, AppConst.MAIN_DEPARTMENT_NAME, value);
    }

    /**
     * 是否已经登录
     */
    public static boolean isLoginBefore() {
        return SPUtils.getBoolean(context, AppConst.IS_LOGIN_BEFORE);
    }

    /**
     * 设置是否登录
     *
     * @param value : 是否登录
     */
    public static boolean setLoginBefore(boolean value) {
        return SPUtils.setBoolean(context, AppConst.IS_LOGIN_BEFORE, value);
    }

    /**
     * 是否已经登录
     */
    public static boolean isCacheBefore() {
        return SPUtils.getBoolean(context, AppConst.IS_CHARTS_CACHE_BEFORE);
    }

    /**
     * 设置是否登录
     *
     * @param value : 是否登录
     */
    public static boolean setCacheBefore(boolean value) {
        return SPUtils.setBoolean(context, AppConst.IS_CHARTS_CACHE_BEFORE, value);
    }


    /**
     * 得到程序崩溃时间
     */
    public static long getCrashTime() {
        return SPUtils.getLong(context, AppConst.CRASH_TIME);
    }

    /**
     * 设置程序崩溃时间
     *
     * @param time : 崩溃时间
     */
    public static boolean setCrashTime(long time) {
        return SPUtils.setLong(context, AppConst.CRASH_TIME, time);
    }

    /**
     * 非WiFi网络时文件大小限制
     *
     * @return
     */
    public static boolean getSizeLimitState() {
        return getSizeLimitState(false);

    }

    /**
     * 非WiFi网络时文件大小限制
     *
     * @return
     */
    public static boolean getSizeLimitState(boolean def) {
        return SPUtils.getBoolean(context, getContactsName(), AppConst.FILE_SIZE_LIMIT, def);

    }

    /**
     * 保存文件大小限制状态
     *
     * @param b
     * @return
     */
    public static boolean setSizeLimitState(boolean b) {
        return SPUtils.setBoolean(context, getContactsName(), AppConst.FILE_SIZE_LIMIT, b);

    }

    /**
     * 权限
     *
     * @param b
     * @return
     */
    public static boolean setRequestPermisionBefore(boolean b) {

        return SPUtils.setBoolean(context, getContactsName(), AppConst.REQUEST_PERMISION_BEFORE, b);

    }

    /**
     * 权限
     *
     * @param def
     * @return
     */
    public static boolean getRequestPermisionBefore(boolean def) {
        return SPUtils.getBoolean(context, getContactsName(), AppConst.REQUEST_PERMISION_BEFORE, def);

    }
}

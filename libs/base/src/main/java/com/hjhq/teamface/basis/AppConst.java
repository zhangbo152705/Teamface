package com.hjhq.teamface.basis;

/**
 * APP全局常量类
 * Created by Administrator on 2018/2/5.
 */

public class AppConst {
    /**
     * 0：其他1：Android客户端2：IOS客户端3:Windows客户端4：Mac客户端
     */
    public static final String CLIENT_FLAG = "1";
    /**
     * 功能权限
     */
    public final static String AUTH = "auth";
    public static final String USER_NAME = "user_name";
    public static final String ROLE_TYPE = "role_type";
    public static final String USER_AVATAR = "user_avatar";
    public static final String USER_PASSCODE = "user_passcode";
    /**
     * 是否登录
     */
    public static final String IS_LOGIN_BEFORE = "is_login_before";
    public static final String IS_CHARTS_CACHE_BEFORE = "is_charts_cache_before";
    /**
     * 当前公司名称
     */
    public static final String LATELY_COMPANY_NAME = "lately_company_name";
    public static final String LATELY_COMPANY_ADDRESS = "lately_company_address";
    public static final String IMEI = "imei";
    /**
     * 工作台在sp缓存的key值
     */
    public static final String WORKBENCH_DATA1 = "workbench_data1";
    public static final String WORKBENCH_DATA2 = "workbench_data2";
    public static final String WORKBENCH_DATA3 = "workbench_data3";
    public static final String WORKBENCH_DATA4 = "workbench_data4";
    /**
     * 同事圈缓存key
     */
    public static final String CCCHAT = "ccchat";
    /**
     * 数据分析k缓存ey
     */
    public static final String CHART_CACHE = "chart_cache";
    /**
     * 同事圈背景缓存
     */
    public static final String CCCHAT_BG = "ccchat_bg";
    public static final String USER_INFO = "user_info";
    public static final String USER_ACCOUNT = "user_account";
    public static final String DOMAIN = "domain";
    public static final String LOGINGSETTING_DOMAIN = "loginsetting_domain";
    public static final String SOCKET_URL = "socket_url";
    public static final String IS_REMENBER_SOCKET_URL = "is_remenber_socket_url";
    public static final String HISTORY_SOCKET_URL = "history_socket_url";
    public static final String MAIN_DEPARTMENT_NAME = "main_company_name";
    public static final String CHECK_UPDATE_TIME = "check_update_time";
    public static final String ATTENDANCE_PLUG = "ATTENDANCE_PLUG";
    /**
     * 最近联系人
     */
    public final static String RECENTLY_CONTACTS = "recently_contacts";

    public static final String TOKEN_KEY = "token_key";
    public final static String USER_ID = "user_id";
    /**
     * 从服务器获取的企信uri
     */
    public final static String SOCKET_URI = "socket_uri";
    /**
     * 员工ID
     */
    public final static String EMPLOYEE_ID = "employee_id";
    /**
     * 公司ID
     */
    public final static String COMPANY_ID = "company_id";
    /**
     * 部门ID
     */
    public final static String DEPARTMENT_ID = "department_id";
    /**
     * 崩溃时间
     */
    public final static String CRASH_TIME = "crashTime";

    /**
     * 非WiFi网络文件大小限制
     */
    public final static String FILE_SIZE_LIMIT = "file_size_limit";
    /**
     * 权限
     */
    public final static String REQUEST_PERMISION_BEFORE = "request_permision_before";


    public static final String LOCATION = "location";
    /**
     * 快速新增
     */
    public static final String QUICKLY_ADD = "quickly_add";

    /**
     * 路由statistic,custom,memo,project,login,attendance,email,filelib,im
     */
    public static final String APP_ROUTER_ROOT = "DDComp://";

    public static final String MODULE_APP = APP_ROUTER_ROOT + "app/";
    public static final String MODULE_ATTENDANCE = APP_ROUTER_ROOT + "attendance/";
    public static final String MODULE_COMMON = APP_ROUTER_ROOT + "app/";
    public static final String MODULE_CUSTOM = APP_ROUTER_ROOT + "custom/";
    public static final String MODULE_EMAIL = APP_ROUTER_ROOT + "email/";
    public static final String MODULE_FILELIB = APP_ROUTER_ROOT + "filelib/";
    public static final String MODULE_IM = APP_ROUTER_ROOT + "im/";
    public static final String MODULE_LOGIN = APP_ROUTER_ROOT + "login/";
    public static final String MODULE_MEMO = APP_ROUTER_ROOT + "memo/";
    public static final String MODULE_PROJECT = APP_ROUTER_ROOT + "project/";
    public static final String MODULE_STATISTIC = APP_ROUTER_ROOT + "statistic/";

//////////////////////////////////////app start//////////////////////////////////


    /////////////////////////////////////////app end//////////////////////////////////
//////////////////////////////////////custom start//////////////////////////////////
    /**
     * 查重
     */
    public static final String MODULE_CUSTOM_REPART_CHECK = MODULE_CUSTOM + "repeatCheck";
    /**
     * 新增
     */
    public static final String MODULE_CUSTOM_ADD = MODULE_CUSTOM + "add";
    /**
     * 编辑
     */
    public static final String MODULE_CUSTOM_EDIT = MODULE_CUSTOM + "edit";
    /**
     * 详情
     */
    public static final String MODULE_CUSTOM_DETAIL = MODULE_CUSTOM + "detail";
    /**
     * 文件
     */
    public static final String MODULE_CUSTOM_FILE = MODULE_CUSTOM + "file";
    /**
     * 选择
     */
    public static final String MODULE_CUSTOM_SELECT = MODULE_CUSTOM + "select";
    /**
     * 列表
     */
    public static final String MODULE_CUSTOM_LIST = MODULE_CUSTOM + "temp";
    /**
     * 关联列表
     */
    public static final String MODULE_CUSTOM_REFERENCE = MODULE_CUSTOM + "referenceTemp";

/////////////////////////////////////////custom end//////////////////////////////////
}

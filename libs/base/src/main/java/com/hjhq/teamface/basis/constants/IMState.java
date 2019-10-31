package com.hjhq.teamface.basis.constants;

/**
 * @author Administrator
 * @date 2017/11/23
 * Describe：
 */

public class IMState {

    private static boolean IM_ONLINE_STATE = false;
    private static boolean IM_CAN_LOGIN = false;
    private static boolean IM_CONNECT_LL_OK = false;
    private static boolean IM_IS_FIRST_TIME_USE = true;
    private static boolean IS_WIFI_OK = false;
    private static boolean IS_MOBILE_OK = false;
    private static String WIFI_SSID = "";
    private static String WIFI_MAC = "";
    public static Long LOGIN_TIME = -1L;

    public static boolean isWifiOk() {
        return IS_WIFI_OK;
    }

    public static void setIsWifiOk(boolean isWifiOk) {
        IS_WIFI_OK = isWifiOk;
    }

    public static boolean isMobileOk() {
        return IS_MOBILE_OK;
    }

    public static void setIsMobileOk(boolean isMobileOk) {
        IS_MOBILE_OK = isMobileOk;
    }

    public static String getWifiSsid() {
        return WIFI_SSID;
    }

    public static void setWifiSsid(String wifiSsid) {
        WIFI_SSID = wifiSsid;
    }

    public static String getWifiMac() {
        return WIFI_MAC;
    }

    public static void setWifiMac(String wifiMac) {
        WIFI_MAC = wifiMac;
    }

    public static boolean isImIsFirstTimeUse() {
        return IM_IS_FIRST_TIME_USE;
    }

    public static void setImIsFirstTimeUse(boolean imIsFirstTimeUse) {
        IM_IS_FIRST_TIME_USE = imIsFirstTimeUse;
    }

    public static boolean getImOnlineState() {
        return IM_ONLINE_STATE;
    }

    public static void setImOnlineState(boolean imOnlineState) {
        IM_ONLINE_STATE = imOnlineState;
    }

    public static boolean isImCanLogin() {
        return IM_CAN_LOGIN;
    }

    public static void setImCanLogin(boolean imCanLogin) {
        IM_CAN_LOGIN = imCanLogin;
    }

    public static boolean isImConnectLlOk() {
        return IM_CONNECT_LL_OK;
    }

    public static void setImConnectLlOk(boolean imConnectLlOk) {
        IM_CONNECT_LL_OK = imConnectLlOk;
    }
}

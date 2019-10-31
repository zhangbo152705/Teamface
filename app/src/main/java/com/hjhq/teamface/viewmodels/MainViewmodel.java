package com.hjhq.teamface.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.api.BaseService;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.PlugBean;
import com.hjhq.teamface.basis.bean.PlugListBean;
import com.hjhq.teamface.basis.bean.attendancebean.BaseAttendanceDataItemBean;
import com.hjhq.teamface.basis.bean.attendancebean.BaseAttendanceInfoBean;
import com.hjhq.teamface.basis.bean.attendancebean.BaseAttendanceTypeListBean;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.network.ApiManager;
import com.hjhq.teamface.basis.network.exception.HttpResultFunc;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.LocationUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.oa.main.MainActivity;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by zzh 2019/4/22.
 * MainActivity ViewModel 主要处理:1.考勤快速打卡
 */

public class MainViewmodel extends ViewModel implements AMapLocationListener {
    long time1;
    long time2;
    long time3;
    long time4;
    long time5;
    long time6;

    long time1Limit;
    long time2Limit;
    long time3Limit;
    long time4Limit;
    long time5Limit;
    long time6Limit;
    long currentTime;
    String classType ="";
    String attendaceType;
    String currentWifiName;
    String currentMacAddress;
    String lat;
    String lng;
    String address;
    long lastLocatingTime;
    long groupId;

    int nearAddressIndex = 0;
    double nearAddressDistance = Double.MAX_VALUE;
    String currentTimeField;

    private int punchcardType = 1;//	是	int	打卡类型1:上班卡，2:下班卡
    private int punchcardWay = 0;//	是	int	打卡方式(0 地址 1 wifi)

    BaseAttendanceInfoBean.DataBean.ClassInfoBean classInfo;
    List<BaseAttendanceDataItemBean> clock_record_list;
    BaseAttendanceInfoBean mAttendanceInfoBean;
    //声明mlocationClient对象
    private AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    private MainActivity mActivity;
    Calendar mCalendarToday;

    private boolean hasAttendence = true;
    private RelativeLayout rootView;


    public void setContext(MainActivity mActivity){
        this.mActivity = mActivity;
    }
    /**
     * 开始考勤
     */
    public void startAttendance(RelativeLayout mRootView){
        this.rootView = mRootView;
        if (mLocationClient == null){
            initLocationSetting();
        }
        location();

        mCalendarToday = Calendar.getInstance();
        mCalendarToday.set(Calendar.HOUR_OF_DAY, 0);
        mCalendarToday.set(Calendar.MINUTE, 0);
        mCalendarToday.set(Calendar.SECOND, 0);
        mCalendarToday.set(Calendar.MILLISECOND, 0);
        getPlugList(false);
    }

    private void initLocationSetting() {
        mLocationClient = new AMapLocationClient(mActivity);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(this);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }

    private void location() {
        if (System.currentTimeMillis() - lastLocatingTime > 3000) {
            mLocationClient.startLocation();
            lastLocatingTime = System.currentTimeMillis();
            lat = null;
            lng = null;
            address  = null;
        }

    }

    /**
     * 获取是否自动打卡
     */
    private void getPlugList(boolean flag) {
        getAttendancePlugList(mActivity, new ProgressSubscriber<PlugListBean>(mActivity, flag) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(PlugListBean plugListBean) {
                super.onNext(plugListBean);
                 if (plugListBean != null && plugListBean.getData() != null){
                     handlePlugList(plugListBean.getData().getDataList());
                 }
            }
        });


    }

    public void handlePlugList(List<PlugBean> dataList){
        if (!CollectionUtils.isEmpty(dataList)){
            for(PlugBean item : dataList){
                if (item.getPlugin_type().equals("1") && item.getPlugin_status().equals("1")){
                    getCurrentDayAttendanceInfo(mCalendarToday.getTimeInMillis());
                    break;
                }
            }
        }

    }

    /**
     * 获取考勤信息
     */
    public void getCurrentDayAttendanceInfo(long date){
        hasAttendence =true;
        getAttendanceInfo(mActivity,date, new Subscriber<BaseAttendanceInfoBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(BaseAttendanceInfoBean attendanceInfoBean) {
                Log.e("getCurrentDayInfo","attendanceInfoBean:"+attendanceInfoBean.toString());
                mAttendanceInfoBean = attendanceInfoBean;
                checkIsAttendance(mAttendanceInfoBean);
            }
        });

    }
    public void showAttendanceSucess(){
        rootView.post(new Runnable() {
            @Override
            public void run() {
                String time = DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                PopUtils.showAttendanceSucessPopWindow(mActivity,time,rootView);
            }
        });
    }
    /**
     * 打卡
     * @param map
     */
    private void realPunch(Map<String, Serializable> map) {
        punchClock(mActivity, map, new Subscriber<BaseBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("realPunch:","onError:"+e.toString());
            }

            @Override
            public void onNext(BaseBean baseBean) {
               Log.e("realPunch:","onNext");
                showAttendanceSucess();
            }
        });
    }

    /**
     * 是否打卡
     */
    public void checkIsAttendance(BaseAttendanceInfoBean attendanceInfoBean){
        if (!hasAttendence){
            return;
        }
        boolean isAttendance = false;
         currentTime = System.currentTimeMillis();

        if (attendanceInfoBean != null && attendanceInfoBean.getData() != null && attendanceInfoBean.getData().getClass_info() != null && attendanceInfoBean.getData().getClock_record_list() != null){
            attendaceType =attendanceInfoBean.getData().getAttendance_type();
            classInfo = attendanceInfoBean.getData().getClass_info();
            clock_record_list = attendanceInfoBean.getData().getClock_record_list();
            classType = classInfo.getClass_type();
            groupId = TextUtil.parseLong(attendanceInfoBean.getData().getId(), 0L);
            if(attendaceType.equals("0") || attendaceType.equals("1")){
                switch (classType) {
                    case "1":
                        time1(currentTime, classInfo);
                        //上班
                        if (time1 == time1Limit){
                            if (currentTime<time1 && clock_record_list.size() ==0){
                                isAttendance =true;
                                punchcardType = 1;
                                currentTimeField = "time1_start";
                            }
                        }else{
                            if (currentTime >time1Limit && currentTime<time1 && clock_record_list.size() ==0){
                                isAttendance =true;
                                punchcardType = 1;
                                currentTimeField = "time1_start";
                            }
                        }

                        //下班
                        if (time2 == time2Limit){
                            if (currentTime>time2 && clock_record_list.size() ==1){
                                isAttendance =true;
                                punchcardType = 2;
                                currentTimeField = "time1_end";
                            }
                        }else{
                            if (currentTime >time2&& currentTime<time2Limit && clock_record_list.size() ==1){
                                isAttendance =true;
                                punchcardType = 2;
                                currentTimeField = "time1_end";
                            }
                        }

                        break;
                    case "2":
                        time1(currentTime, classInfo);
                        time2(currentTime, classInfo);
                        //上班
                        if (time1 == time1Limit){
                            if (currentTime<time1 && clock_record_list.size() ==0){
                                isAttendance =true;
                                punchcardType = 1;
                                currentTimeField = "time1_start";
                            }
                        }else{
                            if (currentTime >time1Limit && currentTime<time1 && clock_record_list.size() ==0){
                                isAttendance =true;
                                punchcardType = 1;
                                currentTimeField = "time1_start";
                            }
                        }
                        //下班
                        if (time2 == time2Limit){
                            if (currentTime>time2 &&  currentTime<time3 && clock_record_list.size() ==1){
                                isAttendance =true;
                                punchcardType = 2;
                                currentTimeField = "time1_end";
                            }
                        }else{
                            if (currentTime >time2 && currentTime<time2Limit &&  currentTime<time3 && clock_record_list.size() ==1){
                                isAttendance =true;
                                punchcardType = 2;
                                currentTimeField = "time1_end";
                            }
                        }

                        //2上班
                        if (time3 == time3Limit){
                            if (currentTime>time2 &&  currentTime<time3 && clock_record_list.size() ==2){
                                isAttendance =true;
                                punchcardType = 1;
                                currentTimeField = "time2_start";

                            }
                        }else{
                            if (currentTime >time2 && currentTime>time3Limit &&  currentTime<time3 && clock_record_list.size() ==2){
                                isAttendance =true;
                                punchcardType = 1;
                                currentTimeField = "time2_start";
                            }
                        }

                        //2下班
                        if (time4 == time4Limit){
                            if (currentTime>time4 && clock_record_list.size() ==3){
                                isAttendance =true;
                                punchcardType = 2;
                                currentTimeField = "time2_end";
                            }
                        }else{
                            if (currentTime >time4 && currentTime<time4Limit &&  clock_record_list.size() ==3){
                                isAttendance =true;
                                punchcardType = 2;
                                currentTimeField = "time2_end";
                            }
                        }
                        break;
                    case "3":
                        time1(currentTime, classInfo);
                        time2(currentTime, classInfo);
                        time3(currentTime, classInfo);
                        //上班
                        if (time1 == time1Limit){
                            if (currentTime<time1 && clock_record_list.size() ==0){
                                isAttendance =true;
                                currentTimeField = "time1_start";
                                punchcardType = 1;
                            }
                        }else{
                            if (currentTime >time1Limit && currentTime<time1 && clock_record_list.size() ==0){
                                isAttendance =true;
                                currentTimeField = "time1_start";
                                punchcardType = 1;
                            }
                        }
                        //下班
                        if (time2 == time2Limit){
                            if (currentTime>time2 &&  currentTime<time3 && clock_record_list.size() ==1){
                                isAttendance =true;
                                currentTimeField = "time1_end";
                                punchcardType = 2;

                            }
                        }else{
                            if (currentTime >time2 && currentTime<time2Limit &&  currentTime<time3 && clock_record_list.size() ==1){
                                isAttendance =true;
                                currentTimeField = "time1_end";
                                punchcardType = 2;
                            }
                        }

                        //2上班
                        if (time3 == time3Limit){
                            if (currentTime>time2 &&  currentTime<time3 && clock_record_list.size() ==2){
                                isAttendance =true;
                                currentTimeField = "time2_start";
                                punchcardType = 1;
                            }
                        }else{
                            if (currentTime >time2 && currentTime>time3Limit &&  currentTime<time3 && clock_record_list.size() ==2){
                                isAttendance =true;
                                currentTimeField = "time2_start";
                                punchcardType = 1;
                            }
                        }

                        //2下班
                        if (time4 == time4Limit){
                            if (currentTime>time4 && currentTime < time5 && clock_record_list.size() ==3){
                                isAttendance =true;
                                currentTimeField = "time2_end";
                                punchcardType = 2;
                            }
                        }else{
                            if (currentTime >time4 && currentTime<time4Limit &&  currentTime < time5 && clock_record_list.size() ==3){
                                isAttendance =true;
                                currentTimeField = "time2_end";
                                punchcardType = 2;
                            }
                        }

                        //3上班
                        if (time5 == time5Limit){
                            if (currentTime>time4 &&  currentTime<time5 && clock_record_list.size() ==4){
                                isAttendance =true;
                                currentTimeField = "time3_start";
                                punchcardType = 1;
                            }
                        }else{
                            if (currentTime >time4 && currentTime>time5Limit &&  currentTime<time5 && clock_record_list.size() ==4){
                                isAttendance =true;
                                currentTimeField = "time3_start";
                                punchcardType = 1;
                            }
                        }

                        //3下班
                        if (time6 == time6Limit){
                            if (currentTime>time6 &&  clock_record_list.size() ==5){
                                isAttendance =true;
                                currentTimeField = "time3_end";
                                punchcardType = 2;
                            }
                        }else{
                            if (currentTime >time6 &&   currentTime<time6Limit && clock_record_list.size() ==5){
                                isAttendance =true;
                                currentTimeField = "time3_end";
                                punchcardType = 2;
                            }
                        }

                        break;
                }
                if(isAttendance){
                    Log.e("checkIsAttendance","checkIsAttendance");
                    if (checkWifi(attendanceInfoBean) || isCheckLocation(attendanceInfoBean)){
                        Log.e("checkIsAttendance","checkWifi:"+punchcardWay);
                        hasAttendence =false;
                        Map<String, Serializable> map = new HashMap<>();
                        map.put("punchcardTimeField", currentTimeField);
                        map.put("attendanceDate", mCalendarToday.getTimeInMillis());
                        map.put("groupId", groupId);
                        map.put("punchcardType", punchcardType);
                        map.put("punchcardWay", punchcardWay);
                        map.put("punchcardEquipment", Build.BRAND + Build.DEVICE);
                        map.put("punchcardAddress", punchcardWay == 0 ? address : currentWifiName);
                        map.put("photo", "");
                        map.put("remark", "");
                        map.put("isOutworker",  1);
                        realPunch(map);

                    }
                }
            }

        }
    }

    /**
     * 检测地址
     * @param attendanceInfoBean
     * @return
     */
    public boolean isCheckLocation(BaseAttendanceInfoBean attendanceInfoBean){
        boolean isLocation = false;
        List<BaseAttendanceTypeListBean> attendance_address = attendanceInfoBean.getData().getAttendance_address();
        if (attendance_address != null && lat != null && lng != null)
        for (int i = 0; i < attendance_address.size(); i++) {
            BaseAttendanceTypeListBean attendanceTypeListBean = attendance_address.get(i);
            long effective_range = TextUtil.parseLong(attendanceTypeListBean.getEffective_range());
            List<BaseAttendanceTypeListBean.LocationBean> location = attendanceTypeListBean.getLocation();
            if (location != null && location.size() > 0) {
                BaseAttendanceTypeListBean.LocationBean locationBean = location.get(0);
                double d1, d2, d3, d4;
                d1 = TextUtil.parseDouble(lng);
                d2 = TextUtil.parseDouble(lat);
                d3 = TextUtil.parseDouble(locationBean.getLng());
                d4 = TextUtil.parseDouble(locationBean.getLat());
                if (Math.abs(LocationUtils.getDistance(d1, d2, d3, d4)) < nearAddressDistance) {
                    nearAddressDistance = Math.abs(LocationUtils.getDistance(d1, d2, d3, d4));
                    nearAddressIndex = i;
                }
                if (Math.abs(LocationUtils.getDistance(d1, d2, d3, d4)) < effective_range) {
                    isLocation =true;
                    punchcardWay = 0;
                    break;
                }
            }
        }
    return isLocation;

    }

    /**
     * 检测是否为打卡wifi
     */
    public boolean checkWifi(BaseAttendanceInfoBean attendanceInfoBean){
        List<BaseAttendanceTypeListBean> wifiList = attendanceInfoBean.getData().getAttendance_wifi();
        boolean isWifi = false;
        if(wifiList != null && wifiList.size()>0){
            WifiManager wm = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                WifiInfo winfo = wm.getConnectionInfo();
                if (winfo != null) {
                    currentWifiName = winfo.getSSID();
                    currentMacAddress = winfo.getBSSID();
                    if (currentMacAddress != null){
                        for (int i = 0; i < wifiList.size(); i++) {
                            if (currentMacAddress.toLowerCase().equals(wifiList.get(i).getAddress().toLowerCase())) {
                                isWifi = true;
                                punchcardWay = 1;
                                break;
                            }
                        }
                    }
                }
            }

        }
        return  isWifi;
    }


    private void time3(long currentTime, BaseAttendanceInfoBean.DataBean.ClassInfoBean class_info) {
        time5 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime3_start(), "yyyy-MM-dd HH:mm");
        time6 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime3_end(), "yyyy-MM-dd HH:mm");
        if (time5 <= time4) {
            time5 = time5 + 24 * 60 * 60 * 1000;
        }
        if (time6 <= time5) {
            time6 = time6 + 24 * 60 * 60 * 1000;
        }
        time5Limit = time3 - TextUtil.parseInt(class_info.getTime3_start_limit(), 0) * 60 * 1000;
        time6Limit = time4 + TextUtil.parseInt(class_info.getTime3_end_limit(), 0) * 60 * 1000;
    }

    private void time2(long currentTime, BaseAttendanceInfoBean.DataBean.ClassInfoBean class_info) {
        time3 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime2_start(), "yyyy-MM-dd HH:mm");
        time4 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime2_end(), "yyyy-MM-dd HH:mm");
        if (time3 <= time2) {
            time3 = time3 + 24 * 60 * 60 * 1000;
        }
        if (time4 <= time3) {
            time4 = time4 + 24 * 60 * 60 * 1000;
        }
        time3Limit = time3 - TextUtil.parseInt(class_info.getTime2_start_limit(), 0) * 60 * 1000;
        time4Limit = time4 + TextUtil.parseInt(class_info.getTime2_end_limit(), 0) * 60 * 1000;
    }

    private void time1(long currentTime, BaseAttendanceInfoBean.DataBean.ClassInfoBean class_info) {
        time1 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime1_start(), "yyyy-MM-dd HH:mm");
        time2 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime1_end(), "yyyy-MM-dd HH:mm");

        if (time2 <= time1) {
            time2 = time2 + 24 * 60 * 60 * 1000;
        }
        time1Limit = time1 - TextUtil.parseInt(class_info.getTime1_start_limit(), 0) * 60 * 1000;
        time2Limit = time2 + TextUtil.parseInt(class_info.getTime1_end_limit(), 0) * 60 * 1000;
    }

    public void getAttendanceInfo(BaseActivity activity, Long date, Subscriber<BaseAttendanceInfoBean> s) {
        getApi().getBaseAttendanceInfo(date)
                .map(new HttpResultFunc<>())
                .compose(activity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(s);
    }

    public void punchClock(BaseActivity activity,Map<String, Serializable> date, Subscriber<BaseBean> s) {
        getApi().punchClock(date)
                .map(new HttpResultFunc<>())
                .compose(activity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(s);
    }

    public void getAttendancePlugList(BaseActivity mActivity, Subscriber<PlugListBean> s) {
        getApi().getAttendancePlugList().map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public BaseService getApi() {
        return  new ApiManager<BaseService>().getAPI(BaseService.class);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                lat = aMapLocation.getLatitude() + "";
                lng = aMapLocation.getLongitude() + "";
                address = aMapLocation.getAddress();
                checkIsAttendance(mAttendanceInfoBean);
                Log.e("onLocationChanged:","lat:"+lat+",lng:"+lng+",address:"+address);
            } else {

            }
            mLocationClient.stopLocation();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mLocationClient != null){
            mLocationClient.onDestroy();
        }

    }
}

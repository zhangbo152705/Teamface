package com.hjhq.teamface.attendance.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.RelationModuleAdapter;
import com.hjhq.teamface.attendance.adapter.TodayAttendanceAdapter;
import com.hjhq.teamface.attendance.bean.AttendanceDataItemBean;
import com.hjhq.teamface.attendance.bean.AttendanceInfoBean;
import com.hjhq.teamface.attendance.bean.AttendanceTypeListBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.utils.NotifyUtils;
import com.hjhq.teamface.attendance.views.AttendanceFragmentDelegate;
import com.hjhq.teamface.basis.bean.AppriveInfo;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.LocationUtils;
import com.hjhq.teamface.basis.util.NetWorkUtils;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.ui.location.LocationPresenterV2;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.EmptyView;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AttendanceFragment extends FragmentPresenter<AttendanceFragmentDelegate, AttendanceModel>
        implements AMapLocationListener {
    private ScheduledExecutorService scheduledExecutorService;
    private Runnable progressRunnable;
    private TodayAttendanceAdapter mTodayAttendanceAdapter;
    private RelationModuleAdapter mRelationModuleAdapter;
    private List<AttendanceDataItemBean> dataList = new ArrayList<>();
    private List<AttendanceTypeListBean> relationModuleList = new ArrayList<>();
    //声明mlocationClient对象
    private AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;

    //考勤方式优先级：WIFI、地址、外勤打卡
    static AttendanceFragment newInstance(String tag) {
        AttendanceFragment myFragment = new AttendanceFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, tag);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    private boolean needLocation = true;
    private boolean isLocating = false;
    private boolean locationReady = false;
    private boolean infoReady = false;
    private boolean wifiReady = false;
    private boolean attendanceComplete = false;
    private boolean isNormal = true;
    private String currentWifiName = "";
    private String currentMacAddress = "";
    private boolean outworker_status = false;
    private int isOutworker = 0;
    private int punchcardType = 1;//	是	int	打卡类型1:上班卡，2:下班卡
    private int punchcardWay = 0;//	是	int	打卡方式(0 地址 1 wifi)
    private long rightTime;
    private long lastLocatingTime;
    private boolean wifiOK = false;
    private boolean locationOk = false;

    private String lat;
    private String lng;
    private String nearLat;
    private String nearLng;
    private String rangeValue;
    private String address;
    Calendar mCalendar;
    Calendar mCalendarToday;
    Calendar mCalendarCutrrent;
    AttendanceInfoBean.DataBean mDataBean;
    private Map<String, String> nodeMap = new LinkedHashMap<>();
    private boolean hasArrangement = false;
    private EmptyView mEmptyView;
    private String[] typeString = new String[]{"上班打卡", "迟到打卡", "下班打卡", "早退打卡"};
    private int currentType = 4;
    private int lastCurrentType =4;
    private String currentTypeString = "上班打卡";
    private String currentTimeField = "";
    private boolean forground = false;
    private String title;
    private String content;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocationSetting();
        location();
        wifi();
        lastLocatingTime = System.currentTimeMillis();

    }

    @Override
    public void onResume() {
        super.onResume();
        forground = true;
        location();
    }

    @Override
    public void onPause() {
        super.onPause();
        forground = false;
    }

    private void wifi() {
        wifiOK = false;
        wifiReady = NetWorkUtils.isWifiConnected(getActivity());
        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            WifiInfo winfo = wm.getConnectionInfo();
            if (winfo != null) {
                currentWifiName = winfo.getSSID();
                currentMacAddress = winfo.getBSSID();
                if (mDataBean != null) {
                    initActionBtn();
                }
            }
        }
    }

    public void getAttendanceInfo(Long date, boolean showLoading) {
        model.getAttendanceInfo(((ActivityPresenter) getActivity()), date,
                new ProgressSubscriber<AttendanceInfoBean>(getActivity(), showLoading) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        viewDelegate.mRefreshLayout.finishRefresh();
                        mEmptyView.setEmptyTitle("数据错误");
                    }

                    @Override
                    public void onNext(AttendanceInfoBean baseBean) {
                        super.onNext(baseBean);
                        viewDelegate.mRefreshLayout.finishRefresh();
                        mDataBean = baseBean.getData();
                        outworker_status = "1".equals(mDataBean.getOutworker_status());
                        showData();
                        showRelationModuleData();
                        initActionBtn();
                        infoReady = true;
                    }
                });
    }

    private void showData() {
        viewDelegate.setAttendanceGroupName(mDataBean.getName());
        dataList.clear();
        dataList.addAll(mDataBean.getClock_record_list());
        mTodayAttendanceAdapter.notifyDataSetChanged();

    }

    private void showRelationModuleData(){
        /*AttendanceTypeListBean remp = new AttendanceTypeListBean();
        remp.setChinese_name("请假");
        remp.setStart_time(System.currentTimeMillis());
        remp.setEnd_time(System.currentTimeMillis());*/
        relationModuleList.clear();
       // relationModuleList.add(remp);
       // relationModuleList.add(remp);
        if (mDataBean.getRelation_module() != null){
            relationModuleList.addAll(mDataBean.getRelation_module());
            mRelationModuleAdapter.notifyDataSetChanged();
        }
    }

    private void getAttendanceList(Long date) {
        model.queryAttendanceRecord(((ActivityPresenter) getActivity()), date, new ProgressSubscriber<BaseBean>(getActivity()) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                initActionBtn();
            }
        });

    }


    private void initLocationSetting() {
        mLocationClient = new AMapLocationClient(getActivity());
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
            isLocating = true;
            lastLocatingTime = System.currentTimeMillis();
            locationOk = false;
        }

    }

    @Override
    protected void init() {
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendarToday = Calendar.getInstance();
        mCalendarCutrrent = Calendar.getInstance();
        mCalendarToday.set(Calendar.HOUR_OF_DAY, 0);
        mCalendarToday.set(Calendar.MINUTE, 0);
        mCalendarToday.set(Calendar.SECOND, 0);
        mCalendarToday.set(Calendar.MILLISECOND, 0);
        viewDelegate.setDate(mCalendarToday.getTimeInMillis());
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        progressRunnable = new MyRunnable();
        scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 1000, TimeUnit.MILLISECONDS);
        refreshData();
        mTodayAttendanceAdapter = new TodayAttendanceAdapter(dataList);
        mRelationModuleAdapter = new RelationModuleAdapter(relationModuleList);
        mEmptyView = new EmptyView(getActivity());
        mEmptyView.setEmptyTitle(" ");
        mTodayAttendanceAdapter.setEmptyView(mEmptyView);
        View footer = getLayoutInflater().inflate(R.layout.attendance_daka_footer, null);
        mTodayAttendanceAdapter.setFooterView(footer);
        viewDelegate.setAdapter(mTodayAttendanceAdapter);
        viewDelegate.setMouudelAdapter(mRelationModuleAdapter);

        viewDelegate.setMouudelItemClick(new SimpleItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                final AttendanceTypeListBean bean = relationModuleList.get(position);
                Bundle bundle = new Bundle();
                    if (!TextUtils.isEmpty(bean.getBean_name()) && !TextUtils.isEmpty(bean.getData_id())) {
                        Map<String, String> map = new HashMap<>();
                        String dataId = bean.getData_id();
                        String moduleBean = bean.getBean_name();
                        map.put("dataId", dataId);
                        map.put("moduleBean", moduleBean);
                        getApprovalDat(map,bundle,bean.getBean_name(),bean.getData_id());
                    }
            }
        });
        viewDelegate.setItemClick(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_update_time) {
                    punchClock();
                } else if (view.getId() == R.id.tv_approve) {
                    final AttendanceDataItemBean bean = dataList.get(position);
                    Bundle bundle = new Bundle();
                    if ("1".equals(bean.getPunchcard_status())) {
                        if (!TextUtils.isEmpty(bean.getBean_name()) && !TextUtils.isEmpty(bean.getData_id())) {
                            Map<String, String> map = new HashMap<>();
                            String dataId = bean.getData_id();
                            String moduleBean = bean.getBean_name();
                            map.put("dataId", dataId);
                            map.put("moduleBean", moduleBean);
                            getApprovalDat(map,bundle,bean.getBean_name(),bean.getData_id());
                        }
                    } else if ("5".equals(bean.getPunchcard_status())) {

                        bundle.putString(Constants.MODULE_BEAN, bean.getBean_name());
                        UIRouter.getInstance().openUri(getActivity(), "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE);
                    }
                } else if (view.getId() == R.id.tv_remark) {
                    AttendanceDataItemBean dataItemBean = dataList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DATA_TAG1, dataItemBean.getReal_punchcard_time());
                    bundle.putString(Constants.DATA_TAG2, dataItemBean.getPunchcard_address());
                    bundle.putString(Constants.DATA_TAG3, dataItemBean.getRemark());
                    bundle.putString(Constants.DATA_TAG4, dataItemBean.getPhoto());
                    CommonUtil.startActivtiy(getActivity(), AttendanceRemarkActivity.class, bundle);
                }
            }
        });
        getAttendanceInfo(getMillis(mCalendarToday), true);
    }

    public void getApprovalDat(Map<String, String> map, Bundle bundle,String beanName,String dataId){
        model.queryApprovalData(((ActivityPresenter) getActivity()), map, new ProgressSubscriber<AppriveInfo>(getActivity()) {
            @Override
            public void onNext(AppriveInfo appriveInfo) {
                super.onNext(appriveInfo);
                bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, appriveInfo.getData().getProcess_definition_id());
                bundle.putString(ApproveConstants.PROCESS_FIELD_V, appriveInfo.getData().getProcess_field_v());
                bundle.putString(ApproveConstants.APPROVAL_DATA_ID, appriveInfo.getData().getApproval_data_id());
                bundle.putString(ApproveConstants.TASK_KEY, appriveInfo.getData().getTask_key());
                bundle.putString(ApproveConstants.TASK_NAME, appriveInfo.getData().getTask_name());
                bundle.putString(ApproveConstants.TASK_ID, appriveInfo.getData().getTask_id());
                bundle.putString(Constants.MODULE_BEAN, beanName);//bean.getBean_name()
                bundle.putString(Constants.DATA_ID, dataId);//bean.getData_id()
                bundle.putInt(ApproveConstants.APPROVE_TYPE, 0);
                UIRouter.getInstance().openUri(getActivity(), "DDComp://app/approve/detail", bundle);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    public void refreshData() {
        location();
        getAttendanceInfo(getMillis(mCalendar), false);
    }

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

    private void initActionBtn() {
        if (mDataBean == null) {
            currentType = 4;
            currentTypeString = "上班打卡";
            return;
        }
        //排班类型
        String mode = mDataBean.getAttendance_type();

        AttendanceInfoBean.DataBean.ClassInfoBean class_info = mDataBean.getClass_info();
        if (class_info == null) {
            wifiOK = true;
            locationOk = true;
            mEmptyView.setEmptyTitle("没有打卡规则,请联系管理员设置");
            dataList.clear();
            dataList.addAll(mDataBean.getClock_record_list());
            mTodayAttendanceAdapter.notifyDataSetChanged();
            if (dataList.size() % 2 == 0) {
                punchcardType = 1;
                currentType = 1;
                currentTypeString = "上班打卡";
            } else {
                punchcardType = 2;
                currentType = 1;
                currentTypeString = "下班打卡";
            }
            Calendar c = Calendar.getInstance();
            if (getMillis(c) > getMillis(mCalendar)) {
                viewDelegate.hideActionBtn(true);
            } else {
                viewDelegate.hideActionBtn(false);
            }
            // viewDelegate.setWifiLocationInfo(wifiOK, locationOk, currentWifiName, address);
            viewDelegate.setVisibility(R.id.rl_wifi_and_location_info, false);
            return;
        }
        final String class_infoId = class_info.getId();
        hasArrangement = (!TextUtils.isEmpty(class_infoId) && !"0".equals(class_infoId) && !"-1".equals(class_infoId));
        viewDelegate.hideActionBtn(false);
        if (hasArrangement) {
            viewDelegate.setVisibility(R.id.rl_wifi_and_location_info, true);
            List<AttendanceTypeListBean> attendance_address = mDataBean.getAttendance_address();
            if (locationReady && attendance_address != null && attendance_address.size() > 0) {
                int nearAddressIndex = 0;
                double nearAddressDistance = Double.MAX_VALUE;
                for (int i = 0; i < attendance_address.size(); i++) {
                    AttendanceTypeListBean attendanceTypeListBean = attendance_address.get(i);
                    long effective_range = TextUtil.parseLong(attendanceTypeListBean.getEffective_range());
                    List<AttendanceTypeListBean.LocationBean> location = attendanceTypeListBean.getLocation();
                    if (location != null && location.size() > 0) {
                        AttendanceTypeListBean.LocationBean locationBean = location.get(0);
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
                            punchcardWay = 0;
                            locationOk = true;
                        }

                    }
                }
                nearLat = attendance_address.get(nearAddressIndex).getLocation().get(0).getLat();
                nearLng = attendance_address.get(nearAddressIndex).getLocation().get(0).getLng();
                rangeValue = attendance_address.get(nearAddressIndex).getEffective_range();

            }
            List<AttendanceTypeListBean> attendance_wifi = mDataBean.getAttendance_wifi();
            if (!locationOk && !TextUtils.isEmpty(currentMacAddress) && attendance_wifi != null && attendance_wifi.size() > 0) {
                for (int i = 0; i < attendance_wifi.size(); i++) {
                    if (currentMacAddress.toLowerCase().equals(attendance_wifi.get(i).getAddress().toLowerCase())) {
                        punchcardWay = 1;
                        wifiOK = true;
                    }
                }
            }
            viewDelegate.setWifiLocationInfo(wifiOK, locationOk, currentWifiName, address);
            initDataList(mode);
        } else {
            List<AttendanceTypeListBean> attendance_address = mDataBean.getAttendance_address();
            if (locationReady && attendance_address != null && attendance_address.size() > 0) {
                int nearAddressIndex = 0;
                double nearAddressDistance = Double.MAX_VALUE;
                for (int i = 0; i < attendance_address.size(); i++) {
                    AttendanceTypeListBean attendanceTypeListBean = attendance_address.get(i);
                    long effective_range = TextUtil.parseLong(attendanceTypeListBean.getEffective_range());
                    List<AttendanceTypeListBean.LocationBean> location = attendanceTypeListBean.getLocation();
                    if (location != null && location.size() > 0) {
                        AttendanceTypeListBean.LocationBean locationBean = location.get(0);
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
                            punchcardWay = 0;
                            locationOk = true;
                        }

                    }
                }
                nearLat = attendance_address.get(nearAddressIndex).getLocation().get(0).getLat();
                nearLng = attendance_address.get(nearAddressIndex).getLocation().get(0).getLng();
                rangeValue = attendance_address.get(nearAddressIndex).getEffective_range();

            }
            List<AttendanceTypeListBean> attendance_wifi = mDataBean.getAttendance_wifi();
            if (!locationOk && !TextUtils.isEmpty(currentMacAddress) && attendance_wifi != null && attendance_wifi.size() > 0) {
                for (int i = 0; i < attendance_wifi.size(); i++) {
                    if (currentMacAddress.toLowerCase().equals(attendance_wifi.get(i).getAddress().toLowerCase())) {
                        punchcardWay = 1;
                        wifiOK = true;
                    }
                }

            }


            mEmptyView.setEmptyTitle("今天不上班,好好休息！");
            mEmptyView.setEmptyImage(R.drawable.workbench_empty);
            List<AttendanceDataItemBean> clock_record_list = mDataBean.getClock_record_list();
            dataList.clear();
            dataList.addAll(clock_record_list);
            attendanceComplete = false;
            viewDelegate.setVisibility(R.id.rl_action, true);
            viewDelegate.setVisibility(R.id.ll_complete, false);

            if (TextUtils.isEmpty(class_infoId)) {
                wifiOK = true;
                locationOk = true;
                if (dataList.size() % 2 == 0) {
                    punchcardType = 1;
                    currentType = 1;
                    currentTypeString = "上班打卡";
                } else {
                    punchcardType = 2;
                    currentType = 1;
                    currentTypeString = "下班打卡";
                }
            } else {
                if ("0".equals(class_infoId)) {
                    if (dataList.size() % 2 == 0) {
                        punchcardType = 1;
                        currentType = 1;
                        currentTypeString = "加班打卡";
                    } else {
                        punchcardType = 2;
                        currentType = 1;
                        currentTypeString = "加班打卡";
                    }
                } else if ("-1".equals(class_infoId)) {
                    if (dataList.size() % 2 == 0) {
                        punchcardType = 1;
                        currentType = 1;
                        currentTypeString = "上班打卡";
                    } else {
                        punchcardType = 2;
                        currentType = 1;
                        currentTypeString = "下班打卡";
                    }
                }
            }

            viewDelegate.setWifiLocationInfo(wifiOK, locationOk, currentWifiName, address);
        }

    }

    private void initDataList(String mode) {
        final long currentTime = mCalendarCutrrent.getTimeInMillis();
        final AttendanceInfoBean.DataBean.ClassInfoBean class_info = mDataBean.getClass_info();
        final String class_type = class_info.getClass_type();
        List<AttendanceDataItemBean> list = new ArrayList<>();
        List<AttendanceDataItemBean> clock_record_list = mDataBean.getClock_record_list();

        initTime(currentTime, class_info);
        AttendanceDataItemBean bean1 = new AttendanceDataItemBean();
        AttendanceDataItemBean bean2 = new AttendanceDataItemBean();
        AttendanceDataItemBean bean3 = new AttendanceDataItemBean();
        AttendanceDataItemBean bean4 = new AttendanceDataItemBean();
        AttendanceDataItemBean bean5 = new AttendanceDataItemBean();
        AttendanceDataItemBean bean6 = new AttendanceDataItemBean();
        switch (mode) {
            case "0":
                //固定
            case "1":
                //排班
                switch (class_type) {
                    case "1":
                        bean1.setExpect_punchcard_time(class_info.getTime1_start());
                        bean1.setPunchcard_type("1");
                        bean1.setPunchcard_status("0");
                        bean1.setExpect_punchcard_time(time1 + "");
                        list.add(bean1);
                        bean2.setExpect_punchcard_time(class_info.getTime1_end());
                        bean2.setPunchcard_type("2");
                        bean2.setPunchcard_status("0");
                        bean2.setExpect_punchcard_time(time2 + "");
                        list.add(bean2);
                        break;
                    case "2":
                        bean1.setExpect_punchcard_time(class_info.getTime1_start());
                        bean1.setPunchcard_type("1");
                        bean1.setPunchcard_status("0");
                        bean1.setExpect_punchcard_time(time1 + "");
                        list.add(bean1);
                        bean2.setExpect_punchcard_time(class_info.getTime1_end());
                        bean2.setPunchcard_type("2");
                        bean2.setPunchcard_status("0");
                        bean2.setExpect_punchcard_time(time2 + "");
                        list.add(bean2);
                        bean3.setExpect_punchcard_time(class_info.getTime2_start());
                        bean3.setPunchcard_type("1");
                        bean3.setPunchcard_status("0");
                        bean3.setExpect_punchcard_time(time3 + "");
                        list.add(bean3);
                        bean4.setExpect_punchcard_time(class_info.getTime2_end());
                        bean4.setPunchcard_type("2");
                        bean4.setPunchcard_status("0");
                        bean4.setExpect_punchcard_time(time4 + "");
                        list.add(bean4);
                        break;
                    case "3":
                        bean1.setExpect_punchcard_time(class_info.getTime1_start());
                        bean1.setPunchcard_type("1");
                        bean1.setPunchcard_status("0");
                        bean1.setExpect_punchcard_time(time1 + "");
                        list.add(bean1);
                        bean2.setExpect_punchcard_time(class_info.getTime1_end());
                        bean2.setPunchcard_type("2");
                        bean2.setPunchcard_status("0");
                        bean2.setExpect_punchcard_time(time2 + "");
                        list.add(bean2);
                        bean3.setExpect_punchcard_time(class_info.getTime2_start());
                        bean3.setPunchcard_type("1");
                        bean3.setPunchcard_status("0");
                        bean3.setExpect_punchcard_time(time3 + "");
                        list.add(bean3);
                        bean4.setExpect_punchcard_time(class_info.getTime2_end());
                        bean4.setPunchcard_type("2");
                        bean4.setPunchcard_status("0");
                        bean4.setExpect_punchcard_time(time4 + "");
                        list.add(bean4);
                        bean5.setExpect_punchcard_time(class_info.getTime3_start());
                        bean5.setPunchcard_status("0");
                        bean5.setPunchcard_type("1");
                        bean5.setExpect_punchcard_time(time5 + "");
                        list.add(bean5);
                        bean6.setExpect_punchcard_time(class_info.getTime3_end());
                        bean6.setPunchcard_type("2");
                        bean6.setPunchcard_status("0");
                        bean6.setExpect_punchcard_time(time6 + "");
                        list.add(bean6);
                        break;
                }

                for (int i = 0; i < clock_record_list.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (i == j) {
                            list.get(j).setId(clock_record_list.get(i).getId());
                            list.get(j).setIs_outworker(clock_record_list.get(i).getIs_outworker());
                            list.get(j).setIs_way(clock_record_list.get(i).getIs_way());
                            list.get(j).setPunchcard_address(clock_record_list.get(i).getPunchcard_address());
                            list.get(j).setPunchcard_result(clock_record_list.get(i).getPunchcard_result());
                            list.get(j).setPunchcard_status(clock_record_list.get(i).getPunchcard_status());
                            list.get(j).setReal_punchcard_time(clock_record_list.get(i).getReal_punchcard_time());
                            list.get(j).setData_id(clock_record_list.get(i).getData_id());
                            list.get(j).setBean_name(clock_record_list.get(i).getBean_name());
                            list.get(j).setPhoto(clock_record_list.get(i).getPhoto());
                            list.get(j).setRemark(clock_record_list.get(i).getRemark());
                            if (!"1".equals(list.get(j).getPunchcard_status())) {
                                isNormal = false;
                            }
                        }
                    }
                }
                if (clock_record_list.size() == 0) {
                    list.get(0).setId("");
                    list.get(0).setIs_outworker("1");
                    list.get(0).setIs_way("");
                    list.get(0).setPunchcard_address("");
                    list.get(0).setPunchcard_result("");
                    list.get(0).setPunchcard_status("0");
                    list.get(0).setReal_punchcard_time("");
                    list.get(0).setPunchcard_type("1");
                }
                dataList.clear();
                dataList.addAll(list);
                mTodayAttendanceAdapter.notifyDataSetChanged();

                if (dataList.size() == 0) {
                    attendanceComplete = false;
                } else {

                    if ("0".equals(dataList.get(dataList.size() - 1).getPunchcard_status())) {
                        attendanceComplete = false;
                    } else {
                        attendanceComplete = true;
                    }
                }

                rootFor:
                for (int i = 0; i < dataList.size(); i++) {
                    final String punchcard_status = dataList.get(i).getPunchcard_status();
                    if ("0".equals(punchcard_status)) {
                        switch (i) {
                            case 0:
                                punchcardType = 1;
                                if (currentTime < time1) {
                                    // TODO: 2019/3/11 判断是否已打上班卡
                                    if (time1Limit == time1) {
                                        currentType = 1;
                                        attendanceComplete = false;
                                        currentTypeString = "上班打卡";
                                        currentTimeField = "time1_start";
                                    } else {
                                        if (currentTime > time1Limit && currentTime < time1) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "上班打卡";
                                            currentTimeField = "time1_start";
                                        } else {
                                            //ToastUtils.showToast(getActivity(),"还不能打上班卡");
                                            currentType = 4;
                                            attendanceComplete = false;
                                            currentTypeString = "上班打卡";
                                        }
                                    }

                                } else if (currentTime > time1 && currentTime < time2) {
                                    // TODO: 2019/3/11 若已打上班卡则此处为打早退卡
                                    currentType = 3;
                                    attendanceComplete = false;
                                    currentTypeString = "迟到打卡";
                                    currentTimeField = "time1_start";

                                }
                                break;
                            case 1:
                                punchcardType = 2;
                                if (currentTime > time1 && currentTime < time2) {
                                    currentType = 0;
                                    attendanceComplete = false;
                                    currentTypeString = "早退打卡";
                                    currentTimeField = "time1_end";
                                    title = "你早退了" + getDuration(currentTime, time2);
                                    content = "下班时间:" + DateTimeUtil.longToStr(time2, "HH:mm");

                                } else {
                                    if (time2 == time2Limit) {
                                        if (currentTime >= time2) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "下班打卡";
                                            currentTimeField = "time1_end";
                                        }

                                    } else {
                                        if (currentTime >= time2 && currentTime <= time2Limit) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "下班打卡";
                                            currentTimeField = "time1_end";
                                        }
                                    }

                                }
                                break;
                            case 2:
                                punchcardType = 1;
                                if (currentTime < time3) {
                                    // TODO: 2019/3/11 判断是否已打上班卡
                                    if (time3Limit == time3) {
                                        currentType = 1;
                                        attendanceComplete = false;
                                        currentTypeString = "上班打卡";
                                        currentTimeField = "time2_start";
                                    } else {
                                        if (currentTime > time3Limit && currentTime < time3) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "上班打卡";
                                            currentTimeField = "time2_start";
                                        } else {
                                            //ToastUtils.showToast(getActivity(),"还不能打上班卡");
                                            currentType = 4;
                                            attendanceComplete = false;
                                            currentTypeString = "上班打卡";
                                        }
                                    }

                                } else if (currentTime > time3 && currentTime < time4) {
                                    // TODO: 2019/3/11 若已打上班卡则此处为打早退卡
                                    currentType = 3;
                                    attendanceComplete = false;
                                    currentTypeString = "迟到打卡";
                                    currentTimeField = "time2_start";

                                }
                                break;
                            case 3:
                                punchcardType = 2;
                                if (currentTime > time3 && currentTime < time4) {
                                    currentType = 0;
                                    attendanceComplete = false;
                                    currentTypeString = "早退打卡";
                                    currentTimeField = "time2_end";
                                    title = "你早退了" + getDuration(currentTime, time4);
                                    content = "下班时间:" + DateTimeUtil.longToStr(time4, "HH:mm");

                                } else {
                                    if (time4 == time4Limit) {
                                        if (currentTime >= time4) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "下班打卡";
                                            currentTimeField = "time2_end";
                                        }

                                    } else {
                                        if (currentTime >= time4 && currentTime <= time4Limit) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "下班打卡";
                                            currentTimeField = "time2_end";
                                        }
                                    }

                                }
                                break;
                            case 4:
                                punchcardType = 1;
                                if (currentTime < time5) {
                                    // TODO: 2019/3/11 判断是否已打上班卡
                                    if (time5Limit == time5) {
                                        currentType = 1;
                                        attendanceComplete = false;
                                        currentTypeString = "上班打卡";
                                        currentTimeField = "time3_start";
                                    } else {
                                        if (currentTime > time5Limit && currentTime < time5) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "上班打卡";
                                            currentTimeField = "time3_start";
                                        } else {
                                            //ToastUtils.showToast(getActivity(),"还不能打上班卡");
                                            currentType = 4;
                                            attendanceComplete = false;
                                            currentTypeString = "上班打卡";
                                        }
                                    }

                                } else if (currentTime > time5 && currentTime < time6) {
                                    // TODO: 2019/3/11 若已打上班卡则此处为打早退卡
                                    currentType = 3;
                                    attendanceComplete = false;
                                    currentTypeString = "迟到打卡";
                                    currentTimeField = "time3_start";

                                }
                                break;
                            case 5:
                                punchcardType = 2;
                                if (currentTime > time5 && currentTime < time6) {
                                    currentType = 0;
                                    attendanceComplete = false;
                                    currentTypeString = "早退打卡";
                                    currentTimeField = "time3_end";
                                    title = "你早退了" + getDuration(currentTime, time6);
                                    content = "下班时间:" + DateTimeUtil.longToStr(time6, "HH:mm");

                                } else {
                                    if (time6 == time6Limit) {
                                        if (currentTime >= time6) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "下班打卡";
                                            currentTimeField = "time3_end";
                                        }

                                    } else {
                                        if (currentTime >= time6 && currentTime <= time6Limit) {
                                            currentType = 1;
                                            attendanceComplete = false;
                                            currentTypeString = "下班打卡";
                                            currentTimeField = "time3_end";
                                        }
                                    }

                                }
                                break;
                        }

                        break rootFor;
                    }

                }
                if (attendanceComplete) {
                    viewDelegate.attendanceComplete(isNormal);
                    if (dataList.size() == 2) {
                        punchcardType = 2;
                        if (currentTime > time1 && currentTime < time2) {
                            currentType = 0;
                            attendanceComplete = false;
                            currentTypeString = "早退打卡";
                            title = "你早退了" + getDuration(currentTime, time2);
                            content = "下班时间:" + DateTimeUtil.longToStr(time2, "HH:mm");
                            currentTimeField = "time1_end";
                            dataList.get(1).setCanUpdate(true);

                        } else {
                            if (time2 == time2Limit) {
                                if (currentTime >= time2) {
                                    currentType = 1;
                                    attendanceComplete = false;
                                    currentTypeString = "下班打卡";
                                    currentTimeField = "time1_end";
                                    dataList.get(1).setCanUpdate(true);
                                }

                            } else {
                                if (currentTime >= time2 && currentTime <= time2Limit) {
                                    currentType = 1;
                                    attendanceComplete = false;
                                    currentTypeString = "下班打卡";
                                    currentTimeField = "time1_end";
                                    dataList.get(1).setCanUpdate(true);
                                }
                            }

                        }
                    } else if (dataList.size() == 4) {
                        punchcardType = 2;
                        if (currentTime > time3 && currentTime < time4) {
                            currentType = 0;
                            attendanceComplete = false;
                            currentTypeString = "早退打卡";
                            currentTimeField = "time2_end";
                            dataList.get(3).setCanUpdate(true);
                            title = "你早退了" + getDuration(currentTime, time4);
                            content = "下班时间:" + DateTimeUtil.longToStr(time4, "HH:mm");

                        } else {
                            if (time4 == time4Limit) {
                                if (currentTime >= time4) {
                                    currentType = 1;
                                    attendanceComplete = false;
                                    currentTypeString = "下班打卡";
                                    currentTimeField = "time2_end";
                                    dataList.get(3).setCanUpdate(true);
                                }

                            } else {
                                if (currentTime >= time4 && currentTime <= time4Limit) {
                                    currentType = 1;
                                    attendanceComplete = false;
                                    currentTypeString = "下班打卡";
                                    currentTimeField = "time2_end";
                                    dataList.get(3).setCanUpdate(true);
                                }
                            }

                        }
                    } else if (dataList.size() == 6) {
                        punchcardType = 2;
                        if (currentTime > time5 && currentTime < time6) {
                            currentType = 0;
                            attendanceComplete = false;
                            currentTypeString = "早退打卡";
                            currentTimeField = "time3_end";
                            content = "下班时间:" + DateTimeUtil.longToStr(time6, "HH:mm");
                            dataList.get(5).setCanUpdate(true);
                            title = "你早退了" + getDuration(currentTime, time6);

                        } else {
                            if (time6 == time6Limit) {
                                if (currentTime >= time6) {
                                    currentType = 1;
                                    attendanceComplete = false;
                                    currentTypeString = "下班打卡";
                                    currentTimeField = "time3_end";
                                    dataList.get(5).setCanUpdate(true);
                                }

                            } else {
                                if (currentTime >= time6 && currentTime <= time6Limit) {
                                    currentType = 1;
                                    attendanceComplete = false;
                                    currentTypeString = "下班打卡";
                                    currentTimeField = "time3_end";
                                    dataList.get(5).setCanUpdate(true);
                                }
                            }

                        }
                    }
                } else {
                    viewDelegate.setVisibility(R.id.rl_action, true);
                    viewDelegate.setVisibility(R.id.ll_complete, false);
                }
                break;
            case "2":
                attendanceComplete = false;
                viewDelegate.setVisibility(R.id.rl_action, true);
                viewDelegate.setVisibility(R.id.ll_complete, false);
                dataList.clear();
                dataList.addAll(clock_record_list);
                //自由
                String id = mDataBean.getClass_info().getId();

                if ("0".equals(id)) {
                    //休息
                    if (dataList.size() % 2 == 0) {
                        punchcardType = 1;
                        currentType = 1;
                        currentTypeString = "加班打卡";
                    } else {
                        punchcardType = 2;
                        currentType = 1;
                        currentTypeString = "加班打卡";
                    }
                } else if ("-1".equals(id)) {
                    if (dataList.size() % 2 == 0) {
                        punchcardType = 1;
                        currentType = 1;
                        currentTypeString = "上班打卡";
                    } else {
                        punchcardType = 2;
                        currentType = 1;
                        currentTypeString = "下班打卡";
                    }
                }


                break;
        }
        mTodayAttendanceAdapter.notifyDataSetChanged();

    }

    private String getDuration(long currentTime, long timeEnd) {
        String time = "0分钟";
        if (timeEnd > currentTime) {
            long duration = timeEnd - currentTime;
            int hour = (int) (duration / (60 * 60 * 1000));
            int minite = (int) ((duration % (60 * 60 * 1000)) / (60 * 1000));
            if (hour > 0) {
                time = hour + "小时" + minite + "分钟";
            } else {
                time = minite + "分钟";
            }
        }


        return time;
    }

    private void initTime(long currentTime, AttendanceInfoBean.DataBean.ClassInfoBean class_info) {
        final String class_type = class_info.getClass_type();
        if (TextUtils.isEmpty(class_type)) {
            return;
        }
        switch (class_type) {
            case "1":
                time1(currentTime, class_info);
                break;
            case "2":
                time1(currentTime, class_info);
                time2(currentTime, class_info);
                break;
            case "3":
                time1(currentTime, class_info);
                time2(currentTime, class_info);
                time3(currentTime, class_info);

                break;
        }


    }

    private void time3(long currentTime, AttendanceInfoBean.DataBean.ClassInfoBean class_info) {
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

    private void time2(long currentTime, AttendanceInfoBean.DataBean.ClassInfoBean class_info) {
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

    private void time1(long currentTime, AttendanceInfoBean.DataBean.ClassInfoBean class_info) {
        time1 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime1_start(), "yyyy-MM-dd HH:mm");
        time2 = DateTimeUtil.strToLong(DateTimeUtil.longToStr(currentTime, "yyyy-MM-dd") + " " + class_info.getTime1_end(), "yyyy-MM-dd HH:mm");

        if (time2 <= time1) {
            time2 = time2 + 24 * 60 * 60 * 1000;
        }
        time1Limit = time1 - TextUtil.parseInt(class_info.getTime1_start_limit(), 0) * 60 * 1000;
        time2Limit = time2 + TextUtil.parseInt(class_info.getTime1_end_limit(), 0) * 60 * 1000;
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.get(R.id.action_btn2).setOnClickListener(v -> {
            punchClock();
        });
        viewDelegate.get(R.id.rl_wifi_and_location_info).setOnClickListener(v -> {
            if (hasArrangement && mDataBean != null && mDataBean.getAttendance_address().size() > 0) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, nearLat);
                bundle.putString(Constants.DATA_TAG2, nearLng);
                bundle.putString(Constants.DATA_TAG3, rangeValue);
                CommonUtil.startActivtiyForResult(getActivity(), LocationPresenterV2.class, Constants.REQUEST_CODE1, bundle);
            } else {
                ToastUtils.showToast(getActivity(), "无考勤位置数据");
            }

        });
        viewDelegate.get(R.id.action_btn2).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN){
                    viewDelegate.setTimeAndAction(currentType,1);
                }else if (action == MotionEvent.ACTION_UP){
                    viewDelegate.setTimeAndAction(currentType,0);
                }
                return false;
            }
        });

        viewDelegate.get(R.id.text3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, CloneUtils.clone(mCalendar));
                bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM_DD);
                CommonUtil.startActivtiyForResult(getActivity(), DateTimeSelectPresenter.class, Constants.REQUEST_CODE12, bundle);
            }
        });
        viewDelegate.mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
            }
        });

        viewDelegate.mTvAttendanceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.DATA_TAG1,(Serializable)mDataBean);
                CommonUtil.startActivtiy(getActivity(), AttendanceRulesMineActivity.class,bundle);
            }
        });
    }

    private void punchClock() {
        if (mDataBean == null || currentType == 4) {
            getAttendanceInfo(getMillis(mCalendar), false);
            // ToastUtils.showToast(getActivity(), "正在获取信息");
            return;
        }
        if (!outworker_status && !(wifiOK || locationOk)) {
            ToastUtils.showToast(getActivity(), "不允许外勤打卡");
            return;
        }
        //今天是否需要打卡
        Map<String, Serializable> map = new HashMap<>();
        //判断wifi与定位
        AttendanceInfoBean.DataBean.ClassInfoBean class_info = mDataBean.getClass_info();
        Long groupId = null;
        groupId = TextUtil.parseLong(mDataBean.getId(), 0L);
        if (groupId == 0L) {
            groupId = null;
        }
        if (!hasArrangement) {
            currentTimeField = "";
        }
        if (!TextUtils.isEmpty(currentTimeField)) {
            switch (currentTimeField) {
                case "time1_start":
                    rightTime = time1;
                    break;
                case "time2_start":
                    rightTime = time2;
                    break;
                case "time3_start":
                    rightTime = time3;
                    break;
                case "time1_end":
                    rightTime = time4;
                    break;
                case "time2_end":
                    rightTime = time5;
                    break;
                case "time3_end":
                    rightTime = time6;
                    break;
            }
        } else {
            rightTime = System.currentTimeMillis();
        }


        //判断时间段
        //能否外勤打卡
        //图片与原由
        map.put("punchcardTimeField", currentTimeField);
        map.put("attendanceDate", getMillis(mCalendar));
        map.put("groupId", groupId);
        map.put("punchcardType", punchcardType);
        map.put("punchcardWay", punchcardWay);
        map.put("punchcardEquipment", Build.BRAND + Build.DEVICE);
        map.put("punchcardAddress", punchcardWay == 0 ? address : currentWifiName);
        map.put("photo", "");
        map.put("remark", "");
        map.put("isOutworker", (wifiOK || locationOk) ? 1 : 0);
        if (!(wifiOK || locationOk)) {
            /*NotifyUtils.getInstance().showEditMenu3(getActivity(), "880", "地址", "输入备注", viewDelegate.getRootView(), new NotifyUtils.OnSingleValueListener() {
                @Override
                public void clickSure(String value1) {
                    map.put("remark", value1);
                    realPunch(map);
                }
            });*/
            Bundle bundle = new Bundle();
            bundle.putString(Constants.DATA_TAG1, nearLat);
            bundle.putString(Constants.DATA_TAG2, nearLng);
            bundle.putString(Constants.DATA_TAG3, rangeValue);
            bundle.putSerializable(Constants.DATA_TAG4, (Serializable) map);
            CommonUtil.startActivtiyForResult(getActivity(), LocationPresenterV2.class, Constants.REQUEST_CODE10, bundle);
        } else {
            if (currentType == 0) {
                DialogUtils.getInstance().sureOrCancel(getActivity(), title, content, "确定打卡", "取消", viewDelegate.getRootView(), new DialogUtils.OnClickSureListener() {
                    @Override
                    public void clickSure() {
                        realPunch(map);
                    }
                });

            } else {
                realPunch(map);
            }
        }

    }

    private void realPunch(Map<String, Serializable> map) {
        model.punchClock(((ActivityPresenter) getActivity()), map, new ProgressSubscriber<BaseBean>(getActivity()) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                refreshData();
                String content1 = "";
                String content2 = "打卡时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                int type = 0;
                long time = mCalendarCutrrent.getTimeInMillis();
                if (hasArrangement && !TextUtils.isEmpty(currentTimeField)) {
                    switch (currentTimeField) {
                        case "time1_start":
                            if (time <= time1) {
                                type = AttendanceConstants.TYPE_WORK_NORMAL;
                                content1 = "上班时间:" + DateTimeUtil.longToStr(time1, "HH:mm");
                            } else {
                                type = AttendanceConstants.TYPE_WORK_UNNORMAL;
                                content1 = "迟到了明天早点来哦";
                            }
                            break;
                        case "time2_start":
                            if (time <= time3) {
                                type = AttendanceConstants.TYPE_WORK_NORMAL;
                                content1 = "上班时间:" + DateTimeUtil.longToStr(time3, "HH:mm");
                            } else {
                                type = AttendanceConstants.TYPE_WORK_UNNORMAL;
                                content1 = "迟到了明天早点来哦";
                            }
                            break;
                        case "time3_start":
                            if (time <= time5) {
                                type = AttendanceConstants.TYPE_WORK_NORMAL;
                                content1 = "上班时间:" + DateTimeUtil.longToStr(time5, "HH:mm");
                            } else {
                                type = AttendanceConstants.TYPE_WORK_UNNORMAL;
                                content1 = "迟到了明天早点来哦";
                            }
                            break;
                        case "time1_end":
                            if (time > time2) {
                                type = AttendanceConstants.TYPE_OFF_NORMAL;
                                content1 = "下班时间:" + DateTimeUtil.longToStr(time5, "HH:mm");
                            } else {
                                type = AttendanceConstants.TYPE_OFF_UNNORMAL;
                                content1 = "早退了要跟领导说明下哦";
                            }
                            break;
                        case "time2_end":
                            if (time > time4) {
                                type = AttendanceConstants.TYPE_OFF_NORMAL;
                                content1 = "下班时间:" + DateTimeUtil.longToStr(time4, "HH:mm");
                            } else {
                                type = AttendanceConstants.TYPE_OFF_UNNORMAL;
                                content1 = "早退了要跟领导说明下哦";
                            }
                            break;
                        case "time3_end":
                            if (time > time6) {
                                type = AttendanceConstants.TYPE_OFF_NORMAL;
                                content1 = "下班时间:" + DateTimeUtil.longToStr(time6, "HH:mm");
                            } else {
                                type = AttendanceConstants.TYPE_OFF_UNNORMAL;
                                content1 = "早退了要跟领导说明下哦";
                            }
                            break;
                        default:
                            if (punchcardType == 1) {
                                type = AttendanceConstants.TYPE_WORK_NORMAL;
                                content1 = "上班时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                                content2 = "打卡时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                            } else {
                                type = AttendanceConstants.TYPE_OFF_NORMAL;
                                content1 = "下班时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                                content2 = "打卡时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                            }
                            break;
                    }
                } else {
                    if (punchcardType == 1) {
                        type = AttendanceConstants.TYPE_WORK_NORMAL;
                        content1 = "上班时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                        content2 = "打卡时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                    } else {
                        type = AttendanceConstants.TYPE_OFF_NORMAL;
                        content1 = "下班时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                        content2 = "打卡时间:" + DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm");
                    }
                }

                NotifyUtils.getInstance().showOperationNotify(getActivity(), type, content1,
                        content2, viewDelegate.getRootView());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                getAttendanceInfo(getMillis(mCalendar), false);
            }
        });
    }

    private Long getMillis(Calendar c) {
        if (c == null) {
            return 0L;
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE12) {
            Calendar calendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (calendar.getTimeInMillis() >= mCalendarToday.getTimeInMillis() + (24 * 60 * 60 * 1000)) {
                ToastUtils.showError(getActivity(), "只能选择当前或之前的日期!");
            } else {
                mCalendar = calendar;
                viewDelegate.setDate(mCalendar.getTimeInMillis());
                getAttendanceInfo(getMillis(mCalendar), true);
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE10) {
            refreshData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        isLocating = false;
        isOutworker = 0;
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                lat = amapLocation.getLatitude() + "";
                lng = amapLocation.getLongitude() + "";
                address = amapLocation.getAddress();
                //   ToastUtils.showToast(getActivity(), address);
                viewDelegate.setStateInfo(address);
                locationReady = true;
                if (mDataBean != null) {
                    initActionBtn();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {
            if (forground) {
                viewDelegate.getRootView().post(new Runnable() {
                    @Override
                    public void run() {
                        if (!(wifiOK || locationOk)) {
                            currentType = 2;
                            currentTypeString = "外勤打卡";
                            if (!outworker_status) {
                                currentType = 4;
                            }
                        }
                        if (lastCurrentType != currentType){//判断是否需要切换背景
                            lastCurrentType = currentType;
                            viewDelegate.setTimeAndAction(currentType,0);
                        }

                        String setStr = DateTimeUtil.longToStr(System.currentTimeMillis(), "HH:mm:ss") + "\n" + currentTypeString;
                        Spannable str = new SpannableString(setStr);
                        str.setSpan(new AbsoluteSizeSpan(22, true), 0, setStr.length()-currentTypeString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        str.setSpan(new AbsoluteSizeSpan(16, true),setStr.length()-currentTypeString.length(), setStr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        viewDelegate.setTimeString(str);

                        if (needLocation && !isLocating) {
                            location();
                            wifi();
                            initActionBtn();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(this);
    }
}

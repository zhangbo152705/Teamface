package com.hjhq.teamface.attendance.presenter;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.LocationSource;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceInfoBean;
import com.hjhq.teamface.attendance.bean.AttendanceTypeListBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceDelegate2;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.luojilab.router.facade.annotation.RouteNode;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RouteNode(path = "/add_attendance", desc = "打卡测试界面")
public class AttendanceActivity2 extends ActivityPresenter<AttendanceDelegate2, AttendanceModel> implements View.OnClickListener, LocationSource.OnLocationChangedListener {
    private Calendar mDateCalendar;
    private Calendar mTimeCalendar;
    private AttendanceInfoBean.DataBean mData;
    private String[] addressMenu;
    private String[] wifiMenu;
    private String[] timeFieldValueMenu = new String[]{"time1_start", "time1_end", "time2_start", "time2_end", "time3_start", "time3_end"};
    private String[] timeFieldMenu = new String[]{"第1次上班", "第1次下班", "第2次上班", "第2次下班", "第3次上班", "第3次下班"};
    private int timeFieldMenuIndex = -1;

    @Override
    public void onClick(View v) {

    }

    @Override
    public void init() {
        viewDelegate.setTitle("打卡测试界面");
        mDateCalendar = Calendar.getInstance();
        mTimeCalendar = Calendar.getInstance();
        mDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mDateCalendar.set(Calendar.MINUTE, 0);
        mDateCalendar.set(Calendar.SECOND, 0);
        mDateCalendar.set(Calendar.MILLISECOND, 0);
        model.getAttendanceInfo(mContext, getMillis(mDateCalendar), new ProgressSubscriber<AttendanceInfoBean>(mContext) {
            @Override
            public void onNext(AttendanceInfoBean attendanceInfoBean) {
                super.onNext(attendanceInfoBean);
                mData = attendanceInfoBean.getData();
                if (mData != null) {
                    TextUtil.setText(viewDelegate.mEditText1, mData.getId());
                    List<AttendanceTypeListBean> attendance_address = mData.getAttendance_address();
                    if (attendance_address != null && attendance_address.size() > 0) {
                        addressMenu = new String[attendance_address.size()];
                        for (int i = 0; i < attendance_address.size(); i++) {
                            addressMenu[i] = attendance_address.get(i).getAddress();
                        }
                        if (addressMenu != null && addressMenu.length > 0) {
                            viewDelegate.mEditText4.setText(addressMenu[0]);
                        }
                    }
                    List<AttendanceTypeListBean> attendance_wifi = mData.getAttendance_wifi();
                    if (attendance_wifi != null && attendance_wifi.size() > 0) {
                        wifiMenu = new String[attendance_wifi.size()];
                        for (int i = 0; i < attendance_wifi.size(); i++) {
                            wifiMenu[i] = attendance_wifi.get(i).getName();
                        }
                        if (wifiMenu != null && wifiMenu.length > 0) {
                            viewDelegate.mEditText3.setText(wifiMenu[0]);
                        }
                    }
                    TextUtil.setText(viewDelegate.mTvInfo, JSONObject.toJSONString(mData));


                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
        viewDelegate.mEditText5.setText(Build.BRAND + Build.DEVICE + "");
        viewDelegate.mEditText0.setText(DateTimeUtil.longToStr(getMillis(mDateCalendar), "yyyy-MM-dd"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String currentTimeField = "";
        String isOutworker = "";
        String punchcardType = "";
        String punchcardWay = "";
        String punchcardAddress = "";
        String punchcardEquipment = "";
        String photo = "";
        String remark = "";
        if (timeFieldMenuIndex > -1) {
            currentTimeField = timeFieldValueMenu[timeFieldMenuIndex];
        }
        punchcardEquipment = viewDelegate.mEditText5.getText().toString().trim();
        punchcardType = viewDelegate.mEditText7.getText().toString().trim();
        punchcardWay = viewDelegate.mEditText2.getText().toString().trim();
        remark = viewDelegate.mEditText9.getText().toString().trim();
        photo = viewDelegate.mEditText8.getText().toString().trim();
        if ("0".equals(punchcardWay)) {
            punchcardAddress = viewDelegate.mEditText4.getText().toString().trim();
        } else if ("1".equals(punchcardWay)) {
            punchcardAddress = viewDelegate.mEditText3.getText().toString().trim();
        }
        isOutworker = viewDelegate.mEditText10.getText().toString().trim();

        Map<String, Serializable> map = new HashMap<>();
        map.put("punchcardTimeField", currentTimeField);
        map.put("attendanceDate", getMillis(mDateCalendar));
        map.put("groupId", mData == null ? "" : mData.getId());
        map.put("punchcardType", punchcardType);
        map.put("punchcardWay", punchcardWay);
        map.put("punchcardEquipment", punchcardEquipment);
        map.put("punchcardAddress", punchcardAddress);
        map.put("photo", photo);
        map.put("remark", remark);
        map.put("isOutworker", isOutworker);
        model.punchClock(mContext, map, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showToast(mContext, "打卡成功");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ;
            }
        });
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.get(R.id.tv_choose_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, CloneUtils.clone(mDateCalendar));
                bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM_DD);
                CommonUtil.startActivtiyForResult(mContext, DateTimeSelectPresenter.class, Constants.REQUEST_CODE6, bundle);
            }
        });
        viewDelegate.get(R.id.choose_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiMenu != null && wifiMenu.length > 0) {
                    PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "考勤WiFi名称", wifiMenu, new OnMenuSelectedListener() {
                        @Override
                        public boolean onMenuSelected(int p) {
                            viewDelegate.mEditText3.setText(wifiMenu[p]);
                            return false;
                        }
                    });

                } else {
                    ToastUtils.showToast(mContext, "未设置");
                }
            }
        });
        viewDelegate.get(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData != null) {
                    SystemFuncUtils.copyTextToClipboard(mContext, JSONObject.toJSONString(mData));
                    ToastUtils.showToast(mContext, "复制成功");
                }
            }
        });
        viewDelegate.get(R.id.tv_choose_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressMenu != null && addressMenu.length > 0) {
                    PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "考勤地址", addressMenu, new OnMenuSelectedListener() {
                        @Override
                        public boolean onMenuSelected(int p) {
                            viewDelegate.mEditText4.setText(addressMenu[p]);
                            return false;
                        }
                    });

                } else {
                    ToastUtils.showToast(mContext, "未设置");
                }
            }
        });
        viewDelegate.get(R.id.tv_choose_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeFieldMenu != null && timeFieldMenu.length > 0) {
                    PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "考勤地址", timeFieldMenu, new OnMenuSelectedListener() {
                        @Override
                        public boolean onMenuSelected(int p) {
                            viewDelegate.mEditText6.setText(timeFieldMenu[p]);
                            timeFieldMenuIndex = p;
                            return false;
                        }
                    });

                } else {
                    ToastUtils.showToast(mContext, "未设置");
                }

            }
        });
        viewDelegate.get(R.id.tv_clean_et6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDelegate.mEditText6.setText("");
                timeFieldMenuIndex = -1;
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
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE11) {
            Calendar calendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (calendar != null) {
                mDateCalendar = calendar;
                viewDelegate.mEditText0.setText(DateTimeUtil.longToStr(getMillis(mDateCalendar), "yyyy-MM-dd"));
            }

        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE12) {
            Calendar calendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (calendar != null) {
                mTimeCalendar = calendar;
                viewDelegate.mEditText6.setText(DateTimeUtil.longToStr(getMillis(mDateCalendar), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

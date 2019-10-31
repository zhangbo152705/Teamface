package com.hjhq.teamface.attendance.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.ViewAttendanceNumDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/view_attendance_member", desc = "打卡人数")
public class AttendanceNumActivity extends ActivityPresenter<ViewAttendanceNumDelegate, AttendanceModel> implements View.OnClickListener {
    List<Fragment> mFragmentList = new ArrayList<>(2);
    String[] titleArray = new String[]{"打卡人数(0)", "未打卡人数(0)"};
    AttendanceDayDataBean.DataListBean mDataListBean;
    long time;

    @Override
    public void init() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDataListBean = (AttendanceDayDataBean.DataListBean) bundle.getSerializable(Constants.DATA_TAG1);
            time = bundle.getLong(Constants.DATA_TAG2, 0L);
            if (time > 0) {
                viewDelegate.setRightMenuTexts(DateTimeUtil.longToStr(time, "yyyy.MM.dd"));
            }
            titleArray[0] = "打卡人数(" + mDataListBean.getEmployeeList().size() + ")";
            titleArray[1] = "未打卡人数(" + mDataListBean.getNopunchClock().getEmployeeList().size() + ")";
        }
        initView();
    }

    private void initView() {
        mFragmentList.add(DayDataFragment.newInstance(1));
        mFragmentList.add(DayDataFragment.newInstance(2));
        viewDelegate.initNavigator(titleArray, mFragmentList);
        viewDelegate.setTitle(R.string.attendance_view_member_num);
    }


    @Override
    protected void bindEvenListener() {


        super.bindEvenListener();
    }


    @Override
    public void onClick(View v) {

    }

    public List<AttendanceDayDataBean.DataListBean> getData(int type) {
        List<AttendanceDayDataBean.DataListBean> list = new ArrayList<>();
        if (mDataListBean != null) {
            if (type == 1) {
                list = mDataListBean.getEmployeeList();
            } else if (type == 2) {
                list = mDataListBean.getNopunchClock().getEmployeeList();
            }
            return list;
        } else {

            return new ArrayList<>();
        }
    }
}

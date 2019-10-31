package com.hjhq.teamface.attendance.presenter;

import android.os.Bundle;
import android.view.View;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.DayDataDetailAdapter;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.DataListDelegate;
import com.hjhq.teamface.common.view.EmptyView;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/day_data_detail", desc = "日排行榜")
public class DayActivity extends ActivityPresenter<DataListDelegate, AttendanceModel> implements View.OnClickListener {
    private AttendanceMonthDataBean.DataBean.DataListBean data;
    ArrayList<AttendanceDayDataBean.DataListBean> employeeList;
    AttendanceDayDataBean.DataListBean mData;
    DayDataDetailAdapter mAdapter;
    EmptyView mEmptyView;
    private int type;
    private long time;


    @Override
    public void onClick(View v) {

    }

    @Override
    public void init() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mData = (AttendanceDayDataBean.DataListBean) extras.getSerializable(Constants.DATA_TAG1);
            viewDelegate.setTitle(mData.getName());
            employeeList = mData.getEmployeeList();
            time = extras.getLong(Constants.DATA_TAG2);
            type = extras.getInt(Constants.DATA_TAG3);
            mAdapter = new DayDataDetailAdapter(type, employeeList);
            mEmptyView = new EmptyView(mContext);
            mEmptyView.setEmptyImage(R.drawable.workbench_empty);
            mEmptyView.setEmptyTitle("无数据");
            mAdapter.setEmptyView(mEmptyView);
            viewDelegate.setAdapter(mAdapter);
        }
    }
}

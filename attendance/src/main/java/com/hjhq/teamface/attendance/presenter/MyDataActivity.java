package com.hjhq.teamface.attendance.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.MyDataSubAdapter;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.DataListDelegate;
import com.hjhq.teamface.common.view.EmptyView;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/my_data_detail", desc = "我的统计")
public class MyDataActivity extends ActivityPresenter<DataListDelegate, AttendanceModel> {
    private AttendanceMonthDataBean.DataBean.DataListBean data;
    ArrayList<AttendanceMonthDataBean.AttendanceListBean> employeeList;
    MyDataSubAdapter mAdapter;
    EmptyView mEmptyView;


    @Override
    public void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            data = (AttendanceMonthDataBean.DataBean.DataListBean) bundle.getSerializable(Constants.DATA_TAG1);
            String date = bundle.getString(Constants.DATA_TAG2);
            if (!TextUtils.isEmpty(date)) {
                viewDelegate.setRightMenuTexts(date);
            }
            if (data != null) {
                employeeList = data.getAttendanceList();
                mAdapter = new MyDataSubAdapter(TextUtil.parseInt(data.getType()), employeeList);
                mEmptyView = new EmptyView(mContext);
                mEmptyView.setEmptyImage(R.drawable.workbench_empty);
                mAdapter.setEmptyView(mEmptyView);
                mEmptyView.setEmptyTitle("无数据");
                viewDelegate.setAdapter(mAdapter);
                String name = "";
                switch (data.getType()) {
                    case "1":
                        name = "迟到";
                        break;
                    case "2":
                        name = "早退";
                        break;
                    case "3":
                        name = "缺卡";
                        break;
                    case "4":
                        name = "旷工";
                        break;
                    case "5":
                        name = "外勤打卡";
                        break;
                    case "6":
                        name = data.getName();
                        break;
                    case "7":
                        name = "正常打卡";
                        break;
                }
                viewDelegate.setTitle(name);
            }
        }

    }
}

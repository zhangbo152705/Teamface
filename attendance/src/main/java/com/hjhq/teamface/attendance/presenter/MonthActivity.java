package com.hjhq.teamface.attendance.presenter;

import android.os.Bundle;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.MonthDataAdapter;
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
@RouteNode(path = "/month_data_detail", desc = "月排行榜")
public class MonthActivity extends ActivityPresenter<DataListDelegate, AttendanceModel> {
    private AttendanceMonthDataBean.DataBean.DataListBean data;
    ArrayList<AttendanceMonthDataBean.EmployeeListBean> employeeList;
    MonthDataAdapter mAdapter;
    EmptyView mEmptyView;


    @Override
    public void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            data = (AttendanceMonthDataBean.DataBean.DataListBean) bundle.getSerializable(Constants.DATA_TAG1);
            if (data != null) {
                employeeList = data.getEmployeeList();
                mAdapter = new MonthDataAdapter(TextUtil.parseInt(data.getType()), employeeList);
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
                }
                viewDelegate.setTitle(name);
            }
        }

    }
}

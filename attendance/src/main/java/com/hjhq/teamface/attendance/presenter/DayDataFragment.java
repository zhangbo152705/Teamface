package com.hjhq.teamface.attendance.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.DayDataDetailAdapter;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceDataFragmentDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.util.ArrayList;
import java.util.List;

public class DayDataFragment extends FragmentPresenter<AttendanceDataFragmentDelegate, AttendanceModel> {
    private DayDataDetailAdapter mAdapter1;
    private int type;


    static DayDataFragment newInstance(int type) {
        DayDataFragment myFragment = new DayDataFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, type);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        this.type = arguments.getInt(Constants.DATA_TAG1);

    }

    @Override
    protected void init() {
        viewDelegate.get(R.id.header1).setVisibility(View.GONE);
        viewDelegate.get(R.id.header2).setVisibility(View.GONE);
        switch (type) {
            case 1:
                //打卡人数

                mAdapter1 = new DayDataDetailAdapter(type, getData());
                viewDelegate.setAdapter(mAdapter1);
                break;
            case 2:
                //未打卡人数
                mAdapter1 = new DayDataDetailAdapter(type, getData());
                viewDelegate.setAdapter(mAdapter1);
                break;

        }

    }

    private List<AttendanceDayDataBean.DataListBean> getData() {
        final FragmentActivity activity = getActivity();
        if (activity instanceof AttendanceNumActivity) {
            return ((AttendanceNumActivity) activity).getData(type);
        }
        return new ArrayList<>();
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();

        viewDelegate.setItemListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, mAdapter1.getData().get(position).getEmployee_id());
                //CommonUtil.startActivtiy(mContext, EmployeeInfoActivity.class, bundle);
                UIRouter.getInstance().openUri(getActivity(), "DDComp://app/employee/info", bundle);

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE2) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

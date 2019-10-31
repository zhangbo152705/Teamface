package com.hjhq.teamface.attendance.views;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.zygote.AppDelegate;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：
 */

public class AttendanceApproveFragmentDelegate extends AppDelegate {

    public RecyclerView module_recycler;
    public RelativeLayout rl_approve_list;


    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_approve_fragment;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        rl_approve_list = get(R.id.rl_approve_list);
        module_recycler = get(R.id.module_recycler);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        module_recycler.setLayoutManager(mGridLayoutManager);
    }


}

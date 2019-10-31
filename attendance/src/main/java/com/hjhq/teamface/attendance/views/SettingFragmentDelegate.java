package com.hjhq.teamface.attendance.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.view.MineGridView;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：
 */

public class SettingFragmentDelegate extends AppDelegate {

    public MineGridView mGridView;
    RecyclerView mRecyclerView;
    public ImageView iv_add_rules;

    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_setting_fragment;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mGridView = get(R.id.gridview);
        mRecyclerView = get(R.id.recycler_view);
        iv_add_rules = get(R.id.iv_add_rules);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setItemListener(RecyclerView.OnItemTouchListener listener) {
        mRecyclerView.addOnItemTouchListener(listener);
    }

}

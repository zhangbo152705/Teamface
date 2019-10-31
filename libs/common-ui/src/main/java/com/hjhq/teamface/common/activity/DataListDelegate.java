package com.hjhq.teamface.common.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;

/**
 * @author Administrator
 * @date 2017/11/9
 * Describe：编辑文本页面
 */

public class DataListDelegate extends AppDelegate {
    private RecyclerView mRecyclerView;
    public RelativeLayout rlCan;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_data_list;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mRecyclerView = get(R.id.rv);
        rlCan = get(R.id.rl_can);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setAdapter(DragItemAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setItemClickListener(SimpleItemClickListener listener) {
        mRecyclerView.addOnItemTouchListener(listener);
    }

    public void addView(View v) {
        rlCan.removeAllViews();
        rlCan.addView(v);
        rlCan.setVisibility(View.VISIBLE);
    }
}

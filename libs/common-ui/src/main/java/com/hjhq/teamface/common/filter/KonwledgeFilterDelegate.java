package com.hjhq.teamface.common.filter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.adapter.KnowledgeCatgAdapter;
import com.hjhq.teamface.common.adapter.KnowledgeFiexdCatgAdapter;

public class KonwledgeFilterDelegate extends AppDelegate {
    public RecyclerView mRecyclerViewFixed;
    private RecyclerView mRecyclerView;

    @Override
    public int getRootLayoutId() {
        //组件显示
        return R.layout.custom_knowledge_filter_fragment;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        get(R.id.rl_title_text).setPadding(0, statusBarHeight, 0, 0);
        mRecyclerViewFixed = get(R.id.rv_fixed);
        mRecyclerView = get(R.id.rv);
        mRecyclerViewFixed.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    public void setAdapter(KnowledgeCatgAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setFixedAdapter(KnowledgeFiexdCatgAdapter adapter) {
        mRecyclerViewFixed.setAdapter(adapter);
    }

}
package com.hjhq.teamface.project.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * 选择项目角色
 * Created by Administrator on 2018/4/16.
 */

public class ProjectRoleDelegate extends AppDelegate {
    public RecyclerView mRecyclerView;
    public SmartRefreshLayout mRefreshLayout;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_activity_role;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle(R.string.project_role);
        setRightMenuTexts(R.color.app_blue, mContext.getString(R.string.confirm));
        mRecyclerView = get(R.id.rv);
        mRefreshLayout = get(R.id.refresh);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
}

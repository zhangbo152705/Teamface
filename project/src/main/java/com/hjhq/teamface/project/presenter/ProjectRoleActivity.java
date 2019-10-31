package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.project.adapter.ProjectRoleAdapter;
import com.hjhq.teamface.project.bean.ProjectRoleBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.ui.ProjectRoleDelegate;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择项目角色
 * Created by Administrator on 2018/4/16.
 */

public class ProjectRoleActivity extends ActivityPresenter<ProjectRoleDelegate, ProjectModel> {
    /**
     * 0 成员 1 访客
     */
    private String selectRole;
    private Long projectId = -1L;
    private String id;
    private ProjectRoleAdapter mProjectRoleAdapter;
    List<ProjectRoleBean.DataBean.DataListBean> dataList = new ArrayList<>();
    private int currentPageNo = 1;//当前页数
    private int pageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    private int totalNum = -1;//总数
    private int state = Constants.NORMAL_STATE;
    private int pageSize = 20;
    private String isManager = "0";

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            selectRole = getIntent().getStringExtra(Constants.DATA_TAG1);
            id = getIntent().getStringExtra(Constants.DATA_TAG2);
            projectId = getIntent().getLongExtra(Constants.DATA_TAG3, -1L);
        }
    }

    @Override
    public void init() {
        mProjectRoleAdapter = new ProjectRoleAdapter(dataList);
        viewDelegate.setAdapter(mProjectRoleAdapter);
        getRoleList();
    }

    private void getRoleList() {
        currentPageNo = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        model.queryRoleList(mContext, projectId, currentPageNo, pageSize, new ProgressSubscriber<ProjectRoleBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(ProjectRoleBean baseBean) {
                super.onNext(baseBean);
                showData(baseBean);
            }
        });
    }

    private void showData(ProjectRoleBean baseBean) {
        viewDelegate.mRefreshLayout.finishLoadMore(true);
        List<ProjectRoleBean.DataBean.DataListBean> newDataList = baseBean.getData().getDataList();
        switch (state) {
            case Constants.NORMAL_STATE:
            case Constants.REFRESH_STATE:
                dataList.clear();
                break;
            case Constants.LOAD_STATE:

                break;
            default:
                break;
        }
        if (newDataList != null && newDataList.size() > 0) {
            dataList.addAll(newDataList);
        }
        mProjectRoleAdapter.notifyDataSetChanged();
        for (int i = 0; i < dataList.size(); i++) {
            final ProjectRoleBean.DataBean.DataListBean bean = dataList.get(i);
            if ((selectRole + "").equals(bean.getId())) {
                dataList.get(i).setCheck(true);
            } else {
                dataList.get(i).setCheck(false);
            }
        }
        mProjectRoleAdapter.notifyDataSetChanged();
        PageInfo pageInfo = baseBean.getData().getPageInfo();
        totalPages = pageInfo.getTotalPages();
        totalNum = pageInfo.getTotalRows();
        pageNo = pageInfo.getPageNum();
        if (totalNum == mProjectRoleAdapter.getData().size()) {
            viewDelegate.mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                for (int i = 0; i < mProjectRoleAdapter.getData().size(); i++) {
                    mProjectRoleAdapter.getData().get(i).setCheck(false);
                }
                mProjectRoleAdapter.getData().get(position).setCheck(true);
                mProjectRoleAdapter.notifyDataSetChanged();
            }
        });
        //监听下拉刷新
        viewDelegate.mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                viewDelegate.mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        viewDelegate.mRefreshLayout.finishRefresh();
                    }
                }, 500);
            }
        });
        viewDelegate.mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mProjectRoleAdapter.getData().size() >= totalNum) {
                    viewDelegate.mRefreshLayout.finishLoadMore();
                    return;
                }
                state = Constants.LOAD_STATE;
                getRoleList();
            }
        });
    }

    private void refreshData() {


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (int i = 0; i < mProjectRoleAdapter.getData().size(); i++) {
            final ProjectRoleBean.DataBean.DataListBean bean = mProjectRoleAdapter.getData().get(i);
            if (bean.isCheck()) {
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, bean.getId());
                intent.putExtra(Constants.DATA_TAG2, id);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.hjhq.teamface.project.ui.task;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;

/**
 * @author Administrator
 * @date 2018/6/21
 */
public class SelectTaskDelegateV2 extends AppDelegate {
    public RecyclerView mRecyclerView;
    public RecyclerView mRvProject;
    public SwipeRefreshLayout swipeRefreshWidget;
    private TextView tvTitle;
    private TextView tvProject;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_activity_select_task_v2;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        /*setTitle(R.string.task);
        setRightMenuTexts(mContext.getString(R.string.confirm));
        setLeftIcon(R.drawable.icon_blue_back);
        setLeftText(R.color.app_blue, "返回");*/
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
            get(R.id.rl_main).setPadding(0, statusBarHeight, 0, 0);
            get(R.id.rl_menu).setPadding(0, statusBarHeight, 0, 0);
        }*/
        swipeRefreshWidget = get(R.id.swipe_refresh_widget);
        swipeRefreshWidget.setColorSchemeResources(R.color.main_green);

        mRecyclerView = get(R.id.recyclerview);
        mRvProject = get(R.id.rv_list);
        tvTitle = get(R.id.title_tv);
        tvProject = get(R.id.tv_project);
        tvTitle.setText("任务");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRvProject.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.view_workbench_empty, null);
        adapter.setEmptyView(emptyView);
    }

    public void setProjectAdapter(BaseQuickAdapter adapter) {
        mRvProject.setAdapter(adapter);
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.view_workbench_empty, null);
        adapter.setEmptyView(emptyView);
    }

    public void setCurrentProjectName(String title) {
        TextUtil.setText(tvProject, title);
    }
}

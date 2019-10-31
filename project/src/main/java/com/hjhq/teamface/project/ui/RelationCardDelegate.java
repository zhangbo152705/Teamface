package com.hjhq.teamface.project.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.RelationTaskAdapter;
import com.hjhq.teamface.project.adapter.TaskCardApter;
import com.hjhq.teamface.project.presenter.filter.FilterFragment;

/**
 * 项目视图
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class RelationCardDelegate extends AppDelegate {
    public TextView tvTask;
    public TextView ivAdd;
    public RecyclerView recycler_view;
    public SwipeRefreshLayout swipe_refresh_layout;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_relation_card_layout;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }


    @Override
    public void initWidget() {
        super.initWidget();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        get(R.id.fl_tool_bar).setPadding(0, statusBarHeight, 0, 0);
        ivAdd = get(R.id.iv_add_bar);
        tvTask = get(R.id.tv_task);
        recycler_view = get(R.id.card_recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipe_refresh_layout = get(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setColorSchemeResources(R.color.main_green);
    }

    /**
     * 设置适配器
     */
    public void setAdapter(RelationTaskAdapter adapter){
        recycler_view.setAdapter(adapter);
    }


    /**
     * 设置title
     */
    public void setTitle(String title){
        tvTask.setText(title);
    }
}


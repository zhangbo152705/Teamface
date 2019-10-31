package com.hjhq.teamface.project.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.basis.zygote.FragmentAdapter;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.TaskCardApter;
import com.hjhq.teamface.project.presenter.filter.FilterFragment;

import java.util.List;

/**
 * 项目视图
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class TaskCardDelegate extends AppDelegate {
    public TextView tvTask;
    public DrawerLayout mDrawerLayout;
    public ImageView ivFiltrate;
    public ImageView ivAdd;
    public RecyclerView recycler_view;
    public SwipeRefreshLayout swipe_refresh_layout;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_task_card_layout;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    /**
     * 这里有侧滑控件，所以布局不能用父类的实现
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public void create(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int rootLayoutId = getRootLayoutId();
        rootLayout = (ViewGroup) inflater.inflate(R.layout.drawer_layout, container, false);
        View rootView = inflater.inflate(rootLayoutId, container, false);
        rootLayout.addView(rootView, 0);
        mDrawerLayout = get(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void initWidget() {
        super.initWidget();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        get(R.id.fl_tool_bar).setPadding(0, statusBarHeight, 0, 0);
        ivFiltrate = get(R.id.iv_filtrate_bar);
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
    public void setAdapter(TaskCardApter adapter){
        recycler_view.setAdapter(adapter);
    }

    /**
     * 初始化筛选控件
     */
    public void initFilter(long projectId,int queryType) {
        Fragment fragment = FilterFragment.newInstance(queryType, projectId);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer_content, fragment).commit();
    }

    /**
     * 打开筛选
     */
    public void openDrawer() {

        mDrawerLayout.openDrawer(get(R.id.drawer_content));
    }

    /**
     * 关闭筛选
     *
     * @return
     */
    public boolean closeDrawer() {
        DrawerLayout mDrawerLayout = get(R.id.drawer_layout);
        View view = get(R.id.drawer_content);
        if (view.getVisibility() == View.VISIBLE) {
            mDrawerLayout.closeDrawer(view);
            return true;
        }
        return false;
    }

    /**
     * 隐藏增加按钮
     */
    public void hideAddBar(boolean flag){
        if(flag){
            ivAdd.setVisibility(View.GONE);
        }else {
            ivAdd.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 设置title
     */
    public void setTitle(String title){
        tvTask.setText(title);
    }
}


package com.hjhq.teamface.project.ui.filter;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;

/**
 * @author Administrator
 * @date 2018/4/12
 */

public class TaskSearchDelegate extends AppDelegate {
    public RecyclerView mRecyclerView;
    public DrawerLayout mDrawerLayout;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_activity_task_search_and_filter;
    }

    @Override
    public boolean isToolBar() {
        return true;
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
        LinearLayout rootLayout2 = (LinearLayout) inflater.inflate(R.layout.root_layout, container, false);
        View toolbar = inflater.inflate(R.layout.toolbar_comment, container, false);
        rootLayout2.addView(toolbar);
        rootLayout2.addView(rootView);
        rootLayout.addView(rootLayout2, 0);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        get(R.id.ll_tool_bar).setPadding(0, statusBarHeight, 0, 0);
        Toolbar toolbar = getToolbar();
        toolbar.setTitleTextColor(ColorUtils.resToColor(mContext, R.color.title_bar_txt_color));
        setRightMenuIcons(R.drawable.project_icon_filtrate);
        mDrawerLayout = get(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        setLeftIcon(R.drawable.icon_blue_back);
        setLeftText(R.color.app_blue, "返回");
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
}

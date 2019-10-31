package com.hjhq.teamface.memo.view;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.memo.R;


/**
 * Created by Administrator on 2018/3/21.
 * Describe：
 */

public class KonwledgeListDelegate extends AppDelegate {

    @Override
    public int getRootLayoutId() {
        return R.layout.memo_knowledge_activity_main;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

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
        View toolbar = get(R.id.ll_tool_bar);
        toolbar.setPadding(0, statusBarHeight, 0, 0);
        setTitle("知识库(全部)");
        setRightMenuIcons(R.drawable.memo_switch_view_icon, R.drawable.memo_menu_icon);
        setLeftIcon(R.drawable.icon_blue_back);
        setLeftText(R.color.app_blue, "返回");
    }

    /**
     * 打开筛选
     */
    public void openDrawer() {
        DrawerLayout mDrawerLayout = get(R.id.drawer_layout);
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

    public boolean isDrawerOpen() {
        DrawerLayout mDrawerLayout = get(R.id.drawer_layout);
        View view = get(R.id.drawer_content);
        return mDrawerLayout.isDrawerOpen(view);
    }
}

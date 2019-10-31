package com.hjhq.teamface.project.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.basis.zygote.FragmentAdapter;
import com.hjhq.teamface.common.view.NoScrollViewPager;
import com.hjhq.teamface.project.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2018/4/12
 */

public class ProjectDetailDelegate extends AppDelegate {
    public NoScrollViewPager mViewPager;
    List<ImageView> imageViews = new ArrayList<>();
    List<TextView> textViews = new ArrayList<>();
    public DrawerLayout mDrawerLayout;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_activity_detail;
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
        setRightMenuIcons(R.drawable.project_icon_star, R.drawable.project_icon_unstar,
                R.drawable.project_icon_filtrate, R.drawable.project_icon_setting,
                R.drawable.icon_plus_gray, R.drawable.icon_plus_gray);
        showMenu(1, 2, 3);
        mDrawerLayout = get(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        imageViews.add(get(R.id.iv_task));
        imageViews.add(get(R.id.iv_share));
        imageViews.add(get(R.id.iv_library));
        imageViews.get(0).setSelected(true);
        textViews.add(get(R.id.tv_task));
        textViews.add(get(R.id.tv_share));
        textViews.add(get(R.id.tv_library));
        mViewPager = get(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(4);
        //禁止ViewPager滑动，以免造成逻辑混乱
        mViewPager.setNoScroll(true);
        setCurrentItem(0);
        setLeftIcon(R.drawable.icon_blue_back);
        setLeftText(R.color.app_blue, "返回");
    }

    public void setCurrentItem(int i) {
        if (i == 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        mViewPager.setCurrentItem(i);
        for (ImageView image : imageViews) {
            if (imageViews.get(i) == image) {
                image.setSelected(true);
            } else {
                image.setSelected(false);
            }
        }
        for (TextView text : textViews) {
            if (textViews.get(i) == text) {
                text.setTextColor(ColorUtils.resToColor(mContext, R.color.app_blue));
            } else {
                text.setTextColor(ColorUtils.resToColor(mContext, R.color.gray_99));
            }
        }
    }

    public void setAdapter(FragmentAdapter mAdapter) {
        mViewPager.setAdapter(mAdapter);
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
        SoftKeyboardUtils.hide(getActivity());
        DrawerLayout mDrawerLayout = get(R.id.drawer_layout);
        View view = get(R.id.drawer_content);
        if (view.getVisibility() == View.VISIBLE) {
            mDrawerLayout.closeDrawer(view);
            return true;
        }
        return false;
    }
}

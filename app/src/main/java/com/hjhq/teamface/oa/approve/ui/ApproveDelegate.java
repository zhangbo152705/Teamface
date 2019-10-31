package com.hjhq.teamface.oa.approve.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.DeviceUtils;
import com.hjhq.teamface.basis.util.device.ScreenUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.basis.zygote.FragmentAdapter;
import com.hjhq.teamface.common.view.ScaleTransitionPagerTitleView;
import com.hjhq.teamface.basis.bean.ApproveUnReadCountResponseBean;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * 审批 视图代理类
 * Created by lx on 2017/8/31.
 */

public class ApproveDelegate extends AppDelegate {
    ViewPager mViewPager;
    MagicIndicator mMagicIndicator;
    List<View> viewList = new ArrayList<>();
    ScaleTransitionPagerTitleView scaleTransitionPagerTitleView;


    private List<Fragment> fragments = new ArrayList<>(4);
    private FragmentAdapter mAdapter;

    /**
     * 导航栏名称
     */
    private String[] BUTTOM_NAME;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_approve;
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
        View toolbar = get(R.id.ll_tool_bar);
        toolbar.setPadding(0, statusBarHeight, 0, 0);
        viewList.add(get(R.id.iv_catg1));
        viewList.add(get(R.id.iv_catg2));
        viewList.add(get(R.id.iv_catg3));
        viewList.add(get(R.id.iv_catg4));
        viewList.get(0).setSelected(true);

        setTitle("审批");
        setRightMenuIcons(R.drawable.icon_filtrate, R.drawable.add_company_icon);
        BUTTOM_NAME = mContext.getResources().getStringArray(R.array.approve_buttom_name);

        mViewPager = get(R.id.m_view_pager);
        mMagicIndicator = get(R.id.m_magic_indicator);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < viewList.size(); i++) {
                    viewList.get(i).setSelected(false);
                }
                viewList.get(position).setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initNavigator();

        ImageView ivLeft = toolbar.findViewById(R.id.iv_left);
        TextView tvLeft = toolbar.findViewById(R.id.tv_left);
        ivLeft.setImageResource(R.drawable.icon_blue_back);
        tvLeft.setText("返回");
        tvLeft.setTextColor(getActivity().getResources().getColor(R.color.app_blue));
        toolbar.findViewById(R.id.rl_toolbar_back).setOnClickListener(v -> getActivity().finish());
        getToolbar().setNavigationIcon(null);
    }

    /**
     * 初始化导航
     */
    private void initNavigator() {
        View view = get(R.id.drawer_content);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (ScreenUtils.getScreenWidth(mContext) / 5 * 4);
        view.setLayoutParams(layoutParams);

        mAdapter = new FragmentAdapter(((ActivityPresenter) mContext).getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(5);

        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(commonNavigatorAdapter);
        commonNavigator.setAdjustMode(true);
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    /**
     * 设置fragment
     *
     * @param list
     */
    public void setFragments(List<Fragment> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        fragments.clear();
        fragments.addAll(list);
        mAdapter.notifyDataSetChanged();
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

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (listener == null) {
            return;
        }
        mViewPager.addOnPageChangeListener(listener);
    }


    CommonNavigatorAdapter commonNavigatorAdapter = new CommonNavigatorAdapter() {

        @Override
        public int getCount() {
            return BUTTOM_NAME == null ? 0 : BUTTOM_NAME.length;
        }

        @Override
        public IPagerTitleView getTitleView(Context context, final int index) {
            scaleTransitionPagerTitleView = new ScaleTransitionPagerTitleView(context);
            scaleTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(mContext, R.color.gray_69));
            scaleTransitionPagerTitleView.setGravity(Gravity.CENTER);
            scaleTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(mContext, R.color.main_green));
            scaleTransitionPagerTitleView.setTextSize(18);
            scaleTransitionPagerTitleView.setText(BUTTOM_NAME[index]);
            scaleTransitionPagerTitleView.setOnClickListener(view -> mViewPager.setCurrentItem(index));
            scaleTransitionPagerTitleView.setOnSelectChangeListener(new ScaleTransitionPagerTitleView.OnSelectChangeListener() {
                @Override
                public void onEnter(int index) {

                }

                @Override
                public void onLeave(int index) {

                }
            });
            return scaleTransitionPagerTitleView;
        }

        @Override
        public IPagerIndicator getIndicator(Context context) {
            LinePagerIndicator indicator = new LinePagerIndicator(context);
            indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
            indicator.setLineWidth(DeviceUtils.dpToPixel(mContext, 20));
            indicator.setLineHeight(DeviceUtils.dpToPixel(mContext, 3));
            indicator.setYOffset(DeviceUtils.dpToPixel(mContext, 0));
            indicator.setColors(ContextCompat.getColor(mContext, R.color.main_green));
            return indicator;
        }

    };

    /**
     * 设置未读数量
     *
     * @param data
     */
    public void setUnReadCount(ApproveUnReadCountResponseBean.DataBean data) {
        if (data == null) {
            return;
        }
        TextView tvPendApprove = get(R.id.tv_pend_approve);
        TextView tvCopyToMe = get(R.id.tv_copy_to_me);
        int copyCount = TextUtil.parseInt(data.getCopyCount());
        int treatCount = TextUtil.parseInt(data.getTreatCount());
        if (treatCount > 0) {
            tvPendApprove.setVisibility(View.VISIBLE);
            if (treatCount > 1000) {
                TextUtil.setText(tvPendApprove, "999+");
            } else {
                TextUtil.setText(tvPendApprove, treatCount + "");

            }
        } else {
            tvPendApprove.setVisibility(View.GONE);
        }
        if (copyCount > 0) {
            tvCopyToMe.setVisibility(View.VISIBLE);
            if (copyCount > 1000) {

                TextUtil.setText(tvCopyToMe, "999+");
            } else {
                TextUtil.setText(tvCopyToMe, copyCount + "");

            }
        } else {
            tvCopyToMe.setVisibility(View.GONE);
        }
    }
}

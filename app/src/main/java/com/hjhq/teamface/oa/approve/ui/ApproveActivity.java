package com.hjhq.teamface.oa.approve.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.bean.ApproveUnReadCountResponseBean;
import com.hjhq.teamface.util.CommonUtil;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 审批
 *
 * @author lx
 * @date 2017/616
 */


@RouteNode(path = "/approve/main", desc = "审批首页")
public class ApproveActivity extends ActivityPresenter<ApproveDelegate, ApproveModel> {
    private ApproveFragment approveFragment1;
    private ApproveFragment approveFragment2;
    private ApproveFragment approveFragment3;
    private ApproveFragment approveFragment4;
    private ApproveFilterFragment filterFragment;
    private List<Fragment> fragments = new ArrayList<>();
    private int currentType = ApproveFragment.TAG1;
    private int index = 0;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            index = extras.getInt(Constants.DATA_TAG1, 0);
        }
    }

    @Override
    public void init() {

        fragments.add(approveFragment1 = ApproveFragment.newInstance(ApproveFragment.TAG1));
        fragments.add(approveFragment2 = ApproveFragment.newInstance(ApproveFragment.TAG2));
        fragments.add(approveFragment3 = ApproveFragment.newInstance(ApproveFragment.TAG3));
        fragments.add(approveFragment4 = ApproveFragment.newInstance(ApproveFragment.TAG4));
        viewDelegate.setFragments(fragments);
        if (index != 0 && index < fragments.size()) {
            pageChange(index);
        }
        initFilter();
        getUnReadCount();
    }

    /**
     * 得到未读数量
     */
    private void getUnReadCount() {
        model.queryApprovalCount(this, new ProgressSubscriber<ApproveUnReadCountResponseBean>(this, false) {
            @Override
            public void onNext(ApproveUnReadCountResponseBean unReadCountResponseBean) {
                super.onNext(unReadCountResponseBean);
                ApproveUnReadCountResponseBean.DataBean data = unReadCountResponseBean.getData();
                viewDelegate.setUnReadCount(data);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 初始化筛选控件
     */
    public void initFilter() {
        filterFragment = new ApproveFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, currentType);
        filterFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer_content, filterFragment).commit();
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int cur = currentType;
                currentType = position;
                filterFragment.setType(position);
                if (cur != position && ((ApproveFragment) fragments.get(cur)).filterDataNotNull()) {
                    ((ApproveFragment) fragments.get(cur)).refreshData();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewDelegate.get(R.id.iv_catg1).setOnClickListener(v -> {
            pageChange(0);
        });
        viewDelegate.get(R.id.iv_catg2).setOnClickListener(v -> {
            pageChange(1);
        });
        viewDelegate.get(R.id.iv_catg3).setOnClickListener(v -> {
            pageChange(2);
        });
        viewDelegate.get(R.id.iv_catg4).setOnClickListener(v -> {
            pageChange(3);
        });
    }

    private void pageChange(int index) {
        viewDelegate.mViewPager.setCurrentItem(index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                viewDelegate.openDrawer();
                break;
            case 1:
                CommonUtil.startActivtiy(this, ApproveTypeActivity.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!viewDelegate.closeDrawer()) {
            super.onBackPressed();
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean messageBean) {
        String tag = messageBean.getTag();
        if (ApproveConstants.APPROVAL_FILTER_DATA_OK.equals(tag)) {
            viewDelegate.closeDrawer();
        }
        if (ApproveConstants.APPROVAL_REFRESH_UNREAD_NUMBER.equals(tag)) {
            getUnReadCount();
        }
    }

}

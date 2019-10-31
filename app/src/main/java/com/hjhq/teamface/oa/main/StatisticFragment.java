package com.hjhq.teamface.oa.main;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.BaseFragment;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.AppConstant;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.common.view.loading.LoadingView;
import com.hjhq.teamface.componentservice.statistic.StatisticService;
import com.luojilab.component.componentlib.router.Router;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import butterknife.Bind;

/**
 * 数据分析
 *
 * @author Administrator
 * @date 2018/4/27
 */

public class StatisticFragment extends BaseFragment {
    @Bind(R.id.ll_statistic_root)
    LinearLayout llRoot;
    @Bind(R.id.fl_layout)
    FrameLayout flCan;
    @Bind(R.id.refresh_statistic)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.sv_web)
    ScrollView svWeb;
    @Bind(R.id.fl_loading)
    FrameLayout flLoading;
    @Bind(R.id.lv)
    LoadingView lvLoading;
    @Bind(R.id.rl_toolbar_back)
    RelativeLayout rlBack;
    StatisticService service;
    WebView webView;

    @Override
    protected int getContentView() {
        return R.layout.fragment_statistics;
    }


    @Override
    protected void initView(View view) {
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        llRoot.setPadding(0, statusBarHeight, 0, 0);
        initData();
    }

    @Override
    protected void initData() {
        service = (StatisticService) Router.getInstance().getService(StatisticService.class.getSimpleName());
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_layout, service.getChart()).commit();
    }

    @Override
    protected void setListener() {
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (webView != null) {
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.evaluateJavascript("javascript:refresh()", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    mRefreshLayout.finishRefresh();
                                }
                            });
                        }
                    }, 100);
                }
                mRefreshLayout.finishRefresh(2000);
            }
        });
        setOnClicks(rlBack);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_toolbar_back) {
            EventBusUtils.sendEvent(new MessageBean(0, AppConstant.HIDE_STATISTIC, null));
        }

    }

    public void setWebView(View v) {
        service.setWebView(v);
    }

    public View getWebView2() {
        WebView webView = (WebView) service.getWebView2();
        webView.addJavascriptInterface(((MainActivity) getActivity()), "android");
        return webView;
    }

    public View getWebView() {
        WebView webView = (WebView) service.getWebView();
        return webView;
    }


    public void closeLoading() {
        lvLoading.stopAnim();
        flLoading.post(new Runnable() {
            @Override
            public void run() {
                flLoading.setVisibility(View.GONE);
            }
        });
    }
}

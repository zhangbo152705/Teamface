package com.hjhq.teamface.componentservice.statistic;

import android.support.v4.app.Fragment;
import android.view.View;


/**
 * 统计
 * Created by Administrator on 2018/3/26.
 */

public interface StatisticService {
    /**
     * 得到报表
     */
    Fragment getReport();

    /**
     * 得到仪表盘
     */
    Fragment getChart();

    /**
     * 得到WebView
     */
    View getWebView();

    /**
     * 得到WebView
     */
    View getWebView2();

    /**
     * 丢回WebView
     */
    void setWebView(View view);
}

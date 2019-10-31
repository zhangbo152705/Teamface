package com.hjhq.teamface.statistic.serviceimpl;

import android.support.v4.app.Fragment;
import android.view.View;

import com.hjhq.teamface.componentservice.statistic.StatisticService;
import com.hjhq.teamface.statistic.ui.ChartTempFragment;
import com.hjhq.teamface.statistic.ui.ReportTempFragment;

/**
 * 自定义组件对外接口实现
 *
 * @author Administrator
 * @date 2018/3/26
 */

public class StatisticServiceImpl implements StatisticService {
    ChartTempFragment mChartTempFragment;

    @Override
    public Fragment getReport() {
        return new ReportTempFragment();
    }

    @Override
    public Fragment getChart() {
        if (mChartTempFragment == null) {
            mChartTempFragment = new ChartTempFragment();
        }
        return mChartTempFragment;
    }

    @Override
    public View getWebView() {
        if (mChartTempFragment != null) {
            return mChartTempFragment.getWebView();
        }
        return null;

    }

    @Override
    public View getWebView2() {
        if (mChartTempFragment != null) {
            return mChartTempFragment.getWebView2();
        }
        return null;
    }

    @Override
    public void setWebView(View view) {
        if (mChartTempFragment != null) {
            mChartTempFragment.setWebView(view);
        }

    }

}

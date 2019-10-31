package com.hjhq.teamface.statistic.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.log.LogUtil;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.statistic.bean.ChartDataResultBean;
import com.hjhq.teamface.statistic.bean.MenuBean;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.Serializable;
import java.util.List;

/**
 * 图标列表
 *
 * @author lx
 * @date 2017/8/31
 */
public class ChartTempFragment extends FragmentPresenter<ChartTempDelegate, StatisticsModel> {
    final static int REQUEST_CHART_SORT_CODE = 0X6578;
    private List<MenuBean> dataList;
    private boolean isFinish;
    private String json;
    private String menuId;

    @Override
    public void init() {
       /* //获取缓存
        // json = SPHelper.getChartCache();
        if (!TextUtils.isEmpty(json)) {

            handResult();
        }
        // loadTempData();*/
    }

    /**
     * 获取详情数据
     */
    private void getDetail() {
        if (TextUtil.isEmpty(menuId) || "0".equals(menuId)) {
            return;
        }
        model.getChartDataDetail((RxAppCompatActivity) getActivity(), menuId, new ProgressSubscriber<ChartDataResultBean>(getActivity()) {
            @Override
            public void onNext(ChartDataResultBean baseBean) {
                super.onNext(baseBean);
                json = baseBean.getData();
                LogUtil.d("-----------" + json);

                handResult();
                //缓存数据
                // SPHelper.setChartCache(json);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);

            }
        });
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
       /* viewDelegate.setWebClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isFinish = true;
                handResult();
            }

        });
        viewDelegate.get(R.id.ll_sort).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                viewDelegate.clearCache();
                return true;
            }
        });*/
    }

    /**
     * 调用js
     */
    private void handResult() {
       /* if (isFinish && json != null) {
            viewDelegate.evaluateJavascript(json.toString());
        }*/
    }

    /**
     * 分类选择 点击
     */
    protected void sortClick() {
        if (CollectionUtils.isEmpty(dataList)) {
            ToastUtils.showError(getActivity(), "仪表盘数据为空");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DATA_TAG1, (Serializable) dataList);
        bundle.putInt(Constants.DATA_TAG2, 0);
        CommonUtil.startActivtiyForResult(getActivity(), SelectSortActivity.class, REQUEST_CHART_SORT_CODE, bundle);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_CHART_SORT_CODE && resultCode == Activity.RESULT_OK) {
            dataList = (List<MenuBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            Observable.from(dataList).filter(MenuBean::isCheck).subscribe(menuBean -> {
                menuId = menuBean.getId();
                viewDelegate.setSortInfo(menuBean.getName());
                //getDetail();
            });
        }*/
    }

    /**
     * 获取且移出
     *
     * @return
     */
    public View getWebView() {
        if (viewDelegate != null) {

            return viewDelegate.getWebView();

        }
        return null;
    }

    /**
     * 获取不移出
     *
     * @return
     */
    public View getWebView2() {
        if (viewDelegate != null) {

            return viewDelegate.getWebView2();
        }
        return null;
    }

    public void setWebView(View view) {
        if (viewDelegate == null) {
            return;
        }
        viewDelegate.setWebView(view);
    }
}

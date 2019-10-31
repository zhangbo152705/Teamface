package com.hjhq.teamface.statistic.ui;

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ZoomButtonsController;

import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.utils.AESUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.statistic.R;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 图表 视图代理类
 *
 * @author lx
 * @date 2017/8/31
 */

public class ChartTempDelegate extends AppDelegate {
    public com.tencent.smtt.sdk.WebView mWebView;
    private CoordinatorLayout flWeb;

    /**
     * 空数据
     */


    @Override
    public int getRootLayoutId() {
        return R.layout.statistic_activity_chart_temp;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }


    @Override
    public void initWidget() {
        super.initWidget();
        mWebView = get(R.id.web_view);
        mWebView.addJavascriptInterface(getActivity(), "android");

        flWeb = get(R.id.fl_web);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
        //隐藏水平滚动条
        mWebView.setHorizontalScrollBarEnabled(false);
        WebSettings settings = mWebView.getSettings();
        //缓存网页
        if (!Constants.IS_DEBUG) {
        }

        /*if (SPHelper.isCacheBefore()) {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }*/
        //File file = JYFileHelper.getFileDir(getActivity(), Constants.PATH_DOWNLOAD);
        // settings.setAppCachePath(file.getAbsolutePath());
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        //允许js
        settings.setJavaScriptEnabled(true);
        // 显示放大缩小 controler
        settings.setBuiltInZoomControls(true);
        // 可以缩放
        settings.setSupportZoom(true);
        // 默认缩放
        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        // 设置图片放大缩小
        settings.setUseWideViewPort(true);
        // 取消放大缩小按钮显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(mWebView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);

            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                SPHelper.setCacheBefore(true);
                setToken();
            }

        });
        mWebView.loadUrl(Constants.STATISTIC_CHART_URL);

    }

    private void setToken() {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("TOKEN", SPHelper.getToken());
                    //jo.editPut("TOKEN", "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIyNiIsInN1YiI6IjI1IiwiYXVkIjoiMyIsImlzcyI6IjEwMDY2IiwiaWF0IjoxNTQyNzY3NTk3fQ.ulFlTmrjPiTB_lX53wZ5hWwwB8qVBpekl4sv9FhfYCc");
                    jo.put("FLAG", 1);
                    jo.put("SIGN", AESUtil.getSign());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mWebView.evaluateJavascript("javascript:getToken(" + jo + ")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
        });
    }


    public void clearCache() {
        if (Constants.IS_DEBUG) {
            mWebView.clearCache(true);
            mWebView.clearFormData();
            mWebView.clearHistory();
            mWebView.clearMatches();
            mWebView.loadUrl(Constants.STATISTIC_CHART_URL);
            ToastUtils.showSuccess(getActivity(), "清除成功");
        }

    }

    public View getWebView() {
        View v = flWeb.getChildAt(0);
        flWeb.removeViewAt(0);
        return v;

    }

    public View getWebView2() {
        View v = flWeb.getChildAt(0);
        return v;

    }

    public void setWebView(View view) {
        flWeb.addView(view, 0);
    }
}

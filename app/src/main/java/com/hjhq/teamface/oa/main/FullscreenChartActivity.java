package com.hjhq.teamface.oa.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2018/11/9.
 * Describe：
 */

public class FullscreenChartActivity extends RxAppCompatActivity {
    private FrameLayout flCan;
    private String chartType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_chart_layout);
        EventBusUtils.register(this);
        if (getIntent().getExtras() != null) {
            chartType = getIntent().getStringExtra(Constants.DATA_TAG1);
        }

        flCan = findViewById(R.id.fl_root);
        View view1 = MainActivity.getView();

        if (view1 == null) {
            finish();
            return;
        }

        if (view1 instanceof WebView) {
            WebView view = (WebView) view1;
            MainActivity.canOpenFullscreenMode = false;
            if (view == null) {
                finish();
                return;
            }
            WebSettings settings = view.getSettings();
            settings.setSupportZoom(true);
            settings.setUseWideViewPort(true);
            settings.setBuiltInZoomControls(false);
            flCan.addView(view);
            //view.reload();
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.evaluateJavascript("javascript:stateReady()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
            });
        }
        findViewById(R.id.rl_close).setVisibility(View.GONE);
        findViewById(R.id.rl_close).setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        final View view1 = flCan.getChildAt(0);
        if (view1 instanceof WebView) {
            WebView view = (WebView) view1;
            WebSettings settings = view.getSettings();
            settings.setSupportZoom(false);
            settings.setUseWideViewPort(false);
            settings.setBuiltInZoomControls(false);
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.evaluateJavascript("javascript:exitFullScreen()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
            });
            flCan.removeAllViews();
            MainActivity.setView(view);
            finish();
        } else {
            finish();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean bean) {
        if (bean != null && MsgConstant.QUIT_FULLSCREEN_MODE.equals(bean.getTag())) {
            onBackPressed();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.canOpenFullscreenMode = true;
    }

    /* @Override
    protected int getContentView() {
        return R.layout.activity_fullscreen_chart_layout;
    }

    @Override
    protected void initView() {
        mFlData = findViewById(R.id.fl_root);
        WebView view = (WebView) MainActivity.getView();
        mFlData.addView(view);
        view.reload();
    }

    @Override
    protected void setListener() {
        findViewById(R.id.iv_close).setOnClickListener(v -> onBackPressed());

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        final View childAt = mFlData.getChildAt(0);
        mFlData.removeAllViews();
        MainActivity.setView(childAt);
        finish();
    }*/
}

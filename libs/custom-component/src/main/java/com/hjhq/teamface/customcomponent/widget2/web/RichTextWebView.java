package com.hjhq.teamface.customcomponent.widget2.web;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.TextWebView;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;

/**
 * 富文本
 * Created by Administrator on 2018/7/10.
 */

public class RichTextWebView extends BaseView implements ActivityPresenter.OnActivityResult {
    public TextView tvTitle;
    private com.tencent.smtt.sdk.WebView mWebView;
    private TextWebView textWebView;
    private String content = "";
    private View flEdit;
    private View tvLoading;
    private int mWebViewHeight;
    private LinearLayout llContent;
    private RelativeLayout rlWeb;
    private ViewGroup.LayoutParams webviewLayoutParams;
    private ViewGroup.LayoutParams flLayoutParams;
    private View.OnClickListener listener;
    private boolean flag =false;
    private View bottom_line;

    public RichTextWebView(CustomBean bean) {
        super(bean);
        initTextWeb();
    }

    public RichTextWebView(CustomBean bean,boolean flag) {
        super(bean);
        initTextWeb();
        this.flag = flag;
    }

    public void initTextWeb(){
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
            //关联映射
            RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
            //关联映射
            RxManager.$(aHashCode).on(keyName, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
    }


    public void setContent(Object o) {
        /*if (TextUtils.isEmpty(o + "")) {
            setData("<p></p>");
        } else {
            setData2(o);
        }*/

        setData2(o);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        if ("0".equals(structure)) {
            mView = View.inflate(activity, R.layout.custom_item_vertical_web_view, null);
        } else {
            mView = View.inflate(activity, R.layout.custom_item_vertical_web_view_row, null);
        }
        tvTitle = mView.findViewById(R.id.tv_title);
        flEdit = mView.findViewById(R.id.fl_edit);
        tvLoading = mView.findViewById(R.id.tv_loading);
        rlWeb = mView.findViewById(R.id.rl_web);

        llContent = mView.findViewById(R.id.ll_input);
        // textWebView = new TextWebView(activity);
        textWebView = mView.findViewById(R.id.text_web_view);
        // rlWeb.addView(textWebView, 0);
        textWebView.setName(bean.getName());
        mWebView = textWebView.getWebView();
        mWebView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
       /* mWebView.setVisibility(View.GONE);*/
        textWebView.setName(bean.getName());
        parent.addView(mView);
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editContent();
            }
        };
        initView();
        if ("0".equals(terminalApp)) {
            mView.setVisibility(View.GONE);
        }
        bottom_line = mView.findViewById(R.id.bottom_line);
        if (flag){
            tvTitle.setVisibility(View.GONE);
            if (bottom_line != null){
                bottom_line.setVisibility(View.GONE);
            }
        }
    }

    private void initView() {
        if (CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvTitle.setTextColor(ColorUtils.hexToColor("#B1B5BB"));
        }
        webviewLayoutParams = mWebView.getLayoutParams();
        flLayoutParams = flEdit.getLayoutParams();
        textWebView.loadUrl(1, Constants.EMAIL_DETAIL_URL);
        /*RxManager.$(aHashCode).on(bean.getName(), o -> {
            mWebViewHeight = ((Integer) o);
            mWebViewHeight = mWebView.getContentHeight();
            tvLoading.setVisibility(View.GONE);
            mWebView.postDelayed(() -> {
                mWebView.setVisibility(View.VISIBLE);
                *//*mWebViewHeight = mWebView.getView().getHeight();
                flLayoutParams.height = rlWeb.getHeight();*//*
                flLayoutParams.height = mWebViewHeight;
//                webviewLayoutParams.height = mWebViewHeight;
//                mWebView.setLayoutParams(webviewLayoutParams);
                if (flEdit.getVisibility() == View.VISIBLE) {
                    flLayoutParams.height = rlWeb.getHeight();
                    flEdit.setLayoutParams(flLayoutParams);
                }
            }, 10);
        });*/

        setTitle(tvTitle, title);
        if (isDetailState()) {
            setWebContent(bean.getValue());
            //llContent.setBackgroundResource(R.drawable.diy_bg);
            flEdit.setVisibility(View.GONE);
            textWebView.setFocusable(false);
            rlWeb.setFocusable(false);
        } else {
            if (state == CustomConstants.ADD_STATE) {
                CustomBean.FieldBean field = bean.getField();
                if (field != null) {
                    String defaultValue = field.getDefaultValue();
                    setWebContent(defaultValue);
                }
            } else {
                setWebContent(bean.getValue());
                setData2(content);
                flEdit.setVisibility(View.VISIBLE);
            }
            if (CustomConstants.FIELD_READ.equals(fieldControl)) {
                flEdit.setOnClickListener(v -> ToastUtils.showError(getContext(), "只读属性不可更改"));
            } else {
                flEdit.setOnClickListener(v -> {
                    //清除焦点
                    RxManager.$(aHashCode).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, "");
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.DATA_TAG1, title);
                    bundle.putString(Constants.DATA_TAG2, content);

                    ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
                    CommonUtil.startActivtiyForResult(getContext(), RichTextEditActivity.class, code, bundle);
                });
              /*  ((ViewGroup) textWebView.getWebView().getParent()).setOnClickListener(v -> {
                    //清除焦点
                    editContent();
                });*/
                // mView.setOnClickListener(listener);
            }
        }


        textWebView.setOnStateChanListener(new TextWebView.OnStateChangeListener() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setData2(RichTextWebView.this.content);
                //   rlWeb.addView(textWebView, 0);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("error", "code=" + error.getErrorCode() + "Description=" + error.getDescription().toString());
            }


        });
        textWebView.setObject(mView.getContext());
        mWebView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (flEdit.getVisibility() == View.VISIBLE) {
                    flLayoutParams.height = rlWeb.getHeight();
                    flEdit.setLayoutParams(flLayoutParams);
                }
            }
        });

    }

    /**
     * 进入编辑界面
     */
    private void editContent() {
        RxManager.$(aHashCode).post(CustomConstants.MESSAGE_CLEAR_FOCUS_CODE, "");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, title);
        bundle.putString(Constants.DATA_TAG2, content);
        ((ActivityPresenter) getContext()).setOnActivityResult(code, this);
        CommonUtil.startActivtiyForResult(getContext(), RichTextEditActivity.class, code, bundle);
    }

    /**
     * 当网页加载完成后设置数据
     *
     * @param content
     */
    void setWebContent(Object content) {
        if (content == null) {
            content = "";
        }
        this.content = content + "";

    }

    protected void setData2(Object value) {
        if (value == null || TextUtils.isEmpty(value + "")) {

            value = "";
            if (isDetailState()) {
                value = "未填写";
            }
        }
        textWebView.setWebText(bean.getName(), value + "");
        if (TextUtils.isEmpty(Html.fromHtml(value + ""))) {
            tvLoading.setVisibility(View.GONE);
        }


    }

    @Override
    protected void setData(Object value) {
        /*if (value == null) {
            value = "";
        }
        textWebView.setWebText(bean.getName(), value + "");*/
        Log.e("RichTextWebview","value"+JSONObject.toJSONString(value));
        setData2(value);//zzh:富文本增加关联映射和联动
    }

    @Override
    public void put(JSONObject jsonObj) {
        jsonObj.put(keyName, content);
    }

    @Override
    public boolean checkNull() {
        return TextUtil.isEmpty(content);
    }

    @Override
    public boolean formatCheck() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code && resultCode == Activity.RESULT_OK) {
            content = data.getStringExtra(Constants.DATA_TAG1);
            //textWebView.setWebText(content);
            setData2(content);
        }
    }

    /**
     * 刷新WebView高度
     */
    public class RefreshHeightRunnable implements Runnable {
        int height;
        long time;

        public RefreshHeightRunnable(int height, long time) {
            this.height = height;
            this.time = time;
        }

        @Override
        public void run() {
            mWebViewHeight = (int) (mWebView.getContentHeight() * 1.2);
            webviewLayoutParams.height = mWebViewHeight;
            mWebView.setLayoutParams(webviewLayoutParams);
            flLayoutParams.height = mWebViewHeight;
            flEdit.setLayoutParams(flLayoutParams);
            Log.e("WebView", title + "     高度2==" + textWebView.getWebView().getContentHeight());
            if (height != mWebViewHeight) {
                webviewLayoutParams.height = mWebViewHeight;
                mWebView.setLayoutParams(webviewLayoutParams);
                flLayoutParams.height = mWebViewHeight;
                flEdit.setLayoutParams(flLayoutParams);
                mView.postDelayed(new RefreshHeightRunnable(mWebViewHeight, time), CustomConstants.REFRESH_INTERVAL_TIME);
            } else {
                if (System.currentTimeMillis() - time < CustomConstants.REFRESH_TIMEOUT) {
                    mView.postDelayed(new RefreshHeightRunnable(mWebViewHeight, time), CustomConstants.REFRESH_INTERVAL_TIME);
                }
            }
        }
    }
}

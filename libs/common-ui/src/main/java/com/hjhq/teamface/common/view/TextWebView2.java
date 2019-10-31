package com.hjhq.teamface.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import com.hjhq.teamface.basis.bean.Photo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.ScreenUtils;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.activity.FullscreenViewImageActivity;
import com.hjhq.teamface.common.activity.ViewUserProtocolActivity;
import com.hjhq.teamface.common.ui.ImagePagerActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 富文本webView
 * Created by Administrator on 2018/7/10.
 */

public class TextWebView2 extends FrameLayout {
    private RichTextView mWebView;
    private TextWebInterface textWebInterface;
    private OnStateChangeListener mListener;
    private int type;
    private String name;
    private Object mObject;

    public interface TextWebInterface {
        void getWebText(String text);
    }

    public TextWebView2(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public TextWebView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TextWebView2(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @SuppressLint("JavascriptInterface")
    private void initView(Context context) {
        mWebView = (RichTextView) View.inflate(context, R.layout.layout_text_web_view2, null);
        addView(mWebView);

        if (Constants.IS_DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = mWebView.getSettings();
        settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        mWebView.setClickable(true);
        mWebView.setFocusable(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        // mWebView.addJavascriptInterface(getContext(), "android");
        mWebView.addJavascriptInterface(this, "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                if (mListener != null) {
                    return mListener.shouldOverrideUrlLoading(view, url);
                } else {
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //  getWebText(null);

                if (mListener != null) {
                    mListener.onPageFinished(view, url);
                }
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                //为了使webview加载完数据后resize高度，之所以不放在onPageFinished里，是因为onPageFinished不是每次加载完都会调用
                int w = MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED);
                int h = MeasureSpec.makeMeasureSpec(0,
                        MeasureSpec.UNSPECIFIED);
                //重新测量
                Log.e("onPageStarted", w + "宽高" + h);
                webView.measure(w, h);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        //回到页面顶端
                        webView.scrollTo(0, 0);
                        Log.e("mWebView", webView.getContentWidth() + "宽高" + webView.getContentHeight());
                    }
                });
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
            }

        });
        mWebView.setFindListener(new IX5WebViewBase.FindListener() {
            @Override
            public void onFindResultReceived(int i, int i1, boolean b) {

            }
        });

        //view重绘时回调
        mWebView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {

            }
        });


    }

    /**
     * 加载url,支持查看和编辑两种形式
     *
     * @param type 0编辑,1详情
     * @param url
     */
    public void loadUrl(int type, String url) {
        mWebView.loadUrl(url);
        this.type = type;
    }

    /**
     * JS调用-查看图片
     *
     * @param value
     */
   /* @JavascriptInterface
    public void viewPicture(String value) {
        ArrayList<Photo> photoList = new ArrayList<Photo>();
        Photo p = new Photo(value);
        photoList.add(p);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, photoList);
        CommonUtil.startActivtiy(this.getContext(), FullscreenViewImageActivity.class, bundle);
        Log.i("内容1=    ", value);
    }

    @JavascriptInterface
    public void viewPicture() {
        String value = "https://cdn.flutterchina.club/images/homepage/header-illustration.png";
        ArrayList<Photo> photoList = new ArrayList<Photo>();
        Photo p = new Photo(value);
        photoList.add(p);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, photoList);
        CommonUtil.startActivtiy(this.getContext(), FullscreenViewImageActivity.class, bundle);
        Log.i("内容1=    ", value);
    }

    *//**
     * 富文本组件接收到高度信息
     *
     * @param value
     *//*
    @JavascriptInterface
    public void receiveHeight(Object value) {
        final String string = com.alibaba.fastjson.JSONObject.toJSONString(value);
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(string);
            String bean = jsonObject.optString("bean");
            int height = jsonObject.optInt("height");
            RxManager.$(name).post(bean, height);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    /**
     * 设置值
     */
    public void setWebText(String text) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("head", new JSONObject());
            jo.put("html", text);
            jo.put("type", type);
            jo.put("device", 0);
            jo.put("width", ScreenUtils.getScreenWidth(mWebView.getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebView.evaluateJavascript("javascript:getValHtml(" + jo + ")", value -> {
        });
    }

    /**
     * 设置值
     *
     * @param bean
     * @param text
     */
    public void setWebText(String bean, String text) {
        JSONObject jo = new JSONObject();
        try {
            jo.put("head", new JSONObject());
            jo.put("html", text);
            jo.put("type", type);
            jo.put("device", 0);
            jo.put("width", ScreenUtils.getScreenWidth(mWebView.getContext()));
            jo.put("bean", bean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWebView.evaluateJavascript("javascript:getValHtml(" + jo + ")", value -> {
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    /**
     * 得到web值
     */
    public void getWebText(TextWebInterface textWebInterface) {
        this.textWebInterface = textWebInterface;
        mWebView.evaluateJavascript("javascript:sendValMobile(1)", value -> {
        });
    }

    public WebView getWebView() {
        return mWebView;
    }

    /**
     * 获取内容的回调
     *
     * @param value
     */
    @JavascriptInterface
    public void getContent2(String value) {
        if (textWebInterface != null) {
            textWebInterface.getWebText(value);
        }
    }

    /**
     * 富文本组件接收到高度信息
     *
     * @param value
     */
    @JavascriptInterface
    public void receiveHeight(String value) {
        //RxManager.$(getContext().hashCode()).post(name, value);
        try {
            JSONObject jsonObject = new JSONObject(value);
            String bean = jsonObject.optString("bean");
            int height = jsonObject.optInt("height");
           // RxManager.$(getContext().hashCode()).post(bean, height);

            Log.e("receiveHeight", bean + "----" + height);
            mWebView.postDelayed(() -> {
                Log.e("localHeight", bean + "----" + mWebView.getContentHeight());
                Log.e("getScrollBarSize", bean + "----" + mWebView.getScrollBarStyle());
                /*((Activity) mWebView.getContext()).getWindow().setLayout(-1, -1);
                mWebView.requestLayout();
                mWebView.invalidate();*/
            }, 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void viewPicture(String value) {
        ArrayList<Photo> photoList = new ArrayList<Photo>();
        Photo p = new Photo(value);
        p.setName(System.currentTimeMillis() + ".jpg");
        photoList.add(p);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, photoList);
        CommonUtil.startActivtiy(getContext(), FullscreenViewImageActivity.class, bundle);
        Log.i("内容1=    ", value);
    }

    @JavascriptInterface
    public void viewLink(String src){
        if (!TextUtil.isEmpty(src)){
            Bundle bundle = new Bundle();
            bundle.putString(Constants.DATA_TAG1, src);
            CommonUtil.startActivtiy(getContext(), ViewUserProtocolActivity.class, bundle);
        }
    }

    public interface OnStateChangeListener {

        boolean shouldOverrideUrlLoading(WebView view, String url);

        void onPageFinished(WebView view, String url);

        void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);
    }

    public void setOnStateChanListener(OnStateChangeListener listener) {
        this.mListener = listener;
    }
}

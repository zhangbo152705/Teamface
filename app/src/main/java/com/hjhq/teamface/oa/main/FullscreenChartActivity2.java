package com.hjhq.teamface.oa.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;

import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.common.view.TextWebView;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by Administrator on 2018/11/9.
 * Describe：
 */

public class FullscreenChartActivity2 extends RxAppCompatActivity {
    private FrameLayout flCan;
    private String mDataStr;
    private TextWebView mWebView1;
    private TextWebView mWebView2;
    private TextWebView mWebView3;
    private String content1 = "<p>放大发</p><p><br/></p><p><br/></p><p><img src=\"http://192.168.1.183:8081/custom-gateway/common/file/emailFileDownload?bean=email&fileName=1542336319842.png\" title=\"image.png\" alt=\"image.png\"/>FDa</p><p><br/></p><p><img src=\"http://192.168.1.183:8081/custom-gateway/common/file/emailFileDownload?bean=email&fileName=1542336322933.png\" title=\"image.png\" alt=\"image.png\"/></p><p>jieshu</p><p>还好还好哈</p><p>黄那你叫姐姐</p><p>不能贾健健康康</p><p>好好聚聚就</p><p>抱抱你</p><p>陈v宝贝</p><p> 吧</p><p>抱抱你</p>";
    private String content2 = "<p>多少的</p><p><br/></p><p><br/></p><p>fasdg</p><p>是</p><p><br/></p><p>gdfgdfgsdf</p><p><br/></p><p><br/></p><p><br/></p><p><br/></p><p>gfsgsgf</p><p><br/></p><p><br/></p><p><br/></p><p><br/></p><p><br/></p><p>gsfdgs</p><p>gfsdg</p><p><br/></p><p>gsfgsfgfr</p><p>公司代付</p><p><br/></p><p>结束</p><p>宝贝妞妞那</p>";
    private String content3 = "<p><img src='http://192.168.1.183:8081/custom-gateway/common/file/emailFileDownload?bean=email&fileName=1542336331494.png' title='image.png' alt='image.png'/><img src='http://192.168.1.183:8081/custom-gateway/common/file/emailFileDownload?bean=email&fileName=1542336336981.png' title='image.png' alt='image.png'/><img src='http://192.168.1.183:8081/custom-gateway/common/file/emailFileDownload?bean=email&fileName=1542336337686.png' title='image.png' alt='image.png'/><img src='http://192.168.1.183:8081/custom-gateway/common/file/emailFileDownload?bean=email&fileName=1542336338147.png' title='image.png' alt='image.png'/><img src='http://192.168.1.183:8081/custom-gateway/common/file/emailFileDownload?bean=email&fileName=1542336339202.png' title='image.png' alt='image.png'/></p>";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_chart_layout2);
        mWebView1 = findViewById(R.id.twv1);
        mWebView2 = findViewById(R.id.twv2);
        mWebView3 = findViewById(R.id.twv3);

        mWebView1.loadUrl(0, Constants.EMAIL_EDIT_URL);
        mWebView2.loadUrl(0, Constants.EMAIL_EDIT_URL);
        mWebView3.loadUrl(0, Constants.EMAIL_EDIT_URL);
        mWebView1.setOnStateChanListener(new TextWebView.OnStateChangeListener() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView1.setWebText(content1);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });
        mWebView2.setOnStateChanListener(new TextWebView.OnStateChangeListener() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView2.setWebText(content2);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });
        mWebView3.setOnStateChanListener(new TextWebView.OnStateChangeListener() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView3.setWebText(content3);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });


    }


    @JavascriptInterface
    public void quitFullscreenMode() {
        finish();
    }
}

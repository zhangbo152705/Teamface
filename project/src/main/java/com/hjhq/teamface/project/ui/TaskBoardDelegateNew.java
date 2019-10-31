package com.hjhq.teamface.project.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.load.LoadingDialog;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.NodeBean;
import com.hjhq.teamface.project.bean.SelectNodeResultBean;
import com.hjhq.teamface.project.bean.SubTaskBean;
import com.hjhq.teamface.project.presenter.task.TaskDetailActivity;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import net.lucode.hackware.magicindicator.MagicIndicator;

import static cn.jiguang.share.android.api.AbsPlatform.getApplicationContext;

/**
 * @author Administrator
 * @date 2018/4/12
 */

public class TaskBoardDelegateNew extends AppDelegate{
    private MagicIndicator mMagicIndicator;
    //public ViewPager mViewPager;
    //public MyOpenFragmentStatePagerAdapter mAdapter;
    //public View mMenu;
    com.tencent.smtt.sdk.WebView mWebView;
    NodeBean rootNode;
    public SelectNodeResultBean selectNode;
    private boolean webLoadFinish = false;//网页是否已经加载
    private boolean getDataFinish = false;
    private int webLoadFinishIndex = 0;//网页加载次数
    public RelativeLayout project_task_delete_re;
    public RelativeLayout project_task_edit_re;
    public RelativeLayout project_task_classify_re;
    public RelativeLayout project_task_sub_re;
    public ProgressBar webProgressBar;
    LoadingDialog loadingDialog;

    public TextView task_class_name;
    public TextView task_name;
    public ImageView tv_zoom;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_fragment_task_board_new;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mWebView = get(R.id.wv_task_content);
        project_task_delete_re = get(R.id.project_task_delete_re);
        project_task_edit_re = get(R.id.project_task_edit_re);
        project_task_classify_re = get(R.id.project_task_classify_re);
        project_task_sub_re = get(R.id.project_task_sub_re);
        webProgressBar = get(R.id.progressbar);
        task_class_name = get(R.id.task_class_name);
        task_name = get(R.id.task_name);
        tv_zoom = get(R.id.tv_zoom);
        tv_zoom.setTag(1);
        initWebViw();
        bindListener();

    }

    public void initWebViw(){

        if (Constants.IS_DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = mWebView.getSettings();
        settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(false);
        mWebView.setClickable(true);
        mWebView.setFocusable(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(this, "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView.setInitialScale(50);//网页默认缩小为20%
    }

    public void bindListener(){
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webLoadFinish =true;
                //  getWebText(null);
                if(getDataFinish && webLoadFinishIndex ==0){
                    updateHtml(view);
                    showWebData();
                }

                if (loadingDialog != null){
                    loadingDialog.close();
                }
                webLoadFinishIndex =webLoadFinishIndex;
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                loadingDialog = new LoadingDialog(mContext);
                loadingDialog.setLoadingText("Loading");
                loadingDialog.setInterceptBack(false);
                loadingDialog.show();
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (loadingDialog != null){
                    loadingDialog.close();
                }
            }


        });
        mWebView.loadUrl(Constants.PRJECT_TASK_EDIT_URL);


    }

    public void updateHtml(WebView view){
        String meta = "var meta = document.createElement('meta'); meta.setAttribute('name', 'viewport'); meta.setAttribute('content', 'width=device-width,initial-scale=0.2');" +
                " document.getElementsByTagName('head')[0].appendChild(meta);";
       String jsUrl = "javascript:(function() { " +meta +"})()";
        view.loadUrl(jsUrl);
    }

    /**
     * 传数据给WebView
     */
    private void showWebData() {
        if (rootNode == null){
            return;
        }
        String token = SPHelper.getToken();
        String domain = SPHelper.getDomain();
        JSONObject obj = (JSONObject) JSONObject.toJSON(rootNode);
        JSONObject jo = new JSONObject();
        try {
            jo.put("html", obj);
            jo.put("token",token);
            jo.put("domain",domain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("showWebData","objstr:"+jo);
        mWebView.evaluateJavascript("javascript:getValHtml(" + jo + ")", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e("js 返回的结果1", "====" + value);
            }
        });
    }

    /**
     * 缩放思维导图
     */
    public void showZoom(int jo){
        mWebView.evaluateJavascript("javascript:showClicked(" + jo + ")", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e("js 返回的结果1", "====" + value);
            }
        });
    }

    @JavascriptInterface
    public void androidReJsValue(String value) {
        Log.i("ReJsceiveValue=",""+ value);
        if (!TextUtil.isEmpty(value)){
            selectNode = JSON.parseObject(value,SelectNodeResultBean.class);
            RxManager.$(hashCode()).post(ProjectConstants.PROJECT_UPDATE_TITLE,1);
            if (selectNode.getActionType() == 1){//新建
                RxManager.$(hashCode()).post(ProjectConstants.PROJECT_ADD_FIRST_CLASS_TAG,1);
            }else if (selectNode.getActionType() == 2){//跳转
                try {
                    TaskInfoBean item = selectNode.getSelectNode().getTask_info();

                    if (selectNode.getSelectNode().getNode_type() == 3){
                        Bundle bundle = new Bundle();
                        bundle.putLong(ProjectConstants.TASK_ID, item.getId());
                        bundle.putLong(ProjectConstants.TASK_FROM_TYPE, 2);
                        bundle.putString(ProjectConstants.TASK_NAME, selectNode.getSelectNode().getTask_name());
                        bundle.putString(Constants.MODULE_BEAN,ProjectConstants.PROJECT_TASK_MOBULE_BEAN + item.getProject_id());
                        bundle.putLong(ProjectConstants.PROJECT_ID, item.getProject_id());
                        bundle.putLong(ProjectConstants.SUBNODE_ID, item.getId());
                        if(TextUtil.isEmpty(item.getTask_id())){
                            bundle.putLong(ProjectConstants.MAIN_TASK_ID, Long.parseLong(item.getTask_id()));
                        }
                        bundle.putString(ProjectConstants.MAIN_TASK_NODE_ID, selectNode.getSelectNode().getId()+"");

                        long taskId = 0;
                        String dataId = item.getTask_id();
                        if (dataId != null && TextUtil.isInteger(dataId)){
                            taskId = Long.parseLong(dataId);
                        }
                        bundle.putLong(ProjectConstants.MAIN_TASK_ID, taskId);
                        bundle.putString(ProjectConstants.MAIN_TASK_NODECODE, selectNode.getSelectNode().getNode_code());
                        CommonUtil.startActivtiy(mContext, TaskDetailActivity.class, bundle);
                    }else if(selectNode.getSelectNode().getNode_type() == 2){
                        TaskHelper.INSTANCE.clickTaskItem(mContext, item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TaskTempFragment", e.getMessage());
                }
            }else if (selectNode.getActionType() == 3){//完成
                RxManager.$(hashCode()).post(ProjectConstants.PROJECT_ADD_FIRST_CLASS_TAG,3);
            }
        }
    }

    /**
     * 刷新节点导航
     *
     * @param dataList
     */
    public void refreshNode(NodeBean dataList) {
        this.getDataFinish = true;
        this.rootNode = dataList;
        selectNode = null;
        if (webLoadFinish){
            showWebData();
        }

    }




}

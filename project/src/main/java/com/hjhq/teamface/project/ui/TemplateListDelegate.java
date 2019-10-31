package com.hjhq.teamface.project.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.load.LoadingDialog;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.NodeBean;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Administrator on 2018/4/10.
 */

public class TemplateListDelegate extends AppDelegate {
    public RecyclerView mRecyclerView;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_template_list_layout;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mRecyclerView = get(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));





    }








    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
}

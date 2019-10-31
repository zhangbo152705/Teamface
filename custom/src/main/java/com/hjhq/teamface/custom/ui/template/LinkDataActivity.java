package com.hjhq.teamface.custom.ui.template;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.WebLinkDataListBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.EncodingUtils;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.DataListDelegate;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.LinkDataAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;


/**
 * @author Administrator
 * @date 2017/9/5
 * Describe：搜索页
 */

public class LinkDataActivity extends ActivityPresenter<DataListDelegate, DataTempModel> {
    private LinkDataAdapter mAdapter;
    private List<WebLinkDataListBean.DataBean> mDataList = new ArrayList<>();
    private String moduleBean;
    private String seapoolId;
    private String relevanceModule;
    private String relevanceField;
    private String relevanceValue;
    private int source;
    private boolean isSeapool = false;
    private View barcodeView;
    private LinearLayout rlCode1;
    private LinearLayout rlCode2;
    private View line;
    private ImageView ivCode1;
    private ImageView ivCode2;
    private RelativeLayout btn1;
    private RelativeLayout btn2;
    private int mPosition;
    private String url1;
    private String url2;

    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    private Bundle needSaveIntent;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState != null) {
            needSaveIntent = savedInstanceState;
            moduleBean = savedInstanceState.getString(Constants.DATA_TAG1);
            seapoolId = savedInstanceState.getString(Constants.DATA_TAG2);
            source = savedInstanceState.getInt(Constants.DATA_TAG3);
            relevanceModule = savedInstanceState.getString(Constants.DATA_TAG4);
            relevanceField = savedInstanceState.getString(Constants.DATA_TAG5);
            relevanceValue = savedInstanceState.getString(Constants.DATA_TAG6);
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                needSaveIntent = bundle;
                moduleBean = bundle.getString(Constants.DATA_TAG1);
                seapoolId = bundle.getString(Constants.DATA_TAG2);
                source = bundle.getInt(Constants.DATA_TAG3);
                relevanceModule = bundle.getString(Constants.DATA_TAG4);
                relevanceField = bundle.getString(Constants.DATA_TAG5);
                relevanceValue = bundle.getString(Constants.DATA_TAG6);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (needSaveIntent != null) {
            super.onSaveInstanceState(needSaveIntent);
        } else {
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void init() {
        initView();
        mAdapter = new LinkDataAdapter(mDataList);
        viewDelegate.setAdapter(mAdapter);
        if (!TextUtils.isEmpty(moduleBean)) {
            getNetData();
        }
        viewDelegate.setTitle("Web表单链接");
    }

    private void initView() {
        barcodeView = getLayoutInflater().inflate(R.layout.dialog_show_barcode_layout2, null);
        rlCode2 = barcodeView.findViewById(R.id.ll_sign2);
        ivCode1 = barcodeView.findViewById(R.id.iv1);
        ivCode2 = barcodeView.findViewById(R.id.iv2);
        btn1 = barcodeView.findViewById(R.id.rl_btn1);
        btn2 = barcodeView.findViewById(R.id.rl_btn2);
        line = barcodeView.findViewById(R.id.line);
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setItemClickListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                url1 = mDataList.get(position).getExternalLink();
                url2 = mDataList.get(position).getSignInLink();
                if (view.getId() == R.id.tv_open) {
                    if (Patterns.WEB_URL.matcher(url1).matches()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, (Uri.parse(url1))).addCategory(Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else {
                        ToastUtils.showError(mContext, "链接不合法");
                    }

                } else if (view.getId() == R.id.tv_copy) {
                    SystemFuncUtils.copyTextToClipboard(mContext, url1);
                    ToastUtils.showSuccess(mContext, "复制成功");
                } else if (view.getId() == R.id.iv_qr_code) {
                    showBarcode(position);
                } else if (view.getId() == R.id.iv_barcode) {

                }
            }
        });
        /*viewDelegate.rlCan.setOnClickListener(v -> {
            viewDelegate.rlCan.removeAllViews();
            viewDelegate.rlCan.setVisibility(View.GONE);
        });
        ivCode1.setOnClickListener(v -> {
            viewDelegate.rlCan.removeAllViews();
            viewDelegate.rlCan.setVisibility(View.GONE);
        });
        ivCode1.setOnClickListener(v -> {
            viewDelegate.rlCan.removeAllViews();
            viewDelegate.rlCan.setVisibility(View.GONE);
        });*/
        btn1.setOnClickListener(v -> {
            shareWeb(mPosition, url1);
            viewDelegate.rlCan.removeAllViews();
            viewDelegate.rlCan.setVisibility(View.GONE);
        });
        btn2.setOnClickListener(v -> {
            shareWeb(mPosition, url2);
            viewDelegate.rlCan.removeAllViews();
            viewDelegate.rlCan.setVisibility(View.GONE);
        });


    }

    private void showBarcode(int position) {
        mPosition = position;
        WebLinkDataListBean.DataBean dataBean = mDataList.get(position);
        url1 = dataBean.getExternalLink();
        url2 = dataBean.getSignInLink();
        viewDelegate.addView(barcodeView);
        ivCode1.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = EncodingUtils.createBarcode(mContext, url1, 1000, 1000, false);
                if (bitmap != null) {
                    ivCode1.setImageBitmap(bitmap);
                }
            }
        });
        if (!TextUtils.isEmpty(url2)) {
            rlCode2.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            ivCode2.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = EncodingUtils.createBarcode(mContext, url2, 1000, 1000, false);
                    if (bitmap != null) {
                        ivCode2.setImageBitmap(bitmap);
                    }
                }
            });
        } else {
            rlCode2.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }

    }

    private void shareWeb(int position, String url) {
        WebLinkDataListBean.DataBean dataBean = mDataList.get(position);
        ShareParams shareParams = new ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setUrl(url);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        shareParams.setImageData(bitmap);
        shareParams.setText(dataBean.getShareDescription());
        shareParams.setTitle(dataBean.getShareTitle());
        JShareInterface.share("Wechat", shareParams, new PlatActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                runOnUiThread(() -> {
                    ToastUtils.showToast(mContext, "分享成功");
                });
            }

            @Override
            public void onError(Platform platform, int i, int i1, Throwable throwable) {
                throwable.printStackTrace();
                runOnUiThread(() -> {
                    ToastUtils.showToast(mContext, "分享失败");
                });
                Log.e("分享失败", platform.getName());
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
    }

    public void getNetData() {
        model.getWebformListForAdd(mContext, moduleBean, source, seapoolId, relevanceModule, relevanceField, relevanceValue, new ProgressSubscriber<WebLinkDataListBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(WebLinkDataListBean linkDataBean) {
                super.onNext(linkDataBean);
                mDataList.clear();
                mDataList.addAll(linkDataBean.getData());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (viewDelegate.rlCan.getVisibility() == View.VISIBLE) {
            viewDelegate.rlCan.removeAllViews();
            viewDelegate.rlCan.setVisibility(View.GONE);
        } else {
            finish();
        }
    }
}

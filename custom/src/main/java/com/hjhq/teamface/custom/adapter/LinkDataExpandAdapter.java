package com.hjhq.teamface.custom.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Patterns;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.WebLinkDataListBean;
import com.hjhq.teamface.basis.util.SystemFuncUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.custom.R;

import java.util.HashMap;
import java.util.List;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;

/**
 * 数据列表适配器
 *
 * @author lx
 * @date 2017/3/28
 */

public class LinkDataExpandAdapter extends BaseQuickAdapter<WebLinkDataListBean.DataBean.ExpandLinkBean, BaseViewHolder> {


    public LinkDataExpandAdapter(List<WebLinkDataListBean.DataBean.ExpandLinkBean> data) {
        super(R.layout.custom_item_link_data_expand, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WebLinkDataListBean.DataBean.ExpandLinkBean item) {
        helper.setText(R.id.tv_type, item.getName());
        helper.setText(R.id.tv_content, item.getUrl());
        helper.getView(R.id.tv_copy).setOnClickListener(v -> {
            SystemFuncUtils.copyTextToClipboard(mContext, item.getUrl());
            ToastUtils.showSuccess(mContext, "复制成功");
        });
        helper.getView(R.id.tv_open).setOnClickListener(v -> {
            String url = item.getUrl();
            if (Patterns.WEB_URL.matcher(url).matches()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, (Uri.parse(url))).addCategory(Intent.CATEGORY_BROWSABLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                ToastUtils.showError(mContext, "链接不合法");
            }
        });
        helper.getView(R.id.iv_qr_code).setOnClickListener(v -> {
            DialogUtils.getInstance().showQRcode(((Activity) mContext), "表单二维码", item.getUrl(), helper.getConvertView(), new DialogUtils.OnClickSureListener() {
                @Override
                public void clickSure() {
                    ShareParams shareParams = new ShareParams();
                    shareParams.setShareType(Platform.SHARE_WEBPAGE);
                    shareParams.setUrl(item.getUrl());
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
                    shareParams.setImageData(bitmap);
                    shareParams.setText(item.getUrl());
                    shareParams.setTitle(item.getName());
                    JShareInterface.share("Wechat", shareParams, new PlatActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                            ToastUtils.showToast(mContext, "分享成功");
                        }

                        @Override
                        public void onError(Platform platform, int i, int i1, Throwable throwable) {
                            throwable.printStackTrace();
                            ToastUtils.showToast(mContext, "分享失败");
                        }

                        @Override
                        public void onCancel(Platform platform, int i) {
                        }
                    });
                }
            });

        });

    }
}

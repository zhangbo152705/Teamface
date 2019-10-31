package com.hjhq.teamface.common.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.bean.AttendanceApproveListBean;

import java.util.List;


public class AttendanceApprovalFrangmentAdapter extends BaseQuickAdapter<AttendanceApproveListBean.DataBean.DataListBean, BaseViewHolder> {

    public AttendanceApprovalFrangmentAdapter(List<AttendanceApproveListBean.DataBean.DataListBean> data) {
        super(R.layout.attendance_approve_frangment_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttendanceApproveListBean.DataBean.DataListBean item) {
        //helper.addOnClickListener(R.id.edit);
        helper.setText(R.id.text, item.getRelevance_title());
        loadImage(item,helper.getView(R.id.image));
    }

    private void loadImage(AttendanceApproveListBean.DataBean.DataListBean item,ImageView imageView) {
        Context context = imageView.getContext();
        GradientDrawable drawable = (GradientDrawable) ((RelativeLayout) imageView.getParent()).getBackground();
        drawable.setColor(Color.WHITE);
        if ("1".equals(item.getIcon_type())) {//1.网络图片  其他加载本地图片
            ImageLoader.loadRoundImage(imageView.getContext(), item.getIcon_url(),
                    imageView, R.drawable.project_task_item_custom_icon);
        } else{// if ("0".equals(item.getIcon_type()))
            String iconColor = item.getIcon_color();
            drawable.setColor(ColorUtils.hexToColor(iconColor, "#3689E9"));
            String imageUrl = item.getIcon_url();
            if (TextUtil.isEmpty(imageUrl)) {
                ImageLoader.loadRoundImage(context,R.drawable.project_task_item_custom_icon, imageView);
            } else {
                //将 - 转换成 _
                String replace = imageUrl.replace("-", "_");
                int resId = context.getResources().getIdentifier(replace, "drawable", context.getPackageName());
                Log.e("loadImage","getPackageName:"+ context.getPackageName());
                Log.e("loadImage","resId:"+resId);
                Log.e("loadImage","replace:"+replace);
                ImageLoader.loadRoundImage(context, resId, imageView, R.drawable.project_task_item_custom_icon);
            }
        }
    }
}
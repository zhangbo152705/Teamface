package com.hjhq.teamface.common.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.DeviceUtils;
import com.hjhq.teamface.common.R;

import java.util.List;

/**
 * 角色适配器
 */
public class ManageWidgetAdapter extends BaseQuickAdapter<AppModuleBean, BaseViewHolder> {
    private int type = 1;

    public ManageWidgetAdapter(List<AppModuleBean> data) {
        super(R.layout.item_manage_widget, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, AppModuleBean item) {
        if (type == 1) {
            helper.addOnClickListener(R.id.iv_select);
            helper.setImageResource(R.id.iv_select, R.drawable.icon_red_remove);
            helper.setVisible(R.id.menu, true);
        } else if (type == 2) {
            helper.setImageResource(R.id.iv_select, R.drawable.icon_green_add);
            helper.setVisible(R.id.menu, false);
        }
        helper.setText(R.id.title, item.getChinese_name());
        View iconBg = helper.getView(R.id.rl_icon);
        ImageView icon = helper.getView(R.id.iv_icon);
        loadImage(icon, iconBg, item);
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param item
     * @param iconBg
     * @return
     */
    public void loadImage(ImageView ivIcon, View iconBg, AppModuleBean item) {
        GradientDrawable drawable = (GradientDrawable) iconBg.getBackground();
        drawable.setColor(Color.WHITE);

        String english_name = item.getEnglish_name();
        if (english_name == null) english_name = "";
        switch (english_name) {
            case ProjectConstants.PROJECT_BEAN_NAME:
            case ProjectConstants.TASK_MODULE_BEAN:
                ImageLoader.loadRoundImage(mContext, R.drawable.icon_nav_project, ivIcon);
                break;
            case EmailConstant.BEAN_NAME:
                ImageLoader.loadRoundImage(mContext, R.drawable.module_icon_email, ivIcon);
                break;
            case FileConstants.BEAN_NAME:
                ImageLoader.loadRoundImage(mContext, R.drawable.icon_wenjianku1, ivIcon);
                break;
            case MemoConstant.BEAN_NAME:
                ImageLoader.loadRoundImage(mContext, R.drawable.icon_system_library_4, ivIcon);
                break;
            case ApproveConstants.APPROVAL_MODULE_BEAN:
                ImageLoader.loadRoundImage(mContext, R.drawable.icon_shenpi, ivIcon);
                break;
            case MemoConstant.BEAN_NAME_KNOWLEDGE:
            case MemoConstant.BEAN_NAME_KNOWLEDGE2:
                ImageLoader.loadRoundImage(mContext, R.drawable.icon_knowledge, ivIcon);
                break;
            case AttendanceConstants.BEAN_NAME:
                ImageLoader.loadRoundImage(mContext, R.drawable.module_icon_attendance, ivIcon);
                break;
            default:
                /*if (TextUtil.isEmpty(item.getApplication_id())) {
                    int padding = (int) DeviceUtils.dpToPixel(mContext, 5);
                    iconBg.setPadding(padding, padding, padding, padding);
                } else if ("1".equals(item.getIcon_type())) {
                    int padding = (int) DeviceUtils.dpToPixel(mContext, 10);
                    iconBg.setPadding(padding, padding, padding, padding);
                } else {
                    iconBg.setPadding(0, 0, 0, 0);
                }*/
                if (TextUtil.isEmpty(item.getEnglish_name())) {
                    int padding = (int) DeviceUtils.dpToPixel(mContext, 5);
                    iconBg.setPadding(padding, padding, padding, padding);
                }
                if ("1".equals(item.getIcon_type())) {
                    int padding = (int) DeviceUtils.dpToPixel(mContext, 10);
                    iconBg.setPadding(padding, padding, padding, padding);
                }
                String iconColor = item.getIcon_color();
                drawable.setColor(ColorUtils.hexToColor(iconColor, "#3689E9"));

                String imageUrl = item.getIcon_url();
                if ("1".equals(item.getIcon_type())) {
                    ImageLoader.loadRoundImage(mContext, imageUrl, ivIcon, R.drawable.ic_image);
                } else if (TextUtil.isEmpty(imageUrl)) {
                    ImageLoader.loadRoundImage(mContext, R.drawable.ic_image, ivIcon);
                } else {
                    //将 - 转换成 _
                    String replace = imageUrl.replace("-", "_");
                    int resId = mContext.getResources().getIdentifier(replace, "drawable", mContext.getPackageName());
                    ImageLoader.loadRoundImage(mContext, resId, ivIcon, R.drawable.ic_image);
                }
                break;
        }
    }
}
package com.hjhq.teamface.customcomponent.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.customcomponent.R;

import java.util.List;

/**
 * 多选 Dialog适配器
 * Created by lx on 2017/3/28.
 */

public class KnowledgeItemAdapter extends BaseQuickAdapter<KnowledgeClassBean, BaseViewHolder> {


    public KnowledgeItemAdapter(List<KnowledgeClassBean> list) {
        super(R.layout.custom_item_dialog_widget, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, KnowledgeClassBean item) {
        ImageView ivSelect = helper.getView(R.id.iv_select);
        TextView tvItem = helper.getView(R.id.tv_item);
        ivSelect.setSelected(item.isCheck());
        TextUtil.setText(tvItem, item.getName());
        GradientDrawable myGrad = (GradientDrawable) tvItem.getBackground();
        tvItem.setTextColor(ColorUtils.resToColor(mContext, R.color.custom_content_color));
        myGrad.setColor(Color.WHITE);
       /* String color = item.getColor();

        GradientDrawable myGrad = (GradientDrawable) tvItem.getBackground();
        if (TextUtil.isEmpty(color) || !ColorUtils.checkColor(color) || "#FFFFFF".equals(color)) {
            tvItem.setTextColor(ColorUtils.resToColor(mContext, R.color.custom_content_color));
            myGrad.setColor(Color.WHITE);
        } else {
            tvItem.setTextColor(Color.WHITE);
            myGrad.setColor(ColorUtils.hexToColor(color, "#000000"));
        }*/

    }


}

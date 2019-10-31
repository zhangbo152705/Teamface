package com.hjhq.teamface.customcomponent.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.customcomponent.R;

import java.util.List;

/**
 * @author lx
 * @date 2017/5/15
 */

public class SelectAdapter extends BaseQuickAdapter<EntryBean, BaseViewHolder> {

    public SelectAdapter(List<EntryBean> data) {
        super(R.layout.custom_item_select, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EntryBean item) {
        TextView tvTitle = helper.getView(R.id.tv_title);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        ImageView iv_icon = helper.getView(R.id.iv_icon);
        String color = item.getColor();
        if (TextUtil.isEmpty(color)) {
            tvTitle.setTextColor(ColorUtils.resToColor(tvTitle.getContext(), R.color.custom_content_color));
        } else {
            tvTitle.setTextColor(Color.WHITE);
            tvTitle.setPadding(12, 4, 12, 4);
            tvTitle.setBackgroundResource(R.drawable.custom_flow_label);
            GradientDrawable myGrad = (GradientDrawable) tvTitle.getBackground();
            if ("#FFFFFF".equals(color)) {
                tvTitle.setTextColor(ColorUtils.resToColor(tvTitle.getContext(), R.color.black_4a));
            }
            myGrad.setColor(ColorUtils.hexToColor(color, "#000000"));
        }
        String label = item.getLabel();
        if(item.isCheck()){
            ivSelect.setImageResource(R.drawable.icon_selected);
        }else{
            ivSelect.setImageResource(R.drawable.icon_unselect);
        }
        tvTitle.setText(label);

        if (item.getFromType() == Constants.STATE_FROM_PROJECR){
            iv_icon.setVisibility(View.VISIBLE);
            if(item.getValue() != null && item.getValue().equals("0")){
                iv_icon.setImageResource(R.drawable.project_nostart);
            }else if(item.getValue() != null && item.getValue().equals("1")){
                iv_icon.setImageResource(R.drawable.taskcard_state);
            }else if(item.getValue() != null && item.getValue().equals("2")){
                iv_icon.setImageResource(R.drawable.project_suspended);
            }else if(item.getValue() != null && item.getValue().equals("3")){
                iv_icon.setImageResource(R.drawable.project_complete);
            }

        }else {
            iv_icon.setVisibility(View.GONE);
        }
    }
}

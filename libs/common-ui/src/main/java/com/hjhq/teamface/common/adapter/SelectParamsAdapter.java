package com.hjhq.teamface.common.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.bean.DynamicParamsBean;

import java.util.List;

/**
 * 角色适配器
 */
public class SelectParamsAdapter extends BaseQuickAdapter<DynamicParamsBean.DataBean, BaseViewHolder> {

    public SelectParamsAdapter(List<DynamicParamsBean.DataBean> data) {
        super(R.layout.item_dynamic_param, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, DynamicParamsBean.DataBean item) {

        TextView tvTitle = helper.getView(R.id.title);
        tvTitle.setText(item.getName());
        ImageView ivCheck = helper.getView(R.id.iv_select);
        helper.setVisible(R.id.iv_next, false);
        ivCheck.setImageResource(item.isCheck() ? R.drawable.icon_selected : R.drawable.icon_unselect);
    }
}
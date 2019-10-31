package com.hjhq.teamface.basis.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.R;
import com.hjhq.teamface.basis.bean.EntryBean;

import java.util.List;

/**
 * @author lx
 * @date 2017/5/15
 */

public class SelectItemAdapter extends BaseQuickAdapter<EntryBean, BaseViewHolder> {

    public SelectItemAdapter(List<EntryBean> data) {
        super(R.layout.custom_item_select_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EntryBean item) {
        helper.setText(R.id.tv_item, item.getLabel());
        ImageView view = helper.getView(R.id.iv_select);
        if (item.isCheck()) {
            view.setImageResource(R.drawable.icon_check_rec);
        } else {
            view.setImageResource(R.drawable.icon_uncheck_rec);
        }
    }
}

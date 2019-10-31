package com.hjhq.teamface.project.ui.filter.weight.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.R;
import com.hjhq.teamface.basis.bean.EntryBean;

import java.util.List;

/**
 * @author lx
 * @date 2017/3/28
 */

public class ItemFilterAdapter extends BaseQuickAdapter<EntryBean, BaseViewHolder> {

    public ItemFilterAdapter(List<EntryBean> list) {
        super(R.layout.custom_item_filter, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, EntryBean item) {
        helper.setText(R.id.name, item.getLabel());
        helper.getView(R.id.check_null).setSelected(item.isCheck());
        ImageView check = helper.getView(R.id.check_null);
        if (item.isCheck()) {
            check.setImageResource(R.drawable.filter_multi_select);
        } else {
            check.setImageResource(R.drawable.filter_multi_unselect);
        }
    }

}

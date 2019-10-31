package com.hjhq.teamface.common.weight.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.R;
import com.hjhq.teamface.basis.bean.FilterFieldResultBean;

import java.util.List;

/**
 * @author lx
 * @date 2017/3/28
 */

public class ItemFilterAdapter extends BaseQuickAdapter<FilterFieldResultBean.DataBean.EntrysBean, BaseViewHolder> {

    public ItemFilterAdapter(List<FilterFieldResultBean.DataBean.EntrysBean> list) {
        super(R.layout.custom_item_filter, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, FilterFieldResultBean.DataBean.EntrysBean item) {
        helper.setText(R.id.name, item.getLabel());
        helper.getView(R.id.check_null).setBackgroundResource(item.isChecked() ? R.drawable.filter_multi_select : R.drawable.filter_multi_unselect);
    }

}

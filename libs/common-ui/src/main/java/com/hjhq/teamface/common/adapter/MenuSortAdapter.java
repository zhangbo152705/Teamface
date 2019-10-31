package com.hjhq.teamface.common.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.TempMenuResultBean;
import com.hjhq.teamface.common.R;

import java.util.List;

/**
 * @author lx
 * @date 2017/3/28
 */

public class MenuSortAdapter extends BaseQuickAdapter<TempMenuResultBean.DataBean, BaseViewHolder> {


    public MenuSortAdapter(List<TempMenuResultBean.DataBean> list, int selectedIndex) {
        super(R.layout.workbench_widget_diy_module_menu_item, list);
    }

    public MenuSortAdapter(List<TempMenuResultBean.DataBean> list) {
        super(R.layout.workbench_widget_diy_module_menu_item, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, TempMenuResultBean.DataBean item) {
        helper.setText(R.id.widget_name, item.getName());
    }
}

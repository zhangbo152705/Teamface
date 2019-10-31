package com.hjhq.teamface.basis.util.popupwindow;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.R;
import com.hjhq.teamface.basis.bean.ToolMenu;

import java.util.List;

/**
 * 默认更多 popupwindow 适配器
 *
 * @author zzh
 */

public class ListItemAdapter extends BaseQuickAdapter<ToolMenu, BaseViewHolder> {
    public ListItemAdapter(List<ToolMenu> list) {
        super(R.layout.item_pop_menu_list, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ToolMenu item) {
        helper.setText(R.id.tv_title, item.getTitle());
    }
}

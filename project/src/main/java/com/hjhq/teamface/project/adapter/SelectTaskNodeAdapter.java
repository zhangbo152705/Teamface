package com.hjhq.teamface.project.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.NodeBean;

import java.util.List;

/**
 * 选择节点适配器
 *
 * @author Administrator
 * @date 2018/4/16
 */

public class SelectTaskNodeAdapter extends BaseItemDraggableAdapter<NodeBean, BaseViewHolder> {
    public SelectTaskNodeAdapter(List<NodeBean> data) {
        super(R.layout.project_task_item_select_node, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NodeBean item) {
        helper.addOnClickListener(R.id.iv_select);
        helper.setText(R.id.tv_name, item.getNode_name());
        helper.getView(R.id.iv_select).setSelected(item.isCheck());
        if (item.getChild() != null && item.getChild().size()>0){
            helper.getView(R.id.iv_more).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.iv_more).setVisibility(View.GONE);
        }

    }

}

package com.hjhq.teamface.memo.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;
import com.hjhq.teamface.memo.R;

import java.util.List;


public class SearchKnowledgeCatgListAdapter extends BaseQuickAdapter<KnowledgeClassBean, BaseViewHolder> {


    public SearchKnowledgeCatgListAdapter(List<KnowledgeClassBean> data) {
        super(R.layout.memo_knowledge_search_catg_item, data);

    }


    @Override
    protected void convert(BaseViewHolder helper, KnowledgeClassBean item) {
        helper.setText(R.id.tv_content, item.getName());
        if (helper.getAdapterPosition() == 0) {
            helper.setVisible(R.id.line_left, false);
        } else {
            if (helper.getAdapterPosition() == 1) {
                helper.setVisible(R.id.line_left, false);
                helper.setVisible(R.id.line_right, false);
            } else if (helper.getAdapterPosition() % 2 == 0) {
                helper.setVisible(R.id.line_left, false);
                helper.setVisible(R.id.line_right, true);
                helper.setVisible(R.id.line_top, false);
            } else if (helper.getAdapterPosition() % 2 == 1) {
                helper.setVisible(R.id.line_left, false);
                helper.setVisible(R.id.line_right, false);
                helper.setVisible(R.id.line_top, false);
            }
        }
    }
}
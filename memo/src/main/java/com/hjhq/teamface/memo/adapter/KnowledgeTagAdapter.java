package com.hjhq.teamface.memo.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.KnowledgeTagBean;
import com.hjhq.teamface.memo.R;

import java.util.List;


public class KnowledgeTagAdapter extends BaseQuickAdapter<KnowledgeTagBean, BaseViewHolder> {


    public KnowledgeTagAdapter(List<KnowledgeTagBean> data) {
        super(R.layout.memo_knowledge_list_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, KnowledgeTagBean item) {


    }


}
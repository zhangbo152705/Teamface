package com.hjhq.teamface.memo.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.bean.VersionListBean;

import java.util.List;


public class KnowledgeVersionAdapter extends BaseQuickAdapter<VersionListBean.VersionBean, BaseViewHolder> {

    public KnowledgeVersionAdapter(List<VersionListBean.VersionBean> data) {
        super(R.layout.memo_knowledge_version_list_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, VersionListBean.VersionBean item) {
        helper.setText(R.id.tv_content, item.getName());
        helper.getView(R.id.iv_icon).setSelected(item.isCheck());
        helper.setTextColor(R.id.tv_content, item.isCheck()
                ? helper.getConvertView().getContext().getResources().getColor(R.color.orange_color)
                : helper.getConvertView().getContext().getResources().getColor(R.color.gray_4a));
    }

}
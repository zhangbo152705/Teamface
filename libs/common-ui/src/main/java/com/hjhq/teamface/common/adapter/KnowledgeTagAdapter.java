package com.hjhq.teamface.common.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;
import com.hjhq.teamface.common.R;

import java.util.List;

/**
 * @author lx
 * @date 2017/5/15
 */

public class KnowledgeTagAdapter extends BaseQuickAdapter<KnowledgeClassBean, BaseViewHolder> {

    public KnowledgeTagAdapter(List<KnowledgeClassBean> data) {
        super(R.layout.custom_tag_item_filter, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, KnowledgeClassBean item) {
        helper.setText(R.id.name, item.getName());
        ImageView check = helper.getView(R.id.check_null);
        if (item.isCheck()) {
            check.setImageResource(R.drawable.selected);
        } else {
            check.setImageResource(R.drawable.state_uncheck_oval);
        }

    }
}

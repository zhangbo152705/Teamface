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

public class KnowledgeFiexdCatgAdapter extends BaseQuickAdapter<KnowledgeClassBean, BaseViewHolder> {

    public KnowledgeFiexdCatgAdapter(List<KnowledgeClassBean> data) {
        super(R.layout.custom_fixed_catg_item_filter, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, KnowledgeClassBean item) {
        helper.setText(R.id.name, item.getName());
        ImageView ivCheck = helper.getView(R.id.check_null);
        setCheck(item.isCheck(), ivCheck);
        helper.getView(R.id.rl_title).setOnClickListener(v -> {
            final boolean check = getData().get(helper.getAdapterPosition()).isCheck();
            for (int i = 0; i < getData().size(); i++) {
                if (i == helper.getAdapterPosition()) {
                    item.setCheck(!check);
                } else {
                    getData().get(i).setCheck(false);
                }
            }
            notifyDataSetChanged();
        });


    }

    private void setCheck(boolean isCheck, ImageView check) {
        if (isCheck) {
            check.setImageResource(R.drawable.selected);
        } else {
            check.setImageResource(R.drawable.state_uncheck_oval);
        }
    }
}

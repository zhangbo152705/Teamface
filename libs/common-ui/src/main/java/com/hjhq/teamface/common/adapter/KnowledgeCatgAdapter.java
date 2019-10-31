package com.hjhq.teamface.common.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lx
 * @date 2017/5/15
 */

public class KnowledgeCatgAdapter extends BaseQuickAdapter<KnowledgeClassBean, BaseViewHolder> {

    public KnowledgeCatgAdapter(List<KnowledgeClassBean> data) {
        super(R.layout.custom_catg_item_filter, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, KnowledgeClassBean item) {
        helper.setText(R.id.name, item.getName());
        RecyclerView rv = helper.getView(R.id.rv);
        ImageView check = helper.getView(R.id.check_null);
        ImageView next = helper.getView(R.id.next);
        setCheck(item.isCheck(), check);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        KnowledgeTagAdapter tagAdapter = new KnowledgeTagAdapter(item.getLabels());
        rv.setAdapter(tagAdapter);
        rv.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                if (tagAdapter.getData() == null || tagAdapter.getData().size() == 0) {
                    return;
                }
                tagAdapter.getData().get(position).setCheck(!tagAdapter.getData().get(position).isCheck());
                final List<KnowledgeClassBean> data = tagAdapter.getData();
                boolean checkAll = true;
                for (int i = 0; i < data.size(); i++) {
                    if (!data.get(i).isCheck()) {
                        checkAll = false;
                        break;
                    }
                }
                tagAdapter.notifyDataSetChanged();
                item.setCheck(checkAll);
                setCheck(checkAll, check);
            }
        });
        helper.getView(R.id.rl_title).setOnClickListener(v -> {
            if (rv.getVisibility() == View.VISIBLE) {
                rv.setVisibility(View.GONE);
                next.setImageResource(R.drawable.icon_sort_up);
            } else {
                rv.setVisibility(View.VISIBLE);
                next.setImageResource(R.drawable.icon_sort_down);
            }
        });
        check.setOnClickListener(v -> {
            item.setCheck(!item.isCheck());
            setCheck(item.isCheck(), check);
            final ArrayList<KnowledgeClassBean> labels = item.getLabels();
            for (int i = 0; i < labels.size(); i++) {
                labels.get(i).setCheck(item.isCheck());
            }
            tagAdapter.notifyDataSetChanged();
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

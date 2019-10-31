package com.hjhq.teamface.custom.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.WebLinkDataListBean;
import com.hjhq.teamface.basis.util.DrawableUtils;
import com.hjhq.teamface.custom.R;

import java.util.List;

/**
 * 数据列表适配器
 *
 * @author lx
 * @date 2017/3/28
 */

public class LinkDataAdapter extends BaseQuickAdapter<WebLinkDataListBean.DataBean, BaseViewHolder> {


    public LinkDataAdapter(List<WebLinkDataListBean.DataBean> data) {
        super(R.layout.custom_item_link_data, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WebLinkDataListBean.DataBean item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_content, item.getExternalLink());
        helper.addOnClickListener(R.id.tv_copy);
        helper.addOnClickListener(R.id.tv_open);
        helper.addOnClickListener(R.id.iv_qr_code);
        helper.setVisible(R.id.rv_expand, false);

        if (item.getExpandLink() != null && item.getExpandLink().size() > 0) {
            helper.setVisible(R.id.rl_expand, true);
            RecyclerView rv = helper.getView(R.id.rv_expand);
            rv.setLayoutManager(new LinearLayoutManager(rv.getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            LinkDataExpandAdapter adapter = new LinkDataExpandAdapter(item.getExpandLink());
            rv.setAdapter(adapter);
        } else {
            helper.setVisible(R.id.rl_expand, false);
            helper.setVisible(R.id.rv_expand, false);
        }
        helper.getView(R.id.rl_expand).setOnClickListener(v -> {
            int visibility = helper.getView(R.id.rv_expand).getVisibility();
            if (visibility == View.VISIBLE) {
                helper.setVisible(R.id.rv_expand, false);
                DrawableUtils.rotateView(helper.convertView.getContext(), helper.getView(R.id.iv), 0f, -180f, 500,
                        R.drawable.icon_sort_down);
            } else {
                helper.setVisible(R.id.rv_expand, true);
                DrawableUtils.rotateView(helper.convertView.getContext(), helper.getView(R.id.iv), 0f, 180f, 500,
                        R.drawable.icon_sort_down);
            }
        });
    }
}

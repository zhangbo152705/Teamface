package com.hjhq.teamface.custom.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.InsertSubformBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.custom.R;

import java.util.List;

/**
 * 关联列表适配器
 *
 * @author xj
 * @date 2017/3/28
 */

public class InsertSubformAdapter extends BaseQuickAdapter<InsertSubformBean.DataBean.DataListBean, BaseViewHolder> {
    private boolean isMulti = false;

    public InsertSubformAdapter(List<InsertSubformBean.DataBean.DataListBean> list) {
        super(R.layout.custom_item_reference_temp, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, InsertSubformBean.DataBean.DataListBean item) {
        List<RowBean> row = item.getRow();
        RecyclerView mRecyclerView = helper.getView(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(new ReferenceItemAdapter(row));
        ImageView check = helper.getView(R.id.iv_check);
        helper.setVisible(R.id.iv_check, isMulti);
        if (item.isCheck()) {
            check.setImageResource(R.drawable.icon_selected);
        } else {
            check.setImageResource(R.drawable.icon_unselect);
        }

    }

    public void setType(boolean isMulti) {
        this.isMulti = isMulti;
    }
}

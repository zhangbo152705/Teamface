package com.hjhq.teamface.custom.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.custom.R;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 关联列表适配器
 *
 * @author xj
 * @date 2017/3/28
 */

public class ReferenceTempAdapter extends BaseQuickAdapter<ReferDataTempResultBean.DataListBean, BaseViewHolder> {
    private boolean isMulti = false;

    public ReferenceTempAdapter(List<ReferDataTempResultBean.DataListBean> list) {
        super(R.layout.custom_item_reference_temp, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ReferDataTempResultBean.DataListBean item) {
        List<RowBean> row = item.getRow();
        if (!CollectionUtils.isEmpty(row)) {
            List<RowBean> newList = new ArrayList<>();
            Observable.from(row).filter(data -> "0".equals(data.getHidden())).subscribe(newList::add);
            if (!CollectionUtils.isEmpty(newList)) {
                newList.get(0).setIsLock("1");
                RecyclerView mRecyclerView = helper.getView(R.id.recycler_view);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                mRecyclerView.setAdapter(new ReferenceItemAdapter(newList));
            }

        }
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

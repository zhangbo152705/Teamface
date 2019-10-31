package com.hjhq.teamface.common.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.RoleGroupResponseBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.utils.MemberUtils;

import java.util.List;

/**
 * 角色适配器
 */
public class SelectRolesAdapter extends BaseQuickAdapter<RoleGroupResponseBean.DataBean.RolesBean, BaseViewHolder> {
    private SimpleItemClickListener simItemTouchListener;
    private SelectRolesAdapter adapter;
    public SelectRolesAdapter(List<RoleGroupResponseBean.DataBean.RolesBean> data,SimpleItemClickListener simItemTouchListener) {
        super(R.layout.item_organization, data);
        this.simItemTouchListener = simItemTouchListener;
        adapter = this;
    }


    @Override
    protected void convert(BaseViewHolder helper, RoleGroupResponseBean.DataBean.RolesBean item) {
        ImageView ivSelect = helper.getView(R.id.iv_select);
        TextView tvTitle = helper.getView(R.id.title);
        LinearLayout ll_item_layout = helper.getView(R.id.ll_item_layout);
        helper.setVisible(R.id.iv_next, false);
        helper.setVisible(R.id.tv_sub_title, false);
        int selectState = item.getSelectState();
        if (MemberUtils.checkState(selectState, C.CAN_NOT_SELECT)) {
            if (item.isCheck()) {
                ivSelect.setImageResource(R.drawable.icon_prohibit_select);
            } else {
                ivSelect.setImageResource(R.drawable.icon_prohibit_unselect);
            }
        } else if (MemberUtils.checkState(selectState, C.FREE_TO_SELECT)) {
            if (item.isCheck()) {
                ivSelect.setImageResource(R.drawable.icon_selected);
            } else {
                ivSelect.setImageResource(R.drawable.icon_unselect);
            }
        }
        tvTitle.setText(item.getName());
        ll_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simItemTouchListener.onItemClick(adapter,v,helper.getAdapterPosition());
            }
        });
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simItemTouchListener.onItemChildClick(adapter,v,helper.getAdapterPosition());
            }
        });
    }
}

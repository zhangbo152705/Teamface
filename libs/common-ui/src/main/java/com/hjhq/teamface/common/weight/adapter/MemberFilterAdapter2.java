package com.hjhq.teamface.common.weight.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.R;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;

import java.util.List;

/**
 * @author lx
 * @date 2017/3/28
 */

public class MemberFilterAdapter2 extends BaseQuickAdapter<Member, BaseViewHolder> {
    private boolean isDepartment = false;

    public MemberFilterAdapter2(List<Member> list) {
        super(R.layout.custom_item_filter_v2, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Member item) {
        helper.setVisible(R.id.photo, !isDepartment);
        helper.setText(R.id.name, item.getName());
        helper.getView(R.id.check_null).setOnClickListener(v -> {
            getData().remove(helper.getAdapterPosition());
            notifyDataSetChanged();
        });
        ImageView photoView = helper.getView(R.id.photo);
        ImageLoader.loadCircleImage(mContext, item.getPicture(), photoView, item.getName());
    }


    public void setDepartment(boolean department) {
        isDepartment = department;
    }
}

package com.hjhq.teamface.basis.view.member;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.R;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lx on 2017/3/29.
 */

public class AddMemberViewAdapter extends BaseQuickAdapter<Member, BaseViewHolder> {
    @Override
    public void setNewData(List<Member> data) {
        this.mData = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    public AddMemberViewAdapter(List<Member> data) {
        super(R.layout.item_add_member_view_list, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, Member item) {
        if (item.getType() == 0) {
            //部门
            ImageLoader.loadCircleImage(mContext, R.drawable.icon_department, helper.getView(R.id.avatar_iv));
            helper.setText(R.id.avatar_tv, item.getName());
        } else if (item.getType() == 1) {
            //人员
            String name = item.getName();
            if (TextUtils.isEmpty(name)) {
                name = item.getEmployee_name();
            }
            ImageLoader.loadCircleImage(mContext, item.getPicture(), helper.getView(R.id.avatar_iv), name);
            helper.setText(R.id.avatar_tv, item.getEmployee_name());
        }

    }
}

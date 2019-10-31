package com.hjhq.teamface.common.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.common.R;

import java.util.List;

/**
 * 群组成员适配器
 * Created by lx on 2017/5/15.
 */

public class MemberAdapter extends BaseQuickAdapter<Member, BaseViewHolder> {

    public MemberAdapter(List<Member> data) {
        super(R.layout.im_item_group_member, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Member item) {
        if (TextUtils.isEmpty(item.getName())) {
            helper.setText(R.id.name, item.getEmployee_name());
        } else {
            helper.setText(R.id.name, item.getName());
        }
        if (TextUtils.isEmpty(item.getPost_name())) {
            helper.setText(R.id.department_position, "--");
        } else {
            helper.setText(R.id.department_position, "" + item.getPost_name());
        }
        ImageLoader.loadCircleImage(mContext, item.getPicture(), helper.getView(R.id.avatar), item.getName());
        helper.setVisible(R.id.choosed, false);
    }
}

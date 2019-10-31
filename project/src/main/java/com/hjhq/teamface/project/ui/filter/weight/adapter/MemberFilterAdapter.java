package com.hjhq.teamface.project.ui.filter.weight.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.project.R;

import java.util.List;

/**
 * @author lx
 * @date 2017/3/28
 */

public class MemberFilterAdapter extends BaseQuickAdapter<Member, BaseViewHolder> {

    public MemberFilterAdapter(List<Member> list) {
        super(R.layout.custom_item_filter, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Member item) {
        helper.setVisible(R.id.photo, true);
        helper.setText(R.id.name, item.getName());
        ImageView check = helper.getView(R.id.check_null);
        if (item.isCheck()) {
            check.setImageResource(R.drawable.filter_multi_select);
        } else {
            check.setImageResource(R.drawable.filter_multi_unselect);
        }
        /*helper.getView(R.id.check_null).setOnClickListener(v -> {
            item.setCheck(!item.isCheck());
            helper.getView(R.id.check_null).setSelected(item.isCheck());
            notifyDataSetChanged();
        });*/
        ImageView photoView = helper.getView(R.id.photo);
        ImageLoader.loadCircleImage(mContext, item.getPicture(), photoView, item.getName());
    }

}

package com.hjhq.teamface.common.adapter;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.utils.MemberUtils;

import java.util.List;


/**
 * 选择员工适配器
 * Created by lx on 2017/5/15.
 */

public class OrganizationEmployeeAdapter extends BaseQuickAdapter<Member, BaseViewHolder> {
    //1单选2多选
    private int chooseType = C.RADIO_SELECT;
    private boolean showCheck = false;

    public OrganizationEmployeeAdapter(List<Member> data) {
        super(R.layout.item_contact, data);
    }


    public void setChooseType(int chooseType) {
        this.chooseType = chooseType;
    }

    public void setShowCheck(boolean showCheck) {
        this.showCheck = showCheck;
    }

    @Override
    protected void convert(BaseViewHolder helper, Member item) {
        /*if (chooseType == C.RADIO_SELECT) {
            helper.setVisible(R.id.iv_select, false);
        } else if (chooseType == C.MULTIL_SELECT) {
            helper.setVisible(R.id.iv_select, true);
        }*/
        helper.setVisible(R.id.iv_select, showCheck);
        TextView tvTitle = helper.getView(R.id.title);
        ImageView ivSelect = helper.getView(R.id.iv_select);
        TextView tvSubTitle = helper.getView(R.id.tv_sub_title);
        ImageView avatar = helper.getView(R.id.avatar_in_contacts_iv);
        helper.setVisible(R.id.catalog, false);

        TextUtil.setText(tvTitle, item.getName());
        if (TextUtils.isEmpty(item.getPost_name())) {
            TextUtil.setText(tvSubTitle, "--");
        } else {
            TextUtil.setText(tvSubTitle, item.getPost_name());
        }
        ImageLoader.loadCircleImage(mContext, item.getPicture(), avatar, item.getName());

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
    }
}

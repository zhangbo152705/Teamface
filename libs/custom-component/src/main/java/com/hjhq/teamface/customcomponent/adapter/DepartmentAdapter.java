package com.hjhq.teamface.customcomponent.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.customcomponent.R;

import java.util.List;

/**
 * @author lx
 * @date 2017/5/15
 */

public class DepartmentAdapter extends BaseQuickAdapter<Member, BaseViewHolder> {
    private boolean isEdit = false;

    public DepartmentAdapter(List<Member> data) {
        super(R.layout.department_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Member item) {
        helper.setText(R.id.name, item.getName());
        if (isEdit) {
            helper.setVisible(R.id.delete, true);
        } else {
            helper.setVisible(R.id.delete, false);
        }
        helper.addOnClickListener(R.id.delete);

    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }
}

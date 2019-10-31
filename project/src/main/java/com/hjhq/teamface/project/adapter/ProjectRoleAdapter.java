package com.hjhq.teamface.project.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.ProjectRoleBean;

import java.util.List;

/**
 * 项目角色列表适配器
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class ProjectRoleAdapter extends BaseQuickAdapter<ProjectRoleBean.DataBean.DataListBean, BaseViewHolder> {
    public ProjectRoleAdapter(List<ProjectRoleBean.DataBean.DataListBean> data) {
        super(R.layout.project_role_list_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectRoleBean.DataBean.DataListBean item) {
        helper.setText(R.id.project_textview, item.getName());
        if (item.isCheck()) {
            helper.setVisible(R.id.iv_visitor, true);
        } else {
            helper.setVisible(R.id.iv_visitor, false);
        }
    }
}

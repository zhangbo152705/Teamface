package com.hjhq.teamface.project.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.FilterDataBean;

import java.util.List;

/**
 * 子任务适配器
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class FilterMainAdapter extends BaseQuickAdapter<FilterDataBean, BaseViewHolder> {
    private int index = -1;

    public FilterMainAdapter(List<FilterDataBean> data) {
        super(R.layout.project_main_filter_item, data);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    protected void convert(BaseViewHolder helper, FilterDataBean item) {
        helper.setText(R.id.tv_task_name, item.getBeanName());
        ImageView ivCheck = helper.getView(R.id.iv_check);
        if (index == helper.getAdapterPosition()) {
            ivCheck.setImageResource(R.drawable.selected);
        } else {
            ivCheck.setImageResource(R.drawable.state_uncheck_oval);
        }
    }
}

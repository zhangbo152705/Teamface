package com.hjhq.teamface.custom.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.bean.TabListBean;

import java.util.List;

/**
 * @author Administrator
 * @date 2017/12/12
 */

public class TabListAdapter extends BaseQuickAdapter<TabListBean.DataBean.DataListBean, BaseViewHolder> {

    public TabListAdapter(List<TabListBean.DataBean.DataListBean> data) {
        super(R.layout.custom_item_relevance_module, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TabListBean.DataBean.DataListBean item) {
        switch (item.getCondition_type()) {
            case "2":
                helper.setText(R.id.tv_module, "邮件");
                break;
            case "3":
                helper.setText(R.id.tv_module, "匹配." + item.getChinese_name());
                break;
            default:
                helper.setText(R.id.tv_module, item.getChinese_name());
                break;
        }

    }
}

package com.hjhq.teamface.project.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.RelationListResultBean;

/**
 * 关联任务、被关联任务
 * Created by Administrator on 2018/7/16.
 */

public class RelationTaskAdapter extends BaseQuickAdapter<RelationListResultBean.DataBean.ModuleDataBean, BaseViewHolder> {
    private TaskItemAdapter.OnItemClickListener onItemClickListener;
    private boolean isBeRelation = false;

    public RelationTaskAdapter() {
        super(R.layout.project_task_relevance_item, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, RelationListResultBean.DataBean.ModuleDataBean item) {
        helper.setText(R.id.tv_task_temp_name, item.getModule_name());
        helper.addOnClickListener(R.id.iv_task_menu);

        LinearLayout li_item = helper.getView(R.id.li_item);
        RecyclerView relevanceRecyclerView = helper.getView(R.id.recycler_view);
        relevanceRecyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        TaskItemAdapter taskItemAdapter = new TaskItemAdapter(item.getModuleDataList(), false);
        relevanceRecyclerView.setAdapter(taskItemAdapter);

        helper.getView(R.id.ll_relation_item).setOnClickListener(v ->
                helper.setVisible(R.id.li_item, li_item.getVisibility() != View.VISIBLE));

        if (onItemClickListener != null) {
            taskItemAdapter.setOnItemClickListener(onItemClickListener);
        }

        helper.addOnClickListener(R.id.ll_relation);
        helper.addOnClickListener(R.id.ll_add);
        if (!CollectionUtils.isEmpty(item.getModuleDataList())){
            int datatype = item.getModuleDataList().get(0).getDataType();
            if (datatype ==2){
                helper.setVisible(R.id.ll_add,false);
            }else {
                helper.setVisible(R.id.ll_add,true);
            }
        }

        //是否是被关联
        if (isBeRelation){
            helper.setVisible(R.id.is_be_relation,false);
        }
    }

    public void setOnItemClickListener(TaskItemAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置是否是被关联数据
     * @param flag
     */
    public void setBeRelation(boolean flag){
        this.isBeRelation = flag;

    }
}

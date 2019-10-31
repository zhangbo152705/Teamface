package com.hjhq.teamface.project.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.ProjectLabelsAdapter;

/**
 * 新增项目/项目设置
 */

public class ProjectDynamicDelegate extends AppDelegate {
    public RecyclerView mRecyclerView;
    public TextView trend_content_num;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_activity_dynamic;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle("项目动态");
        mRecyclerView = get(R.id.rv_all_trend);
        trend_content_num = get(R.id.trend_content_num);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    /**
     * 设置数目
     */
    public void setNum(int num){
        trend_content_num.setText(num+mContext.getResources().getString(R.string.project_dymanic_item_num));
    }

    public void setAdapter(ProjectLabelsAdapter adapter) {
        mRecyclerView.setAdapter(adapter);

    }

    public void setItemListener(RecyclerView.OnItemTouchListener listener) {
        mRecyclerView.addOnItemTouchListener(listener);

    }
}

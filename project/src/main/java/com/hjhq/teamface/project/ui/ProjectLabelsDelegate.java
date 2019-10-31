package com.hjhq.teamface.project.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.ProjectLabelsAdapter;

/**
 * 新增项目/项目设置
 */

public class ProjectLabelsDelegate extends AppDelegate {
    private RecyclerView mRecyclerView;
    public EditText etSearch;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_labels_activity_layout;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle("项目标签");
        mRecyclerView = get(R.id.rv);
        etSearch = get(R.id.et_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setAdapter(ProjectLabelsAdapter adapter) {
        mRecyclerView.setAdapter(adapter);

    }

    public void setItemListener(RecyclerView.OnItemTouchListener listener) {
        mRecyclerView.addOnItemTouchListener(listener);

    }
}

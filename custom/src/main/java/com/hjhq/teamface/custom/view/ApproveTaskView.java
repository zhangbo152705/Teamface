package com.hjhq.teamface.custom.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.util.device.DeviceUtils;
import com.hjhq.teamface.basis.view.recycler.MyLinearDeviderDecoration;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.bean.ProcessFlowResponseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 审批流程组件
 * Created by Administrator on 2018/1/4.
 */

public class ApproveTaskView extends FrameLayout {

    private Context mContext;
    private View mView;
    private ApproveTaskItemAdapter approveAdapter;
    private RecyclerView mRecyclerView;
    List<ProcessFlowResponseBean.DataBean> taskFlow = new ArrayList<>();

    public ApproveTaskView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ApproveTaskView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ApproveTaskView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mView = View.inflate(mContext, R.layout.custom_approve_task_view, null);
        approveAdapter = new ApproveTaskItemAdapter(taskFlow);

        mRecyclerView = mView.findViewById(R.id.recyclerview);
        mRecyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new MyLinearDeviderDecoration(mContext, R.color.transparent, (int) DeviceUtils.dpToPixel(mContext, 0)));
        mRecyclerView.setAdapter(approveAdapter);
        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_del) {
                    adapter.remove(position);
                    int count = adapter.getItemCount() + 1;
                }
            }
        });

        addView(mView);
    }

    /**
     * 设置数据
     *
     * @param taskFlow
     */
    public void setApproveTaskFlow(List<ProcessFlowResponseBean.DataBean> taskFlow) {
        if (taskFlow == null) {
            taskFlow = new ArrayList<>();
        }
        this.taskFlow.clear();
        this.taskFlow.addAll(taskFlow);
        approveAdapter.notifyDataSetChanged();
    }
}

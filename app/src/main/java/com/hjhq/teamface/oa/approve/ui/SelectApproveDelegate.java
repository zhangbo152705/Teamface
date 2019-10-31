package com.hjhq.teamface.oa.approve.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.zygote.AppDelegate;


/**
 * 选择审批 视图代理类
 *
 * @author lx
 * @date 2017/8/31
 */

public class SelectApproveDelegate extends AppDelegate {
    RecyclerView mRecyclerView;
    SwipeRefreshLayout swipeRefreshWidget;


    @Override
    public int getRootLayoutId() {
        return R.layout.activity_select_approve;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }


    @Override
    public void initWidget() {
        super.initWidget();
        setTitle("审批");
        setRightMenuTexts(mContext.getString(R.string.add_new),mContext.getString(R.string.confirm));

        swipeRefreshWidget = get(R.id.swipe_refresh_widget);
        swipeRefreshWidget.setColorSchemeResources(R.color.main_green);

        mRecyclerView = get(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }

    public void showMenus(int fromType){
        if (fromType ==1){
            showMenu(0,1);
        }else {
            showMenu(1);
        }
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        View emptyView = mContext.getLayoutInflater().inflate(R.layout.view_workbench_empty, null);
        TextView titleTv = emptyView.findViewById(R.id.title_tv);
        titleTv.setText("暂无审批，喝杯咖啡思考人生");
        adapter.setEmptyView(emptyView);
        mRecyclerView.setAdapter(adapter);
    }

}

package com.hjhq.teamface.project.ui.task;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.DeviceUtils;
import com.hjhq.teamface.basis.view.recycler.MyLinearDeviderDecoration;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.SelectNodeAdapter;
import com.hjhq.teamface.project.adapter.SelectTaskNodeAdapter;
import com.hjhq.teamface.project.bean.NodeBean;

import java.util.List;

/**
 * 选择分组列表
 * Created by Administrator on 2018/7/12.
 */

public class SelectGroupTempDelegate extends AppDelegate {
    TextView tvGroup;
    TextView tvTemp;
    TextView tvSubTemp;

    public RecyclerView mRecyclerView;
    SelectTaskNodeAdapter adapter;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_activity_select_group_temp;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setRightMenuTexts(mContext.getString(R.string.confirm));
        tvGroup = get(R.id.tv_group_name);
        tvTemp = get(R.id.tv_temp_name);
        tvSubTemp = get(R.id.tv_sub_temp_name);

        setOnClickListener((View.OnClickListener) mContext, R.id.ll_group, R.id.ll_temp,R.id.ll_sub_temp);


        mRecyclerView = get(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyLinearDeviderDecoration myLinearDeviderDecoration = new MyLinearDeviderDecoration(mContext);
        myLinearDeviderDecoration.setMarginStart((int) DeviceUtils.dpToPixel(mContext, 15));
        mRecyclerView.addItemDecoration(myLinearDeviderDecoration);

        adapter = new SelectTaskNodeAdapter(null);
        setAdapter(adapter);
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        View inflate = View.inflate(mContext, R.layout.view_workbench_empty, null);
        adapter.setEmptyView(inflate);
        mRecyclerView.setAdapter(adapter);
    }
    /**
     * 设置新数据
     *
     * @param list
     */
    public void setNewData(List<NodeBean> list) {
        adapter.setNewData(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 列
     *
     * @param visibility
     */
    public void setTempVisibility(boolean visibility) {
        setVisibility(R.id.tv_temp, visibility);
        setVisibility(R.id.ll_temp, visibility);
    }

    /**
     * 子列
     *
     * @param visibility
     */
    public void setSubTempVisbility(boolean visibility) {
        setVisibility(R.id.tv_sub_temp, visibility);
        setVisibility(R.id.ll_sub_temp, visibility);
    }

    public void setNodeName(String nodeName) {
        TextUtil.setText(tvGroup, nodeName);
    }
    public void setSubNodeName(String subNodeName) {
        TextUtil.setText(tvTemp, subNodeName);
    }
    public void setSubTempName(String subTempName) {
        TextUtil.setText(tvSubTemp, subTempName);
    }


    public void clearSubNode() {
        TextUtil.setText(tvTemp,"");
    }

    public void clearSubTemp() {
        TextUtil.setText(tvSubTemp,"");
    }

}

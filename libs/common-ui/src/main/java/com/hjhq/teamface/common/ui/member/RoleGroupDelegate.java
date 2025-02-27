package com.hjhq.teamface.common.ui.member;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.view.recycler.MyLinearDeviderDecoration;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.R;


/**
 * 角色 视图代理类
 * Created by lx on 2017/8/31.
 */

public class RoleGroupDelegate extends AppDelegate {
    RecyclerView mRvNavi;
    RecyclerView mRvDataList;

    @Override
    public int getRootLayoutId() {
        return R.layout.role_group_layout;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }


    @Override
    public void initWidget() {
        super.initWidget();

        mRvDataList = get(R.id.rv_role_group);
        mRvDataList.addItemDecoration(new MyLinearDeviderDecoration(mContext));
        mRvDataList.setLayoutManager(new LinearLayoutManager(mContext));

        mRvNavi = get(R.id.rv_navi);
        LinearLayoutManager naviLm = new LinearLayoutManager(mContext);
        naviLm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvNavi.setLayoutManager(naviLm);
    }

    public void setNaviAdapter(BaseQuickAdapter adapter) {
        mRvNavi.setAdapter(adapter);
    }

    public void setDataAdapter(BaseQuickAdapter adapter) {
        if (mRvDataList.getAdapter() != null) {

            mRvDataList.swapAdapter(adapter, true);
        } else {
            mRvDataList.setAdapter(adapter);
        }
    }

}

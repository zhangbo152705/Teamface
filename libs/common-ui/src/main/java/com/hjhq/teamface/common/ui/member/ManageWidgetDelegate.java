package com.hjhq.teamface.common.ui.member;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.R;


/**
 * 选人、选部门、选角色、动态 视图代理类
 *
 * @author lx
 * @date 2017/8/31
 */

public class ManageWidgetDelegate extends AppDelegate {
    public RecyclerView mRecyclerView1;
    public RecyclerView mRecyclerView2;
    public EditText mEditText;


    @Override
    public int getRootLayoutId() {
        return R.layout.manage_widget_layout;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle("添加模块");
        setRightMenuTexts(R.color.app_blue, getActivity().getResources().getString(R.string.confirm));
        mRecyclerView1 = get(R.id.rv1);
        mRecyclerView2 = get(R.id.rv2);
        mEditText = get(R.id.et);
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }
}

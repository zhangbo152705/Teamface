package com.hjhq.teamface.customcomponent.widget2.select;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.adapter.SelectAdapter;

/**
 * Created by Administrator on 2018/7/10.
 */

public class MultiGradeSelectDelegate extends AppDelegate {
    @Override
    public int getRootLayoutId() {
        return R.layout.custom_multi_grage_select_layout;
    }

    RecyclerView mRecyclerView1;
    RecyclerView mRecyclerView2;
    RecyclerView mRecyclerView3;

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle("请选择");
        setRightMenuTexts(R.color.app_blue, mContext.getString(R.string.confirm));
        mRecyclerView1 = get(R.id.rv1);
        mRecyclerView2 = get(R.id.rv2);
        mRecyclerView3 = get(R.id.rv3);
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView3.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void setAdapter1(SelectAdapter adapter) {
        mRecyclerView1.setAdapter(adapter);
    }

    public void setAdapter2(SelectAdapter adapter) {
        mRecyclerView2.setAdapter(adapter);
    }

    public void setAdapter3(SelectAdapter adapter) {
        mRecyclerView3.setAdapter(adapter);
    }

    public void levelChange(int level) {
        switch (level) {
            case 1:
                mRecyclerView1.setVisibility(View.VISIBLE);
                mRecyclerView2.setVisibility(View.GONE);
                mRecyclerView3.setVisibility(View.GONE);

                break;
            case 2:
                mRecyclerView1.setVisibility(View.GONE);
                mRecyclerView2.setVisibility(View.VISIBLE);
                mRecyclerView3.setVisibility(View.GONE);
                break;
            case 3:
                mRecyclerView1.setVisibility(View.GONE);
                mRecyclerView2.setVisibility(View.GONE);
                mRecyclerView3.setVisibility(View.VISIBLE);
                break;
        }
    }
}

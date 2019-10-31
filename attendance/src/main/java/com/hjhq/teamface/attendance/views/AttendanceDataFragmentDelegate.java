package com.hjhq.teamface.attendance.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;


/**
 * Created by Administrator on 2018/3/21.
 * Describe：
 */

public class AttendanceDataFragmentDelegate extends AppDelegate {
    private RecyclerView mRecyclerView;
    private TextView mTvNum;
    private TextView mTvDate;
    public RelativeLayout rl_choose;
    public SmartRefreshLayout mRefreshLayout;

    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_data_fragment_mine;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mRefreshLayout = get(R.id.refresh_all_2);
        mRefreshLayout.setEnableLoadMore(false);
        mRecyclerView = get(R.id.recycler_view);
        mTvNum = get(R.id.header1_text2);
        mTvDate = get(R.id.header1_text1);
        rl_choose = get(R.id.rl_choose);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setItemListener(RecyclerView.OnItemTouchListener l) {
        mRecyclerView.addOnItemTouchListener(l);

    }


    public void setItemClickListener(SimpleItemClickListener simpleItemClickListener) {
        mRecyclerView.addOnItemTouchListener(simpleItemClickListener);
    }

    public void setNum(String attendance_person_number) {
        TextUtil.setText(mTvNum, attendance_person_number + "人参与考勤");
    }

    public void setContent(String content) {
        TextUtil.setText(mTvNum, content);
    }

    public void setDate(int type, long timeInMillis) {
        switch (type) {
            case 1:
                TextUtil.setText(mTvDate, DateTimeUtil.longToStr(timeInMillis, "yyyy.MM.dd"));
                break;
            case 2:
                TextUtil.setText(mTvDate, DateTimeUtil.longToStr(timeInMillis, "yyyy.MM"));
                break;
            case 3:
                TextUtil.setText(mTvDate, DateTimeUtil.longToStr(timeInMillis, "yyyy.MM"));
                break;
        }

    }
}

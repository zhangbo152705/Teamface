package com.hjhq.teamface.attendance.views;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.view.MineGridView;
import com.hjhq.teamface.common.view.PieView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;


/**
 * Created by Administrator on 2018/3/21.
 * Describe：
 */

public class AttendanceDayDataFragmentDelegate extends AppDelegate {
    private RecyclerView mRecyclerView;
    private TextView mTvNum;
    private TextView mTvDate;
    public RelativeLayout rl_choose;
    public SmartRefreshLayout mRefreshLayout;
    public PieView pie_view;
    public TextView tv_nomal_num;
    public RecyclerView module_recycler;
    public MineGridView tableView;
    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_day_data_fragment;
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
        pie_view= get(R.id.pie_view);
        tv_nomal_num = get(R.id.tv_nomal_num);
        tableView = get(R.id.table_viw);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        module_recycler = get(R.id.module_recycler);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        module_recycler.setLayoutManager(mGridLayoutManager);

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

    /**
     * 设置参与考勤人数
     * @param attendance_person_number
     */
    public void setNum(String attendance_person_number) {
        String pieStr = mContext.getResources().getString(R.string.attendance_persion_num)+"\n"+attendance_person_number;
        Spannable str = new SpannableString(pieStr);
        str.setSpan(new AbsoluteSizeSpan(12, true), 0, pieStr.length()-attendance_person_number.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        str.setSpan(new AbsoluteSizeSpan(22, true),pieStr.length()-attendance_person_number.length(), pieStr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv_nomal_num.setText(str);
        mTvNum.setVisibility(View.GONE);
        //TextUtil.setText(mTvNum, attendance_person_number + "人参与考勤");
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

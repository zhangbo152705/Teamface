package com.hjhq.teamface.attendance.views;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：
 */

public class ViewMonthlyDataDelegate extends AppDelegate {
    RecyclerView mRecyclerView;
    RecyclerView mRecyclerView2;
    TextView subTitle;
    TextView tvDetail;
    ImageView ivAvatar;
    TextView tvName;
    TextView tvPos;
    RelativeLayout rlBack;
    TextView tvMonth;
    RelativeLayout rlEmpty;

    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_view_monthly_data_activity;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        rlEmpty = get(R.id.rl_data);
        mRecyclerView = get(R.id.recycler_view);
        mRecyclerView2 = get(R.id.recycler_view2);
        subTitle = get(R.id.sub_title);
        tvDetail = get(R.id.tv_detail);
        ivAvatar = get(R.id.avatar);
        tvName = get(R.id.text1);
        tvPos = get(R.id.text2);
        rlBack = get(R.id.rl_back);
        tvMonth = get(R.id.tv_month);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 7);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        rlBack.setOnClickListener(v -> {
            getActivity().finish();
        });
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setAdapter2(BaseQuickAdapter adapter) {
        mRecyclerView2.setAdapter(adapter);
    }

    public void setItemClick(RecyclerView.OnItemTouchListener l) {
        mRecyclerView.addOnItemTouchListener(l);
    }


    public void setSubTitle(String s) {
        subTitle.setText(s);
    }

    public void setMember(Member member) {
        ImageLoader.loadCircleImage(getActivity(), member.getPicture(), ivAvatar, member.getName());
        tvName.setText(member.getName() + "");
        tvPos.setText("考勤组:");

    }

    public void setGroupName(String member) {

        tvPos.setText("考勤组:" + (TextUtils.isEmpty(member) ? "" : member));

    }

    public void setMenuTexts(String s) {
        TextUtil.setText(tvMonth, s);
    }

    public void setDetailTexts(String s) {
        tvDetail.setVisibility(View.VISIBLE);
        rlEmpty.setVisibility(View.GONE);
        TextUtil.setText(tvDetail, s);
    }

    public void noData() {
        tvDetail.setVisibility(View.GONE);
        rlEmpty.setVisibility(View.VISIBLE);
    }
}

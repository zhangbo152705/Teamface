package com.hjhq.teamface.attendance.views;

import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;




public class AttendanceFragmentDelegate extends AppDelegate {

    TextView mButton;
    RecyclerView mRecyclerView;
    RecyclerView mMoudelView;
    private TextView tvStateInfo;
    private TextView mTvName;
    public TextView mTvAttendanceName;
    private TextView mTvComplete;
    private TextView mTvCompleteInfo;
    private TextView mtvSwitchDay;
    private ImageView mIvAvatar;
    private ImageView mIvState;
    private ImageView mIvComplete;
    public SmartRefreshLayout mRefreshLayout;
    private View infoLayout;

    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_fragment_layout;
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
        mButton = get(R.id.action_btn2);
        mRecyclerView = get(R.id.recycler_view);
        mMoudelView = get(R.id.module_view);
        mTvName = get(R.id.text1);
        mTvAttendanceName = get(R.id.text2);
        mTvComplete = get(R.id.tv_complete);
        mTvCompleteInfo = get(R.id.tv_complete_info);
        infoLayout = get(R.id.info_layout);
        mtvSwitchDay = get(R.id.text3);
        mIvAvatar = get(R.id.avatar);
        mIvState = get(R.id.iv_state);
        mIvComplete = get(R.id.iv_complete);
        mtvSwitchDay.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mtvSwitchDay.getPaint().setAntiAlias(true);//抗锯齿
        tvStateInfo = get(R.id.tv_location_and_wifi_info);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMoudelView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextUtil.setText(mTvName, SPHelper.getUserName());
        ImageLoader.loadCircleImage(getActivity(), SPHelper.getUserAvatar(), mIvAvatar, SPHelper.getUserName());
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
    public void setMouudelAdapter(BaseQuickAdapter adapter) {
        mMoudelView.setAdapter(adapter);
    }
    public void setItemClick(RecyclerView.OnItemTouchListener l) {
        mRecyclerView.addOnItemTouchListener(l);
    }
    public void setMouudelItemClick(RecyclerView.OnItemTouchListener l) {
        mMoudelView.addOnItemTouchListener(l);
    }
    
    public void setStateInfo(String text) {
        TextUtil.setText(tvStateInfo, text);
    }

    public void setTimeAndAction(int i,int type) {
        //mButton.setText(s);
        switch (i) {
            case 0:
                if (type ==0){
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_orange_bg));
                }else {
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_orange_click_bg));
                }
                break;
            case 1:
                if (type ==0){
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_blue_bg));
                }else {
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_blue_click_bg));
                }
                break;
            case 2:
                if (type ==0){
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_green_bg));
                }else {
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_green_click_bg));
                }
                break;
            case 3:
                if (type ==0){
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_orange_bg));
                }else {
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_orange_click_bg));
                }
                break;
            case 4:
                if (type ==0){
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_gray_bg));
                }else {
                    mButton.setBackground(getActivity().getResources().getDrawable(R.drawable.attendance_gray_click_bg));
                }
                break;
        }
    }

    public void setTimeString(Spannable s){
        mButton.setText(s);
    }

    public void setDate(long timeInMillis) {
        TextUtil.setText(mtvSwitchDay, DateTimeUtil.longToStr(timeInMillis, "yyyy-MM-dd"));
    }

    public void setAttendanceGroupName(String name) {
        TextUtil.setText(mTvAttendanceName, "考勤组:" + (TextUtils.isEmpty(name) ? "" : name)
                +"  ("+mContext.getResources().getString(R.string.attendance_check_rolus)+")");
    }

    public void setWifiLocationInfo(int punchcardWay, int isOutworker, String currentWifiName, String address) {
        if (isOutworker == 1) {
            if (punchcardWay == 1) {
                //wifi
                TextUtil.setText(tvStateInfo, "已连接打卡WiFi:" + currentWifiName);

            } else if (punchcardWay == 0) {
                //定位
                TextUtil.setText(tvStateInfo, "已进入打卡范围:" + address);
            }
            mIvState.setImageResource(R.drawable.icon_selected);
        } else {
            if (punchcardWay == 1) {
                //wifi
                TextUtil.setText(tvStateInfo, "未连接打卡WiFi");

            } else if (punchcardWay == 0) {
                //定位
                TextUtil.setText(tvStateInfo, "未进入打卡范围");
            }
            mIvState.setImageResource(R.drawable.icon_delete_red);
        }


    }

    public void setWifiLocationInfo(boolean wifiOk, boolean locationOk, String currentWifiName, String address) {
        if (wifiOk || locationOk) {
            if (wifiOk) {
                //wifi
                TextUtil.setText(tvStateInfo, "已连接打卡WiFi:" + currentWifiName);
            } else if (locationOk) {
                //定位
                TextUtil.setText(tvStateInfo, "已进入打卡范围:" + address);
            }
            mIvState.setImageResource(R.drawable.icon_selected);
        } else {
            TextUtil.setText(tvStateInfo, "未进入打卡范围/未连接打卡WiFi");
            mIvState.setImageResource(R.drawable.icon_delete_red);
        }


    }

    public void attendanceComplete(boolean normal) {
        get(R.id.rl_action).setVisibility(View.GONE);
        get(R.id.ll_complete).setVisibility(View.VISIBLE);
        if (normal) {
            mIvComplete.setImageResource(R.drawable.attendance_normal);
            mTvCompleteInfo.setText("工作辛苦了，下班好好休息下！");
            mTvComplete.setTextColor(getActivity().getResources().getColor(R.color.attendance_bg_12));
        } else {
            mIvComplete.setImageResource(R.drawable.attendance_abnormal);
            mTvComplete.setTextColor(getActivity().getResources().getColor(R.color.attendance_bg_22));
            mTvCompleteInfo.setText("考勤有异常，记得和领导说明原因！");
        }
    }

    public void hideActionBtn(boolean hide) {
        if (hide) {
            infoLayout.setVisibility(View.GONE);
        } else {
            infoLayout.setVisibility(View.VISIBLE);
        }
    }
}

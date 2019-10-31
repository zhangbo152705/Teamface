package com.hjhq.teamface.attendance.adapter;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.DayItemBean;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;


public class TodayAttendanceAdapter2 extends BaseQuickAdapter<DayItemBean, BaseViewHolder> {

    public TodayAttendanceAdapter2(List<DayItemBean> data) {
        super(R.layout.attendance_state_item, data);


    }


    @Override
    protected void convert(BaseViewHolder helper, DayItemBean item) {
        View line = helper.getView(R.id.line);
        if (helper.getAdapterPosition() == getData().size() - 1) {
            line.setVisibility(View.INVISIBLE);
        } else {
            line.setVisibility(View.VISIBLE);
        }

        TextView tvNode = helper.getView(R.id.tv_node);
        ImageView ivNode = helper.getView(R.id.iv_node);
        if ("1".equals(item.getPunchcardType())) {
            TextUtil.setText(tvNode, "上班时间" + DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpectPunchcardTime()), "HH:mm"));
        } else if ("2".equals(item.getPunchcardType())) {
            TextUtil.setText(tvNode, "下班时间" + DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpectPunchcardTime()), "HH:mm"));
        }
        TextView tvTime = helper.getView(R.id.tv_time);
        Long realTime = TextUtil.parseLong(item.getRealPunchcardTime(), 0L);
        if (realTime > 0L) {
            tvTime.setVisibility(View.VISIBLE);
            TextUtil.setText(tvTime, "打卡时间" + DateTimeUtil.longToStr(TextUtil.parseLong(item.getRealPunchcardTime()), "HH:mm"));
        } else {
            tvTime.setVisibility(View.GONE);
        }


        TextView tvState1 = helper.getView(R.id.tv_state1);
        TextView tvState2 = helper.getView(R.id.tv_state2);
        if ("1".equals(item.getIsOutworker())) {
            tvState2.setVisibility(View.VISIBLE);
        } else {
            tvState2.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(item.getPunchcardState())) {
            tvState1.setVisibility(View.GONE);
        } else {
            tvState1.setVisibility(View.VISIBLE);
            String state = "";
            switch (item.getPunchcardState()) {
                case "0":
                    tvState1.setVisibility(View.GONE);
                    break;
                case "1":
                    state = "正常";
                    tvState1.setTextColor(helper.getConvertView().getContext().getResources().getColor(R.color.attendance_state_1));
                    tvState1.setBackgroundResource(R.drawable.attendance_stroke_bg_state1);
                    break;
                case "2":
                    state = "迟到";
                    tvState1.setTextColor(helper.getConvertView().getContext().getResources().getColor(R.color.attendance_state_2));
                    tvState1.setBackgroundResource(R.drawable.attendance_stroke_bg_state2);

                    break;
                case "3":
                    state = "早退";
                    tvState1.setTextColor(helper.getConvertView().getContext().getResources().getColor(R.color.attendance_state_3));
                    tvState1.setBackgroundResource(R.drawable.attendance_stroke_bg_state3);

                    break;
                case "4":
                    state = "旷工";
                    tvState1.setTextColor(helper.getConvertView().getContext().getResources().getColor(R.color.attendance_state_4));
                    tvState1.setBackgroundResource(R.drawable.attendance_stroke_bg_state4);
                    break;
                case "5":
                    state = "缺卡";
                    tvState1.setTextColor(helper.getConvertView().getContext().getResources().getColor(R.color.attendance_state_5));
                    tvState1.setBackgroundResource(R.drawable.attendance_stroke_bg_state5);

                    break;
                default:
                    tvState1.setVisibility(View.GONE);
                    break;
            }
            //字体颜色与背景

            TextUtil.setText(tvState1, state);
        }


        TextView address = helper.getView(R.id.tv_verify_desc);
        TextUtil.setText(address, item.getIsWayInfo());
        ImageView ivType = helper.getView(R.id.iv_verify_type);
        if ("1".equals(item.getIsWay())) {
            ivType.setImageResource(R.drawable.attendance_wifi);
            helper.setVisible(R.id.rl_verify, true);
        } else if ("1".equals(item.getIsWay())) {
            ivType.setImageResource(R.drawable.attendance_location);
            helper.setVisible(R.id.rl_verify, true);
        } else {
            helper.setVisible(R.id.rl_verify, false);
        }

        TextView updateTime = helper.getView(R.id.tv_update_time);
        TextPaint paint = updateTime.getPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        if (helper.getAdapterPosition() == 0) {
            helper.setVisible(R.id.header, true);
        } else {
            helper.setVisible(R.id.header, false);
        }

        if ("0".equals(item.getIsOutworker())) {
            ivNode.setImageResource(R.drawable.attendance_wai);
        } else {
            //punchcard_status	int	打卡状态，0:未打卡,1:正常,2:迟到,3:早退,4:旷工,5:缺卡
            final String punchcard_status = item.getPunchcardState();
            switch (punchcard_status) {
                case "0":
                    ivNode.setImageResource(R.drawable.attendance_wu);
                    break;
                case "1":
                    ivNode.setImageResource(R.drawable.attendance_ok);
                    break;
                case "2":
                    ivNode.setImageResource(R.drawable.attendance_chi);
                    break;
                case "3":
                    ivNode.setImageResource(R.drawable.attendance_zao);
                    break;
                case "4":
                    ivNode.setImageResource(R.drawable.attendance_kuang);
                    break;
                case "5":
                    ivNode.setImageResource(R.drawable.attendance_que);
                    break;
            }
        }
        /*helper.addOnClickListener(R.id.tv_update_time);
        helper.setVisible(R.id.tv_update_time, false);
        if (item.isCanUpdate() && helper.getAdapterPosition() == getData().size() - 1 && "2".equals(item.getPunchcard_type())) {
            helper.setVisible(R.id.tv_update_time, true);
        }*/
        /*helper.getView(R.id.tv_update_time).setOnClickListener(v -> {
            ToastUtils.showToast(v.getContext(), "更新打卡");
            CommonUtil.startActivtiyForResult(helper.getConvertView().getContext(), AddApprovalActivity.class, Constants.REQUEST_CODE1);
        });*/


    }
}
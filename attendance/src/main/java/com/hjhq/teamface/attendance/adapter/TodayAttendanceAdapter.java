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
import com.hjhq.teamface.attendance.bean.AttendanceDataItemBean;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;


public class TodayAttendanceAdapter extends BaseQuickAdapter<AttendanceDataItemBean, BaseViewHolder> {

    public TodayAttendanceAdapter(List<AttendanceDataItemBean> data) {
        super(R.layout.attendance_state_item, data);


    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceDataItemBean item) {
        View line = helper.getView(R.id.line);
        if (helper.getAdapterPosition() == getData().size() - 1) {
            line.setVisibility(View.INVISIBLE);
        } else {
            line.setVisibility(View.VISIBLE);
        }

        TextView tvNode = helper.getView(R.id.tv_node);
        ImageView ivNode = helper.getView(R.id.iv_node);
        String timeString = DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpect_punchcard_time()), "HH:mm");
        if ("1".equals(item.getPunchcard_type())) {
            TextUtil.setText(tvNode, "上班时间" + timeString);
        } else if ("2".equals(item.getPunchcard_type())) {
            TextUtil.setText(tvNode, "下班时间" + timeString);
        }
        TextView tvTime = helper.getView(R.id.tv_time);
        Long realTime = TextUtil.parseLong(item.getReal_punchcard_time(), 0L);
        if (realTime > 0L) {
            tvTime.setVisibility(View.VISIBLE);
            TextUtil.setText(tvTime, "打卡时间" + DateTimeUtil.longToStr(TextUtil.parseLong(item.getReal_punchcard_time()), "HH:mm"));
        } else {
            tvTime.setVisibility(View.GONE);
        }


        TextView tvState = helper.getView(R.id.tv_state1);
        TextView tvState2 = helper.getView(R.id.tv_state2);
        TextView tvRemark = helper.getView(R.id.tv_remark);
        TextView tvApprove = helper.getView(R.id.tv_approve);
        if (TextUtils.isEmpty(item.getPunchcard_result())) {
            tvState.setVisibility(View.GONE);
        } else {
            tvState.setVisibility(View.VISIBLE);
            TextUtil.setText(tvState, item.getPunchcard_result());
        }
        TextView address = helper.getView(R.id.tv_verify_desc);
        TextUtil.setText(address, item.getPunchcard_address());
        ImageView ivType = helper.getView(R.id.iv_verify_type);
        if ("1".equals(item.getIs_way())) {
            ivType.setImageResource(R.drawable.attendance_wifi);
            helper.setVisible(R.id.rl_verify, true);
        } else if ("0".equals(item.getIs_way())) {
            ivType.setImageResource(R.drawable.attendance_location);
            helper.setVisible(R.id.rl_verify, true);
        } else {
            helper.setVisible(R.id.rl_verify, false);
        }

        TextView updateTime = helper.getView(R.id.tv_update_time);
        TextPaint paint = updateTime.getPaint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextPaint paint2 = tvApprove.getPaint();
        paint2.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint2.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextPaint paint3 = tvRemark.getPaint();
        paint3.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint3.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        if (helper.getAdapterPosition() == 0) {
            helper.setVisible(R.id.header, true);
        } else {
            helper.setVisible(R.id.header, false);
        }

        if ("0".equals(item.getIs_outworker())) {
            ivNode.setImageResource(R.drawable.attendance_wai);
            tvState2.setVisibility(View.VISIBLE);
            tvRemark.setVisibility(View.VISIBLE);

        } else {
            tvRemark.setVisibility(View.GONE);
            tvState2.setVisibility(View.GONE);
        }
        tvApprove.setVisibility(View.GONE);//zzh->ad:默认隐藏
        //punchcard_status	int	打卡状态，0:未打卡,1:正常,2:迟到,3:早退,4:旷工,5:缺卡,7:请假,8:出差,9:外出
        final String punchcard_status = item.getPunchcard_status();
        switch (punchcard_status) {
            case "0":
                ivNode.setImageResource(R.drawable.attendance_wu);
                break;
            case "1":
                ivNode.setImageResource(R.drawable.attendance_ok);
                tvState.setTextColor(mContext.getResources().getColor(R.color.attendance_state_1));
                tvState.setBackgroundResource(R.drawable.attendance_stroke_bg_state1);
                if (!TextUtils.isEmpty(item.getBean_name()) && !TextUtils.isEmpty(item.getData_id())) {
                    tvApprove.setVisibility(View.VISIBLE);
                    tvApprove.setText("申请补卡(审批通过)");
                        /*tvApprove.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.MODULE_BEAN, item.getBean_name());
                            bundle.putString(Constants.DATA_ID, item.getData_id());
                            bundle.putInt(ApproveConstants.APPROVE_TYPE, 0);
                            UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/detail", bundle);
                        });*/
                }
                break;
            case "2":
                ivNode.setImageResource(R.drawable.attendance_chi);
                tvState.setBackgroundResource(R.drawable.attendance_stroke_bg_state2);
                tvState.setTextColor(mContext.getResources().getColor(R.color.attendance_state_2));
                break;
            case "3":
                ivNode.setImageResource(R.drawable.attendance_zao);
                tvState.setBackgroundResource(R.drawable.attendance_stroke_bg_state3);
                tvState.setTextColor(mContext.getResources().getColor(R.color.attendance_state_3));
                break;
            case "4":
                ivNode.setImageResource(R.drawable.attendance_kuang);
                tvState.setBackgroundResource(R.drawable.attendance_stroke_bg_state5);
                tvState.setTextColor(mContext.getResources().getColor(R.color.attendance_state_5));
                break;
            case "5":
                ivNode.setImageResource(R.drawable.attendance_que);
                tvState.setBackgroundResource(R.drawable.attendance_stroke_bg_state4);
                tvState.setTextColor(mContext.getResources().getColor(R.color.attendance_state_4));
                if (!TextUtils.isEmpty(item.getBean_name())) {
                    tvApprove.setVisibility(View.VISIBLE);
                    tvApprove.setText("申请补卡");
                      /*  tvApprove.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.MODULE_BEAN, item.getBean_name());
                            UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE);
                        });*/
                }
                helper.setVisible(R.id.tv_update_time, false);
                item.setCanUpdate(false);
                break;
        }
        helper.addOnClickListener(R.id.tv_update_time);
        helper.addOnClickListener(R.id.tv_approve);
        helper.addOnClickListener(R.id.tv_remark);
        helper.setVisible(R.id.tv_update_time, false);
        //zzh->ad:增加punchcard_status为5,7,8,9时 不显示更新打卡
        //&&!punchcard_status.equals("5")  && !punchcard_status.equals("7") && !punchcard_status.equals("8") && !punchcard_status.equals("9")

        if (item.isCanUpdate() && helper.getAdapterPosition() == getData().size() - 1 && "2".equals(item.getPunchcard_type()) ) {
            helper.setVisible(R.id.tv_update_time, true);
        }
        /*helper.getView(R.id.tv_update_time).setOnClickListener(v -> {
            ToastUtils.showToast(v.getContext(), "更新打卡");
            CommonUtil.startActivtiyForResult(helper.getConvertView().getContext(), AddApprovalActivity.class, Constants.REQUEST_CODE1);
        });*/


    }
}
package com.hjhq.teamface.attendance.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AddLeaveingLateBean;
import com.hjhq.teamface.basis.util.NumberFormatUtil;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;


public class LeavingLateAdapter extends BaseQuickAdapter<AddLeaveingLateBean, BaseViewHolder> {

    public LeavingLateAdapter(List<AddLeaveingLateBean> data) {
        super(R.layout.attendance_additional_settings_type1_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, AddLeaveingLateBean item) {
        helper.setText(R.id.title,
                String.format(helper.getConvertView().getContext().getString(R.string.attendance_rules_label), NumberFormatUtil.integerToString(helper.getAdapterPosition() + 1)));
        helper.addOnClickListener(R.id.delete);
        helper.addOnClickListener(R.id.set);
        helper.setText(R.id.text1, String.format(helper.getConvertView().getContext().getString(R.string.attendance_leave_time)
                , TextUtil.parseInt(item.getNigthwalkmin())));
        helper.setText(R.id.text2, String.format(helper.getConvertView().getContext().getString(R.string.attendance_arrival_time), TextUtil.parseInt(item.getLateMin())));
        helper.setText(R.id.desc, String.format(helper.getConvertView().getContext().getString(R.string.attendance_leave_arrival_time)
                ,TextUtil.parseInt(item.getNigthwalkmin()) , TextUtil.parseInt(item.getLateMin())));
        // helper.setText(R.id.text1, format(helper.getConvertView().getContext(), R.string.attendance_leave_time, item.getLeaveingLateTime()));
    }

    public String format(Context context, int stringRes, int... value) {
        return String.format(context.getString(stringRes), value);

    }

}
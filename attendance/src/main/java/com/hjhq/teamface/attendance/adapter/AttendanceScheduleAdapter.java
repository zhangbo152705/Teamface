package com.hjhq.teamface.attendance.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceDataBean;

import java.util.List;


public class AttendanceScheduleAdapter extends BaseQuickAdapter<AttendanceDataBean, BaseViewHolder> {

    public AttendanceScheduleAdapter(List<AttendanceDataBean> data) {
        super(R.layout.attendance_calendar_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceDataBean item) {
        TextView date = helper.getView(R.id.tv_date);
        View dot = helper.getView(R.id.dot);
        helper.setText(R.id.tv_date, item.getDate());
        helper.setVisible(R.id.dot, false);
        date.setTextColor(Color.parseColor("#8C96AB"));
        date.setBackgroundResource(R.color.white);
        if (item.isToday()) {
            date.setTextColor(Color.parseColor("#667490"));
            date.setBackgroundResource(R.drawable.attendance_default_date_bg);
        }
        if (item.isSelected()) {
            date.setTextColor(Color.parseColor("#FFFFFF"));
            date.setBackgroundResource(R.drawable.attendance_selected_date_bg);
        }
        if (item.getData() != null && !TextUtils.isEmpty(item.getData().getId())) {
            helper.setVisible(R.id.dot, true);
            dot.setBackgroundResource(R.drawable.attendance_gray_dot_bg);
        } else {
            helper.setVisible(R.id.dot, false);
        }

    }
}
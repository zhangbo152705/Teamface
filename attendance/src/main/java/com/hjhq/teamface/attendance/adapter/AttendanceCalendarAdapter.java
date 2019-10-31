package com.hjhq.teamface.attendance.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.MonthDataItem;

import java.util.List;


public class AttendanceCalendarAdapter extends BaseQuickAdapter<MonthDataItem, BaseViewHolder> {

    public AttendanceCalendarAdapter(List<MonthDataItem> data) {
        super(R.layout.attendance_calendar_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, MonthDataItem item) {
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
        //当日状态（0正常、1异常、2不需打卡、3未到日期）
        if (TextUtils.isEmpty(item.getState())) {
            helper.setVisible(R.id.dot, false);
        } else {
            switch (item.getState()) {
                case "0":
                    helper.setVisible(R.id.dot, true);
                    dot.setBackgroundResource(R.drawable.attendance_gray_dot_bg);
                    break;
                case "1":
                    helper.setVisible(R.id.dot, true);
                    dot.setBackgroundResource(R.drawable.attendance_red_dot_bg);
                    break;
                case "2":
                    helper.setVisible(R.id.dot, false);
                    break;
                case "3":
                    helper.setVisible(R.id.dot, false);
                    break;
            }
        }


    }
}
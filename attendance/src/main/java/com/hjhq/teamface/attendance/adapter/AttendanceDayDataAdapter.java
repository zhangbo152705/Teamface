package com.hjhq.teamface.attendance.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.IconBean;

import java.util.List;


public class AttendanceDayDataAdapter extends BaseQuickAdapter<IconBean, BaseViewHolder> {

    public AttendanceDayDataAdapter(List<IconBean> data) {
        super(R.layout.attendance_day_data_grid_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, IconBean item) {
        helper.setText(R.id.text, item.getName());
       // helper.setBackgroundColor(R.id.image, Color.parseColor(item.getColor()));
        GradientDrawable myGrad = (GradientDrawable)helper.getView(R.id.image).getBackground();
        myGrad.setColor(Color.parseColor(item.getColor()));
    }

}
package com.hjhq.teamface.attendance.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;

import java.util.List;


public class DayDataAdapter extends BaseQuickAdapter<AttendanceDayDataBean.DataListBean, BaseViewHolder> {

    public DayDataAdapter(List<AttendanceDayDataBean.DataListBean> data) {
        super(R.layout.attendance_day_data_item, data);


    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceDayDataBean.DataListBean item) {
        String name = item.getName();
        helper.setText(R.id.text1, name);
        helper.setText(R.id.text2, item.getNumber());

    }
}
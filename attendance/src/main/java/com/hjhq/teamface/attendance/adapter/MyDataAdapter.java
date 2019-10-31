package com.hjhq.teamface.attendance.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;

import java.util.List;


public class MyDataAdapter extends BaseQuickAdapter<AttendanceMonthDataBean.DataBean.DataListBean, BaseViewHolder> {

    public MyDataAdapter(List<AttendanceMonthDataBean.DataBean.DataListBean> data) {
        super(R.layout.attendance_day_data_item, data);


    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceMonthDataBean.DataBean.DataListBean item) {
        String name = item.getName();
        String unit = "次";
        switch (item.getType()) {
            case "1":
                name = "迟到";
                break;
            case "2":
                name = "早退";
                break;
            case "3":
                name = "缺卡";
                break;
            case "4":
                name = "旷工";
                break;
            case "5":
                name = "外勤打卡";
                break;
            case "6":
                name = item.getName();
                break;
            case "7":
                name = "正常打卡";
                unit = "天";
                break;
            case "8":
                name = item.getName();
                break;
        }
        helper.setText(R.id.text1, item.getName());
        helper.setText(R.id.text2, item.getNumber() + unit);

    }
}
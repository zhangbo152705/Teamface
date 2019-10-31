package com.hjhq.teamface.attendance.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;
import com.hjhq.teamface.basis.image.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class MonthDataAdapter extends BaseQuickAdapter<AttendanceMonthDataBean.EmployeeListBean, BaseViewHolder> {
    int type;

    public MonthDataAdapter(int type, List<AttendanceMonthDataBean.EmployeeListBean> data) {
        super(R.layout.attendance_month_data_item, data);
        this.type = type;
    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceMonthDataBean.EmployeeListBean item) {
        helper.setText(R.id.tv_name, item.getEmployee_name());
        helper.setText(R.id.tv_pos, item.getPost_name());
        helper.setText(R.id.tv_num, item.getCount());
        View view = helper.getView(R.id.rl_list);
        RecyclerView rv = helper.getView(R.id.rv);
        ImageView icon = helper.getView(R.id.next);
        ImageView avatar = helper.getView(R.id.avatar);
        ImageLoader.loadCircleImage(helper.getConvertView().getContext(), item.getPicture(), avatar, item.getEmployee_name());
        ArrayList<AttendanceMonthDataBean.AttendanceListBean> attendanceList = item.getAttendanceList();
        if (attendanceList != null && attendanceList.size() > 0) {
            for (int i = 0; i < attendanceList.size(); i++) {
                attendanceList.get(i).setType(type);
            }
        }
        MonthDataSubAdapter subAdapter = new MonthDataSubAdapter(type, attendanceList);
        rv.setAdapter(subAdapter);
        rv.setLayoutManager(new LinearLayoutManager(helper.getConvertView().getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        helper.getView(R.id.rl13).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.getVisibility() == View.VISIBLE) {
                    view.setVisibility(View.GONE);
                    // icon.setImageResource(R.drawable.icon_arrow_down);
                } else {
                    view.setVisibility(View.VISIBLE);
                    //icon.setImageResource(R.drawable.icon_arrow_up);
                }
            }
        });

    }


}
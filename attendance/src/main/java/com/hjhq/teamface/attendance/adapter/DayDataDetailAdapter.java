package com.hjhq.teamface.attendance.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.basis.image.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class DayDataDetailAdapter extends BaseQuickAdapter<AttendanceDayDataBean.DataListBean, BaseViewHolder> {

    int type;

    public DayDataDetailAdapter(int type, List<AttendanceDayDataBean.DataListBean> data) {
        super(R.layout.attendance_day_data_detail_item, data);
        this.type = type;
    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceDayDataBean.DataListBean item) {
        helper.setText(R.id.tv_name, item.getEmployee_name());
        helper.setText(R.id.tv_pos, item.getPost_name());
        helper.setText(R.id.tv_num, item.getNumber());
        ImageView avatar = helper.getView(R.id.avatar);
        ImageLoader.loadCircleImage(helper.getConvertView().getContext(), item.getPicture(), avatar, item.getEmployee_name());
        if (type == 4) {
            helper.setVisible(R.id.tv_state, true);
        } else {
            helper.setVisible(R.id.tv_state, false);
        }
        View view = helper.getView(R.id.rl_list);
        RecyclerView rv = helper.getView(R.id.rv);
        ImageView icon = helper.getView(R.id.next);
        ArrayList<AttendanceDayDataBean.DataListBean> attendanceList = item.getAttendanceList();
        if (attendanceList != null && attendanceList.size() > 0) {
            for (int i = 0; i < attendanceList.size(); i++) {
                attendanceList.get(i).setType(type + "");
            }
        }
        DayDataSubAdapter subAdapter = new DayDataSubAdapter(type, attendanceList);
        rv.setAdapter(subAdapter);
        rv.setLayoutManager(new LinearLayoutManager(helper.getConvertView().getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }
}
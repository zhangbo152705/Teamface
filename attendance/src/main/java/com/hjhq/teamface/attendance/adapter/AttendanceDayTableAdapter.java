package com.hjhq.teamface.attendance.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.attendance.bean.IconBean;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;

/**
 * Created by Administrator on 2019/6/20.
 */

public class AttendanceDayTableAdapter extends BaseAdapter{

    private Context context;
    private List<AttendanceDayDataBean.DataListBean> dataList;
    public AttendanceDayTableAdapter(Context context,List<AttendanceDayDataBean.DataListBean> dataList) {
        this.context = context;
        this.dataList = dataList;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        if (CollectionUtils.isEmpty(dataList)){
            return null;
        }else {
            return dataList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context,R.layout.attendance_day_data_table_item,null);
            holder.text1 = (TextView) convertView.findViewById(R.id.text1);
            holder.text2 = (TextView) convertView.findViewById(R.id.text2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (TextUtil.isEmpty(dataList.get(position).getName())){
            holder.text1.setText("");
        }else {
            holder.text1.setText(dataList.get(position).getName());
        }
        if (dataList.get(position).getEmployeeList() != null){
            holder.text2.setText(""+dataList.get(position).getEmployeeList().size());
        }else {
            holder.text2.setText("");
        }

        return convertView;
    }

    static class ViewHolder {
        public TextView text1;
        public TextView text2;
    }




}

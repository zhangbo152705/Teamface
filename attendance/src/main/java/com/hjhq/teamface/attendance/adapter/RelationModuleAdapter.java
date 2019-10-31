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
import com.hjhq.teamface.attendance.bean.AttendanceTypeListBean;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;

/**
 * zzh->ad
 */
public class RelationModuleAdapter extends BaseQuickAdapter<AttendanceTypeListBean, BaseViewHolder> {

    public RelationModuleAdapter(List<AttendanceTypeListBean> data) {
        super(R.layout.attendance_relation_module_item, data);


    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceTypeListBean item) {

        TextView tvName = helper.getView(R.id.tv_name);
        TextView tvStartTime = helper.getView(R.id.tv_startime);
        TextView tvEndTime = helper.getView(R.id.tv_endtime);
        ImageView iv_node= helper.getView(R.id.iv_node);

        String name = item.getChinese_name();
        long startTime = item.getStart_time();
        long endTime = item.getEnd_time();
        String relevanceStatus = item.getRelevance_status();
        TextUtil.setText(tvName, name + " : ");
        if (startTime > 0){
            if(endTime>0){
                TextUtil.setText(tvStartTime, DateTimeUtil.longToStr(startTime, "yyyy-MM-dd HH:mm")+"  ~  " );
            }else {
                TextUtil.setText(tvStartTime, DateTimeUtil.longToStr(startTime, "yyyy-MM-dd HH:mm"));
            }

        }
        if (endTime > 0){
            TextUtil.setText(tvEndTime,DateTimeUtil.longToStr(endTime, "yyyy-MM-dd HH:mm"));
        }
        if (relevanceStatus.equals("0") || relevanceStatus.equals("1") || relevanceStatus.equals("2")){
            iv_node.setImageResource(R.drawable.attendance_relation_leave);
        }else if(relevanceStatus.equals("3")){
            iv_node.setImageResource(R.drawable.attendance_relation_travel);
        }else if(relevanceStatus.equals("4")){
            iv_node.setImageResource(R.drawable.attendance_relation_selling);
        }else if(relevanceStatus.equals("5")){
            iv_node.setImageResource(R.drawable.attendance_relation_out);
        }

    }
}
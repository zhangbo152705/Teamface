package com.hjhq.teamface.attendance.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;


public class DayDataSubAdapter extends BaseMultiItemQuickAdapter<AttendanceDayDataBean.DataListBean, BaseViewHolder> {
    private int type;

    public DayDataSubAdapter(int type, List<AttendanceDayDataBean.DataListBean> data) {
        super(data);
        this.type = type;
        //迟到,缺卡,旷工,外勤,请假,早退
       /* case "1":
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
        break;*/
        addItemType(1, R.layout.attendance_day_chidao_item);
        addItemType(2, R.layout.attendance_day_zaotui_item);
        addItemType(3, R.layout.attendance_day_queka_item);
        addItemType(4, R.layout.attendance_day_kuanggong_item);
        addItemType(5, R.layout.attendance_day_waiqin_item);
        addItemType(6, R.layout.attendance_day_qingjia_item);
    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceDayDataBean.DataListBean item) {
        StringBuilder info = new StringBuilder();
        switch (item.getItemType()) {
            case 1:
                if (helper.getAdapterPosition() == 0) {
                    helper.setVisible(R.id.tv_time, true);
                } else {
                    helper.setVisible(R.id.tv_time, false);
                }
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getReal_punchcard_time()), "yyyy年M月d日(EEEE)"));

                info.append("上班时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpect_punchcard_time()), "HH:mm"));
                info.append("  打卡时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getReal_punchcard_time()), "HH:mm"));
                info.append("  迟到");
                long time1 = TextUtil.parseLong(item.getReal_punchcard_time());
                long time2 = TextUtil.parseLong(item.getExpect_punchcard_time());
                String laterTime = item.getLate_time_number();//zzh:新增字段迟到分钟数
                if (TextUtil.isEmpty(laterTime)){
                    info.append(((time1 - time2) / (60 * 1000)) + "");
                }else {
                    info.append(laterTime);
                }
                info.append("分钟");
                helper.setText(R.id.tv_content, info);
                break;
            case 2:
                if (helper.getAdapterPosition() == 0) {
                    helper.setVisible(R.id.tv_time, true);
                } else {
                    helper.setVisible(R.id.tv_time, false);
                }
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getReal_punchcard_time()), "yyyy年M月d日(EEEE)"));

                info.append("下班时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpect_punchcard_time()), "HH:mm"));
                info.append("  打卡时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getReal_punchcard_time()), "HH:mm"));
                info.append("  早退");
                final long time001 = TextUtil.parseLong(item.getReal_punchcard_time());
                final long time002 = TextUtil.parseLong(item.getExpect_punchcard_time());
                String leaveEarlyTime = item.getLeave_early_time_number();
                if (TextUtil.isEmpty(leaveEarlyTime)){
                    info.append(leaveEarlyTime);
                }else {
                    info.append(((time002 - time001) / (60 * 1000)) + "");
                }

                info.append("分钟");
                helper.setText(R.id.tv_content, info);
                break;
            case 3:
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpect_punchcard_time()), "yyyy年M月d日(EEEE)"));
                if ("1".equals(item.getPunchcard_type())) {
                    info.append("上班时间:");
                } else {
                    info.append("下班时间:");
                }
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpect_punchcard_time()), "HH:mm"));
                helper.setText(R.id.tv1, info);
                break;
            case 4:

                break;
            case 5:
                if (helper.getAdapterPosition() == 0) {
                    helper.setVisible(R.id.tv_time, true);
                } else {
                    helper.setVisible(R.id.tv_time, false);
                }
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpect_punchcard_time()), "yyyy年M月d日(EEEE)"));
                if ("1".equals(item.getPunchcard_type())) {
                    info.append("上班时间:");
                } else {
                    info.append("下班时间:");
                }
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getExpect_punchcard_time()), "HH:mm"));
                helper.setText(R.id.tv1, info);
                helper.setText(R.id.tv2, item.getPunchcard_address());
                break;
            case 6:
                // TODO: 2019/3/14 时长会增加新的字段
                String total = "时长:" + TextUtil.parseInt(item.getDuration()) + ("0".equals(item.getDuration_unit()) ? "天" : "小时");
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getAttendance_date()), "yyyy年M月d日(EEEE)") + total);
                info.append("开始时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getStart_time()), "HH:mm"));
                info.append("  结束时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getEnd_time()), "HH:mm"));
                helper.setText(R.id.tv_content, info);
                break;
        }

    }


}
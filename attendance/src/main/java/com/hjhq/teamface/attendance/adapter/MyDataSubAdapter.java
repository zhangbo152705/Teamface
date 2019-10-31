package com.hjhq.teamface.attendance.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;

import java.util.List;


public class MyDataSubAdapter extends BaseMultiItemQuickAdapter<AttendanceMonthDataBean.AttendanceListBean, BaseViewHolder> {
    private int type;

    public MyDataSubAdapter(int type, List<AttendanceMonthDataBean.AttendanceListBean> data) {
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
        addItemType(1, R.layout.attendance_month_chidao_item);
        addItemType(2, R.layout.attendance_month_zaotui_item);
        addItemType(3, R.layout.attendance_month_queka_item);
        addItemType(4, R.layout.attendance_month_kuanggong_item);
        addItemType(5, R.layout.attendance_month_waiqin_item);
        addItemType(6, R.layout.attendance_month_qingjia_item);
        addItemType(7, R.layout.attendance_month_normal_item);
    }


    @Override
    protected void convert(BaseViewHolder helper, AttendanceMonthDataBean.AttendanceListBean item) {
      /*  if (helper.getAdapterPosition() == 0) {
            helper.setVisible(R.id.tv_time, true);
        } else {
            helper.setVisible(R.id.tv_time, false);
        }*/
        StringBuilder info = new StringBuilder();
        switch (item.getItemType()) {
            case 1:
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getAttendanceDate()), "yyyy年M月d日(EEEE) HH:mm"));
                info.append("打卡时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getPunchcardTime()), "HH:mm"));
                info.append("  迟到");
                long t1 = TextUtil.parseLong(item.getPunchcardTime());
                long t2 = TextUtil.parseLong(item.getAttendanceDate());
                info.append(((t1 - t2) / (60 * 1000)) + "");
                info.append("分钟");
                helper.setText(R.id.tv_content, info);
                break;
            case 2:
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getAttendanceDate()), "yyyy年M月d日(EEEE) HH:mm"));
                info.append("打卡时间:");
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getPunchcardTime()), "HH:mm"));
                info.append("  早退");
                info.append((TextUtil.parseLong(item.getAttendanceDate()) - TextUtil.parseLong(item.getPunchcardTime())) / 60 * 1000 + "");
                info.append("分钟");
                helper.setText(R.id.tv_content, info);
                break;
            case 3:
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getAttendanceDate()), "yyyy年M月d日(EEEE) HH:mm"));
                break;
            case 4:
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getAttendanceDate()), "yyyy年M月d日(EEEE)"));
                break;
            case 5:
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getAttendanceDate()), "yyyy年M月d日(EEEE)"));
                if ("1".equals(item.getPunchcardType())) {
                    info.append("上班时间:");
                } else {
                    info.append("下班时间:");
                }
                info.append(DateTimeUtil.longToStr(TextUtil.parseLong(item.getPunchcardTime()), "HH:mm"));
                helper.setText(R.id.tv1, info);
                helper.setText(R.id.tv2, item.getPunchcardAddress());
                break;
            case 6:
                helper.setText(R.id.tv1, "开始时间:" + DateTimeUtil.longToStr(TextUtil.parseLong(item.getStartTime()), "yyyy年M月d日(EEEE) HH:mm"));
                helper.setText(R.id.tv2, "结束时间:" + DateTimeUtil.longToStr(TextUtil.parseLong(item.getEndTime()), "yyyy年M月d日(EEEE) HH:mm"));
                helper.setText(R.id.tv_time, "时长\n" + item.getDuration());
                break;
            case 7:
                helper.setText(R.id.tv_time, DateTimeUtil.longToStr(TextUtil.parseLong(item.getAttendanceDate()), "yyyy年M月d日(EEEE)"));
                break;
        }

    }


}
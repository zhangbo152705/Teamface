package com.hjhq.teamface.attendance.views;

import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.AppDelegate;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：打卡规则,排班管理
 */

public class AttendaceRulesMineDelegate extends AppDelegate {
    private ImageView mIvAvatar;
    private TextView mTvName;
    public TextView mTvAttendanceName;
    public TextView mtvSwitchDay;
    public TextView check_class_time;
    public TextView class_time;//班次
    public TextView on_work_remine;//上班提醒
    public TextView over_work_remine;//下班提醒
    public TextView over_time_rules;//晚到晚走
    public TextView late_number;//允许迟到次数
    public TextView neglect_on_time;//迟到旷工
    public TextView neglect_over_time;//早退旷工
    public TextView work_location;//办公地点
    public TextView work_wifi;//办公wifi
    public RelativeLayout rl_attentance_time;//考勤时间\
    public LinearLayout li_attentance_time;
    public RelativeLayout rl_attentance_rules;//人性化规则
    public LinearLayout li_attentance_rules;
    public RelativeLayout rl_attentance_rangle;//考勤范围
    public LinearLayout li_attentance_rangle;//
    public ImageView iv_attdance_time;
    public ImageView iv_persion_rules;
    public ImageView iv_range;

    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_rules_mine;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mTvName = get(R.id.text1);
        mTvAttendanceName = get(R.id.text2);
        mtvSwitchDay = get(R.id.text3);
        mIvAvatar = get(R.id.avatar);
        check_class_time = get(R.id.check_class_time);
        class_time = get(R.id.class_time);

        on_work_remine = get(R.id.on_work_remine);//上班提醒
        over_work_remine = get(R.id.over_work_remine);
        over_time_rules= get(R.id.over_time_rules);
        late_number= get(R.id.late_number);
        neglect_on_time= get(R.id.neglect_on_time);
        neglect_over_time= get(R.id.neglect_over_time);
        work_location= get(R.id.work_location);
        work_wifi= get(R.id.work_wifi);

        rl_attentance_time = get(R.id.rl_attentance_time);
        li_attentance_time = get(R.id.li_attentance_time);
        rl_attentance_rules = get(R.id.rl_attentance_rules);
        li_attentance_rules = get(R.id.li_attentance_rules);
        rl_attentance_rangle = get(R.id.rl_attentance_rangle);
        li_attentance_rangle = get(R.id.li_attentance_rangle);

        iv_attdance_time = get(R.id.iv_attdance_time);
        iv_persion_rules = get(R.id.iv_persion_rules);
        iv_range = get(R.id.iv_range);


        mtvSwitchDay.setVisibility(View.GONE);
        check_class_time.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextUtil.setText(mTvName, SPHelper.getUserName());
        ImageLoader.loadCircleImage(getActivity(), SPHelper.getUserAvatar(), mIvAvatar, SPHelper.getUserName());

    }

    /**
     * 设置考勤组名称
     * @param name
     */
    public void setAttendanceGroupName(String name) {
        TextUtil.setText(mTvAttendanceName, "考勤组:" + (TextUtils.isEmpty(name) ? "" : name));
    }

    /**
     * 设置班次时间
     * @param classTime
     */
    public void setAttendanceClassTime(String classTime) {
        TextUtil.setText(class_time,classTime);
    }
}

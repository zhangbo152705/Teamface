package com.hjhq.teamface.attendance.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.AttendanceRulesAdapter;
import com.hjhq.teamface.attendance.bean.AddDateBean;
import com.hjhq.teamface.attendance.bean.AddLeaveingLateBean;
import com.hjhq.teamface.attendance.bean.AdditionalSettingDetailBean;
import com.hjhq.teamface.attendance.bean.AttendanceDataItemBean;
import com.hjhq.teamface.attendance.bean.AttendanceInfoBean;
import com.hjhq.teamface.attendance.bean.AttendanceRulesListBean;
import com.hjhq.teamface.attendance.bean.AttendanceTypeListBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendaceRulesMineDelegate;
import com.hjhq.teamface.attendance.views.WorkTimeManageDelegate;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe：我的考勤规则
 */
public class AttendanceRulesMineActivity extends ActivityPresenter<AttendaceRulesMineDelegate, AttendanceModel> implements View.OnClickListener {

    AttendanceInfoBean.DataBean mDataBean;
    private List<AttendanceDataItemBean> dataList = new ArrayList<>();
    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null){
            mDataBean = (AttendanceInfoBean.DataBean) intent.getSerializableExtra(Constants.DATA_TAG1);
        }

    }

    @Override
    public void init() {
        initView();
        getDetailData();

    }
    private void initView() {
        viewDelegate.setTitle(R.string.attendance_rules_mine_manage_title);
        if (mDataBean != null){
            viewDelegate.setAttendanceGroupName(mDataBean.getName());
            String classTime =this.getResources().getString(R.string.attendance_class_time)+" :";
            if (mDataBean.getClass_info() != null){
                classTime = classTime+mDataBean.getClass_info().getClass_desc();
            }
            viewDelegate.setAttendanceClassTime(classTime);
            String adress = "";
            String wifi = "";
            List<AttendanceTypeListBean> attendance_address = mDataBean.getAttendance_address();
            List<AttendanceTypeListBean> attendance_wifi = mDataBean.getAttendance_wifi();
            if (!CollectionUtils.isEmpty(attendance_address)){
                for (AttendanceTypeListBean adressItem : attendance_address){
                    if (TextUtil.isEmpty(adress)){
                        adress = adress+adressItem.getAddress();
                    }else {
                        adress = adress+" \n "+adressItem.getAddress();
                    }
                }
            }

            if (!CollectionUtils.isEmpty(attendance_wifi)){
                for (AttendanceTypeListBean adressItem : attendance_wifi){
                    if (TextUtil.isEmpty(wifi)){
                        wifi = wifi+adressItem.getName();
                    }else {
                        wifi = wifi+" , "+adressItem.getName();
                    }
                }
            }
            viewDelegate.work_location.setText(adress);
            viewDelegate.work_wifi.setText(wifi);
        }
    }

    /**
     * 获取考勤设置详情
     */
    private void getDetailData() {
        model.getSettingDetail(mContext, new ProgressSubscriber<AdditionalSettingDetailBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(AdditionalSettingDetailBean attendanceSettingDetailBean) {
                super.onNext(attendanceSettingDetailBean);
                showData(attendanceSettingDetailBean.getData());
            }
        });
    }
    @SuppressLint("StringFormatMatches")
    public void showData(AdditionalSettingDetailBean.DataBean data){
        int remindBefore = TextUtil.parseInt(data.getRemind_clock_before_work());
        int remindAfter = TextUtil.parseInt(data.getRemind_clock_after_work());
        int maxLateTimes = TextUtil.parseInt(data.getHumanization_allow_late_times());
        int maxLateMinutes = TextUtil.parseInt(data.getHumanization_allow_late_minutes());
        int earlyWouldBeAbsenteeism = TextUtil.parseInt(data.getAbsenteeism_rule_leave_early_minutes());
        int lateWouldBeAbsenteeism = TextUtil.parseInt(data.getAbsenteeism_rule_be_late_minutes());
        List<AddLeaveingLateBean> late_nigth_walk_arr = data.getLate_nigth_walk_arr();

        String mRemindBefore = this.getResources().getString(R.string.attendance_ontime_remine);
        String mRemindAfter = this.getResources().getString(R.string.attendance_overtime_remine);
        String mMaxLateTimes = this.getResources().getString(R.string.attendance_late_go);
        String mLateWouldBeAbsenteeism = this.getResources().getString(R.string.attendance_neglect_late);
        String mEarlyWouldBeAbsenteeism = this.getResources().getString(R.string.attendance_neglect_ealer);
        String mLate_nigth_walk_arr = this.getResources().getString(R.string.attendance_late_go);

        mRemindBefore = String.format(mRemindBefore,remindBefore);//打卡提醒提前
        mRemindAfter = String.format(mRemindAfter,remindAfter);//打卡提醒提前
        mMaxLateTimes = String.format(mMaxLateTimes,maxLateTimes,maxLateMinutes);//人性化班次
        mEarlyWouldBeAbsenteeism = String.format(mEarlyWouldBeAbsenteeism,earlyWouldBeAbsenteeism);//单次早退
        mLateWouldBeAbsenteeism = String.format(mLateWouldBeAbsenteeism,lateWouldBeAbsenteeism);//单次迟到

        String lateNight = "";
        if (!CollectionUtils.isEmpty(late_nigth_walk_arr)){
             for (AddLeaveingLateBean item : late_nigth_walk_arr){
                 String itemStr = String.format(mLate_nigth_walk_arr,TextUtil.parseInt(item.getNigthwalkmin()),TextUtil.parseInt(item.getLateMin()));//单次迟到
                 if (TextUtil.isEmpty(lateNight)){
                     lateNight = lateNight+itemStr;
                 }else {
                     lateNight = lateNight+ "\n"+itemStr;
                 }
             }
        }
        viewDelegate.on_work_remine.setText(mRemindBefore);
        viewDelegate.over_work_remine.setText(mRemindAfter);
        viewDelegate.late_number.setText(mMaxLateTimes);
        viewDelegate.neglect_on_time.setText(mEarlyWouldBeAbsenteeism);
        viewDelegate.neglect_over_time.setText(mLateWouldBeAbsenteeism);
        viewDelegate.over_time_rules.setText(lateNight);
    }












    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();

        viewDelegate.rl_attentance_time.setOnClickListener(this);
        viewDelegate.li_attentance_time.setOnClickListener(this);
        viewDelegate.rl_attentance_rules.setOnClickListener(this);
        viewDelegate.li_attentance_rules.setOnClickListener(this);
        viewDelegate.rl_attentance_rangle.setOnClickListener(this);
        viewDelegate.li_attentance_rangle.setOnClickListener(this);
        viewDelegate.check_class_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
         if(v.getId() == R.id.rl_attentance_time){
             if (viewDelegate.li_attentance_time.getVisibility() == View.VISIBLE){
                 viewDelegate.li_attentance_time.setVisibility(View.GONE);
                 viewDelegate.iv_attdance_time.setSelected(false);
             }else {
                 viewDelegate.li_attentance_time.setVisibility(View.VISIBLE);
                 viewDelegate.iv_attdance_time.setSelected(true);
             }
         }else if (v.getId() == R.id.rl_attentance_rules){
             if (viewDelegate.li_attentance_rules.getVisibility() == View.VISIBLE){
                 viewDelegate.li_attentance_rules.setVisibility(View.GONE);
                 viewDelegate.iv_persion_rules.setSelected(false);
             }else {
                 viewDelegate.li_attentance_rules.setVisibility(View.VISIBLE);
                 viewDelegate.iv_persion_rules.setSelected(true);
             }
         }else if (v.getId() == R.id.rl_attentance_rangle){
             if (viewDelegate.li_attentance_rangle.getVisibility() == View.VISIBLE){
                 viewDelegate.li_attentance_rangle.setVisibility(View.GONE);
                 viewDelegate.iv_range.setSelected(false);
             }else {
                 viewDelegate.li_attentance_rangle.setVisibility(View.VISIBLE);
                 viewDelegate.iv_range.setSelected(true);
             }
         }else if (v.getId() == R.id.check_class_time){
             CommonUtil.startActivtiy(this, ScheduleDetailActivity.class);
         }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }
}

package com.hjhq.teamface.attendance.model;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.attendance.adapter.RangeListBean;
import com.hjhq.teamface.attendance.api.AttendanceApiService;
import com.hjhq.teamface.attendance.bean.AddLeaveingLateRulesListBean;
import com.hjhq.teamface.attendance.bean.AddRulesBean;
import com.hjhq.teamface.attendance.bean.AddTypeBean;
import com.hjhq.teamface.attendance.bean.AddWorkTimeBean;
import com.hjhq.teamface.attendance.bean.AdditionalSettingDetailBean;
import com.hjhq.teamface.attendance.bean.AttendanceApproveDetailBean;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.attendance.bean.AttendanceGroupDetailBean;
import com.hjhq.teamface.attendance.bean.AttendanceGroupListBean;
import com.hjhq.teamface.attendance.bean.AttendanceInfoBean;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;
import com.hjhq.teamface.attendance.bean.AttendanceRulesListBean;
import com.hjhq.teamface.attendance.bean.AttendanceScheduleDetailBean;
import com.hjhq.teamface.attendance.bean.AttendanceTypeBean;
import com.hjhq.teamface.attendance.bean.ChangeRankRulesBean;
import com.hjhq.teamface.attendance.bean.MonthlyDataBean;
import com.hjhq.teamface.attendance.bean.SaveAttendanceApprovalBean;
import com.hjhq.teamface.attendance.bean.WorkTimeDetailBean;
import com.hjhq.teamface.attendance.bean.WorkTimeListBean;
import com.hjhq.teamface.basis.bean.AppriveInfo;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.LocalModuleBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.bean.PlugListBean;
import com.hjhq.teamface.basis.network.ApiManager;
import com.hjhq.teamface.basis.network.exception.HttpResultFunc;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.IModel;
import com.hjhq.teamface.common.bean.AttendanceApproveListBean;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;


public class AttendanceModel implements IModel<AttendanceApiService> {
    @Override
    public AttendanceApiService getApi() {


        return new ApiManager<AttendanceApiService>().getAPI(AttendanceApiService.class);
    }


    public void addAttendanceType(ActivityPresenter mActivity, AddTypeBean bean, Subscriber<BaseBean> s) {
        getApi().addAttendanceType(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void appDaydataList(ActivityPresenter mActivity, Long attendanceDate, Subscriber<AttendanceDayDataBean> s) {
        getApi().appDaydataList(attendanceDate).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }
///////////////////////////////统计↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓


    public void getAppMonthDataByAuth(ActivityPresenter mActivity, String attendanceMonth, Subscriber<AttendanceMonthDataBean> s) {
        getApi().getAppMonthDataByAuth(attendanceMonth).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAppMonthDataBySelf(ActivityPresenter mActivity, String attendanceMonth, Subscriber<AttendanceMonthDataBean> s) {
        getApi().getAppMonthDataBySelf(attendanceMonth).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAppMonthDataBySelfForCalendar(ActivityPresenter mActivity, String attendanceMonth, Subscriber<MonthlyDataBean> s) {
        getApi().getAppMonthDataBySelfForCalendar(attendanceMonth).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryGroupList(ActivityPresenter mActivity, Subscriber<AttendanceGroupListBean> s) {
        getApi().queryGroupList().map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void earlyArrivalList(ActivityPresenter mActivity, String attendanceDate, Long groupId, Subscriber<RangeListBean> s) {
        getApi().earlyArrivalList(attendanceDate, groupId).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void diligentList(ActivityPresenter mActivity, String attendanceMonth, Long groupId, Subscriber<RangeListBean> s) {
        getApi().diligentList(attendanceMonth, groupId).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void lateList(ActivityPresenter mActivity, String attendanceMonth, Long groupId, Subscriber<RangeListBean> s) {
        getApi().lateList(attendanceMonth, groupId).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


///////////////////////////////统计↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


    public void updateAttendanceType(ActivityPresenter mActivity, AddTypeBean bean, Subscriber<BaseBean> s) {
        getApi().updateAttendanceType(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void delAttendanceType(ActivityPresenter mActivity, String id, Subscriber<BaseBean> s) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        getApi().delAttendanceType(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAttendanceTypeList(ActivityPresenter mActivity, int type, Subscriber<AttendanceTypeBean> s) {
        getApi().getAttendanceTypeList(type).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAttendanceInfo(ActivityPresenter mActivity, Long date, Subscriber<AttendanceInfoBean> s) {
        getApi().getAttendanceInfo(date).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryAttendanceRecord(ActivityPresenter mActivity, Long date, Subscriber<BaseBean> s) {
        getApi().queryAttendanceRecord(date).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void punchClock(ActivityPresenter mActivity, Map<String, Serializable> date, Subscriber<BaseBean> s) {
        getApi().punchClock(date).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryAttendanceRecord(ActivityPresenter mActivity, Subscriber<BaseBean> s) {
        getApi().queryAttendanceRecord().map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void saveAttendanceTime(ActivityPresenter mActivity, AddWorkTimeBean bean, Subscriber<BaseBean> s) {
        getApi().saveAttendanceTime(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateAttendanceTime(ActivityPresenter mActivity, AddWorkTimeBean bean, Subscriber<BaseBean> s) {
        getApi().updateAttendanceTime(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAttendanceTimeList(ActivityPresenter mActivity, Subscriber<WorkTimeListBean> s) {
        getApi().getAttendanceTimeList().map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void getAttendancePlugList(ActivityPresenter mActivity, Subscriber<PlugListBean> s) {
        getApi().getAttendancePlugList().map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void closeOrOpenPlug(ActivityPresenter mActivity, JSONObject obj, Subscriber<PlugListBean> s) {
        getApi().openOrCloseAttendanceRules(obj).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void deleteAttendanceTime(ActivityPresenter mActivity, String id, Subscriber<BaseBean> s) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        getApi().delAttendanceTime(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void deleteApprove(ActivityPresenter mActivity, String id, Subscriber<BaseBean> s) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        getApi().deleteApproveItem(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void findAttendanceTimeDetail(ActivityPresenter mActivity, String id, Subscriber<WorkTimeDetailBean> s) {

        getApi().findAttendanceTimeDetail(id).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAttendanceRulesDetail(ActivityPresenter mActivity, String id, Subscriber<AttendanceGroupDetailBean> s) {

        getApi().getAttendanceRulesDetail(id).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void delAttendanceRules(ActivityPresenter mActivity, String id, Subscriber<BaseBean> s) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        getApi().delAttendanceRules(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAttendanceRulesList(ActivityPresenter mActivity, int pageNum, int pageSize, Subscriber<AttendanceRulesListBean> s) {
        getApi().getAttendanceRulesList(pageNum, pageSize).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void addAttendanceRules(ActivityPresenter mActivity, AddRulesBean bean, Subscriber<BaseBean> s) {
        getApi().addAttendanceRules(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void updateAttendanceRules(ActivityPresenter mActivity, AddRulesBean bean, Subscriber<BaseBean> s) {
        getApi().updateAttendanceRules(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void addAdmin(ActivityPresenter mActivity, String adminArr, Subscriber<BaseBean> s) {
        Map<String, String> map = new HashMap<>();
        map.put("adminArr", adminArr);
        getApi().saveAdmin(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void rankRules(ActivityPresenter mActivity, ChangeRankRulesBean bean, Subscriber<BaseBean> s) {
        getApi().saveCount(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void saveHommization(ActivityPresenter mActivity, int times, int minutes, Subscriber<BaseBean> s) {
        Map<String, Object> map = new HashMap<>();
        //人性化允许每月迟到次数
        map.put("humanizationAllowLateTimes", times);
        //人性化允许每月迟到次数
        map.put("humanizationAllowLateMinutes", minutes);
        getApi().saveHommization(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void eidtAbsenteeism(ActivityPresenter mActivity, String id, int times, int minutes, Subscriber<BaseBean> s) {
        Map<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(id)) {
            map.put("id", id);
        }
        //人性化允许每月迟到次数
        map.put("absenteeismRuleBeLateMinutes", times);
        //人性化允许每月迟到次数
        map.put("absenteeismRuleLeaveEarlyMinutes", minutes);
        getApi().saveAbsenteeism(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAttendanceDetail(ActivityPresenter mActivity, Long dateStamp, Long id, Subscriber<AttendanceScheduleDetailBean> s) {

        getApi().getAttendanceDetail(dateStamp, id).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void leaveingLateRules(ActivityPresenter mActivity, AddLeaveingLateRulesListBean bean, Subscriber<BaseBean> s) {
        getApi().saveLate(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getSettingDetail(ActivityPresenter mActivity, Subscriber<AdditionalSettingDetailBean> s) {
        getApi().getSettingDetail().map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void saveRemind(ActivityPresenter mActivity, Long remindCockBeforeWork, Long remindClockAfterWork, Subscriber<BaseBean> s) {
        Map<String, Long> map = new HashMap<>();
        map.put("remindCockBeforeWork", remindCockBeforeWork);
        map.put("remindClockAfterWork", remindClockAfterWork);
        getApi().saveRemind(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void saveAttendanceApproval(ActivityPresenter mActivity, final SaveAttendanceApprovalBean bean, Subscriber<ModuleBean> s) {
        getApi().saveAttendanceApproval(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateAttendanceApproval(ActivityPresenter mActivity, final SaveAttendanceApprovalBean bean, Subscriber<ModuleBean> s) {
        getApi().updateAttendanceApproval(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAttendanceApprovalList(ActivityPresenter mActivity, Subscriber<AttendanceApproveListBean> s) {
        getApi().findApproveList().map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void getAttendanceApprovalDetail(ActivityPresenter mActivity, String id, Subscriber<AttendanceApproveDetailBean> s) {
        long l = TextUtil.parseLong(id, 0L);
        if (l == 0L) {
            return;
        }
        getApi().findApproveDetail(l).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getCustomLayout(ActivityPresenter mActivity, Map<String, Object> map, Subscriber<CustomLayoutResultBean> s) {
        getApi().getEnableFields(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void queryApprovalData(ActivityPresenter mActivity, Map<String, String> map, Subscriber<AppriveInfo> s) {
        getApi().queryApprovalData(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void findRoles(ActivityPresenter mActivity, Subscriber<LocalModuleBean> s) {
        getApi().findRoles(1).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


}

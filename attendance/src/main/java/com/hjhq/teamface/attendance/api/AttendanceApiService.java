package com.hjhq.teamface.attendance.api;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.attendance.adapter.RangeListBean;
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
import com.hjhq.teamface.common.bean.AttendanceApproveListBean;

import java.io.Serializable;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface AttendanceApiService {

    @Headers({"Content-Type: application/json"})
    @POST("attendanceWay/save")
    Observable<BaseBean> addAttendanceType(@Body AddTypeBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceWay/update")
    Observable<BaseBean> updateAttendanceType(@Body AddTypeBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceWay/del")
    Observable<BaseBean> delAttendanceType(@Body Map<String, String> bean);


    @GET("attendanceWay/findDetail")
    Observable<BaseBean> findDetail(@Query("id") String id);


    @GET("attendanceWay/findWebList")
    Observable<AttendanceTypeBean> getAttendanceTypeList(@Query("type") int type);


    @GET("attendanceClock/findUserAttendanceGroup")
    Observable<AttendanceInfoBean> getAttendanceInfo(@Query("attendanceDate") Long attendanceDate);


    @GET("attendanceClock/queryAttendanceRecord")
    Observable<BaseBean> queryAttendanceRecord(@Query("attendanceDate") Long attendanceDate);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceClock/punchClock")
    Observable<BaseBean> punchClock(@Body Map<String, Serializable> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceClass/save")
    Observable<BaseBean> saveAttendanceTime(@Body AddWorkTimeBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceClass/update")
    Observable<BaseBean> updateAttendanceTime(@Body AddWorkTimeBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceClass/del")
    Observable<BaseBean> delAttendanceTime(@Body Map<String, String> bean);


    @GET("attendanceClass/findDetail")
    Observable<WorkTimeDetailBean> findAttendanceTimeDetail(@Query("id") String id);


    @GET("attendanceClass/findWebList")
    Observable<WorkTimeListBean> getAttendanceTimeList();

    @GET("attendanceSetting/findPluginList")
    Observable<PlugListBean> getAttendancePlugList();

    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/openOrClose")
    Observable<PlugListBean> openOrCloseAttendanceRules(@Body JSONObject bean);

    @Headers({"Content-Type: application/json"})
    @POST("attendanceGroup/save")
    Observable<BaseBean> addAttendanceRules(@Body AddRulesBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceGroup/update")
    Observable<BaseBean> updateAttendanceRules(@Body AddRulesBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceGroup/del")
    Observable<BaseBean> delAttendanceRules(@Body Map<String, String> bean);


    @GET("attendanceGroup/findDetail")
    Observable<AttendanceGroupDetailBean> getAttendanceRulesDetail(@Query("id") String id);


    @GET("attendanceGroup/findWebList")
    Observable<AttendanceRulesListBean> getAttendanceRulesList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceGroup/synchronousUpdate")
    Observable<BaseBean> syncEmployeeInfo(@Body Map<String, String> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceCycle/save")
    Observable<BaseBean> addAttendanceCycle(@Body Map<String, String> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceCycle/update")
    Observable<BaseBean> updateAttendanceCycle(@Body Map<String, String> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceCycle/del")
    Observable<BaseBean> delAttendanceCycle(@Body Map<String, String> bean);


    @GET("attendanceCycle/findDetail")
    Observable<BaseBean> getAttendanceCycleDetail(@Query("id") String id);


    @GET("attendanceManagement/findAppDetail")
    Observable<AttendanceScheduleDetailBean> getAttendanceDetail(@Query("month") Long dateStamp, @Query("employeeId") Long employeeId);


    @GET("common/file/projectDownload")
    Observable<BaseBean> downloadProjectFile();


    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/saveAdmin")
    Observable<BaseBean> saveAdmin(@Body Map<String, String> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/update")
    Observable<BaseBean> updateAdmin(@Body Map<String, String> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/saveRemind")
    Observable<BaseBean> saveRemind(@Body Map<String, Long> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/saveCount")
    Observable<BaseBean> saveCount(@Body ChangeRankRulesBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/saveLate")
    Observable<BaseBean> saveLate(@Body AddLeaveingLateRulesListBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/saveHommization")
    Observable<BaseBean> saveHommization(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceSetting/saveAbsenteeism")
    Observable<BaseBean> saveAbsenteeism(@Body Map<String, Object> bean);


    @GET("attendanceSetting/findDetail")
    Observable<AdditionalSettingDetailBean> getSettingDetail();


    @GET("attendanceRelevanceApprove/findList")
    Observable<AttendanceApproveListBean> findApproveList();


    @GET("attendanceRelevanceApprove/findDetail")
    Observable<AttendanceApproveDetailBean> findApproveDetail(@Query("id") Long id);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceRelevanceApprove/del")
    Observable<BaseBean> deleteApproveItem(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceRelevanceApprove/save")
    Observable<ModuleBean> saveAttendanceApproval(@Body SaveAttendanceApprovalBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("attendanceRelevanceApprove/update")
    Observable<ModuleBean> updateAttendanceApproval(@Body SaveAttendanceApprovalBean bean);


    @GET("layout/getEnableLayout")
    Observable<CustomLayoutResultBean> getEnableFields(@QueryMap Map<String, Object> map);


///////////////////////////////////////>>>以下接口由徐兵开发<<<///////////////////////////////////////


    @GET("attendanceReport/queryAttendanceRecord")
    Observable<CustomLayoutResultBean> queryAttendanceRecord();


    @GET("attendanceClock/getUserAttendanceGroup")
    Observable<CustomLayoutResultBean> getUserAttendanceGroup();


    @Headers({"Content-Type: application/json"})
    @POST("attendanceClock/punchtheClock")
    Observable<ModuleBean> punchtheClock(@Body SaveAttendanceApprovalBean bean);


    @GET("attendanceReport/appDaydataList")
    Observable<AttendanceDayDataBean> appDaydataList(@Query("attendanceDate") Long attendanceDate);


    @GET("attendanceReport/getAppMonthDataByAuth")
    Observable<AttendanceMonthDataBean> getAppMonthDataByAuth(@Query("attendanceMonth") String attendanceMonth);


    @GET("attendanceReport/queryGroupList")
    Observable<AttendanceGroupListBean> queryGroupList();


    @GET("attendanceReport/earlyArrivalList")
    Observable<RangeListBean> earlyArrivalList(
            @Query("attendanceDate") String attendanceDate,
            @Query("groupId") Long groupId
    );


    @GET("attendanceReport/diligentList")
    Observable<RangeListBean> diligentList(
            @Query("attendanceMonth") String attendanceMonth,
            @Query("groupId") Long groupId);


    @GET("attendanceReport/lateList")
    Observable<RangeListBean> lateList(
            @Query("attendanceMonth") String attendanceMonth,
            @Query("groupId") Long groupId);


    @GET("attendanceReport/employeeMonthdataList")
    Observable<BaseBean> employeeMonthdataList(@Query("attendanceMonth") String attendanceMonth,
                                               @Query("employeeId") String employeeId);

    @GET("attendanceReport/getAppMonthDataBySelf")
    Observable<AttendanceMonthDataBean> getAppMonthDataBySelf(@Query("attendanceMonth") String attendanceMonth);


    @GET("attendanceReport/getAppMonthDataBySelfForCalendar")
    Observable<MonthlyDataBean> getAppMonthDataBySelfForCalendar(@Query("attendanceMonth") String attendanceMonth);

    @Headers({"Content-Type: application/json"})
    @POST("approval/queryApprovalData")
    Observable<AppriveInfo> queryApprovalData(@Body Map<String, String> map);

    @GET("moduleManagement/findModuleList")
    Observable<LocalModuleBean> findRoles(@Query("type") int type);
}

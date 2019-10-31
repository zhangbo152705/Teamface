package com.hjhq.teamface.basis.api;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.PlugListBean;
import com.hjhq.teamface.basis.bean.attendancebean.BaseAttendanceInfoBean;
import com.hjhq.teamface.basis.network.ApiManager;

import java.io.Serializable;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2019/4/22.
 */

public interface BaseService {




    @GET("attendanceClock/findUserAttendanceGroup")
    Observable<BaseAttendanceInfoBean> getBaseAttendanceInfo(@Query("attendanceDate") Long attendanceDate);

    @Headers({"Content-Type: application/json"})
    @POST("attendanceClock/punchClock")
    Observable<BaseBean> punchClock(@Body Map<String, Serializable> bean);

    @GET("attendanceSetting/findPluginList")
    Observable<PlugListBean> getAttendancePlugList();

}

package com.hjhq.teamface.custom;


import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.DataListRequestBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.DetailResultBean;
import com.hjhq.teamface.basis.bean.EmailListBean;
import com.hjhq.teamface.basis.bean.FilterFieldResultBean;
import com.hjhq.teamface.basis.bean.InsertSubformBean;
import com.hjhq.teamface.basis.bean.LinkDataBean;
import com.hjhq.teamface.basis.bean.MappingDataBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.bean.TempMenuResultBean;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.bean.WebLinkDataListBean;
import com.hjhq.teamface.common.bean.AddCommentRequestBean;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.bean.DynamicListResultBean;
import com.hjhq.teamface.custom.bean.AddOrEditShareRequestBean;
import com.hjhq.teamface.custom.bean.AutoModuleResultBean;
import com.hjhq.teamface.custom.bean.DataRelationResultBean;
import com.hjhq.teamface.custom.bean.FindAutoByBean;
import com.hjhq.teamface.custom.bean.ProcessFlowResponseBean;
import com.hjhq.teamface.custom.bean.RelationDataRequestBean;
import com.hjhq.teamface.custom.bean.RepeatCheckResponseBean;
import com.hjhq.teamface.custom.bean.SaveCustomDataRequestBean;
import com.hjhq.teamface.custom.bean.SeasPoolResponseBean;
import com.hjhq.teamface.custom.bean.ShareResultBean;
import com.hjhq.teamface.custom.bean.TabListBean;
import com.hjhq.teamface.custom.bean.TransferDataReqBean;
import com.hjhq.teamface.custom.bean.TransformationRequestBean;
import com.hjhq.teamface.custom.bean.TransformationResultBean;

import java.io.Serializable;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface CustomApiService {


    @GET("layout/getEnableLayout")
    Observable<CustomLayoutResultBean> getEnableFields(@QueryMap Map<String, Object> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/saveData")
    Observable<ModuleBean> saveCustomData(@Body SaveCustomDataRequestBean bean);

    @Headers({"Content-Type: application/json"})
    @POST("approval/checkAttendanceRelevanceTime")
    Observable<BaseBean> checkAttendanceRelevanceTime(@Body JSONObject bean);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/updateData")
    Observable<BaseBean> updateCustomData(@Body SaveCustomDataRequestBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/findDataList")
    Observable<DataTempResultBean> getDataTemp(@Body DataListRequestBean bean);


    @GET("moduleOperation/getRecheckingFields")
    Observable<RepeatCheckResponseBean> getRecheckingFields(
            @Query("bean") String bean,
            @Query("field") String field,
            @Query("label") String label,
            @Query("value") String value);


    @GET("submenu/getSubmenusForPC")
    Observable<TempMenuResultBean> getMenuList(@Query("moduleId") String moduleId);


    @GET("moduleEmail/getModuleSubmenus")
    Observable<TempMenuResultBean> getModuleSubmenus(@Query("moduleId") String moduleId);


    @GET("moduleOperation/findFilterFields")
    Observable<FilterFieldResultBean> getFilterFields(@Query("bean") String bean);


    @GET("moduleOperation/findDataDetail")
    Observable<DetailResultBean> getDataDetail(@QueryMap Map<String, Object> map);


    @GET("moduleOperation/findDataRelation")
    Observable<DataRelationResultBean> getDataRelation(@Query("id") String id, @Query("bean") String bean);


    @GET("tab/findTabList")
    Observable<TabListBean> getCustomDataTabList(@Query("dataId") String dataId, @Query("bean") String bean);


    @GET("tab/findListById")
    Observable<DataTempResultBean> getTabListData(
            @Query("dataAuth") int dataAuth,
            @Query("id") Long id,
            @Query("type") Integer type,
            @Query("ruleId") Long ruleId,
            @Query("moduleId") Long moduleId,
            @Query("tabId") Long tabId,
            @Query("pageSize") int pageSize,
            @Query("pageNum") int pageNum
    );


    @GET("automatch/findAllModule")
    Observable<AutoModuleResultBean> getAutoModule(@Query("bean") String bean);


    @GET("automatch/findAutoByBean")
    Observable<FindAutoByBean> findAutoByBean(@Query("sorceBean") String sorceBean, @Query("targetBean") String targetBean);


    @GET("automatch/findAutoList")
    Observable<DataTempResultBean> findAutoList(@Query("dataId") String dataId, @Query("ruleId") long ruleId, @Query("sorceBean") String sorceBean,
                                                @Query("targetBean") String targetBean,
                                                @Query("pageNum") int pageNum,
                                                @Query("pageSize") int pageSize);


    @Headers({"Content-Type: application/json"})
    @POST("common/savaCommonComment")
    Observable<BaseBean> addComment(@Body AddCommentRequestBean bean);


    @GET("common/queryCommentDetail")
    Observable<CommentDetailResultBean> getCommentDetail(@Query("id") String id, @Query("bean") String bean);


    @GET("common/queryAppDynamicList")
    Observable<DynamicListResultBean> getDynamicList(@Query("id") String id, @Query("bean") String bean);


    // TODO: 2018/1/12 processDefinitionId参数后面要改成 processInstanceId
    @GET("workflow/getProcessWholeFlow")
    Observable<ProcessFlowResponseBean> getProcessWholeFlow(@QueryMap Map<String, Object> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/findRelationDataList")
    Observable<ReferDataTempResultBean> findRelationDataList(@Body RelationDataRequestBean bean);


    @Multipart
    @POST("common/file/applyFileUpload")
    Observable<UpLoadFileResponseBean> uploadApplyFile(@PartMap Map<String, RequestBody> fileList);


    @Multipart
    @POST("common/file/upload")
    Observable<UpLoadFileResponseBean> uploadFile(@PartMap Map<String, RequestBody> fileList);


    @GET("moduleDataAuth/getFuncAuthByModule")
    Observable<ModuleFunctionBean> getModuleFunctionAuth(@Query("bean") String moduleBean);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/transfor")
    Observable<BaseBean> transfor(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/transfor")
    Observable<BaseBean> transforV2(@Body TransferDataReqBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/copy")
    Observable<DetailResultBean> copy(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/del")
    Observable<BaseBean> del(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/deleteData")
    Observable<BaseBean> deleteData(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleSingleShare/del")
    Observable<BaseBean> delShare(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleSingleShare/save")
    Observable<BaseBean> saveShare(@Body AddOrEditShareRequestBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("moduleSingleShare/update")
    Observable<BaseBean> editShare(@Body AddOrEditShareRequestBean bean);


    @GET("moduleSingleShare/getSingles")
    Observable<ShareResultBean> getSingleShare(@Query("dataId") String dataId, @Query("bean") String bean);


    @GET("fieldTransform/getFieldTransformationsForMobile")
    Observable<TransformationResultBean> getFieldTransformations(@Query("bean") String bean);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/transformation")
    Observable<BaseBean> transformations(@Body TransformationRequestBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/take")
    Observable<BaseBean> take(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/returnBack")
    Observable<BaseBean> returnBack(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/allocate")
    Observable<BaseBean> allocate(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/moveData2OtherSeapool")
    Observable<BaseBean> moveSeapool(@Body Map<String, String> map);


    @GET("seapool/getSeapools")
    Observable<SeasPoolResponseBean> getSeapools(@Query("bean") String bean);


    @GET("mailOperation/queryMailListByAccount")
    Observable<EmailListBean> getEmailListyAccount(
            @Query("dataAuth") int dataAuth,
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountName") String accountName
    );


    @Headers({"Content-Type: application/json"})
    @POST("barcode/createBarcode")
    Observable<BaseBean> createBarcode(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("barcode/findDetailByBarcode")
    Observable<BaseBean> findDetailByBarcode(@Body Map<String, String> map);


    @Headers({"Content-Type: application/json"})
    @POST("barcode/getBarcodeMsg")
    Observable<BaseBean> getBarcodeMsg(@Body Map<String, String> map);


    @GET("moduleDataAuth/getFuncAuthWithCommunal")
    Observable<ViewDataAuthResBean> queryAuth(@Query("bean") String bean,
                                              @Query("dataId") String dataId,
                                              @Query(value = "reqmap", encoded = true) String reqmap
    );


    @Headers({"Content-Type: application/json"})
    @POST("moduleDataAuth/getFuncAuthWithCommunal")
    Observable<ViewDataAuthResBean> queryAuth2(@QueryMap Map<String, String> Map
    );


    @GET("webform/getWebformList")
    Observable<LinkDataBean> getWebformList(@Query("moduleBean") String moduleBean);


    @GET("webform/getWebformListForAdd")
    Observable<WebLinkDataListBean> getWebformListForAdd(
            @Query("moduleBean") String moduleBean,
            @Query("source") int source,
            @Query("seasPoolId") String seasPoolId,
            @Query("relevanceModule") String relevanceModule,
            @Query("relevanceField") String relevanceField,
            @Query("relevanceValue") String relevanceValue);


    @Headers({"Content-Type: application/json"})
    @POST("tab/findReferenceMapping")
    Observable<MappingDataBean> findReferenceMapping(@Body Map<String, String> map);

    @Headers({"Content-Type: application/json"})
    @POST("fieldRelyon/findSubRelationDataList")
    Observable<InsertSubformBean> findSubRelationDataList(@Body Map<String, Serializable> map);


    @GET("mailOperation/queryList")
    Observable<EmailListBean> getEmailList(
            @Query("pageNum") int pageNum,
            @Query("pageSize") int pageSize,
            @Query("accountId") String accountId,
            @Query("boxId") String boxId
    );


    @GET("mailOperation/receive")
    Observable<EmailListBean> receive();

}

package com.hjhq.teamface.memo;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CommonNewResultBean;
import com.hjhq.teamface.basis.bean.DataListRequestBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.ModuleResultBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.bean.ProjectListBean;
import com.hjhq.teamface.common.bean.TaskListBean;
import com.hjhq.teamface.memo.bean.AddRelevantBean;
import com.hjhq.teamface.memo.bean.AnswerListBean;
import com.hjhq.teamface.common.bean.KnowledgeClassListBean;
import com.hjhq.teamface.memo.bean.KnowledgeDetailBean;
import com.hjhq.teamface.memo.bean.KnowledgeListData;
import com.hjhq.teamface.memo.bean.MemberListBean;
import com.hjhq.teamface.memo.bean.MemberReadListBean;
import com.hjhq.teamface.memo.bean.MemoDetailBean;
import com.hjhq.teamface.memo.bean.MemoListBean;
import com.hjhq.teamface.memo.bean.NewMemoBean;
import com.hjhq.teamface.memo.bean.RelevantDataBean;
import com.hjhq.teamface.memo.bean.RevelantDataListBean;
import com.hjhq.teamface.memo.bean.SearchModuleDataBean;
import com.hjhq.teamface.memo.bean.VersionListBean;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


public interface MemoApi {

    @Headers({"Content-Type: application/json"})
    @POST("memo/save")
    Observable<CommonNewResultBean> saveMemo(@Body NewMemoBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("memo/updateRelationById")
    Observable<BaseBean> updateRelationById(@Body AddRelevantBean bean);


    @GET("memo/findRelationList")
    Observable<RelevantDataBean> findRelationList(@Query("id") String id);


    @Headers({"Content-Type: application/json"})
    @POST("memo/update")
    Observable<BaseBean> updateMemo(@Body NewMemoBean bean);


    @Headers({"Content-Type: application/json"})
    @POST("memo/del")
    Observable<BaseBean> memoOperation(@Query("type") String type, @Query("ids") String ids);


    @GET("memo/findMemoList")
    Observable<MemoListBean> findMemoList(@Query("pageNum") String pageNum,
                                          @Query("pageSize") String pageSize,
                                          @Query("type") String type,
                                          @Query("keyword") String keyword);

    @GET("memo/findMemoWebList")
    Observable<MemoListBean> findMemoWebList(@Query("type") String type,
                                             @Query("keyword") String keyword);


    @GET("memo/findMemoDetail")
    Observable<MemoDetailBean> findMemoDetail(@Query("id") String id);


    @GET("module/findAllModuleList")
    Observable<ModuleResultBean> getAllModule(@Query("approvalFlag") String approvalFlag);


    @GET("moduleOperation/getFirstFieldFromModule")
    Observable<SearchModuleDataBean> getFirstFieldFromModule(@Query("bean") String bean, @Query("fieldValue") String fieldValue);


    @Headers({"Content-Type: application/json"})
    @POST("moduleOperation/findDataList")
    Observable<DataTempResultBean> getDataTemp(@Body DataListRequestBean bean);


    @GET("moduleDataAuth/getFuncAuthWithCommunal")
    Observable<ViewDataAuthResBean> queryAuth(@Query("bean") String bean,
                                              @Query("style") String style,
                                              @Query("dataId") String dataId);

    /**
     * 查看模块权限
     */
    @GET("moduleDataAuth/getFuncAuthWithCommunal")
    Observable<ViewDataAuthResBean> queryAuth(@Query("bean") String bean,@Query("style") String style
            ,@Query("dataId") String dataId,@Query("reqmap") String reqmap);


    @GET("projectController/queryAllList")
    Observable<ProjectListBean> queryAllList(@Query("type") int type, @Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Query("keyword") String keyword);


    @GET("projectTaskController/queryAllTaskList")
    Observable<TaskListBean> queryAllTaskList(@Query("projectId") Long projectId);
//////////////////////////////////////知识库接口>>START//////////////////////////////////////////////


    @GET("repositoryClassification/getRepositoryClassificationList")
    Observable<KnowledgeClassListBean> getRepositoryClassificationList();


    @GET("repositoryLibraries/getDataDetail")
    Observable<KnowledgeDetailBean> getDataDetail(@Query("repositoryId") Long repositoryId);


    @GET("repositoryAnswer/getDataDetail")
    Observable<KnowledgeDetailBean> getAnswerDetail(@Query("answerId") Long answerId);


    @GET("repositoryLibraries/getPraisePersons")
    Observable<MemberListBean> getPraisePersons(@Query("repositoryId") Long repositoryId);


    @GET("repositoryLibraries/getReadLearningPersons")
    Observable<MemberReadListBean> getViewPersons(@Query("repositoryId") Long repositoryId);


    @GET("repositoryLibraries/getReadLearningPersons")
    Observable<MemberListBean> getReadLearningPersons(@Query("repositoryId") Long repositoryId);


    @GET("repositoryLibraries/getCollectionPersons")
    Observable<MemberListBean> getCollectionPersons(@Query("repositoryId") Long repositoryId);


    @GET("repositoryLibraries/getRepositoryVersions")
    Observable<VersionListBean> getRepositoryVersions(@Query("repositoryId") Long repositoryId);


    @GET("repositoryAnswer/getRepositoryAnswerList")
    Observable<AnswerListBean> getRepositoryAnswerList(
            @Query("repositoryId") Long repositoryId,
            @Query("orderBy") String orderBy);


    @GET("repositoryLibrariesAssociates/queryAssociatesByRepositoryId")
    Observable<RevelantDataListBean> queryAssociatesByRepositoryId(
            @Query("repositoryId") Long repositoryId,
            @Query("fromStatus") Integer fromStatus);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/queryRepositoryLibrariesList")
    Observable<KnowledgeListData> getKnowledgeLsit(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/save")
    Observable<BaseBean> saveKnowledge(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/update")
    Observable<BaseBean> editKnowledge(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/updateLearning")
    Observable<BaseBean> updateLearning(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryAnswer/updatePlacedAtTheTop")
    Observable<BaseBean> updatePlacedAtTheTop(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/updateMove")
    Observable<BaseBean> updateMove(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/updateCollection")
    Observable<BaseBean> updateCollection(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/updatePraise")
    Observable<BaseBean> updatePraise(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryAnswer/save")
    Observable<BaseBean> saveAnswer(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryAnswer/update")
    Observable<BaseBean> updateAnswer(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryAnswer/del")
    Observable<BaseBean> deleteAnswer(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/del")
    Observable<BaseBean> deleteKnowledge(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryAnswer/updatePlacedAtTheTop")
    Observable<BaseBean> putAnswerTop(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/topSetting")
    Observable<BaseBean> putKnowledgeTop(@Body Map<String, Object> bean);


    @Headers({"Content-Type: application/json"})
    @POST("repositoryLibraries/updateInvitePersonsToAnswer")
    Observable<BaseBean> updateInvitePersonsToAnswer(@Body Map<String, Object> bean);

    //////////////////////////////////////知识库接口>>END////////////////////////////////////////////////


    @GET("common/queryCommentDetail")
    Observable<CommentDetailResultBean> getCommentDetail(@Query("id") String id, @Query("bean") String bean);
}

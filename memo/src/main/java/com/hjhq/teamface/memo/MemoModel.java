package com.hjhq.teamface.memo;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CommonNewResultBean;
import com.hjhq.teamface.basis.bean.DataListRequestBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.ModuleResultBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.network.ApiManager;
import com.hjhq.teamface.basis.network.exception.HttpResultFunc;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.IModel;
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
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;


public class MemoModel implements IModel<MemoApi> {

    @Override
    public MemoApi getApi() {
        //   SPHelper.setToken("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxOSIsInN1YiI6IjE5IiwiYXVkIjoiMSIsImlzcyI6IjEwMDE5IiwiaWF0IjoxNTM0OTA1NjczfQ.tM0056Mz5Y4gtHqwN7gqpXYnmlLadWRCSFuniT7OrXE");
        return new ApiManager<MemoApi>().getAPI(MemoApi.class);
    }


    public void saveMemo(ActivityPresenter mActivity, NewMemoBean bean, Subscriber<CommonNewResultBean> s) {
        getApi().saveMemo(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateRelationById(ActivityPresenter mActivity, AddRelevantBean bean, Subscriber<BaseBean> s) {
        getApi().updateRelationById(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateMemo(ActivityPresenter mActivity, NewMemoBean bean, Subscriber<BaseBean> s) {
        getApi().updateMemo(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void memoOperation(ActivityPresenter mActivity, String type, String ids, Subscriber<BaseBean> s) {
        getApi().memoOperation(type, ids).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void findMemoList(ActivityPresenter mActivity, String pageNum, String pageSize, String type,
                             String keyword, Subscriber<MemoListBean> s) {
        getApi().findMemoList(pageNum, pageSize, type, keyword).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void findMemoWebList(ActivityPresenter mActivity, String type,
                                String keyword, Subscriber<MemoListBean> s) {
        getApi().findMemoWebList(type, keyword).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void findMemoDetail(ActivityPresenter mActivity, String id, Subscriber<MemoDetailBean> s) {
        getApi().findMemoDetail(id).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void findRelationList(ActivityPresenter mActivity, String id, Subscriber<RelevantDataBean> s) {
        getApi().findRelationList(id).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryMemoDetail(ActivityPresenter mActivity, String id,
                                Func2<MemoDetailBean, RelevantDataBean, MemoDetailBean> func2,
                                Subscriber<MemoDetailBean> s) {
        Observable.zip(getApi().findMemoDetail(id), getApi().findRelationList(id), func2)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAllModule(ActivityPresenter mActivity, Subscriber<ModuleResultBean> s) {
        getApi().getAllModule(null).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getFirstFieldFromModule(ActivityPresenter mActivity, String beanName,
                                        String keyword, Subscriber<SearchModuleDataBean> s) {
        getApi().getFirstFieldFromModule(beanName, keyword).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getFirstFieldFromModule2(ActivityPresenter mActivity, String beanName,
                                         String keyword, Subscriber<DataTempResultBean> s) {
        DataListRequestBean bean = new DataListRequestBean();

        getApi().getDataTemp(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryAuth(ActivityPresenter mActivity, String bean, String style,
                          String dataId, Subscriber<ViewDataAuthResBean> s) {
        getApi().queryAuth(bean, style, dataId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void queryAuth(ActivityPresenter mActivity, String bean, String style,
                          String dataId,String reqmap, Subscriber<ViewDataAuthResBean> s) {
        getApi().queryAuth(bean, style, dataId,reqmap).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryAllList(RxAppCompatActivity mActivity, int type, int pageNum, int pageSize, String keyword, Subscriber<ProjectListBean> s) {
        getApi().queryAllList(type, pageNum, pageSize, keyword).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryAllTaskList(RxAppCompatActivity mActivity, Long projectId, Subscriber<TaskListBean> s) {
        getApi().queryAllTaskList(projectId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }
//////////////////////////////////////知识库接口>>START//////////////////////////////////////////////


    public void getKnowledgeLsit(RxAppCompatActivity mActivity, Map<String, Object> map, Subscriber<KnowledgeListData> s) {
        getApi().getKnowledgeLsit(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getRepositoryClassificationList(RxAppCompatActivity mActivity, Subscriber<KnowledgeClassListBean> s) {
        getApi().getRepositoryClassificationList().map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getDataDetail(RxAppCompatActivity mActivity, Long repositoryId, Subscriber<KnowledgeDetailBean> s) {
        getApi().getDataDetail(repositoryId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getAnswerDetail(RxAppCompatActivity mActivity, Long answerId, Subscriber<KnowledgeDetailBean> s) {
        getApi().getAnswerDetail(answerId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getPraisePersons(RxAppCompatActivity mActivity, Long repositoryId, Subscriber<MemberListBean> s) {
        getApi().getPraisePersons(repositoryId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getViewPersons(RxAppCompatActivity mActivity, Long repositoryId, Subscriber<MemberReadListBean> s) {
        getApi().getViewPersons(repositoryId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getReadLearningPersons(RxAppCompatActivity mActivity, Long repositoryId, Subscriber<MemberListBean> s) {
        getApi().getReadLearningPersons(repositoryId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getCollectionPersons(RxAppCompatActivity mActivity, Long repositoryId, Subscriber<MemberListBean> s) {
        getApi().getCollectionPersons(repositoryId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getRepositoryVersions(RxAppCompatActivity mActivity, Long repositoryId, Subscriber<VersionListBean> s) {
        getApi().getRepositoryVersions(repositoryId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void getRepositoryAnswerList(RxAppCompatActivity mActivity, Long repositoryId, String orderBy, Subscriber<AnswerListBean> s) {
        getApi().getRepositoryAnswerList(repositoryId, orderBy).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void queryAssociatesByRepositoryId(RxAppCompatActivity mActivity, Long repositoryId, int type, Subscriber<RevelantDataListBean> s) {
        getApi().queryAssociatesByRepositoryId(repositoryId, type).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void saveAnswer(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().saveAnswer(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateAnswer(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().updateAnswer(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void deleteAnswer(RxAppCompatActivity mActivity, String id, Subscriber<BaseBean> s) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        getApi().deleteAnswer(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void deleteKnowledge(RxAppCompatActivity mActivity, String id, Subscriber<BaseBean> s) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        getApi().deleteKnowledge(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void putAnswerTop(RxAppCompatActivity mActivity, String id, int status, Subscriber<BaseBean> s) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("status", status);
        getApi().putAnswerTop(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void putKnowledgeTop(RxAppCompatActivity mActivity, String id, int top, Subscriber<BaseBean> s) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("top", top);
        getApi().putKnowledgeTop(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateInvitePersonsToAnswer(RxAppCompatActivity mActivity, String id, String invite_employees, Subscriber<BaseBean> s) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("invite_employees", invite_employees);
        getApi().updateInvitePersonsToAnswer(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void addKnowledge(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().saveKnowledge(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void editKnowledge(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().editKnowledge(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateLearning(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().updateLearning(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updatePlacedAtTheTop(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().updatePlacedAtTheTop(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateMove(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().updateMove(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updateCollection(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().updateCollection(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void updatePraise(RxAppCompatActivity mActivity, Map<String, Object> data, Subscriber<BaseBean> s) {
        getApi().updatePraise(data).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }
//////////////////////////////////////知识库接口>>END////////////////////////////////////////////////


    public void getCommentDetail(RxAppCompatActivity mActivity, String id, String bean, Subscriber<CommentDetailResultBean> s) {
        getApi().getCommentDetail(id, bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }
}

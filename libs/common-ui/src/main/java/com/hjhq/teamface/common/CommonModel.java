package com.hjhq.teamface.common;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.bean.AddGroupChatReqBean;
import com.hjhq.teamface.basis.bean.AddGroupChatResponseBean;
import com.hjhq.teamface.basis.bean.AddSingleChatResponseBean;
import com.hjhq.teamface.basis.bean.ApproveUnReadCountResponseBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.DetailResultBean;
import com.hjhq.teamface.basis.bean.EmailUnreadNumBean;
import com.hjhq.teamface.basis.bean.EmployeeResultBean;
import com.hjhq.teamface.basis.bean.GetDepartmentStructureBean;
import com.hjhq.teamface.basis.bean.ModuleAuthBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.QueryBarcodeDataBean;
import com.hjhq.teamface.basis.bean.RoleGroupResponseBean;
import com.hjhq.teamface.basis.bean.TempMenuResultBean;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.bean.WidgetListBean;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.ApiManager;
import com.hjhq.teamface.basis.network.MainRetrofit;
import com.hjhq.teamface.basis.network.body.FileRequestBody;
import com.hjhq.teamface.basis.network.callback.RetrofitCallback;
import com.hjhq.teamface.basis.network.exception.HttpResultFunc;
import com.hjhq.teamface.basis.network.factory.FastJsonConverterFactory;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.IModel;
import com.hjhq.teamface.common.bean.AddCommentRequestBean;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.bean.DynamicListResultBean;
import com.hjhq.teamface.common.bean.DynamicParamsBean;
import com.hjhq.teamface.common.bean.KnowledgeClassListBean;
import com.hjhq.teamface.common.bean.LinkageFieldsResultBean;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;

/**
 * 通用model
 * Created by Administrator on 2018/3/23.
 */

public class CommonModel implements IModel<CommonApiService> {
    @Override
    public CommonApiService getApi() {
        return new ApiManager<CommonApiService>().getAPI(CommonApiService.class);
    }

    /**
     * 添加评论
     *
     * @param s
     */
    public void addComment(RxAppCompatActivity mActivity, AddCommentRequestBean bean, Subscriber<BaseBean> s) {
        getApi().addComment(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 评论详情
     *
     * @param s
     */
    public void getCommentDetail(RxAppCompatActivity mActivity, String id, String bean, Subscriber<CommentDetailResultBean> s) {
        getApi().getCommentDetail(id, bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 动态列表
     *
     * @param s
     */
    public void getDynamicList(RxAppCompatActivity mActivity, String id, String bean, Subscriber<DynamicListResultBean> s) {
        getApi().getDynamicList(id, bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    /**
     * 评论上传附件
     *
     * @param mActivity
     * @param url
     * @param s
     */
    public void uploadFile(RxAppCompatActivity mActivity,
                           String url, String bean, Subscriber<UpLoadFileResponseBean> s) {
        File file = new File(url);
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        Map<String, RequestBody> fileList = new HashMap<>();
        fileList.put("file\"; filename=\"" + file.getAbsolutePath(), requestBody1);

        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), bean);
        fileList.put("bean", requestBody2);
        getApi().uploadFile(fileList)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 上传文件 回调
     *
     * @param url
     * @param callback
     */
    public void uploadFile(RxAppCompatActivity mActivity, String url, String moduleBean, RetrofitCallback callback, Subscriber<UpLoadFileResponseBean> s) {
        Map<String, RequestBody> fileList = new HashMap<>(2);
        File file = new File(url);

        RequestBody requestBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody requestBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), moduleBean);
        FileRequestBody fileRequestBody = new FileRequestBody(requestBody1, callback);

        fileList.put("file\"; filename=\"" + file.getName(), fileRequestBody);
        fileList.put("bean", requestBody2);

        getApi().uploadFile(fileList)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);

    }

    /**
     * 获取人员
     *
     * @param mActivity
     * @param userName
     * @param s
     */
    public void getEmployee(RxAppCompatActivity mActivity, String userName, int dismiss, Subscriber<EmployeeResultBean> s) {
        getApi().getEmployee(userName, dismiss).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取选择范围内的员工
     *
     * @param mActivity
     * @param map
     * @param s
     */
    public void queryRangeEmployeeList(RxAppCompatActivity mActivity, Map<String, List<Member>> map, Subscriber<EmployeeResultBean> s) {
        getApi().queryRangeEmployeeList(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取选择范围内的部门
     *
     * @param mActivity
     * @param map
     * @param s
     */
    public void queryRangeDepartmentList(RxAppCompatActivity mActivity, Map<String, List<Member>> map, Subscriber<EmployeeResultBean> s) {
        getApi().queryRangeDepartmentList(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取人员
     *
     * @param mActivity
     * @param departmentId
     * @param userName
     * @param s
     */
    public void findByUserName(RxAppCompatActivity mActivity,
                               String departmentId,
                               String userName,
                               int dismiss,
                               Subscriber<EmployeeResultBean> s) {
        getApi().findByUserName(departmentId, userName, dismiss).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取公司人员架构
     *
     * @param mActivity
     * @param companyId
     * @param s
     */
    public void findUsersByCompany(RxAppCompatActivity mActivity, String companyId, int dismiss,
                                   Subscriber<GetDepartmentStructureBean> s) {
        getApi().findUsersByCompany(companyId, dismiss)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取公司人员架构
     *
     * @param mActivity
     * @param s
     */
    public void getRoleGroupList(RxAppCompatActivity mActivity,
                                 Subscriber<RoleGroupResponseBean> s) {
        getApi().getRoleGroupList()
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);

    }

    /**
     * 获取模块的功能权限(ActivityPresenter)
     *
     * @param mActivity
     * @param moduleBean
     * @param s
     */
    public void getMoudleFunctionAuth(ActivityPresenter mActivity, String moduleBean,
                                      Subscriber<ModuleFunctionBean> s) {
        getApi().getModuleFunctionAuth(moduleBean)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void getMoudleFunctionAuth(BaseActivity mActivity, String moduleBean,
                                      Subscriber<ModuleFunctionBean> s) {
        getApi().getModuleFunctionAuth(moduleBean)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 验证功能权限
     *
     * @param mActivity
     * @param bean
     * @param s
     */
    public void getAuthByBean(RxAppCompatActivity mActivity, String bean, String dataId, Subscriber<ViewDataAuthBean> s) {
        getApi().getAuthByBean(bean, dataId + "")
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 验证功能权限
     *
     * @param mActivity
     * @param bean
     * @param s
     */
    public void getAuth(RxAppCompatActivity mActivity, String bean, String dataId,
                        String style, String params, Subscriber<ViewDataAuthResBean> s) {
        getApi().queryAuth(bean, style, dataId + "", params)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 验证模块权限
     *
     * @param mActivity
     * @param bean
     * @param s
     */
    public void getAuthByModule(RxAppCompatActivity mActivity, String bean, Subscriber<ModuleAuthBean> s) {
        getApi().getAuthByModule(bean)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 添加个人聊天
     *
     * @param mActivity
     * @param receiverId
     * @param s
     */
    public void addSingleChat(RxAppCompatActivity mActivity, String receiverId, Subscriber<AddSingleChatResponseBean> s) {


        getApi().addSingleChat(receiverId)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 根据条形码获取详情
     *
     * @param mActivity
     * @param barcodeValue
     * @param s
     */
    public void findDetailByBarcode(RxAppCompatActivity mActivity, String barcodeValue,
                                    Subscriber<QueryBarcodeDataBean> s) {
        getApi().findDetailByBarcode(barcodeValue)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    /**
     * 创建群聊
     *
     * @param mActivity
     * @param bean
     * @param s
     */
    public void addGroupChat(RxAppCompatActivity mActivity, AddGroupChatReqBean bean, Subscriber<AddGroupChatResponseBean> s) {
        getApi().addGroupChat(bean)
                .map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取最近联系人
     *
     * @return
     */
    public List<Member> getRecentContacts() {
        List<Member> list = new ArrayList<>();
        List<Member> recentContacts = SPHelper.getRecentContacts();
        if (recentContacts != null && recentContacts.size() > 0) {
            list.addAll(recentContacts);
        }
        return list;

    }

    /**
     * 保存最近联系人
     *
     * @param memberList 多人
     */
    public void saveRecentContacts(List<Member> memberList) {
        if (memberList != null) {
            List<Member> recentContacts = getRecentContacts();
            List<Member> allContacts = new ArrayList<>();
            allContacts.addAll(recentContacts);
            allContacts.addAll(memberList);
            recentContacts.addAll(memberList);

            Comparator<Member> comparator = new Comparator<Member>() {
                @Override
                public int compare(Member o1, Member o2) {
                    return (int) (o2.getUpdateTime() - o1.getUpdateTime());
                }
            };
            Collections.sort(recentContacts, comparator);

            Iterator<Member> iterator = allContacts.iterator();
            while (iterator.hasNext()) {
                Member m = iterator.next();
                boolean eq = false;
                for (int i = 0; i < memberList.size(); i++) {
                    if (memberList.get(i).getId() == m.getId() && memberList.get(i).getUpdateTime() > m.getUpdateTime()) {
                        iterator.remove();
                    } else if (memberList.get(i).getId() == m.getId() && memberList.get(i).getUpdateTime() == m.getUpdateTime()) {
                        eq = true;
                    }
                }
                for (int i = 0; i < recentContacts.size(); i++) {
                    if (recentContacts.get(i).getId() == m.getId() && recentContacts.get(i).getUpdateTime() > m.getUpdateTime()) {
                        iterator.remove();
                    } else if (recentContacts.get(i).getId() == m.getId() && recentContacts.get(i).getUpdateTime() > m.getUpdateTime()) {
                        if (eq) {
                            iterator.remove();
                        }
                    }
                }

            }

            List<Member> list = new ArrayList<>();
            if (allContacts.size() > 10) {
                list.addAll(recentContacts.subList(0, 10));
                SPHelper.saveRecentContacts(list, true);
            } else {
                SPHelper.saveRecentContacts(allContacts, true);
            }
            SPHelper.saveRecentContacts(recentContacts, true);
        }
    }


    /**
     * 获取联动字段
     *
     * @param mActivity
     * @param bean
     * @param s
     */
    public void getLinkageFields(ActivityPresenter mActivity, String bean, Subscriber<LinkageFieldsResultBean> s) {
        getApi().getLinkageFields(bean).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 任务勾选完成状态复选框
     *
     * @param mActivity
     * @param jsonObject
     * @param s
     */
    public void updateStatus(RxAppCompatActivity mActivity, JSONObject jsonObject, Subscriber<BaseBean> s) {
        getApi().updateStatus(jsonObject).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 子任务勾选完成状态复选框
     *
     * @param mActivity
     * @param jsonObject
     * @param s
     */
    public void updateSubStatus(RxAppCompatActivity mActivity, JSONObject jsonObject, Subscriber<BaseBean> s) {
        getApi().updateSubStatus(jsonObject).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 个人任务勾选完成状态复选框
     *
     * @param mActivity
     * @param s
     */
    public void updatePersonelTaskStatus(RxAppCompatActivity mActivity, long taskId, Subscriber<BaseBean> s) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskId", taskId);
        getApi().updatePersonelTaskStatus(jsonObject).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 个人子任务勾选完成状态复选框
     *
     * @param mActivity
     * @param s
     */
    public void updatePersonelSubTaskStatus(RxAppCompatActivity mActivity, long taskId, Subscriber<BaseBean> s) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskId", taskId);
        getApi().updatePersonelSubTaskStatus(jsonObject).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取联动字段对应的关联数据
     *
     * @param mActivity
     * @param jsonObject
     * @param s
     */
    public void getLinkageData(RxAppCompatActivity mActivity, JSONObject jsonObject, Subscriber<DetailResultBean> s) {
        CommonApiService commonApiService = new MainRetrofit.Builder<CommonApiService>()
                .addConverterFactory(new FastJsonConverterFactory())
                .build(CommonApiService.class);
        commonApiService.getLinkageData(jsonObject).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取审批未读数
     *
     * @param mActivity
     * @param s
     */
    public void getApproveData(BaseActivity mActivity, Subscriber<ApproveUnReadCountResponseBean> s) {
        getApi().queryApprovalCount().map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取邮件未读数
     *
     * @param mActivity
     * @param s
     */
    public void getEmailData(BaseActivity mActivity, Subscriber<EmailUnreadNumBean> s) {
        getApi().queryUnreadNumsByBox().map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取子菜单
     *
     * @param mActivity
     * @param moduleId
     * @param s
     */
    public void getMenuList(BaseActivity mActivity, String moduleId, Subscriber<TempMenuResultBean> s) {
        getApi().getMenuList(moduleId).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取已保存工作台组件列表
     *
     * @param mActivity
     * @param s
     */
    public void getCommonModules(RxAppCompatActivity mActivity, Subscriber<WidgetListBean> s) {
        getApi().getCommonModules(1).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 获取已保存工作台组件列表
     *
     * @param mActivity
     * @param s
     */
    public void findAllModule(RxAppCompatActivity mActivity, Subscriber<WidgetListBean> s) {
        getApi().findAllModule().map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    /**
     * 保存组件列表
     *
     * @param mActivity
     * @param map
     * @param s
     */
    public void saveWidgetList(RxAppCompatActivity mActivity, Map<String, Object> map, Subscriber<WidgetListBean> s) {
        getApi().saveWidgetList(map).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    /**
     * 获取知识库分类列表
     *
     * @param mActivity
     * @param s
     */
    public void getRepositoryClassificationList(RxAppCompatActivity mActivity, Subscriber<KnowledgeClassListBean> s) {
        getApi().getRepositoryClassificationList().map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }

    public void getSharePersonalFields(RxAppCompatActivity mActivity, String bean, int type, Subscriber<DynamicParamsBean> s) {
        getApi().getSharePersonalFields(bean, type).map(new HttpResultFunc<>())
                .compose(mActivity.bindToLifecycle())
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }


    public void punchClock(ActivityPresenter mActivity, Map<String, Serializable> date, Subscriber<BaseBean> s) {
        getApi().punchClock(date).map(new HttpResultFunc<>())
                .compose(mActivity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(TransformerHelper.applySchedulers())
                .subscribe(s);
    }
}

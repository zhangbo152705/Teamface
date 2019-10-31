package com.hjhq.teamface.oa.approve.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.DetailResultBean;
import com.hjhq.teamface.basis.bean.LayoutData;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.Photo;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.database.SocketMessage;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.adapter.CommentAdapter;
import com.hjhq.teamface.common.bean.ApproveAuthBean;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.bean.LinkageFieldsResultBean;
import com.hjhq.teamface.common.ui.ImagePagerActivity;
import com.hjhq.teamface.common.ui.comment.CommentActivity;
import com.hjhq.teamface.common.ui.email.EmailDetailFragment;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.voice.VoicePlayActivity;
import com.hjhq.teamface.common.utils.ApproveUtils;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.componentservice.custom.CustomService;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.ReferenceViewInterface;
import com.hjhq.teamface.customcomponent.widget2.subfield.SubfieldView;
import com.hjhq.teamface.customcomponent.widget2.subforms.CommonSubFormsView;
import com.hjhq.teamface.oa.approve.bean.ApproveCopyRequestBean;
import com.hjhq.teamface.oa.approve.bean.ApproveRevokeRequestBean;
import com.hjhq.teamface.oa.approve.bean.ApproverBean;
import com.hjhq.teamface.oa.approve.bean.ProcessFlowResponseBean;
import com.hjhq.teamface.util.CommonUtil;
import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * 审批数据详情
 *
 * @author Administrator
 */
@RouteNode(path = "/approve/detail", desc = "审批数据详情")
public class ApproveDetailActivity extends ActivityPresenter<ApproveDetailDelegate, ApproveModel>
        implements View.OnClickListener, ReferenceViewInterface {
    /**
     * 编辑请求码
     */
    public static final int REQUEST_EDIT_CODE = 0x503;
    /**
     * 催办
     */
    public static final int REQUEST_URGETO_CODE = 0x505;
    /**
     * 驳回
     */
    public static final int REQUEST_REJECT_CODE = 0x506;
    /**
     * 转交
     */
    public static final int REQUEST_TRANSFER_CODE = 0x507;
    /**
     * 通过
     */
    public static final int REQUEST_PASS_CODE = 0x508;

    protected Map<String, Object> detailMap = new HashMap<>();
    private CustomLayoutResultBean.DataBean customLayoutData;
    private CustomLayoutResultBean mCustomLayoutResultBean;
    private int type;
    private int originType;
    //模块bean
    protected String moduleBean;
    //审批数据id，可以当模块数据Id用
    protected String approveDataId;
    //数据id，用于评论
    protected String dataId;
    //流程实例id
    protected String processInstanceId;
    //当前节点key
    private String taskKey;
    //当前节点ID
    private String taskId;
    //当前节点名称
    private String taskName;
    //流程状态
    private String processStatus;
    private boolean isFirstLoad = true;
    private boolean canRecall = false;
    private boolean canEdit = false;
    private boolean isMyCommit = false;
    /**
     * 当前节点 审批人ID
     */
    private List<String> currentNodeUsers;
    //权限
    private List<ApproveAuthBean> auths;
    private List<String> optionList = new ArrayList<>();
    private List<Member> ccTo;
    //流程字段版本
    private String processFieldV;
    private EmailDetailFragment emailDetailFragment;
    private CommentAdapter mCommentAdapter;
    private CommentInputView mCommentInputView;
    private LayoutData layoutData;
    private Serializable layoutDataObject;
    /**
     * 0未读 1已读
     */
    private String status;
    /**
     * 小助手进入 携带信息
     */
    private String appAssistant;

    /**
     * 待我审批
     */
    private boolean isMyTurn = false;
    /**
     * 小助手ID
     */
    private String appAssistantId;
    /**
     * 联动组件key
     */
    private List<String> linkData;
    /**
     * 发起人
     */
    private ApproverBean beginUser;
    /**
     * 流程数据
     */
    private List<ProcessFlowResponseBean.DataBean> flowData;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            moduleBean = intent.getStringExtra(ApproveConstants.MODULE_BEAN);
            approveDataId = intent.getStringExtra(ApproveConstants.APPROVAL_DATA_ID);
            taskKey = intent.getStringExtra(ApproveConstants.TASK_KEY);
            taskName = intent.getStringExtra(ApproveConstants.TASK_NAME);
            taskId = intent.getStringExtra(ApproveConstants.TASK_ID);
            processInstanceId = intent.getStringExtra(ApproveConstants.PROCESS_INSTANCE_ID);
            processFieldV = intent.getStringExtra(ApproveConstants.PROCESS_FIELD_V);
            type = intent.getIntExtra(ApproveConstants.APPROVE_TYPE, ApproveFragment.TAG4);
            originType = type;
            status = intent.getStringExtra(ApproveConstants.APPROVAL_READ);
            appAssistantId = intent.getStringExtra(ApproveConstants.APPROVAL_APP_ASSISTANT_ID);
            appAssistant = intent.getStringExtra(ApproveConstants.APPROVAL_APP_ASSISTANT);
            dataId = intent.getStringExtra(Constants.DATA_ID);
            if (TextUtils.isEmpty(appAssistant)) {
                JSONObject jo = new JSONObject();
                jo.put("dataId", approveDataId);
                jo.put("fromType", "1");
                jo.put("taskKey", taskKey);
                jo.put("moduleBean", moduleBean);
                appAssistant = JSONObject.toJSONString(jo);
            }
        }
    }


    @Override
    public void init() {
        if (ApproveConstants.EMAIL_BEAN.equals(moduleBean)) {
            FragmentManager manager = getSupportFragmentManager();
            emailDetailFragment = new EmailDetailFragment();
            manager.beginTransaction().add(R.id.fl_detail, emailDetailFragment).commit();
        }
        loadData();
        getCommentList();
        CustomService service = (CustomService) Router.getInstance().getService(CustomService.class.getSimpleName());
        service.handleHidenFields(hashCode(), toString(), viewDelegate.mViewList);
        mCommentInputView = new CommentInputView(this);
        mCommentInputView.setData(ApproveConstants.APPROVAL_MODULE_BEAN, dataId);
        viewDelegate.setCommentView(mCommentInputView);
        mCommentAdapter = new CommentAdapter(new ArrayList<>());
        mCommentAdapter.setShowMore(true);
        viewDelegate.setAdapter(mCommentAdapter);
    }

    /**
     * 得到评论列表
     */
    public void getCommentList() {
        model.getCommentDetail(mContext, dataId, ApproveConstants.APPROVAL_MODULE_BEAN, new ProgressSubscriber<CommentDetailResultBean>(mContext, false) {
            @Override
            public void onNext(CommentDetailResultBean commentDetailResultBean) {
                super.onNext(commentDetailResultBean);
                List<CommentDetailResultBean.DataBean> data = commentDetailResultBean.getData();
                CollectionUtils.notifyDataSetChanged(mCommentAdapter, mCommentAdapter.getData(), data);
                /*if (data.size() > 0) {
                    viewDelegate.showMore(false);
                    viewDelegate.getmRecyclerView().scrollToPosition(data.size() - 1);
                }*/
                if (!isFirstLoad) {
                    scrollToBottom();
                }
                isFirstLoad = false;
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageBean event) {
        if (Constants.DATA_TAG1.equals(event.getTag())) {
            File file = (File) event.getObject();
            int code = event.getCode();
            mCommentInputView.commentFileUpload(code, file);
        }
    }

    private void scrollToBottom() {
        viewDelegate.mScroll.fullScroll(View.FOCUS_DOWN);
        viewDelegate.mScroll.clearFocus();
        viewDelegate.mScroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SoftKeyboardUtils.isShown(mCommentInputView.getInputView())) {
                    mCommentInputView.getInputView().requestFocus();
                }
            }
        }, 100);
    }

    /**
     * 加载数据
     */
    protected void loadData() {
        getDataDetail();
        getCustomLayout();
        // getProcessWholeFlow();
        if (ApproveFragment.TAG2 == originType && !ApproveConstants.EMAIL_BEAN.equals(moduleBean)) {
            getLinkageFields();
        }
    }

    /**
     * 标记已读，小助手进入不执行该方法
     *
     * @param id
     */
    private void approvalRead(String id) {
        if (status == null || ApproveConstants.READ.equals(status)) {
            return;
        }
        model.approvalRead(this, id, originType + "", new ProgressSubscriber<BaseBean>(this, false) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                EventBusUtils.sendEvent(new MessageBean(ApproveConstants.REFRESH, null, null));
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    /**
     * 获取联动字段
     */
    private void getLinkageFields() {
        new CommonModel().getLinkageFields(mContext, moduleBean, new ProgressSubscriber<LinkageFieldsResultBean>(mContext) {
            @Override
            public void onNext(LinkageFieldsResultBean linkageFieldsResultBean) {
                super.onNext(linkageFieldsResultBean);
                linkData = linkageFieldsResultBean.getData();
                setLinkage();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 设置驱动
     */
    private void setLinkage() {
        if (linkData == null || CollectionUtils.isEmpty(viewDelegate.mViewList)) {
            return;
        }

        Observable.from(viewDelegate.mViewList).flatMap(new Func1<Object, Observable<BaseView>>() {
            @Override
            public Observable<BaseView> call(Object subfieldView) {
                return Observable.from(((SubfieldView) subfieldView).getViewList());
            }
        }).subscribe(baseView -> {
            if (baseView instanceof CommonSubFormsView) {
                Observable.from(((CommonSubFormsView) baseView).getViewList()).flatMap(new Func1<List<BaseView>, Observable<BaseView>>() {
                    @Override
                    public Observable<BaseView> call(List<BaseView> baseViews) {
                        return Observable.from(baseViews);
                    }
                }).subscribe(subView -> Observable.from(linkData).filter(link -> link.equals(subView.getKeyName())).subscribe(link -> subView.setLinkage()));
            } else {
                Observable.from(linkData).filter(link -> link.equals(baseView.getKeyName())).subscribe(link -> baseView.setLinkage());
            }
        });
    }

    /**
     * 获取审批流
     */
    private void getProcessWholeFlow() {
        Map<String, Object> map = new HashMap<>();
        map.put("processInstanceId", processInstanceId);
        map.put("dataId", approveDataId);
        map.put("moduleBean", moduleBean);
        model.getProcessWholeFlow(this, map, new ProgressSubscriber<ProcessFlowResponseBean>(this) {
            @Override
            public void onNext(ProcessFlowResponseBean baseBean) {
                super.onNext(baseBean);
                flowData = baseBean.getData();
                viewDelegate.setApproveTaskFlow(flowData);
                initAction(flowData);
                /*//根据从什么入口进入做判断
                if (!TextUtils.isEmpty(appAssistant)) {
                    initAction(data);
                } else {
                    setOption();
                }*/
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void initAction(List<ProcessFlowResponseBean.DataBean> data) {
        int flag = -1;
        type = ApproveFragment.TAG3;

        for (int i = 0; i < data.size(); i++) {
            if (i == 0 && SPHelper.getEmployeeId().equals(data.get(0).getApproval_employee_id())) {
                type = ApproveFragment.TAG1;
                flag = type;
                if ("0".equals(processStatus)) {
                    isMyCommit = true;
                    canRecall = true;
                }
                continue;
            } else {
                canRecall = false;
                canEdit = false;
            }

            if ("1".equals(data.get(i).getTask_status_id())
                    || "2".equals(data.get(i).getTask_status_id())
                    || "3".equals(data.get(i).getTask_status_id())
                    || "4".equals(data.get(i).getTask_status_id())
                    || "5".equals(data.get(i).getTask_status_id())) {
                //只有未进入过以上节点的待审批可撤回
                canRecall = false;
            }
            if ("1".equals(data.get(i).getTask_status_id())) {
                final String employeeIds = data.get(i).getApproval_employee_id();
                final String nextEmployeeId = data.get(i - 1).getNext_approval_employee_id();
                if (!TextUtils.isEmpty(employeeIds)) {
                    if (employeeIds.contains(",")) {
                        final String[] split = employeeIds.split(",");
                        final List<String> strings = Arrays.asList(split);
                        if (strings != null && strings.contains(SPHelper.getEmployeeId())) {
                            type = ApproveFragment.TAG2;
                            isMyTurn = true;
                            flag = type;
                            canEdit = canRecall;
                        }
                    } else {
                        SPHelper.getEmployeeId().equals(employeeIds);
                        type = ApproveFragment.TAG2;
                        isMyTurn = true;
                        flag = type;
                        canEdit = canRecall;
                    }

                }

            }
            if ("1".equals(data.get(i).getTask_status_id()) && SPHelper.getEmployeeId().equals(data.get(i).getApproval_employee_id())) {

                type = ApproveFragment.TAG2;
                isMyTurn = true;
                flag = type;
                canEdit = canRecall;
            } else {
                if (type != ApproveFragment.TAG2 && !ApproveConstants.APPROVE_PENDING.equals(processStatus)) {
                    type = ApproveFragment.TAG3;
                }
            }
        }
        //邮件/审批绘制
        drawDataLayout();
        if (flag == -1) {
            type = ApproveFragment.TAG3;
        }
        if (originType == ApproveFragment.TAG1) {
            type = ApproveFragment.TAG1;
        }
        //本人提交并且当前状态为待审批时可以撤销审批
        if (isMyCommit) {
            if (ApproveConstants.APPROVE_PENDING.equals(processStatus)) {
                canRecall = true;
            }
        }

        setOption();
    }

    private void setOption() {
        optionList.clear();
        boolean isCCtoAuth = false;
        boolean auth1 = ApproveUtils.checkAuth(auths, ApproveConstants.APPROVER_CCTO_AUTH);
        boolean auth2 = ApproveUtils.checkAuth(auths, ApproveConstants.BEGIN_CCTO_AUTH);
        boolean auth3 = ApproveUtils.checkAuth(auths, ApproveConstants.CCTO_CCTO_AUTH);
        boolean isApprover = false;
        for (int i = 0; i < flowData.size(); i++) {
            if ("1".equals(flowData.get(i).getTask_status_id()) && SPHelper.getEmployeeId().equals(flowData.get(i).getApproval_employee_id())) {
                isApprover = true;
            }
        }
        auth1 = auth1 && isApprover && originType == 1;
        auth2 = SPHelper.getEmployeeId().equals(beginUser.getId()) && auth2 && originType == 0;
        boolean isCC = false;
        for (int i = 0; i < ccTo.size(); i++) {
            if (SPHelper.getEmployeeId().equals(String.valueOf(ccTo.get(i).getId()))) {
                isCC = true;
            }
        }
        auth3 = auth3 && isCC && originType == 3;
        if (auth1 || auth2 || auth3) {
            isCCtoAuth = true;
        }
        if (isCCtoAuth && !ApproveConstants.APPROVE_REVOKED.equals(processStatus)) {
            optionList.add(ApproveConstants.CCTO);
        }
        if (ApproveFragment.TAG2 == type) {
            approvalRead(taskId);
        } else if (ApproveFragment.TAG4 == type) {
            approvalRead(processInstanceId);
        } else if (ApproveFragment.TAG3 == type) {
            approvalRead(processInstanceId);
        }
        setOptions(auths, type, processStatus);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }


    /**
     * 资料
     */
    protected void getCustomLayout() {
        if (ApproveConstants.EMAIL_BEAN.equals(moduleBean)) {
            return;
        }
        Map<String, Object> map = new HashMap<>(5);
        map.put("bean", moduleBean);
        map.put("taskKey", taskKey);
        map.put("operationType", CustomConstants.DETAIL_STATE);
        map.put("dataId", approveDataId);
        map.put("processFieldV", processFieldV);
        model.getCustomLayout(this, map, new ProgressSubscriber<CustomLayoutResultBean>(this) {
            @Override
            public void onNext(CustomLayoutResultBean customLayoutResultBean) {
                super.onNext(customLayoutResultBean);
                customLayoutData = customLayoutResultBean.getData();
                final JSONObject jsonObject = JSONObject.parseObject(customLayoutResultBean.getRaw() + "");
                final JSONObject data = jsonObject.getJSONObject("data");
                layoutDataObject = data.getJSONArray("layout");
                handleDetailData();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 得到详情数据
     */
    private void getDataDetail() {
        model.getDataDetail(this, approveDataId, moduleBean, taskKey, processFieldV, new ProgressSubscriber<DetailResultBean>(this) {
            @Override
            public void onNext(DetailResultBean detailResultBean) {
                super.onNext(detailResultBean);
                detailMap = detailResultBean.getData();
                handleDetailData();
                setLinkage();

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 处理详情数据
     */
    private synchronized void handleDetailData() {
        //非邮件审批 布局不能为null
        if (!ApproveConstants.EMAIL_BEAN.equals(moduleBean) && customLayoutData == null) {
            return;
        }
        //详情不能为空
        if (detailMap.size() == 0) {
            return;
        }
        //当前审批状态
        processStatus = TextUtil.doubleParseInt(detailMap.get("process_status") + "");
        // drawDataLayout();


        Object currentNodeUsersObject = detailMap.get("currentNodeUsers");
        currentNodeUsers = new JsonParser<String>().jsonFromList(currentNodeUsersObject, String.class);

        Object btnAuth = detailMap.get("btnAuth");
        auths = new JsonParser<ApproveAuthBean>().jsonFromList(btnAuth, ApproveAuthBean.class);
        //setOptions(auths, type, processStatus);

        Object beginUserObject = detailMap.get("beginUser");
        beginUser = new JsonParser<ApproverBean>().jsonFromObject(beginUserObject, ApproverBean.class);
        viewDelegate.setBeginUser(beginUser, processStatus);

        Object ccToObject = detailMap.get("ccTo");
        ccTo = new JsonParser<Member>().jsonFromList(ccToObject, Member.class);
        if (ccTo != null && ccTo.size() > 0) {
            viewDelegate.setCCTo(ccTo);
        }
        getProcessWholeFlow();

    }

    private void drawDataLayout() {
        if (!ApproveConstants.EMAIL_BEAN.equals(moduleBean) && customLayoutData == null) {
            return;
        }
        //邮件审批
        if (ApproveConstants.EMAIL_BEAN.equals(moduleBean)) {
            emailDetailFragment.setData(detailMap.get("mailDetail"));
        } else {
            layoutData = new LayoutData();
            layoutData.setLayout(layoutDataObject);
            layoutData.setTitle(customLayoutData.getTitle());
            layoutData.setModuleId(detailMap.get("module_id") + "");
            layoutData.setProcessId(customLayoutData.getProcessId());
            int state = CustomConstants.DETAIL_STATE;
            if (isMyTurn && ApproveConstants.APPROVE_PENDING.equals(processStatus) && originType == ApproveFragment.TAG2) {
                state = CustomConstants.APPROVE_DETAIL_STATE;
            }
            viewDelegate.drawLayout(customLayoutData, detailMap, state, moduleBean);
            setLinkage();
        }
    }


    /**
     * 设置操作按钮
     *
     * @param auths
     * @param type
     */

    public void setOptions(List<ApproveAuthBean> auths, int type, String processStatus) {
        if (ApproveConstants.APPROVE_PASS.equals(processStatus)) {
            viewDelegate.setApproveTag(R.drawable.icon_approve_pass_tag);
        } else if (ApproveConstants.APPROVE_REJECT.equals(processStatus)) {
            viewDelegate.setApproveTag(R.drawable.icon_approve_reject_tag);
        } else {
            viewDelegate.setApproveTagVisible(View.GONE);
        }
        viewDelegate.setOptionVisibility(View.VISIBLE);
        boolean isRevokeAuth = ApproveUtils.checkAuth(auths, ApproveConstants.REVOKE_AUTH);

        switch (type) {
            case ApproveFragment.TAG1: {

                switch (processStatus) {
                    case ApproveConstants.APPROVE_PENDING:
                        if (isRevokeAuth) {
                            if (canRecall) {
                                viewDelegate.setOption2Text(ApproveConstants.REVOKE);
                            } else {
                                viewDelegate.hideOption2();
                            }
                        }
                        viewDelegate.setOption1Text(ApproveConstants.URGETO);
                        break;
                    case ApproveConstants.APPROVING:
                        viewDelegate.setOption1Text(ApproveConstants.URGETO);
                        break;
                    case ApproveConstants.APPROVE_PASS:
                        viewDelegate.hideAnchor();
                        break;
                    case ApproveConstants.APPROVE_REJECT:
                        viewDelegate.hideAnchor();
                        break;
                    case ApproveConstants.APPROVE_REVOKED:
                    case ApproveConstants.APPROVE_PEND_SUBMIT:
                        viewDelegate.hideOption1();
                        viewDelegate.hideOption2();
                        viewDelegate.hideOption3();
                        viewDelegate.hideAnchor();
                        optionList.add(ApproveConstants.EDIT);
                        optionList.add(ApproveConstants.DEL);
                        break;
                    default:
                        break;
                }
                if (originType == ApproveFragment.TAG2) {
                    viewDelegate.hideOption1();
                    viewDelegate.hideOption2();
                    viewDelegate.setOptionVisibility(View.GONE);

                }
                hideUrgentIcon();
                break;
            }
            case ApproveFragment.TAG2: {
                boolean isTransferAuth = ApproveUtils.checkAuth(auths, ApproveConstants.TRANSFER_AUTH);
                if (isTransferAuth) {
                    viewDelegate.setOption6Text(ApproveConstants.TRANSFER);
                }
                viewDelegate.setOption5Text(ApproveConstants.PASS);
                viewDelegate.setOption3Text(ApproveConstants.REJECT);
                if (originType == ApproveFragment.TAG3 || originType == ApproveFragment.TAG4 || originType == ApproveFragment.TAG2) {
                    viewDelegate.hideOption1();
                    viewDelegate.hideOption2();
                }
                if (originType == ApproveFragment.TAG1 && isMyCommit) {
                    viewDelegate.setOption1Text(ApproveConstants.URGETO);
                    if (canRecall && isRevokeAuth) {
                        viewDelegate.setOption2Text(ApproveConstants.REVOKE);
                    } else {
                        viewDelegate.hideOption2();
                    }
                    viewDelegate.hideOption3();
                    viewDelegate.hideOption5();
                    viewDelegate.hideOption6();

                }
                if (isMyCommit && ApproveConstants.APPROVE_REVOKED.equals(processStatus)) {
                    optionList.add(ApproveConstants.EDIT);
                    optionList.add(ApproveConstants.DEL);
                }
                hideUrgentIcon();
                /*if (isMyCommit) {
                    optionList.add(ApproveConstants.EDIT);
                    optionList.add(ApproveConstants.DEL);
                }*/
                break;
            }
            case ApproveFragment.TAG3: {
                viewDelegate.hideAnchor();
                if (isMyCommit && ApproveConstants.APPROVE_REVOKED.equals(processStatus)
                        && originType != ApproveFragment.TAG3 && originType != ApproveFragment.TAG4
                        && originType != ApproveFragment.TAG2) {
                    optionList.add(ApproveConstants.EDIT);
                    optionList.add(ApproveConstants.DEL);
                }
                if (originType == ApproveFragment.TAG1 && isMyCommit && (ApproveConstants.APPROVING.equals(processStatus) || ApproveConstants.APPROVE_PENDING.equals(processStatus))) {
                    viewDelegate.setOptionVisibility(View.VISIBLE);
                    viewDelegate.setOption1Text(ApproveConstants.URGETO);
                }
                hideUrgentIcon();
                break;
            }
            case ApproveFragment.TAG4: {
                viewDelegate.hideAnchor();
                viewDelegate.setOptionVisibility(View.GONE);
                break;
            }
            default:
                break;
        }
        if (CollectionUtils.isEmpty(optionList)) {
            viewDelegate.showMenu();
        } else {
            viewDelegate.showMenu(0);
        }
    }

    private void hideUrgentIcon() {
        if (originType == ApproveFragment.TAG4) {
            viewDelegate.hideOption1();
            viewDelegate.hideOption2();
            viewDelegate.hideOption3();
            viewDelegate.hideOption5();
            viewDelegate.hideOption6();
            viewDelegate.setOptionVisibility(View.GONE);
        }
        if (originType == ApproveFragment.TAG3) {
            viewDelegate.hideOption1();
            viewDelegate.hideOption2();
            viewDelegate.setOptionVisibility(View.GONE);
        }
    }

    //zzh->ad:子表单增加时设置link值
    public void setCommonSubLinkage(Object o){
            if (o != null){
                List<BaseView> mViewList = (List<BaseView>) o;
                Observable.from(mViewList).subscribe(subView -> Observable.from(linkData).filter(link -> link.equals(subView.getKeyName())).subscribe(link -> {
                            subView.setLinkage();
                        }
                ));
            }

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
//        RxManager.$(mContext.hashCode()).on(EmailConstant.BEAN_NAME);
        //关联列表
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REFERENCE_TEMP_CODE, tag -> {
            SoftKeyboardUtils.hide(ApproveDetailActivity.this);
            MessageBean messageBean = (MessageBean) tag;
            UIRouter.getInstance().openUri(mContext, "DDComp://custom/referenceTemp", (Bundle) messageBean.getObject(), messageBean.getCode());
        });
        mCommentInputView.setOnChangeListener(new CommentInputView.OnChangeListener() {
            @Override
            public void onSend(int state) {
                SoftKeyboardUtils.hide(mCommentInputView.getInputView());
                scrollToBottom();
            }

            @Override
            public void onLoad(int state) {
                // getCommentList();
            }

            @Override
            public void onSuccess(CommentDetailResultBean.DataBean bean) {
                mCommentAdapter.getData().add(bean);
                mCommentAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
        viewDelegate.setOnClickListener(this, R.id.tv_option1, R.id.tv_option2, R.id.tv_option3, R.id.tv_option5, R.id.tv_option6);
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_SUBFORM_LINKAGE_CODE, tag -> {
            if (!CollectionUtils.isEmpty(linkData) && tag != null) {
                setCommonSubLinkage(tag);//zzh->修改子表单设置link设置
                //Observable.from(linkData).filter(link -> link.equals(((BaseView) tag).getKeyName())).subscribe(link -> ((BaseView) tag).setLinkage());
            }
        });
        viewDelegate.rvComment.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                CommentDetailResultBean.DataBean bean = (CommentDetailResultBean.DataBean) adapter.getItem(position);
                if (CollectionUtils.isEmpty(bean.getInformation())) {
                    return;
                }
                UploadFileBean uploadFileBean = bean.getInformation().get(0);
                if (bean.getItemType() == 2) {
                    //语音
                    bundle.putString(Constants.DATA_TAG1, uploadFileBean.getFile_url());
                    bundle.putInt(Constants.DATA_TAG2, uploadFileBean.getVoiceTime());
                    CommonUtil.startActivtiy(mContext, VoicePlayActivity.class, bundle);
                } else if (bean.getItemType() == 3) {
                    //文件
                    SocketMessage socketMessage = new SocketMessage();
                    socketMessage.setMsgID(bean.getId());
                    socketMessage.setFileName(uploadFileBean.getFile_name());
                    socketMessage.setFileUrl(uploadFileBean.getFile_url());
                    socketMessage.setFileSize(TextUtil.parseInt(uploadFileBean.getFile_size()));
                    socketMessage.setFileType(uploadFileBean.getFile_type());
                    socketMessage.setSenderName(bean.getEmployee_name());
                    socketMessage.setServerTimes(TextUtil.parseLong(bean.getDatetime_time()));
                    bundle.putSerializable(MsgConstant.MSG_DATA, socketMessage);
                    UIRouter.getInstance().openUri(mContext, "DDComp://filelib/file_detail", bundle);
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CommentDetailResultBean.DataBean bean = (CommentDetailResultBean.DataBean) adapter.getItem(position);
                UploadFileBean uploadFileBean = bean.getInformation().get(0);
                ArrayList<Photo> list = Photo.toPhotoList(uploadFileBean);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePagerActivity.PICTURE_LIST, list);
                bundle.putInt(ImagePagerActivity.SELECT_INDEX, 0);
                CommonUtil.startActivtiy(mContext, ImagePagerActivity.class, bundle);
            }
        });
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_DATA_DETAIL_CODE, tag -> UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", (Bundle) tag));
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_FILE_DETAIL_CODE, tag -> UIRouter.getInstance().openUri(mContext, "DDComp://custom/file", (Bundle) tag));
        RxManager.$(hashCode()).on(CustomConstants.MESSAGE_REPEAT_CHECK_CODE, tag -> repeatCheck((Bundle) tag));
    }

    /**
     * 查重
     *
     * @param tag
     */
    private void repeatCheck(Bundle tag) {
        UIRouter.getInstance().openUri(mContext, AppConst.MODULE_CUSTOM_REPART_CHECK, tag);
    }

    @Override
    public void onClick(View v) {
        TextView tvOption = (TextView) v;
        String option = tvOption.getText().toString();
        switch (option) {
            case ApproveConstants.PASS:
                pass();
                break;
            case ApproveConstants.URGETO:
                urgeTo();
                break;
            case ApproveConstants.TRANSFER:
                transfer();
                break;
            case ApproveConstants.REJECT:
                reject();
                break;
            case ApproveConstants.REVOKE:
                revokeApprove();
                break;
            case ApproveConstants.COMMENT:
                comment();
                break;
            default:
                break;
        }
    }

    /**
     * 评论
     */
    private void comment() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, ApproveConstants.APPROVAL_MODULE_BEAN);
        bundle.putString(Constants.DATA_ID, dataId);
        bundle.putString(ApproveConstants.APPROVAL_DATA_ID, approveDataId);
        bundle.putString(ApproveConstants.APPROVAL_BEAN, moduleBean);
        bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, processInstanceId);

        CommonUtil.startActivtiy(this, CommentActivity.class, bundle);
    }

    /**
     * 驳回
     */
    private void reject() {
        Bundle bundle = new Bundle();
        bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, processInstanceId);
        bundle.putString(ApproveConstants.APPROVE_OPTION, ApproveConstants.REJECT);
        bundle.putString(ApproveConstants.MODULE_BEAN, moduleBean);
        bundle.putString(ApproveConstants.APPROVAL_DATA_ID, approveDataId);
        bundle.putString(ApproveConstants.TASK_KEY, taskKey);
        bundle.putString(ApproveConstants.TASK_NAME, taskName);
        bundle.putString(ApproveConstants.TASK_ID, taskId);
        bundle.putSerializable(Constants.DATA_TAG1, viewDelegate.getDetail());
        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT, appAssistant);
        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT_ID, appAssistantId);
        CommonUtil.startActivtiyForResult(this, OptionApproveActivity.class, REQUEST_REJECT_CODE, bundle);
    }

    /**
     * 转交
     */
    private void transfer() {
        Bundle bundle = new Bundle();
        bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, processInstanceId);
        bundle.putString(ApproveConstants.APPROVE_OPTION, ApproveConstants.TRANSFER);
        bundle.putString(ApproveConstants.MODULE_BEAN, moduleBean);
        bundle.putString(ApproveConstants.APPROVAL_DATA_ID, approveDataId);
        bundle.putString(ApproveConstants.TASK_KEY, taskKey);
        bundle.putString(ApproveConstants.TASK_NAME, taskName);
        bundle.putString(ApproveConstants.TASK_ID, taskId);
        if (!CollectionUtils.isEmpty(currentNodeUsers)) {
            bundle.putSerializable(ApproveConstants.CURRENT_NODE_USERS, (Serializable) currentNodeUsers);
        }
        bundle.putSerializable(Constants.DATA_TAG1, viewDelegate.getDetail());
        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT, appAssistant);
        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT_ID, appAssistantId);
        CommonUtil.startActivtiyForResult(this, OptionApproveActivity.class, REQUEST_TRANSFER_CODE, bundle);
    }

    /**
     * 催办
     */
    private void urgeTo() {
        Bundle bundle = new Bundle();
        bundle.putString(ApproveConstants.MODULE_BEAN, moduleBean);
        bundle.putString(ApproveConstants.APPROVAL_DATA_ID, approveDataId);
        bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, processInstanceId);
        bundle.putString(ApproveConstants.APPROVE_OPTION, ApproveConstants.URGETO);
        CommonUtil.startActivtiyForResult(this, OptionApproveActivity.class, REQUEST_URGETO_CODE, bundle);
    }

    /**
     * 通过
     */
    private void pass() {
        JSONObject json = new JSONObject();
        if (!viewDelegate.checkData(json)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, processInstanceId);
        bundle.putString(ApproveConstants.APPROVE_OPTION, ApproveConstants.PASS);
        bundle.putString(ApproveConstants.MODULE_BEAN, moduleBean);
        bundle.putString(ApproveConstants.APPROVAL_DATA_ID, approveDataId);
        bundle.putString(ApproveConstants.TASK_KEY, taskKey);
        bundle.putString(ApproveConstants.TASK_NAME, taskName);
        bundle.putString(ApproveConstants.TASK_ID, taskId);
        bundle.putSerializable(Constants.DATA_TAG1, viewDelegate.getDetail());
        bundle.putSerializable(Constants.DATA_TAG2, ((Serializable) detailMap));
        bundle.putSerializable(Constants.DATA_TAG3, layoutData);

        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT, appAssistant);
        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT_ID, appAssistantId);
        CommonUtil.startActivtiyForResult(this, OptionApproveActivity.class, REQUEST_PASS_CODE, bundle);
    }

    /**
     * 保存自定义数据
     *
     * @param json
     */
    private void saveData(JSONObject json) {


    }

    /**
     * 撤销审批
     */
    private void revokeApprove() {
        DialogUtils.getInstance().sureOrCancel(this, "提示", "撤销后，该审批将从审批人与抄送人处撤回，审批流程将会直接终止。你确认要撤销吗？", viewDelegate.getRootView(), () -> {
                    ApproveRevokeRequestBean bean = new ApproveRevokeRequestBean();
                    bean.setProcessInstanceId(processInstanceId);
                    bean.setTaskDefinitionKey(taskKey);
                    bean.setTaskDefinitionName(taskName);
                    bean.setCurrentTaskId(taskId);
                    bean.setDataId(approveDataId);
                    bean.setModuleBean(moduleBean);
                    model.approveRevoke(ApproveDetailActivity.this, bean, new ProgressSubscriber<BaseBean>(ApproveDetailActivity.this) {
                        @Override
                        public void onNext(BaseBean baseBean) {
                            super.onNext(baseBean);
                            ToastUtils.showSuccess(ApproveDetailActivity.this, "撤销成功");
                            EventBusUtils.sendEvent(new MessageBean(ApproveConstants.REFRESH, ApproveConstants.APPROVE_REVOKED, null));
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        optionsMenu();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 操作更多
     */
    private void optionsMenu() {
        if (CollectionUtils.isEmpty(optionList)) {
            return;
        }
        String[] options = new String[optionList.size()];
        optionList.toArray(options);

        PopUtils.showBottomMenu(this, viewDelegate.getRootView(), "操作", options, position -> {
            switch (options[position]) {
                case ApproveConstants.EDIT:
                    editApprove();
                    break;
                case ApproveConstants.DEL:
                    delApprove();
                    break;
                case ApproveConstants.CCTO:
                    ccToSelectMember();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    /**
     * 编辑
     */
    private void editApprove() {
        Bundle bundle = new Bundle();
        if (ApproveConstants.EMAIL_BEAN.equals(moduleBean)) {
            bundle.putInt(Constants.DATA_TAG3, EmailConstant.EDIT_AGAIN);
            bundle.putSerializable(Constants.DATA_TAG5, moduleBean);
            UIRouter.getInstance().openUri(this, "DDComp://email/new_email", bundle, REQUEST_EDIT_CODE);
        } else {
            bundle.putString(Constants.MODULE_BEAN, moduleBean);
            bundle.putString(Constants.DATA_ID, approveDataId);
            bundle.putString(ApproveConstants.TASK_KEY, taskKey);
            bundle.putString(ApproveConstants.PROCESS_FIELD_V, processFieldV);
            bundle.putSerializable(Constants.DATA_TAG1, (Serializable) detailMap);
            bundle.putBoolean(Constants.DATA_TAG2, true);
            bundle.putBoolean(Constants.DATA_TAG3, true);
            if (ApproveConstants.APPROVE_REJECT.equals(processStatus) || ApproveConstants.APPROVE_REVOKED.equals(processStatus)) {
                bundle.putInt(ApproveConstants.OPERATION_TYPE, CustomConstants.APPROVE_AGAIN_STATE);
            }
            UIRouter.getInstance().openUri(this, "DDComp://custom/edit", bundle, REQUEST_EDIT_CODE);
        }
    }

    /**
     * 抄送选人
     */
    private void ccToSelectMember() {
        Bundle bundle = new Bundle();
        bundle.putInt(C.CHECK_TYPE_TAG, C.MULTIL_SELECT);
        CommonUtil.startActivtiyForResult(this, SelectMemberActivity.class, Constants.REQUEST_SELECT_MEMBER, bundle);
    }

    /**
     * 删除审批
     */
    private void delApprove() {
        DialogUtils.getInstance().sureOrCancel(this, "提示", "删除后不可恢复，你确认要删除吗？", viewDelegate.getRootView(), () -> {
                    model.approveDel(ApproveDetailActivity.this, moduleBean, approveDataId, new ProgressSubscriber<BaseBean>(ApproveDetailActivity.this) {
                        @Override
                        public void onNext(BaseBean baseBean) {
                            super.onNext(baseBean);
                            ToastUtils.showSuccess(ApproveDetailActivity.this, "删除成功");
                            EventBusUtils.sendEvent(new MessageBean(ApproveConstants.REFRESH, ApproveConstants.APPROVE_DEL, null));
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });
                }
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.REQUEST_SELECT_MEMBER) {
            List<Member> member = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            if (CollectionUtils.isEmpty(member)) {
                return;
            }
            DialogUtils.getInstance().sureOrCancel(this, "提示", "确定要将审批抄送给选中的成员吗？"
                    , viewDelegate.getRootView()
                    , () -> ccTo(member));
        } else if (requestCode == REQUEST_EDIT_CODE) {
            EventBusUtils.sendEvent(new MessageBean(ApproveConstants.REFRESH, null, null));
            getDataDetail();
            getCustomLayout();

        } else if (requestCode == REQUEST_REJECT_CODE) {
            EventBusUtils.sendEvent(new MessageBean(ApproveConstants.REFRESH, ApproveConstants.APPROVE_REJECT, null));
            flowChange();

        } else if (requestCode == REQUEST_PASS_CODE) {
            EventBusUtils.sendEvent(new MessageBean(ApproveConstants.REFRESH, ApproveConstants.APPROVE_PASS, null));
            type = ApproveFragment.TAG3;
            originType = ApproveFragment.TAG3;
            flowChange();
        } else if (requestCode == REQUEST_TRANSFER_CODE) {
            EventBusUtils.sendEvent(new MessageBean(ApproveConstants.REFRESH, ApproveConstants.APPROVE_TRANSFER, null));

            flowChange();
        }
        mCommentInputView.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == Constants.TAKE_PHOTO_NEW_REQUEST_CODE) {
            //上传拍照图片
            mCommentInputView.prepareUploadPic();
        } else if (requestCode == Constants.PHOTO_ABLUM_NEW_REQUEST_CODE) {
            //上传相册图片
            Uri selectedImage = data.getData();
            mCommentInputView.picktrueUpload(selectedImage);
        } else if (requestCode == Constants.SELECT_FILE_NEW_REQUEST_CODE) {
            //文件
            Uri uri = data.getData();
            mCommentInputView.fileUpload(uri);
        } else if (requestCode == Constants.RESULT_CODE_SELECT_MEMBER) {
            //@功能
            ArrayList<Member> members = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            mCommentInputView.appendMention(members);
        }*/


    }

    private void flowChange() {
        viewDelegate.hideOption1();
        viewDelegate.hideOption2();
        viewDelegate.hideOption3();
        viewDelegate.hideOption5();
        viewDelegate.hideOption6();
        //getProcessWholeFlow();
        getDataDetail();
    }

    /**
     * 抄送
     */
    private void ccTo(List<Member> members) {
        if (CollectionUtils.isEmpty(members)) {
            ToastUtils.showToast(ApproveDetailActivity.this, "请选择抄送人");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Member member : members) {
            sb.append(member.getId() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);

        ApproveCopyRequestBean bean = new ApproveCopyRequestBean();
        bean.setProcessInstanceId(processInstanceId);
        bean.setTaskDefinitionKey(taskKey);
        bean.setTaskDefinitionId(taskId);
        bean.setBeanName(moduleBean);
        bean.setCcTo(sb.toString());
        bean.setDataId(approveDataId);
        model.approveCopy(this, bean, new ProgressSubscriber<BaseBean>(this) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(ApproveDetailActivity.this, "抄送成功");
                getDataDetail();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    public JSONObject getReferenceValue() {
        JSONObject json = new JSONObject();
        CustomService service = (CustomService) Router.getInstance().getService(CustomService.class.getSimpleName());
        service.putReference(viewDelegate.mViewList, json);
        return json;
    }
}

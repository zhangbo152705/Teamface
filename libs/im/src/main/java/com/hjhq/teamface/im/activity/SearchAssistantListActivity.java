package com.hjhq.teamface.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.QueryApprovalDataResultBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.PushMessage;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.im.ImLogic;
import com.hjhq.teamface.im.R;
import com.hjhq.teamface.im.adapter.AssistantListAdapter;
import com.hjhq.teamface.im.bean.ModuleBean;
import com.hjhq.teamface.im.db.DBManager;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小助手
 *
 * @author Administrator
 */
public class SearchAssistantListActivity extends BaseActivity {
    private static final String TAG = "SearchAssistantListActivity";


    private RxAppCompatActivity mContext;
    private SearchBar mSearchBar;
    private RecyclerView rvList;
    private String assistantId;
    private String keyword;
    private String titleString;
    private String beanName;
    private AssistantListAdapter mAdapter;
    private ArrayList<PushMessage> dataList = new ArrayList<>();
    private List<ModuleBean.DataBean> data = new ArrayList<>();
    private int mPosition = 0;
    private EmptyView mEmptyView;
    private TextView title;
    private View chatHeadView;

    @Override
    protected int getContentView() {
        return R.layout.activity_search_chat_detail;
    }

    @Override
    protected void initView() {
        mEmptyView = new EmptyView(this);
        mEmptyView.setEmptyImage(R.drawable.empty_view_img);
        mContext = this;
        mSearchBar = (SearchBar) findViewById(R.id.search_bar);
        rvList = (RecyclerView) findViewById(R.id.rv_contacts);
        chatHeadView = inflater.inflate(R.layout.item_search_result_header, null);
        title = (TextView) chatHeadView.findViewById(R.id.name);
        initAdapter();
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dataList.addAll((ArrayList<PushMessage>) bundle.getSerializable(Constants.DATA_TAG1));
            mAdapter.notifyDataSetChanged();
            keyword = bundle.getString(Constants.DATA_TAG2);
            assistantId = bundle.getString(MsgConstant.CONVERSATION_ID);
            titleString = bundle.getString(MsgConstant.CONV_TITLE);
            beanName = bundle.getString(MsgConstant.BEAN_NAME);
            TextUtil.setText(title, titleString);
            mSearchBar.setText(keyword, false);
            mSearchBar.setHintText(titleString);
        }
    }

    private void initAdapter() {
        mAdapter = new AssistantListAdapter(dataList);
        mAdapter.addHeaderView(chatHeadView);
        mAdapter.setEmptyView(mEmptyView);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(mAdapter);

    }

    @Override
    protected void setListener() {
        rvList.addOnItemTouchListener(new SimpleItemClickListener() {
            Bundle bundle = new Bundle();

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                //小助手类型
                switch (dataList.get(position).getType()) {
                    case MsgConstant.TYPE_GROUP_OPERATION:
                    case MsgConstant.TYPE_GROUP_INFO_CHANGED:
                    case MsgConstant.TYPE_QUIT_GROUP:
                    case MsgConstant.TYPE_ADD_MEMBER:
                    case MsgConstant.TYPE_REMOVE_MEMBER:
                        //showPreview(position);
                        break;
                    case MsgConstant.TYPE_EMAIL:
                        //邮件推送
                        bundle.putString(Constants.DATA_TAG1, dataList.get(position).getData_id());
                        bundle.putInt(Constants.DATA_TAG2, EmailConstant.NO_OPERATION);
                        UIRouter.getInstance().openUri(mContext, "DDComp://email/detail", bundle, Constants.REQUEST_CODE2);
                        break;
                    case MsgConstant.TYPE_AT_ME:
                        viewAtData(position);
                        break;
                    case MsgConstant.TYPE_SELF_DEFINE:
                        //自定义推送
                        //模块权限判断
                        viewModuleDataWithAuth(position);
                        break;
                    case MsgConstant.TYPE_APPROVE:
                        viewApprovalWithoutAuth(position);
                        break;
                    case MsgConstant.TYPE_FROM_FILE_LIB:
                        viewFileWithAuthV2(position);
                        break;
                    case MsgConstant.TYPE_FRIEND_CIRCLE:
                        //CommonUtil.startActivtiy(mContext, FriendsActivity.class, new Bundle());
                        UIRouter.getInstance().openUri(mContext, "DDComp://app/friend/detail", bundle);
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            final boolean b = AppManager.getAppManager().foregroundActivity(mContext, "FriendsActivity");
                            if (!b) {
                                UIRouter.getInstance().openUri(mContext, "DDComp://friend/detail", bundle);
                            }
                        } else {
                            UIRouter.getInstance().openUri(mContext, "DDComp://friend/detail", bundle);
                        }*/
                        break;
                    case MsgConstant.TYPE_MEMO:
                        viewMemoWithAuth(position);
                        break;
                    case MsgConstant.TYPE_FLOW:
                        break;
                    case MsgConstant.TYPE_PROJECT_TASK:
                        //项目任务
                        viewProjectTaskWithAuth(dataList.get(position));
                        break;
                    case MsgConstant.TYPE_PERSONAL_TASK:
                        //个人任务
                        viewPersonalTaskWithAuth(dataList.get(position));
                        break;
                    default:
                        break;
                }
                markReaded(position);
            }


        });
        mSearchBar.setSearchListener(new SearchBar.SearchListener() {
            @Override
            public void clear() {

            }

            @Override
            public void cancel() {
                finish();
            }

            @Override
            public void search() {
                searchData();
                SoftKeyboardUtils.hide(mSearchBar.getEditText());
            }

            @Override
            public void getText(String text) {
                keyword = text;
                searchData();
            }
        });

    }

    private void searchData() {
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        List<PushMessage> pushMessages = DBManager.getInstance().queryAssistantMessage(assistantId, keyword);
        dataList.clear();
        dataList.addAll(pushMessages);
        mAdapter.notifyDataSetChanged();


    }

    /**
     * 标记已读
     *
     * @param position
     */
    private void markReaded(final int position) {
        if (TextUtils.isEmpty(dataList.get(position).getId() + "")) {
            return;
        }
        if ("0".equals(dataList.get(position).getRead_status())) {
            ImLogic.getInstance().readMessage(SearchAssistantListActivity.this, assistantId, dataList.get(position).getId() + "", new ProgressSubscriber<BaseBean>(SearchAssistantListActivity.this) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    // ToastUtils.showToast(mContext, "已读请求失败!");
                }

                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);

                    if (TextUtils.isEmpty(dataList.get(position).getId() + "")) {
                        return;
                    }
                    dataList.get(position).setRead_status("1");
                    mAdapter.notifyDataSetChanged();
                    EventBusUtils.sendEvent(new MessageBean(11, MsgConstant.ASSISTANT_READ_TAG, dataList.get(position)));
                }
            });
        }
    }

    /**
     * 查看项目任务
     *
     * @param message
     */
    private void viewProjectTaskWithAuth(PushMessage message) {


        ImLogic.getInstance().queryAuth(SearchAssistantListActivity.this,
                message.getBean_name(), message.getStyle(),
                message.getData_id(), message.getParam_fields(),
                new ProgressSubscriber<ViewDataAuthResBean>(SearchAssistantListActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean == null || viewDataAuthResBean.getData() == null
                                || TextUtils.isEmpty(viewDataAuthResBean.getData().getReadAuth())) {
                            // ToastUtils.showError(mContext, "查询权限失败");
                        } else {
                            String readAuth = viewDataAuthResBean.getData().getReadAuth();
                            switch (readAuth) {
                                case MsgConstant.AUTH_NO:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case MsgConstant.AUTH_YES:
                                    //
                                    viewProjectTaskWithoutAuth(message);
                                    break;
                                case MsgConstant.AUTH_DATA_DELETED:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case MsgConstant.AUTH_SEAS_POOL:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                default:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                            }
                        }
                    }
                });

    }

    /**
     * 查看项目任务
     *
     * @param message
     */
    private void viewProjectTaskWithoutAuth(PushMessage message) {
        try {
            if (message == null || TextUtils.isEmpty(message.getParam_fields())) {
                return;
            }
            Bundle bundle = new Bundle();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message.getParam_fields());
            if (jsonObject == null) {
                return;
            }
            //1主任务,2子任务
            int taskType = TextUtils.isEmpty(jsonObject.optString("task_Type")) ? 1 : 2;
            String projectId = jsonObject.optString("projectId");
            String taskId = jsonObject.optString("task_id");
            String mainTaskId = jsonObject.optString("taskInfoId");
            String id = jsonObject.optString("id");
            bundle.putInt(ProjectConstants.TASK_FROM_TYPE, taskType);
            bundle.putLong(ProjectConstants.PROJECT_ID, TextUtil.parseLong(projectId));
            if (taskType == 2) {
                bundle.putLong(ProjectConstants.TASK_ID, TextUtil.parseLong(mainTaskId));
                // bundle.putLong(ProjectConstants.MAIN_TASK_ID, TextUtil.parseLong(mainTaskId));
            } else if (taskType == 1) {
                //主任务
                bundle.putLong(ProjectConstants.TASK_ID, TextUtil.parseLong(mainTaskId));

            }
            bundle.putString(ProjectConstants.TASK_NAME, jsonObject.optString("taskName"));
            bundle.putString(Constants.MODULE_BEAN, message.getBean_name());
            UIRouter.getInstance().openUri(mContext, "DDComp://project/project_task_detail", bundle, Constants.REQUEST_CODE2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void viewPersonalTaskWithAuth(PushMessage message) {
        ImLogic.getInstance().queryAuth(SearchAssistantListActivity.this,
                message.getBean_name(), message.getStyle(),
                message.getData_id(), message.getParam_fields(),
                new ProgressSubscriber<ViewDataAuthResBean>(SearchAssistantListActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean == null || viewDataAuthResBean.getData() == null
                                || TextUtils.isEmpty(viewDataAuthResBean.getData().getReadAuth())) {
                            // ToastUtils.showError(mContext, "查询权限失败");
                        } else {
                            String readAuth = viewDataAuthResBean.getData().getReadAuth();
                            switch (readAuth) {
                                case MsgConstant.AUTH_NO:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case MsgConstant.AUTH_YES:
                                    //
                                    viewPersonalTaskWithoutAuth(message);
                                    break;
                                case MsgConstant.AUTH_DATA_DELETED:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case MsgConstant.AUTH_SEAS_POOL:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                default:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 查看个人任务
     *
     * @param message
     */
    private void viewPersonalTaskWithoutAuth(PushMessage message) {
        try {
            if (message == null || TextUtils.isEmpty(message.getParam_fields())) {
                return;
            }
            Bundle bundle = new Bundle();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message.getParam_fields());
            if (jsonObject == null) {
                return;
            }
            //0主任务1子任务
            String taskId = jsonObject.optString("task_id");
            String id = jsonObject.optString("id");
            String mainTaskId = jsonObject.optString("taskInfoId");
            int taskType = TextUtils.isEmpty(jsonObject.optString("task_Type")) ? 0 : 1;
           /* if (taskType == 0) {
                bundle.putLong(ProjectConstants.TASK_ID, TextUtil.parseLong(taskId));
            } else if (taskType == 1) {
            }*/
            bundle.putLong(ProjectConstants.TASK_ID, TextUtil.parseLong(taskId));
            bundle.putInt(ProjectConstants.TASK_FROM_TYPE, taskType);

            bundle.putString(ProjectConstants.TASK_NAME, jsonObject.optString("taskName"));
            bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PERSONAL_TASK_BEAN);
            UIRouter.getInstance().openUri(mContext, "DDComp://project/personal_task_detail", bundle, Constants.REQUEST_CODE2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看文件
     *
     * @param position
     */
    private void viewFileWithAuth(final int position) {
        ImLogic.getInstance().queryAuth(SearchAssistantListActivity.this,
                FileConstants.FILE_LIBRARY_BEAN_NAME,
                dataList.get(position).getStyle(), dataList.get(position).getData_id(), "",
                new ProgressSubscriber<ViewDataAuthResBean>(SearchAssistantListActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean.getData() != null && !TextUtils.isEmpty(viewDataAuthResBean.getData().getReadAuth())) {
                            switch (viewDataAuthResBean.getData().getReadAuth()) {
                                case "0":
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case "1":
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FileConstants.FILE_ID, dataList.get(position).getData_id());
                                    bundle.putInt(FileConstants.FOLDER_STYLE, TextUtil.parseInt(dataList.get(position).getStyle()));
                                    UIRouter.getInstance().openUri(mContext, "DDComp://filelib/file_detail", bundle, Constants.REQUEST_CODE1);
                                    break;
                                case "2":
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                default:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                            }
                        } else {
                            // ToastUtils.showError(mContext, "查询权限失败");
                        }
                    }
                });
    }

    /**
     * 查看文件库文件(已做权限控制)
     *
     * @param position
     */
    private void viewFileWithAuthV2(final int position) {
        ImLogic.getInstance().queryAuth(SearchAssistantListActivity.this,
                dataList.get(position).getBean_name(), dataList.get(position).getStyle(),
                dataList.get(position).getData_id(), "",
                new ProgressSubscriber<ViewDataAuthResBean>(SearchAssistantListActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        // ToastUtils.showError(mContext, "查询权限失败");
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean.getData() != null && "1".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            Bundle bundle = new Bundle();
                            bundle.putString(FileConstants.FILE_ID, dataList.get(position).getData_id());
                            bundle.putInt(FileConstants.FOLDER_STYLE, TextUtil.parseInt(dataList.get(position).getStyle()));
                            UIRouter.getInstance().openUri(mContext, "DDComp://filelib/file_detail", bundle, Constants.REQUEST_CODE1);

                        } /*else if (viewDataAuthResBean.getData() != null && "0".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");

                        } else if (viewDataAuthResBean.getData() != null && "2".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                        } */ else {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                            //ToastUtils.showError(mContext, "查询权限失败");
                        }
                    }
                });
    }

    /**
     * 判断权限,查看数据(@数据)
     *
     * @param position
     */
    private void viewAtData(int position) {
        PushMessage dataListBean = dataList.get(position);
        if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(dataListBean.getBean_name())) {
            viewApprovalWithoutAuth(position);
            return;
        }
        ImLogic.getInstance().queryAuth(SearchAssistantListActivity.this,
                dataListBean.getBean_name(), dataListBean.getStyle(),
                dataListBean.getData_id(), "",
                new ProgressSubscriber<ViewDataAuthResBean>(SearchAssistantListActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        // ToastUtils.showToast(mContext, "查询权限失败");
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean.getData() != null && "1".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            Bundle bundle = new Bundle();
                            switch (dataList.get(position).getBean_name()) {
                                case MemoConstant.BEAN_NAME:
                                    viewMemoWithoutAuth(position);
                                    break;
                                case EmailConstant.BEAN_NAME:
                                    bundle.putString(Constants.DATA_TAG1, dataListBean.getData_id());
                                    bundle.putInt(Constants.DATA_TAG2, EmailConstant.NO_OPERATION);
                                    UIRouter.getInstance().openUri(mContext, "DDComp://email/detail", bundle, Constants.REQUEST_CODE2);
                                    break;
                                case ProjectConstants.PROJECT_BEAN_NAME:

                                    break;
                                case FileConstants.FILE_LIBRARY_BEAN_NAME:
                                    bundle.putString(FileConstants.FILE_ID, dataListBean.getData_id());
                                    bundle.putInt(FileConstants.FOLDER_STYLE, TextUtil.parseInt(dataListBean.getStyle()));
                                    UIRouter.getInstance().openUri(mContext, "DDComp://filelib/file_detail", bundle, Constants.REQUEST_CODE1);

                                    break;
                                default:
                                    break;
                            }
                        } /*else if (viewDataAuthResBean.getData() != null && "0".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");

                        } else if (viewDataAuthResBean.getData() != null && "2".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                        } else if (viewDataAuthResBean.getData() != null && "3".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                        } */ else {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                            // ToastUtils.showError(mContext, "查询权限失败");
                        }
                    }
                });
    }

    /**
     * 查看备忘录(包括权限验证)
     *
     * @param position
     */
    private void viewMemoWithAuth(final int position) {
        ImLogic.getInstance().queryAuth(SearchAssistantListActivity.this,
                dataList.get(position).getBean_name(), dataList.get(position).getStyle(),
                dataList.get(position).getData_id(), "",
                new ProgressSubscriber<ViewDataAuthResBean>(SearchAssistantListActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        //  ToastUtils.showError(mContext, "查询权限失败");
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean.getData() != null && "1".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            viewMemoWithoutAuth(position);
                        } /*else if (viewDataAuthResBean.getData() != null && "0".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                        } else if (viewDataAuthResBean.getData() != null && "2".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                        } else if (viewDataAuthResBean.getData() != null && "3".equals(viewDataAuthResBean.getData().getReadAuth())) {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                        } */ else {
                            ToastUtils.showError(mContext, "无权查看或数据已删除");
                            //ToastUtils.showError(mContext, "查询权限失败");
                        }
                    }
                });
    }

    /**
     * 查询权限查看自定义模块数据详情
     *
     * @param position
     */
    private void viewModuleDataWithAuth(final int position) {
        ImLogic.getInstance().queryAuth(SearchAssistantListActivity.this,
                dataList.get(position).getBean_name(), dataList.get(position).getStyle(),
                dataList.get(position).getData_id(), "",
                new ProgressSubscriber<ViewDataAuthResBean>(SearchAssistantListActivity.this) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        // ToastUtils.showError(mContext, "查询权限失败");
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean viewDataAuthResBean) {
                        super.onNext(viewDataAuthResBean);
                        if (viewDataAuthResBean == null || viewDataAuthResBean.getData() == null || TextUtils.isEmpty(viewDataAuthResBean.getData().getReadAuth())) {
                            // ToastUtils.showError(mContext, "查询权限失败");
                        } else {
                            String readAuth = viewDataAuthResBean.getData().getReadAuth();
                            switch (readAuth) {
                                case MsgConstant.AUTH_NO:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case MsgConstant.AUTH_YES:
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Constants.MODULE_BEAN, dataList.get(position).getBean_name());
                                    bundle.putString(Constants.DATA_ID, dataList.get(position).getData_id());
                                    UIRouter.getInstance().openUri(mContext, "DDComp://custom/detail", bundle, Constants.REQUEST_CODE1);
                                    break;
                                case MsgConstant.AUTH_DATA_DELETED:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                case MsgConstant.AUTH_SEAS_POOL:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                                default:
                                    ToastUtils.showError(mContext, "无权查看或数据已删除");
                                    break;
                            }
                        }
                    }
                });
    }

    /**
     * 查看审批 (验证权限)
     *
     * @param position
     */
    private void viewApprovalWithoutAuth(int position) {
        PushMessage bean = dataList.get(position);
        String paramFields = bean.getParam_fields();
        if (paramFields == null) {
            ToastUtils.showError(mContext, "数据异常");

            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(paramFields);
        String moduleBean = jsonObject.getString("moduleBean");
        String fromType = jsonObject.getString("fromType");
        String dataId = jsonObject.getString("dataId");
        String processInstanceId = jsonObject.getString("processInstanceId");

        if (TextUtil.isEmpty(moduleBean) || TextUtil.isEmpty(dataId) || TextUtil.isEmpty(fromType)) {
            ToastUtils.showError(mContext, "数据异常");
            return;
        }

        Map<String, String> map = new HashMap<>(4);
        map.put("moduleBean", moduleBean);
        map.put("dataId", dataId);
        map.put("type", fromType);
        map.put("processInstanceId", processInstanceId);
        ImLogic.getInstance()
                .queryApprovalData(SearchAssistantListActivity.this, map, new ProgressSubscriber<QueryApprovalDataResultBean>(mContext) {
                    @Override
                    public void onNext(QueryApprovalDataResultBean approvalBean) {
                        super.onNext(approvalBean);
                        QueryApprovalDataResultBean.DataBean data = approvalBean.getData();

                        Bundle bundle = new Bundle();
                        bundle.putString(ApproveConstants.PROCESS_INSTANCE_ID, data.getProcess_definition_id());
                        bundle.putString(ApproveConstants.PROCESS_FIELD_V, data.getProcess_field_v());
                        bundle.putString(ApproveConstants.MODULE_BEAN, moduleBean);
                        bundle.putString(ApproveConstants.APPROVAL_DATA_ID, data.getApproval_data_id());
                        bundle.putString(ApproveConstants.TASK_KEY, data.getTask_key());
                        bundle.putString(ApproveConstants.TASK_NAME, data.getTask_name());
                        bundle.putString(ApproveConstants.TASK_ID, data.getTask_id());
                        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT, paramFields);
                        bundle.putString(ApproveConstants.APPROVAL_APP_ASSISTANT_ID, bean.getId() + "");
                        bundle.putString(Constants.DATA_ID, data.getId());
                        //审批小助手评论消息 使用详情返回的fromType
                        bundle.putInt(ApproveConstants.APPROVE_TYPE, TextUtil.parseInt(data.getFromType()));
                        UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/detail", bundle);
                        //CommonUtil.startActivtiy(SearchAssistantListActivity.this, ApproveDetailActivity.class, bundle);

                    }
                });
    }

    /**
     * 查看备忘录
     *
     * @param position
     */
    private void viewMemoWithoutAuth(int position) {
        Bundle bundle2 = new Bundle();
        bundle2.putString(Constants.DATA_TAG1, dataList.get(position).getData_id());
        UIRouter.getInstance().openUri(mContext, "DDComp://memo/detail", bundle2, Constants.REQUEST_CODE1);
    }


    @Override
    public void onClick(View v) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case Constants.REQUEST_CODE1:

                    break;
                case Constants.REQUEST_CODE2:

                    break;
                case Constants.REQUEST_CODE3:

                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


}

package com.hjhq.teamface.project.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.ApproveListBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.MemoListItemBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.bean.PersonalTaskRoleResultBan;
import com.hjhq.teamface.basis.bean.QueryTaskCompleteAuthResultBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.CacheDataHelper;
import com.hjhq.teamface.basis.network.subscribers.BaseSubscriber;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.ActionSheetDialog;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.bean.TaskListBean;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.ProjectDialog;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;
import com.hjhq.teamface.common.view.boardview.TaskBoardView;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.AddTaskRequestBean;
import com.hjhq.teamface.project.bean.AllNodeResultBean;
import com.hjhq.teamface.project.bean.NodeBean;
import com.hjhq.teamface.project.bean.QueryTaskAuthResultBean;
import com.hjhq.teamface.project.bean.SortNodeRequestBean;
import com.hjhq.teamface.project.bean.TaskMemberListResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.model.TaskModel;
import com.hjhq.teamface.project.presenter.navigation.AddTaskTempActivity;
import com.hjhq.teamface.project.presenter.navigation.EditTaskLinkActivity;
import com.hjhq.teamface.project.presenter.navigation.EditTaskTempActivity;
import com.hjhq.teamface.project.presenter.task.AddTaskActivity;
import com.hjhq.teamface.project.presenter.task.QuoteTaskActivity;
import com.hjhq.teamface.project.ui.TaskListDelegate;
import com.hjhq.teamface.project.util.ProjectUtil;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

import static com.hjhq.teamface.basis.constants.Constants.DATA_TAG1;

/**
 * 项目详情中的任务列表
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class TaskTempFragment extends FragmentPresenter<TaskListDelegate, ProjectModel> {
    public int state = Constants.REFRESH_STATE;

    private long nodeId;
    private ProjectDetailActivity mActivity;
    /**
     * 拖拽前列的位置
     */
    private int oldColumn;
    /**
     * 拖拽前行的位置
     */
    private int oldRow;
    /**
     * 拖拽前列表位置
     */
    private int oldTempIndex;
    /**
     * 当前操作的子节点坐标
     */
    private int columnPosition;
    /**
     * 任务列表下标
     */
    private int taskListIndex = 0;
    private int currentListIndex = 0;
    private int hashCode;
    /**
     * 子节点
     */
    List<NodeBean> subNodeList;
    List<TaskItemAdapter> adapterList = new ArrayList<>();
    List<Long> subNodeIdList = new ArrayList<>();
    /**
     * 任务集合
     */
    private List<NodeBean> taskList;
    /**
     * 四层结构
     */
    private boolean isFourLayer;
    private boolean[] taskAuths;
    private boolean canLongClick = false;
    boolean isTemplateGroup = true;
    private Map<String, Object> paramMap = new HashMap<>();

    public static TaskTempFragment newInstance(long nodeId, List<NodeBean> subnodeArr, boolean isTemplate) {
        TaskTempFragment myFragment = new TaskTempFragment();
        Bundle bundle = new Bundle();
        if (subnodeArr == null) {
            subnodeArr = new ArrayList<>();
        }
        bundle.putSerializable(DATA_TAG1, (Serializable) subnodeArr);
        bundle.putLong(ProjectConstants.NODE_ID, nodeId);
        bundle.putBoolean(Constants.DATA_TAG2, isTemplate);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            subNodeList = (List<NodeBean>) arguments.getSerializable(DATA_TAG1);
            nodeId = arguments.getLong(ProjectConstants.NODE_ID, 0);
            isTemplateGroup = arguments.getBoolean(Constants.DATA_TAG2);
        }
        hashCode = hashCode() % 10000;
    }


    @Override
    protected void init() {
        mActivity = (ProjectDetailActivity) getActivity();
        initData();
    }

    private void initData() {
        isFourLayer = !CollectionUtils.isEmpty(subNodeList) && "1".equals(subNodeList.get(0).getChildren_data_type());
        if (isFourLayer) {
            //三层结构
            viewDelegate.setVisibility(R.id.ll_sub_list, true);
            viewDelegate.setText(R.id.tv_sub_list_name, subNodeList.get(0).getName());
            taskList = subNodeList.get(0).getSubnodeArr();
        } else {
            taskList = subNodeList;
        }
        initTaskList();
    }

    /**
     * 初始化任务列表
     */
    private void initTaskList() {
        adapterList.clear();
        subNodeIdList.clear();
        viewDelegate.removePoint();
        loadTaskList(taskList);
        viewDelegate.setPointIndex(0);
        if (isFourLayer) {
            viewDelegate.setAddPointVisibility(false);
        } else {
            if (ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 20)) {
                viewDelegate.setAddPointVisibility(true);
                View empty = View.inflate(mActivity, R.layout.project_column_empty, null);
                empty.setOnClickListener(v -> {
                    if (ProjectDetailActivity.IS_TEMPLETE_PROJECT) {
                        ToastUtils.showToast(getActivity(), "模板项目不能操作");
                        return;
                    }
                    if (isTemplateGroup) {
                        ToastUtils.showToast(getActivity(), "模板分组不能操作");
                        return;
                    }
                    ProjectUtil.INSTANCE.checkProjectStatus(getContext(), ProjectDetailActivity.projectStatus, () -> {
                        Bundle bundle = new Bundle();
                        bundle.putLong(ProjectConstants.PROJECT_ID, mActivity.projectId);
                        bundle.putLong(ProjectConstants.NODE_ID, nodeId);
                        CommonUtil.startActivtiyForResult(mActivity, AddTaskTempActivity.class, ProjectConstants.PROJECT_SUB_NODE_ADD_TAG, bundle);
                    });

                });
                viewDelegate.mBoardView.addEmptyColumn(empty);
            } else {
                viewDelegate.setAddPointVisibility(false);
            }

        }
    }

    /**
     * 加载任务列表
     */
    private void loadTaskList(List<NodeBean> taskList) {
        if (CollectionUtils.isEmpty(taskList)) {
            return;
        }
        int size = taskList.size();
        for (int i = 0; i < size; i++) {
            NodeBean nodeBean = taskList.get(i);
            String taskTempName = nodeBean.getName();
            long subNodeId = nodeBean.getId();
            TaskItemAdapter listAdapter = new TaskItemAdapter(null, true);
            adapterList.add(listAdapter);
            subNodeIdList.add(subNodeId);
            View header = View.inflate(mActivity, R.layout.project_column_header, null);
            View footer = View.inflate(mActivity, R.layout.project_column_footer, null);
            TextView tvTaskTempName = header.findViewById(R.id.tv_task_temp_name);
            List<ActionSheetDialog.SheetItem> actionSheetItem = getActionSheetItem(nodeBean);
            if (actionSheetItem.size() > 0) {
                header.findViewById(R.id.iv_task_menu).setVisibility(View.VISIBLE);
            } else {
                header.findViewById(R.id.iv_task_menu).setVisibility(View.GONE);
            }
            /*//四层结构
            if (isFourLayer) {
                header.findViewById(R.id.iv_task_menu).setVisibility(View.GONE);
                footer = null;
            } else {
                //项目管理状态（0进行中（启用）1归档2暂停3删除
                header.findViewById(R.id.iv_task_menu).setOnClickListener(v -> {
                            switch (ProjectDetailActivity.projectStatus) {
                                case "0":
                                    new ActionSheetDialog(mActivity)
                                            .builder()
                                            .setCancelable(false)
                                            .setCanceledOnTouchOutside(false)
                                            .addSheetItem(actionSheetItem).show();
                                    break;
                                case "1":
                                    ToastUtils.showError(mActivity, "项目已归档");
                                    break;
                                case "2":
                                    ToastUtils.showError(mActivity, "项目已暂停");
                                    break;
                                case "3":
                                    ToastUtils.showError(mActivity, "项目已删除");
                                    break;
                            }

                        }

                );

                footer.setOnClickListener(v -> {
                    switch (ProjectDetailActivity.projectStatus) {
                        case "0":
                            if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 25)) {
                                ToastUtils.showError(mActivity, "无权限");
                                return;
                            }
                            PopUtils.showBottomMenu(mActivity, viewDelegate.getRootView(), null, new String[]{"新建", "引用"}, p -> {
                                Bundle bundle = new Bundle();
                                columnPosition = subNodeList.indexOf(nodeBean);
                                bundle.putInt(DATA_TAG1, Constants.SELECT_FOR_TASK);
                                if (p == 0) {
                                    CommonUtil.startActivtiyForResult(mActivity, SelectModuleActivity.class, ProjectConstants.ADD_TASK_REQUEST_CODE + hashCode, bundle);
                                } else {
                                    CommonUtil.startActivtiyForResult(mActivity, SelectModuleActivity.class, ProjectConstants.QUOTE_TASK_REQUEST_CODE + hashCode, bundle);
                                }
                                return false;
                            });
                            break;
                        case "1":
                            ToastUtils.showError(mActivity, "项目已归档");
                            break;
                        case "2":
                            ToastUtils.showError(mActivity, "项目已暂停");
                            break;
                        case "3":
                            ToastUtils.showError(mActivity, "项目已删除");
                            break;
                    }

                });
            }
            if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 25)) {
                footer = null;
            }*/
            header.findViewById(R.id.iv_task_menu).setOnClickListener(v -> {
                        ProjectUtil.INSTANCE.checkProjectStatus(getActivity(), ProjectDetailActivity.projectStatus, () -> {
                            new ActionSheetDialog(mActivity)
                                    .builder()
                                    .setCancelable(false)
                                    .setCanceledOnTouchOutside(false)
                                    .addSheetItem(actionSheetItem).show();
                        });

                    }

            );

            footer.setOnClickListener(v -> {
                ProjectUtil.INSTANCE.checkProjectStatus(getActivity(), ProjectDetailActivity.projectStatus, () -> {
                    if (!ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 25)) {
                        ToastUtils.showError(mActivity, "无权限");
                        return;
                    }
                    PopUtils.showBottomMenu(mActivity, viewDelegate.getRootView(), null, new String[]{"新建", "引用"}, p -> {
                        Bundle bundle = new Bundle();
                        if (isFourLayer) {
                            columnPosition = subNodeList.get(currentListIndex).getSubnodeArr().indexOf(nodeBean);
                        } else {
                            columnPosition = subNodeList.indexOf(nodeBean);
                        }
                        bundle.putInt(DATA_TAG1, Constants.SELECT_FOR_TASK);
                        if (p == 0) {
                            CommonUtil.startActivtiyForResult(mActivity, SelectModuleActivity.class, ProjectConstants.ADD_TASK_REQUEST_CODE + hashCode, bundle);
                        } else {
                            CommonUtil.startActivtiyForResult(mActivity, SelectModuleActivity.class, ProjectConstants.QUOTE_TASK_REQUEST_CODE + hashCode, bundle);
                        }
                        return false;
                    });
                });

            });
            TextUtil.setText(tvTaskTempName, taskTempName);
            viewDelegate.mBoardView.addColumnList(listAdapter, header, header, footer, false);
            listAdapter.setOnItemClickListener(new TaskItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DragItemAdapter adapter, View view, int position) {
                    try {
                        TaskInfoBean item = listAdapter.getItem(position);
                        TaskHelper.INSTANCE.clickTaskItem(mActivity, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("TaskTempFragment", e.getMessage());
                    }
                }

                @Override
                public void onItemChildClick(DragItemAdapter adapter, View view, int position) {
                    TaskInfoBean item = listAdapter.getItem(position);
                    long taskId = item.getBean_id();

                    queryTaskMemberList(item, 2, new BaseSubscriber(mActivity) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            ToastUtils.showError(mActivity, e.getMessage());
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();

                            //正在进行的项目
                            boolean projectIsRunning = "0".equals(mActivity.projectStatus);
                            boolean taskCompleteStatus = "1".equals(item.getComplete_status());
                            long projectId = item.getProject_id();
                            int type = 0;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id", item.getBean_id());
                            jsonObject.put("completeStatus", taskCompleteStatus ? 0 : 1);

                            if (item.getFrom() == 1) {
                                if (TextUtils.isEmpty(item.getTask_id())) {
                                    type = 0;
                                } else {
                                    type = 1;
                                }
                                new TaskModel().queryPersonalTaskRole(((RxAppCompatActivity) getActivity()), taskId, type, new ProgressSubscriber<PersonalTaskRoleResultBan>(getActivity()) {
                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                    }

                                    @Override
                                    public void onNext(PersonalTaskRoleResultBan personalTaskRoleResultBan) {
                                        super.onNext(personalTaskRoleResultBan);
                                        if ("0".equals(personalTaskRoleResultBan.getData().getRole())
                                                || "1".equals(personalTaskRoleResultBan.getData().getRole())) {
                                            //个人任务,task_id为空是主任务,不为空为子任务
                                            ProjectDialog.updatePersonalTaskStatus(!TextUtils.isEmpty(item.getTask_id()), mActivity,
                                                    viewDelegate.getRootView(), item.getBean_id(),
                                                    taskCompleteStatus, new ProgressSubscriber<BaseBean>(mActivity, 1) {
                                                        @Override
                                                        public void onNext(BaseBean baseBean) {
                                                            super.onNext(baseBean);
                                                            item.setComplete_status(taskCompleteStatus ? "0" : "1");
                                                            ((TaskItemAdapter) adapter).notifyItemChanged(position);
                                                            refreshColumn(listAdapter, subNodeId);
                                                        }

                                                        @Override
                                                        public void onError(Throwable e) {
                                                            super.onError(e);
                                                        }
                                                    });
                                        } else {
                                            ToastUtils.showToast(getActivity(), "没有权限");
                                        }
                                    }
                                });


                            } else {
                                //项目任务
                                if (TextUtils.isEmpty(item.getTask_id())) {
                                    type = 1;
                                } else {
                                    type = 2;
                                }
                                if (projectIsRunning) {
                                    new TaskModel().queryTaskCompleteAuth(mActivity, projectId, type, taskId,
                                            new ProgressSubscriber<QueryTaskCompleteAuthResultBean>(mActivity) {
                                                @Override
                                                public void onError(Throwable e) {
                                                    super.onError(e);
                                                }

                                                @Override
                                                public void onNext(QueryTaskCompleteAuthResultBean dataBean) {
                                                    super.onNext(dataBean);
                                                    if ("1".equals(dataBean.getData().getFinish_task_role())) {
                                                        //更新状态
                                                        ProjectDialog.updateTaskStatus(((RxAppCompatActivity) getActivity()), viewDelegate.getRootView(),
                                                                jsonObject, taskCompleteStatus, dataBean.getData().getProject_complete_status(), new ProgressSubscriber<BaseBean>(getActivity(), 1) {
                                                                    @Override
                                                                    public void onNext(BaseBean baseBean) {
                                                                        super.onNext(baseBean);
                                                                        item.setComplete_status(taskCompleteStatus ? "0" : "1");
                                                                        ((TaskItemAdapter) adapter).notifyItemChanged(position);
                                                                        refreshColumn(listAdapter, subNodeId);
                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable e) {
                                                                        super.onError(e);
                                                                    }
                                                                });
                                                    } else {
                                                        ToastUtils.showError(getActivity(), "没有权限");
                                                    }
                                                }
                                            });

                                } else {
                                    ToastUtils.showToast(getActivity(), "项目当前状态不支持该操作!");
                                }

                            }

                        }
                    });
                }

                @Override
                public void onItemLongClick(DragItemAdapter adapter, View view, int position) {

                }
            });

            viewDelegate.addPoint();
            loadCacheData(listAdapter, subNodeId);
            refreshColumn(listAdapter, subNodeId);
        }
    }

    /**
     * 加载缓存
     */
    private void loadCacheData(TaskItemAdapter listAdapter, Long subNodeId) {
        final String cacheData = CacheDataHelper.getCacheData(CacheDataHelper.PROJECT_TASK_LIST_CACHE_DATA, "task_data_list" + "_" + subNodeId);
        if (!TextUtils.isEmpty(cacheData)) {
            List<TaskInfoBean> cacheDataList = new Gson().fromJson(cacheData, new TypeToken<List<TaskInfoBean>>() {
            }.getType());
            if (cacheDataList != null && cacheDataList.size() > 0) {
                listAdapter.setItemList(cacheDataList);
            }
        }
    }

    /**
     * 获取子节点下的任务列表
     */
    private void refreshColumn(TaskItemAdapter listAdapter, long subNodeId) {
        //获取子节点下的任务列表
        model.queryWebList(mActivity, subNodeId, paramMap, new ProgressSubscriber<TaskListBean>(mActivity, false) {
            @Override
            public void onNext(TaskListBean baseBean) {
                super.onNext(baseBean);
                List<TaskInfoBean> dataList = baseBean.getData().getDataList();
                listAdapter.setItemList(dataList);
                CacheDataHelper.saveCacheData(CacheDataHelper.PROJECT_TASK_LIST_CACHE_DATA, "task_data_list" + "_" + subNodeId, JSONObject.toJSONString(dataList));
//                viewDelegate.mBoardView.finishLoad(currentListIndex);
//                viewDelegate.mBoardView.finishRefresh(currentListIndex);
            }
        });
    }

    /**
     * 获取 任务列表操作选项
     *
     * @param nodeBean 子节点
     * @return
     */
    private List<ActionSheetDialog.SheetItem> getActionSheetItem(NodeBean nodeBean) {


        List<ActionSheetDialog.SheetItem> sheetItemList = new ArrayList<>();
        if (!ProjectDetailActivity.IS_TEMPLETE_PROJECT) {
            if (ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 21) && !isTemplateGroup) {
                sheetItemList.add(new ActionSheetDialog.SheetItem("编辑任务列表", which -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(DATA_TAG1, nodeBean.getName());
                    bundle.putLong(ProjectConstants.PROJECT_ID, mActivity.projectId);
                    bundle.putLong(ProjectConstants.NODE_ID, nodeId);
                    bundle.putLong(ProjectConstants.SUBNODE_ID, nodeBean.getId());
                    CommonUtil.startActivtiyForResult(mActivity, EditTaskTempActivity.class, ProjectConstants.EDIT_TASK_TEMP_REQUEST_CODE + hashCode, bundle);
                }));
            }
        }


        if (ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 24)) {
            sheetItemList.add(new ActionSheetDialog.SheetItem("取消关联", which -> {
                TaskItemAdapter adapter = (TaskItemAdapter) viewDelegate.mBoardView.getRecyclerView(columnPosition).getAdapter();
                List<TaskInfoBean> itemList = adapter.getItemList();
                ArrayList<TaskInfoBean> list = new ArrayList<TaskInfoBean>();
                for (int i = 0; i < itemList.size(); i++) {
                    if ("1".equals(itemList.get(i).getQuote_status())) {
                        list.add(itemList.get(i));
                    }
                }
                if (list.size() == 0) {
                    ToastUtils.showToast(getActivity(), "当前任务列表无关联任务");
                    return;
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.DATA_TAG1, list);
                    CommonUtil.startActivtiyForResult(mActivity, EditTaskLinkActivity.class, ProjectConstants.CANCEL_TASK_RELEVANT_REQUEST_CODE + hashCode, bundle);
                }
            }));
        }

        if (!ProjectDetailActivity.IS_TEMPLETE_PROJECT) {
            if (ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 23) && !isTemplateGroup) {
                sheetItemList.add(new ActionSheetDialog.SheetItem("删除", ActionSheetDialog.SheetItemColor.Red, which -> {

                    SpannableStringBuilder ssb = new SpannableStringBuilder("确定要删除列表 “" + nodeBean.getName() + "” 吗？删除后该列表下的所有任务将同时被删除。");
                    ssb.setSpan(new ForegroundColorSpan(ColorUtils.hexToColor("#0F0F0F")), 8, 8 + nodeBean.getName().length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    DialogUtils.getInstance().inputDialog(mActivity, getString(R.string.del), ssb, "请输入要删除的列表名称", viewDelegate.getRootView(), content -> {
                        if (TextUtil.isEmpty(content)) {
                            ToastUtils.showError(mActivity, "请输入列表名称");
                            return false;
                        }
                        if (!nodeBean.getName().equals(content)) {
                            ToastUtils.showError(mActivity, "列表名称输入错误！");
                            return false;
                        }

                        delTaskTemp(nodeBean.getId());
                        return true;
                    });

                }));
            }
        }

        /*if (sheetItemList.size() > 0) {
            for (int i = 0; i < viewDelegate.mBoardView.mHeaders.size(); i++) {
                viewDelegate.mBoardView.mHeaders.get(i).findViewById(R.id.iv_task_menu).setVisibility(View.VISIBLE);
            }
        }*/
        return sheetItemList;
    }

    /**
     * 删除任务列表
     *
     * @param id
     */
    private void delTaskTemp(long id) {
        model.deleteSubNode(mActivity, mActivity.projectId, nodeId, id, new ProgressSubscriber<BaseBean>(mActivity) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mActivity, "删除任务列表成功");
                delTaskList();
            }
        });
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        //任务列表、任务移动权限
        RxManager.$(mActivity.hashCode()).onSticky(ProjectConstants.PROJECT_ROLE_TAG, s -> {
            if (isFourLayer) {
                return;
            }
            if (ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 22)) {
                viewDelegate.mBoardView.setColumnDragEnabled(true);
            }
            if (ProjectUtil.INSTANCE.checkProjectPermission(mActivity, mActivity.priviledgeIds, 15)) {
                viewDelegate.mBoardView.setDragEnabled(true);
            }
        });
        viewDelegate.setOnClickListener(v -> {
            List<String> list = new ArrayList<>();
            Observable.from(subNodeList).subscribe(subNode -> list.add(subNode.getName()));

            ProjectDialog.showSortWindow(mActivity,
                    viewDelegate.get(R.id.ll_sub_list),
                    list,
                    taskListIndex,
                    position -> {
                        //选择未改变不做处理
                        if (taskListIndex == position) {
                            return true;
                        }
                        taskListIndex = position;
                        NodeBean nodeBean = subNodeList.get(position);
                        viewDelegate.setText(R.id.tv_sub_list_name, nodeBean.getName());
                        viewDelegate.mBoardView.clearBoard();

                        taskList = nodeBean.getSubnodeArr();
                        initTaskList();
                        return true;
                    });
        }, R.id.ll_sub_list);

        viewDelegate.mBoardView.setBoardListener(new TaskBoardView.BoardListener() {
            @Override
            public void onItemDragStarted(int column, int row) {
                oldColumn = column;
                oldRow = row;
//                Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
                if (fromColumn != toColumn || fromRow != toRow) {
                    //Toast.makeText(getContext(), "End - column: " + toColumn + " row: " + toRow, Toast.LENGTH_SHORT).show();
                }
                JSONObject jsonObject = new JSONObject();
                NodeBean nodeBean = subNodeList.get(toColumn);
                jsonObject.put("toSubnodeId", nodeBean.getId());
                DragItemAdapter adapter = viewDelegate.mBoardView.getAdapter(toColumn);
                List<TaskInfoBean> itemList = adapter.getItemList();
                TaskInfoBean taskInfoBean = itemList.get(toRow);
                jsonObject.put("taskInfoId", taskInfoBean.getBean_id());

                jsonObject.put("dataList", itemList);
                if (fromColumn == toColumn) {
                    if (fromRow != toRow) {
                        model.sortTask(mActivity, jsonObject, new ProgressSubscriber<BaseBean>(mActivity) {
                            @Override
                            public void onError(Throwable e) {
                                dismissWindowView();
                                e.printStackTrace();
                                ToastUtils.showError(mActivity, R.string.project_task_move_failed);
                            }
                        });
                    }
                } else {
                    NodeBean fromBean = subNodeList.get(fromColumn);
                    jsonObject.put("originalNodeId", fromBean.getId());
                    model.sortTask(mActivity, jsonObject, new ProgressSubscriber<BaseBean>(mActivity) {
                        @Override
                        public void onError(Throwable e) {
                            dismissWindowView();
                            e.printStackTrace();
                            ToastUtils.showError(mActivity, R.string.project_task_move_failed);
                        }
                    });
                }
            }

            @Override
            public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                // Toast.makeText(getContext(), "Position changed - column: " + newColumn + " row: " + newRow, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {
//                TextView itemCount1 = mBoardView.getHeaderView(oldColumn).findViewById(R.id.item_count);
//                itemCount1.setText(String.valueOf(mBoardView.getAdapter(oldColumn).getItemCount()));
//                TextView itemCount2 = mBoardView.getHeaderView(newColumn).findViewById(R.id.item_count);
//                itemCount2.setText(String.valueOf(mBoardView.getAdapter(newColumn).getItemCount()));
            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {
                //  Toast.makeText(getContext(), "Focused column changed from " + oldColumn + " to " + newColumn, Toast.LENGTH_SHORT).show();
                viewDelegate.setPointIndex(newColumn);
            }

            @Override
            public void onColumnDragStarted(int position) {
                oldTempIndex = position;
                //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragChangedPosition(int oldPosition, int newPosition) {
                // Toast.makeText(getContext(), "Column changed from " + oldPosition + " to " + newPosition, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragEnded(int position) {
                //  Toast.makeText(getContext(), "Column drag ended at " + position, Toast.LENGTH_SHORT).show();
                if (oldTempIndex == position) {
                    return;
                }

                CollectionUtils.move(subNodeList, oldTempIndex, position);

                SortNodeRequestBean bean = new SortNodeRequestBean();
                bean.setProjectId(mActivity.projectId);
                bean.setToNodeId(nodeId);
                bean.setActiveNodeId(nodeId);
                bean.setOriginalNodeId(nodeId);
                bean.setDataList(subNodeList);
                model.sortSubNode(mActivity, bean, new ProgressSubscriber<BaseBean>(mActivity) {
                    @Override
                    public void onError(Throwable e) {
                        dismissWindowView();
                        e.printStackTrace();
                        ToastUtils.showError(mActivity, R.string.project_task_list_move_failed);
                    }
                });
            }
        });
       /* viewDelegate.mBoardView.setOnRefreshOrLoadListener(new OnRefreshOrLoadListener() {

            @Override
            public void onRefresh(int index) {
                currentListIndex = index;
                refreshColumn(adapterList.get(index), subNodeIdList.get(index));
            }

            @Override
            public void onLoad(int index) {
                currentListIndex = index;
                refreshColumn(adapterList.get(index), subNodeIdList.get(index));
            }
        });*/
       /* viewDelegate.mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                viewDelegate.mSmartRefreshLayout.finishLoadMore(true);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                viewDelegate.mSmartRefreshLayout.finishRefresh(500);

            }
        });*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //ProjectConstants.CANCEL_TASK_RELEVANT_REQUEST_CODE + hashCode
        if (requestCode == ProjectConstants.PROJECT_SUB_NODE_ADD_TAG) {
            //子节点新增
            addTaskList();
        } else if (requestCode == ProjectConstants.CANCEL_TASK_RELEVANT_REQUEST_CODE + hashCode) {
            //取消任务关联
            TaskItemAdapter adapter = (TaskItemAdapter) viewDelegate.mBoardView.getRecyclerView(columnPosition).getAdapter();
            final Long subNodeId = subNodeIdList.get(columnPosition);
            refreshColumn(adapter, subNodeId);
        } else if (requestCode == ProjectConstants.EDIT_TASK_TEMP_REQUEST_CODE + hashCode) {
            //编辑任务列表
            NodeBean nodeBean = (NodeBean) data.getSerializableExtra(DATA_TAG1);
            for (int i = 0; i < subNodeList.size(); i++) {
                NodeBean subNode = subNodeList.get(i);
                if (subNode.getId() == nodeBean.getId()) {
                    subNode.setName(nodeBean.getName());
                    TextView tvTaskTempName = viewDelegate.mBoardView.getHeaderView(i).findViewById(R.id.tv_task_temp_name);
                    TextUtil.setText(tvTaskTempName, subNode.getName());
                    break;
                }
                final List<NodeBean> subnodeArr = subNode.getSubnodeArr();
                if (subnodeArr != null & subnodeArr.size() > 0) {
                    for (int j = 0; j < subnodeArr.size(); j++) {
                        NodeBean node = subnodeArr.get(j);
                        if (node.getId() == nodeBean.getId()) {
                            node.setName(nodeBean.getName());
                            TextView tvTaskTempName = viewDelegate.mBoardView.getHeaderView(j).findViewById(R.id.tv_task_temp_name);
                            TextUtil.setText(tvTaskTempName, node.getName());
                            break;
                        }
                    }
                }
            }
        } else if (requestCode == ProjectConstants.ADD_TASK_REQUEST_CODE + hashCode) {
            //新增
            AppModuleBean appModeluBean = (AppModuleBean) data.getSerializableExtra(DATA_TAG1);
            Bundle bundle = new Bundle();
            String moduleBean = appModeluBean.getEnglish_name();
            switch (moduleBean) {
                case ProjectConstants.TASK_MODULE_BEAN:
                    //任务
                    bundle.putString(Constants.MODULE_BEAN, ProjectConstants.PROJECT_TASK_MOBULE_BEAN + mActivity.projectId);
                    bundle.putLong(ProjectConstants.PROJECT_ID, mActivity.projectId);
                    CommonUtil.startActivtiyForResult(mActivity, AddTaskActivity.class, ProjectConstants.ADD_TASK_TASK_REQUEST_CODE + hashCode, bundle);
                    break;
                case MemoConstant.BEAN_NAME:
                    //备忘录
                    UIRouter.getInstance().openUri(mActivity, "DDComp://memo/add", bundle, ProjectConstants.ADD_TASK_MEMO_REQUEST_CODE + hashCode);
                    break;
                default:
                    if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(appModeluBean.getApplication_id())) {
                        //审批下面的模块
                        bundle.putString(Constants.MODULE_BEAN, moduleBean);
                        UIRouter.getInstance().openUri(mActivity, "DDComp://custom/add", bundle, ProjectConstants.ADD_TASK_APPROVE_REQUEST_CODE + hashCode);
                    } else {
                        //自定义
                        bundle.putString(Constants.MODULE_BEAN, moduleBean);
                        UIRouter.getInstance().openUri(mActivity, "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE + hashCode);
                    }
                    break;
            }
        } else if (requestCode == ProjectConstants.ADD_TASK_TASK_REQUEST_CODE + hashCode) {
            //新增任务
            long id = data.getLongExtra(DATA_TAG1, 0);
            AddTaskRequestBean bean = new AddTaskRequestBean();
            bean.setProjectId(mActivity.projectId);
            bean.setBean(ProjectConstants.PROJECT_TASK_MOBULE_BEAN + mActivity.projectId);
            bean.setDataId(id);
            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            bean.setSubnodeId(subNodeId);
            bean.setCheckMember(data.getStringExtra(Constants.DATA_TAG3));
            bean.setCheckStatus(data.getStringExtra(Constants.DATA_TAG2));
            bean.setAssociatesStatus(data.getStringExtra(Constants.DATA_TAG4));
            bean.setEndTime(data.getStringExtra(Constants.DATA_TAG5));
            bean.setExecutorId(data.getStringExtra(Constants.DATA_TAG6));
            bean.setStartTime(data.getStringExtra(Constants.DATA_TAG7));
            bean.setTaskName(data.getStringExtra(Constants.DATA_TAG8));
            bean.setPicklist_tag(data.getStringExtra(Constants.DATA_TAG9));

            addTask(bean, subNodeId);
        } else if (requestCode == CustomConstants.REQUEST_ADDCUSTOM_CODE + hashCode) {
            //新增自定义模块
            ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(DATA_TAG1);
            AddTaskRequestBean bean = new AddTaskRequestBean();
            bean.setProjectId(mActivity.projectId);
            bean.setBean(moduleBean.getBean());
            bean.setDataId(moduleBean.getDataId());
            bean.setModuleId(moduleBean.getModuleId());
            bean.setModuleName(moduleBean.getModuleName());
            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            bean.setSubnodeId(subNodeId);
            addTask(bean, subNodeId);
        } else if (requestCode == ProjectConstants.ADD_TASK_MEMO_REQUEST_CODE + hashCode) {
            //新增备忘录
            long dataId = data.getLongExtra(Constants.DATA_TAG1, 0);
            AddTaskRequestBean bean = new AddTaskRequestBean();
            bean.setProjectId(mActivity.projectId);
            bean.setBean(MemoConstant.BEAN_NAME);
            bean.setDataId(dataId);

            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            bean.setSubnodeId(subNodeId);
            addTask(bean, subNodeId);
        } else if (requestCode == ProjectConstants.ADD_TASK_APPROVE_REQUEST_CODE + hashCode) {
            //新增审批
            ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(DATA_TAG1);
            AddTaskRequestBean bean = new AddTaskRequestBean();
            bean.setProjectId(mActivity.projectId);
            bean.setBean(moduleBean.getBean());
            bean.setDataId(moduleBean.getDataId());
            bean.setModuleId(moduleBean.getModuleId());
            bean.setModuleName(moduleBean.getModuleName());

            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            bean.setSubnodeId(subNodeId);
            addTask(bean, subNodeId);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_REQUEST_CODE + hashCode) {
            //引用
            AppModuleBean appModeluBean = (AppModuleBean) data.getSerializableExtra(DATA_TAG1);

            String moduleBean = appModeluBean.getEnglish_name();

            Bundle bundle = new Bundle();
            switch (moduleBean) {
                case ProjectConstants.TASK_MODULE_BEAN:
                    //任务
                    bundle.putLong(ProjectConstants.PROJECT_ID, mActivity.projectId);
                    CommonUtil.startActivtiyForResult(mActivity, QuoteTaskActivity.class, ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE + hashCode, bundle);
                    break;
                case MemoConstant.BEAN_NAME:
                    UIRouter.getInstance().openUri(mActivity, "DDComp://memo/choose_memo", bundle, ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE + hashCode);
                    break;
                default:
                    if (ApproveConstants.APPROVAL_MODULE_BEAN.equals(appModeluBean.getApplication_id())) {
                        //审批下面的模块
                        bundle.putString(Constants.DATA_TAG1, moduleBean);
                        bundle.putString(Constants.DATA_TAG2, appModeluBean.getChinese_name());
                        bundle.putString(Constants.DATA_TAG3, appModeluBean.getId());
                        UIRouter.getInstance().openUri(mActivity, "DDComp://app/approve/select", bundle, ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE + hashCode);
                    } else {
                        //自定义模块
                        bundle.putString(Constants.MODULE_BEAN, moduleBean);
                        bundle.putString(Constants.MODULE_ID, appModeluBean.getId());
                        bundle.putString(Constants.NAME, appModeluBean.getChinese_name());
                        UIRouter.getInstance().openUri(mActivity, "DDComp://custom/select", bundle, ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE + hashCode);
                    }
                    break;
            }
        } else if (requestCode == ProjectConstants.QUOTE_TASK_MEMO_REQUEST_CODE + hashCode) {
            //引用备忘录
            ArrayList<MemoListItemBean> choosedItem = (ArrayList<MemoListItemBean>) data.getSerializableExtra(DATA_TAG1);
            if (CollectionUtils.isEmpty(choosedItem)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (MemoListItemBean memo : choosedItem) {
                sb.append("," + memo.getId());
            }
            sb.deleteCharAt(0);

            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("subnodeId", subNodeId);
            jsonObject.put("bean", MemoConstant.BEAN_NAME);
            jsonObject.put("bean_type", 1);
            jsonObject.put("quoteTaskId", sb);
            jsonObject.put("projectId", mActivity.projectId);

            quoteTask(jsonObject, subNodeId);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_APPROVE_REQUEST_CODE + hashCode) {
            //引用审批
            ArrayList<ApproveListBean> datas = (ArrayList<ApproveListBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            String moduleName = data.getStringExtra(Constants.DATA_TAG2);
            String moduleId = data.getStringExtra(Constants.DATA_TAG3);
            String moduleBean = data.getStringExtra(Constants.DATA_TAG4);
            if (CollectionUtils.isEmpty(datas)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (ApproveListBean approve : datas) {
                sb.append("," + approve.getApproval_data_id());
            }
            sb.deleteCharAt(0);
            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("subnodeId", subNodeId);
            jsonObject.put("moduleName", moduleName);
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("bean", moduleBean);
            jsonObject.put("bean_type", 4);
            jsonObject.put("quoteTaskId", sb);
            jsonObject.put("projectId", mActivity.projectId);
            quoteTask(jsonObject, subNodeId);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_TASK_REQUEST_CODE + hashCode) {
            //引用任务
            String ids = data.getStringExtra(Constants.DATA_TAG1);
            String beanName = data.getStringExtra(Constants.DATA_TAG2);
            int taskType = data.getIntExtra(Constants.DATA_TAG3, -1);
            int beanType = -1;
            if (1 == taskType) {
                beanType = 5;
            } else if (0 == taskType) {
                beanType = 2;
            }
            if (beanType == -1) {
                ToastUtils.showError(getActivity(), "任务类型错误");
            }
            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("subnodeId", subNodeId);
            //bean_type  个人任务5 项目任务2
            jsonObject.put("bean_type", beanType);
            jsonObject.put("bean", beanName + "_" + mActivity.projectId);
            jsonObject.put("quoteTaskId", ids);
            jsonObject.put("projectId", mActivity.projectId);
            quoteTask(jsonObject, subNodeId);
        } else if (requestCode == ProjectConstants.QUOTE_TASK_CUSTOM_REQUEST_CODE + hashCode) {
            //引用自定义
            String moduleBean = data.getStringExtra(Constants.MODULE_BEAN);
            ArrayList<DataTempResultBean.DataBean.DataListBean> datas = (ArrayList<DataTempResultBean.DataBean.DataListBean>) data.getSerializableExtra(DATA_TAG1);
            String moduleName = data.getStringExtra(Constants.NAME);
            String moduleId = data.getStringExtra(Constants.MODULE_ID);
            if (CollectionUtils.isEmpty(datas)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (DataTempResultBean.DataBean.DataListBean dataListBean : datas) {
                sb.append("," + dataListBean.getId().getValue());
            }
            sb.deleteCharAt(0);

            long subNodeId = 0L;
            if (isFourLayer) {
                subNodeId = subNodeList.get(currentListIndex).getSubnodeArr().get(columnPosition).getId();
            } else {
                subNodeId = subNodeList.get(columnPosition).getId();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("subnodeId", subNodeId);
            jsonObject.put("bean", moduleBean);
            jsonObject.put("moduleId", moduleId);
            jsonObject.put("moduleName", moduleName);
            jsonObject.put("bean_type", 3);
            jsonObject.put("quoteTaskId", sb);
            jsonObject.put("projectId", mActivity.projectId);
            quoteTask(jsonObject, subNodeId);
        }
    }

    /**
     * 新增任务列表
     */
    private void addTaskList() {
        model.getSubNode(mActivity, nodeId, new ProgressSubscriber<AllNodeResultBean>(mActivity) {
            @Override
            public void onNext(AllNodeResultBean allNodeResultBean) {
                super.onNext(allNodeResultBean);
                ArrayList<NodeBean> dataList = allNodeResultBean.getData().getDataList();
                Iterator<NodeBean> iterator = dataList.iterator();
                while (iterator.hasNext()) {
                    NodeBean next = iterator.next();
                    for (NodeBean sub : subNodeList) {
                        if (sub.getId() == next.getId()) {
                            iterator.remove();
                            break;
                        }
                    }
                }
                CollectionUtils.addAll(subNodeList, dataList);
                loadTaskList(dataList);
            }
        });
    }

    /**
     * 删除任务列表
     */
    private void delTaskList() {
        model.getSubNode(mActivity, nodeId, new ProgressSubscriber<AllNodeResultBean>(mActivity) {
            @Override
            public void onNext(AllNodeResultBean allNodeResultBean) {
                super.onNext(allNodeResultBean);
                ArrayList<NodeBean> dataList = allNodeResultBean.getData().getDataList();
                Iterator<NodeBean> iterator = subNodeList.iterator();
                int column = 0;
                while (iterator.hasNext()) {
                    NodeBean next = iterator.next();
                    boolean bl = false;
                    for (NodeBean note : dataList) {
                        if (note.getId() == next.getId()) {
                            bl = true;
                            break;
                        }
                    }
                    if (!bl) {
                        iterator.remove();
                        viewDelegate.mBoardView.removeColumn(column);
                    }
                    column++;
                }
            }
        });
    }


    /**
     * 引用任务
     *
     * @param jsonObject
     * @param subNodeId
     */
    private void quoteTask(JSONObject jsonObject, long subNodeId) {
        model.quoteTask(mActivity, jsonObject, new ProgressSubscriber<BaseBean>(mActivity) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mActivity, "引用成功!");
                TaskItemAdapter adapter = (TaskItemAdapter) viewDelegate.mBoardView.getRecyclerView(columnPosition).getAdapter();
                refreshColumn(adapter, subNodeId);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    /**
     * 新增任务
     *
     * @param bean
     */
    private void addTask(AddTaskRequestBean bean, long subNodeId) {
        model.addTask(mActivity, bean, new ProgressSubscriber<BaseBean>(mActivity) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                ToastUtils.showSuccess(mActivity, "新增成功!");
                TaskItemAdapter adapter = (TaskItemAdapter) viewDelegate.mBoardView.getRecyclerView(columnPosition).getAdapter();
                refreshColumn(adapter, subNodeId);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    /**
     * 获取任务 全部角色的 信息
     */
    private void queryTaskMemberList(TaskInfoBean item, int authCode, Subscriber s) {
        new TaskModel().queryTaskRoles(mActivity, mActivity.projectId, item.getBean_id(), item.getTask_id() == null ? 1 : 2, (taskMemberListResultBean, queryTaskAuthResultBean) -> {
            TaskMemberListResultBean.DataBean taskMembersBean = taskMemberListResultBean.getData();
            List<QueryTaskAuthResultBean.DataBean> authList = queryTaskAuthResultBean.getData();
            if (taskMembersBean == null) {
                return queryTaskAuthResultBean;
            }
            if (authList == null) {
                return queryTaskAuthResultBean;
            }
            List<TaskMemberListResultBean.DataBean.DataListBean> taskMembers = taskMembersBean.getDataList();
            if (!CollectionUtils.isEmpty(taskMembers)) {
                Observable.from(taskMembers)
                        .subscribe(member -> Observable.from(authList)
                                .filter(auth -> auth.getRole_type().equals(member.getProject_task_role()))
                                .subscribe(auth -> auth.setCheck(true)));
            }
            return queryTaskAuthResultBean;
        }, new ProgressSubscriber<QueryTaskAuthResultBean>(mActivity) {
            @Override
            public void onNext(QueryTaskAuthResultBean queryTaskAuthResultBean) {
                super.onNext(queryTaskAuthResultBean);
                List<QueryTaskAuthResultBean.DataBean> authList = queryTaskAuthResultBean.getData();
                for (QueryTaskAuthResultBean.DataBean auth : authList) {
                    if (auth.isCheck()) {
                        Class<QueryTaskAuthResultBean.DataBean> clz = QueryTaskAuthResultBean.DataBean.class;
                        try {
                            Method mt = clz.getMethod("getAuth_" + authCode);
                            if ("1".equals(mt.invoke(auth))) {
                                s.onCompleted();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                s.onError(new Throwable("有没此权限！"));
            }

            @Override
            public void onError(Throwable e) {
                dismissWindowView();
                e.printStackTrace();
                s.onError(new Throwable("权限请求失败！"));
            }
        });
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean event) {
        if (event.getCode() == ProjectConstants.PROJECT_TASK_REFRESH_CODE && !TextUtils.isEmpty(event.getTag())) {
            for (int i = 0; i < taskList.size(); i++) {
                TaskItemAdapter adapter = (TaskItemAdapter) viewDelegate.mBoardView.getAdapter(i);
                List<TaskInfoBean> itemList = adapter.getItemList();
                int finalI = i;
                Observable.from(itemList).filter(item -> event.getTag().equals(item.getBean_id() + "")).subscribe(item -> {
                    refreshColumn(adapter, taskList.get(finalI).getId());
                });
            }
        }
        if (ProjectConstants.PROJECT_STATUS_CHANGE.equals(event.getTag())) {
            boolean flag = (boolean) event.getObject();
            if (adapterList != null && adapterList.size() > 0) {
                for (TaskItemAdapter adapter : adapterList) {
                    adapter.setDragOnLongPress(flag);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        //筛选
        if (ProjectConstants.PROJECT_TASK_FILTER_CODE == event.getCode()) {
            paramMap.clear();
            final Object object = event.getObject();
            if (object != null && object instanceof Map) {
                final Map<String, Object> map = (Map<String, Object>) object;
                String bean = map.get("bean") + "";
                map.remove("bean");
                paramMap.put("bean", bean);
                final Object queryType1 = map.get("queryType");
                map.remove("queryType");
                if (queryType1 != null && TextUtils.isEmpty(queryType1 + "")) {
                    String queryType = queryType1 + "";
                    if (queryType != null && !TextUtils.isEmpty(queryType)) {
                        paramMap.put("queryType", TextUtil.parseInt(queryType));
                    }
                }

            }
            String sortField = event.getTag();
            paramMap.put("sortField", sortField);
            paramMap.put("queryWhere", object);
            refreshAllColumn();
        }
        if (ProjectConstants.PROJECT_TASK_FILTER_RESET_CODE == event.getCode()) {
            paramMap.clear();
            refreshAllColumn();
        }
    }

    private void refreshAllColumn() {
        for (int i = 0; i < taskList.size(); i++) {
            refreshColumn(adapterList.get(i), subNodeIdList.get(i));
        }
    }

}

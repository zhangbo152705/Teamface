package com.hjhq.teamface.oa.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.BaseFragment;
import com.hjhq.teamface.basis.bean.AppModuleBean;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.ImMessage;
import com.hjhq.teamface.basis.bean.LocalModuleBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.bean.PersonalTaskRoleResultBan;
import com.hjhq.teamface.basis.bean.ProjectInfoBean;
import com.hjhq.teamface.basis.bean.QueryTaskCompleteAuthResultBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.bean.TimeWorkbenchResultBean;
import com.hjhq.teamface.basis.bean.WidgetListBean;
import com.hjhq.teamface.basis.bean.WorkbenchAuthBean;
import com.hjhq.teamface.basis.bean.WorkbenchMemberBean;
import com.hjhq.teamface.basis.constants.AppConstant;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.common.activity.ChooseRangeMemberActivity;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.adapter.WidgetAdapter;
import com.hjhq.teamface.common.bean.WidgetBean;
import com.hjhq.teamface.common.ui.ManageWidgetActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;
import com.hjhq.teamface.im.activity.GroupMemberActivity;
import com.hjhq.teamface.oa.approve.ui.ApproveActivity;
import com.hjhq.teamface.oa.main.logic.MainLogic;
import com.hjhq.teamface.util.TaskStatusHelper;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作台
 *
 * @author lx
 * @date 2017/3/16
 */

public class WorkbenchFragmentV3 extends BaseFragment {

    RelativeLayout mRlRoot;
    TextView tvCompanyName;
    RelativeLayout rlSwitchCom;
    RelativeLayout mRlType1;
    RelativeLayout mRlType2;
    RelativeLayout mRlType3;
    RelativeLayout mRlType4;
    TextView mTvNum1;
    TextView mTvNum2;
    TextView mTvNum3;
    TextView mTvNum4;
    TextView mTvTotalNum;
    TextView mTvCuttentMember;
    ImageView mIvCuttentMember;
    ImageView mAddWidget;
    ImageView mIvSwitch;
    RelativeLayout mRlSwitch;
    TextView mTvChinese;
    TextView mTvEnglish;
    TextView mTvHeaderNum;
    SmartRefreshLayout mRefreshLayout;
    SmartRefreshLayout mRefreshLayoutAll;
    FrameLayout mFl1;
    RelativeLayout mRlHeader;
    RecyclerView mRvType1;
    RecyclerView mRvType2;
    RecyclerView mRvType3;
    RecyclerView mRvType4;
    RecyclerView mRvWidget;
    ScrollView mRootScrollView;

    WidgetAdapter mWidgetAdapter;
    TaskItemAdapter mTaskItemAdapter1;
    TaskItemAdapter mTaskItemAdapter2;
    TaskItemAdapter mTaskItemAdapter3;
    TaskItemAdapter mTaskItemAdapter4;
    ImageView mEmptyView;
    LinearLayout workbench_task_li;
    View widget_top_bg;
    View widget_top;
    public int openedListIndex = 100;
    List<TaskItemAdapter> adapterList = new ArrayList<>();
    private List<WidgetBean> widgetList = new ArrayList<>();
    ArrayList<AppModuleBean> rawWidgetData = new ArrayList<>();
    List<RecyclerView> rvList = new ArrayList<>();
    List<TextView> tvList = new ArrayList<>();
    //分页
    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    //总数
    private int totalNum = 0;
    private int pageSize = 20;
    private Map<Integer, PageInfo> pageInfoList = new HashMap<>();
    private Map<String, List<List<AppModuleBean>>> moduleData = new HashMap<>();

    /**
     * 我的应用里每页模块的数量
     */
    public static final int MY_APP_MODULE_COUNT = 9;
    private String memberIds = "";


    private String[] chineseTitleArr = new String[]{"超期任务", "今日任务", "明日任务", "以后任务"};
    private String[] englishTitleArr = new String[]{"Overdue", "Today", "Tomorrow", "Later"};
    private int[] numArr = new int[4];
    private int[] titleBgArr = {R.drawable.workbench_list_header_bg1, R.drawable.workbench_list_header_bg2,
            R.drawable.workbench_list_header_bg3, R.drawable.workbench_list_header_bg4};
    //已选中人员
    ArrayList<Member> choosedMembers = new ArrayList<>();

    private boolean isRefresh = false;
    private boolean hasSwitchAuth = false;
    private int completeNum = 0;

    @Override
    protected int getContentView() {
        return R.layout.fragment_workbench_v3;
    }


    @Override
    protected void initView(View view) {
        findViews();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        mRlRoot.setPadding(0, 0, 0, 0);
        initAdapter();
        memberIds = "";
        ImageLoader.loadCircleImage(getActivity(), SPHelper.getUserAvatar(),
                mIvCuttentMember, SPHelper.getUserName());
        TextUtil.setText(mTvCuttentMember, SPHelper.getUserName());
        {
            //禁用懒加载,懒加载会导致程序从后台恢复时数据不正常的问题
            initData();
            firstLoad = false;
        }
        if (getActivity() instanceof MainActivity) {
            if (((MainActivity) getActivity()).getAppModuleBean() != null) {
                final AppModuleBean appModuleBean = ((MainActivity) getActivity()).getAppModuleBean();
                openModule(appModuleBean, appModuleBean.getId());
            }
        }

    }

    /**
     * 初始化应用列表参数及点击事件
     */
    private void initAdapter() {
        mRvType1.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvType2.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvType3.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvType4.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvWidget.setLayoutManager(new LinearLayoutManager(getActivity()) {

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mTaskItemAdapter1 = new TaskItemAdapter(new ArrayList<>(), false);
        mTaskItemAdapter2 = new TaskItemAdapter(new ArrayList<>(), false);
        mTaskItemAdapter3 = new TaskItemAdapter(new ArrayList<>(), false);
        mTaskItemAdapter4 = new TaskItemAdapter(new ArrayList<>(), false);

        adapterList.add(mTaskItemAdapter1);
        adapterList.add(mTaskItemAdapter2);
        adapterList.add(mTaskItemAdapter3);
        adapterList.add(mTaskItemAdapter4);
        rvList.add(mRvType1);
        rvList.add(mRvType2);
        rvList.add(mRvType3);
        rvList.add(mRvType4);
        mRvType1.setAdapter(mTaskItemAdapter1);
        mRvType2.setAdapter(mTaskItemAdapter2);
        mRvType3.setAdapter(mTaskItemAdapter3);
        mRvType4.setAdapter(mTaskItemAdapter4);
        tvList.add(mTvNum1);
        tvList.add(mTvNum2);
        tvList.add(mTvNum3);
        tvList.add(mTvNum4);
        TaskItemAdapter.OnItemClickListener mListener = new TaskItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DragItemAdapter adapter, View view, int position) {
                if (openedListIndex < 0 || openedListIndex > adapterList.size() - 1) {
                    return;
                }
                TaskInfoBean infoBean = adapterList.get(openedListIndex).getItem(position);
                TaskHelper.INSTANCE.clickTaskItem(getActivity(), infoBean);
            }

            @Override
            public void onItemChildClick(DragItemAdapter adapter, View view, int position) {
                TaskInfoBean item = ((TaskItemAdapter) adapter).getItem(position);
                long project_id = item.getProject_id();
                int dataType = item.getDataType();
                long taskId = item.getBean_id();
                //1子任务,0主任务
                int type = 0;

                boolean taskCompleteStatus = "1".equals(item.getComplete_status());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", item.getBean_id());
                jsonObject.put("completeStatus", taskCompleteStatus ? 0 : 1);

                if (item.getFrom() == 1) {
                    if (TextUtils.isEmpty(item.getTask_id())) {
                        type = 0;
                    } else {
                        type = 1;
                    }
                    MainLogic.getInstance().queryPersonalTaskRole(((BaseActivity) getActivity()),
                            item.getBean_id(), type, new ProgressSubscriber<PersonalTaskRoleResultBan>(getActivity(), false) {
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
                                        TaskStatusHelper.updatePersonalTaskStatus(!TextUtils.isEmpty(item.getTask_id()), ((BaseActivity) getActivity()),
                                                mFl1, item.getBean_id(),
                                                taskCompleteStatus, new ProgressSubscriber<BaseBean>(getActivity(), false) {
                                                    @Override
                                                    public void onNext(BaseBean baseBean) {
                                                        super.onNext(baseBean);
                                                        /*item.setComplete_status(taskCompleteStatus ? "0" : "1");
                                                        ((TaskItemAdapter) adapter).notifyItemChanged(position);*/
                                                        refreshDataAll();
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        super.onError(e);
                                                    }
                                                });

                                    } else {
                                        ToastUtils.showError(getActivity(), "无权限");
                                    }
                                }
                            });


                } else {
                    if (TextUtils.isEmpty(item.getTask_id())) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                    MainLogic.getInstance().getProjectSettingDetail(((BaseActivity) getActivity()),
                            project_id, new ProgressSubscriber<ProjectInfoBean>(getActivity(), false) {
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                }

                                @Override
                                public void onNext(ProjectInfoBean projectInfoBean) {
                                    super.onNext(projectInfoBean);
                                    if ("0".equals(projectInfoBean.getData().getProject_status())) {
                                        MainLogic.getInstance().queryTaskCompleteAuth(((BaseActivity) getActivity()),
                                                project_id, taskId, type, new ProgressSubscriber<QueryTaskCompleteAuthResultBean>(getActivity(), false) {
                                                    @Override
                                                    public void onError(Throwable e) {
                                                        super.onError(e);
                                                    }

                                                    @Override
                                                    public void onNext(QueryTaskCompleteAuthResultBean queryTaskCompleteAuthResultBean) {
                                                        super.onNext(queryTaskCompleteAuthResultBean);
                                                        if ("1".equals(queryTaskCompleteAuthResultBean.getData().getFinish_task_role())) {
                                                            //更新状态
                                                            TaskStatusHelper.updateTaskStatus(((BaseActivity) getActivity()), mFl1,
                                                                    jsonObject, taskCompleteStatus, queryTaskCompleteAuthResultBean.getData().getProject_complete_status(), new ProgressSubscriber<BaseBean>(getActivity(), false) {
                                                                        @Override
                                                                        public void onNext(BaseBean baseBean) {
                                                                            super.onNext(baseBean);
                                                        /*item.setComplete_status(taskCompleteStatus ? "0" : "1");
                                                        ((TaskItemAdapter) adapter).notifyItemChanged(position);*/
                                                                            refreshDataAll();
                                                                        }

                                                                        @Override
                                                                        public void onError(Throwable e) {
                                                                            super.onError(e);
                                                                        }
                                                                    });
                                                        } else {
                                                            ToastUtils.showError(getActivity(), "无权限");
                                                        }
                                                    }
                                                });
                                    } else {
                                        ToastUtils.showToast(getActivity(), "项目当前状态不支持该操作!");
                                    }
                                }
                            });


                }
            }

            @Override
            public void onItemLongClick(DragItemAdapter adapter, View view, int position) {

            }
        };
        mTaskItemAdapter1.setOnItemClickListener(mListener);
        mTaskItemAdapter2.setOnItemClickListener(mListener);
        mTaskItemAdapter3.setOnItemClickListener(mListener);
        mTaskItemAdapter4.setOnItemClickListener(mListener);
        mWidgetAdapter = new WidgetAdapter(((MainActivity) getActivity()), widgetList);
        mRvWidget.setAdapter(mWidgetAdapter);

    }

    /**
     * 初始化view
     */
    private void findViews() {
        mEmptyView = getActivity().findViewById(R.id.iv_empty);
        mAddWidget = getActivity().findViewById(R.id.iv_add_widget);
        mRootScrollView = getActivity().findViewById(R.id.sv);
        mRlRoot = getActivity().findViewById(R.id.rl_workbeanch_3);
        tvCompanyName = getActivity().findViewById(R.id.tv_company_name);
        rlSwitchCom = getActivity().findViewById(R.id.ll_top_bar);
        mRlType1 = getActivity().findViewById(R.id.rl_sub1);
        mRlType2 = getActivity().findViewById(R.id.rl_sub2);
        mRlType3 = getActivity().findViewById(R.id.rl_sub3);
        mRlType4 = getActivity().findViewById(R.id.rl_sub4);
        mTvChinese = getActivity().findViewById(R.id.tv_title_chinese);
        mTvEnglish = getActivity().findViewById(R.id.tv_title_english);
        mTvHeaderNum = getActivity().findViewById(R.id.tv_num);
        mRefreshLayout = getActivity().findViewById(R.id.refresh_layout);
        mRefreshLayoutAll = getActivity().findViewById(R.id.refresh_all_3);
        mFl1 = getActivity().findViewById(R.id.fl_data);
        mRlHeader = getActivity().findViewById(R.id.rl_overdue);
        mRvType1 = getActivity().findViewById(R.id.rv1);
        mRvType2 = getActivity().findViewById(R.id.rv2);
        mRvType3 = getActivity().findViewById(R.id.rv3);
        mRvType4 = getActivity().findViewById(R.id.rv4);
        mRvWidget = getActivity().findViewById(R.id.rv_widget);
        mTvNum1 = getActivity().findViewById(R.id.tv13);
        mTvNum2 = getActivity().findViewById(R.id.tv23);
        mTvNum3 = getActivity().findViewById(R.id.tv33);
        mTvNum4 = getActivity().findViewById(R.id.tv43);
        mTvTotalNum = getActivity().findViewById(R.id.tv_total_num);
        mTvCuttentMember = getActivity().findViewById(R.id.tv_current_member);
        mIvCuttentMember = getActivity().findViewById(R.id.iv_current_member_avatar);
        mIvSwitch = getActivity().findViewById(R.id.iv_switch_member);
        mRlSwitch = getActivity().findViewById(R.id.rl_switch);
        workbench_task_li = getActivity().findViewById(R.id.workbench_task_li);
        widget_top_bg = getActivity().findViewById(R.id.widget_top_bg);
        widget_top = getActivity().findViewById(R.id.widget_top);
    }

    /**
     * 初始数据
     */
    @Override
    protected void initData() {
        //顶部当前公司名称
        tvCompanyName.setText(SPHelper.getCompanyName());
        //加载工作台数据
        loadWorkbenchData();
        //查询查看其它人工作台权限
        getWorkbenchAuth();
        getSystemAppList();
        //组件
        initStatisticWidget();
        mWidgetAdapter.notifyDataSetChanged();
        getWidgetList();
        mWidgetAdapter.notifyDataSetChanged();
        // mRootScrollView.fullScroll(View.FOCUS_UP);
    }

    /**
     * 查询查看其它人工作台权限
     */
    private void getWorkbenchAuth() {
        MainLogic.getInstance().queryWorkbenchAuth(((BaseActivity) getActivity()),
                new ProgressSubscriber<WorkbenchAuthBean>(getActivity(), false) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }

                    @Override
                    public void onNext(WorkbenchAuthBean workbenchAuthBean) {
                        super.onNext(workbenchAuthBean);
                        finishRefresh();
                        if (workbenchAuthBean.getData() != null && "1".equals(workbenchAuthBean.getData().getHaveChagnePrivilege())) {
                            hasSwitchAuth = true;
                            mIvSwitch.setVisibility(View.VISIBLE);
                        } else {
                            hasSwitchAuth = false;
                            mIvSwitch.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * 获取已保存工作台组件列表
     */
    private void getWidgetList() {
        MainLogic.getInstance().getCommonModules(((BaseActivity) getActivity()), 1,
                new ProgressSubscriber<WidgetListBean>(getActivity(), false) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }

                    @Override
                    public void onNext(WidgetListBean workbenchAuthBean) {
                        super.onNext(workbenchAuthBean);
                        finishRefresh();
                        showWidgetData(workbenchAuthBean.getData());

                    }
                });
    }

    private void showWidgetData(ArrayList<AppModuleBean> workbenchAuthBean) {
        rawWidgetData = workbenchAuthBean;
        for (int i = 0; i < rawWidgetData.size(); i++) {
            final AppModuleBean appModuleBean = rawWidgetData.get(i);
            WidgetBean bean = new WidgetBean();
            String english_name = appModuleBean.getEnglish_name();
            bean.setModuleName(appModuleBean.getChinese_name());
            bean.setModuleId(appModuleBean.getId());
            bean.setModuleBean(english_name);
            bean.setIcon_color(appModuleBean.getIcon_color());
            bean.setIcon_type(appModuleBean.getIcon_type());
            bean.setIcon_url(appModuleBean.getIcon_url());
            if (english_name == null) english_name = "";
            switch (english_name) {
                case ProjectConstants.PROJECT_BEAN_NAME:
                case ProjectConstants.TASK_MODULE_BEAN:
                    bean.setType(AppConstant.TYPE_PROJECT);
                    break;
                case EmailConstant.BEAN_NAME:
                    bean.setType(AppConstant.TYPE_EMAIL);
                    break;
                case FileConstants.BEAN_NAME:
                    bean.setType(AppConstant.TYPE_FILELIB);
                    break;
                case MemoConstant.BEAN_NAME:
                    bean.setType(AppConstant.TYPE_MEMO);
                    break;
                case ApproveConstants.APPROVAL_MODULE_BEAN:
                    bean.setType(AppConstant.TYPE_APPROVE);
                    break;
                case "repository_libraries":
                    bean.setType(AppConstant.TYPE_KNOWLEDGE);
                    break;
                case AttendanceConstants.BEAN_NAME:
                    bean.setType(AppConstant.TYPE_ATTENDANCE);
                    break;
                default:
                    bean.setType(AppConstant.TYPE_DIY_MODULE);
            }
            widgetList.add(bean);
        }
        mWidgetAdapter.notifyDataSetChanged();
    }

    private void initStatisticWidget() {
        widgetList.clear();
        WidgetBean dataNanlysisBean = new WidgetBean();
        dataNanlysisBean.setModuleName("数据分析");
        dataNanlysisBean.setType(AppConstant.TYPE_DATA_ANALYSIS);
        widgetList.add(dataNanlysisBean);
    }


    /**
     * 加载工作台数据
     */

    private void loadWorkbenchData() {
        for (int i = 0; i < 4; i++) {
            getNetData(i + 1, Constants.NORMAL_STATE);
        }
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        getNetData(openedListIndex + 1, Constants.REFRESH_STATE);
    }

    /**
     * 弹出工作台任务列表加载更多
     */
    public void loadMore() {
        getNetData(openedListIndex + 1, Constants.LOAD_STATE);
    }

    /**
     * 刷新或加载更多数据
     *
     * @param workbenchType
     * @param state
     */
    public void getNetData(int workbenchType, int state) {
        PageInfo pageInfo = null;
        pageInfoList.get(workbenchType);
        try {
            pageInfo = pageInfoList.get(workbenchType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pageInfo != null) {
            currentPageNo = pageInfo.getPageNum();
            currentPageNo = state == Constants.LOAD_STATE ? currentPageNo + 1 : 1;
        } else {
            currentPageNo = 1;
        }

        MainLogic.getInstance().queryTimeWorkbench(((BaseActivity) getActivity()), workbenchType,
                currentPageNo, Constants.PAGESIZE, memberIds,
                new ProgressSubscriber<TimeWorkbenchResultBean>(mContext, false) {
                    @Override
                    public void onNext(TimeWorkbenchResultBean baseBean) {
                        super.onNext(baseBean);
                        finishRefresh();
                        List<TaskInfoBean> dataList = baseBean.getData().getDataList();
                        final String dataString = JSONObject.toJSONString(dataList);
                        //缓存工作台数据
                        if (currentPageNo == 1) {
                            SPHelper.setWorkbenchData(workbenchType, dataString);
                        }
                        showData(workbenchType, state, baseBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }
                });

    }

    /**
     * 获取可查看人员列表
     */
    public void getWorkbenchMembers() {
        MainLogic.getInstance().queryTimeWorkbenchMember(((BaseActivity) getActivity()),
                new ProgressSubscriber<WorkbenchMemberBean>(mContext, true) {
                    @Override
                    public void onNext(WorkbenchMemberBean baseBean) {
                        super.onNext(baseBean);
                        ArrayList<Member> dataList = baseBean.getData().getDataList();
                        for (Member m1 : choosedMembers) {
                            for (Member m2 : dataList) {
                                if (m1.getId() == m2.getId()) {
                                    m2.setCheck(true);
                                    m2.setSelectState(C.FREE_TO_SELECT);
                                }
                            }
                        }
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.DATA_TAG1, dataList);
                        bundle.putInt(Constants.DATA_TAG2, GroupMemberActivity.FLAG_MULI);
                        CommonUtil.startActivtiyForResult(getActivity(), ChooseRangeMemberActivity.class, Constants.REQUEST_CODE10, bundle);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    /**
     * 获取系统应用列表
     */
    private void getSystemAppList() {
        //系统应用
        MainLogic.getInstance().findModuleList(((BaseActivity) getActivity()),
                new ProgressSubscriber<LocalModuleBean>(getActivity(), false) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finishRefresh();
                    }

                    @Override
                    public void onNext(LocalModuleBean localModuleBean) {
                        super.onNext(localModuleBean);
                        handleSystemModel(localModuleBean);
                    }
                });
    }

    /**
     * 处理统模块集合
     * @return
     */
    private void handleSystemModel(LocalModuleBean data) {

        List<LocalModuleBean.DataBean> list = data.getData();
        if (list != null && list.size() > 0) {
            boolean flag = false;
            for (int i = 0; i < list.size(); i++) {
                LocalModuleBean.DataBean bean = list.get(i);
                if ("1".equals(bean.getOnoff_status())) {
                    switch (bean.getBean()) {
                        case "project":
                            flag = true;
                            break;
                    }
                }
                if (flag){
                    workbench_task_li.setVisibility(View.VISIBLE);
                    widget_top_bg.setVisibility(View.GONE);
                    widget_top.setVisibility(View.GONE);
                }else {
                    workbench_task_li.setVisibility(View.GONE);
                    widget_top_bg.setVisibility(View.VISIBLE);
                    widget_top.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    /**
     * 处理数据并展示
     *
     * @param workbenchType
     * @param state
     * @param bean
     */
    private void showData(int workbenchType, int state, TimeWorkbenchResultBean bean) {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();

        List<TaskInfoBean> newDataList = bean.getData().getDataList();
        final DragItemAdapter adapter = adapterList.get(workbenchType - 1);
        if (adapter == null) {
            return;
        }
        List<TaskInfoBean> oldDataList = adapterList.get(workbenchType - 1).getItemList();
        switch (state) {
            case Constants.REFRESH_STATE:
            case Constants.NORMAL_STATE:
                adapterList.get(workbenchType - 1).setItemList(newDataList);

                break;
            case Constants.LOAD_STATE:
                if (!CollectionUtils.isEmpty(newDataList)){//如果刷新到新数据  把旧数据清空
                    oldDataList.clear();
                }
                oldDataList.addAll(newDataList);
                adapterList.get(workbenchType - 1).setItemList(oldDataList);
                break;
            default:
                break;
        }
        int num1 = 0;
        List<TaskInfoBean> dataList = adapterList.get(workbenchType - 1).getItemList();
        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getDataType() != 3) {
                    num1++;
                }
            }
        }

        PageInfo pageInfo = bean.getData().getPageInfo();
        numArr[workbenchType - 1] = num1 + pageInfo.getTotalRows();
        switch (workbenchType) {
            case 1:
                TextUtil.setText(mTvNum1, numArr[0] + "");
                break;
            case 2:
                TextUtil.setText(mTvNum2, numArr[1] + "");
                break;
            case 3:
                TextUtil.setText(mTvNum3, numArr[2] + "");
                break;
            case 4:
                TextUtil.setText(mTvNum4, numArr[3] + "");
                break;
            default:

                break;
        }

        int tt = 0;
        for (int i = 0; i < numArr.length; i++) {
            tt += numArr[i];
        }
        TextUtil.setText(mTvTotalNum, tt + "");
        if (openedListIndex == workbenchType - 1) {
            TextUtil.setText(mTvHeaderNum, numArr[openedListIndex] + "");
            if (numArr[openedListIndex] > 0) {
                mEmptyView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
        if (openedListIndex == 100) {
            mEmptyView.setVisibility(View.GONE);
        }
        pageInfoList.put(workbenchType, pageInfo);
    }

    /**
     * 获取系统模块集合
     *
     * @return
     */
    private List<AppModuleBean> getSystemList(LocalModuleBean data) {
        List<AppModuleBean> systemList = new ArrayList<>();
        AppModuleBean data3 = new AppModuleBean();
        data3.setChinese_name("审批");
        data3.setEnglish_name(ApproveConstants.APPROVAL_MODULE_BEAN);
        systemList.add(data3);
        AppModuleBean data2 = new AppModuleBean();
        data2.setChinese_name("备忘录");
        data2.setEnglish_name(MemoConstant.BEAN_NAME);
        systemList.add(data2);
        AppModuleBean data4 = new AppModuleBean();
        data4.setChinese_name("知识库");
        data4.setEnglish_name(MemoConstant.BEAN_NAME_KNOWLEDGE);
        systemList.add(data4);


        List<LocalModuleBean.DataBean> list = data.getData();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                AppModuleBean data1 = new AppModuleBean();
                LocalModuleBean.DataBean bean = list.get(i);
                if ("1".equals(bean.getOnoff_status())) {
                    switch (bean.getBean()) {
                        case "email":
                            data1.setChinese_name("邮件");
                            data1.setEnglish_name(EmailConstant.BEAN_NAME);
                            systemList.add(data1);
                            break;
                        case "project":
                            data1.setChinese_name("协作");
                            data1.setEnglish_name(ProjectConstants.PROJECT_BEAN_NAME);
                            systemList.add(data1);
                            break;
                        case "library":
                            data1.setChinese_name("文件库");
                            data1.setEnglish_name(FileConstants.BEAN_NAME);
                            systemList.add(data1);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        if (Constants.IS_DEBUG) {
            AppModuleBean data5 = new AppModuleBean();
            data5.setChinese_name("考勤");
            data5.setEnglish_name(AttendanceConstants.BEAN_NAME);
            systemList.add(data5);
        }
        return systemList;
    }


    @Override
    protected void setListener() {
        tvCompanyName.setOnClickListener(this);
        rlSwitchCom.setOnClickListener(this);
        //看板滑动回调
        mAddWidget.setOnClickListener(v -> {
            manageWidget();
        });
        mRlType1.setOnClickListener(v -> {
            openDataList(0);
        });
        mRlType2.setOnClickListener(v -> {
            openDataList(1);
        });
        mRlType3.setOnClickListener(v -> {
            openDataList(2);
        });
        mRlType4.setOnClickListener(v -> {
            openDataList(3);
        });
        mRlHeader.setOnClickListener(v -> {
            hideDataList();
        });
        mIvSwitch.setOnClickListener(v -> {
            //选择人员
            getWorkbenchMembers();

        });
        mRlSwitch.setOnClickListener(v -> {
            if (!hasSwitchAuth) {
                return;
            }
            if (mIvSwitch.getVisibility() == View.VISIBLE) {
                //选择人员
                getWorkbenchMembers();
            }

        });
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                moduleData.clear();
                refreshData();
                mRefreshLayout.finishRefresh(3000);

            }
        });
        mRefreshLayoutAll.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshAllData();
                mRefreshLayoutAll.finishRefresh(3000);
            }
        });
        mRefreshLayoutAll.setEnableLoadMore(false);


    }

    /**
     * 管理控件
     */
    private void manageWidget() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DATA_TAG1, (Serializable) rawWidgetData);
        CommonUtil.startActivtiyForResult(getActivity(), ManageWidgetActivity.class, Constants.REQUEST_CODE14, bundle);
    }

    /**
     * 刷新数据
     */
    private void refreshAllData() {
        updateNameAndAvatar();
        isRefresh = true;
        completeNum = 0;
        tvCompanyName.setText(SPHelper.getCompanyName());
        loadWorkbenchData();
        getWorkbenchAuth();
        initStatisticWidget();
        mWidgetAdapter.notifyDataSetChanged();
        getWidgetList();
        getSystemAppList();
    }

    /**
     * 更新名字与头像(用户变更自己的头像时会接到通知)
     */
    private void updateNameAndAvatar() {
        if (TextUtils.isEmpty(memberIds)) {
            ImageLoader.loadCircleImage(getActivity(), SPHelper.getUserAvatar(),
                    mIvCuttentMember, SPHelper.getUserName());
            TextUtil.setText(mTvCuttentMember, SPHelper.getUserName());
        }
    }

    public void hideDataList() {
        mFl1.setVisibility(View.GONE);
        mRefreshLayout.finishLoadMore();
        openedListIndex = 100;
    }

    public void openModule(AppModuleBean item, String moduleId) {
        String moduleBean = item.getEnglish_name();
        if (moduleBean == null) moduleBean = "";
        //系统模块
        Bundle bundle = new Bundle();
        switch (moduleBean) {
            case ApproveConstants.APPROVAL_MODULE_BEAN:
                bundle.putString(Constants.MODULE_BEAN, ApproveConstants.APPROVAL_MODULE_BEAN);
                CommonUtil.startActivtiy(getActivity(), ApproveActivity.class);
                break;
            case ProjectConstants.PROJECT_BEAN_NAME:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://project/main", bundle);
                break;
            case MemoConstant.BEAN_NAME:
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                UIRouter.getInstance().openUri(mContext, "DDComp://memo/main", bundle);
                break;
            case EmailConstant.BEAN_NAME:
                bundle.putInt(Constants.DATA_TAG3, EmailConstant.ADD_NEW_EMAIL);
                UIRouter.getInstance().openUri(mContext, "DDComp://email/email", bundle);
                break;
            case FileConstants.BEAN_NAME:
                UIRouter.getInstance().openUri(mContext, "DDComp://filelib/netdisk", bundle);
                break;
            default:
                if (TextUtil.isEmpty(moduleId)) {
                    //ToastUtils.showError(mContext, "未获取到模块ID");
                    return;
                }
                bundle.putString(Constants.MODULE_BEAN, item.getEnglish_name());
                bundle.putString(Constants.NAME, item.getChinese_name());
                bundle.putString(Constants.MODULE_ID, moduleId);
                UIRouter.getInstance().openUri(mContext, "DDComp://custom/temp", bundle);
                break;
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setAppModuleBean(null);
        }
    }

    /**
     * 打开任务列表
     *
     * @param index
     */
    private void openDataList(int index) {
        mTvChinese.setText(chineseTitleArr[index]);
        mTvEnglish.setText(englishTitleArr[index]);
        TextUtil.setText(mTvHeaderNum, tvList.get(index).getText());
        openedListIndex = index;
        refreshData();
        mRlHeader.setBackgroundResource(titleBgArr[index]);
        for (int i = 0; i < rvList.size(); i++) {
            if (i == index) {
                rvList.get(i).setVisibility(View.VISIBLE);
            } else {
                rvList.get(i).setVisibility(View.GONE);
            }
        }
        mFl1.setVisibility(View.VISIBLE);
        if (numArr[index] > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe
    public void onEvent(MessageBean bean) {
        switch (bean.getCode()) {
            //复制任务,新增任务,删除任务,任务完成状态变更
            case ProjectConstants.PROJECT_TASK_REFRESH_CODE:
            case ProjectConstants.PERSONAL_TASK_REFRESH_CODE:
                //审批状态变更
            case ApproveConstants.REFRESH:
                refreshDataAll();
                break;
            case Constants.EDIT_USER_INFO:
                //用户信息变更
                updateNameAndAvatar();
                break;
            default:
                break;
        }

    }


    @Subscribe
    public void onDataChange(ImMessage bean) {
        if (TextUtils.isEmpty(bean.getTag())) {
            return;
        }

    }


    private void finishRefresh() {
        if (isRefresh) {
            completeNum++;
        }
        if (completeNum == 9) {
            mRefreshLayoutAll.finishRefresh();
            isRefresh = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_company_name:
            case R.id.ll_top_bar:
                CommonUtil.startActivtiy(mContext, SelectCompanyActivity.class);
                break;
            case R.id.iv_app_module:
                UIRouter.getInstance().openUri(getActivity(), "DDComp://project/appModule", null);
                break;
            default:
                break;
        }
    }


    /**
     * 刷新数据
     */
    public void refreshDataAll() {
        pageInfoList.clear();
        loadWorkbenchData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK != resultCode) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE10) {
            ArrayList<Member> list = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            choosedMembers = list;
            if (list == null || list.size() <= 0) {
                choosedMembers = new ArrayList<>();
                memberIds = "";
                updateNameAndAvatar();
                refreshDataAll();
            } else {
                if (list.size() == 1) {
                    memberIds = list.get(0).getId() + "";
                    TextUtil.setText(mTvCuttentMember, list.get(0).getEmployee_name());
                    ImageLoader.loadCircleImage(getActivity(), list.get(0).getPicture(), mIvCuttentMember, list.get(0).getEmployee_name());
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        if (TextUtils.isEmpty(sb)) {
                            sb.append(list.get(i).getId());
                        } else {
                            sb.append("," + list.get(i).getId());
                        }
                    }
                    memberIds = sb.toString();
                    TextUtil.setText(mTvCuttentMember, "共" + list.size() + "人");
                    ImageLoader.loadCircleImage(getActivity(), R.drawable.icon_normal_group, mIvCuttentMember);
                }
                refreshDataAll();
            }
        } else if (requestCode == Constants.REQUEST_CODE14) {
            List<AppModuleBean> list = (List<AppModuleBean>) data.getSerializableExtra(Constants.DATA_TAG1);
            rawWidgetData = (ArrayList<AppModuleBean>) list;
            initStatisticWidget();
            showWidgetData(rawWidgetData);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}

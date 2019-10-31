package com.hjhq.teamface.project.ui.task;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.EntryBean;
import com.hjhq.teamface.basis.bean.ProjectPicklistStatusBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.network.utils.TransformerHelper;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.ScreenUtils;
import com.hjhq.teamface.basis.view.member.MembersView;
import com.hjhq.teamface.basis.view.recycler.MyLinearDeviderDecoration;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.common.view.FlowLayout;
import com.hjhq.teamface.common.view.TextWebView;
import com.hjhq.teamface.customcomponent.widget2.BaseView;
import com.hjhq.teamface.customcomponent.widget2.select.MemberView;
import com.hjhq.teamface.customcomponent.widget2.select.TimeSelectView;
import com.hjhq.teamface.customcomponent.widget2.web.RichTextWebView;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.NodeBean;
import com.hjhq.teamface.project.bean.QueryHierarchyResultBean;
import com.hjhq.teamface.project.bean.SubTaskBean;
import com.hjhq.teamface.project.bean.TaskLayoutResultBean;
import com.hjhq.teamface.project.view.CommonPopupWindow;
import com.hjhq.teamface.project.widget.file.TaskAttachmentView;
import com.hjhq.teamface.project.widget.reference.PersonalTaskReferenceView;
import com.hjhq.teamface.project.widget.select.PickListTagView;
import com.hjhq.teamface.project.widget.utils.ProjectCustomUtil;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 任务详情
 *
 * @author Administrator
 * @date 2018/6/25
 */

public class TaskDetailDelegate extends AppDelegate {
    private TextView tvProjectName;
    private TextView tvNodeName;
    private TextView tvSubNodeName;
    public TextView tvTaskName;
    private LinearLayout llContent;
    private ImageView ivSubTask;
    private ImageView ivRelevance;
    private ImageView ivBeRelevance;
    private View llAssociates;
    private View llRelevance;
    private View llBeRelevance;
    private View llSubTask;
    private ImageView ivAssociatesSpread;
    public RecyclerView subTaskRecyclerView;
    public RecyclerView relevanceRecyclerView;
    public RecyclerView beRelevanceRecyclerView;
    public RecyclerView trendRecyclerView;
    private View header;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private TextView tvProgressSum;
    private ImageView ivLike;
    private TextView tvLikeNum;
    private TextView tvLike;
    private View ivTaskStatus;
    public ImageView ivCheckStatus;
    private View llCheck;
    public MembersView membersView;
    public SwitchButton sbtn;
    private TextView tvSubTempName;
    public RecyclerView mRvComment;
    public RecyclerView mRvDynamic;
    public RecyclerView mRvState;
    public FrameLayout mFlComment;
    public ScrollView mScrollView;
    public CommentInputView mCommentInputView;


    //zzh->ad:协作改版
    public LinearLayout executor_li;//执行人姓名
    public LinearLayout star_ttime_li;//开始时间
    public LinearLayout end_ttime_li;//结束时间
    public LinearLayout describe_value;//描述
    public LinearLayout appendix_li;//附件列表
    public LinearLayout addlable_li;//标签列表
    public LinearLayout priority_li;//优先级
    public LinearLayout dynamic_more_data;//动态显示更多
    public RelativeLayout nomal_li;//基本信息
    public SwitchButton sBtnTaskCheck;
    public SwitchButton sBtnOnlyParticipantVisible;
    public MembersView state_member_view;
    public MembersView executor_member_view;
    private TextView trend_content;//动态选择器
    private TextView nomals_edit;
    private TextView subListProgressSum;
    private LinearLayout picklist_status_li;
    private TextView picklist_status_text;
    private ImageView iv_icon;
    private TextView be_relation_progress_sum;
    private TextView relation_progress_sum;
    private LinearLayout relevance_li;
    public RelativeLayout lable_rl;//标签布局
    public RelativeLayout re_relevance;//关联布局
    public RelativeLayout priority_rl;//优先级布局

    private CommonPopupWindow popupWindow;

    public TaskAttachmentView attachmenView;

    public List<BaseView> listView = new ArrayList<>();
    public List<BaseView> customlistView = new ArrayList<>();

    public boolean editTaskPesssiom = false;
    public boolean isSuspend = false;
    @Override
    public int getRootLayoutId() {
        return R.layout.project_activity_task_detail;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setRightMenuIcons(R.drawable.icon_menu);
        showMenu();
        setLeftIcon(R.drawable.icon_blue_back);
        setLeftText(R.color.app_blue, "返回");
        setTitle("详情");
        llContent = get(R.id.ll_task_layout);
        tvProjectName = get(R.id.tv_project_name);
        tvNodeName = get(R.id.tv_node_name);
        tvSubNodeName = get(R.id.tv_sub_node_name);
        tvSubTempName = get(R.id.tv_sub_temp_name);
        tvTaskName = get(R.id.tv_task_name);
        ivTaskStatus = get(R.id.iv_task_status);
        ivCheckStatus = get(R.id.iv_check_status);
        membersView = get(R.id.member_view);
        llCheck = get(R.id.ll_check);
        sbtn = get(R.id.sbtn_associates);
        mRvComment = get(R.id.rv_comment);
        mRvDynamic = get(R.id.rv_dynamic);
        mRvState = get(R.id.rv_state);
        mFlComment = get(R.id.fl_comment);
        mScrollView = get(R.id.sv_root);

        picklist_status_li = get(R.id.picklist_status_li);
        picklist_status_text = get(R.id.picklist_status_text);
        iv_icon = get(R.id.iv_icon);

        lable_rl = get(R.id.lable_rl);
        re_relevance = get(R.id.re_relevance);
        priority_rl = get(R.id.priority_rl);

        mCommentInputView = new CommentInputView(getActivity());
        mFlComment.addView(mCommentInputView);
        mFlComment.setVisibility(View.GONE);
        mRvComment.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvState.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRvDynamic.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        llAssociates = get(R.id.ll_associates);
        ivAssociatesSpread = get(R.id.iv_associates_spread);
        subTaskRecyclerView = get(R.id.sub_task_recyler_view);
        ivSubTask = get(R.id.iv_sub_task);
        relevanceRecyclerView = get(R.id.relevance_recyler_view);
        ivRelevance = get(R.id.iv_relevance);
        beRelevanceRecyclerView = get(R.id.be_relevance_recyler_view);
        ivBeRelevance = get(R.id.iv_be_relevance);
        llRelevance = get(R.id.ll_relevance);
        llBeRelevance = get(R.id.ll_be_relevance);
        llSubTask = get(R.id.ll_sub_task);

        ivLike = get(R.id.iv_like);
        tvLike = get(R.id.tv_like);
        tvLikeNum = get(R.id.tv_like_num);

        //zzh->ad:协作改版
        executor_li = get(R.id.executor_li);
       star_ttime_li = get(R.id.star_ttime_li);
       end_ttime_li = get(R.id.end_ttime_li);
       describe_value = get(R.id.describe_text);
       appendix_li = get(R.id.appendix_li);
       addlable_li = get(R.id.addlable_li);
       priority_li = get(R.id.priority_li);
        dynamic_more_data=get(R.id.dynamic_more_data);
        trend_content = get(R.id.trend_content);
        trendRecyclerView = get(R.id.rv_all_trend);
        sBtnTaskCheck = get(R.id.sbtn_task_check);
        state_member_view = get(R.id.state_member_view);
        executor_member_view = get(R.id.executor_member_view);
        sBtnOnlyParticipantVisible = get(R.id.sbtn_only_participant_visible);
        nomals_edit = get(R.id.nomals_edit);
        nomal_li = get(R.id.nomal_li);
        subListProgressSum = get(R.id.sublist_progress_sum);
        be_relation_progress_sum = get(R.id.be_relation_progress_sum);
        relation_progress_sum= get(R.id.relation_progress_sum);
        re_relevance = get(R.id.re_relevance);
        relevance_li = get(R.id.relevance_li);


        header = View.inflate(mContext, R.layout.project_item_sub_task_header, null);
        progressBar = header.findViewById(R.id.progress_bar);
        tvProgress = header.findViewById(R.id.tv_progress);
        tvProgressSum = header.findViewById(R.id.tv_progress_sum);

        subTaskRecyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        relevanceRecyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        beRelevanceRecyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        trendRecyclerView.addItemDecoration(new MyLinearDeviderDecoration(mContext, R.color.gray_bb));
        trendRecyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });


        int naviWidth = (int) (ScreenUtils.getScreenWidth(mContext) / 2);
        tvNodeName.setMaxWidth(naviWidth);
        tvSubNodeName.setMaxWidth(naviWidth);
        tvProjectName.setMaxWidth(naviWidth);

        trend_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFlowLayout(v);
            }
        });

    }

    /**
     * 加载动态选择
     */
    private void initFlowLayout(View view) {
        List<String> labels = new ArrayList<>();
        labels.add(mContext.getResources().getString(R.string.project_all_trends));
        labels.add(mContext.getResources().getString(R.string.project_all_task_commen));
        labels.add(mContext.getResources().getString(R.string.project_all_task_dynamic));
        labels.add(mContext.getResources().getString(R.string.project_all_task_state));

        popupWindow = new CommonPopupWindow(mContext,labels,view,0,new CommonPopupWindow.OnClickListener(){

            @Override
            public void onClick(View view, int position,int type) {
                int dynamicType = 0;
                if (position == 1){
                    dynamicType = 1;
                    trend_content.setText(mContext.getResources().getString(R.string.project_all_task_commen));
                }else if(position == 2){
                    dynamicType = 3;
                    trend_content.setText(mContext.getResources().getString(R.string.project_all_task_dynamic));
                }else if (position == 3){
                    dynamicType = 2;
                    trend_content.setText(mContext.getResources().getString(R.string.project_all_task_state));
                }else{
                    trend_content.setText(mContext.getResources().getString(R.string.project_all_trends));
                }
                RxManager.$(hashCode()).post(CustomConstants.MESSAGE_FILE_DETAIL_DYNAMIC_CODE,dynamicType);
                popupWindow.dimiss();
            }
        });
        popupWindow.show(view,2);
    }


    public void setCommentAdapter(BaseQuickAdapter adapter) {
        mRvComment.setAdapter(adapter);
    }

    public void setStateAdapter(BaseQuickAdapter adapter) {
        mRvState.setAdapter(adapter);
    }

    public void setDyanmicAdapter(BaseQuickAdapter adapter) {
        mRvDynamic.setAdapter(adapter);
    }


    /***
     * 设置子任务数量
     * @param count
     * @param progress
     */
    public void setSubTaskHead(int count, int progress) {
        //List data = adapter.getData();
       // int count = data.size();
       // progressBar.setMax(count);
       // progressBar.setProgress(progress);
      //  TextUtil.setText(tvProgress, progress + "");
      //  adapter.setHeaderView(header);
        TextUtil.setText(subListProgressSum,progress+ "/" + count);
    }

    /**
     * 设置引用数量
     * @param count
     */
    public void setRelationHead(int count){
        TextUtil.setText(relation_progress_sum, ""+count);
    }
    /**
     * 设置被引用数量
     * @param count
     */
    public void setBeRelationHead(int count){
        TextUtil.setText(be_relation_progress_sum, ""+count);
    }

    /**
     * 设置子任务适配器
     *
     * @param adapter
     */
    public void setSubTaskAdapter(BaseQuickAdapter adapter) {
        subTaskRecyclerView.setAdapter(adapter);
    }

    /**
     * 设置引用适配器
     *
     * @param adapter
     */
    public void setRelevanceAdapter(BaseQuickAdapter adapter) {
        relevanceRecyclerView.setAdapter(adapter);
    }

    /**
     * 设置被引用适配器
     *
     * @param adapter
     */
    public void setBeRelevanceAdapter(BaseQuickAdapter adapter) {
        beRelevanceRecyclerView.setAdapter(adapter);
    }

    /**
     * 隐藏被引用
     */
    public void hideBeRelevance() {
        setVisibility(R.id.ll_be_relevance_root, false);
    }

    /***
     * 隐藏个人任务关联
     */
    public void hidePersionRelevance(){
        setVisibility(R.id.re_relevance, false);
    }

    /**
     * 隐藏子任务
     */
    public void hideSubTask() {
        setVisibility(R.id.ll_sub_task_root, false);
    }

    public void setNavigatorLayout(QueryHierarchyResultBean.DataBean data) {
        TextUtil.setText(tvProjectName, data.getProjectname());
        TextUtil.setText(tvNodeName, data.getParentnodename());

       // TextUtil.setText(tvSubNodeName, data.getSubnodename());
       // String subTempName = data.getSubnodename2();
       // TextUtil.setText(tvSubTempName, subTempName);
       // setVisibility(R.id.iv_sub_temp, !TextUtil.isEmpty(subTempName));
    }

    /***
     * 设置子任务导航栏
     */
    public void setSubNavigatorLayout(String parrentTaskName){
        TextUtil.setText(tvProjectName, mContext.getResources().getString(R.string.project_belong_task)+parrentTaskName);
    }

    public void hideNavigator() {
        setVisibility(R.id.ll_title, false);
    }

    public void drawLayout(TaskLayoutResultBean.DataBean.EnableLayoutBean enableLayout, String moduleBean, boolean personalTask
            ,boolean completeStatus,String projectStatus) {
        llContent.removeAllViews();

        //zzh->ad:初始化字段
        describe_value.removeAllViews();
        star_ttime_li.removeAllViews();
        end_ttime_li.removeAllViews();
        executor_li.removeAllViews();
        appendix_li.removeAllViews();
        priority_li.removeAllViews();
        addlable_li.removeAllViews();
        relevance_li.removeAllViews();

        listView.clear();

        List<CustomBean> rows = enableLayout.getRows();
        if (CollectionUtils.isEmpty(rows)) {
            nomal_li.setVisibility(View.GONE);
            return;
        }

        for (CustomBean layoutBean : rows) {
            layoutBean.setModuleBean(moduleBean);
           // ProjectCustomUtil.drawLayout(llContent, layoutBean, CustomConstants.DETAIL_STATE, personalTask);
            if(layoutBean.getName().equals("multitext_desc")){//zzh->ad:描述
               BaseView view = new RichTextWebView(layoutBean,true);
                if (view != null) {
                    addLiView(view,describe_value,completeStatus,moduleBean,projectStatus);
                }
            }else if (layoutBean.getName().equals("datetime_starttime")){//zzh->ad:开始时间
                BaseView view = new TimeSelectView(layoutBean,true,"开始时间");
                if (view != null) {
                    addLiView(view,star_ttime_li,completeStatus,moduleBean,projectStatus);
                }
            }else if (layoutBean.getName().equals("datetime_deadline")){//zzh->ad:结束时间
                BaseView view = new TimeSelectView(layoutBean,true,"截止时间");
                if (view != null) {
                    addLiView(view,end_ttime_li,completeStatus,moduleBean,projectStatus);
                }
            }else if (layoutBean.getName().equals("personnel_principal")){//zzh->ad:执行人
               // BaseView view = new MemberView(layoutBean,true);
               // if (view != null) {
               //     addLiView(view,executor_li,completeStatus,moduleBean);
               // }
            }else if (layoutBean.getName().equals("attachment_customnumber")){//zzh->ad:附件列表
                attachmenView = new TaskAttachmentView(layoutBean,true);
                if (attachmenView != null) {
                    addLiView(attachmenView,appendix_li,completeStatus,moduleBean,projectStatus);
                }
            }else if (layoutBean.getName().equals("picklist_priority")){//zzh->ad:优先级
                BaseView view = new PickListTagView(layoutBean,true);
                if (view != null) {
                    addLiView(view,priority_li,completeStatus,moduleBean,projectStatus);
                }
            }else if (layoutBean.getName().equals("picklist_tag")){//zzh->ad:标签列表
                BaseView view = new PickListTagView(layoutBean,true);
                if (view != null) {
                    addLiView(view,addlable_li,completeStatus,moduleBean,projectStatus);
                }
            }else if (layoutBean.getName().equals("picklist_status")){//zzh->ad:状态
                /*BaseView view = new PickListTagView(layoutBean,true);
                if (view != null) {
                    addLiView(view,state_li,completeStatus,moduleBean,projectStatus);
                }*/
            } else if (layoutBean.getName().equals("text_name")){//任务名称

            }else if (layoutBean.getName().equals("reference_relation")){//个人任务关联
                BaseView  view = new PersonalTaskReferenceView(layoutBean,1);
                if (view != null) {
                    addLiView(view,relevance_li,completeStatus,moduleBean,projectStatus);
                }
            }  else {
                /*int state = 0;
                if (completeStatus){
                    state =CustomConstants.DETAIL_STATE;
                }else{
                    state =CustomConstants.EDIT_STATE;
                }*/
                BaseView customView = ProjectCustomUtil.drawDetailLayout(llContent, layoutBean,CustomConstants.DETAIL_STATE, personalTask);//zzh->ad: DETAIL_STATE 改为EDIT_STATE

                customView.getBean().setModuleBean(moduleBean);
                if (customView != null) {
                    listView.add(customView);
                    customlistView.add(customView);

                }
            }
        }
        if (customlistView.size() ==0 && editTaskPesssiom){
            nomal_li.setVisibility(View.GONE);
        }
    }

    public void addLiView(BaseView view,LinearLayout li,boolean completeStatus,String moduleBean,String projectStatus){
        if (editTaskPesssiom && !isSuspend && !completeStatus && !projectStatus.equals("1") && !projectStatus.equals("2")){
            view.setState( CustomConstants.EDIT_STATE);
        }else{
            view.setState( CustomConstants.DETAIL_STATE);
        }
        view.addView(li, (Activity) li.getContext());
        view.setStateVisible();
        view.getBean().setModuleBean(moduleBean);
        if (view != null) {
            listView.add(view);
        }
    }

    public void setPickStatus(List<EntryBean> status){
        if(!CollectionUtils.isEmpty(status)){
            picklist_status_text.setText(status.get(0).getLabel());
            if(status.get(0).getValue() != null && status.get(0).getValue().equals("0")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.project_nostart);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#EBEDF0"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("1")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.taskcard_state);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#8db4eb"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("2")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.project_suspended);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#EBEDF0"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("3")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.project_complete);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#D6F4E9"));
            }
        }
    }





    /**
     * 设置任务状态
     */
    public void setTaskStatus(boolean complete) {
        ivTaskStatus.setSelected(complete);
    }

    /**
     * 设置检验状态
     *
     * @param passedStatus
     */
    public void setCheckStatus(String passedStatus) {
        ivCheckStatus.setVisibility(View.VISIBLE);
        switch (passedStatus) {
            case ProjectConstants.CHECK_STATUS_WAIT:
                ivCheckStatus.setImageResource(R.drawable.project_icon_check_wait);
                break;
            case ProjectConstants.CHECK_STATUS_PASS:
                ivCheckStatus.setImageResource(R.drawable.project_icon_check_pass);
                break;
            case ProjectConstants.CHECK_STATUS_REJECT:
                ivCheckStatus.setImageResource(R.drawable.project_icon_check_reject);
                break;
            default:
                ivCheckStatus.setImageDrawable(null);
                break;
        }
    }

    /**
     * 设置检验按钮是否显示
     *
     * @param visibility
     */
    public void setCheckBtnVis(int visibility) {
        llCheck.setVisibility(visibility);
    }

    /**
     * 设置任务标题
     *
     * @param title
     */
    public void setTaskTitle(String title) {
        Observable.just(1)
                .compose(TransformerHelper.applySchedulers())
                .subscribe(i -> TextUtil.setText(tvTaskName, title));
    }

    /**
     * 设置协作人是否可见
     */
    public void switchAssociates() {
        setVisibility(R.id.ll_associates, !isVisibili(llAssociates));
        ivAssociatesSpread.setSelected(!ivAssociatesSpread.isSelected());
    }

    /**
     * 设置子任务是否可见
     */
    public void switchSubTask() {
        setVisibility(R.id.ll_sub_task, !isVisibili(llSubTask));
        ivSubTask.setSelected(!ivSubTask.isSelected());
    }

    /**
     * 设置关联是否可见
     */
    public void switchRelevance() {
        setVisibility(R.id.ll_relevance, !isVisibili(llRelevance));
        ivRelevance.setSelected(!ivRelevance.isSelected());
    }

    /**
     * 设置被关联是否可见
     */
    public void switchBeRelevance() {
        setVisibility(R.id.ll_be_relevance, !isVisibili(llBeRelevance));
        ivBeRelevance.setSelected(!ivBeRelevance.isSelected());
    }


    /**
     * 设置协作人数据
     */
    public void setAssociates(List<Member> members) {
        membersView.setMembers(members);
    }

    /**
     * 是否仅协作人可见
     *
     * @param isLook
     */
    public void setCheckedImmediatelyNoEvent(boolean isLook) {
        sbtn.setCheckedImmediatelyNoEvent(isLook);
    }

    public boolean isVisibili(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    /**
     * 设置点赞状态
     */
    public void setLikeStatus() {
        boolean selected = ivLike.isSelected();
        setLikeStatus(!selected);
    }

    public void setLikeStatus(boolean selected) {
        ivLike.setSelected(selected);
        TextUtil.setText(tvLike, mContext.getString(selected ? R.string.project_liked : R.string.project_like));
    }

    /**
     * 是否已点赞
     *
     * @return
     */
    public boolean isLike() {
        return ivLike.isSelected();
    }

    /**
     * 设置点赞任务
     *
     * @param size
     */
    public void setLikeNum(int size) {
        TextUtil.setText(tvLikeNum, size + "个点赞");
    }


    //zzh改版
    /**
     * 隐藏检测控件
     */
    public void hideCheckView() {
        setVisibility(R.id.ll_task_check_one, false);
        setVisibility(R.id.rl_task_check, false);
    }

    /**
     * 隐藏校验按钮控件
     */
    public void hideCheckBtnView() {
        setVisibility(R.id.sbtn_task_check, false);
    }

    /**
     * 隐藏显示更多
     */
    public void hideMoreDynamicData() {
        setVisibility(R.id.dynamic_more_data, false);
    }

    /**
     * 隐藏显示更多
     */
    public void showMoreDynamicData() {
        setVisibility(R.id.dynamic_more_data, true);
    }

    /**
     * 切换校验人
     *
     * @param isChecked
     */
    public void switchTaskCheck(boolean isChecked) {
        setVisibility(R.id.ll_task_check_one, isChecked);
    }

    /**
     * 设置默认检验人
     *
     * @param member
     */
    public void setDefaultCheckOne(Member member) {
        state_member_view.setMember(member);
    }

    /**
     * 设置执行人
     */
    public void setExecutor( List<Member> members) {
        executor_member_view.setMembers(members);
    }


    /**
     * 开启任务校验
     */
    public void openTaskStatus() {
        sBtnTaskCheck.setChecked(true);
        switchTaskCheck(true);
        setVisibility(R.id.sbtn_task_check, true);
    }

    /**
     * 关闭任务校验
     */
    public void closeTaskStatus() {
        sBtnTaskCheck.setChecked(false);
        switchTaskCheck(false);
    }

    /**
     * 获取检验人ID
     * @return
     */
    public String getCheckOneId() {
        String id = null;
        List<Member> members = state_member_view.getMembers();
        if (!CollectionUtils.isEmpty(members)) {
            id = members.get(0).getId() + "";
        }
        return id;
    }

    /**
     * 获取执行人ID
     * @return
     */
    public String getExecutorOneId() {
        String id = "";
        List<Member> members = executor_member_view.getMembers();
        if (!CollectionUtils.isEmpty(members)) {
            id = members.get(0).getId() + "";
        }
        return id;
    }


    public String getTaskCheckStatus() {
        return sBtnTaskCheck.isChecked() ? "1" : "0";
    }

    public String getOnlyParticipantStatus() {
        return sBtnOnlyParticipantVisible.isChecked() ? "1" : "0";
    }

    public void seteditTaskPesssiom(boolean pesssiom){
        editTaskPesssiom = pesssiom;
    }

    public void setIsSuspend(boolean pesssiom){
        isSuspend = pesssiom;
    }

    public void hideLayout(RelativeLayout rl){
        rl.setVisibility(View.GONE);
    }


}

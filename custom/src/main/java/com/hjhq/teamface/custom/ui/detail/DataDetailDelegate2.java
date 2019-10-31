package com.hjhq.teamface.custom.ui.detail;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.LayoutBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.adapter.CommentAdapter;
import com.hjhq.teamface.common.adapter.DynamicAdapter;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.TabListAdapter;
import com.hjhq.teamface.custom.bean.DataRelationResultBean;
import com.hjhq.teamface.custom.bean.ProcessFlowResponseBean;
import com.hjhq.teamface.custom.bean.TabListBean;
import com.hjhq.teamface.custom.utils.AppBarStateChangeListener;
import com.hjhq.teamface.custom.view.ApproveTaskView;
import com.hjhq.teamface.custom.view.JudgeNestedScrollView;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;
import com.hjhq.teamface.customcomponent.widget2.subfield.SubfieldView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import rx.Observable;


/**
 * @author lx
 * @date 2017/9/4
 */

public class DataDetailDelegate2 extends AppDelegate {
    List<View> views = new ArrayList<>();

    public RecyclerView recyclerTab;
    public RecyclerView recyclerComment;
    public RecyclerView recyclerLog;
    private RelativeLayout rlTab;
    private RelativeLayout rlComment;
    private View vBlank;
    private LinearLayout llContent;
    private TabListAdapter mTabListAdapter;
    public CommentAdapter mCommentAdapter;
    public DynamicAdapter mDynamicAdapter;
    private List<SubfieldView> mViewList = new ArrayList<>();

    private CollapsingToolbarLayout collapsingToolbarLayout;
    public CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout appBarLayout;
    public JudgeNestedScrollView scrollView;
    private TextView tvComment;
    private TextView tvText;
    public FrameLayout flComment;
    public CommentInputView commentInputView;
    public ApproveTaskView mApproveTaskView;
    private String title = "";

    @Override
    public int getRootLayoutId() {
        return R.layout.custom_activity_detail2;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public Toolbar getToolbar() {
        return get(R.id.toolbar);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
            get(R.id.rl_title).setPadding(0, statusBarHeight, 0, 0);
        }
        getActivity().setSupportActionBar(getToolbar());
        getToolbar().setNavigationIcon(R.drawable.icon_to_back);

        mContext.getSupportActionBar().setDisplayShowTitleEnabled(true);
        getToolbar().setNavigationOnClickListener(view -> getActivity().finish());
        setRightMenuIcons(R.drawable.menu_white);

        tvComment = get(R.id.tv_comment);
        tvComment.setSelected(true);
        tvText = get(R.id.tv_text);
        scrollView = get(R.id.scrollView);
        rlTab = get(R.id.rl_tab);
        flComment = get(R.id.fl_comment);
        rlComment = get(R.id.rl_comment);
        commentInputView = new CommentInputView(getActivity());
        flComment.addView(commentInputView);
        vBlank = get(R.id.blank);
        collapsingToolbarLayout = get(R.id.collapsingToolbarLayout);
        mCoordinatorLayout = get(R.id.cl);
        collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);
        appBarLayout = get(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.EXPANDED) {
                    //展开状态
                    setVisibility(R.id.tv_text, true);
                    setVisibility(R.id.iv_company, true);
                } else if (state == State.COLLAPSED) {
                    //折叠状态
                    setVisibility(R.id.tv_text, false);
                    setVisibility(R.id.iv_company, false);
                } else {
                    //中间状态
                    setVisibility(R.id.iv_company, false);
                    setVisibility(R.id.tv_text, false);
                }
            }
        });

        recyclerTab = get(R.id.recycler_module);
        recyclerComment = get(R.id.rv_comment);
        recyclerLog = get(R.id.rv_log);
        llContent = get(R.id.ll_content);
        mApproveTaskView = get(R.id.process_view);


        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerTab.setLayoutManager(mLinearLayoutManager);
        recyclerComment.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerLog.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTabListAdapter = new TabListAdapter(new ArrayList<>());
        mCommentAdapter = new CommentAdapter(new ArrayList<>());
        mDynamicAdapter = new DynamicAdapter(new ArrayList<>());
        recyclerTab.setAdapter(mTabListAdapter);
        recyclerLog.setAdapter(mDynamicAdapter);
        recyclerComment.setAdapter(mCommentAdapter);


    }


    /**
     * 设置详情头部信息
     *
     * @param operationInfo
     */
    public void setDetailHeadView(RowBean operationInfo) {
        TextView textView = new TextView(getActivity());
        CustomUtil.setDetailHeaderValue(textView, operationInfo);
        String s = textView.getText().toString();
        tvText.setText(title + "");
        if (!TextUtil.isEmpty(s) && !title.equals(s)) {
            collapsingToolbarLayout.setTitle(s);
        } else {
            //getToolbar().setTitle(title);
            tvText.setText(title + "");
        }

    }

    /**
     * 评论输入
     *
     * @param v
     */
    public void setCommentView(CommentInputView v) {
        commentInputView = v;
        flComment.addView(commentInputView);
    }


    /**
     * 绘制布局
     *
     * @param layoutBeanList 布局
     * @param detailMap      数据
     * @param state          0新增 1详情 2编辑
     * @param moduleId       模块id
     */
    public void drawLayout(CustomLayoutResultBean.DataBean layoutBeanList, Map detailMap, int state, String moduleId, String seapoolId) {
        if (layoutBeanList == null) {
            return;
        }
        title = layoutBeanList.getTitle();
        llContent.removeAllViews();
        mViewList.clear();
        List<LayoutBean> layout = layoutBeanList.getLayout();
        if (layout == null) {
            return;
        }
        for (LayoutBean layoutBean : layout) {
            boolean isTerminalApp = "1".equals(layoutBean.getTerminalApp());
            boolean isHideInDetail = "1".equals(layoutBean.getIsHideInDetail());
            boolean isSpread = "0".equals(layoutBean.getIsSpread());
//            boolean isHideColumnName = "1".equals(layoutBean.getIsHideColumnName());
            //详情分栏一定显示
            boolean isHideColumnName = false;
            if (!isTerminalApp || isHideInDetail) {
                //不是app端或新建时隐藏
                continue;
            }

            List<CustomBean> list = layoutBean.getRows();
            for (CustomBean customBean : list) {
                //设置详情中的值
                Object o = detailMap.get(customBean.getName());
                customBean.setValue(o);
                //判断是否需要做保密处理
                boolean needConceal = false;
                final String pool = customBean.getHighSeasPool();
                if (!TextUtils.isEmpty(pool)) {
                    final List<String> poolIdList = Arrays.asList(pool.split(","));
                    if (poolIdList.contains(seapoolId)) {
                        needConceal = true;
                    }
                }
                customBean.setNeedConceal(needConceal);
            }
            SubfieldView subfieldView = new SubfieldView(list, state, layoutBean.getTitle(), isSpread, moduleId);
            subfieldView.setHideColumnName(isHideColumnName);
            subfieldView.addView(llContent);
            mViewList.add(subfieldView);
        }
    }

    /**
     * 设置关联模块数据
     *
     * @param data
     */
    public void setAdapterData(List<DataRelationResultBean.DataRelation.RefModule> data) {
        List<String> refModules = new ArrayList<>();
        if (!CollectionUtils.isEmpty(data)) {
            recyclerTab.setVisibility(View.VISIBLE);
            Observable.from(data).filter(refModule -> "1".equals(refModule.getShow())).subscribe(refModule -> refModules.add(refModule.getModuleLabel()));
        }
        CollectionUtils.notifyDataSetChanged(mTabListAdapter, mTabListAdapter.getData(), refModules);
    }

    /**
     * 页签数据
     *
     * @param data
     */
    public void setTabData(List<TabListBean.DataBean.DataListBean> data) {
        mTabListAdapter.getData().clear();
        if (!CollectionUtils.isEmpty(data)) {
            rlTab.setVisibility(View.VISIBLE);
            vBlank.setVisibility(View.VISIBLE);
            mTabListAdapter.getData().addAll(data);
        } else {
            rlTab.setVisibility(View.GONE);
            vBlank.setVisibility(View.GONE);
        }
        mTabListAdapter.notifyDataSetChanged();

    }

    /*public void setAuthModule(List<AutoModuleResultBean.DataBean.DataListBean> dataList) {
        if (!CollectionUtils.isEmpty(dataList)) {
            recyclerTab.setVisibility(View.VISIBLE);
            List<String> modules = new ArrayList<>();
            Observable.from(dataList).subscribe(module -> modules.add(module.getTitle()));
            mTabListAdapter.addData(modules);
        }
    }*/

    List<SubfieldView> getViewList() {
        return mViewList;
    }

    void showComment() {
        get(R.id.tv_comment).setVisibility(View.VISIBLE);
        get(R.id.tv_comment).setSelected(true);
        get(R.id.tv_dynamic).setSelected(false);
        rlComment.setVisibility(View.VISIBLE);
        recyclerLog.setVisibility(View.GONE);
        flComment.setVisibility(View.VISIBLE);
        get(R.id.tv_process).setSelected(false);
        mApproveTaskView.setVisibility(View.GONE);
    }

    void showDynamic(boolean flag) {
        if (flag) {
            get(R.id.tv_dynamic).setVisibility(View.VISIBLE);
        } else {
            flComment.setVisibility(View.GONE);
            commentInputView.getInputView().clearFocus();
            SoftKeyboardUtils.hide(commentInputView.getInputView());
            commentInputView.getInputView().clearFocus();
            get(R.id.tv_comment).setSelected(false);
            get(R.id.tv_dynamic).setSelected(true);
            rlComment.setVisibility(View.GONE);
            recyclerLog.setVisibility(View.VISIBLE);
            get(R.id.tv_process).setSelected(false);
            mApproveTaskView.setVisibility(View.GONE);
        }

    }

    void showEmail() {
        get(R.id.line_2).setVisibility(View.VISIBLE);
        get(R.id.ll_bottom_option).setVisibility(View.VISIBLE);
        get(R.id.tv_email).setVisibility(View.VISIBLE);
        views.add(get(R.id.tv_email));
    }

    void showProcess(boolean flag) {
        if (flag) {
            get(R.id.tv_process).setVisibility(View.VISIBLE);
        } else {
            flComment.setVisibility(View.GONE);
            commentInputView.getInputView().clearFocus();
            SoftKeyboardUtils.hide(commentInputView.getInputView());
            commentInputView.getInputView().clearFocus();
            get(R.id.tv_comment).setSelected(false);
            get(R.id.tv_dynamic).setSelected(false);
            rlComment.setVisibility(View.GONE);
            recyclerLog.setVisibility(View.GONE);
            get(R.id.tv_process).setSelected(true);
            mApproveTaskView.setVisibility(View.VISIBLE);
        }
    }

    public void setCommentCount(String count) {
        TextUtil.setText(tvComment, "评论(" + count + ")");
    }

    public void setDataTitle(String s) {
        collapsingToolbarLayout.setTitle(s);
    }

    public void setApproveTaskFlow(List<ProcessFlowResponseBean.DataBean> flowData) {
        mApproveTaskView.setApproveTaskFlow(flowData);
    }
}
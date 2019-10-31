package com.hjhq.teamface.custom.ui.detail;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.hjhq.teamface.custom.bean.TabListBean;
import com.hjhq.teamface.customcomponent.widget2.CustomUtil;
import com.hjhq.teamface.customcomponent.widget2.subfield.SubfieldView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;

;


/**
 * @author lx
 * @date 2017/9/4
 */

public class DataDetailDelegateV3 extends AppDelegate {
    List<View> views = new ArrayList<>();

    public RecyclerView recyclerTab;
    public RecyclerView recyclerComment;
    public RecyclerView recyclerLog;
    private RelativeLayout rlTab;
    private View vBlank;
    private LinearLayout llContent;
    private TabListAdapter mTabListAdapter;
    public CommentAdapter mCommentAdapter;
    public DynamicAdapter mDynamicAdapter;
    private List<SubfieldView> mViewList = new ArrayList<>();

    private TextView tvComment;
    private TextView tvText;
    ScrollView scrollView;
    private TextView tvTitle;
    public LinearLayout flComment;
    public CommentInputView commentInputView;

    @Override
    public int getRootLayoutId() {
        return R.layout.custom_activity_detail_v3;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }


    @Override
    public void initWidget() {
        super.initWidget();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
            get(R.id.rl_title).setPadding(0, statusBarHeight, 0, 0);
            get(R.id.fl_comment).setPadding(0, 0, 0, statusBarHeight);
        }

        tvComment = get(R.id.tv_comment);
        tvComment.setSelected(true);
        tvText = get(R.id.tv_text);
        tvTitle = get(R.id.tv_title);
        rlTab = get(R.id.rl_tab);
        flComment = get(R.id.fl_comment);
        commentInputView = new CommentInputView(getActivity());
        flComment.addView(commentInputView);
        vBlank = get(R.id.blank);
        scrollView = get(R.id.sv);
        recyclerTab = get(R.id.recycler_module);
        recyclerComment = get(R.id.rv_comment);
        recyclerLog = get(R.id.rv_log);
        llContent = get(R.id.ll_content);

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

    @Override
    public void setTitle(String text) {
        TextUtil.setText(tvTitle, text);
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
        if (get(R.id.tv_sub_title).getVisibility() == View.VISIBLE) {
            setTitle("详情");
        } else {
            setTitle(s);
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
    public void drawLayout(CustomLayoutResultBean.DataBean layoutBeanList, Map detailMap, int state, String moduleId) {
        if (layoutBeanList == null) {
            return;
        }

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
                Object o = detailMap.get(customBean.getName());
                customBean.setValue(o);
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
        recyclerComment.setVisibility(View.VISIBLE);
        recyclerLog.setVisibility(View.GONE);
        flComment.setVisibility(View.VISIBLE);
    }

    void showDynamic() {

        flComment.setVisibility(View.GONE);
        commentInputView.getInputView().clearFocus();
        SoftKeyboardUtils.hide(commentInputView.getInputView());
        commentInputView.getInputView().clearFocus();
        get(R.id.tv_dynamic).setVisibility(View.VISIBLE);
        get(R.id.tv_comment).setSelected(false);
        get(R.id.tv_dynamic).setSelected(true);
        recyclerComment.setVisibility(View.GONE);
        recyclerLog.setVisibility(View.VISIBLE);
    }

    void showEmail() {
        get(R.id.line_2).setVisibility(View.VISIBLE);
        get(R.id.ll_bottom_option).setVisibility(View.VISIBLE);
        get(R.id.tv_email).setVisibility(View.VISIBLE);
        views.add(get(R.id.tv_email));
    }

}
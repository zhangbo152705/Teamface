package com.hjhq.teamface.project.ui.filter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.util.StatusBarUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.utils.TaskHelper;
import com.hjhq.teamface.project.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 任务筛选
 * Created by lx on 2017/9/4.
 */

public class FilterDelegate extends AppDelegate {
    public RecyclerView mRecyclerViewMain;
    public LinearLayout mLinearLayout;
    private RecyclerView mRecyclerView;
    private ImageView ivCustomSortArrows;
    private ImageView ivMeSortArrows;
    public RelativeLayout rlCheck1;
    public RelativeLayout rlCheck2;
    public RelativeLayout rlCheck3;
    private ImageView ivCheck1;
    private ImageView ivCheck2;
    private ImageView ivCheck3;
    private List<ImageView> ivList = new ArrayList<>();
    private List<RelativeLayout> rlList = new ArrayList<>();

    @Override
    public int getRootLayoutId() {
        return R.layout.project_fragment_filter;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(getActivity());
        get(R.id.fl_search).setPadding(0, statusBarHeight, 0, 0);
        mLinearLayout = get(R.id.ll_container);
        mRecyclerViewMain = get(R.id.recycler_view_main);
        mRecyclerView = get(R.id.recycler_view);
        ivCheck1 = get(R.id.iv_responsible);
        ivCheck2 = get(R.id.iv_created);
        ivCheck3 = get(R.id.iv_participant);
        rlCheck1 = get(R.id.rl_responsible);
        rlCheck2 = get(R.id.rl_created);
        rlCheck3 = get(R.id.rl_participant);
        ivList.add(ivCheck1);
        ivList.add(ivCheck2);
        ivList.add(ivCheck3);
        rlList.add(rlCheck1);
        rlList.add(rlCheck2);
        rlList.add(rlCheck3);
        mRecyclerViewMain.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        ivCustomSortArrows = get(R.id.iv_custom_sort_arrows);
        ivMeSortArrows = get(R.id.iv_me_sort_arrows);

        //视图 箭头转变
        TaskHelper.INSTANCE.rotateView(mContext, ivMeSortArrows, 0f, 180f, 50,
                R.drawable.icon_sort_down);
    }


    /**
     * 隐藏我的排序
     */
    public void hideMeSort() {
        setVisibility(R.id.ll_me_sort, false);
    }

    /**
     * 自定义排序 开关
     */
    public void switchCustomSort() {
        boolean bool = get(R.id.ll_custom_sort_content).getVisibility() == View.VISIBLE;
        setVisibility(R.id.ll_custom_sort_content, !bool);

        SoftKeyboardUtils.hide(ivCustomSortArrows);
        TaskHelper.INSTANCE.rotateView(mContext, ivCustomSortArrows, 0f, bool ? -180f : 180f, 500,
                R.drawable.icon_sort_down);
    }

    /**
     * 我的排序 开关
     */
    public void switchMeSort() {
        boolean bool = get(R.id.ll_me_sort_content).getVisibility() == View.VISIBLE;
        setVisibility(R.id.ll_me_sort_content, !bool);

        SoftKeyboardUtils.hide(ivMeSortArrows);
        TaskHelper.INSTANCE.rotateView(mContext, ivMeSortArrows, 0f, bool ? -180f : 180f, 500,
                R.drawable.icon_sort_down);
    }

    public void setMainAdapter(BaseQuickAdapter adapter) {
        mRecyclerViewMain.setAdapter(adapter);
    }

    public void setAdapter(BaseQuickAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * 自定义排序点击
     *
     * @param id
     */
    public void customSortClick(int id) {
        get(R.id.tv_create_time_asc).setSelected(false);
        get(R.id.tv_create_time_asc_arrows).setSelected(false);
        get(R.id.iv_create_time_asc).setSelected(false);

        get(R.id.tv_create_time_desc).setSelected(false);
        get(R.id.tv_create_time_desc_arrows).setSelected(false);
        get(R.id.iv_create_time_desc).setSelected(false);

        get(R.id.tv_modify_time_asc).setSelected(false);
        get(R.id.tv_modify_time_asc_arrows).setSelected(false);
        get(R.id.iv_modify_time_asc).setSelected(false);

        get(R.id.tv_modify_time_desc).setSelected(false);
        get(R.id.tv_modify_time_desc_arrows).setSelected(false);
        get(R.id.iv_modify_time_desc).setSelected(false);

        if (id == R.id.rl_create_time_asc) {
            get(R.id.tv_create_time_asc).setSelected(true);
            get(R.id.tv_create_time_asc_arrows).setSelected(true);
            get(R.id.iv_create_time_asc).setSelected(true);
        } else if (id == R.id.rl_create_time_desc) {
            get(R.id.tv_create_time_desc).setSelected(true);
            get(R.id.tv_create_time_desc_arrows).setSelected(true);
            get(R.id.iv_create_time_desc).setSelected(true);
        } else if (id == R.id.rl_modify_time_asc) {
            get(R.id.tv_modify_time_asc).setSelected(true);
            get(R.id.tv_modify_time_asc_arrows).setSelected(true);
            get(R.id.iv_modify_time_asc).setSelected(true);
        } else if (id == R.id.rl_modify_time_desc) {
            get(R.id.tv_modify_time_desc).setSelected(true);
            get(R.id.tv_modify_time_desc_arrows).setSelected(true);
            get(R.id.iv_modify_time_desc).setSelected(true);
        }
    }

    /**
     * 我的排序点击
     *
     * @param id
     */
    public void meSortClick(int id) {
        get(R.id.tv_responsible).setSelected(false);
        get(R.id.iv_responsible).setSelected(false);
        get(R.id.tv_created).setSelected(false);
        get(R.id.iv_created).setSelected(false);
        get(R.id.tv_participant).setSelected(false);
        get(R.id.iv_participant).setSelected(false);

        if (id == R.id.rl_responsible) {
            get(R.id.tv_responsible).setSelected(true);
            get(R.id.iv_responsible).setSelected(true);
        } else if (id == R.id.rl_created) {
            get(R.id.tv_created).setSelected(true);
            get(R.id.iv_created).setSelected(true);
        } else if (id == R.id.rl_participant) {
            get(R.id.tv_participant).setSelected(true);
            get(R.id.iv_participant).setSelected(true);
        }
    }


    /**
     * 获取自定义排序
     */
    public String getCustomSort() {
        if (get(R.id.iv_create_time_asc).isSelected()) {
            return "create_time:asc";
        } else if (get(R.id.iv_create_time_desc).isSelected()) {
            return "create_time:desc";
        } else if (get(R.id.iv_modify_time_asc).isSelected()) {
            return "modify_time:asc";
        } else if (get(R.id.iv_modify_time_desc).isSelected()) {
            return "modify_time:desc";
        }
        return "";
    }

    /**
     * 重置
     */
    public void reset() {
        customSortClick(0);
        meSortClick(0);
        setCheck(-1);
    }


    public void setCheck(int index) {
        if (index == -1) {
            for (int i = 0; i < ivList.size(); i++) {
                ivList.get(i).setSelected(false);
                ivList.get(i).setImageResource(R.drawable.state_uncheck_oval);
            }

        } else {
            boolean selected = ivList.get(index).isSelected();
            for (int i = 0; i < ivList.size(); i++) {
                ivList.get(i).setSelected(false);
            }
            ivList.get(index).setSelected(!selected);
            for (int i = 0; i < ivList.size(); i++) {
                if (ivList.get(i).isSelected()) {
                    ivList.get(i).setImageResource(R.drawable.selected);
                } else {
                    ivList.get(i).setImageResource(R.drawable.state_uncheck_oval);
                }
            }
        }


    }

    public String getCheck() {
        String type = "";
        for (int i = 0; i < ivList.size(); i++) {
            if (ivList.get(i).isSelected()) {
                type = i + "";
                return type;
            }
        }
        return type;
    }
}

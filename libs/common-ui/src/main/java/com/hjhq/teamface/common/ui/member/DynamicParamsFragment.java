package com.hjhq.teamface.common.ui.member;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.bean.RoleGroupResponseBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.SPUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.adapter.OrganizationNaviAdapter;
import com.hjhq.teamface.common.adapter.SelectParamsAdapter;
import com.hjhq.teamface.common.bean.DynamicParamsBean;
import com.hjhq.teamface.common.bean.OrganizationNaviData;
import com.hjhq.teamface.common.utils.MemberUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 角色
 */
public class DynamicParamsFragment extends FragmentPresenter<RoleGroupDelegate, CommonModel> implements View.OnClickListener {
    private SelectMemberActivity mActivity;
    private OrganizationNaviAdapter mNaviAdapter;
    private SelectParamsAdapter mParamsAdapter;

    /**
     * 当前导航数据
     */
    ArrayList<OrganizationNaviData> naviData = new ArrayList<>();
    /**
     * 当前角色
     */
    private List<DynamicParamsBean.DataBean> roleGroupList = new ArrayList<>();

    /**
     * 所有数据
     */
    private List<DynamicParamsBean.DataBean> allData;
    //导航层级
    int roleLevel;

    @Override
    protected void init() {
        mActivity = (SelectMemberActivity) getActivity();
        mNaviAdapter = new OrganizationNaviAdapter(naviData);

        viewDelegate.setNaviAdapter(mNaviAdapter);
        initData();
    }


    private void initData() {
        roleLevel = 0;
        model.getSharePersonalFields((RxAppCompatActivity) getActivity(), mActivity.moduleBean, 2,
                new ProgressSubscriber<DynamicParamsBean>(getActivity()) {
                    @Override
                    public void onNext(DynamicParamsBean baseBean) {
                        super.onNext(baseBean);
                        allData = baseBean.getData();
                        filterData();
                        mActivity.setParamData(allData);
                        handleParamData();

                        OrganizationNaviData organizationNaviData = new OrganizationNaviData();
                        organizationNaviData.setOrganizationId(SPUtils.getString(getContext(), AppConst.COMPANY_ID));
                        organizationNaviData.setOrganizationLevel(roleLevel);
                        organizationNaviData.setOrganizationName("动态参数");
                        naviData.clear();
                        naviData.add(organizationNaviData);
                        mNaviAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * 过滤数据
     */
    private void filterData() {
        if (mActivity != null && mActivity.mSpecialMembers != null){
            for (DynamicParamsBean.DataBean dataBean : allData) {
                for (Member member : mActivity.mSpecialMembers) {
                    if (member.getType() == 3 && member.getValue() .equals(dataBean.getValue())) {
                        dataBean.setCheck(member.isCheck());
                    }
                }
            }
        }

        /*for (DynamicParamsBean.DataBean dataBean : allData) {
            List<DynamicParamsBean.DataBean> roles = dataBean.get();
            if (CollectionUtils.isEmpty(roles)) {
                continue;
            }
            for (RoleGroupResponseBean.DataBean.RolesBean rolesBean : roles) {
                for (Member member : mActivity.mSpecialMembers) {
                    if (member.getType() == 2 && member.getId() == rolesBean.getId()) {
                        rolesBean.setCheck(member.isCheck());
                        rolesBean.setSelectState(member.getSelectState());
                    }
                }
            }

            //去掉需要隐藏的条目
            Iterator<RoleGroupResponseBean.DataBean.RolesBean> iterator = roles.iterator();
            while (iterator.hasNext()) {
                if (MemberUtils.checkState(iterator.next().getSelectState(), C.HIDE_TO_SELECT)) {
                    iterator.remove();
                }
            }
        }*/
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRvDataList.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (mActivity.checkType == C.RADIO_SELECT) {
                    final boolean check = mParamsAdapter.getData().get(position).isCheck();
                    for (int i = 0; i < mParamsAdapter.getData().size(); i++) {
                        mParamsAdapter.getData().get(i).setCheck(false);
                    }
                    mParamsAdapter.getData().get(position).setCheck(!check);

                } else if (mActivity.checkType == C.MULTIL_SELECT) {
                    final boolean check = mParamsAdapter.getData().get(position).isCheck();
                    mParamsAdapter.getData().get(position).setCheck(!check);
                }
                mParamsAdapter.notifyDataSetChanged();
                mActivity.setParamData(allData);
            }

        });
        viewDelegate.mRvNavi.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                OrganizationNaviData organizationNaviData = naviData.get(position);
                int level = organizationNaviData.getOrganizationLevel();
                if (level > 0 || level == roleLevel) {
                    return;
                }

                //处理角色组
                handleParamData();
                naviData.remove(1);
                roleLevel = level;
                mNaviAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 处理角色组数据
     */
    private void handleParamData() {
        if (mParamsAdapter == null) {
            mParamsAdapter = new SelectParamsAdapter(allData);
        }
        viewDelegate.setDataAdapter(mParamsAdapter);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.search_edit_text) {
            Bundle bundle = new Bundle();
            bundle.putInt(C.CHECK_TYPE_TAG, mActivity.checkType);
            bundle.putInt(C.MEMBER_TYPE_TAG, C.ROLE);
            ToastUtils.showError(getActivity(), "敬请期待");
//            CommonUtil.startActivtiyForResult(mActivity, SearchMemberActivity.class, Constants.REQUEST_SEARCH_MEMBER, bundle);

        }

    }

    /**
     * 设置全选
     *
     * @param isChecked
     */
    public void setAllSelect(boolean isChecked) {
        setRolesGroupSelect(allData, isChecked);
        mParamsAdapter.notifyDataSetChanged();
        mActivity.setParamData(allData);
    }

    /**
     * 设置角色 选中
     *
     * @param roleGroupList
     * @param isCheck       是否选中
     */
    public void setRolesGroupSelect(List<DynamicParamsBean.DataBean> roleGroupList, boolean isCheck) {
        if (CollectionUtils.isEmpty(roleGroupList)) {
            return;
        }
        for (DynamicParamsBean.DataBean roleGroup : roleGroupList) {
            roleGroup.setCheck(isCheck);
        }
        mParamsAdapter.notifyDataSetChanged();
    }

    /**
     * 设置角色 选中
     *
     * @param rolesBeanList
     * @param isCheck       是否选中
     */
    public void setRolesSelect(List<RoleGroupResponseBean.DataBean.RolesBean> rolesBeanList, boolean isCheck) {
        if (!CollectionUtils.isEmpty(rolesBeanList)) {
            for (RoleGroupResponseBean.DataBean.RolesBean bean : rolesBeanList) {
                bean.setCheck(isCheck);
            }
        }
    }

    public int getSelectNum() {
        int num = 0;
        if (!CollectionUtils.isEmpty(allData)) {
            for (DynamicParamsBean.DataBean bean : allData) {
                if (bean.isCheck()) {
                    num++;
                }
            }
        }
        return num;
    }

    public boolean onBackPressed() {
        boolean isBack = true;
        if (roleLevel > 0) {
            naviData.remove(roleLevel);
            roleLevel--;
            //处理角色组
            handleParamData();
            mNaviAdapter.notifyDataSetChanged();
            isBack = false;
        }
        return isBack;
    }
}
package com.hjhq.teamface.common.weight.filter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.R;
import com.hjhq.teamface.basis.bean.FilterFieldResultBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.weight.BaseFilterView;
import com.hjhq.teamface.common.weight.adapter.MemberFilterAdapter2;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;


/**
 * 筛选条件中 人员选择
 *
 * @author Administrator
 */

public class MemberFilterView2 extends BaseFilterView implements ActivityPresenter.OnActivityResult {

    private RelativeLayout rlAction;
    private RelativeLayout rlChoose;
    private RelativeLayout rlContent;
    private RecyclerView mRecyclerView;
    private TextView tvTitle;
    private MemberFilterAdapter2 mAdapter;
    private List<Member> mDataList = new ArrayList<>();

    private boolean mFlag = false;
    private View ivArraw;
    private int mRequestCode;

    public MemberFilterView2(FilterFieldResultBean.DataBean bean) {
        super(bean);
    }

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {
        mRequestCode = ++Constants.REQUEST_CODE;
        mView = View.inflate(activity, R.layout.crm_item_goods_filter_by_creator_v2, null);
        tvTitle = (TextView) mView.findViewById(R.id.tv_title);

        rlAction = (RelativeLayout) mView.findViewById(R.id.rl_title_creator);
        rlChoose = (RelativeLayout) mView.findViewById(R.id.rl_choose);
        rlContent = (RelativeLayout) mView.findViewById(R.id.rl_action_creator);
        if (!mFlag) {
            rlContent.setVisibility(View.GONE);
        }
        ivArraw = mView.findViewById(R.id.iv);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        initView();
        setClickListener();
        parent.addView(mView, index);

    }

    private void initView() {
        TextUtil.setText(tvTitle, title);
        mAdapter = new MemberFilterAdapter2(mDataList);
        if (bean.getName().startsWith(CustomConstants.DEPARTMENT)) {
            mAdapter.setDepartment(true);
        } else if (bean.getName().startsWith(CustomConstants.DEPARTMENT)) {
            mAdapter.setDepartment(false);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setClickListener() {
        rlAction.setOnClickListener(v -> {
            SoftKeyboardUtils.hide(v);
            if (!mFlag) {
                rlContent.setVisibility(View.VISIBLE);
                mFlag = !mFlag;
                rotateCButton(mActivity, ivArraw, 0f, 180f, 500,
                        R.drawable.icon_sort_down);
            } else {
                rlContent.setVisibility(View.GONE);
                mFlag = !mFlag;
                rotateCButton(mActivity, ivArraw, 0f, -180f, 500,
                        R.drawable.icon_sort_down);
            }
        });
        rlChoose.setOnClickListener(v -> {
            SoftKeyboardUtils.hide(v);
            Bundle bundle = new Bundle();
            ArrayList<Member> members = new ArrayList<Member>();
            if (mDataList != null && mDataList.size() > 0) {
                for (Member member : mDataList) {
                    member.setCheck(true);
                    member.setSelectState(C.FREE_TO_SELECT);
                    members.add(member);
                }
            }

            bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, members);
            bundle.putInt(C.CHECK_TYPE_TAG, C.MULTIL_SELECT);
            if (bean.getId().startsWith(CustomConstants.DEPARTMENT)) {
                bundle.putString(C.CHOOSE_RANGE_TAG, "1");
            } else if (bean.getId().startsWith(CustomConstants.PERSONNEL)) {
                bundle.putString(C.CHOOSE_RANGE_TAG, "0");
            }
            bundle.putInt(Constants.DATA_TAG666, 1);
            CommonUtil.startActivtiyForResult(getContext(), SelectMemberActivity.class, mRequestCode, bundle);

        });
    }

    public boolean formatCheck() {
        return true;
    }

    @Override
    public boolean put(JSONObject json) throws Exception {
        if (mDataList == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        Observable.from(mDataList).filter(data -> data != null).subscribe(data -> sb.append(data.getId() + ","));
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            json.put(keyName, sb.toString());
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK != resultCode) {
            return;
        }
        if (requestCode != mRequestCode) {
            return;
        }
        mDataList.clear();
        if (data != null) {
            List<Member> list = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            if (list != null && list.size() > 0) {
                mDataList.addAll(list);
            }
        }
        mAdapter.notifyDataSetChanged();

    }
}

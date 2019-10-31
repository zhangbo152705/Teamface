package com.hjhq.teamface.customcomponent.widget2.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.layoutmanager.MyLayoutManager2;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.member.SelectRangeDepartmentActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.FlowLayout;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.adapter.DepartmentAdapter;
import com.hjhq.teamface.customcomponent.widget2.base.SelectCommonView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门组件
 *
 * @author lx
 * @date 2017/6/21
 */
public class DepartmentView extends SelectCommonView implements ActivityPresenter.OnActivityResult {
    /**
     * 选择范围
     */
    private List<Member> chooseRange;
    /**
     * 默认部门
     */
    private List<Member> defaultDepartment;

    /**
     * 是否多选  选择类型：0单选、1多选
     */
    private boolean isMulti;

    private FlowLayout mFlowLayout;
    /**
     * 已选人员
     */
    private List<Member> mMembers;
    private DepartmentAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private View rlFlowLayout;

    public DepartmentView(CustomBean bean) {
        super(bean);
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            defaultDepartment = field.getDefaultDepartment();
            isMulti = "1".equals(field.getChooseType());
            chooseRange = field.getChooseRange();
        }
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
            RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
            RxManager.$(aHashCode).on(keyName, this::setContent);
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
    }

    public void setContent(Object content) {
        if (content instanceof List) {
            List<Member> list = new ArrayList<>();
            for (Object o : ((List) content)) {
                try {
                    Member m = JSONObject.parseObject(JSONObject.toJSONString(o), Member.class);
                    if (m != null) {
                        list.add(m);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setMembers(list);
        } else {
            setMembers(new ArrayList<>());
        }
    }

    @Override
    public int getLayout() {
        if ("0".equals(structure)) {
            return R.layout.custom_department_widget_layout;
        } else {
            return R.layout.custom_department_widget_row_layout;
        }
    }

    @Override
    public void initView() {
        mFlowLayout = mView.findViewById(R.id.pick_flow_layout);
        rlFlowLayout = mView.findViewById(R.id.rl_flow_layout);
        mRecyclerView = mView.findViewById(R.id.rv);
        mRecyclerView.setLayoutManager(new MyLayoutManager2());
        mAdapter = new DepartmentAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);
        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            mAdapter.setEdit(true);
            tvContent.addTextChangedListener(new TextWatcherUtil.MyTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    if (TextUtil.isEmpty(s.toString())) {
                        ivRight.setImageResource(R.drawable.custom_icon_department);
                    } else {
                        ivRight.setImageResource(R.drawable.clear_button);
                    }
                }
            });
            ivRight.setOnClickListener(v -> {
                List<Member> members = getMembers();
                if (!CollectionUtils.isEmpty(members)) {
                    members.clear();
                    setMembers(members);
                }
            });
        }
        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                onClick();
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                mMembers.remove(position);
                mAdapter.setNewData(mMembers);
            }
        });
        super.initView();
    }

    @Override
    public void initOption() {
        ivRight.setImageResource(R.drawable.custom_icon_department);
        if (isDetailState() && (mMembers == null || mMembers.size() == 0)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("未选择");
            tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
        }
    }

    @Override
    protected void setData(Object value) {
        List<Member> members;
        if (value instanceof String) {
            String vstr = (String) value;
            if (TextUtils.isEmpty(vstr)) {
                if (isDetailState()) {
                    rlFlowLayout.setVisibility(View.GONE);
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setText("未选择");
                    tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
                }
                return;
            }
        }
        final String string = JSONObject.toJSONString(value);
        if (TextUtils.isEmpty(string)) {
            if (isDetailState()) {
                rlFlowLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText("未选择");
                tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
            }
            return;
        }

        members = new Gson().fromJson(JSONObject.toJSONString(value), new TypeToken<List<Member>>() {
        }.getType());

        setMembers(members);
    }


    private void setMembers(List<Member> members) {
        mMembers = members;
        /*if (mMembers != null && mMembers.size() > 0) {
            for (Member member : members) {
                member.setCheck(true);
                member.setSelectState(C.FREE_TO_SELECT);
                member.setType(C.DEPARTMENT);
                member.setValue(C.DEPARTMENT + ":" + member.getId());
            }
        }
        StringBuilder sb = new StringBuilder();
        Observable.from(members).subscribe(member -> sb.append("、").append(member.getName()));
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        TextUtil.setText(tvContent, sb.toString().trim());*/
        /*if (mMembers != null && mMembers.size() > 0) {
            tvContent.setVisibility(View.GONE);
            rlFlowLayout.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.drawable.clear_button);
        } else {
            rlFlowLayout.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.drawable.custom_icon_department);
        }
        mAdapter.setNewData(mMembers);*/
        if (mMembers != null && mMembers.size() > 0) {
            tvContent.setVisibility(View.GONE);
            rlFlowLayout.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.drawable.clear_button);
            initFlowLayout(mFlowLayout, mMembers);
        } else {
            rlFlowLayout.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.drawable.custom_icon_department);
        }

    }

    private void initFlowLayout(FlowLayout mFragmentFileDetailFlowLayout,
                                List<Member> labels) {

        mFragmentFileDetailFlowLayout.removeAllViews();
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 5;
        lp.rightMargin = 5;
        lp.topMargin = 5;
        lp.bottomMargin = 5;
        if (labels != null) {
            for (int i = 0; i < labels.size(); i++) {
                View rootView = ((Activity) mView.getContext()).getLayoutInflater().inflate(R.layout.department_item, null);
                View delete = rootView.findViewById(R.id.delete);
                if (state == CustomConstants.ADD_STATE || state == CustomConstants.EDIT_STATE) {
                    delete.setVisibility(View.VISIBLE);
                    int pos = i;
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMembers.remove(pos);
                            initFlowLayout(mFlowLayout, mMembers);
                        }
                    });
                } else {
                    delete.setVisibility(View.GONE);
                }
                TextView view = rootView.findViewById(R.id.name);
                view.setText(labels.get(i).getName());
                /*view.setPadding(12, 4, 12, 4);
                view.setTextColor(Color.WHITE);
                view.setTextSize(14);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.custom_flow_label);
                GradientDrawable myGrad = (GradientDrawable) view.getBackground();*/
                view.setTextColor(ColorUtils.resToColor(getContext(), R.color.black_4a));
                mFragmentFileDetailFlowLayout.addView(rootView, lp);

            }
        }

        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            if (CollectionUtils.isEmpty(labels)) {
                ivRight.setImageResource(R.drawable.icon_to_next);
            } else {
                ivRight.setImageResource(R.drawable.clear_button);
            }
        }
    }

    private List<Member> getMembers() {
        return mMembers == null ? new ArrayList<>() : mMembers;
    }

    @Override
    public void onClick() {
        super.onClick();
        Bundle bundle = new Bundle();
        bundle.putInt(C.CHECK_TYPE_TAG, isMulti ? C.MULTIL_SELECT : C.RADIO_SELECT);
        bundle.putString(C.CHOOSE_RANGE_TAG, "0");
        ((ActivityPresenter) mView.getContext()).setOnActivityResult(code, this);
        List<Member> members = getMembers();
        for (Member member : members) {
            member.setCheck(true);
            member.setType(C.DEPARTMENT);
            member.setSelectState(C.FREE_TO_SELECT);
        }
        if (!CollectionUtils.isEmpty(chooseRange)) {
            bundle.putSerializable(C.CHOOSE_RANGE_TAG, (Serializable) chooseRange);
            bundle.putSerializable(C.SELECTED_MEMBER_TAG, (Serializable) members);

            CommonUtil.startActivtiyForResult(mView.getContext(), SelectRangeDepartmentActivity.class, code, bundle);
        } else {
            //公司员工

            bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, (Serializable) members);
            bundle.putString(C.CHOOSE_RANGE_TAG, "1");
            bundle.putString(Constants.NAME, "选择部门");
            CommonUtil.startActivtiyForResult(mView.getContext(), SelectMemberActivity.class, code, bundle);
        }
    }

    @Override
    public boolean formatCheck() {
        return true;
    }

    @Override
    public void setDefaultValue() {
        if (CollectionUtils.isEmpty(defaultDepartment)) {
            return;
        }
        ArrayList<Member> members = new ArrayList<>();
        if (defaultDepartment.get(0).getId() == -1) {
            //动态参数的值 替换成自己
            String departmentId = SPHelper.getDepartmentId();
            Member member = new Member(TextUtil.parseLong(departmentId), SPHelper.getDepartmentName(), 0);
            member.setValue("0:" + departmentId);
            members.add(member);
        } else {
            members.addAll(defaultDepartment);
        }
        setMembers(members);
    }

    /**
     * 添加人员
     */
    private void addMember() {
        Bundle bundle = new Bundle();
        bundle.putInt(C.CHECK_TYPE_TAG, isMulti ? C.MULTIL_SELECT : C.RADIO_SELECT);
        bundle.putString(C.CHOOSE_RANGE_TAG, "0");
        ((ActivityPresenter) mView.getContext()).setOnActivityResult(code, this);
        ArrayList<Member> members = (ArrayList<Member>) getMembers();

        if (!CollectionUtils.isEmpty(chooseRange)) {
            bundle.putSerializable(C.CHOOSE_RANGE_TAG, (Serializable) chooseRange);
            bundle.putSerializable(C.SELECTED_MEMBER_TAG, members);

            CommonUtil.startActivtiyForResult(mView.getContext(), SelectRangeDepartmentActivity.class, code, bundle);
        } else {
            //公司员工
            for (Member member : members) {
                member.setCheck(true);
                member.setSelectState(C.FREE_TO_SELECT);
            }
            bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, members);
            bundle.putString(C.CHOOSE_RANGE_TAG, "1");
            CommonUtil.startActivtiyForResult(mView.getContext(), SelectMemberActivity.class, code, bundle);
        }
    }

    @Override
    public Object getValue() {
        List<Member> members = getMembers();

        if (CollectionUtils.isEmpty(members)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Member member : members) {
            sb.append(member.getId() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code && resultCode == Activity.RESULT_OK) {
            ArrayList<Member> members = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            setMembers(members);
            if (!checkNull() && isLinkage) {
                linkageData();
            }
        }
    }
}

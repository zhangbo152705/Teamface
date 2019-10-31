package com.hjhq.teamface.customcomponent.widget2.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.JsonParser;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.TextWatcherUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.member.SelectEmployeeActivity;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.ui.member.SelectRangeActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.customcomponent.R;
import com.hjhq.teamface.customcomponent.widget2.base.SelectCommonView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 人员组件
 *
 * @author lx
 * @date 2017/6/21
 */
public class MemberView extends SelectCommonView implements ActivityPresenter.OnActivityResult {

    /**
     * 选择范围
     */
    private List<Member> chooseRange;
    /**
     * 可选人员
     */
    private List<Member> choosePersonnel;
    /**
     * 默认人员
     */
    private List<Member> defaultPersonnel;

    /**
     * 是否多选  选择类型：0单选、1多选
     */
    private boolean isMulti;
    private String trim;
    private boolean flag;
    private View bottom_line;
    private ImageView iv_left;
    /**
     * 已选人员
     */
    private List<Member> mMembers;

    public MemberView(CustomBean bean) {
        super(bean);
        initMemberView(bean);
    }

    public MemberView(CustomBean bean,boolean flag) {
        super(bean);
        initMemberView(bean);
        this.flag = flag;
    }

    public void initSpecFlagView(){
        if (flag){
            tvTitle.setVisibility(View.GONE);
            bottom_line = mView.findViewById(R.id.bottom_line);
            bottom_line.setVisibility(View.GONE);
            iv_left = mView.findViewById(R.id.iv_left);
            iv_left.setVisibility(View.VISIBLE);
            //zzh->ad:增加数组越界判断
            if (mMembers != null && mMembers.size()>0){
                ImageLoader.loadImage(getContext(), mMembers.get(0).getPicture(), iv_left);
            }
        }
    }

    public void initMemberView(CustomBean bean){
        if (keyName.startsWith(CustomConstants.SUBFORM)) {
            //关联映射
            RxManager.$(aHashCode).on(keyName + subFormIndex, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG + subFormIndex, this::setContent);
        } else {
            //关联映射
            RxManager.$(aHashCode).on(keyName, this::setContent);
            //联动
            RxManager.$(aHashCode).on(keyName + CustomConstants.LINKAGE_TAG, this::setContent);
        }
        CustomBean.FieldBean field = bean.getField();
        if (field != null) {
            defaultPersonnel = field.getDefaultPersonnel();
            isMulti = "1".equals(field.getChooseType());
            choosePersonnel = field.getChoosePersonnel();
            chooseRange = field.getChooseRange();
            if (defaultPersonnel != null && defaultPersonnel.size() > 0) {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < defaultPersonnel.size(); i++) {
                    if (TextUtils.isEmpty(sb)) {
                        sb.append(defaultPersonnel.get(i).getId() + "");
                    } else {
                        sb.append("," + defaultPersonnel.get(i).getId() + "");
                    }
                }
                defaultValue = sb.toString();
            }
        }
    }

    @Override
    public int getLayout() {
        if ("0".equals(structure)) {
            return R.layout.custom_member_layout;
        } else {
            return R.layout.custom_member_row_layout;
        }
    }

    @Override
    public void initView() {
        if (CustomConstants.DETAIL_STATE != state && !CustomConstants.FIELD_READ.equals(fieldControl)) {
            tvContent.addTextChangedListener(new TextWatcherUtil.MyTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    if (TextUtil.isEmpty(s.toString())) {
                        ivRight.setImageResource(R.drawable.custom_icon_member);
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

        super.initView();
    }

    @Override
    public void initOption() {
        ivRight.setImageResource(R.drawable.custom_icon_member);
        if (isDetailState() && (mMembers == null || mMembers.size() == 0)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText("未选择");
            tvContent.setTextColor(getContext().getResources().getColor(R.color._999999));
        }
    }

    @Override
    protected void setData(Object value) {
        List<Member> members = new JsonParser<Member>().jsonFromList(value, Member.class);
        setMembers(members);
        initSpecFlagView();
    }

    private void setMembers(List<Member> members) {
        mMembers = members;

        StringBuilder sb = new StringBuilder();
        Observable.from(members).subscribe(member -> sb.append("、").append(member.getName()));
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        trim = sb.toString().trim();
        tvContent.post(new Runnable() {
            @Override
            public void run() {


                int width = tvContent.getWidth();
                int v = (int) (tvContent.getTextSize() * trim.length());

                StringBuilder sb2 = new StringBuilder();
                if (width < v + 50) {
                    String s = " 等" + mMembers.size() + "人";
                    for (int i = 0; i < mMembers.size(); i++) {
                        if (TextUtils.isEmpty(mMembers.get(i).getName())) {
                            continue;
                        }
                        if ((sb2.length() + mMembers.get(i).getName().length() + s.length() + 1) * tvContent.getTextSize() < width) {
                            sb2.append("、").append(mMembers.get(i).getName());
                        } else {
                            break;
                        }
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(0);
                    }
                    sb2.append(s);
                    trim = sb2.toString();
                    /*float v1 = s.length() * tvContent.getTextSize();
                    float v2 = (width - v1) / tvContent.getTextSize();
                    if (trim.length() > v2) {
                        trim = trim.substring(0, (int) v2) + s;
                    } else {
                        trim = trim + s;
                    }*/

                }
                TextUtil.setText(tvContent, trim);
            }
        });

    }

    private List<Member> getMembers() {
        return mMembers == null ? new ArrayList<>() : mMembers;
    }


    @Override
    public void onClick() {
        super.onClick();
        SoftKeyboardUtils.hide(((Activity) mView.getContext()));
        SoftKeyboardUtils.hide(mView);
        SoftKeyboardUtils.hide2(mView.getRootView());
        Bundle bundle = new Bundle();
        bundle.putInt(C.CHECK_TYPE_TAG, isMulti ? C.MULTIL_SELECT : C.RADIO_SELECT);
        bundle.putString(C.CHOOSE_RANGE_TAG, "0");
        ((ActivityPresenter) mView.getContext()).setOnActivityResult(code, this);
        List<Member> members = getMembers();
        if (!CollectionUtils.isEmpty(chooseRange)) {
            bundle.putSerializable(C.CHOOSE_RANGE_TAG, (Serializable) chooseRange);
            bundle.putSerializable(C.SELECTED_MEMBER_TAG, (Serializable) members);
            CommonUtil.startActivtiyForResult(mView.getContext(), SelectRangeActivity.class, code, bundle);
        } else {
            if (choosePersonnel == null) {
                //公司员工
                for (Member member : members) {
                    member.setCheck(true);
                    member.setSelectState(C.FREE_TO_SELECT);
                }

                bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, (Serializable) members);
                CommonUtil.startActivtiyForResult(mView.getContext(), SelectMemberActivity.class, code, bundle);
            } else {
                for (Member person : choosePersonnel) {
                    person.setCheck(false);
                    Observable.from(members)
                            .filter(Member::isCheck)
                            .filter(member -> member.getId() == person.getId())
                            .subscribe(member -> person.setCheck(true));
                }
                bundle.putSerializable(C.OPTIONAL_MEMBERS_TAG, (Serializable) choosePersonnel);
                CommonUtil.startActivtiyForResult(mView.getContext(), SelectEmployeeActivity.class, code, bundle);
            }
        }
    }

    /**
     * 设置选人范围
     *
     * @param chooseRange
     */
    public void setChooseRange(List<Member> chooseRange) {
        this.chooseRange = chooseRange;
    }

    @Override
    public boolean formatCheck() {
        return true;
    }

    @Override
    public void setDefaultValue() {
        if (CollectionUtils.isEmpty(defaultPersonnel)) {
            return;
        }
        ArrayList<Member> members = new ArrayList<>();
        if (defaultPersonnel.get(0).getId() == -1) {
            //动态参数的值 替换成自己
            String employeeId = SPHelper.getEmployeeId();
            Member member = new Member(TextUtil.parseLong(employeeId), SPHelper.getUserName(), 1);
            member.setPicture(SPHelper.getUserAvatar());
            member.setValue("1:" + employeeId);
            members.add(member);
        } else {
            members.addAll(defaultPersonnel);
        }
        setMembers(members);
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

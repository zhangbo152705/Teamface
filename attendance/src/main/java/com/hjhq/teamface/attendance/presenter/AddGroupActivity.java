package com.hjhq.teamface.attendance.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AddDateBean;
import com.hjhq.teamface.attendance.bean.AddRulesBean;
import com.hjhq.teamface.attendance.bean.AttendanceGroupDetailBean;
import com.hjhq.teamface.attendance.bean.AttendanceTypeListBean;
import com.hjhq.teamface.attendance.bean.MemberOrDepartmentBean;
import com.hjhq.teamface.attendance.bean.TimeBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AddGroupDelegate;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.C;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.member.AddMemberView;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.member.SelectMemberActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/add_attendance_group", desc = "添加考勤组/打卡规则(成员界面)")
public class AddGroupActivity extends ActivityPresenter<AddGroupDelegate, AttendanceModel> implements View.OnClickListener {

    String id;
    int type;
    String name;
    AddMemberView mustMemberView;
    AddMemberView notMemberView;
    AttendanceGroupDetailBean.DataBean mDataBean;
    AddRulesBean bean = new AddRulesBean();

    @Override
    public void init() {
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString(Constants.DATA_TAG1);
            type = bundle.getInt(Constants.DATA_TAG2);

        }
        if (AttendanceConstants.TYPE_EDIT == type && !TextUtils.isEmpty(id)) {
            getNetData();
        }

    }

    private void initView() {
        viewDelegate.setTitle(R.string.attendance_rules_manage_title);
        mustMemberView = viewDelegate.get(R.id.member_view1);
        notMemberView = viewDelegate.get(R.id.member_view2);
    }


    @Override
    protected void bindEvenListener() {

        super.bindEvenListener();
        mustMemberView.setOnAddMemberClickedListener(new AddMemberView.OnAddMemberClickedListener() {
            @Override
            public void onAddMemberClicked() {
                ArrayList<Member> list = (ArrayList<Member>) mustMemberView.getMembers();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCheck(true);
                    list.get(i).setSelectState(C.FREE_TO_SELECT);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, list);
                bundle.putInt(C.CHECK_TYPE_TAG, C.MULTIL_SELECT);
                bundle.putString(C.CHOOSE_RANGE_TAG, "0,1");
                CommonUtil.startActivtiyForResult(AddGroupActivity.this, SelectMemberActivity.class, Constants.REQUEST_CODE1, bundle);

            }
        });
        notMemberView.setOnAddMemberClickedListener(new AddMemberView.OnAddMemberClickedListener() {
            @Override
            public void onAddMemberClicked() {
                ArrayList<Member> list = (ArrayList<Member>) notMemberView.getMembers();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCheck(true);
                    list.get(i).setSelectState(C.FREE_TO_SELECT);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(C.SPECIAL_MEMBERS_TAG, list);
                bundle.putInt(C.CHECK_TYPE_TAG, C.MULTIL_SELECT);
                bundle.putString(C.CHOOSE_RANGE_TAG, "0,1");
                CommonUtil.startActivtiyForResult(AddGroupActivity.this, SelectMemberActivity.class, Constants.REQUEST_CODE2, bundle);
            }
        });

    }

    public void getNetData() {
        model.getAttendanceRulesDetail(mContext, id, new ProgressSubscriber<AttendanceGroupDetailBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(AttendanceGroupDetailBean baseBean) {
                super.onNext(baseBean);
                mDataBean = baseBean.getData();
                setData(mDataBean);
            }
        });
    }

    private void setData(AttendanceGroupDetailBean.DataBean data) {
        String id = data.getId();
        String groupName = data.getName();
        viewDelegate.setName(groupName);
        List<Member> poorPeoples = data.getAttendance_users();
        mustMemberView.setMembers(poorPeoples);
        List<MemberOrDepartmentBean> poor = new ArrayList<>();
        if (poorPeoples != null) {
            for (int i = 0; i < poorPeoples.size(); i++) {
                Member member = poorPeoples.get(i);
                MemberOrDepartmentBean b = new MemberOrDepartmentBean();
                b.setName(member.getName());
                b.setId(member.getId() + "");
                b.setType(member.getType() + "");
                b.setPicture(member.getPicture());
                b.setSign_id(member.getSign_id());
                b.setValue(member.getType() + "-" + member.getId());
                poor.add(b);
            }
        }
        List<Member> richPeoples = data.getExcluded_users();
        notMemberView.setMembers(richPeoples);
        List<MemberOrDepartmentBean> rich = new ArrayList<>();
        if (richPeoples != null) {
            for (int i = 0; i < richPeoples.size(); i++) {
                Member member = richPeoples.get(i);
                MemberOrDepartmentBean b = new MemberOrDepartmentBean();
                b.setName(member.getName());
                b.setId(member.getId() + "");
                b.setType(member.getType() + "");
                b.setPicture(member.getPicture());
                b.setSign_id(member.getSign_id());
                b.setValue(member.getType() + "-" + member.getId());
                rich.add(b);
            }
        }
        String groupType = data.getAttendance_type();
        String holiday_auto_status = data.getHoliday_auto_status();
        String face_status = data.getFace_status();
        String outworker_status = data.getOutworker_status();

        List<Long> workList = data.getWork_day_list();
        List<AttendanceTypeListBean> address = data.getAttendance_address();
        List<Long> addressIds = new ArrayList<>(address.size());
        for (int i = 0; i < address.size(); i++) {
            addressIds.add(TextUtil.parseLong(address.get(i).getId()));
        }
        List<AttendanceTypeListBean> wifi = data.getAttendance_wifi();
        List<Long> wifiIds = new ArrayList<>(wifi.size());
        for (int i = 0; i < wifi.size(); i++) {
            wifiIds.add(TextUtil.parseLong(wifi.get(i).getId()));
        }
        List<AddDateBean> must_punchcard_date = data.getMust_punchcard_date();
        List<TimeBean> no_punchcard_date = data.getNo_punchcard_date();
        bean.setName(groupName);
        bean.setAttendanceusers(poor);
        bean.setExcludedusers(rich);
        bean.setAttendanceType(groupType);
        bean.setWorkdaylist(workList);
        bean.setHolidayAutoStatus(TextUtil.parseInt(holiday_auto_status));
        bean.setMustPunchcardDate(must_punchcard_date);
        bean.setNoPunchcardDate(no_punchcard_date);
        bean.setAttendanceAddress(addressIds);
        bean.setAttendanceWifi(wifiIds);
        bean.setOutworkerStatus(TextUtil.parseInt(outworker_status));
        bean.setFaceStatus(face_status);
        bean.setId(id);
        bean.setAttendanceStartTime(data.getAttendance_start_time());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle bundle = new Bundle();
        switch (type) {

            case AttendanceConstants.TYPE_ADD:
                name = viewDelegate.getName();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast(mContext, "名字不能为空");
                    return true;
                }
                final ArrayList<Member> members = (ArrayList<Member>) mustMemberView.getMembers();
                if (members.size() == 0) {
                    ToastUtils.showToast(mContext, "必须打卡人员不能为空");
                    return true;
                }
                bundle.putInt(Constants.DATA_TAG1, AttendanceConstants.TYPE_ADD);
                bundle.putString(Constants.DATA_TAG2, id);
                bundle.putSerializable(Constants.DATA_TAG3, ((ArrayList<Member>) mustMemberView.getMembers()));
                bundle.putSerializable(Constants.DATA_TAG4, ((ArrayList<Member>) notMemberView.getMembers()));
                bundle.putString(Constants.DATA_TAG5, name);
                CommonUtil.startActivtiyForResult(mContext, AddRulesActivity.class, Constants.REQUEST_CODE3, bundle);

                break;
            case AttendanceConstants.TYPE_EDIT:
                if (mDataBean == null) {
                    getNetData();
                    ToastUtils.showToast(mContext, "正在获取数据");
                } else {
                    String name = viewDelegate.getName();
                    if (TextUtils.isEmpty(name)) {
                        ToastUtils.showToast(mContext, "名字不能为空");
                        return true;
                    }
                    bean.setName(name);
                    List<Member> poorPeoples = mustMemberView.getMembers();
                    List<MemberOrDepartmentBean> poor = new ArrayList<>();
                    if (poorPeoples != null && poorPeoples.size() > 0) {
                        for (int i = 0; i < poorPeoples.size(); i++) {
                            Member member = poorPeoples.get(i);
                            MemberOrDepartmentBean b = new MemberOrDepartmentBean();
                            b.setName(member.getName());
                            b.setId(member.getId() + "");
                            b.setType(member.getType() + "");
                            b.setPicture(member.getPicture());
                            b.setSign_id(member.getSign_id());
                            b.setValue(member.getType() + "-" + member.getId());
                            poor.add(b);
                        }
                    } else {
                        ToastUtils.showToast(mContext, "考勤人员不能为空");
                        return true;
                    }
                    bean.setAttendanceusers(poor);
                    List<Member> richPeoples = notMemberView.getMembers();
                    List<MemberOrDepartmentBean> rich = new ArrayList<>();
                    if (richPeoples != null) {
                        for (int i = 0; i < richPeoples.size(); i++) {
                            Member member = richPeoples.get(i);
                            MemberOrDepartmentBean b = new MemberOrDepartmentBean();
                            b.setName(member.getName());
                            b.setId(member.getId() + "");
                            b.setType(member.getType() + "");
                            b.setPicture(member.getPicture());
                            b.setSign_id(member.getSign_id());
                            b.setValue(member.getType() + "-" + member.getId());
                            rich.add(b);
                        }
                    }
                    bean.setExcludedusers(rich);
                    DialogUtils.getInstance().sureOrCancel(this, "", "选择规则生效时间", viewDelegate.getRootView(), "立即生效", "明日生效", new DialogUtils.OnClickSureOrCancelListener() {
                        @Override
                        public void clickSure() {
                            bean.setEffectiveDate(0L);
                            updateData();
                        }

                        @Override
                        public void clickCancel() {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            Long time = calendar.getTimeInMillis() / 1000 * 1000 + (12 * 60 * 60 * 1000);
                            bean.setEffectiveDate(time);
                            updateData();
                        }
                    });


                }
                break;
            default:

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateData() {
        model.updateAttendanceRules(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CODE1:
                //必须考勤人员/部门
                if (data != null) {
                    List<Member> list1 = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
                    List<Member> members = notMemberView.getMembers();
                    if (members != null && members.size() > 0) {
                        for (int i = 0; i < members.size(); i++) {
                            Iterator<Member> iterator = list1.iterator();
                            while (iterator.hasNext()) {
                                Member next = iterator.next();
                                if (next.getId() == members.get(i).getId()) {
                                    iterator.remove();
                                }
                            }
                        }
                    }
                    mustMemberView.setMembers(list1);
                }
                break;
            case Constants.REQUEST_CODE2:
                //非必须考勤人员/部门
                if (data != null) {
                    List<Member> list2 = (List<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
                    notMemberView.setMembers(list2);
                    List<Member> list3 = mustMemberView.getMembers();
                    if (list2 != null && list2.size() > 0) {
                        for (int i = 0; i < list2.size(); i++) {
                            Iterator<Member> iterator = list3.iterator();
                            while (iterator.hasNext()) {
                                Member next = iterator.next();
                                if (next.getId() == list2.get(i).getId()) {
                                    iterator.remove();
                                }
                            }
                        }
                        mustMemberView.setMembers(list3);
                    }
                }

                break;
            case Constants.REQUEST_CODE3:
                setResult(RESULT_OK);
                finish();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

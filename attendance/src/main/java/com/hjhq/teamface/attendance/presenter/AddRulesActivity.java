package com.hjhq.teamface.attendance.presenter;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.AddDateAdapter;
import com.hjhq.teamface.attendance.bean.AddDateBean;
import com.hjhq.teamface.attendance.bean.AddRulesBean;
import com.hjhq.teamface.attendance.bean.AttendanceGroupDetailBean;
import com.hjhq.teamface.attendance.bean.AttendanceTypeBean;
import com.hjhq.teamface.attendance.bean.AttendanceTypeListBean;
import com.hjhq.teamface.attendance.bean.BaseSelectBean;
import com.hjhq.teamface.attendance.bean.MemberOrDepartmentBean;
import com.hjhq.teamface.attendance.bean.TimeBean;
import com.hjhq.teamface.attendance.bean.WorkTimeListBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.utils.NotifyUtils;
import com.hjhq.teamface.attendance.views.AddRulesDelegate;
import com.hjhq.teamface.attendance.widget.SelectView;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CloneUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/add_attendance_rules", desc = "添加/编辑考勤规则")
public class AddRulesActivity extends ActivityPresenter<AddRulesDelegate, AttendanceModel> implements View.OnClickListener {

    RadioGroup mRadioGroup;
    //外勤打卡
    com.kyleduo.switchbutton.SwitchButton allowOutdoor;
    //人脸识别
    com.kyleduo.switchbutton.SwitchButton allowVeriface;
    //节假日自动排休
    com.kyleduo.switchbutton.SwitchButton autoArrange;


    LinearLayout llCanRoot;
    LinearLayout llCan11;
    LinearLayout llCan12;
    LinearLayout llCan21;
    LinearLayout llCan31;
    LinearLayout llCan32;
    //顶部班次说明
    TextView tvRulesHint;

    //固定班制
    SelectView selectView111;
    SelectView selectView112;
    SelectView selectView113;
    SelectView selectView114;
    SelectView selectView115;
    SelectView selectView116;
    SelectView selectView117;
    List<SelectView> selectViewList1 = new ArrayList<>();
    List<SelectView> selectViewList3 = new ArrayList<>();
    //排班制
    SelectView selectView121;
    SelectView selectView122;
    SelectView selectView211;
    //自由工时
    SelectView selectView311;
    SelectView selectView312;
    SelectView selectView313;
    SelectView selectView314;
    SelectView selectView315;
    SelectView selectView316;
    SelectView selectView317;
    ArrayList<BaseSelectBean> workTimeList = new ArrayList<>();
    ArrayList<BaseSelectBean> freeWorkTimeList = new ArrayList<>();
    List<BaseSelectBean> locationList = new ArrayList<>();
    List<BaseSelectBean> wifiList = new ArrayList<>();
    List<AddDateBean> mustAttendanceDays = new ArrayList<>();
    List<AddDateBean> notAttendanceDays = new ArrayList<>();
    private ArrayList<Member> mustMember = new ArrayList<>();
    private ArrayList<Member> notMember = new ArrayList<>();
    AddDateAdapter mAddDateAdapter1;
    AddDateAdapter mAddDateAdapter2;
    String[] weekdayArr;
    String[] optionArr;
    String[] hintArr;
    private int mode = 1;

    boolean isHaveRestTime = true;
    private int type;
    private String id;
    private String name;
    AttendanceGroupDetailBean.DataBean mDataBean;
    AddRulesBean updateBean = new AddRulesBean();
    AddRulesBean addBean = new AddRulesBean();

    @Override
    public void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString(Constants.DATA_TAG1);
            type = bundle.getInt(Constants.DATA_TAG2, AttendanceConstants.TYPE_ADD);
            if (!TextUtils.isEmpty(id)) {
                getDetailData();
            }
            mustMember = (ArrayList<Member>) bundle.getSerializable(Constants.DATA_TAG3);
            notMember = (ArrayList<Member>) bundle.getSerializable(Constants.DATA_TAG4);
            name = bundle.getString(Constants.DATA_TAG5);

        }
        weekdayArr = getResources().getStringArray(R.array.attendance_week_day_list);
        optionArr = getResources().getStringArray(R.array.attendance_rules_other_option_list);
        hintArr = getResources().getStringArray(R.array.attendance_add_rules_hint_list);
        AddDateBean bean1 = new AddDateBean();
        bean1.setName("上班");
        bean1.setId("");
        bean1.setType("1");
        bean1.setAttendance_time("");
        bean1.setLabel("");
        AddDateBean bean2 = new AddDateBean();
        bean2.setName("休息");
        bean2.setId("");
        bean2.setType("2");
        bean2.setAttendance_time("");
        bean2.setLabel("");

        freeWorkTimeList.add(bean1);
        freeWorkTimeList.add(bean2);
        initView();
        getNetData();
        initAdapter();
        //自由工时
        selectView311.setData(CloneUtils.clone(freeWorkTimeList));
        selectView311.setSelectedData(0);
        selectView312.setData(CloneUtils.clone(freeWorkTimeList));
        selectView312.setSelectedData(0);
        selectView313.setData(CloneUtils.clone(freeWorkTimeList));
        selectView313.setSelectedData(0);
        selectView314.setData(CloneUtils.clone(freeWorkTimeList));
        selectView314.setSelectedData(0);
        selectView315.setData(CloneUtils.clone(freeWorkTimeList));
        selectView315.setSelectedData(0);
        selectView316.setData(CloneUtils.clone(freeWorkTimeList));
        selectView316.setSelectedData(1);
        selectView317.setData(CloneUtils.clone(freeWorkTimeList));
        selectView317.setSelectedData(1);
    }

    private void initAdapter() {
        mAddDateAdapter1 = new AddDateAdapter(1, mustAttendanceDays);
        mAddDateAdapter2 = new AddDateAdapter(2, notAttendanceDays);
        viewDelegate.setAdapter1(mAddDateAdapter1);
        viewDelegate.setAdapter2(mAddDateAdapter2);
        viewDelegate.setItemClick1(new SimpleItemClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.delete) {
                    mustAttendanceDays.remove(position);
                    mAddDateAdapter1.notifyDataSetChanged();
                }
            }
        });
        viewDelegate.setItemClick2(new SimpleItemClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.delete) {
                    notAttendanceDays.remove(position);
                    mAddDateAdapter2.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 获取详情
     */
    private void getDetailData() {
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
        List<Member> poorPeoples = data.getAttendance_users();
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
        mode = TextUtil.parseInt(groupType) + 1;
        mRadioGroup.getChildAt(TextUtil.parseInt(groupType)).performClick();
        mRadioGroup.getChildAt(0).setEnabled(false);
        mRadioGroup.getChildAt(1).setEnabled(false);
        mRadioGroup.getChildAt(2).setEnabled(false);
        String holiday_auto_status = data.getHoliday_auto_status();
        autoArrange.setChecked("1".equals(holiday_auto_status));
        String face_status = data.getFace_status();
        allowVeriface.setChecked("1".equals(face_status));
        String outworker_status = data.getOutworker_status();
        allowOutdoor.setChecked("1".equals(outworker_status));
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
        updateBean.setName(groupName);
        updateBean.setAttendanceusers(poor);
        updateBean.setExcludedusers(rich);
        updateBean.setAttendanceType(groupType);
        updateBean.setWorkdaylist(workList);
        updateBean.setHolidayAutoStatus(TextUtil.parseInt(holiday_auto_status));
        autoArrange.setChecked(updateBean.getHolidayAutoStatus() == 1);
        updateBean.setMustPunchcardDate(must_punchcard_date);
        updateBean.setNoPunchcardDate(no_punchcard_date);
        updateBean.setAttendanceAddress(addressIds);
        updateBean.setAttendanceWifi(wifiIds);
        updateBean.setOutworkerStatus(TextUtil.parseInt(outworker_status));
        allowOutdoor.setChecked(updateBean.getOutworkerStatus() == 1);
        updateBean.setFaceStatus(face_status);
        updateBean.setId(id);
        updateBean.setAttendanceStartTime(data.getAttendance_start_time());
        mergeData(0);
    }

    /**
     * 获取班次/地址/wifi
     */
    private void getNetData() {
        //获取班次列表
        model.getAttendanceTimeList(mContext, new ProgressSubscriber<WorkTimeListBean>(mContext, false) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(WorkTimeListBean workTimeListBean) {
                super.onNext(workTimeListBean);
                workTimeList.clear();
                List<AddDateBean> dataList = workTimeListBean.getData().getDataList();
                if (dataList != null) {
                    workTimeList.addAll(dataList);
                    //固定班制
                    selectView111.setData(CloneUtils.clone(workTimeList));
                    selectView111.addRestDataItem();
                    selectView111.setSelectedData(1);
                    selectView112.setData(CloneUtils.clone(workTimeList));
                    selectView112.addRestDataItem();
                    selectView112.setSelectedData(1);
                    selectView113.setData(CloneUtils.clone(workTimeList));
                    selectView113.addRestDataItem();
                    selectView113.setSelectedData(1);
                    selectView114.setData(CloneUtils.clone(workTimeList));
                    selectView114.addRestDataItem();
                    selectView114.setSelectedData(1);
                    selectView115.setData(CloneUtils.clone(workTimeList));
                    selectView115.addRestDataItem();
                    selectView115.setSelectedData(1);
                    selectView116.setData(CloneUtils.clone(workTimeList));
                    selectView116.addRestDataItem();
                    selectView116.setSelectedData(0);
                    selectView117.setData(CloneUtils.clone(workTimeList));
                    selectView117.addRestDataItem();
                    selectView117.setSelectedData(0);
                    //排班制
                    selectView211.setData(CloneUtils.clone(workTimeList));
                    //selectView211.addRestDataItem();
                    selectView211.setSelectedData(0);
                    mergeData(1);
                }
            }
        });
        //获取考勤地址列表
        model.getAttendanceTypeList(mContext, 0, new ProgressSubscriber<AttendanceTypeBean>(mContext, false) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(AttendanceTypeBean attendanceTypeBean) {
                super.onNext(attendanceTypeBean);
                locationList.clear();
                List<AttendanceTypeListBean> dataList = attendanceTypeBean.getData().getDataList();
                if (dataList != null) {
                    locationList.addAll(dataList);
                    selectView121.setData(locationList);
                    mergeData(2);
                }
            }
        });
        //获取考勤WiFi列表
        model.getAttendanceTypeList(mContext, 1, new ProgressSubscriber<AttendanceTypeBean>(mContext, false) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(AttendanceTypeBean attendanceTypeBean) {
                super.onNext(attendanceTypeBean);
                wifiList.clear();
                List<AttendanceTypeListBean> dataList = attendanceTypeBean.getData().getDataList();
                if (dataList != null) {
                    wifiList.addAll(dataList);
                    selectView122.setData(wifiList);
                    mergeData(3);

                }
            }
        });

    }

    private void mergeData(int type) {
        if (mDataBean == null) {
            return;
        }
        switch (type) {
            case 0:
                mergeWorkList();
                mergeAddress();
                mergeWifi();
                break;
            case 1:
                mergeWorkList();
                break;
            case 2:
                mergeAddress();
                break;
            case 3:
                mergeWifi();
                break;
        }


    }

    private void mergeWorkList() {
        if (workTimeList == null || workTimeList.size() == 0) {
            return;
        }
        final List<Long> work_day_list = mDataBean.getWork_day_list();
        switch (mode) {
            case 1:
                final List<AddDateBean> must_punchcard_date = mDataBean.getMust_punchcard_date();
                mAddDateAdapter1.getData().clear();
                mAddDateAdapter1.getData().addAll(must_punchcard_date);
                mAddDateAdapter1.notifyDataSetChanged();
                final List<TimeBean> no_punchcard_date = mDataBean.getNo_punchcard_date();
                List<AddDateBean> noPunch = new ArrayList<>();
                if (no_punchcard_date != null && no_punchcard_date.size() > 0) {
                    for (int i = 0; i < no_punchcard_date.size(); i++) {
                        AddDateBean add = new AddDateBean();
                        add.setTime(no_punchcard_date.get(i).getTime() + "");
                        noPunch.add(add);
                    }
                }
                mAddDateAdapter2.getData().clear();
                mAddDateAdapter2.getData().addAll(noPunch);
                mAddDateAdapter2.notifyDataSetChanged();
                for (int i = 0; i < selectViewList1.size(); i++) {
                    mergeDayType1(selectViewList1.get(i), i);
                }
                break;
            case 2:
                mergeDayType2();
                break;
            case 3:
                for (int i = 0; i < selectViewList3.size(); i++) {
                    mergeDayType3(selectViewList3.get(i), i);
                }
                break;
        }


    }

    private void mergeDayType1(SelectView selectView, int index) {
        List<Long> work_day_list = mDataBean.getWork_day_list();
        List<BaseSelectBean> data = selectView.getDataList();
        if (work_day_list == null || work_day_list.size() < 7) {
            return;
        }
        long id = work_day_list.get(index);
        if (id == 0L) {
            selectViewList1.get(index).setSelectedData(0);
        } else {
            if (data != null && data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {

                   /* if (data.get(i) instanceof AttendanceTypeListBean) {
                        if (id == TextUtil.parseLong(((AttendanceTypeListBean) data.get(i)).getId(), -1L)) {
                            data.get(i).setCheck(true);
                            selectViewList1.get(index).setSelectedData(i);
                        }
                    }*/
                    if (data.get(i) instanceof AddDateBean) {
                        if (id == TextUtil.parseLong(((AddDateBean) data.get(i)).getId(), -1L)) {
                            data.get(i).setCheck(true);
                            selectViewList1.get(index).setSelectedData(i);
                        }
                    }
                }
            }
        }

    }

    private void mergeDayType2() {
        List<Long> work_day_list = mDataBean.getWork_day_list();
        List<BaseSelectBean> data = selectView121.getDataList();
        if (work_day_list == null || work_day_list.size() == 0) {
            return;
        }
        long id = work_day_list.get(0);


        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                if (id == TextUtil.parseLong(((AttendanceTypeListBean) data.get(i)).getId())) {
                    data.get(i).setCheck(true);
                    selectView121.setSelectedData(i);
                }
            }
        }

    }

    private void mergeDayType3(SelectView selectView, int index) {
        List<Long> work_day_list = mDataBean.getWork_day_list();
        List<BaseSelectBean> data = selectView.getDataList();
        if (work_day_list == null || work_day_list.size() < 7) {
            return;
        }
        long id = work_day_list.get(index);
        if (id == 0L) {
            selectViewList3.get(index).setSelectedData(0);
        } else {
            if (data != null && data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    if (id == TextUtil.parseLong(((AddDateBean) data.get(i)).getId())) {
                        data.get(i).setCheck(true);
                        selectViewList3.get(index).setSelectedData(i);
                    }
                }
            }
        }

        selectViewList3.get(index).setData(data);
    }

    private void mergeAddress() {
        if (locationList == null || locationList.size() == 0) {
            return;
        }
        List<AttendanceTypeListBean> attendance_address = mDataBean.getAttendance_address();
        if (attendance_address != null && attendance_address.size() > 0) {
            for (int i = 0; i < attendance_address.size(); i++) {
                for (int j = 0; j < locationList.size(); j++) {
                    if (attendance_address.get(i).getId().equals(((AttendanceTypeListBean) locationList.get(j)).getId())) {
                        locationList.get(j).setCheck(true);
                    }
                }
            }
        }
        selectView121.setData(locationList);

    }

    private void mergeWifi() {
        if (wifiList == null || wifiList.size() == 0) {
            return;
        }
        List<AttendanceTypeListBean> attendance_wifi = mDataBean.getAttendance_wifi();
        if (attendance_wifi != null && attendance_wifi.size() > 0) {
            for (int i = 0; i < attendance_wifi.size(); i++) {
                for (int j = 0; j < wifiList.size(); j++) {
                    if (attendance_wifi.get(i).getId().equals(((AttendanceTypeListBean) wifiList.get(j)).getId())) {
                        wifiList.get(j).setCheck(true);
                    }
                }
            }
        }
        selectView122.setData(wifiList);
    }


    private void initView() {
        mRadioGroup = viewDelegate.get(R.id.radio_group);
        tvRulesHint = viewDelegate.get(R.id.tv_rules_hint);
        TextUtil.setText(tvRulesHint, hintArr[mode - 1]);
        llCanRoot = viewDelegate.get(R.id.ll_can);
        llCan11 = viewDelegate.get(R.id.ll_can11);
        llCan12 = viewDelegate.get(R.id.ll_can12);
        llCan21 = viewDelegate.get(R.id.ll_can21);
        llCan31 = viewDelegate.get(R.id.ll_can31);
        llCan32 = viewDelegate.get(R.id.ll_can32);
        allowOutdoor = viewDelegate.get(R.id.sb11);
        allowVeriface = viewDelegate.get(R.id.sb12);
        autoArrange = viewDelegate.get(R.id.vb11);

        selectView111 = new SelectView(mContext, weekdayArr[0], SelectView.RADIO);
        selectView112 = new SelectView(mContext, weekdayArr[1], SelectView.RADIO);
        selectView113 = new SelectView(mContext, weekdayArr[2], SelectView.RADIO);
        selectView114 = new SelectView(mContext, weekdayArr[3], SelectView.RADIO);
        selectView115 = new SelectView(mContext, weekdayArr[4], SelectView.RADIO);
        selectView116 = new SelectView(mContext, weekdayArr[5], SelectView.RADIO);
        selectView117 = new SelectView(mContext, weekdayArr[6], SelectView.RADIO);
        llCan11.addView(selectView111);
        llCan11.addView(selectView112);
        llCan11.addView(selectView113);
        llCan11.addView(selectView114);
        llCan11.addView(selectView115);
        llCan11.addView(selectView116);
        llCan11.addView(selectView117);
        selectViewList1.add(selectView111);
        selectViewList1.add(selectView112);
        selectViewList1.add(selectView113);
        selectViewList1.add(selectView114);
        selectViewList1.add(selectView115);
        selectViewList1.add(selectView116);
        selectViewList1.add(selectView117);
        selectView121 = new SelectView(mContext, optionArr[1], SelectView.MULTI);
        selectView122 = new SelectView(mContext, optionArr[2], SelectView.MULTI);
        llCanRoot.addView(selectView121);
        llCanRoot.addView(selectView122);
        //选择班次
        selectView211 = new SelectView(mContext, optionArr[0], SelectView.RADIO);
        llCan21.addView(selectView211);
        //自由班次
        selectView311 = new SelectView(mContext, weekdayArr[0], SelectView.RADIO);
        selectView312 = new SelectView(mContext, weekdayArr[1], SelectView.RADIO);
        selectView313 = new SelectView(mContext, weekdayArr[2], SelectView.RADIO);
        selectView314 = new SelectView(mContext, weekdayArr[3], SelectView.RADIO);
        selectView315 = new SelectView(mContext, weekdayArr[4], SelectView.RADIO);
        selectView316 = new SelectView(mContext, weekdayArr[5], SelectView.RADIO);
        selectView317 = new SelectView(mContext, weekdayArr[6], SelectView.RADIO);
        llCan31.addView(selectView311);
        llCan31.addView(selectView312);
        llCan31.addView(selectView313);
        llCan31.addView(selectView314);
        llCan31.addView(selectView315);
        llCan31.addView(selectView316);
        llCan31.addView(selectView317);
        selectViewList3.add(selectView311);
        selectViewList3.add(selectView312);
        selectViewList3.add(selectView313);
        selectViewList3.add(selectView314);
        selectViewList3.add(selectView315);
        selectViewList3.add(selectView316);
        selectViewList3.add(selectView317);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SoftKeyboardUtils.hide(viewDelegate.get(R.id.et_name));
        addBean = new AddRulesBean();
        if (type == AttendanceConstants.TYPE_EDIT) {
            addBean = updateBean;
            addBean.setId(mDataBean.getId());
        } else if (type == AttendanceConstants.TYPE_ADD) {
            addBean.setName(name);
            List<MemberOrDepartmentBean> list = new ArrayList<>();
            if (mustMember != null) {
                for (int i = 0; i < mustMember.size(); i++) {
                    Member member = mustMember.get(i);
                    MemberOrDepartmentBean b = new MemberOrDepartmentBean();
                    b.setName(member.getName());
                    b.setId(member.getId() + "");
                    b.setType(member.getType() + "");
                    b.setPicture(member.getPicture());
                    b.setSign_id(member.getSign_id());
                    b.setValue(member.getType() + "-" + member.getId());
                    list.add(b);

                }
            }
            addBean.setAttendanceusers(list);
            List<MemberOrDepartmentBean> list2 = new ArrayList<>();
            if (notMember != null) {
                for (int i = 0; i < notMember.size(); i++) {
                    Member member = notMember.get(i);
                    MemberOrDepartmentBean b = new MemberOrDepartmentBean();
                    b.setName(member.getName());
                    b.setId(member.getId() + "");
                    b.setType(member.getType() + "");
                    b.setPicture(member.getPicture());
                    b.setSign_id(member.getSign_id());
                    b.setValue(member.getType() + "-" + member.getId());
                    list2.add(b);

                }
            }
            addBean.setExcludedusers(list2);
        }
        addBean.setHolidayAutoStatus(autoArrange.isChecked() ? 1 : 0);
        addBean.setAttendanceType((mode - 1) + "");
        addBean.setMustPunchcardDate(mustAttendanceDays);
        List<TimeBean> list3 = new ArrayList<>();
        for (int i = 0; i < notAttendanceDays.size(); i++) {
            TimeBean tb = new TimeBean();
            tb.setTime(TextUtil.parseLong(notAttendanceDays.get(i).getTime()));
            list3.add(tb);
        }
        addBean.setNoPunchcardDate(list3);
        addBean.setAutoStatus(autoArrange.isChecked() ? "1" : "0");
        addBean.setFaceStatus(allowVeriface.isChecked() ? "1" : "0");
        addBean.setOutworkerStatus(allowOutdoor.isChecked() ? 1 : 0);
        List<BaseSelectBean> locationList = selectView121.getSelectedData();
        List<BaseSelectBean> wifiList = selectView122.getSelectedData();
        List<Long> location = new ArrayList<>();
        List<Long> wifi = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            if (locationList.get(i).getItemType() == AttendanceConstants.TYPE_SELECT_LOCATION) {
                AttendanceTypeListBean lb = (AttendanceTypeListBean) locationList.get(i);
                location.add(TextUtil.parseLong(lb.getId()));
            }
        }
        for (int i = 0; i < wifiList.size(); i++) {
            if (wifiList.get(i).getItemType() == AttendanceConstants.TYPE_SELECT_WIFI) {
                AttendanceTypeListBean lb = (AttendanceTypeListBean) wifiList.get(i);
                wifi.add(TextUtil.parseLong(lb.getId()));
            }
        }
        addBean.setAttendanceAddress(location);
        addBean.setAttendanceWifi(wifi);
        addBean.setWorkdaylist(new ArrayList<>());
        List<AddDateBean> resultData = new ArrayList<>();
        //type   1上班,2休息,3必须打卡,4不用打卡
        switch (mode) {
            case 1:
                addBean.setAttendanceStartTime(viewDelegate.getStartTime());
                getResultData(mode, resultData);
                addBean.setWorkdaylist(getScheduleData(mode));
                break;
            case 2:
                addBean.setWorkdaylist(getScheduleData(mode));
                break;
            case 3:
                getResultData(mode, resultData);
                addBean.setWorkdaylist(getScheduleData(mode));
                addBean.setAttendanceStartTime(viewDelegate.getStartTime());
                break;
            default:
                break;
        }

        if (type == AttendanceConstants.TYPE_ADD) {
            addBean.setEffectiveDate(0L);
            addData(addBean);
        } else if (type == AttendanceConstants.TYPE_EDIT) {
            DialogUtils.getInstance().sureOrCancel(this, "", "选择规则生效时间", viewDelegate.getRootView(), "立即生效", "明日生效", new DialogUtils.OnClickSureOrCancelListener() {
                @Override
                public void clickSure() {
                    addBean.setEffectiveDate(0L);
                    updateData(addBean);
                }

                @Override
                public void clickCancel() {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    Long time = calendar.getTimeInMillis() / 1000 * 1000 + (12 * 60 * 60 * 1000);
                    addBean.setEffectiveDate(time);
                    updateData(addBean);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Long> getScheduleData(int mode) {
        List<Long> data = new ArrayList<>();
        switch (mode) {
            case 1:
                data.add(selectView111.getSingleSelectedValue());
                data.add(selectView112.getSingleSelectedValue());
                data.add(selectView113.getSingleSelectedValue());
                data.add(selectView114.getSingleSelectedValue());
                data.add(selectView115.getSingleSelectedValue());
                data.add(selectView116.getSingleSelectedValue());
                data.add(selectView117.getSingleSelectedValue());
                break;
            case 2:
                data.add(selectView121.getSingleSelectedValue());
                break;
            case 3:
                data.add(selectView311.getSingleSelectedValue());
                data.add(selectView312.getSingleSelectedValue());
                data.add(selectView313.getSingleSelectedValue());
                data.add(selectView314.getSingleSelectedValue());
                data.add(selectView315.getSingleSelectedValue());
                data.add(selectView316.getSingleSelectedValue());
                data.add(selectView317.getSingleSelectedValue());
                break;
        }
        return data;
    }

    private void addData(AddRulesBean bean) {
        model.addAttendanceRules(mContext, bean, new ProgressSubscriber<BaseBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(BaseBean baseBean) {
                super.onNext(baseBean);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void updateData(AddRulesBean bean) {
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

    private void getResultData(int mode, List<AddDateBean> resultData) {
        switch (mode) {
            case 1:
                for (int i = 0; i < selectViewList1.size(); i++) {
                    List<BaseSelectBean> list1 = selectViewList1.get(i).getDataList();
                    if (list1 != null && list1.size() > 0) {
                        AddDateBean bean11 = (AddDateBean) list1.get(0);
                        AddDateBean bean12 = new AddDateBean();
                        bean12.setType(bean11.getType());
                        bean12.setName(bean11.getName());
                        bean12.setLabel(bean11.getAttendance_time());
                        bean12.setId(bean11.getId());
                        bean12.setTime("");
                        resultData.add(bean12);
                    }
                }
                break;
            case 2:

                break;
            case 3:
                for (int i = 0; i < selectViewList3.size(); i++) {
                    List<BaseSelectBean> list1 = selectViewList3.get(i).getDataList();
                    if (list1 != null && list1.size() > 0) {
                        AddDateBean bean11 = (AddDateBean) list1.get(0);
                        AddDateBean bean12 = new AddDateBean();
                        bean12.setType(bean11.getType());
                        bean12.setName(bean11.getName());
                        bean12.setLabel(bean11.getAttendance_time());
                        bean12.setId(bean11.getId());
                        bean12.setTime("");
                        resultData.add(bean12);
                    }
                }
                break;
        }


    }

    @Override
    protected void bindEvenListener() {
        //自由工时选择开始时间
        viewDelegate.get(R.id.rl31).setOnClickListener(v -> {
            selectTime(R.id.rl31, viewDelegate.getStartTime());
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                SoftKeyboardUtils.hide(viewDelegate.get(R.id.et_name));
                if (checkedId == R.id.rb1) {
                    mode = 1;
                } else if (checkedId == R.id.rb2) {
                    mode = 2;
                } else if (checkedId == R.id.rb3) {
                    mode = 3;
                }
                TextUtil.setText(tvRulesHint, hintArr[mode - 1]);
                viewDelegate.switchType(mode);
            }
        });
        viewDelegate.get(R.id.tv122).setOnClickListener(v -> {
            NotifyUtils.showDateSelectMenu(mContext, viewDelegate.getRootView(), "", new NotifyUtils.OnDateSelectedListener() {
                @Override
                public void selected(long time) {
                    String[] menu = new String[workTimeList.size()];
                    for (int i = 0; i < workTimeList.size(); i++) {
                        menu[i] = ((AddDateBean) workTimeList.get(i)).getName() + "" + ((AddDateBean) workTimeList.get(i)).getClass_desc();

                    }
                    PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "班次选择", menu, new OnMenuSelectedListener() {
                        @Override
                        public boolean onMenuSelected(int p) {
                            AddDateBean bean = new AddDateBean();
                            bean.setName(((AddDateBean) workTimeList.get(p)).getName());
                            bean.setId(((AddDateBean) workTimeList.get(p)).getId());
                            bean.setClass_desc(((AddDateBean) workTimeList.get(p)).getClass_desc());
                            bean.setTime(time + "");
                            bean.setType("3");
                            mustAttendanceDays.add(bean);
                            mAddDateAdapter1.notifyDataSetChanged();
                            return false;
                        }
                    });
                }
            });
        });
        viewDelegate.get(R.id.tv142).setOnClickListener(v -> {
            NotifyUtils.showDateSelectMenu(mContext, viewDelegate.getRootView(), "", new NotifyUtils.OnDateSelectedListener() {
                @Override
                public void selected(long time) {
                    AddDateBean bean = new AddDateBean();
                    bean.setName("");
                    bean.setId("");
                    bean.setLabel("");
                    bean.setTime(time + "");
                    bean.setType("4");
                    notAttendanceDays.add(bean);
                    mAddDateAdapter2.notifyDataSetChanged();
                }
            });
        });
        super.bindEvenListener();
    }

    private void showRestTime(boolean isHaveRestTime) {
        /*if (isHaveRestTime) {
            viewDelegate.get(R.id.rl13).setVisibility(View.VISIBLE);
            viewDelegate.get(R.id.rl14).setVisibility(View.VISIBLE);
            viewDelegate.get(R.id.line4).setVisibility(View.VISIBLE);
            viewDelegate.get(R.id.line3).setVisibility(View.VISIBLE);
        } else {
            viewDelegate.get(R.id.rl13).setVisibility(View.GONE);
            viewDelegate.get(R.id.rl14).setVisibility(View.GONE);
            viewDelegate.get(R.id.line4).setVisibility(View.GONE);
            viewDelegate.get(R.id.line3).setVisibility(View.GONE);
        }*/

    }

    public void selectTime(int i, String time) {

        NotifyUtils.showTimeSelectMenu(mContext, viewDelegate.getRootView(), time, new NotifyUtils.OnTimeSelectedListener() {
            @Override
            public void selected(String time) {
                viewDelegate.setStartTime(i, time);

            }
        });
    }

    @Override
    public void onClick(View v) {
        SoftKeyboardUtils.hide(viewDelegate.get(R.id.et_name));
        /*switch (v.getId()) {
            case R.id.rl11:
                NotifyUtils.getInstance().showOperationNotify(mContext, 1, "上班时间: 09:00", "打卡时间: 09:00", viewDelegate.getRootView());
                break;
            case R.id.rl12:
                NotifyUtils.getInstance().showOperationNotify(mContext, 2, "下班时间: 09:00", "打卡时间: 09:00", viewDelegate.getRootView());
                break;
            case R.id.rl13:
                NotifyUtils.getInstance().showOperationNotify(mContext, 3, "迟到了明天早点来哦", "打卡时间: 09:00", viewDelegate.getRootView());
                break;
            case R.id.rl14:
                NotifyUtils.getInstance().showOperationNotify(mContext, 4, "早退了要跟领导说明下哦", "打卡时间: 09:00", viewDelegate.getRootView());
                break;
            case R.id.rl21:

                break;
            case R.id.rl22:

                break;
            case R.id.rl23:

                break;
            case R.id.rl24:

                break;
            case R.id.rl31:

                break;
            case R.id.rl32:

                break;
            case R.id.rl33:

                break;
            case R.id.rl34:

                break;
            case R.id.rl35:

                break;
            case R.id.rl36:

                break;
        }*/

    }
}

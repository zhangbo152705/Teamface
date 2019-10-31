package com.hjhq.teamface.attendance.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.AttendanceDayDataAdapter;
import com.hjhq.teamface.attendance.adapter.AttendanceDayTableAdapter;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;
import com.hjhq.teamface.attendance.bean.IconBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceDayDataFragmentDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.EmptyView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceDayDataFragment extends FragmentPresenter<AttendanceDayDataFragmentDelegate, AttendanceModel> {
    private AttendanceDayDataAdapter adapter;
    private AttendanceDayTableAdapter tableAdapter;
    private int type;
    private List<AttendanceMonthDataBean.DataBean.DataListBean> monthData = new ArrayList<>();
    private List<AttendanceDayDataBean.DataListBean> dayData = new ArrayList<>();
    private List<AttendanceMonthDataBean.DataBean.DataListBean> myData = new ArrayList<>();
    private List<IconBean> dataList = new ArrayList<>();
    private List<AttendanceDayDataBean.DataListBean> tableList = new ArrayList<>();
    Calendar mCalendar;
    Calendar mCalendarToday;
    EmptyView mEmptyView;
    String[] colors = {"#3895FE", "#3895FE", "#4F78D4", "#F05049", "#4DB27F", "#FE8239",
            "#398BA0", "#6A56DD", "#BA4BA8"};

    static AttendanceDayDataFragment newInstance(int type) {
        AttendanceDayDataFragment myFragment = new AttendanceDayDataFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, type);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        this.type = arguments.getInt(Constants.DATA_TAG1);
    }

    @Override
    protected void init() {
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        mCalendarToday = Calendar.getInstance();
        mCalendarToday.set(Calendar.HOUR_OF_DAY, 0);
        mCalendarToday.set(Calendar.MINUTE, 0);
        mCalendarToday.set(Calendar.SECOND, 0);
        mCalendarToday.set(Calendar.MILLISECOND, 0);
        mEmptyView = new EmptyView(getActivity());
        mEmptyView.setEmptyTitle("无数据");
        viewDelegate.setDate(type, mCalendar.getTimeInMillis());

        viewDelegate.get(R.id.header2).setVisibility(View.GONE);
        adapter = new AttendanceDayDataAdapter(dataList);
        viewDelegate.module_recycler.setAdapter(adapter);
        tableAdapter = new AttendanceDayTableAdapter(getActivity(), tableList);
        viewDelegate.tableView.setAdapter(tableAdapter);
        getDayData();
    }

    private void getDayData() {
        model.appDaydataList(((ActivityPresenter) getActivity()), getMillis(mCalendar), new ProgressSubscriber<AttendanceDayDataBean>(getActivity()) {
            @Override
            public void onNext(AttendanceDayDataBean attendanceDayDataBean) {
                super.onNext(attendanceDayDataBean);
                finishRefresh();
                dayData.clear();
                final ArrayList<AttendanceDayDataBean.DataListBean> dataList = attendanceDayDataBean.getData().getDataList();
                if (dataList != null && dataList.size() > 0) {
                    dayData.addAll(dataList);
                    viewDelegate.setNum(attendanceDayDataBean.getData().getAttendance_person_number());
                } else {
                    viewDelegate.setNum("0");
                }
                handleData();
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                finishRefresh();
            }
        });
    }
    /**
     * 处理当天考勤数据
     */
    public void handleData() {
        List<Float> list = new ArrayList<>();
        List<String> colorList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dayData)) {
            int totalNum = 0;
            for (int i = 0; i < dayData.size(); i++) {//0为打卡人数不参与圆环统计
                if (i != 0) {
                    totalNum = totalNum + dayData.get(i).getEmployeeList().size();
                }
            }
            tableList.clear();
            dataList.clear();
            for (int i = 0; i < dayData.size(); i++) {
                if (i != 0) {//0为打卡人数不参与圆环统计
                    //圆环统计
                    if (dayData.get(i).getEmployeeList() != null && totalNum > 0 && dayData.get(i).getEmployeeList().size() > 0) {
                        float sub = dayData.get(i).getEmployeeList().size() * 100;
                        float percent = Math.round(((dayData.get(i).getEmployeeList().size()) * 100.0 / totalNum));
                        list.add(percent);
                        if (colors.length>i){
                            colorList.add(colors[i]);
                        }else {
                            colorList.add(colors[0]);
                        }

                    }

                    //颜色样例
                    if (!TextUtil.isEmpty(dayData.get(i).getName())) {
                        IconBean colorIcon = new IconBean();
                        if (colors.length>i){
                            colorIcon.setColor(colors[i]);
                        }else {
                            colorIcon.setColor(colors[0]);
                        }

                        colorIcon.setName(dayData.get(i).getName());
                        dataList.add(colorIcon);
                    }

                }
                //1为正常打卡人数 不显示在表格里
                if (i != 1 && !TextUtil.isEmpty(dayData.get(i).getName()) && dayData.get(i).getEmployeeList() != null) {
                    tableList.add(dayData.get(i));
                }
            }
            //表格铺满整行
            int residueNum = tableList.size() % 3;
            if (residueNum > 0) {
                for (int j = 0; j < 3 - residueNum; j++) {
                    AttendanceDayDataBean.DataListBean resiIcon = new AttendanceDayDataBean.DataListBean();
                    resiIcon.setName("");
                    resiIcon.setNumber("");
                    tableList.add(tableList.size(), resiIcon);
                }
            }
            adapter.notifyDataSetChanged();
            tableAdapter.notifyDataSetChanged();
        }

        float total = 0;
        for (float item : list) {
            total = total + item;
        }

        if (!CollectionUtils.isEmpty(list)) {
            if (total < 100 && total>0) {
                float first = list.get(0) + (100 - total);
                list.set(0, first);
            }
        }
        viewDelegate.pie_view.updateData(list, colorList);
    }

    private void finishRefresh() {
        if (viewDelegate.mRefreshLayout != null) {
            viewDelegate.mRefreshLayout.finishRefresh();
        }
    }

    public void refreshData() {
        switch (type) {
            case 1:
                //日统计
                getDayData();
                break;
        }
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.rl_choose.setOnClickListener(v -> {
            switch (type) {
                case 1:
                    chooseDay();
                    break;
                default:

                    break;


            }
        });

        viewDelegate.tableView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                if (tableList.size() > position && !TextUtil.isEmpty(tableList.get(position).getName())) {
                    if ("0".equals(tableList.get(position).getType())) {
                        bundle.putSerializable(Constants.DATA_TAG1, tableList.get(position));
                        bundle.putLong(Constants.DATA_TAG2, mCalendar.getTimeInMillis());
                        CommonUtil.startActivtiy(getActivity(), AttendanceNumActivity.class, bundle);
                    } else {
                        bundle.putSerializable(Constants.DATA_TAG1, tableList.get(position));
                        bundle.putLong(Constants.DATA_TAG2, mCalendar.getTimeInMillis());
                        bundle.putInt(Constants.DATA_TAG3, TextUtil.parseInt(tableList.get(position).getType()));
                        CommonUtil.startActivtiy(getActivity(), DayActivity.class, bundle);
                    }
                }
            }
        });
        viewDelegate.mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
            }
        });
    }


    private void chooseDay() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mCalendar);
        bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM_DD);
        CommonUtil.startActivtiyForResult(getActivity(), DateTimeSelectPresenter.class, Constants.REQUEST_CODE7, bundle);
    }


    private Long getMillis(Calendar c) {
        if (c == null) {
            return 0L;
        }
        if (type == 2 || type == 3) {
            c.set(Calendar.DAY_OF_MONTH, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE7 && type == 1) {
            Calendar calendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (calendar.getTimeInMillis() >= mCalendarToday.getTimeInMillis() + (24 * 60 * 60 * 1000)) {
                ToastUtils.showError(getActivity(), "只能选择当前或之前的日期!");
            } else {
                mCalendar = calendar;
                viewDelegate.setDate(type, mCalendar.getTimeInMillis());
                getDayData();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

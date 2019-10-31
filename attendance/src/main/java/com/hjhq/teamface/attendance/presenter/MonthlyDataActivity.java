package com.hjhq.teamface.attendance.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.AttendanceCalendarAdapter;
import com.hjhq.teamface.attendance.adapter.TodayAttendanceAdapter2;
import com.hjhq.teamface.attendance.bean.DayItemBean;
import com.hjhq.teamface.attendance.bean.MonthDataItem;
import com.hjhq.teamface.attendance.bean.MonthlyDataBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.ViewMonthlyDataDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.EmptyView;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/view_monthly_data", desc = "打卡月历")
public class MonthlyDataActivity extends ActivityPresenter<ViewMonthlyDataDelegate, AttendanceModel> {
    AttendanceCalendarAdapter mAdapter;
    TodayAttendanceAdapter2 mAdaapter2;
    List<MonthDataItem> monthData = new ArrayList<>();
    List<DayItemBean> attendanceData = new ArrayList<>();
    Calendar currentCalendar;
    Calendar monthCalendar;
    int currnetMonth;
    int currentDay;
    int dayOfWeek;
    int dayNumOfMonth;
    EmptyView mEmptyView;

    @Override
    public void init() {
        TextView title = viewDelegate.get(R.id.tv_title);
        title.setText("打卡月历");
        viewDelegate.get(R.id.rl_header).setVisibility(View.GONE);
        viewDelegate.get(R.id.tv_detail).setVisibility(View.GONE);
        viewDelegate.get(R.id.rl_data).setVisibility(View.VISIBLE);
        viewDelegate.setSubTitle("考勤组:--");
        initView();
        initCalendar();
        getData();

    }

    private void initCalendar() {
        currentCalendar = Calendar.getInstance();
        currentCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
        dayNumOfMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1) {
            dayOfWeek = dayOfWeek + 6;
        } else {
            dayOfWeek = dayOfWeek - 1;
        }
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        currnetMonth = currentCalendar.get(Calendar.MONTH) + 1;
        monthCalendar = Calendar.getInstance();
        monthCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        viewDelegate.setMenuTexts(currentCalendar.get(Calendar.YEAR) + "." + (currentCalendar.get(Calendar.MONTH) + 1));
        monthCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
        monthCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        formatData();

    }

    private void getData() {
        attendanceData.clear();
        mAdaapter2.notifyDataSetChanged();
        model.getAppMonthDataBySelfForCalendar(mContext, getMillis(monthCalendar) + "", new ProgressSubscriber<MonthlyDataBean>(mContext) {
            @Override
            public void onNext(MonthlyDataBean baseBean) {
                super.onNext(baseBean);
                setData(baseBean.getData().getDateList());
                viewDelegate.setSubTitle("考勤组:" + (TextUtils.isEmpty(baseBean.getData().getGroupName()) ? "" : baseBean.getData().getGroupName()));
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void formatData() {
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        dayNumOfMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //Log.e("一周的第几天1", dayNumOfMonth + "");
        dayOfWeek = monthCalendar.get(Calendar.DAY_OF_WEEK);
        //Log.e("一周的第几天2", dayOfWeek + "");
        if (dayOfWeek == 1) {
            dayOfWeek = dayOfWeek + 6;
        } else {
            dayOfWeek = dayOfWeek - 1;
        }
        //Log.e("一周的第几天3", dayOfWeek + "");
        viewDelegate.setMenuTexts(monthCalendar.get(Calendar.YEAR) + "." + (monthCalendar.get(Calendar.MONTH) + 1));
        int monthDay = DateTimeUtil.getCurrentMonthDay(monthCalendar);
        monthData.clear();
        for (int i = 0; i < dayOfWeek - 1; i++) {
            MonthDataItem bean = new MonthDataItem();
            bean.setDate("");
            bean.setSelected(false);
            bean.setDataState(4);
            bean.setToday(false);
            monthData.add(bean);

        }

        for (int i = 0; i < monthDay; i++) {
            MonthDataItem bean = new MonthDataItem();
            bean.setDate(i + 1 + "");
            bean.setSelected(false);
            bean.setDataState(i % 3);
            bean.setToday(false);
            bean.setState("2");
            bean.setAttendanceList(new ArrayList<>());
            monthData.add(bean);
        }
    }

    private void setData(List<MonthDataItem> data) {
        if (data == null /*|| data.size() != monthData.size()*/) {
            ToastUtils.showError(mContext, "数据错误");
        } else {
            boolean sameMonth = false;
            if (monthCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) && monthCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)) {
                sameMonth = true;
            }
            for (int i = 0; i < data.size(); i++) {
                if (sameMonth && i + 1 == currentDay) {
                    data.get(i).setToday(true);
                } else {
                    data.get(i).setToday(false);
                }
                monthData.get(dayOfWeek - 1 + i).setAttendanceList(data.get(i).getAttendanceList());
                monthData.get(dayOfWeek - 1 + i).setSelected(false);
                monthData.get(dayOfWeek - 1 + i).setState(data.get(i).getState());
                monthData.get(dayOfWeek - 1 + i).setToday(data.get(i).isToday());

            }
            if (1 == 1) {
                monthData.get(dayOfWeek - 1).setSelected(true);
                if (monthData.get(dayOfWeek - 1).getAttendanceList() == null || monthData.get(dayOfWeek - 1).getAttendanceList().size() == 0) {
                    viewDelegate.noData();
                } else {
                    attendanceData.clear();
                    attendanceData.addAll(monthData.get(dayOfWeek - 1).getAttendanceList());
                    mAdaapter2.notifyDataSetChanged();
                }
            }
            mAdapter.notifyDataSetChanged();

        }

    }

    private Long getMillis(Calendar c) {
        if (c == null) {
            return 0L;
        }
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private void initView() {


        mAdapter = new AttendanceCalendarAdapter(monthData);
        mAdaapter2 = new TodayAttendanceAdapter2(attendanceData);
        mEmptyView = new EmptyView(mContext);
        mEmptyView.setEmptyImage(R.drawable.workbench_empty);
        mEmptyView.setEmptyTitle("无数据");
        mAdaapter2.setEmptyView(mEmptyView);
        viewDelegate.setAdapter2(mAdaapter2);
        viewDelegate.setAdapter(mAdapter);

    }


    @Override
    protected void bindEvenListener() {
        //选择月份
        viewDelegate.get(R.id.tv_month).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, currentCalendar);
            bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM);
            CommonUtil.startActivtiyForResult(mContext, DateTimeSelectPresenter.class, Constants.REQUEST_CODE2, bundle);
        });
        //返回
        viewDelegate.get(R.id.rl_back).setOnClickListener(v -> {
            finish();
        });
        viewDelegate.setItemClick(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (monthData.get(position).getDataState() == 4) {
                    return;
                }
                for (int i = 0; i < monthData.size(); i++) {
                    monthData.get(i).setSelected(false);
                }
                monthData.get(position).setSelected(true);
                mAdapter.notifyDataSetChanged();
                attendanceData.clear();
                attendanceData.addAll(monthData.get(position).getAttendanceList());
                mAdaapter2.notifyDataSetChanged();
                super.onItemClick(adapter, view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
            }
        });


        super.bindEvenListener();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE2) {
            if (data != null) {
                monthCalendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
                monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                viewDelegate.setMenuTexts(monthCalendar.get(Calendar.YEAR) + "." + (monthCalendar.get(Calendar.MONTH) + 1));
                formatData();
                getData();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}

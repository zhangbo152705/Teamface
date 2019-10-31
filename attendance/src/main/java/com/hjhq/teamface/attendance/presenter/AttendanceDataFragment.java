package com.hjhq.teamface.attendance.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.AttendanceDataListAdapter;
import com.hjhq.teamface.attendance.adapter.DayDataAdapter;
import com.hjhq.teamface.attendance.adapter.MyDataAdapter;
import com.hjhq.teamface.attendance.bean.AttendanceDayDataBean;
import com.hjhq.teamface.attendance.bean.AttendanceMonthDataBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceDataFragmentDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
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

public class AttendanceDataFragment extends FragmentPresenter<AttendanceDataFragmentDelegate, AttendanceModel> {
    private DayDataAdapter mAdapter1;
    private AttendanceDataListAdapter mAdapter2;
    private MyDataAdapter mAdapter3;
    private int type;
    private List<AttendanceMonthDataBean.DataBean.DataListBean> monthData = new ArrayList<>();
    private List<AttendanceDayDataBean.DataListBean> dayData = new ArrayList<>();
    private List<AttendanceMonthDataBean.DataBean.DataListBean> myData = new ArrayList<>();
    Calendar mCalendar;
    Calendar mCalendarToday;
    EmptyView mEmptyView;


    static AttendanceDataFragment newInstance(int type) {
        AttendanceDataFragment myFragment = new AttendanceDataFragment();
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

        switch (type) {
            case 1:
                //日统计
                viewDelegate.get(R.id.header2).setVisibility(View.GONE);
                mAdapter1 = new DayDataAdapter(dayData);
                mAdapter1.setEmptyView(mEmptyView);
                viewDelegate.setAdapter(mAdapter1);
                getDayData();
                break;
            case 2:
                //月统计
                viewDelegate.get(R.id.header2).setVisibility(View.GONE);
                mAdapter2 = new AttendanceDataListAdapter(monthData);
                mAdapter2.setEmptyView(mEmptyView);
                viewDelegate.setAdapter(mAdapter2);
                getMonthData();
                break;
            case 3:
                //我的
                viewDelegate.get(R.id.header2).setVisibility(View.VISIBLE);
                mAdapter3 = new MyDataAdapter(myData);
                mAdapter3.setEmptyView(mEmptyView);
                viewDelegate.setAdapter(mAdapter3);
                viewDelegate.setContent("打卡月历");
                viewDelegate.get(R.id.header1_text2).setOnClickListener(v -> {
                    CommonUtil.startActivtiy(getActivity(), MonthlyDataActivity.class);
                });
                getMyData();
                break;
        }

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
                    mAdapter1.notifyDataSetChanged();
                    viewDelegate.setNum(attendanceDayDataBean.getData().getAttendance_person_number());
                } else {
                    viewDelegate.setNum("0");
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                finishRefresh();
            }
        });

    }


    private void getMyData() {

        model.getAppMonthDataBySelf(((ActivityPresenter) getActivity()), getMillis(mCalendar) + "", new ProgressSubscriber<AttendanceMonthDataBean>(getActivity()) {
            @Override
            public void onNext(AttendanceMonthDataBean attendanceMonthDataBean) {
                super.onNext(attendanceMonthDataBean);
                myData.clear();
                myData.addAll(attendanceMonthDataBean.getData().getDataList());
                mAdapter3.notifyDataSetChanged();
                setMyData(attendanceMonthDataBean.getData().getGroupName());
                finishRefresh();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                finishRefresh();
            }
        });


    }

    private void finishRefresh() {
        if (viewDelegate.mRefreshLayout != null) {
            viewDelegate.mRefreshLayout.finishRefresh();
        }
    }

    private void setMyData(String groupName) {
        ImageView avatar = viewDelegate.get(R.id.avatar);
        TextView tvName = viewDelegate.get(R.id.text1);
        TextView tvInfo = viewDelegate.get(R.id.text2);
        String pic = SPHelper.getUserAvatar();
        String name = SPHelper.getUserName();
        ImageLoader.loadCircleImage(getActivity(), pic, avatar, name);
        TextUtil.setText(tvName, name);
        TextUtil.setText(tvInfo, "考勤组:" + (TextUtils.isEmpty(groupName) ? "" : groupName));

    }

    public void refreshData() {
        switch (type) {
            case 1:
                //日统计
                getDayData();
                break;
            case 2:
                //月统计
                getMonthData();
                break;
            case 3:
                //我的
                getMyData();
                break;
        }
    }


    public void getMonthData() {
        model.getAppMonthDataByAuth(((ActivityPresenter) getActivity()), getMillis(mCalendar) + "", new ProgressSubscriber<AttendanceMonthDataBean>(getContext()) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                finishRefresh();
            }

            @Override
            public void onNext(AttendanceMonthDataBean baseBean) {
                super.onNext(baseBean);
                finishRefresh();
                monthData.clear();
                if (baseBean.getData() != null && baseBean.getData().getDataList() != null) {
                    monthData.addAll(baseBean.getData().getDataList());
                    mAdapter2.notifyDataSetChanged();
                    viewDelegate.setNum(baseBean.getData().getAttendancePersonNumber());
                } else {
                    viewDelegate.setNum("0");
                }

            }
        });
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.rl_choose.setOnClickListener(v -> {
            switch (type) {
                case 1:
                    chooseDay();
                    break;
                case 2:
                    chooseMonth2();
                    break;
                case 3:
                    chooseMonth3();
                    break;
                default:

                    break;


            }
        });
        viewDelegate.setItemListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                Bundle bundle = new Bundle();
                switch (type) {
                    case 1:
                        List<AttendanceDayDataBean.DataListBean> data = mAdapter1.getData();
                        if ("0".equals(data.get(position).getType())) {
                            bundle.putSerializable(Constants.DATA_TAG1, data.get(position));
                            bundle.putLong(Constants.DATA_TAG2, mCalendar.getTimeInMillis());
                            CommonUtil.startActivtiy(getActivity(), AttendanceNumActivity.class, bundle);
                        } else {
                            bundle.putSerializable(Constants.DATA_TAG1, data.get(position));
                            bundle.putLong(Constants.DATA_TAG2, mCalendar.getTimeInMillis());
                            bundle.putInt(Constants.DATA_TAG3, TextUtil.parseInt(data.get(position).getType()));
                            CommonUtil.startActivtiy(getActivity(), DayActivity.class, bundle);
                        }

                        break;
                    case 2:
                        bundle.putSerializable(Constants.DATA_TAG1, monthData.get(position));
                        CommonUtil.startActivtiy(getActivity(), MonthActivity.class, bundle);
                        break;
                    case 3:
                        AttendanceMonthDataBean.DataBean.DataListBean dataListBean = myData.get(position);
                        ArrayList<AttendanceMonthDataBean.AttendanceListBean> attendanceList = dataListBean.getAttendanceList();
                        if (attendanceList != null && attendanceList.size() > 0) {
                            for (int i = 0; i < attendanceList.size(); i++) {
                                attendanceList.get(i).setType(TextUtil.parseInt(dataListBean.getType()));
                            }
                        }
                        bundle.putSerializable(Constants.DATA_TAG1, dataListBean);
                        bundle.putString(Constants.DATA_TAG2, DateTimeUtil.longToStr(mCalendar.getTimeInMillis(), "yyyy.MM"));
                        CommonUtil.startActivtiy(getActivity(), MyDataActivity.class, bundle);
                        break;
                }

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
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


    private void chooseMonth2() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mCalendar);
        bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM);
        CommonUtil.startActivtiyForResult(getActivity(), DateTimeSelectPresenter.class, Constants.REQUEST_CODE8, bundle);
    }

    private void chooseMonth3() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mCalendar);
        bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM);
        CommonUtil.startActivtiyForResult(getActivity(), DateTimeSelectPresenter.class, Constants.REQUEST_CODE9, bundle);
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
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE8 && type == 2) {
            Calendar calendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (calendar.getTimeInMillis() >= mCalendarToday.getTimeInMillis() + (24 * 60 * 60 * 1000)) {
                ToastUtils.showError(getActivity(), "只能选择当前或之前的日期!");
            } else {
                mCalendar = calendar;
                viewDelegate.setDate(type, mCalendar.getTimeInMillis());
                getMonthData();
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE9 && type == 3) {
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

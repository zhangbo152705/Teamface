package com.hjhq.teamface.attendance.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.RangeDataAdapter;
import com.hjhq.teamface.attendance.adapter.RangeListBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceDataFragmentDelegate;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.AttendanceConstants;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.ui.time.DateTimeSelectPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.EmptyView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceRangeFragment extends FragmentPresenter<AttendanceDataFragmentDelegate, AttendanceModel> {
    private RangeDataAdapter mAdapter;
    private int type;
    private Long groupId = null;
    private List<RangeListBean.DataBean.DataListBean> dataList = new ArrayList<>();
    Calendar mCalendar;
    Calendar mCalendarToday;
    private EmptyView mEmptyView;

    static AttendanceRangeFragment newInstance(int type) {
        AttendanceRangeFragment myFragment = new AttendanceRangeFragment();
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
        mAdapter = new RangeDataAdapter(type, dataList);

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
        viewDelegate.setAdapter(mAdapter);
        viewDelegate.setDate(type, mCalendar.getTimeInMillis());
        mEmptyView = new EmptyView(getActivity());
        mEmptyView.setEmptyImage(R.drawable.workbench_empty);
        mEmptyView.setEmptyTitle("无数据");
        mAdapter.setEmptyView(mEmptyView);
        switch (type) {
            case 1:
                //早到榜
                viewDelegate.get(R.id.header2).setVisibility(View.GONE);
                viewDelegate.setContent("按上班时间排序");

                break;
            case 2:
                //勤勉榜
                viewDelegate.get(R.id.header2).setVisibility(View.GONE);
                viewDelegate.setContent("按工作时长排序");

                break;
            case 3:
                //迟到榜
                viewDelegate.get(R.id.header2).setVisibility(View.GONE);
                viewDelegate.setContent("按迟到次数和时长排序");

                viewDelegate.get(R.id.header1_text2).setOnClickListener(v -> {
                    CommonUtil.startActivtiy(getActivity(), MonthlyDataActivity.class);
                });
                break;
        }
     //   viewDelegate.mRefreshLayout.setEnableRefresh(false);
        viewDelegate.mRefreshLayout.setEnableLoadMore(false);

    }

    public void setGroupid(Long id) {
        groupId = id;
        refreshData();
    }

    private void getDayData() {
        model.earlyArrivalList(((ActivityPresenter) getActivity()), getMillis(mCalendar) + "",
                groupId, new ProgressSubscriber<RangeListBean>(getActivity()) {
                    @Override
                    public void onNext(RangeListBean attendanceGroupListBean) {
                        super.onNext(attendanceGroupListBean);
                        dataList.clear();
                        dataList.addAll(attendanceGroupListBean.getData().getDataList());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });

    }

    public void getMonthData() {
        model.diligentList(((ActivityPresenter) getActivity()), getMillis(mCalendar) + "",
                groupId, new ProgressSubscriber<RangeListBean>(getActivity()) {
                    @Override
                    public void onNext(RangeListBean attendanceGroupListBean) {
                        super.onNext(attendanceGroupListBean);
                        dataList.clear();
                        dataList.addAll(attendanceGroupListBean.getData().getDataList());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void getMyData() {
        model.lateList(((ActivityPresenter) getActivity()), getMillis(mCalendar) + "",
                groupId, new ProgressSubscriber<RangeListBean>(getActivity()) {
                    @Override
                    public void onNext(RangeListBean attendanceGroupListBean) {
                        super.onNext(attendanceGroupListBean);
                        dataList.clear();
                        dataList.addAll(attendanceGroupListBean.getData().getDataList());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });

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

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean bean) {
        if (bean != null && AttendanceConstants.RULES_CHANGED.equals(bean.getTag())) {
            refreshData();
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
                case 2:
                    chooseMonth();
                    break;
                case 3:
                    chooseMonth();
                    break;
                default:

                    break;


            }
        });
        /*viewDelegate.setItemListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                CommonUtil.startActivtiy(getActivity(), AttendanceNumActivity.class);

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
            }
        });*/
        //zzh->add:新增下拉刷新数据功能
        viewDelegate. mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMoreWithNoMoreData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshData();
                refreshLayout.finishRefresh();
            }
        });

    }

    /**
     * 选择日期
     */
    private void chooseDay() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mCalendar);
        bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM_DD);
        CommonUtil.startActivtiyForResult(getActivity(), DateTimeSelectPresenter.class, Constants.REQUEST_CODE2, bundle);
    }

    /**
     * 选择月份
     */
    private void chooseMonth() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DateTimeSelectPresenter.CALENDAR, mCalendar);
        bundle.putString(DateTimeSelectPresenter.FORMAT, DateTimeSelectPresenter.YYYY_MM);
        CommonUtil.startActivtiyForResult(getActivity(), DateTimeSelectPresenter.class, Constants.REQUEST_CODE2, bundle);
    }

    private Long getMillis(Calendar c) {
        if (c == null) {
            return 0L;
        } else {
            if (type == 2 || type == 3) {
                c.set(Calendar.DAY_OF_MONTH, 1);
            }
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        return c.getTimeInMillis();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE2) {
            Calendar calendar = (Calendar) data.getSerializableExtra(DateTimeSelectPresenter.CALENDAR);
            if (calendar.getTimeInMillis() >= mCalendarToday.getTimeInMillis() + (24 * 60 * 60 * 1000)) {
                ToastUtils.showError(getActivity(), "只能选择当前或之前的日期!");
            } else {
                mCalendar = calendar;
                viewDelegate.setDate(type, mCalendar.getTimeInMillis());
                Log.e("onActivityResult","time:"+getMillis(mCalendar) + "");
                refreshData();
            }
        }else if (resultCode == Activity.RESULT_OK){
            refreshData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

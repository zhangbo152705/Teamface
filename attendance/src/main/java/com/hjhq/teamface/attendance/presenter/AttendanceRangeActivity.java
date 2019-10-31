package com.hjhq.teamface.attendance.presenter;

import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.bean.AttendanceGroupListBean;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.ViewAttendanceNumDelegate;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.popupwindow.OnMenuSelectedListener;
import com.hjhq.teamface.basis.util.popupwindow.PopUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/view_attendance_range", desc = "排行榜")
public class AttendanceRangeActivity extends ActivityPresenter<ViewAttendanceNumDelegate, AttendanceModel> implements View.OnClickListener {
    List<Fragment> mFragmentList = new ArrayList<>(2);
    String[] titleArray = new String[]{"早到榜", "勤勉榜", "迟到榜"};
    private List<AttendanceGroupListBean.DataBean.DataListBean> dataList = new ArrayList<>();
    private int index = 0;

    @Override
    public void init() {
        initView();
    }

    private void initView() {
        mFragmentList.add(AttendanceRangeFragment.newInstance(1));
        mFragmentList.add(AttendanceRangeFragment.newInstance(2));
        mFragmentList.add(AttendanceRangeFragment.newInstance(3));
        viewDelegate.initNavigator(titleArray, mFragmentList);
        viewDelegate.setTitle(R.string.attendance_view_range_title);
        viewDelegate.setRightMenuIcons(R.drawable.attendance_group_icon);
        viewDelegate.showMenu();
        getGroupData();
    }

    private void getGroupData() {
        model.queryGroupList(mContext, new ProgressSubscriber<AttendanceGroupListBean>(mContext) {
            @Override
            public void onNext(AttendanceGroupListBean attendanceGroupListBean) {
                super.onNext(attendanceGroupListBean);
                dataList.clear();
                dataList.addAll(attendanceGroupListBean.getData().getDataList());
                final String list_set_type = attendanceGroupListBean.getData().getList_set_type();
                //榜单统计方式（0分开、1一起）
                if ("0".equals(list_set_type)) {
                    if (dataList != null && dataList.size() > 0) {
                        for (int i = 0; i < mFragmentList.size(); i++) {
                            ((AttendanceRangeFragment) mFragmentList.get(i)).setGroupid(TextUtil.parseLong(dataList.get(0).getId()));
                        }
                        viewDelegate.showMenu(0);
                    } else {
                        /*for (int i = 0; i < mFragmentList.size(); i++) {
                            ((AttendanceRangeFragment) mFragmentList.get(i)).setGroupid(null);
                        }*/
                    }
                } else {
                    for (int i = 0; i < mFragmentList.size(); i++) {
                        ((AttendanceRangeFragment) mFragmentList.get(i)).setGroupid(null);
                    }
                    viewDelegate.showMenu();
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dataList == null || dataList.size() == 0) {
            return true;
        }
        showGroupMenu();
        return super.onOptionsItemSelected(item);
    }

    private void showGroupMenu() {
        String[] menu = new String[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            menu[i] = dataList.get(i).getName();
        }
        PopUtils.showBottomMenu(mContext, viewDelegate.getRootView(), "选择考勤组", menu, index, 0, new OnMenuSelectedListener() {
            @Override
            public boolean onMenuSelected(int p) {
                index = p;
                for (int i = 0; i < mFragmentList.size(); i++) {
                    ((AttendanceRangeFragment) mFragmentList.get(i)).setGroupid(TextUtil.parseLong(dataList.get(index).getId()));
                }
                return true;
            }
        });

    }

    @Override
    protected void bindEvenListener() {


        super.bindEvenListener();
    }


    @Override
    public void onClick(View v) {

    }
}

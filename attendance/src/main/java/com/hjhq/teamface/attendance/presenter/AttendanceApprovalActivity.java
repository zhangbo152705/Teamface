package com.hjhq.teamface.attendance.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.adapter.AttendanceApprovalAdapter;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.WorkTimeManageDelegate;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.bean.AttendanceApproveListBean;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/5.
 * Describe：
 */
@RouteNode(path = "/view_approval", desc = "关联审批单")
public class AttendanceApprovalActivity extends ActivityPresenter<WorkTimeManageDelegate, AttendanceModel> implements View.OnClickListener {
    AttendanceApprovalAdapter mAdapter;
    List<AttendanceApproveListBean.DataBean.DataListBean> dataList = new ArrayList<>();

    @Override
    public void init() {
        initView();
    }

    private void initView() {
        viewDelegate.setTitle(R.string.attendance_rules_manage_title);
        viewDelegate.setRightMenuTexts(R.color.app_blue, getString(R.string.add));
        mAdapter = new AttendanceApprovalAdapter(dataList);
        viewDelegate.setAdapter(mAdapter);
        viewDelegate.setItemListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, dataList.get(position).getId());
                CommonUtil.startActivtiyForResult(mContext, AddApprovalActivity.class, Constants.REQUEST_CODE1, bundle);

                super.onItemClick(adapter, view, position);
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.edit) {
                    ToastUtils.showToast(mContext, "修改" + position);
                } else if (view.getId() == R.id.delete) {
                    deleteData(position);
                }
                super.onItemChildClick(adapter, view, position);
            }
        });
        getNetData();
    }

    private void deleteData(int position) {
        DialogUtils.getInstance().sureOrCancel(mContext, "提示", "删除后，该类型审批将不会被考勤所统计，是否删除。", viewDelegate.getRootView(), new DialogUtils.OnClickSureListener() {
            @Override
            public void clickSure() {
                model.deleteApprove(mContext, dataList.get(position).getId(), new ProgressSubscriber<BaseBean>(mContext) {
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        dataList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
            }
        });


    }

    private void getNetData() {
        model.getAttendanceApprovalList(mContext, new ProgressSubscriber<AttendanceApproveListBean>(mContext) {
            @Override
            public void onNext(AttendanceApproveListBean attendanceApproveListBean) {
                super.onNext(attendanceApproveListBean);
                dataList.clear();
                dataList.addAll(attendanceApproveListBean.getData().getDataList());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CommonUtil.startActivtiyForResult(mContext, AddApprovalActivity.class, Constants.REQUEST_CODE1);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindEvenListener() {


        super.bindEvenListener();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && requestCode == Constants.REQUEST_CODE1) {
            getNetData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

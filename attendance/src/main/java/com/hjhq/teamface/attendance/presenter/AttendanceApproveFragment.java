package com.hjhq.teamface.attendance.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceApproveFragmentDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.adapter.AttendanceApprovalFrangmentAdapter;
import com.hjhq.teamface.common.bean.AttendanceApproveListBean;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.util.ArrayList;
import java.util.List;

public class AttendanceApproveFragment extends FragmentPresenter<AttendanceApproveFragmentDelegate, AttendanceModel> {


    private List<AttendanceApproveListBean.DataBean.DataListBean> dataList = new ArrayList<>();
    private AttendanceApprovalFrangmentAdapter adapter;
    static AttendanceApproveFragment newInstance(String tag) {
        AttendanceApproveFragment myFragment = new AttendanceApproveFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, tag);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
    }

    @Override
    protected void init() {
        adapter = new AttendanceApprovalFrangmentAdapter(dataList);
        viewDelegate.module_recycler.setAdapter(adapter);
        getNetData();
    }


    private void getNetData() {
        model.getAttendanceApprovalList(((ActivityPresenter) getActivity()), new ProgressSubscriber<AttendanceApproveListBean>(getActivity()) {
            @Override
            public void onNext(AttendanceApproveListBean attendanceApproveListBean) {
                super.onNext(attendanceApproveListBean);
                dataList.clear();
                dataList.addAll(attendanceApproveListBean.getData().getDataList());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.rl_approve_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MODULE_BEAN, getActivity().getResources().getString(R.string.attendance_approve_records));
                UIRouter.getInstance().openUri(getActivity(), "DDComp://app/approve/main", bundle);
            }
        });
        viewDelegate.module_recycler.addOnItemTouchListener(new SimpleItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                AttendanceApproveListBean.DataBean.DataListBean item = (AttendanceApproveListBean.DataBean.DataListBean) adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MODULE_BEAN,item.getRelevance_bean());
                bundle.putString(Constants.MODULE_ID, item.getId());
                bundle.putBoolean(Constants.DATA_TAG4, true);
                UIRouter.getInstance().openUri(getActivity(), "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }
}

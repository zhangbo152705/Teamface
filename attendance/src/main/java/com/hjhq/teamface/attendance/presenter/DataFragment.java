package com.hjhq.teamface.attendance.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.DataFragmentDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends FragmentPresenter<DataFragmentDelegate, AttendanceModel> {

    List<Fragment> mFragmentList = new ArrayList<>();

    static DataFragment newInstance(String tag) {
        DataFragment myFragment = new DataFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, tag);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mFragmentList.add(AttendanceDayDataFragment.newInstance(1));
        mFragmentList.add(AttendanceDataFragment.newInstance(2));
        mFragmentList.add(AttendanceDataFragment.newInstance(3));
    }

    @Override
    protected void init() {
        viewDelegate.setFragment(mFragmentList);
        viewDelegate.initNavigator(mFragmentList);
    }




    @Override
    protected void bindEvenListener() {


        super.bindEvenListener();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

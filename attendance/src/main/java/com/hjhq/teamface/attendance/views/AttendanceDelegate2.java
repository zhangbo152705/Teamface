package com.hjhq.teamface.attendance.views;

import android.widget.EditText;
import android.widget.TextView;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.zygote.AppDelegate;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：
 */

public class AttendanceDelegate2 extends AppDelegate {
    public TextView mEditText0;
    public EditText mEditText1;
    public EditText mEditText2;
    public EditText mEditText3;
    public EditText mEditText4;
    public EditText mEditText5;
    public EditText mEditText6;
    public EditText mEditText7;
    public EditText mEditText8;
    public EditText mEditText9;
    public EditText mEditText10;
    public TextView mTvInfo;

    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_activity2;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle("打卡");
        setRightMenuTexts(R.color.app_blue, "打卡");
        mEditText0 = get(R.id.et0);
        mEditText1 = get(R.id.et1);
        mEditText2 = get(R.id.et2);
        mEditText3 = get(R.id.et3);
        mEditText4 = get(R.id.et4);
        mEditText5 = get(R.id.et5);
        mEditText6 = get(R.id.et6);
        mEditText7 = get(R.id.et7);
        mEditText8 = get(R.id.et8);
        mEditText9 = get(R.id.et9);
        mEditText10 = get(R.id.et10);
        mTvInfo = get(R.id.info);
    }

}

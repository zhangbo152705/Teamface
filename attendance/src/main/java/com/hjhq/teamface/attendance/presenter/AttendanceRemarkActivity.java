package com.hjhq.teamface.attendance.presenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.hjhq.teamface.attendance.model.AttendanceModel;
import com.hjhq.teamface.attendance.views.AttendanceRemarkDelegate;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/view_attendance_remark", desc = "查看外勤打卡备注")
public class AttendanceRemarkActivity extends ActivityPresenter<AttendanceRemarkDelegate, AttendanceModel> {
    private String time;
    private String address;
    private String remark;
    private String url;

    @Override
    public void init() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            time = extras.getString(Constants.DATA_TAG1);
            address = extras.getString(Constants.DATA_TAG2);
            remark = extras.getString(Constants.DATA_TAG3);
            url = extras.getString(Constants.DATA_TAG4);
            viewDelegate.mTvAddress.setText(TextUtils.isEmpty(address) ? "" : address);
            viewDelegate.mTvTime.setText(DateTimeUtil.longToStr(TextUtil.parseLong(time), "HH:mm"));
            viewDelegate.mTvRemark.setText(TextUtils.isEmpty(remark) ? "" : remark);
            ImageLoader.loadRoundImage(mContext, url, viewDelegate.mIvPhoto);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }




}

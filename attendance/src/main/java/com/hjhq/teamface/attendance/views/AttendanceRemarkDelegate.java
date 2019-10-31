package com.hjhq.teamface.attendance.views;

import android.widget.ImageView;
import android.widget.TextView;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.zygote.AppDelegate;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：
 */

public class AttendanceRemarkDelegate extends AppDelegate {
    public TextView mTvTime;
    public TextView mTvAddress;
    public TextView mTvRemark;
    public ImageView mIvPhoto;

    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_remark_activity;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle("打卡备注");
        setRightMenuTexts(R.color.app_blue, "关闭");
        mTvTime = get(R.id.tv_time);
        mTvAddress = get(R.id.tv_address);
        mTvRemark = get(R.id.tv_remark);
        mIvPhoto = get(R.id.photo);

    }

}

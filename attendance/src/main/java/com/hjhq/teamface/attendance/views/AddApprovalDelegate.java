package com.hjhq.teamface.attendance.views;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hjhq.teamface.attendance.R;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;

/**
 * Created by Administrator on 2017/11/9.
 * Describe：
 */

public class AddApprovalDelegate extends AppDelegate {
    TextView tvUnit;
    TextView tvState;
    TextView tvTitle;
    TextView tvStartTime;
    TextView tvStopTime;
    TextView name4;
    public TextView etDuration;


    @Override
    public int getRootLayoutId() {
        return R.layout.attendance_add_approval_activity;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle(R.string.attendance_add_approval_title);
        setRightMenuTexts(R.color.app_blue, "保存");
        tvState = get(R.id.state_content);
        tvUnit = get(R.id.unit_content);
        tvTitle = get(R.id.type_content);
        tvStartTime = get(R.id.content4);
        tvStopTime = get(R.id.content5);
        etDuration = get(R.id.content3);
        name4 = get(R.id.name4);
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener, int... ids) {
        super.setOnClickListener(listener, ids);
    }

    public void setUnitText(String s) {
        tvUnit.setText(s);
    }

    public void setStateText(String s) {
        tvState.setText(s);
    }

    /**
     * 关联审批模块名/标题
     *
     * @param title
     */
    public void setApproveTitle(String title) {
        TextUtil.setText(tvTitle, title);
    }

    /**
     * 开始时间
     *
     * @param s
     */
    public void setStartTime(String s) {
        TextUtil.setText(tvStartTime, s);

    }

    /**
     * 结束时间
     *
     * @param s
     */
    public void setStopTime(String s) {
        TextUtil.setText(tvStopTime, s);

    }

    /**
     * 开始title
     *
     * @param s
     */
    public void setStartTimeTitle(String s) {
        TextUtil.setText(name4, s);

    }

    /**
     * 获取输入的时长
     *
     * @return
     */
    public String getDurationText() {
        return etDuration.getText().toString();
    }

    public void setDurationText(String text) {
        if (!TextUtils.isEmpty(text)) {
            etDuration.setText(text);
        } else {
            etDuration.setText("");
        }
    }

    public void setTime(String s) {
        TextUtil.setText(etDuration, s);
    }
}

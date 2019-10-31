package com.hjhq.teamface.project.ui;

import android.widget.TextView;

import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.project.R;

/**
 * 交接任务
 */

public class HandleTaskDelegate extends AppDelegate {
    private TextView mTvName;

    @Override
    public int getRootLayoutId() {
        return R.layout.project_handle_task_activity;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setTitle(R.string.project_handle_task);
        setRightMenuTexts(mContext.getString(R.string.confirm));
        mTvName = get(R.id.tv_content);

    }


    public void setChoosedMember(String employee_name) {
        TextUtil.setText(mTvName, employee_name);
    }
}

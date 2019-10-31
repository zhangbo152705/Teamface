package com.hjhq.teamface.filelib.view;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.filelib.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/**
 * Created by Administrator on 2018/8/13.
 * Describe：
 */

public class MoveFileDelegate extends AppDelegate {
    public SmartRefreshLayout mRefreshLayout;

    @Override
    public int getRootLayoutId() {
        return R.layout.filelib_move_file_activity;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setRightMenuTexts(R.color.app_blue, "确定");
        mRefreshLayout = get(R.id.refresh_layout);
    }
}

package com.hjhq.teamface.email.view;

import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.email.R;

public class SearchEmailDelegate extends AppDelegate {


    @Override
    public int getRootLayoutId() {
        return R.layout.email_search_email_activity;
    }

    @Override
    public boolean isToolBar() {
        return false;
    }

    @Override
    public void initWidget() {
        super.initWidget();

    }
}
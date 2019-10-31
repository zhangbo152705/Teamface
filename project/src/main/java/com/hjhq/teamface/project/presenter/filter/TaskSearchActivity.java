package com.hjhq.teamface.project.presenter.filter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.ui.filter.TaskSearchDelegate;

/**
 * 项目详情
 *
 * @author Administrator
 * @date 2018/4/12
 */

public class TaskSearchActivity extends ActivityPresenter<TaskSearchDelegate, ProjectModel> implements View.OnClickListener {
    public long projectId;
    public String projectName;
    public static String projectStatus;
    private FilterFragment mFilterFragment;
    private boolean initFragmentFlag = false;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            projectId = intent.getLongExtra(ProjectConstants.PROJECT_ID, 0);
            projectName = intent.getStringExtra(ProjectConstants.PROJECT_NAME);
            projectStatus = intent.getStringExtra(ProjectConstants.PROJECT_STATUS);
        }
        if (projectId == 0) {
            ToastUtils.showError(this, "项目ID为空");
            finish();
        }
    }

    @Override
    public void init() {
        initFilter();
        viewDelegate.setTitle(projectName);
    }

    private void getNetData() {
    }

    /**
     * 初始化筛选控件
     */
    public void initFilter() {
        mFilterFragment = FilterFragment.newInstance(1, projectId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer_content, mFilterFragment).commit();
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_task) {

        } else if (viewId == R.id.rl_share) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewDelegate.openDrawer();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!viewDelegate.closeDrawer()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (Constants.REQUEST_CODE8 == requestCode) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
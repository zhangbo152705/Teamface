package com.hjhq.teamface.project.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.ui.TaskDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合模版  项目集合、任务集合公用
 * Created by Administrator on 2018/4/10.
 */

public class ListTempFragment extends FragmentPresenter<TaskDelegate, ProjectModel> {
    static final String[] TITLES_PROJECT = new String[]{"全部", "我负责", "我参与", "我创建", "进行中", "已完成"};
    static final String[] TITLES_PERSONAL = new String[]{"全部", "我负责", "我参与", "我创建", "已完成"};
    List<Fragment> fragmentList = new ArrayList<>();
    private int index;

    static ListTempFragment newInstance(int index) {
        ListTempFragment myFragment = new ListTempFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, index);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            index = arguments.getInt(Constants.DATA_TAG1, 0);
        }
    }


    @Override
    protected void init() {
        fragmentList.clear();
        if (index == 0) {
            for (int i = 0; i < TITLES_PROJECT.length; i++) {
                ProjectListFragment projectListFragment = ProjectListFragment.newInstance(i);
                fragmentList.add(projectListFragment);
            }
        } else if (index == 1) {
            for (int i = 0; i < TITLES_PERSONAL.length; i++) {
                if (i == TITLES_PERSONAL.length - 1) {
                    AllTaskListFragmentNew projectListFragment = AllTaskListFragmentNew.newInstance(i + 1);
                    fragmentList.add(projectListFragment);
                } else {
                    AllTaskListFragmentNew projectListFragment = AllTaskListFragmentNew.newInstance(i);
                    fragmentList.add(projectListFragment);
                }

            }
        }

        viewDelegate.initNavigator(getChildFragmentManager(), index == 0 ? TITLES_PROJECT : TITLES_PERSONAL, fragmentList);
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof ProjectListFragment) {
                ProjectListFragment projectFragment = (ProjectListFragment) fragment;
                if (projectFragment.getActivity() == null) {
                    continue;
                }
                projectFragment.refreshData();
            } else if (fragment instanceof AllTaskListFragmentNew) {
                AllTaskListFragmentNew allTaskListFragment = (AllTaskListFragmentNew) fragment;
                if (allTaskListFragment.getActivity() == null) {
                    continue;
                }
                allTaskListFragment.refreshData();
            }
        }
    }

    public void search(String key) {
        int currentItem = viewDelegate.getCurrentItem();
        if (index == 0) {
            ((ProjectListFragment) fragmentList.get(currentItem)).search(key);
        }
    }

    public int getCurrentItem() {
        return viewDelegate.getCurrentItem();
    }
}

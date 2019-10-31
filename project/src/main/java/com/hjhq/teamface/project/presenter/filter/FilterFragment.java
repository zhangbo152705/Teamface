package com.hjhq.teamface.project.presenter.filter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.log.LogUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.adapter.FilterMainAdapter;
import com.hjhq.teamface.project.adapter.TaskFilterAdapter;
import com.hjhq.teamface.project.bean.ConditionBean;
import com.hjhq.teamface.project.bean.FilterDataBean;
import com.hjhq.teamface.project.bean.PersonalTaskConditionResultBean;
import com.hjhq.teamface.project.bean.TaskConditionResultBean;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.ui.filter.FilterDelegate;
import com.hjhq.teamface.project.ui.filter.weight.BaseFilterView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 筛选
 */

public class FilterFragment extends FragmentPresenter<FilterDelegate, ProjectModel> {
    /**
     * 0 个人任务筛选  1 项目任务筛选
     */
    private int fromType;
    private long projectId;
    private String beanName;
    private RxAppCompatActivity mActivity;
    private FilterMainAdapter mMainAdapter;
    private TaskFilterAdapter mAdapter;
    private List<FilterDataBean> childFilteDataList = new ArrayList<>();

    /**
     * 项目任务筛选
     *
     * @param projectId 项目ID
     * @return
     */
    public static FilterFragment newInstance(int fromType, long projectId) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.DATA_TAG1, fromType);
        bundle.putLong(ProjectConstants.PROJECT_ID, projectId);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            fromType = arguments.getInt(Constants.DATA_TAG1, 0);
            projectId = arguments.getLong(ProjectConstants.PROJECT_ID);
        }
    }

    @Override
    protected void init() {
        mActivity = (RxAppCompatActivity) getActivity();
        mMainAdapter = new FilterMainAdapter(null);
        mAdapter = new TaskFilterAdapter(childFilteDataList);
        viewDelegate.setAdapter(mAdapter);
        viewDelegate.setMainAdapter(mMainAdapter);
        initData();
    }

    private void initData() {
        if (fromType == 0) {
            //个人任务
            viewDelegate.hideMeSort();
            model.queryPersonalTaskConditions(mActivity, new ProgressSubscriber<PersonalTaskConditionResultBean>(mActivity, false) {
                @Override
                public void onNext(PersonalTaskConditionResultBean personalTaskConditionResultBean) {
                    super.onNext(personalTaskConditionResultBean);
                    List<FilterDataBean> list = new ArrayList<>();
                    FilterDataBean dataBean = new FilterDataBean();
                    dataBean.setBeanName("任务");
                    dataBean.setCondition(personalTaskConditionResultBean.getData().getCondition());
                    setTaskConditionType(dataBean.getCondition());
                    dataBean.setBean(ProjectConstants.PERSONAL_TASK_BEAN);
                    list.add(dataBean);
                    CollectionUtils.notifyDataSetChanged(mMainAdapter, mMainAdapter.getData(), list);
                }
            });
        } else if (fromType == 1) {
            model.queryProjectTaskConditions(mActivity, projectId, new ProgressSubscriber<TaskConditionResultBean>(mActivity, false) {
                @Override
                public void onNext(TaskConditionResultBean taskConditionResultBean) {
                    super.onNext(taskConditionResultBean);
                    List<FilterDataBean> taskConditionResultBeanData = taskConditionResultBean.getData();
                    if (!CollectionUtils.isEmpty(taskConditionResultBeanData)) {
                        for (FilterDataBean dataBean : taskConditionResultBeanData) {
                            final String bean = dataBean.getBean();
                            if (!TextUtils.isEmpty(bean)) {
                                if (bean.startsWith("memo")) {
                                    dataBean.setBeanName("备忘录");
                                } else if (bean.startsWith("project_custom_")) {
                                    dataBean.setBeanName("任务");
                                }
                            }

                            setTaskConditionType(dataBean.getCondition());
                        }
                    }
                    CollectionUtils.notifyDataSetChanged(mMainAdapter, mMainAdapter.getData(), taskConditionResultBeanData);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }
            });
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(v -> viewDelegate.switchCustomSort(), R.id.rl_custom_sort);
        viewDelegate.setOnClickListener(v -> viewDelegate.switchMeSort(), R.id.rl_me_sort);
        viewDelegate.setOnClickListener(v -> viewDelegate.customSortClick(v.getId()), R.id.rl_create_time_asc, R.id.rl_create_time_desc, R.id.rl_modify_time_asc, R.id.rl_modify_time_desc);
        viewDelegate.setOnClickListener(v -> viewDelegate.meSortClick(v.getId()), R.id.rl_responsible, R.id.rl_created, R.id.rl_participant);

        viewDelegate.setOnClickListener(v -> {
            String sortField = viewDelegate.getCustomSort();
            int timeSort = mAdapter.getTimeSort();
            Map<String, Object> map = new HashMap<>();
            if (timeSort >= 0) {
                map.put("dateFormat", timeSort);
            }
            JSONObject json = new JSONObject();
            JSONObject viewList = mAdapter.getViewList();
            Iterator<String> keys1 = viewList.keySet().iterator();
            try {
                while (keys1.hasNext()) {
                    String next = keys1.next();
                    JSONArray jsonArray = viewList.getJSONArray(next);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        BaseFilterView view = (BaseFilterView) jsonArray.get(i);
                        view.put(json);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LogUtil.e(json.toString());

            Set<String> keys = json.keySet();
            for (String key : keys) {
                Object o = json.get(key);
                map.put(key, o);
            }
            map.put("bean", beanName);
            map.put("queryType", viewDelegate.getCheck());

            if (fromType == 0) {
                EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PERSONAL_TASK_FILTER_CODE, sortField, map));
            } else {
                EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PROJECT_TASK_FILTER_CODE, sortField, map));
            }
        }, R.id.tv_confirm);

        viewDelegate.setOnClickListener(v -> {
            beanName = "";
            mMainAdapter.setIndex(-1);
            mMainAdapter.notifyDataSetChanged();
            childFilteDataList.clear();
            mAdapter.notifyDataSetChanged();
            mAdapter.reset();
            viewDelegate.reset();
            EventBusUtils.sendEvent(new MessageBean(ProjectConstants.PROJECT_TASK_FILTER_RESET_CODE, "", null));
        }, R.id.tv_reset);

        viewDelegate.mRecyclerViewMain.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                mMainAdapter.setIndex(position);
                mMainAdapter.notifyDataSetChanged();
                beanName = mMainAdapter.getData().get(position).getBean();
                childFilteDataList.clear();
                childFilteDataList.add(mMainAdapter.getData().get(position));
                mAdapter.notifyDataSetChanged();
            }
        });

        viewDelegate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.rl_responsible) {
                    viewDelegate.setCheck(0);
                } else if (v.getId() == R.id.rl_created) {
                    viewDelegate.setCheck(1);
                } else if (v.getId() == R.id.rl_participant) {
                    viewDelegate.setCheck(2);
                }

            }
        }, R.id.rl_responsible, R.id.rl_created, R.id.rl_participant);
    }


    private JSONObject getData() {

        return new JSONObject();
    }

    /**
     * 设置任务的条件类型
     *
     * @param list
     */
    private void setTaskConditionType(List<ConditionBean> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (ConditionBean conditionBean : list) {
            if (!TextUtil.isEmpty(conditionBean.getType())) {
                continue;
            }
            switch (conditionBean.getField()) {
                case ProjectConstants.PROJECT_TASK_DEADLINE:
                case ProjectConstants.PROJECT_TASK_STARTTIME:
                case "datetime_create_time":
                case "datetime_modify_time":
                    conditionBean.setType("datetime");
                    break;
                case ProjectConstants.PROJECT_TASK_EXECUTOR:
                case "personnel_create_by":
                case "personnel_modify_by":
                case "personnel_collaborator":
                    conditionBean.setType("personnel");
                    break;
                case "picklist_priority":
                case "picklist_difficulty":
                    conditionBean.setType("picklist");
                    break;
                case ProjectConstants.PROJECT_TASK_LABEL:
                case "tag":
                    conditionBean.setType("tag");
                    break;
                default:
                    conditionBean.setType("text");
                    break;
            }
        }

    }

    /**
     * 项目设置变更后获取新的筛选信息
     */
    public void getNewData() {
        initData();
    }

}

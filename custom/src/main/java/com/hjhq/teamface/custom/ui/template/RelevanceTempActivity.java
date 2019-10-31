package com.hjhq.teamface.custom.ui.template;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.ViewDataAuthResBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.AuthHelper;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.custom.adapter.DataTempAdapter;
import com.hjhq.teamface.custom.bean.FindAutoByBean;
import com.hjhq.teamface.custom.bean.TabListBean;
import com.hjhq.teamface.custom.bean.TabListDataReqBean;
import com.hjhq.teamface.custom.ui.add.AddCustomActivity;
import com.hjhq.teamface.custom.ui.detail.DataDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hjhq.teamface.basis.constants.CustomConstants.REQUEST_DETAIL_CODE;

/**
 * 关联模块列表控制器
 * Created by lx on 2017/8/31.
 */

public class RelevanceTempActivity extends ActivityPresenter<RelevantDataDelegate, DataTempModel> {

    private DataTempAdapter dataTempAdapter;
    private String title;
    private String mTatgetBean;
    private String mSourceBean;
    private long mDataId = -1L;
    private long rulesId = 0L;
    private int dataAuth = -1;
    private Integer conditionType;
    /**
     * 引用对象ID
     */
    private String relevanceObjectId;
    //关联组件的key
    private String fieldName;
    private Long moduleId;
    private Long tabId;
    //关联字段的值
    private String referenceFieldValue;
    private TabListDataReqBean mTabListDataReqBean = new TabListDataReqBean();
    private TabListBean.DataBean.DataListBean mData;
    private List<FindAutoByBean.DataBean.DataListBean> rulesList = new ArrayList<>();
    protected HashMap<String, Object> detailMap;


    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mData = (TabListBean.DataBean.DataListBean) bundle.getSerializable(Constants.DATA_TAG1);
                mDataId = TextUtil.parseLong(bundle.getString(Constants.DATA_TAG2));
                detailMap = (HashMap<String, Object>) bundle.getSerializable(Constants.DATA_TAG3);
                referenceFieldValue = bundle.getString(CustomConstants.FIELD_VALUE_TAG);
                fieldName = bundle.getString(Constants.FIELD_NAME);
            }

        } else {
            mData = (TabListBean.DataBean.DataListBean) savedInstanceState.getSerializable(Constants.DATA_TAG1);
            mDataId = TextUtil.parseLong(savedInstanceState.getString(Constants.DATA_TAG2));
            referenceFieldValue = savedInstanceState.getString(CustomConstants.FIELD_VALUE_TAG);
            fieldName = savedInstanceState.getString(Constants.FIELD_NAME);
        }
        if (mData != null) {
            mTatgetBean = mData.getTarget_bean();
            mSourceBean = mData.getSorce_bean();
            conditionType = TextUtil.parseInt(mData.getCondition_type());
            title = mData.getChinese_name();
            tabId = TextUtil.parseLong(mData.getId());
            moduleId = TextUtil.parseLong(mData.getModule_id());
           /* relevanceObjectId = savedInstanceState.getString(Constants.DATA_ID);
            fieldName = savedInstanceState.getString(Constants.FIELD_NAME);
            referenceFieldValue = savedInstanceState.getString(CustomConstants.FIELD_VALUE_TAG);*/

        }
        mTabListDataReqBean.setId(mDataId);
        mTabListDataReqBean.setSorceBean(mSourceBean);
        mTabListDataReqBean.setTargetBean(mTatgetBean);
        mTabListDataReqBean.setType(conditionType);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.DATA_TAG1, mData);
        outState.putString(Constants.DATA_TAG2, mDataId + "");
        outState.putString(Constants.FIELD_NAME, fieldName);
        outState.putString(CustomConstants.FIELD_VALUE_TAG, referenceFieldValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void init() {
        viewDelegate.setTitle(title);
        viewDelegate.setAdapter(dataTempAdapter = new DataTempAdapter(null));


        initMenu();
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        AuthHelper.getInstance().getModuleFunctionAuth(this, mTatgetBean, new AuthHelper.InitialDataCompleteListener() {
            @Override
            public void complete(ModuleFunctionBean moduleFunctionBean) {
                dataAuth = TextUtil.parseInt(moduleFunctionBean.getData().get(0).getData_auth(), 0);
                loadTempData(0L);
                boolean b = AuthHelper.getInstance().checkFunctionAuth(mTatgetBean, CustomConstants.ADD_NEW);
                if (b) {
                    viewDelegate.showMenu(0);
                }
            }

            @Override
            public void error() {

            }
        });
    }

    /**
     * 加载列表数据
     */
    private void loadTempData(Long rulesId) {

        model.getTabListData(this, dataAuth, mTabListDataReqBean, rulesId, moduleId, tabId, 20, 1, new ProgressSubscriber<DataTempResultBean>(this) {
            @Override
            public void onNext(DataTempResultBean baseBean) {
                super.onNext(baseBean);
                showDataResult(baseBean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void getRulesData() {
        model.findAutoByBean(this, mSourceBean, mTatgetBean, new ProgressSubscriber<FindAutoByBean>(this) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(FindAutoByBean findAutoByBean) {
                super.onNext(findAutoByBean);
                if (findAutoByBean != null && findAutoByBean.getData() != null
                        && findAutoByBean.getData().getDataList() != null) {
                    rulesList.clear();
                    rulesList.addAll(findAutoByBean.getData().getDataList());
                    if (rulesList.size() > 0) {
                        rulesId = rulesList.get(0).getId();
                    }
                    loadTempData(rulesId);
                }
            }
        });
    }

    /**
     * 显示数据结果
     *
     * @param baseBean
     */
    protected void showDataResult(DataTempResultBean baseBean) {
        DataTempResultBean.DataBean data = baseBean.getData();
        if (data != null) {
            List<DataTempResultBean.DataBean.DataListBean> dataList = data.getDataList();
            List<DataTempResultBean.DataBean.DataListBean> data1 = dataTempAdapter.getData();
            data1.clear();
            if (!CollectionUtils.isEmpty(dataList)) {
                data1.addAll(dataList);
            }
            dataTempAdapter.notifyDataSetChanged();
            viewDelegate.setSortInfo(data1.size());
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        addCustom();
        return super.onOptionsItemSelected(item);
    }

    private void viewLink() {


    }

    /**
     * 新增自定义
     */
    private void addCustom() {
        AuthHelper.getInstance().getModuleFunctionAuth(this, mTatgetBean, new AuthHelper.InitialDataCompleteListener() {
            @Override
            public void complete(ModuleFunctionBean moduleFunctionBean) {
                boolean b = AuthHelper.getInstance().checkFunctionAuth(mTatgetBean, CustomConstants.ADD_NEW);
                if (b) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MODULE_BEAN, mTatgetBean);

                    bundle.putString(Constants.MODULE_ID, moduleId + "");

                    HashMap<String, Object> map = new HashMap<>(1);
                    JSONArray jsonArray = new JSONArray();
                    Map<String, Object> map1 = new HashMap<>(2);
                    map1.put("id", mDataId + "");
                    map1.put("name", title);
                    jsonArray.add(map1);
                    map.put(fieldName, jsonArray);

                    bundle.putSerializable(Constants.DATA_TAG1, map);
                    bundle.putSerializable(Constants.DATA_TAG2, (Serializable) detailMap);
                    bundle.putString(Constants.DATA_TAG3, mSourceBean);
                    CommonUtil.startActivtiyForResult(RelevanceTempActivity.this, AddCustomActivity.class, CustomConstants.REQUEST_ADDCUSTOM_CODE, bundle);
                } else {
                    ToastUtils.showError(RelevanceTempActivity.this, "没有权限进行新增");
                    finish();
                }
            }

            @Override
            public void error() {
                ToastUtils.showError(RelevanceTempActivity.this, "获取权限失败！");
            }
        });
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                tempItemClick(adapter, view, position);
            }
        });
        viewDelegate.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (dataAuth == -1) {
                initMenu();
            } else {
                loadTempData(rulesId);
            }
            viewDelegate.mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    /**
     * 数据点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    protected void tempItemClick(BaseQuickAdapter adapter, View view, int position) {
        DataTempResultBean.DataBean.DataListBean item = (DataTempResultBean.DataBean.DataListBean) adapter.getItem(position);
        String id = item.getId().getValue();

        //模块权限判断
        AuthHelper.getInstance().queryDataAuth(this,
                mTatgetBean, id,
                new ProgressSubscriber<ViewDataAuthResBean>(this, false) {
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showError(RelevanceTempActivity.this, "权限请求失败!");
                    }

                    @Override
                    public void onNext(ViewDataAuthResBean baseBean) {
                        if (baseBean.getData() == null) {
                            ToastUtils.showError(RelevanceTempActivity.this, "无权限查看!");
                            return;
                        }
                        if ("1".equals(baseBean.getData().getReadAuth()) || "3".equals(baseBean.getData().getReadAuth())|| "4".equals(baseBean.getData().getReadAuth())|| "5".equals(baseBean.getData().getReadAuth())) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.MODULE_BEAN, mTatgetBean);
                            bundle.putString(Constants.MODULE_ID, moduleId + "");
                            bundle.putString(Constants.DATA_ID, id);
                            CommonUtil.startActivtiyForResult(RelevanceTempActivity.this, DataDetailActivity.class, CustomConstants.REQUEST_DETAIL_CODE, bundle);
                        } else if ("2".equals(baseBean.getData().getReadAuth())) {
                            ToastUtils.showError(RelevanceTempActivity.this, "数据已删除!");
                        } else if ("0".equals(baseBean.getData().getReadAuth())) {
                            ToastUtils.showError(RelevanceTempActivity.this, "无权限查看!");
                        } else {
                            ToastUtils.showError(RelevanceTempActivity.this, "无权限查看!");
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CustomConstants.REQUEST_ADDCUSTOM_CODE && resultCode == RESULT_OK) {
            init();
        } else if (requestCode == REQUEST_DETAIL_CODE && resultCode == RESULT_OK) {
            init();
        }
    }
}

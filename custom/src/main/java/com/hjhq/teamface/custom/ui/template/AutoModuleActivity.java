package com.hjhq.teamface.custom.ui.template;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.AuthHelper;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.DataTempAdapter;
import com.hjhq.teamface.custom.bean.FindAutoByBean;
import com.hjhq.teamface.custom.bean.TabListBean;
import com.hjhq.teamface.custom.bean.TabListDataReqBean;
import com.hjhq.teamface.custom.ui.add.AddCustomActivity;
import com.hjhq.teamface.custom.ui.detail.DataDetailActivity;
import com.hjhq.teamface.custom.ui.detail.DataDetailModel;
import com.hjhq.teamface.custom.utils.CustomPopUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hjhq.teamface.basis.constants.CustomConstants.REQUEST_DETAIL_CODE;


public class AutoModuleActivity extends ActivityPresenter<AutoModuleDelegate, DataTempModel> {

    private DataTempAdapter dataTempAdapter;
    private String title;
    //menu数据
    private List<FindAutoByBean.DataBean.DataListBean> menuList;
    private TabListDataReqBean mTabListDataReqBean = new TabListDataReqBean();
    private TabListBean.DataBean.DataListBean mData;
    //选中的menu 值
    protected long ruleId = -1L;

    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    private int state = Constants.NORMAL_STATE;
    private String sorceBean;
    private String targetBean;
    private String dataId;
    private int dataAuth = -1;
    private String moduleBean;
    private int conditionType;
    private int currentPosition = 0;
    private long moduleId;
    private long tabId;


    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            dataId = intent.getStringExtra(Constants.DATA_ID);
            sorceBean = intent.getStringExtra(Constants.DATA_TAG1);
            targetBean = intent.getStringExtra(Constants.DATA_TAG2);
            title = intent.getStringExtra(Constants.NAME);
            mData = (TabListBean.DataBean.DataListBean) intent.getSerializableExtra(Constants.DATA_TAG3);

        } else {
            dataId = savedInstanceState.getString(Constants.DATA_ID);
            sorceBean = savedInstanceState.getString(Constants.DATA_TAG1);
            targetBean = savedInstanceState.getString(Constants.DATA_TAG2);
            title = savedInstanceState.getString(Constants.NAME);
            mData = (TabListBean.DataBean.DataListBean) savedInstanceState.getSerializable(Constants.DATA_TAG3);
        }
        if (mData != null) {
            targetBean = mData.getTarget_bean();
            sorceBean = mData.getSorce_bean();
            conditionType = TextUtil.parseInt(mData.getCondition_type());
            title = mData.getChinese_name();
            mTabListDataReqBean.setId(TextUtil.parseLong(dataId));
            mTabListDataReqBean.setSorceBean(sorceBean);
            mTabListDataReqBean.setTargetBean(targetBean);
            mTabListDataReqBean.setType(conditionType);
        }
        moduleBean = targetBean;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.DATA_ID, dataId);
        outState.putString(Constants.DATA_TAG1, sorceBean);
        outState.putString(Constants.DATA_TAG2, targetBean);
        outState.putString(Constants.NAME, title);
        outState.putSerializable(Constants.DATA_TAG3, mData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void init() {
        viewDelegate.setTitle(title);
        initAdapter();
        initAuth();
    }


    /**
     * 初始化菜单
     */
    private void initAuth() {
        AuthHelper.getInstance().getModuleFunctionAuth(this, moduleBean, new AuthHelper.InitialDataCompleteListener() {
            @Override
            public void complete(ModuleFunctionBean moduleFunctionBean) {
                dataAuth = TextUtil.parseInt(moduleFunctionBean.getData().get(0).getData_auth(), 0);
                loadData();
            }

            @Override
            public void error() {

            }
        });
    }

    /**
     * 初始化适配器
     */
    protected void initAdapter() {
        if (dataTempAdapter == null) {
            dataTempAdapter = new DataTempAdapter(null);
            viewDelegate.setAdapter(dataTempAdapter);
        }
    }

    /**
     * 加载无筛选的数据
     */
    public void loadData() {
        new DataDetailModel().findAutoByBean(this, sorceBean, targetBean, new ProgressSubscriber<FindAutoByBean>(this) {
            @Override
            public void onNext(FindAutoByBean baseBean) {
                super.onNext(baseBean);
                menuList = baseBean.getData().getDataList();
                if (!CollectionUtils.isEmpty(menuList)) {
                    FindAutoByBean.DataBean.DataListBean dataBean = menuList.get(0);
                    String menuLabel = dataBean.getTitle();
                    ruleId = dataBean.getId();
                    moduleBean = dataBean.getEnglish_name();
                    viewDelegate.setSortInfo(menuLabel);
                    //loadTempData();
                    loadTempData(ruleId);
                }
            }
        });
    }

    /**
     * 加载列表数据
     */
    private void loadTempData() {
        new DataDetailModel().findAutoList(this, dataId, ruleId, sorceBean, targetBean, state == Constants.LOAD_STATE ? currentPageNo + 1 : 1, Constants.PAGESIZE, new ProgressSubscriber<DataTempResultBean>(this) {
            @Override
            public void onNext(DataTempResultBean baseBean) {
                super.onNext(baseBean);
                showDataResult(baseBean);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (state == Constants.LOAD_STATE) {
                    dataTempAdapter.loadMoreFail();
                }
            }
        });
    }

    /**
     * 加载列表数据
     */
    private void loadTempData(Long rulesId) {
        model.getTabListData(this, dataAuth, mTabListDataReqBean, rulesId,
                TextUtil.parseLong(mData.getModule_id()), TextUtil.parseLong(mData.getId()), 20,
                state == Constants.LOAD_STATE ? currentPageNo + 1 : 1,
                new ProgressSubscriber<DataTempResultBean>(this) {
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


    /**
     * 显示数据结果
     *
     * @param baseBean
     */
    protected void showDataResult(DataTempResultBean baseBean) {
        DataTempResultBean.DataBean data = baseBean.getData();

        List<DataTempResultBean.DataBean.DataListBean> dataList = data.getDataList();
        switch (state) {
            case Constants.NORMAL_STATE:
            case Constants.REFRESH_STATE:
                CollectionUtils.notifyDataSetChanged(dataTempAdapter, dataTempAdapter.getData(), dataList);
                break;
            case Constants.LOAD_STATE:
                dataTempAdapter.addData(dataList);
                dataTempAdapter.loadMoreComplete();
                break;
            default:
                break;
        }

        PageInfo pageInfo = data.getPageInfo();
        if (pageInfo != null) {
            totalPages = pageInfo.getTotalPages();
            currentPageNo = pageInfo.getPageNum();

            viewDelegate.setSortInfo(pageInfo.getTotalRows());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        addCustomData();

        return super.onOptionsItemSelected(item);
    }

    private void addCustomData() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, targetBean);
        //bundle.putString(Constants.MODULE_ID, moduleId);
        HashMap<String, Object> map = new HashMap<>(1);
        JSONArray jsonArray = new JSONArray();
        Map<String, Object> map1 = new HashMap<>(2);
        map1.put("id", dataId);
        map1.put("name", title);
        jsonArray.add(map1);
        //map.editPut(fieldName, jsonArray);
        bundle.putSerializable(Constants.DATA_TAG1, map);
        CommonUtil.startActivtiyForResult(AutoModuleActivity.this, AddCustomActivity.class, CustomConstants.REQUEST_ADDCUSTOM_CODE, bundle);


    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(v -> sortClick(), R.id.ll_sort);
        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                tempItemClick(adapter, view, position);
            }
        });
        //刷新
        viewDelegate.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            viewDelegate.mSwipeRefreshLayout.setRefreshing(false);
        });

        //加载更更多
        dataTempAdapter.setOnLoadMoreListener(() -> {
            if (currentPageNo >= totalPages) {
                dataTempAdapter.loadMoreEnd();
                return;
            }
            state = Constants.LOAD_STATE;
            loadData();
        }, viewDelegate.mRecyclerView);
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
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MODULE_BEAN, moduleBean);
        bundle.putString(Constants.DATA_ID, id);
        CommonUtil.startActivtiyForResult(this, DataDetailActivity.class, REQUEST_DETAIL_CODE, bundle);
    }


    /**
     * 分类选择 点击
     */
    protected void sortClick() {
        if (menuList != null && menuList.size() > 0) {
            List<String> list = new ArrayList<>();
            for (FindAutoByBean.DataBean.DataListBean dataBean : menuList) {
                list.add(dataBean.getTitle());
            }
            CustomPopUtil.showSortWindow(this,
                    viewDelegate.get(R.id.ll_sort),
                    list, currentPosition,
                    position -> {
                        currentPosition = position;
                        sortLoadData(position);
                        return true;
                    });
        }
    }

    /**
     * 排序点击加载数据
     *
     * @param position
     */
    protected void sortLoadData(int position) {
        FindAutoByBean.DataBean.DataListBean dataBean = menuList.get(position);
        ruleId = dataBean.getId();
        moduleBean = dataBean.getEnglish_name();
        viewDelegate.setSortInfo(dataBean.getTitle());
        refreshData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CustomConstants.REQUEST_DETAIL_CODE) {
            refreshData();
        }
    }

    private void refreshData() {
        state = Constants.REFRESH_STATE;
        if (dataAuth == -1) {
            initAuth();
        } else {
            loadTempData(ruleId);
        }
    }
}

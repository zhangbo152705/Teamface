package com.hjhq.teamface.custom.ui.template;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.DataListRequestBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.bean.TempMenuResultBean;
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
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.DataTempAdapter;
import com.hjhq.teamface.custom.ui.add.AddCustomActivity;
import com.hjhq.teamface.custom.ui.detail.DataDetailActivity;
import com.hjhq.teamface.custom.ui.filter.FilterFragment;
import com.hjhq.teamface.custom.utils.CustomPopUtil;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hjhq.teamface.basis.constants.CustomConstants.REQUEST_DETAIL_CODE;


/**
 * 数据列表控制器
 *
 * @author lx
 * @date 2017/8/31
 */

@RouteNode(path = "/temp", desc = "数据列表控制器")
public class DataTempActivity extends ActivityPresenter<DataTempDelegate, DataTempModel>
        implements View.OnClickListener {

    private DataTempAdapter dataTempAdapter;
    private String title;
    private String moduleBean;
    //menu数据
    private ArrayList<TempMenuResultBean.DataBean> menuList = new ArrayList<>();
    //选中的menu值
    protected String menuId;
    protected String menuIdFromWidget;
    protected String menuType;
    protected String menuCondition = "";
    private String moduleId;
    private int dataAuth = -1;

    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    private int state = Constants.NORMAL_STATE;

    //是否是公海池
    private boolean isPool = false;
    //是否是公海池管理员
    private boolean isPoolAdmin;
    //是否具有新增权限
    private boolean addAuth;
    private int currentPosition = 0;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            moduleId = intent.getStringExtra(Constants.MODULE_ID);
            title = intent.getStringExtra(Constants.NAME);
            menuIdFromWidget = intent.getStringExtra(Constants.DATA_TAG1);
            menuList = (ArrayList<TempMenuResultBean.DataBean>) intent.getSerializableExtra(Constants.DATA_TAG2);
        } else {
            moduleBean = savedInstanceState.getString(Constants.MODULE_BEAN);
            title = savedInstanceState.getString(Constants.NAME);
            moduleId = savedInstanceState.getString(Constants.MODULE_ID);
            menuIdFromWidget = savedInstanceState.getString(Constants.DATA_TAG1);
            menuList = (ArrayList<TempMenuResultBean.DataBean>) savedInstanceState.getSerializable(Constants.DATA_TAG2);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.MODULE_BEAN, moduleBean);
        outState.putString(Constants.NAME, title);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void init() {
        viewDelegate.setTitle(title);
        viewDelegate.initFilter(this, moduleBean);
        initAdapter();
        getModuleFunctionAuth();
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
        if (dataAuth == -1) {
            getModuleFunctionAuth();
            return;
        }
        if (menuList == null || menuList.size() == 0) {
            loadMenuData();
        } else {
            loadTempData(new HashMap<>(0));
        }
    }

    /**
     * 得到功能权限
     */
    public void getModuleFunctionAuth() {
        AuthHelper.getInstance().getModuleFunctionAuth(this, moduleBean, new AuthHelper.InitialDataCompleteListener() {
            @Override
            public void complete(ModuleFunctionBean moduleFunctionBean) {
                addAuth = AuthHelper.getInstance().checkFunctionAuth(moduleBean, CustomConstants.ADD_NEW);
                dataAuth = TextUtil.parseInt(moduleFunctionBean.getData().get(0).getData_auth(), 0);
                optionAddMenu();
                if (menuList != null) {
                    for (int i = 0; i < menuList.size(); i++) {
                        if (menuIdFromWidget.equals(menuList.get(i).getId())) {
                            TempMenuResultBean.DataBean dataBean = menuList.get(i);
                            String menuLabel = dataBean.getName();
                            menuId = dataBean.getId();
                            menuCondition = dataBean.getQuery_condition();
                            isPool = "1".equals(dataBean.getIs_seas_pool());
                            isPoolAdmin = "1".equals(dataBean.getIs_seas_admin());
                            optionAddMenu();
                            viewDelegate.setSortInfo(menuLabel);
                        }
                    }
                }
                loadData();
            }

            @Override
            public void error() {
                ToastUtils.showError(DataTempActivity.this, "获取权限失败，请退出重试");
                finish();
            }
        });
    }

    /**
     * 加载menu数据
     */
    private void loadMenuData() {
        model.getMenuList(this, moduleId, new ProgressSubscriber<TempMenuResultBean>(this) {
            @Override
            public void onNext(TempMenuResultBean baseBean) {
                super.onNext(baseBean);
                menuList = new ArrayList<TempMenuResultBean.DataBean>();
                menuList.addAll(baseBean.getData().getDefaultSubmenu());
                menuList.addAll(baseBean.getData().getNewSubmenu());
                if (menuList.size() > 0) {
                    if (TextUtils.isEmpty(menuIdFromWidget)) {
                        TempMenuResultBean.DataBean dataBean = menuList.get(0);
                        String menuLabel = dataBean.getName();
                        menuId = dataBean.getId();
                        menuCondition = dataBean.getQuery_condition();
                        isPool = "1".equals(dataBean.getIs_seas_pool());
                        isPoolAdmin = "1".equals(dataBean.getIs_seas_admin());
                        optionAddMenu();
                        viewDelegate.setSortInfo(menuLabel);
                        refreshData();
                    } else {
                        for (int i = 0; i < menuList.size(); i++) {
                            if (menuIdFromWidget.equals(menuList.get(i).getId())) {
                                TempMenuResultBean.DataBean dataBean = menuList.get(i);
                                currentPosition = i;
                                String menuLabel = dataBean.getName();
                                menuId = dataBean.getId();
                                menuCondition = dataBean.getQuery_condition();
                                isPool = "1".equals(dataBean.getIs_seas_pool());
                                isPoolAdmin = "1".equals(dataBean.getIs_seas_admin());
                                optionAddMenu();
                                viewDelegate.setSortInfo(menuLabel);
                                refreshData();
                            }
                        }

                    }

                }
            }
        });
    }

    /**
     * 加载列表数据
     *
     * @param map
     */
    private void loadTempData(Map<String, Object> map) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(state == Constants.LOAD_STATE ? currentPageNo + 1 : 1);
        pageInfo.setPageSize(Constants.PAGESIZE);

        DataListRequestBean bean = new DataListRequestBean();
        bean.setBean(moduleBean);
        if (isPool) {
            bean.setSeas_pool_id(menuId);
        } else {
            bean.setMenuId(menuId);
        }
        bean.setMenuCondition(menuCondition);
        bean.setMenuType(menuType);
        bean.setQueryWhere(map);
        bean.setPageInfo(pageInfo);
        bean.setDataAuth(dataAuth);
        model.getDataTemp(this, bean, new ProgressSubscriber<DataTempResultBean>(this) {
            @Override
            public void onNext(DataTempResultBean baseBean) {
                super.onNext(baseBean);
                dataTempAdapter.setSeasPwdFields(baseBean.getData().getSeasPwdFields());
                dataTempAdapter.setNeedConceal(isPool && !isPoolAdmin);
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

    public String getModuleBean() {
        return moduleBean;
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

        //关闭筛选
        viewDelegate.closeDrawer();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                viewLinkData();
                break;
            case 1:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MODULE_BEAN, moduleBean);
                bundle.putString(Constants.DATA_TAG1, menuId);
                CommonUtil.startActivtiyForResult(this, SearchActivity.class, CustomConstants.REQUEST_DETAIL_CODE, bundle);
                break;
            case 2:
                addCustom();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewLinkData() {
        //1、后台获取发布链接，2、非公海池，3公海池，4关联模块
        int source = 2;
        String seapoolId = "";
        if (isPool) {
            source = 3;
            seapoolId = menuId;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, moduleBean);
        bundle.putString(Constants.DATA_TAG2, seapoolId);
        bundle.putInt(Constants.DATA_TAG3, source);
        bundle.putString(Constants.DATA_TAG4, "");
        bundle.putString(Constants.DATA_TAG5, "");
        bundle.putString(Constants.DATA_TAG6, "");
        CommonUtil.startActivtiy(mContext, LinkDataActivity.class, bundle);
    }

    /**
     * 新增自定义
     */
    private void addCustom() {
        AuthHelper.getInstance().getModuleFunctionAuth(this, moduleBean, new AuthHelper.InitialDataCompleteListener() {
            @Override
            public void complete(ModuleFunctionBean moduleFunctionBean) {
                boolean b = AuthHelper.getInstance().checkFunctionAuth(moduleBean, CustomConstants.ADD_NEW);
                if (b) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MODULE_BEAN, moduleBean);
                    bundle.putString(Constants.MODULE_ID, moduleId);
                    if (isPool) {
                        bundle.putString(Constants.POOL, menuId);
                    }
                    CommonUtil.startActivtiyForResult(DataTempActivity.this, AddCustomActivity.class, CustomConstants.REQUEST_ADDCUSTOM_CODE, bundle);
                } else {
                    ToastUtils.showError(DataTempActivity.this, "没有权限进行新增");
                    finish();
                }
            }

            @Override
            public void error() {
                ToastUtils.showError(DataTempActivity.this, "获取权限失败！");
            }
        });
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(this, R.id.ll_sort, R.id.iv_filtrate, R.id.iv_approve);
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
        bundle.putString(Constants.MODULE_ID, moduleId);
        bundle.putString(Constants.DATA_ID, id);
        if (isPool) {
            bundle.putString(Constants.POOL, menuId);
            bundle.putBoolean(Constants.IS_POOL_ADMIN, isPoolAdmin);
        }
        CommonUtil.startActivtiyForResult(this, DataDetailActivity.class, REQUEST_DETAIL_CODE, bundle);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_sort) {
            sortClick();
        } else if (id == R.id.iv_filtrate) {
            viewDelegate.openDrawer();
        } else if (id == R.id.iv_approve) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.MODULE_BEAN, moduleBean);
            UIRouter.getInstance().openUri(mContext, "DDComp://app/approve/main", bundle);
        }
    }


    /**
     * 分类选择 点击
     */
    protected void sortClick() {
        if (menuList.size() > 0) {
            List<String> list = new ArrayList<>();
            for (TempMenuResultBean.DataBean dataBean : menuList) {
                list.add(dataBean.getName());
            }
            CustomPopUtil.showSortWindow(this,
                    viewDelegate.get(R.id.ll_sort),
                    list, currentPosition,
                    position -> {
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
        currentPosition = position;
        TempMenuResultBean.DataBean dataBean = menuList.get(position);
        menuCondition = dataBean.getQuery_condition();
        menuId = dataBean.getId();
        menuType = dataBean.getType();
        isPool = "1".equals(dataBean.getIs_seas_pool());
        isPoolAdmin = "1".equals(dataBean.getIs_seas_admin());
        optionAddMenu();
        viewDelegate.setSortInfo(dataBean.getName());
        refreshData();
    }


    /**
     * 操作新增菜单按钮
     */
    private synchronized void optionAddMenu() {
        if (isPool) {
            if (isPoolAdmin) {
                viewDelegate.showMenu(0, 1, 2);
            } else {
                viewDelegate.showMenu(0, 1);
            }
        } else if (addAuth) {
            viewDelegate.showMenu(0, 1, 2);
        } else {
            viewDelegate.showMenu(0, 1);
        }
    }


    @Override
    public void onBackPressed() {
        if (!viewDelegate.closeDrawer()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CustomConstants.REQUEST_ADDCUSTOM_CODE) {
            refreshData();
        } else if (requestCode == CustomConstants.REQUEST_DETAIL_CODE) {
            refreshData();
        }
    }

    private void refreshData() {
        state = Constants.REFRESH_STATE;
        loadData();
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBean messageBean) {
        int responseCode = messageBean.getCode();
        //筛选回调
        if (responseCode == FilterFragment.FILTER_DATA) {
            Object object = messageBean.getObject();
            if (object instanceof Map) {
                state = Constants.NORMAL_STATE;
                loadTempData((Map<String, Object>) object);
            }
        }
    }
}

package com.hjhq.teamface.custom.ui.select;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.DataListRequestBean;
import com.hjhq.teamface.basis.bean.DataTempResultBean;
import com.hjhq.teamface.basis.bean.ModuleBean;
import com.hjhq.teamface.basis.bean.ModuleFunctionBean;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.DataTempAdapter;
import com.hjhq.teamface.custom.ui.template.DataTempModel;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * 选择自定义数据列表控制器
 *
 * @author lx
 * @date 2017/8/31
 */

@RouteNode(path = "/select", desc = "选择自定义数据列表控制器")
public class SelectDataTempActivity extends ActivityPresenter<SelectDataTempDelegate, DataTempModel> {
    private DataTempAdapter dataTempAdapter;
    private String title;
    private String moduleBean;
    private String moduleId;
    private String icon_color;
    private String icon_type;
    private String icon_url;

    private int currentPageNo = 1;//当前页数
    private int totalPages = 1;//总页数
    private int state = Constants.NORMAL_STATE;
    private String searchKey;
    private int fromType = 0;
    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            moduleId = intent.getStringExtra(Constants.MODULE_ID);
            title = intent.getStringExtra(Constants.NAME);
            icon_color = intent.getStringExtra(Constants.DATA_TAG3);
            icon_type = intent.getStringExtra(Constants.DATA_TAG4);
            icon_url = intent.getStringExtra(Constants.DATA_TAG5);
            fromType = getIntent().getIntExtra(ProjectConstants.TASK_FROM_TYPE,0);
        }
    }


    @Override
    public void init() {
        viewDelegate.setTitle(title);
        initAdapter();
        loadData();
        viewDelegate.showMenus(fromType);
    }


    /**
     * 初始化适配器
     */
    protected void initAdapter() {
        if (dataTempAdapter == null) {
            dataTempAdapter = new DataTempAdapter(null);
            dataTempAdapter.setCheckType(true);
            viewDelegate.setAdapter(dataTempAdapter);
        }
    }

    /**
     * 加载无筛选的数据
     */
    public void loadData() {
        getModuleFunction();
    }

    /**
     * zzh->ad:将原loadData里代码移到getDataTemp方法里
     */
    public void getDataTemp(int dataAuth){
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(state == Constants.LOAD_STATE ? currentPageNo + 1 : 1);
        pageInfo.setPageSize(Constants.PAGESIZE);

        DataListRequestBean bean = new DataListRequestBean();
        bean.setBean(moduleBean);
        bean.setPageInfo(pageInfo);
        bean.setDataAuth(dataAuth);

        model.getDataTemp(this, bean, new ProgressSubscriber<DataTempResultBean>(this) {
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
     *  zzh->ad:增加获取权限数据
     */
    public void getModuleFunction(){
        model.getModuleFunction(this,moduleBean,new ProgressSubscriber<ModuleFunctionBean>(this){
            @Override
            public void onNext(ModuleFunctionBean moduleFunctionBean) {
                super.onNext(moduleFunctionBean);
               String dataAuth = moduleFunctionBean.getData().get(0).getData_auth();
                if (dataAuth != null && TextUtil.isInteger(dataAuth)){
                    int auth = Integer.parseInt(dataAuth);
                    getDataTemp(auth);
                }else{
                    getDataTemp(2);
                }
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
        totalPages = pageInfo.getTotalPages();
        currentPageNo = pageInfo.getPageNum();
        //搜索的字段
        RowBean operationInfo = data.getOperationInfo();
        if (operationInfo != null) {
            searchKey = operationInfo.getName();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0){
            Bundle bundle = new Bundle();
            bundle.putString(Constants.MODULE_BEAN, moduleBean);
            UIRouter.getInstance().openUri(mContext, "DDComp://custom/add", bundle, CustomConstants.REQUEST_ADDCUSTOM_CODE);
        }else if (item.getItemId() == 1){
            setCustomResult();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置自定义选择结果
     */
    private void setCustomResult() {
        List<DataTempResultBean.DataBean.DataListBean> list = dataTempAdapter.getData();
        ArrayList<DataTempResultBean.DataBean.DataListBean> dataList = new ArrayList();
        Observable.from(list).filter(DataTempResultBean.DataBean.DataListBean::isCheck).subscribe(dataList::add);
        if (CollectionUtils.isEmpty(dataList)) {
            ToastUtils.showToast(mContext, "请选择数据");
            return;
        }

        if (!CollectionUtils.isEmpty(dataList)) {
            Intent intent = new Intent();
            intent.putExtra(Constants.DATA_TAG1, dataList);
            intent.putExtra(Constants.MODULE_BEAN, moduleBean);
            intent.putExtra(Constants.NAME, title);
            intent.putExtra(Constants.MODULE_ID, moduleId);
            intent.putExtra(Constants.DATA_TAG3, icon_color);
            intent.putExtra(Constants.DATA_TAG4, icon_type);
            intent.putExtra(Constants.DATA_TAG5, icon_url);
            intent.putExtra(Constants.DATA_TAG_TYPE, 1);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setOnClickListener(v -> {
            /*if (TextUtil.isEmpty(searchKey)) {
                ToastUtils.showError(this, "未获取到搜索字段");
                return;
            }*/
            Bundle bundle = new Bundle();
            bundle.putString(Constants.DATA_TAG1, searchKey);
            bundle.putString(Constants.MODULE_BEAN, moduleBean);
            bundle.putString(Constants.NAME, title);
            CommonUtil.startActivtiyForResult(mContext, SearchDataTempActivity.class, Constants.REQUEST_CODE1, bundle);
        }, R.id.search_rl);

        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DataTempResultBean.DataBean.DataListBean item = dataTempAdapter.getItem(position);
                item.setCheck(!item.isCheck());
                dataTempAdapter.notifyItemChanged(position);
            }
        });
        //刷新
        viewDelegate.mSwipeRefreshLayout.setOnRefreshListener(() -> {
            state = Constants.REFRESH_STATE;
            loadData();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && Constants.REQUEST_CODE1 == requestCode) {
            setResult(RESULT_OK, data);
            finish();
        }else if(RESULT_OK == resultCode && requestCode ==CustomConstants.REQUEST_ADDCUSTOM_CODE){
            ModuleBean.DataBean moduleBean = (ModuleBean.DataBean) data.getSerializableExtra(Constants.DATA_TAG1);
            Intent intent = new Intent();
            intent.putExtra(Constants.DATA_TAG1,moduleBean);
            intent.putExtra(Constants.DATA_TAG_TYPE, 2);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}

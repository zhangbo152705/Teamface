package com.hjhq.teamface.custom.ui.template;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.PageInfo;
import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.CaptureActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.ReferenceTempAdapter;
import com.hjhq.teamface.custom.bean.RelationDataRequestBean;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 引用组件 列表
 *
 * @author xj
 * @date 2017/8/31
 */

@RouteNode(path = "/referenceTemp", desc = "引用组件 列表")
public class ReferenceTempActivity extends ActivityPresenter<ReferenceTempDelegate, DataTempModel> {
    protected int scan_code = 101;
    protected ReferenceTempAdapter dataTempAdapter;
    protected String keyValue;
    protected HashMap<String, Object> form;
    protected HashMap<String, Object> relyForm;
    private PageInfo mPageInfo;
    private int currentPageNum = 1;
    private int pageSize = Constants.PAGESIZE;
    private boolean isMulti = false;
    private String value = "";
    private String scanCode="";

    /**
     * 所在模块的bean
     */
    protected String moduleBean;
    /**
     * 关联查询字段 name
     */
    protected String referenceField;
    /**
     * 搜索框提示文字
     */
    protected String searchHint;
    //子表单的keyName
    private String subFormName;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            moduleBean = intent.getStringExtra(Constants.MODULE_BEAN);
            referenceField = intent.getStringExtra(Constants.REFERENCE_FIELD);
            subFormName = intent.getStringExtra(Constants.SUBFORM_NAME);
            Serializable serializableExtra = intent.getSerializableExtra(Constants.DATA_TAG1);
            searchHint = intent.getStringExtra(Constants.DATA_TAG2);
            isMulti = intent.getBooleanExtra(Constants.DATA_TAG7, false);
            relyForm = (HashMap<String, Object>) intent.getSerializableExtra(Constants.DATA_TAG8);


            if (serializableExtra != null) {
                form = (HashMap) serializableExtra;
               /* Set<String> set = form.keySet();
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String s = com.alibaba.fastjson.JSONObject.toJSONString(form.get(key));
                    try {
                        JSONObject jo = new JSONObject(s);
                        s = jo.optString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    form.editPut(key, s);
                }*/
            } else {
                form = new HashMap();
            }
            scanCode = intent.getStringExtra(Constants.REFERENCE_SCAN);
        }
        SoftKeyboardUtils.hide(this);
    }

    @Override
    public void init() {
        initAdapter();
        form.put(referenceField, "");

        viewDelegate.mRefreshLayout.setEnableLoadMore(false);
        if (isMulti) {
            viewDelegate.get(R.id.tv_cancel).setVisibility(View.VISIBLE);
            viewDelegate.mSearchBar.setCancelText("确定");
        } else {
            viewDelegate.get(R.id.tv_cancel).setVisibility(View.GONE);
            viewDelegate.mSearchBar.setCancelText("取消");
        }
        if (TextUtil.isEmpty(scanCode)){
            loadData(1);
        }else {
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.DATA_TAG1,3);
            CommonUtil.startActivtiyForResult(mContext, CaptureActivity.class,scan_code,bundle);
        }
    }

    /**
     * 初始化适配器
     */
    protected void initAdapter() {
        if (dataTempAdapter == null) {
            dataTempAdapter = new ReferenceTempAdapter(null);
            dataTempAdapter.setType(isMulti);
            viewDelegate.setAdapter(dataTempAdapter);
        }
    }

    /**
     * 加载数据
     */
    public void loadData(int pageNum) {
        RelationDataRequestBean bean = new RelationDataRequestBean();
        RelationDataRequestBean.PageInfo info = new RelationDataRequestBean.PageInfo();
        info.setPageNum(pageNum);
        info.setPageSize(pageSize);
        bean.setBean(moduleBean);
        bean.setSearchField(referenceField);
        bean.setForm(form);
        bean.setReylonForm(relyForm);
        bean.setSubform(subFormName);
        bean.setPageInfo(info);

        model.findRelationDataList(this, bean, new ProgressSubscriber<ReferDataTempResultBean>(this) {
            @Override
            public void onNext(ReferDataTempResultBean dataTempResultBean) {
                super.onNext(dataTempResultBean);
                if (TextUtil.isEmpty(scanCode)){
                    mPageInfo = dataTempResultBean.getData().getPageInfo();
                    currentPageNum = mPageInfo.getPageNum();
                    CollectionUtils.notifyDataSetChanged(dataTempAdapter, dataTempAdapter.getData(), dataTempResultBean.getData().getDataList());
                    if (mPageInfo.getTotalPages() > mPageInfo.getPageNum()) {
                        viewDelegate.mRefreshLayout.setEnableLoadMore(true);
                    } else {
                        viewDelegate.mRefreshLayout.setEnableLoadMore(false);
                    }
                }else {//zzh:新增通过扫码的值查询
                    if (dataTempResultBean.getData() != null && !CollectionUtils.isEmpty(dataTempResultBean.getData().getDataList())){
                        ReferDataTempResultBean.DataListBean item = dataTempResultBean.getData().getDataList().get(0);
                        if (item == null || item.getId() == null || item.getRow() == null) {
                            ToastUtils.showError(mContext, "数据错误！");
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra(Constants.DATA_TAG1, item);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else {
                        ToastUtils.showToast(mContext, getResources().getString(R.string.custom_no_reference_data));
                        finish();
                    }
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isMulti) {
                    dataTempAdapter.getItem(position).setCheck(!dataTempAdapter.getItem(position).isCheck());
                    dataTempAdapter.notifyDataSetChanged();
                } else {
                    ReferDataTempResultBean.DataListBean item = dataTempAdapter.getItem(position);
                    if (item == null || item.getId() == null || item.getRow() == null) {
                        ToastUtils.showError(mContext, "数据错误！");
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, item);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        viewDelegate.mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                viewDelegate.mRefreshLayout.finishLoadMore(3000);
                if (mPageInfo != null) {
                    int totalPages = mPageInfo.getTotalPages();
                    int pageNum = mPageInfo.getPageNum();
                    if (pageNum < totalPages) {
                        loadData(pageNum + 1);
                    } else {
                        ToastUtils.showToast(mContext, "无更多数据");
                    }
                }
            }
        });

        viewDelegate.setSearchBarHint(searchHint);
        viewDelegate.setSearchListener(new SearchBar.SearchListener() {
            @Override
            public void clear() {
            }

            @Override
            public void cancel() {
                if (!isMulti) {
                    finish();
                    return;
                }
                List<ReferDataTempResultBean.DataListBean> data = dataTempAdapter.getData();
                ArrayList<ReferDataTempResultBean.DataListBean> list = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isCheck()) {
                        list.add(data.get(i));
                    }
                }
                if (list.size() == 0) {
                    ToastUtils.showError(mContext, "未选择数据");
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG2, list);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void search() {
                if (TextUtils.isEmpty(keyValue)) {
                    ToastUtils.showError(mContext, "请输入搜索内容！");
                    return;
                }
                form.put(referenceField, keyValue);
                loadData(1);
            }

            @Override
            public void getText(String text) {
                keyValue = text;
                if (TextUtils.isEmpty(text)) {
                    List<ReferDataTempResultBean.DataListBean> data = dataTempAdapter.getData();
                    data.clear();
                    dataTempAdapter.notifyDataSetChanged();
                }
            }
        });
        viewDelegate.get(R.id.tv_cancel).setOnClickListener(v -> finish());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == scan_code && resultCode == Activity.RESULT_OK) {//扫一扫返回值
            String scanCode = data.getStringExtra(Constants.DATA_TAG1);
            if (!TextUtil.isEmpty(scanCode)){
                if (!TextUtil.isEmpty(scanCode)){
                    form.put("barcode_value",scanCode);
                }
                loadData(1);
            }
        }
    }
}

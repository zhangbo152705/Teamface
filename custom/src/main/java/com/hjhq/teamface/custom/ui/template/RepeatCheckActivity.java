package com.hjhq.teamface.custom.ui.template;

import android.os.Bundle;
import android.view.View;

import com.hjhq.teamface.basis.bean.ReferDataTempResultBean;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.adapter.ReferenceTempAdapter;
import com.hjhq.teamface.custom.bean.RepeatCheckResponseBean;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;


/**
 * 查重
 *
 * @author Administrator
 * @date 2017/9/5
 * Describe：搜索页
 */
@RouteNode(path = "/repeatCheck", desc = "查重")
public class RepeatCheckActivity extends ActivityPresenter<RepeatCheckDelegate, DataTempModel> {

    private String moduleBean;
    private String keyword;
    private ReferenceTempAdapter mAdapter2;
    private String searchKey;
    private String searchName;
    private String label;
    List<RepeatCheckResponseBean.DataBean> dataBeanList = new ArrayList<>();
    List<ReferDataTempResultBean.DataListBean> dataBeanList2 = new ArrayList<>();

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            moduleBean = getIntent().getStringExtra(Constants.MODULE_BEAN);
            keyword = getIntent().getStringExtra(Constants.DATA_TAG1);
            searchKey = getIntent().getStringExtra(Constants.DATA_TAG2);
            searchName = getIntent().getStringExtra(Constants.DATA_TAG3);
            label = getIntent().getStringExtra(Constants.DATA_TAG4);
        }
    }

    @Override
    public void init() {
        mAdapter2 = new ReferenceTempAdapter(dataBeanList2);
        View emptyView2 = mContext.getLayoutInflater().inflate(R.layout.view_workbench_empty, null);
        mAdapter2.setEmptyView(emptyView2);
        viewDelegate.setAdapter(mAdapter2);
        viewDelegate.setHintText("请输入" + searchName);
        viewDelegate.setSearchText(keyword);
        if (!TextUtil.isEmpty(keyword)) {
            repeatCheck();
        }
    }

    /**
     * 查重
     */
    public void repeatCheck() {
        if (TextUtil.isEmpty(keyword)) {
            ToastUtils.showError(this, "请输入" + searchName);
            return;
        }
        if (keyword.length() < 2) {
            ToastUtils.showError(this, "搜索内容不能少于两个字");
            return;
        }
        model.getRecheckingFields(this, moduleBean, searchKey, label, keyword, new ProgressSubscriber<RepeatCheckResponseBean>(this) {
            @Override
            public void onNext(RepeatCheckResponseBean baseBean) {
                super.onNext(baseBean);
                List<RepeatCheckResponseBean.DataBean> data = baseBean.getData();
                dataBeanList.clear();
                dataBeanList2.clear();
                dataBeanList.addAll(data);

                for (RepeatCheckResponseBean.DataBean bean : dataBeanList) {
                    ReferDataTempResultBean.DataListBean dataListBean = new ReferDataTempResultBean.DataListBean();
                    final ArrayList<RowBean> row = bean.getRow();
                    for (RowBean rowBean : row) {
                        rowBean.setHidden("0");
                    }
                    dataListBean.setRow(row);
                    dataBeanList2.add(dataListBean);
                }
                mAdapter2.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        ((SearchBar) viewDelegate.get(R.id.search_bar)).setSearchListener(new SearchBar.SearchListener() {
            @Override
            public void cancel() {
                finish();
            }

            @Override
            public void clear() {
                viewDelegate.setSearchText("");
            }

            @Override
            public void getText(String text) {
                keyword = text;
            }

            @Override
            public void search() {
                repeatCheck();
            }
        });
    }

}

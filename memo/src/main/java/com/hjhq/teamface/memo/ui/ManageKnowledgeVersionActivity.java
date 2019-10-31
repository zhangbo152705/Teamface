package com.hjhq.teamface.memo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.DataListDelegate;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.adapter.KnowledgeVersionAdapter;
import com.hjhq.teamface.memo.bean.VersionListBean;
import com.luojilab.router.facade.annotation.RouteNode;

import java.util.ArrayList;
import java.util.List;

@RouteNode(path = "/manage_knowledge_version", desc = "知识版本")
public class ManageKnowledgeVersionActivity extends ActivityPresenter<DataListDelegate, MemoModel> {
    private KnowledgeVersionAdapter mAdapter;
    private List<VersionListBean.VersionBean> versionDataList = new ArrayList<>();
    private String dataId;
    private String currentVersion;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState != null) {
            currentVersion = savedInstanceState.getString(Constants.DATA_TAG1);
            dataId = savedInstanceState.getString(Constants.DATA_TAG2);
        } else {
            currentVersion = getIntent().getExtras().getString(Constants.DATA_TAG1);
            dataId = getIntent().getExtras().getString(Constants.DATA_TAG2);
        }
    }

    @Override
    public void init() {
        viewDelegate.setRightMenuTexts(R.color.app_blue, getResources().getString(R.string.confirm));
        mAdapter = new KnowledgeVersionAdapter(versionDataList);
        viewDelegate.setAdapter(mAdapter);
        getData();
    }

    private void getData() {
        model.getRepositoryVersions(mContext, TextUtil.parseLong(dataId), new ProgressSubscriber<VersionListBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(VersionListBean baseBean) {
                super.onNext(baseBean);
                versionDataList.clear();
                versionDataList.addAll(baseBean.getData());
                if (TextUtils.isEmpty(currentVersion)) {
                    if (versionDataList.size() > 0) {
                        versionDataList.get(0).setCheck(true);
                    }
                } else {
                    for (VersionListBean.VersionBean bean : versionDataList) {
                        if (currentVersion.equals(bean.getId())) {
                            bean.setCheck(true);
                            break;
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (VersionListBean.VersionBean bean : versionDataList) {
            if (bean.isCheck()) {
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, bean.getId());
                intent.putExtra(Constants.DATA_TAG2, bean.getContent());
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.setItemClickListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                for (VersionListBean.VersionBean bean : versionDataList) {
                    bean.setCheck(false);
                }
                versionDataList.get(position).setCheck(true);
                mAdapter.notifyDataSetChanged();
            }
        });


    }

    /**
     * 选择知识分类
     */
    private void chooseCatg() {
        CommonUtil.startActivtiyForResult(mContext, ChooseCatgActivity.class, Constants.REQUEST_CODE2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) {
            return;
        }

        switch (requestCode) {
            case Constants.REQUEST_CODE2:
                //选择知识分类
                break;
            case Constants.REQUEST_CODE3:
                //选择知识标签
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}

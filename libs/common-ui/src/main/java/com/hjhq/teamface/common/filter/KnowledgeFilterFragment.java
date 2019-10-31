package com.hjhq.teamface.common.filter;

import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.KnowledgeClassBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.basis.zygote.FragmentPresenter;
import com.hjhq.teamface.common.CommonModel;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.adapter.KnowledgeCatgAdapter;
import com.hjhq.teamface.common.adapter.KnowledgeFiexdCatgAdapter;
import com.hjhq.teamface.common.bean.KnowledgeClassListBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnowledgeFilterFragment extends FragmentPresenter<KonwledgeFilterDelegate, CommonModel> {
    private int mType = 0;
    private KnowledgeCatgAdapter mAdapter;
    private KnowledgeFiexdCatgAdapter mFixedAdapter;
    private List<KnowledgeClassBean> dataList = new ArrayList<>();
    private List<KnowledgeClassBean> fixedList = new ArrayList<>();
    private String[] mainMenu;

    @Override
    protected void init() {
        mAdapter = new KnowledgeCatgAdapter(dataList);
        mFixedAdapter = new KnowledgeFiexdCatgAdapter(fixedList);
        viewDelegate.setFixedAdapter(mFixedAdapter);
        viewDelegate.setAdapter(mAdapter);
        getFilterData();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * 得到筛选数据
     */
    public void getFilterData() {
        mainMenu = getResources().getStringArray(R.array.knowledge_main_catg_array);
        for (int i = 0; i < mainMenu.length; i++) {
            KnowledgeClassBean bean = new KnowledgeClassBean();
            bean.setId(i + "");
            bean.setName(mainMenu[i]);
            fixedList.add(bean);
        }
        mFixedAdapter.notifyDataSetChanged();
        model.getRepositoryClassificationList(((ActivityPresenter) getActivity()), new ProgressSubscriber<KnowledgeClassListBean>(getActivity()) {
            @Override
            public void onNext(KnowledgeClassListBean knowledgeClassListBean) {
                super.onNext(knowledgeClassListBean);
                dataList.clear();
                dataList.addAll(knowledgeClassListBean.getData());
                mAdapter.notifyDataSetChanged();
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
        viewDelegate.get(R.id.tv_ok).setOnClickListener(v -> {
            sendFilterData();
        });
        viewDelegate.mRecyclerViewFixed.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                final boolean check = mFixedAdapter.getData().get(position).isCheck();
                for (int i = 0; i < mFixedAdapter.getData().size(); i++) {
                    if (i == position) {
                        mFixedAdapter.getData().get(i).setCheck(!check);
                    } else {
                        mFixedAdapter.getData().get(i).setCheck(false);
                    }
                }
                if (!check) {
                    mType = position;
                } else {
                    mType = 0;
                }
                mFixedAdapter.notifyDataSetChanged();
            }
        });
    }

    private void sendFilterData() {
        StringBuilder classIds = new StringBuilder();
        StringBuilder labelIds = new StringBuilder();
        final List<KnowledgeClassBean> data = mAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isCheck()) {
                if (TextUtils.isEmpty(classIds)) {
                    classIds.append(data.get(i).getId());
                } else {
                    classIds.append(",");
                    classIds.append(data.get(i).getId());
                }
            }

            final ArrayList<KnowledgeClassBean> labels = data.get(i).getLabels();
            for (int j = 0; j < labels.size(); j++) {
                if (labels.get(j).isCheck()) {
                    if (TextUtils.isEmpty(labelIds)) {
                        labelIds.append(labels.get(j).getId());
                    } else {
                        labelIds.append(",");
                        labelIds.append(labels.get(j).getId());

                    }
                }
            }

        }
        Map<String, Object> map = new HashMap<>();
        map.put("class_id", classIds.toString());
        map.put("label_id", labelIds.toString());
        map.put("range", mType);
        EventBusUtils.sendEvent(new MessageBean(MemoConstant.CLOSE_DRAWER, MemoConstant.FILTER_DATA, map));
    }


    public int getType() {
        return mType;
    }
}
package com.hjhq.teamface.email.presenter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.view.MylayoutManager;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.email.EmailModel;
import com.hjhq.teamface.email.R;
import com.hjhq.teamface.email.adapter.ModuleContactsAdapter;
import com.hjhq.teamface.email.bean.MolduleListBean;
import com.hjhq.teamface.email.view.SearchEmailDelegate;

import java.util.ArrayList;
import java.util.List;

public class SearchModuleContactActivity extends ActivityPresenter<SearchEmailDelegate, EmailModel> {

    private ModuleContactsAdapter mAdapter;
    private RecyclerView mRvContacts;
    private SearchBar mSearchBar;
    private TextView mTvResultnum;
    private RelativeLayout rlResult;
    private String keyword;
    private EmptyView emptyView1;
    private List<MolduleListBean.DataBean> dataList = new ArrayList<>();


    @Override
    public void init() {
        initData();
        initListener();
    }

    private void initListener() {

        mSearchBar.setSearchListener(new SearchBar.SearchListener() {
            @Override
            public void clear() {

            }

            @Override
            public void cancel() {
                finish();
            }

            @Override
            public void search() {
                searchData(keyword);
            }

            @Override
            public void getText(String text) {
                keyword = text;

            }
        });
        mRvContacts.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                ArrayList<MolduleListBean.DataBean> list = new ArrayList<>();
                list.add(mAdapter.getData().get(position));
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, list);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    protected void initData() {
        mRvContacts = viewDelegate.get(R.id.search_result_recycler_view);
        mTvResultnum = viewDelegate.get(R.id.tv_num);
        rlResult = viewDelegate.get(R.id.rl_result_remind);
        mSearchBar = viewDelegate.get(R.id.search_bar);
        rlResult.setVisibility(View.GONE);
        mRvContacts.setLayoutManager(new MylayoutManager(this));
        mAdapter = new ModuleContactsAdapter(dataList);
        mRvContacts.setAdapter(mAdapter);
        emptyView1 = new EmptyView(mContext);
        emptyView1.setEmptyImage(R.drawable.empty_view_img);
        //emptyView1.setEmptyTitle("输入关键字进行搜索~");
        mAdapter.setEmptyView(emptyView1);

    }


    /**
     * 搜索备忘录
     */
    private void searchData(String word) {
        if (TextUtils.isEmpty(word)) {
            rlResult.setVisibility(View.GONE);
            return;
        }
        model.getModuleEmails(mContext, word,
                new ProgressSubscriber<MolduleListBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(MolduleListBean moduleResultBean) {
                        super.onNext(moduleResultBean);
                        dataList.clear();
                        dataList.addAll(moduleResultBean.getData());
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}

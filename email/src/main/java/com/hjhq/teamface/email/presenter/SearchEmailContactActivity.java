package com.hjhq.teamface.email.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleItemBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.MylayoutManager;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.email.EmailModel;
import com.hjhq.teamface.email.R;
import com.hjhq.teamface.email.adapter.EmailContactsAdapter;
import com.hjhq.teamface.email.bean.EmailContactsListBean;
import com.hjhq.teamface.email.bean.EmailRecentContactsListBean;
import com.hjhq.teamface.email.view.SearchEmailDelegate;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SearchEmailContactActivity extends ActivityPresenter<SearchEmailDelegate, EmailModel> {
    public static final int TYPE_RECENT = 100;
    public static final int TYPE_CONTACTS = 200;
    public static final int TYPE_DATA = 300;
    private EmailContactsAdapter mAdapter;
    private RecyclerView mRvContacts;
    private RelativeLayout rlProject;
    private SearchBar mSearchBar;
    private TextView mTvResultnum;
    private RelativeLayout rlResult;
    private int type = 100;
    private String keyword;
    private EmptyView emptyView1;
    private List<Member> dataList = new ArrayList<>();


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
                ArrayList<Member> list = new ArrayList<Member>();
                list.add(mAdapter.getData().get(position));
                Intent intent = new Intent();
                intent.putExtra(Constants.DATA_TAG1, list);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getInt(Constants.DATA_TAG1);
        }
        mRvContacts = viewDelegate.get(R.id.search_result_recycler_view);
        mTvResultnum = viewDelegate.get(R.id.tv_num);
        rlResult = viewDelegate.get(R.id.rl_result_remind);
        mSearchBar = viewDelegate.get(R.id.search_bar);
        rlResult.setVisibility(View.GONE);
        mRvContacts.setLayoutManager(new MylayoutManager(this));
        mAdapter = new EmailContactsAdapter(dataList);
        mAdapter.showCheck(false);
        mRvContacts.setAdapter(mAdapter);
        emptyView1 = new EmptyView(mContext);
        emptyView1.setEmptyImage(R.drawable.empty_view_img);
        emptyView1.setEmptyTitle("输入关键字进行搜索~");
        //mAdapter.setEmptyView(emptyView1);

    }


    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(MessageBean bean) {
        switch (bean.getCode()) {

            case MemoConstant.DATALIST_CHANGE:
                if (!TextUtils.isEmpty(keyword)) {
                    searchData(keyword);
                }
                break;
            default:
                break;
        }

    }

    /**
     * 搜索备忘录
     */
    private void searchData(String word) {
        if (TextUtils.isEmpty(word)) {
            rlResult.setVisibility(View.GONE);
            return;
        }
        emptyView1.setEmptyTitle("正在搜索~");
        if (type == TYPE_RECENT) {
            model.getRecentEmailContacts(mContext, word,
                    new ProgressSubscriber<EmailRecentContactsListBean>(mContext, true) {
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(EmailRecentContactsListBean emailRecentContactsListBean) {
                            super.onNext(emailRecentContactsListBean);
                            List<EmailRecentContactsListBean.DataBean> newData = emailRecentContactsListBean.getData();
                            dataList.clear();
                            if (newData != null && newData.size() > 0) {
                                for (int i = 0; i < newData.size(); i++) {
                                    Member m = new Member();
                                    m.setEmail(newData.get(i).getMail_account());
                                    m.setEmployee_name(newData.get(i).getEmployee_name());
                                    dataList.add(m);
                                }
                            }
                            mAdapter.notifyDataSetChanged();//zzh:->增加刷新列表
                        }
                    });
        } else if (type == TYPE_CONTACTS) {
            model.getEmailContacts(mContext, word, new ProgressSubscriber<EmailContactsListBean>(mContext, true) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    e.printStackTrace();
                }

                @Override
                public void onNext(EmailContactsListBean baseBean) {
                    super.onNext(baseBean);
                    List<EmailContactsListBean.DataBean.DataListBean> newData = baseBean.getData().getDataList();
                    dataList.clear();
                    if (newData != null) {
                        for (int i = 0; i < newData.size(); i++) {
                            Member m = new Member();
                            m.setEmployee_name(newData.get(i).getName());
                            m.setId(TextUtil.parseLong(newData.get(i).getId()));
                            m.setEmail(newData.get(i).getMail_address());
                            dataList.add(m);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<ModuleItemBean> list = (ArrayList<ModuleItemBean>) data.getSerializableExtra(Constants.DATA_TAG1);
                if (list != null && list.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, list);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtils.showToast(mContext, "请选择数据");
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

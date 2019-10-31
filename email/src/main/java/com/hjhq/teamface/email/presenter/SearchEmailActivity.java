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
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.EmailBean;
import com.hjhq.teamface.basis.bean.EmailListBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ModuleItemBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.EmailConstant;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.view.MylayoutManager;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.common.view.SearchBar;
import com.hjhq.teamface.email.EmailModel;
import com.hjhq.teamface.email.R;
import com.hjhq.teamface.email.adapter.EmailListAdapter;
import com.hjhq.teamface.email.view.SearchEmailDelegate;
import com.luojilab.router.facade.annotation.RouteNode;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

@RouteNode(path = "/search_email", desc = "搜索邮件")
public class SearchEmailActivity extends ActivityPresenter<SearchEmailDelegate, EmailModel> {

    private EmailListAdapter mAdapter;
    private RecyclerView mRvContacts;
    private RelativeLayout rlProject;
    private SearchBar mSearchBar;
    private TextView mTvResultnum;
    private RelativeLayout rlResult;
    private int type = 1;
    private String keyword;
    private EmptyView emptyView1;
    private int boxId;
    private List<EmailBean> dataList = new ArrayList<>();


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

    }

    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            boxId = bundle.getInt(Constants.DATA_TAG1);
            type = bundle.getInt(Constants.DATA_TAG2);

        }
        mRvContacts = viewDelegate.get(R.id.search_result_recycler_view);
        mTvResultnum = viewDelegate.get(R.id.tv_num);
        rlResult = viewDelegate.get(R.id.rl_result_remind);
        mSearchBar = viewDelegate.get(R.id.search_bar);
        rlResult.setVisibility(View.GONE);
        mRvContacts.setLayoutManager(new MylayoutManager(this));
        mAdapter = new EmailListAdapter(boxId, dataList);
        mRvContacts.setAdapter(mAdapter);
        emptyView1 = new EmptyView(mContext);
        emptyView1.setEmptyImage(R.drawable.empty_view_img);
        emptyView1.setEmptyTitle("输入关键字进行搜索~");
        mAdapter.setEmptyView(emptyView1);
        String hint = "";
        switch (boxId) {
            case 1:
                hint = "收件箱";
                break;
            case 2:
                hint = "已发送";
                break;
            case 3:
                hint = "草稿箱";
                break;
            case 4:
                hint = "已删除";
                break;
            case 5:
                hint = "垃圾箱";
                break;
            case 6:
                hint = "未读";
                break;
        }
        mSearchBar.setHintText(hint);

    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        mRvContacts.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemClick(adapter, view, position);
                if (type == 2) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DATA_TAG1, mAdapter.getData().get(position));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (EmailConstant.DRAFTBOX.equals(boxId + "") && ("1".equals(mAdapter.getData().get(position).getDraft_status()))
                            && (EmailConstant.APPROVAL_STATE_NO.equals(mAdapter.getData().get(position).getApproval_status()) || EmailConstant.APPROVAL_STATE_CANCEL.equals(mAdapter.getData().get(position).getApproval_status()))) {
                        //草稿箱中的草稿和撤销的审批进入编辑界面
                        editEmail(mAdapter.getData().get(position));
                    } else {
                        viewEmailDetail(position);
                    }
                    if ("0".equals(mAdapter.getData().get(position).getRead_status())) {
                        mAdapter.getData().get(position).setRead_status("1");
                        mAdapter.notifyDataSetChanged();
                        markEmailReaded(mAdapter.getData().get(position).getId(), position);
                    }
                }

            }
        });
    }

    /**
     * 标记邮件已读
     *
     * @param id
     */
    public void markEmailReaded(String id, int position) {
        model.markMailReadOrUnread(mContext, id,
                EmailConstant.EMAIL_READ_TAG, boxId + "",
                new ProgressSubscriber<BaseBean>(mContext, false) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);

                    }
                });

    }

    /**
     * 编辑邮件(草稿,需要审批的邮件已撤销/驳回)
     *
     * @param mBean
     */
    public void editEmail(EmailBean mBean) {
        if (mBean == null) {
            return;
        }
        Bundle bundle = new Bundle();
        //标记为草稿
        bundle.putInt(Constants.DATA_TAG3, EmailConstant.EDIT_DRAFT);
        //邮件内容
        bundle.putSerializable(Constants.DATA_TAG5, mBean);
        CommonUtil.startActivtiyForResult(SearchEmailActivity.this, NewEmailActivity.class, Constants.REQUEST_CODE1, bundle);

    }

    /**
     * 查看邮件详情
     *
     * @param position
     */
    private void viewEmailDetail(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DATA_TAG1, mAdapter.getData().get(position).getId());
        bundle.putInt(Constants.DATA_TAG2, boxId);
        bundle.putSerializable(Constants.DATA_TAG3, mAdapter.getData().get(position));
        CommonUtil.startActivtiyForResult(mContext, EmailDetailActivity.class, Constants.REQUEST_CODE1, bundle);
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
        model.getEmailList(mContext, 1, 1000, "", boxId + "", word,
                new ProgressSubscriber<EmailListBean>(mContext, true) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);

                    }

                    @Override
                    public void onNext(EmailListBean emailListBean) {
                        super.onNext(emailListBean);
                        // mEmptyView.setEmptyTitle("无数据");
                        showData(emailListBean);
                    }
                });
    }

    private void showData(EmailListBean emailListBean) {
        emptyView1.setEmptyTitle("");
        dataList.clear();
        dataList.addAll(emailListBean.getData().getDataList());
        mAdapter.notifyDataSetChanged();

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

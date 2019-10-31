package com.hjhq.teamface.memo.ui;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.DataListDelegate;
import com.hjhq.teamface.common.adapter.MemberAdapter;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.memo.MemoModel;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.bean.MemberListBean;
import com.hjhq.teamface.memo.bean.MemberReadListBean;
import com.luojilab.component.componentlib.router.ui.UIRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-12-11.
 * Describe：查看阅读/点赞/收藏/学习人员列表;
 */

public class ViewMemberActivity extends ActivityPresenter<DataListDelegate, MemoModel> {
    public static final int TYPE1 = 1;
    public static final int TYPE2 = 2;
    public static final int TYPE3 = 3;
    public static final int TYPE4 = 4;
    private int type = TYPE1;
    private String dataId;
    MemberAdapter mAdapter;
    ProgressSubscriber<MemberListBean> mSubscriber;
    private List<Member> memberList = new ArrayList<>();
    private EmptyView mEmptyView;

    @Override
    public void init() {
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getInt(Constants.DATA_TAG1);
            dataId = bundle.getString(Constants.DATA_TAG2);
        }
        mEmptyView = new EmptyView(mContext);
        mEmptyView.setEmptyImage(R.drawable.empty_view_img);
        mAdapter = new MemberAdapter(memberList);
        mAdapter.setEmptyView(mEmptyView);
        viewDelegate.setAdapter(mAdapter);

        /*mSubscriber = new ProgressSubscriber<MemberListBean>(mContext) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(MemberListBean member) {
                super.onNext(member);
                memberList.clear();
                memberList.addAll(member.getData());
                mAdapter.notifyDataSetChanged();
                mSubscriber.onCompleted();
            }
        };*/
        switch (type) {
            case TYPE1:
                viewDelegate.setTitle("阅读");
                model.getViewPersons(mContext, TextUtil.parseLong(dataId), new ProgressSubscriber<MemberReadListBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(MemberReadListBean memberReadListBean) {
                        super.onNext(memberReadListBean);
                        memberList.clear();
                        if (memberReadListBean != null && memberReadListBean.getData() != null && memberReadListBean.getData().getReads() != null) {
                            memberList.addAll(memberReadListBean.getData().getReads());
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case TYPE4:
                viewDelegate.setTitle("已学习");
                model.getViewPersons(mContext, TextUtil.parseLong(dataId), new ProgressSubscriber<MemberReadListBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(MemberReadListBean memberReadListBean) {
                        super.onNext(memberReadListBean);
                        memberList.clear();
                        if (memberReadListBean != null && memberReadListBean.getData() != null && memberReadListBean.getData().getLearning() != null) {
                            memberList.addAll(memberReadListBean.getData().getLearning());
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;

            case TYPE2:
                viewDelegate.setTitle("收藏");
                model.getCollectionPersons(mContext, TextUtil.parseLong(dataId), new ProgressSubscriber<MemberListBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(MemberListBean member) {
                        super.onNext(member);
                        memberList.clear();
                        memberList.addAll(member.getData());
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case TYPE3:
                viewDelegate.setTitle("点赞");
                model.getPraisePersons(mContext, TextUtil.parseLong(dataId), new ProgressSubscriber<MemberListBean>(mContext) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(MemberListBean member) {
                        super.onNext(member);
                        memberList.clear();
                        memberList.addAll(member.getData());
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            default:
                break;
        }

    }

    @Override
    protected void bindEvenListener() {
        viewDelegate.setItemClickListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.DATA_TAG1, memberList.get(position).getId() + "");
                UIRouter.getInstance().openUri(mContext, "DDComp://app/employee/info", bundle, Constants.REQUEST_CODE8);
            }
        });
    }
}

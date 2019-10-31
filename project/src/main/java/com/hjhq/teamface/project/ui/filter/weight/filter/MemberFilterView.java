package com.hjhq.teamface.project.ui.filter.weight.filter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.device.SoftKeyboardUtils;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.ConditionBean;
import com.hjhq.teamface.project.ui.filter.weight.BaseFilterView;
import com.hjhq.teamface.project.ui.filter.weight.adapter.MemberFilterAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;


/**
 * 筛选条件中 人员选择
 *
 * @author Administrator
 */

public class MemberFilterView extends BaseFilterView {

    private RelativeLayout rlAction;
    private RelativeLayout rlContent;
    private RecyclerView mRecyclerView;
    private TextView tvTitle;
    private MemberFilterAdapter mAdapter;
    private List<Member> mDataList = new ArrayList<>();

    private boolean mFlag = false;
    private View ivArraw;

    public MemberFilterView(ConditionBean bean) {
        super(bean);
    }

    @Override
    public void addView(LinearLayout parent, Activity activity, int index) {

        mView = View.inflate(activity, R.layout.crm_item_goods_filter_by_creator, null);
        tvTitle = (TextView) mView.findViewById(R.id.tv_title);

        rlAction = (RelativeLayout) mView.findViewById(R.id.rl_title_creator);
        rlContent = (RelativeLayout) mView.findViewById(R.id.rl_action_creator);
        if (!mFlag) {
            rlContent.setVisibility(View.GONE);
        }
        ivArraw = mView.findViewById(R.id.iv);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        initView();
        setClickListener();
        parent.addView(mView, index);

    }

    private void initView() {
        TextUtil.setText(tvTitle, title);
        mDataList = bean.getMember();
        mAdapter = new MemberFilterAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setClickListener() {
        rlAction.setOnClickListener(v -> {
            SoftKeyboardUtils.hide(v);
            if (!mFlag) {
                rlContent.setVisibility(View.VISIBLE);
                mFlag = !mFlag;
                rotateCButton(mActivity, ivArraw, 0f, 180f, 500,
                        R.drawable.icon_sort_down);
            } else {
                rlContent.setVisibility(View.GONE);
                mFlag = !mFlag;
                rotateCButton(mActivity, ivArraw, 0f, -180f, 500,
                        R.drawable.icon_sort_down);
            }
        });
        mRecyclerView.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mDataList.get(position).setCheck(!mDataList.get(position).isCheck());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public boolean formatCheck() {
        return true;
    }

    @Override
    public boolean put(JSONObject json) throws Exception {
        if (mDataList == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        Observable.from(mDataList).filter(Member::isCheck).subscribe(data -> sb.append(data.getId()).append(","));
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            json.put(keyName, sb.toString());
        }
        return true;
    }

    @Override
    public void reset() {
        if (mDataList != null) {
            Observable.from(mDataList).subscribe(data -> data.setCheck(false));
            mAdapter.notifyDataSetChanged();
        }
    }

}

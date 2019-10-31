package com.hjhq.teamface.project.presenter.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.DataListDelegate;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.model.ProjectModel;

import java.util.ArrayList;

/**
 * 编辑任务列表名称
 *
 * @author Administrator
 * @date 2018/4/16
 */
public class EditTaskLinkActivity extends ActivityPresenter<DataListDelegate, ProjectModel> {
    private TaskItemAdapter mTaskItemAdapter;
    private ArrayList<TaskInfoBean> dataList = new ArrayList<>();

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            dataList = (ArrayList<TaskInfoBean>) intent.getSerializableExtra(Constants.DATA_TAG1);
        }
    }

    @Override
    public void init() {
        viewDelegate.setTitle(R.string.cancel_relevant);
        mTaskItemAdapter = new TaskItemAdapter(dataList, false);
        mTaskItemAdapter.setChooseType(true);
        viewDelegate.setAdapter(mTaskItemAdapter);
        viewDelegate.setRightMenuTexts(R.color.app_blue, getResources().getString(R.string.confirm));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        cancleQuote();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        mTaskItemAdapter.setOnItemClickListener(new TaskItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DragItemAdapter adapter, View view, int position) {
                dataList.get(position).setCheck(!dataList.get(position).isCheck());
                mTaskItemAdapter.notifyDataSetChanged();
                // mTaskItemAdapter.notifyItemChanged(position);
            }

            @Override
            public void onItemChildClick(DragItemAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemLongClick(DragItemAdapter adapter, View view, int position) {

            }
        });
    }

    /**
     * 编辑节点
     */
    private void cancleQuote() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).isCheck()) {
                sb.append("," + dataList.get(i).getBean_id());
            }
        }
        if (TextUtils.isEmpty(sb.toString())) {
            ToastUtils.showToast(mContext, "未选择数据");
            return;
        } else {
            String ids = sb.toString().substring(1);
            if (TextUtils.isEmpty(ids)) {
                ToastUtils.showToast(mContext, "未选择数据");
                return;
            } else {
                model.cancleQuote(this, ids, new ProgressSubscriber<BaseBean>(this) {
                    @Override
                    public void onNext(BaseBean baseBean) {
                        super.onNext(baseBean);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        }


    }
}

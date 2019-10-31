package com.hjhq.teamface.project.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.common.activity.GroupMemberActivity;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.model.ProjectModel;
import com.hjhq.teamface.project.ui.HandleTaskDelegate;

import java.util.ArrayList;

/**
 * 交接任务
 *
 * @author Administrator
 * @date 2018/4/10
 */
public class HandleTaskActivity extends ActivityPresenter<HandleTaskDelegate, ProjectModel> {
    private ArrayList<Member> mMembers;
    private long newId = -1L;
    private long oldId;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            mMembers = (ArrayList<Member>) getIntent().getSerializableExtra(Constants.DATA_TAG1);
            oldId = getIntent().getLongExtra(Constants.DATA_TAG2, -1L);
        }
    }

    @Override
    public void init() {


    }

    @Override
    protected void bindEvenListener() {

        viewDelegate.get(R.id.ll_handle).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.DATA_TAG1, mMembers);
            bundle.putInt(Constants.DATA_TAG2, GroupMemberActivity.FLAG_TRANSFER);
            CommonUtil.startActivtiyForResult(mContext, GroupMemberActivity.class, Constants.REQUEST_CODE1, bundle);
        });
        super.bindEvenListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (newId > 0 && oldId > 0) {
            model.deleteProjectMemberAndHandleTask(mContext, oldId, newId, new ProgressSubscriber<BaseBean>(mContext) {
                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                }

                @Override
                public void onNext(BaseBean baseBean) {
                    super.onNext(baseBean);
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == Constants.REQUEST_CODE1 && data != null) {
            ArrayList<Member> list = (ArrayList<Member>) data.getSerializableExtra(Constants.DATA_TAG1);
            if (list != null && list.size() > 0) {
                Member m = list.get(0);
                viewDelegate.setChoosedMember(m.getEmployee_name());
                newId = m.getId();
            }

        }

    }

}

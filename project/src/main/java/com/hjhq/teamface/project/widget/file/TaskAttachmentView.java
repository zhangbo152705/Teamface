package com.hjhq.teamface.project.widget.file;

import android.view.View;

import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.customcomponent.widget2.file.AttachmentView;
import com.hjhq.teamface.project.adapter.TaskAttachmentItemAdapter;

/**
 * Created by Administrator on 2018/8/30.
 */

public class TaskAttachmentView extends AttachmentView {
    private boolean flag =false;
    public TaskAttachmentView(CustomBean bean) {
        super(bean);
    }
    public TaskAttachmentView(CustomBean bean,boolean falg) {
        super(bean);
        this.flag = falg;
    }


    @Override
    protected void initAdapter() {
        fileAdapter = new TaskAttachmentItemAdapter((ActivityPresenter) mActivity, uploadFileBeanList, state, bean.getModuleBean(), fieldControl,flag);
        mRecyclerView.setAdapter(fileAdapter);
        if (flag){
            tvTitle.setVisibility(View.GONE);
            bottom_line.setVisibility(View.GONE);
            setFromType(1);
        }
    }

}

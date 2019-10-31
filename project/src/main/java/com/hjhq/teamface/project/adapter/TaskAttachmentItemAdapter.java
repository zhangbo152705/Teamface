package com.hjhq.teamface.project.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.customcomponent.CustomComponentModel;
import com.hjhq.teamface.customcomponent.adapter.AttachmentItemAdapter;

import java.util.List;

/**
 * 任务附件
 * @author lx
 * @date 2017/5/15
 */

public class TaskAttachmentItemAdapter extends AttachmentItemAdapter {
    private boolean flag;
    public TaskAttachmentItemAdapter(ActivityPresenter activity, List data, int state, String moduleBean, String fieldControl) {
        super(activity, data, state, moduleBean, fieldControl);
    }
    public TaskAttachmentItemAdapter(ActivityPresenter activity, List data, int state, String moduleBean, String fieldControl,boolean flag) {
        super(activity, data, state, moduleBean, fieldControl);
        this.flag = flag;
    }
    @Override
    protected void uploadFile(BaseViewHolder helper, UploadFileBean item, String fileUrl) {
        new CustomComponentModel().uploadFile(mActivity, fileUrl, moduleBean, new ProgressSubscriber<UpLoadFileResponseBean>(mActivity) {
            @Override
            public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                super.onNext(upLoadFileResponseBean);
                List<UploadFileBean> data = upLoadFileResponseBean.getData();
                //处理上传后的数据
                handleUploadData(data, item);
                //显示文件信息
                showFileInfo(helper, item);
                //显示进度
                if (flag){
                    RxManager.$(hashCode()).post(CustomConstants.MESSAGE_FILE_DETAIL_ATTACH_CODE,fileUrl);
                }
            }
        });
    }
}

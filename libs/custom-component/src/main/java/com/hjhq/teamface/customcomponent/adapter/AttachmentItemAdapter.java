package com.hjhq.teamface.customcomponent.adapter;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.UpLoadFileResponseBean;
import com.hjhq.teamface.basis.bean.UpLoadResumeanalysisBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.rxbus.RxManager;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.FileTypeUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.file.FormatFileSizeUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.customcomponent.CustomComponentModel;
import com.hjhq.teamface.customcomponent.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author lx
 * @date 2017/5/15
 */

public class AttachmentItemAdapter extends BaseQuickAdapter<UploadFileBean, BaseViewHolder> {
    protected String fieldControl;
    protected String moduleBean;
    protected int state;
    protected ActivityPresenter mActivity;
    protected int fromType;//来源 : 1:简历解析附件
    protected int aHashCode;

    public AttachmentItemAdapter(ActivityPresenter activity, List data, int state, String moduleBean, String fieldControl) {
        super(R.layout.custom_item_attachment_v2, data);
        this.mActivity = activity;
        this.state = state;
        this.moduleBean = moduleBean;
        this.fieldControl = fieldControl;
    }
    public AttachmentItemAdapter(ActivityPresenter activity, List data, int state, String moduleBean, String fieldControl,int mfromType,int aHashCode) {
        super(R.layout.custom_item_attachment_v2, data);
        this.mActivity = activity;
        this.state = state;
        this.moduleBean = moduleBean;
        this.fieldControl = fieldControl;
        this.fromType = mfromType;
        this.aHashCode = aHashCode;
    }

    @Override
    protected void convert(BaseViewHolder helper, UploadFileBean item) {

        TextView tvTitle = helper.getView(R.id.tv_title);
        TextView tvName = helper.getView(R.id.tv_name);
        String fileUrl = item.getFile_url();
        tvTitle.setText(item.getFile_name());

        if (state == CustomConstants.DETAIL_STATE || CustomConstants.FIELD_READ.equals(fieldControl)) {
            //详情不显示
            helper.setVisible(R.id.rl_del, false);
            showFileInfo(helper, item);
        } else {
            helper.setVisible(R.id.rl_del, true);
            helper.addOnClickListener(R.id.rl_del);


            //是否已上传
            if (item.getSerial_number() != null) {
                //显示进度
                showFileInfo(helper, item);

            } else {
                uploadFile(helper, item, fileUrl);
            }
        }
    }

    protected void uploadFile(final BaseViewHolder helper, final UploadFileBean item, String fileUrl) {
        if (!TextUtil.isEmpty(moduleBean) && moduleBean.indexOf("project_custom") != -1){//zzh:判断是否是任务模块
            new CustomComponentModel().uploadFile(mActivity, fileUrl, moduleBean, new ProgressSubscriber<UpLoadFileResponseBean>(mActivity) {
                @Override
                public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                    super.onNext(upLoadFileResponseBean);
                    List<UploadFileBean> data = upLoadFileResponseBean.getData();
                    //处理上传后的数据
                    handleUploadData(data, item);
                    //显示文件信息
                    showFileInfo(helper, item);
                }
            });
        }else {
            if (fromType == 1){
                Log.e("uploadFile","moduleBean:"+moduleBean);
                new CustomComponentModel().uploadResunmeanalyFile(mActivity, fileUrl, moduleBean, new ProgressSubscriber<UpLoadResumeanalysisBean>(mActivity) {
                    @Override
                    public void onNext(UpLoadResumeanalysisBean upLoadFileResponseBean) {
                        super.onNext(upLoadFileResponseBean);
                        if (upLoadFileResponseBean.getData() == null){
                            return;
                        }
                        List<UploadFileBean> data = new ArrayList<UploadFileBean>();
                        UpLoadResumeanalysisBean.Resumeanalysis nalysisData = upLoadFileResponseBean.getData();
                        UploadFileBean currentData = nalysisData.getObj();
                        if (currentData != null){
                            data.add(currentData);
                            //处理上传后的数据
                            handleUploadData(data, item);
                            //显示文件信息
                            showFileInfo(helper, item);
                        }

                        HashMap<String, Object> subData = nalysisData.getSubData();
                        HashMap<String, Object> mainData = nalysisData.getMainData();
                        //处理主表单数据
                        if (mainData != null){
                            handFromfsData(mainData);
                        }
                        //处理子表单数据
                        if (subData != null){
                            handFromfsData(subData);
                        }

                    }
                });
            }else {
                new CustomComponentModel().uploadApplyFile(mActivity, fileUrl, moduleBean, new ProgressSubscriber<UpLoadFileResponseBean>(mActivity) {
                    @Override
                    public void onNext(UpLoadFileResponseBean upLoadFileResponseBean) {
                        super.onNext(upLoadFileResponseBean);
                        List<UploadFileBean> data = upLoadFileResponseBean.getData();
                        //处理上传后的数据
                        handleUploadData(data, item);
                        //显示文件信息
                        showFileInfo(helper, item);
                        //显示进度
                    }
                });
            }
        }

    }

    /**
     * 显示文件信息
     *
     * @param helper
     * @param item
     */

    protected void showFileInfo(BaseViewHolder helper, UploadFileBean item) {
        ImageView ivIcon = helper.getView(R.id.iv_icon);
        TextView tvName = helper.getView(R.id.tv_name);
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvSize = helper.getView(R.id.tv_size);

        long uploadTime = TextUtil.parseLong(item.getUpload_time());
        if (uploadTime != 0) {
            String time = DateTimeUtil.longToStr(uploadTime, "yyyy-MM-dd");
            TextUtil.setText(tvTime, time);
        }

        long size = TextUtil.parseLong(item.getFile_size());
        String fileSize = FormatFileSizeUtil.formatFileSize(size);

        TextUtil.setText(tvName, item.getUpload_by());
        TextUtil.setText(tvSize, fileSize);
        if (FileTypeUtils.isImage(item.getFile_type())) {
            ImageLoader.loadImage(mContext, item.getFile_url(), ivIcon, R.drawable.icon_default, R.drawable.icon_default);
        } else {
            ImageLoader.loadImage(mContext, FileTypeUtils.getFileTypeIcon(item.getFile_type()), ivIcon);
        }
    }


    /**
     * 处理上传后的数据
     *
     * @param data
     * @param item
     */
    protected void handleUploadData(List<UploadFileBean> data, UploadFileBean item) {
        if (data != null && data.size() > 0) {
            UploadFileBean uploadFileBean = data.get(0);
            item.setUpload_time(uploadFileBean.getUpload_time());
            item.setUpload_by(uploadFileBean.getUpload_by());
            item.setFile_name(uploadFileBean.getFile_name());
            item.setFile_size(uploadFileBean.getFile_size());
            item.setFile_type(uploadFileBean.getFile_type());
            item.setFile_url(uploadFileBean.getFile_url());
            if (!TextUtil.isEmpty(uploadFileBean.getSerial_number())){
                item.setSerial_number(uploadFileBean.getSerial_number());
            }else {
                item.setSerial_number("1");
            }

            item.setOriginal_file_name(uploadFileBean.getOriginal_file_name());
            item.setId(uploadFileBean.getId());
            item.setPdfUrl(uploadFileBean.getPdfUrl());
            item.setPdfUrl(uploadFileBean.getPdfUrl());
        }
    }

    /**
     * 处理主表或子表 数据
     */
    public void handFromfsData(Map<String, Object> relationField){
        if (relationField != null && relationField.size() > 0) {
            Iterator<String> iterator = relationField.keySet().iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                Object relationValue = relationField.get(next);
                if (relationValue != null) {
                    if (next.startsWith(CustomConstants.SUBFORM)) {
                        if (next.lastIndexOf("_") == 7) {
                            RxManager.$(aHashCode).post(next + "resumeanlusis_list", relationValue);
                        }
                    } else {
                        RxManager.$(aHashCode).post(next, relationValue);
                    }
                }
            }
        }

    }

}

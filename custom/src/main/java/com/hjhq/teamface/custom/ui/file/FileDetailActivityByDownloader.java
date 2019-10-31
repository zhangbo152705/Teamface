package com.hjhq.teamface.custom.ui.file;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.hjhq.teamface.basis.bean.ProgressBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.FileConstants;
import com.hjhq.teamface.basis.util.FileUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.dialog.DialogUtils;
import com.hjhq.teamface.basis.util.file.FormatFileSizeUtil;
import com.hjhq.teamface.basis.util.file.JYFileHelper;
import com.hjhq.teamface.basis.util.log.LogUtil;
import com.hjhq.teamface.basis.zygote.ActivityPresenter;
import com.hjhq.teamface.custom.R;
import com.hjhq.teamface.custom.ui.add.AddCustomModel;
import com.hjhq.teamface.download.service.DownloadService;
import com.hjhq.teamface.download.utils.FileDownloaderUtil;
import com.hjhq.teamface.download.utils.FileTransmitUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.luojilab.router.facade.annotation.RouteNode;

import java.io.File;

/**
 * 通过FileDownloader下载的文件详情
 * Created by Administrator on 2018/4/10.
 */
@RouteNode(path = "/file_by_downloader", desc = "文件详情")
public class FileDetailActivityByDownloader extends ActivityPresenter<FileDetailDelegate, AddCustomModel> {
    private boolean hasInitReceiver = false;
    UploadFileBean mBean;
    private boolean isDel;
    private int totalSize;
    private int downloadTaskId;

    @Override
    public void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);
        if (savedInstanceState == null) {
            mBean = (UploadFileBean) getIntent().getSerializableExtra(Constants.DATA_TAG1);
            isDel = getIntent().getBooleanExtra(Constants.DATA_TAG2, false);
        }
    }
    @Override
    public void init() {
        if (mBean == null) {
            ToastUtils.showError(mContext, "文件错误");
            finish();
        }
        viewDelegate.setTitle(mBean.getFile_name());
        viewDelegate.showData(mBean);
        if (isDel) {
            viewDelegate.showDeletBtn(true);
        } else {
            viewDelegate.showDeletBtn(false);
        }
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        viewDelegate.get(R.id.iv_delete).setOnClickListener(v -> {
            //删除文件
            deleteFile();
        });
        viewDelegate.get(R.id.iv_delete2).setOnClickListener(v -> {
            //删除文件
            deleteFile();
        });
        viewDelegate.get(R.id.btn_open).setOnClickListener(v -> {
            //打开文件
            File file = new File(JYFileHelper.getFileDir(mContext, Constants.PATH_DOWNLOAD).getAbsolutePath(), mBean.getSerial_number() + mBean.getFile_name());
            if (file.exists()) {
                openFile(file);
            } else {
                ToastUtils.showToast(mContext, "本地文件不存在");
                viewDelegate.isDownloading(false);
            }

        });
        viewDelegate.get(R.id.btn_download).setOnClickListener(v -> {
            //下载文件
            checkFileSizeAndDownload(TextUtil.parseLong(mBean.getFile_size()));
        });
        viewDelegate.get(R.id.iv_download).setOnClickListener(v -> {
            //下载文件
            checkFileSizeAndDownload(TextUtil.parseLong(mBean.getFile_size()));
        });
        viewDelegate.get(R.id.iv_download2).setOnClickListener(v -> {
            //下载文件
            checkFileSizeAndDownload(TextUtil.parseLong(mBean.getFile_size()));
        });
        viewDelegate.get(R.id.iv_thumb).setOnClickListener(v -> {
            //全屏查看图片
        });
        viewDelegate.get(R.id.tv_view_online).setOnClickListener(v -> {
            //在线预览
        });
        viewDelegate.get(R.id.cancel_download).setOnClickListener(v -> {
            //取消下载
            FileTransmitUtils.cancelDownload(mBean.getSerial_number());
            viewDelegate.isDownloading(false);
        });

    }

    /**
     * 删除文件
     */
    private void deleteFile() {
        DialogUtils.getInstance().sureOrCancel(mContext, "", "确认删除文件吗?", viewDelegate.getRootView(), () -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    /**
     * 检查文件大小并下载
     *
     * @param size
     */
    public void checkFileSizeAndDownload(long size) {
        if (FileTransmitUtils.checkLimit(size)) {
            DialogUtils.getInstance().sureOrCancel(mContext, "",
                    "当前为移动网络且文件大小超过10M,继续下载吗?",
                    viewDelegate.getRootView(), () -> downloadFile());
        } else {
            downloadFile();
        }
    }

    private void downloadFile() {
        viewDelegate.isDownloading(true);
        File file = new File(JYFileHelper.getFileDir(this, Constants.PATH_DOWNLOAD), mBean.getSerial_number() + mBean.getFile_name());
        if (file.exists()) {
            file.delete();
        }
        String path = Uri.fromFile(file).getPath();
        Log.e("downloadFile","path:"+path);
        totalSize = TextUtil.parseInt(mBean.getFile_size());
        downloadTaskId = FileDownloaderUtil.getInstance(this).downBySingleTask(mBean.getFile_url(),path,listener);
       // DownloadService.getInstance().downloadFileFromUrl(mBean.getSerial_number(), mBean.getFile_url(), mBean.getFile_name());
    }

    private void openFile(File file) {
        if (!file.exists()) {
            ToastUtils.showToast(mContext, "本地文件不存在");
            viewDelegate.localFileNotExist();
            return;
        }
        FileUtils.browseDocument(this, viewDelegate.getRootView(), file.getName(), file.getAbsolutePath());
        LogUtil.e("文件名" + file + "绝对路径" + file.getAbsolutePath());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadTaskId != 0){
            FileDownloaderUtil.getInstance(this).stopDownTask(downloadTaskId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewDelegate.stopPlay();
    }

    /**
     * 文件下载回调
     */
    FileDownloadListener listener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("FileDownloadListener","pending:"+soFarBytes+"/"+totalBytes);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            viewDelegate.updateProgress(
                    String.format("下载中(%s)", FormatFileSizeUtil.formatFileSize(soFarBytes) + "/" + FormatFileSizeUtil.formatFileSize(totalSize)),
                    soFarBytes,totalSize);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            viewDelegate.isDownloading(false);
            Log.e("FileDownloadListener","completed:");
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("FileDownloadListener","paused:"+soFarBytes+"/"+totalBytes);
        }


        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            ToastUtils.showError(mContext, "下载失败");
            viewDelegate.isDownloading(false);
            Log.e("FileDownloadListener","error:"+e.toString());
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };


}

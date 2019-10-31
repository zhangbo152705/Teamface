package com.hjhq.teamface.download.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.hjhq.teamface.basis.AppConst;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.file.JYFileHelper;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.util.file.SPUtils;
import com.hjhq.teamface.download.service.DownloadService;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2019/7/11.
 */

public class FileDownloaderUtil {

    private static FileDownloaderUtil mFileDownloaderUtil;
    private Context mcontext;

    private FileDownloaderUtil(){};
    private FileDownloaderUtil(Context context){
       this.mcontext = context;
    }

    public static FileDownloaderUtil getInstance(Context context) {

        if (mFileDownloaderUtil == null) {
            FileDownloader.setup(context);//使用前初始化
            mFileDownloaderUtil = new FileDownloaderUtil(context);
        }
        return mFileDownloaderUtil;
    }

    /**
     * 启动单任务下载
     * @param url 下载服务器地址
     * @param localPath 下载到本地存储地址
     * @param listener 下载回调
     */
    public int downBySingleTask(String url,String localPath,FileDownloadListener listener){
        if (TextUtils.isEmpty(url)) {
            return 0;
        }
        try {
            //url中含有中文和特殊字符时需要转换
            url = URLEncoder.encode (url,"UTF-8");
            url = url.replace("%3A", ":");
            url = url.replace("%2F", "/");
            url = url.replace("%2B", "+");
            url = url.replace("%26", "&");
            url = url.replace("%3D", "=");
            url = url.replace("%3F", "?");
            url = url.replace("%25", "%");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")
                && !url.toLowerCase().startsWith("ftp://") && !url.toLowerCase().startsWith("ftps://")) {
            url = SPHelper.getDomain() + url;
        }

       int id = FileDownloader.getImpl().create(url)
                .addHeader("TOKEN", SPUtils.getString(mcontext, AppConst.TOKEN_KEY))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .setPath(localPath)
                .setListener(listener).start();
        return id;
    }

    public void stopDownTask(int downloadId1){
        FileDownloader.getImpl().pause(downloadId1);
    }
}

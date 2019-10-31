package com.hjhq.teamface.project.bean;

import com.hjhq.teamface.basis.bean.BaseBean;

/**
 * 全部节点
 * Created by Administrator on 2018/4/25.
 */

public class TaskDynamicInformationBean extends BaseBean {

    private String file_name;
    private long file_size;
    private String file_type;
    private String file_url;
    private String original_file_name;
    private int serial_number;
    private String upload_by;
    private long upload_time;
    private long voiceTime;

    public String getFile_name() {
        return file_name;
    }

    public long getFile_size() {
        return file_size;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFile_url() {
        return file_url;
    }

    public String getOriginal_file_name() {
        return original_file_name;
    }

    public int getSerial_number() {
        return serial_number;
    }

    public String getUpload_by() {
        return upload_by;
    }

    public long getUpload_time() {
        return upload_time;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public void setOriginal_file_name(String original_file_name) {
        this.original_file_name = original_file_name;
    }

    public void setSerial_number(int serial_number) {
        this.serial_number = serial_number;
    }

    public void setUpload_by(String upload_by) {
        this.upload_by = upload_by;
    }

    public void setUpload_time(long upload_time) {
        this.upload_time = upload_time;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(long voiceTime) {
        this.voiceTime = voiceTime;
    }
}

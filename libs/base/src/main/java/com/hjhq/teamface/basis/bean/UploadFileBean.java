package com.hjhq.teamface.basis.bean;

import java.io.Serializable;

/**
 * 文件上传实体
 *
 * @author Administrator
 * @date 2017/4/11
 */

public class UploadFileBean implements Serializable {

    /**
     * originalFileName : IMG20170303152612.jpg
     * fileName : IMG20170303152612.jpg
     * serialNumber : 1
     * fileSize : 1308690
     * fileUrl : http://192.168.1.172:9400/6/01b062297df0b6
     * fileType : jpg
     */

    protected String original_file_name;
    protected String file_name;
    protected String serial_number;
    protected String file_size;
    protected String upload_by;
    protected String upload_time;
    protected String file_url;
    protected String file_type;
    protected int voiceTime;
    protected int select_from_type;

    protected int id;
    protected String pdfUrl;
    protected String url;

    public UploadFileBean() {
    }

    public UploadFileBean(String fileName, String fileUrl, String fileType) {
        this.file_name = fileName;
        this.file_url = fileUrl;
        this.file_type = fileType;
    }

    public String getOriginal_file_name() {
        return original_file_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public String getFile_size() {
        return file_size;
    }

    public String getUpload_by() {
        return upload_by;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public String getFile_url() {
        return file_url;
    }

    public String getFile_type() {
        return file_type;
    }

    public int getVoiceTime() {
        return voiceTime;
    }

    public int getSelect_from_type() {
        return select_from_type;
    }

    public void setOriginal_file_name(String original_file_name) {
        this.original_file_name = original_file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public void setUpload_by(String upload_by) {
        this.upload_by = upload_by;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public void setVoiceTime(int voiceTime) {
        this.voiceTime = voiceTime;
    }

    public void setSelect_from_type(int select_from_type) {
        this.select_from_type = select_from_type;
    }

    public int getId() {
        return id;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

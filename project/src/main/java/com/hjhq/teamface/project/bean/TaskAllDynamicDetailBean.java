package com.hjhq.teamface.project.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.FileTypeUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 全部节点
 * Created by Administrator on 2018/4/25.
 */

public class TaskAllDynamicDetailBean extends BaseBean implements MultiItemEntity, Serializable {

   private String content;
   private long create_time;
   private int dynamic_type;
   private String employee_id;
   private String employee_name;
   private long id;
   private String picture;
   private long relation_id;
   private long sign_id;
   private List<TaskDynamicInformationBean>  information;

    public String getContent() {
        return content;
    }

    public long getCreate_time() {
        return create_time;
    }

    public int getDynamic_type() {
        return dynamic_type;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public long getId() {
        return id;
    }

    public String getPicture() {
        return picture;
    }

    public long getRelation_id() {
        return relation_id;
    }

    public long getSign_id() {
        return sign_id;
    }

    public List<TaskDynamicInformationBean> getInformation() {
        return information;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public void setDynamic_type(int dynamic_type) {
        this.dynamic_type = dynamic_type;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setRelation_id(long relation_id) {
        this.relation_id = relation_id;
    }

    public void setSign_id(long sign_id) {
        this.sign_id = sign_id;
    }

    public void setInformation(List<TaskDynamicInformationBean> information) {
        this.information = information;
    }

    @Override
    public int getItemType() {
        if (getDynamic_type() == 1){
                if (!CollectionUtils.isEmpty(getInformation())) {
                    TaskDynamicInformationBean uploadFileBean = getInformation().get(0);
                    String fileType = uploadFileBean.getFile_type();
                    if (FileTypeUtils.isImage(fileType)) {
                        return 1;
                    } else if (FileTypeUtils.isAudio(fileType) && uploadFileBean.getVoiceTime() != 0) {
                        return 2;
                    } else {
                        return 3;
                    }
                }else {
                    return 0;
                }

        }else if (getDynamic_type() == 2){
            return 4;
        }else if (getDynamic_type() == 3){
            return 5;
        }

        return 0;
    }
}

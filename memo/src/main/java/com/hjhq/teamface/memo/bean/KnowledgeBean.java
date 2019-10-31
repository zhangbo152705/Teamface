package com.hjhq.teamface.memo.bean;

import com.hjhq.teamface.basis.bean.AttachmentBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.database.Member;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018-12-25.
 * Describe：
 */

public class KnowledgeBean implements Serializable {
    private String id;
    private String create_time;
    private String praisecount;
    private ArrayList<ProjectLabelBean> label_ids;
    private String title;
    private String content;
    private String modify_time;
    private String del_status;
    //0知识1提问
    private String type_status;
    private String readcount;
    private String collectioncount;
    private String studycount;
    private Member create_by;
    private String classification_id;
    private String classification_name;
    private Member modify_by;
    private String video;
    //学习状态
    private String alreadystudying;
    //点赞状态
    private String alreadyprasing;
    //收藏状态
    private String alreadycollecting;
    private ArrayList<AttachmentBean> repository_lib_attachment;
    private ArrayList<AttachmentBean> repository_answer_attachment;
    private ArrayList<TaskInfoBean> relevantList;
    //管理员
    private ArrayList<Member> allot_manager;
    //1：置顶 0：非置顶
    private String top;

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPraisecount() {
        return praisecount;
    }

    public void setPraisecount(String praisecount) {
        this.praisecount = praisecount;
    }

    public ArrayList<ProjectLabelBean> getLabel_ids() {
        return label_ids;
    }

    public void setLabel_ids(ArrayList<ProjectLabelBean> label_ids) {
        this.label_ids = label_ids;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }

    public String getDel_status() {
        return del_status;
    }

    public void setDel_status(String del_status) {
        this.del_status = del_status;
    }

    public String getType_status() {
        return type_status;
    }

    public void setType_status(String type_status) {
        this.type_status = type_status;
    }

    public Member getCreate_by() {
        return create_by;
    }

    public void setCreate_by(Member create_by) {
        this.create_by = create_by;
    }

    public String getClassification_id() {
        return classification_id;
    }

    public void setClassification_id(String classification_id) {
        this.classification_id = classification_id;
    }

    public String getClassification_name() {
        return classification_name;
    }

    public void setClassification_name(String classification_name) {
        this.classification_name = classification_name;
    }

    public String getReadcount() {
        return readcount;
    }

    public void setReadcount(String readcount) {
        this.readcount = readcount;
    }

    public String getCollectioncount() {
        return collectioncount;
    }

    public void setCollectioncount(String collectioncount) {
        this.collectioncount = collectioncount;
    }

    public String getStudycount() {
        return studycount;
    }

    public void setStudycount(String studycount) {
        this.studycount = studycount;
    }

    public Member getModify_by() {
        return modify_by;
    }

    public void setModify_by(Member modify_by) {
        this.modify_by = modify_by;
    }

    public String getAlreadystudying() {
        return alreadystudying;
    }

    public void setAlreadystudying(String alreadystudying) {
        this.alreadystudying = alreadystudying;
    }

    public String getAlreadyprasing() {
        return alreadyprasing;
    }

    public void setAlreadyprasing(String alreadyprasing) {
        this.alreadyprasing = alreadyprasing;
    }

    public String getAlreadycollecting() {
        return alreadycollecting;
    }

    public void setAlreadycollecting(String alreadycollecting) {
        this.alreadycollecting = alreadycollecting;
    }

    public ArrayList<Member> getAllot_manager() {
        return allot_manager;
    }

    public void setAllot_manager(ArrayList<Member> allot_manager) {
        this.allot_manager = allot_manager;
    }

    public ArrayList<AttachmentBean> getRepository_lib_attachment() {
        return repository_lib_attachment;
    }

    public void setRepository_lib_attachment(ArrayList<AttachmentBean> repository_lib_attachment) {
        this.repository_lib_attachment = repository_lib_attachment;
    }

    public ArrayList<AttachmentBean> getRepository_answer_attachment() {
        return repository_answer_attachment;
    }

    public void setRepository_answer_attachment(ArrayList<AttachmentBean> repository_answer_attachment) {
        this.repository_answer_attachment = repository_answer_attachment;
    }

    public void setRevelant(ArrayList<TaskInfoBean> relevantList) {
    }

    public ArrayList<TaskInfoBean> getRelevantList() {
        return relevantList;
    }

    public void setRelevantList(ArrayList<TaskInfoBean> relevantList) {
        this.relevantList = relevantList;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }
}

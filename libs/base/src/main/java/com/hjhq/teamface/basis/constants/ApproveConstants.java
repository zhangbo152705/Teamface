package com.hjhq.teamface.basis.constants;

/**
 * 审批常量类
 *
 * @author Administrator
 * @date 2018/1/17
 */

public class ApproveConstants {
    /**
     * 审批的bean
     */
    public static final String APPROVAL_MODULE_BEAN = "approval";

    public static final String MODULE_BEAN = "module_bean";
    public static final String APPROVAL_DATA_ID = "approval_data_id";
    public static final String TASK_KEY = "taskKey";
    public static final String TASK_NAME = "taskName";
    public static final String TASK_ID = "taskID";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String APPROVE_TYPE = "approveType";
    public static final String PROCESS_STATUS = "process_status";
    public static final String APPROVE_OPTION = "approve_option";
    public static final String PROCESS_FIELD_V = "processFieldV";
    public static final String OPERATION_TYPE = "operationType";
    public static final String CURRENT_NODE_USERS = "currentNodeUsers";
    public static final String EMAIL_BEAN = "mail_box_scope";
    public static final String APPROVAL_READ = "approvalRead";
    public static final String APPROVAL_BEAN = "approvalBean";
    public static final String APPROVAL_FILTER_DATA_OK = "approval_filter_data_ok";
    public static final String APPROVAL_REFRESH_UNREAD_NUMBER = "approval_refresh_unread_number";
    public static final String APPROVAL_REFRESH_DATA_LIST = "approval_refresh_data_list";
    /**
     * 审批小助手
     */
    public static final String APPROVAL_APP_ASSISTANT = "approval_app_assistant";
    public static final String APPROVAL_APP_ASSISTANT_ID = "approval_app_assistant_ID";

    /**
     * 未读
     */
    public static final String UNREAD = "0";
    /**
     * 已读
     */
    public static final String READ = "1";

    /**
     * 固定流程
     */
    public static final String FIX_FLOW = "0";
    /**
     * 自由流程
     */
    public static final String FREE_FLOW = "1";

    /*PROCESS_STATUS_OVER("结束"), 0
    PROCESS_STATUS_WAIT_COMMIT("待提交"), 1
    PROCESS_STATUS_COMMIT("已提交"), 2
    PROCESS_STATUS_WAIT_APPROVAL("待审批"), 3
    PROCESS_STATUS_ING("审批中"), 4
    PROCESS_STATUS_FINISH("审批通过"), 5
    PROCESS_STATUS_REJECT("审批驳回"), 6
    PROCESS_STATUS_REVOKE("已撤销"), 7
    PROCESS_STATUS_TRANSFER("已转交");8*/
    /**
     * 审批结束
     */
    public static final String APPROVE_OVER = "0";
    /**
     * -1已提交
     */
    public static final String APPROVE_SUBMITTED = "-1";
    /**
     * 节点错误
     */
    public static final String APPROVE_ERROR = "-3";
    /**
     * 待审批
     */
    public static final String APPROVE_PENDING = "0";
    /**
     * 审批中
     */
    public static final String APPROVING = "1";
    /**
     * 审批通过
     */
    public static final String APPROVE_PASS = "2";
    /**
     * 审批驳回
     */
    public static final String APPROVE_REJECT = "3";
    /**
     * 已撤销
     */
    public static final String APPROVE_REVOKED = "4";
    /**
     * 已转交
     */
    public static final String APPROVE_TRANSFER = "5";
    /**
     * 待提交  驳回到发起人
     */
    public static final String APPROVE_PEND_SUBMIT = "6";
    /**
     * 流程开始
     */
    public static final String FIRST_TASK = "firstTask";
    /**
     * 流程结束
     */
    public static final String END_TASK = "endEvent";


    public static final String PASS = "通过";
    public static final String URGETO = "催办";
    public static final String TRANSFER = "转交";
    public static final String REJECT = "驳回";
    public static final String REVOKE = "撤销";
    public static final String COMMENT = "评论";
    public static final String EDIT = "编辑";
    public static final String DEL = "删除";
    public static final String CCTO = "抄送";
    public static final String APPROVE_DEL = "10";

    /**
     * 撤销
     */
    public static final String REVOKE_AUTH = "1";
    /**
     * 转交
     */
    public static final String TRANSFER_AUTH = "2";
    /**
     * 发起人抄送
     */
    public static final String BEGIN_CCTO_AUTH = "3";
    /**
     * 审批人抄送
     */
    public static final String APPROVER_CCTO_AUTH = "4";
    /**
     * 抄送人抄送
     */
    public static final String CCTO_CCTO_AUTH = "5";


    /**
     * 审批/工作台列表刷新
     */
    public static final int REFRESH = 0x565;
}

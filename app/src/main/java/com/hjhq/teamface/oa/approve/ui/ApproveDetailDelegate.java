package com.hjhq.teamface.oa.approve.ui;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.R;
import com.hjhq.teamface.basis.bean.CustomBean;
import com.hjhq.teamface.basis.bean.CustomLayoutResultBean;
import com.hjhq.teamface.basis.bean.LayoutBean;
import com.hjhq.teamface.basis.constants.ApproveConstants;
import com.hjhq.teamface.basis.constants.CustomConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.adapter.CommentAdapter;
import com.hjhq.teamface.common.utils.ApproveUtils;
import com.hjhq.teamface.common.view.CommentInputView;
import com.hjhq.teamface.componentservice.custom.CustomService;
import com.hjhq.teamface.oa.approve.bean.ApproverBean;
import com.hjhq.teamface.oa.approve.bean.ProcessFlowResponseBean;
import com.hjhq.teamface.oa.approve.widget.ApproveTaskView;
import com.luojilab.component.componentlib.router.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 审批详情视图
 *
 * @author lx
 * @date 2017/9/4
 */

public class ApproveDetailDelegate extends AppDelegate {
    private LinearLayout llCustomLayout;
    private TextView tvOption1;
    private TextView tvOption2;
    private TextView tvOption3;
    private TextView tvOption5;
    private TextView tvOption6;
    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvSubTitle;
    private TextView tvCcMembers;
    private ApproveTaskView approveTaskView;
    private ImageView ivApproveTag;
    public RelativeLayout rlComment;
    public RelativeLayout rlRoot;
    public RecyclerView rvComment;
    public NestedScrollView mScroll;

    List<Object> mViewList = new ArrayList<>();
    private CustomService service;

    @Override
    public int getRootLayoutId() {
        return R.layout.approve_detail_layout;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        llCustomLayout = get(R.id.ll_custom_layout);
        tvOption1 = get(R.id.tv_option1);
        tvOption2 = get(R.id.tv_option2);
        tvOption3 = get(R.id.tv_option3);
        tvOption5 = get(R.id.tv_option5);
        tvOption6 = get(R.id.tv_option6);
        ivAvatar = get(R.id.iv_avatar);
        ivApproveTag = get(R.id.iv_approve_tag);
        tvName = get(R.id.tv_name);
        tvSubTitle = get(R.id.tv_subTitle);
        tvCcMembers = get(R.id.tv_members);
        approveTaskView = get(R.id.approve_task_view);
        rlComment = get(R.id.rl_comment);
        rlRoot = get(R.id.rl_root);
        rvComment = get(R.id.rv_comment);
        mScroll = get(R.id.nsv);
        rvComment.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        service = (CustomService) Router.getInstance().getService(CustomService.class.getSimpleName());
        setRightMenuIcons(R.drawable.icon_more);
        showMenu();
    }


    /**
     * 绘制布局
     *
     * @param layoutBeanList 布局
     * @param detailMap      数据
     * @param state          0新增 1详情 2编辑
     * @param moduleId       模块id
     */
    public void drawLayout(CustomLayoutResultBean.DataBean layoutBeanList, Map detailMap, int state, String moduleId) {
        if (layoutBeanList == null) {
            return;
        }
        int stateValue = state;
        llCustomLayout.removeAllViews();
        String title = layoutBeanList.getTitle();
        setTitle(title);
        List<LayoutBean> layout = layoutBeanList.getLayout();
        if (layout == null) {
            return;
        }
        mViewList.clear();
        for (LayoutBean layoutBean : layout) {

            boolean isSpread = "0".equals(layoutBean.getIsSpread());
            String name = layoutBean.getName();
            boolean isHideColumnName = "1".equals(layoutBean.getIsHideColumnName());
            if ("systemInfo".equals(name)) {
                //系统信息 隐藏
                continue;
            }

            List<CustomBean> list = layoutBean.getRows();
            for (CustomBean customBean : list) {
                Object o = detailMap.get(customBean.getName());
                customBean.setValue(o);
                if (CustomConstants.FORMULA.equals(customBean.getType())
                        || CustomConstants.FUNCTION_FORMULA.equals(customBean.getType())
                        || CustomConstants.REFERENCE_FORMULA.equals(customBean.getType())
                        || CustomConstants.SENIOR_FORMULA.equals(customBean.getType())) {
                    customBean.setState(CustomConstants.APPROVE_DETAIL_STATE);
                    customBean.setStateEnv(CustomConstants.APPROVE_DETAIL_STATE);
                } else {
                    customBean.setState(state);
                    customBean.setStateEnv(state);
                }
                //子表单
                if (customBean.getComponentList() != null && customBean.getComponentList().size() > 0) {
                    final List<CustomBean> componentList = customBean.getComponentList();
                    for (CustomBean bean : componentList) {
                        if (CustomConstants.FORMULA.equals(customBean.getType())
                                || CustomConstants.FUNCTION_FORMULA.equals(customBean.getType())
                                || CustomConstants.REFERENCE_FORMULA.equals(customBean.getType())
                                || CustomConstants.SENIOR_FORMULA.equals(customBean.getType())) {
                            bean.setState(CustomConstants.DETAIL_STATE);
                        } else {
                            bean.setState(state);
                        }
                    }
                }
            }

            if (service != null) {
                Object subfieldView = service.getSubfield(list, state, layoutBean.getTitle(), isSpread, moduleId, isHideColumnName, llCustomLayout);
                mViewList.add(subfieldView);
            }
        }
    }

    /**
     * 获取详情信息
     *
     * @return
     */
    public JSONObject getDetail() {
        JSONObject json = new JSONObject();
        if (service != null) {
            boolean put = service.putNoCheck(mViewList, json, false);
            if (!put) {
                return null;
            }
        }
        return json;
    }

    /**
     * 检查数据
     *
     * @return
     */
    public boolean checkData(JSONObject json) {
        if (service != null) {
            boolean put = service.putNoCheck(mViewList, json, true);
            if (!put) {
                return false;
            }
        }
        return true;
    }

    /**
     * 设置审批申请人
     *
     * @param beginUser
     */
    public void setBeginUser(ApproverBean beginUser, String processStatus) {
        if (beginUser == null) {
            return;
        }
        View stateView = get(R.id.state_view);
        stateView.setBackgroundResource(ApproveUtils.state2CircleIcon(processStatus));

        TextUtil.setText(tvName, beginUser.getEmployee_name());
        TextUtil.setText(tvSubTitle, ApproveUtils.state2String(processStatus));
        switch (processStatus) {
            case ApproveConstants.APPROVE_SUBMITTED:
                tvSubTitle.setTextColor(getActivity().getResources().getColor(R.color.gray_bg));
                break;
            case ApproveConstants.APPROVE_PENDING:
                tvSubTitle.setTextColor(getActivity().getResources().getColor(R.color.orange_bg));
                break;
            case ApproveConstants.APPROVING:
                tvSubTitle.setTextColor(getActivity().getResources().getColor(R.color.blue_bg));
                break;
            case ApproveConstants.APPROVE_PASS:
                tvSubTitle.setTextColor(getActivity().getResources().getColor(R.color.green_pass_bg));
                break;
            case ApproveConstants.APPROVE_REJECT:
                tvSubTitle.setTextColor(getActivity().getResources().getColor(R.color.red));
                break;
            case ApproveConstants.APPROVE_REVOKED:
                tvSubTitle.setTextColor(getActivity().getResources().getColor(R.color.gray_bg));
                break;
            case ApproveConstants.APPROVE_TRANSFER:
                tvSubTitle.setTextColor(getActivity().getResources().getColor(R.color.orange_bg));
                break;
            case ApproveConstants.APPROVE_PEND_SUBMIT:
            default:
                break;
        }
        ImageLoader.loadCircleImage(mContext, beginUser.getPicture(), ivAvatar, beginUser.getEmployee_name());
        TextUtil.setText(tvName, beginUser.getEmployee_name());
    }

    /**
     * 设置抄送人
     *
     * @param ccTos
     */
    public void setCCTo(List<Member> ccTos) {
        StringBuilder sb = new StringBuilder();
        String memberStr = "";
        for (Member member : ccTos) {
            sb.append(member.getName() + "、");
        }
        memberStr = sb.toString();
        if (!TextUtils.isEmpty(memberStr)) {
            memberStr = memberStr.substring(0, memberStr.length() - 1);
        }
        TextUtil.setText(tvCcMembers, memberStr);

    }

    /**
     * 设置审批流程
     *
     * @param taskFlow
     */
    public void setApproveTaskFlow(List<ProcessFlowResponseBean.DataBean> taskFlow) {
        approveTaskView.setApproveTaskFlow(taskFlow);

    }

    public void setOptionVisibility(int visibility) {
        get(R.id.ll_bottom_option).setVisibility(visibility);
        get(R.id.anchor).setVisibility(visibility);
    }

    public void setOption1Text(String text) {
        tvOption1.setVisibility(View.VISIBLE);
        get(R.id.anchor).setVisibility(View.VISIBLE);
        TextUtil.setText(tvOption1, text);
    }

    public void setOption2Text(String text) {
        get(R.id.anchor).setVisibility(View.VISIBLE);

        tvOption2.setVisibility(View.VISIBLE);
        TextUtil.setText(tvOption2, text);
    }

    public void setOption3Text(String text) {
        get(R.id.anchor).setVisibility(View.VISIBLE);

        tvOption3.setVisibility(View.VISIBLE);
        TextUtil.setText(tvOption3, text);
    }

    public void setOption5Text(String text) {
        //同意
        get(R.id.anchor).setVisibility(View.VISIBLE);
        tvOption5.setVisibility(View.VISIBLE);
        TextUtil.setText(tvOption5, text);
    }

    public void setOption6Text(String text) {
        //转审
        get(R.id.anchor).setVisibility(View.VISIBLE);
        tvOption6.setVisibility(View.VISIBLE);
        TextUtil.setText(tvOption6, text);
    }

    public void setOption4Text(String text) {
       /*tvOption4.setVisibility(View.VISIBLE);
        TextUtil.setText(tvOption4, text);*/
        //评论移动到了底部
    }


    public void hideOption1() {
        tvOption1.setVisibility(View.GONE);
    }

    public void hideOption2() {
        tvOption2.setVisibility(View.GONE);
    }

    public void hideOption3() {
        tvOption3.setVisibility(View.GONE);
    }

    public void hideOption5() {
        tvOption5.setVisibility(View.GONE);
    }

    public void hideOption6() {
        tvOption6.setVisibility(View.GONE);
    }


    /**
     * 设置审批标签图标
     *
     * @param visible
     */
    public void setApproveTagVisible(int visible) {
        ivApproveTag.setVisibility(visible);
    }

    /**
     * 设置审批标签图标
     *
     * @param res
     */
    public void setApproveTag(int res) {
        setApproveTagVisible(View.VISIBLE);
        ivApproveTag.setImageResource(res);
    }

    /**
     * 底部评论组件
     *
     * @param mCommentInputView
     */
    public void setCommentView(CommentInputView mCommentInputView) {
        rlComment.addView(mCommentInputView);
    }

    /**
     * 评论列表适配器
     *
     * @param commentAdapter
     */
    public void setAdapter(CommentAdapter commentAdapter) {
        rvComment.setAdapter(commentAdapter);
    }

    /**
     * 隐藏操作按钮
     */
    public void hideAnchor() {
        get(R.id.anchor).setVisibility(View.GONE);
        get(R.id.ll_bottom_option).setVisibility(View.GONE);
    }
}
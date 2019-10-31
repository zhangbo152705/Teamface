package com.hjhq.teamface.memo.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.view.TextWebView;
import com.hjhq.teamface.memo.R;

/**
 * Created by Administrator on 2018/3/21.
 * Describe：知识库详情视图
 */

public class KnowledgeDetailDelegate extends AppDelegate {


    @Override
    public int getRootLayoutId() {
        return R.layout.memo_knowledge_detail_activity;
    }

    public RecyclerView rvRelevant;
    private RecyclerView rvAttachment;
    private RecyclerView rvComment;
    private RecyclerView rvAnswer;
    private RecyclerView rvVideo;
    private TextView tvSortType;
    public TextWebView mTextWebView;
    private LinearLayout llStudyState;
    private ImageView ivStudyState;
    private TextView tvStudyState;
    private TextView tvAnswerNum;
    public ScrollView mScrollView;

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setRightMenuIcons(R.drawable.icon_more);
        showMenu(0);
        rvAttachment = get(R.id.rv_attachment);
        rvRelevant = get(R.id.rv_relevant);
        rvAnswer = get(R.id.rv_answer);
        rvVideo = get(R.id.rv_video);
        rvComment = get(R.id.rv_comment);
        tvSortType = get(R.id.tv_sort);
        mTextWebView = get(R.id.text_content);
        llStudyState = get(R.id.ll_study_state);
        ivStudyState = get(R.id.iv_check);
        tvStudyState = get(R.id.tv_study_state);
        mScrollView = get(R.id.sv_scroll_view);
        tvAnswerNum = get(R.id.tv_answer_num);
        rvRelevant.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvAnswer.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvAttachment.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvComment.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvVideo.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    public void setRelevantAdapter(TaskItemAdapter adapter) {
        rvRelevant.setAdapter(adapter);
    }

    public void setAttaAdapter(BaseQuickAdapter adapter) {
        rvAttachment.setAdapter(adapter);
    }

    public void setCommentAdapter(BaseQuickAdapter adapter) {
        rvComment.setAdapter(adapter);
    }

    public void setVideoAdapter(BaseQuickAdapter adapter) {
        rvVideo.setAdapter(adapter);
    }

    public void setAnswerAdapter(BaseQuickAdapter adapter) {
        rvAnswer.setAdapter(adapter);
    }

    public void setAttaClick(SimpleItemClickListener listener) {
        rvAttachment.addOnItemTouchListener(listener);
    }

    public void setAnsweClick(SimpleItemClickListener listener) {
        rvAnswer.addOnItemTouchListener(listener);
    }

    public void setCommentClick(SimpleItemClickListener listener) {
        rvComment.addOnItemTouchListener(listener);
    }


    /**
     * 设置排序类型
     *
     * @param s
     */
    public void setSortType(String s) {
        TextUtil.setText(tvSortType, s);
    }

    /**
     * 修改学习状态
     *
     * @param b
     */
    public void changeStudyState(boolean b) {
        llStudyState.setSelected(b);
        ivStudyState.setSelected(b);
        tvStudyState.setSelected(b);
        tvStudyState.setText(b ? "已学习" : "确认学习");

/*
        if (b) {
            llStudyState.setBackgroundResource(R.drawable.blue_round_bg);
            ivStudyState.setBackgroundResource(R.drawable.icon_study_yes);
            tvStudyState.setText("已学习");
            tvStudyState.setTextColor(getActivity().getResources().getColor(R.color.app_blue));

        } else {
            llStudyState.setBackgroundResource(R.drawable.gray_round_bg);
            ivStudyState.setBackgroundResource(R.drawable.icon_not_study);
            tvStudyState.setText("确认学习");
            tvStudyState.setTextColor(getActivity().getResources().getColor(R.color._666666));
        }
*/
    }

    public void setAnswerNum(int size) {
        TextUtil.setText(tvAnswerNum, size + "个回答");
    }
}

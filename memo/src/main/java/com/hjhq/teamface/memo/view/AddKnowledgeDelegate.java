package com.hjhq.teamface.memo.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.basis.zygote.AppDelegate;
import com.hjhq.teamface.common.adapter.TaskItemAdapter;
import com.hjhq.teamface.common.view.TextWebView;
import com.hjhq.teamface.memo.R;

import java.util.List;

/**
 * Created by Administrator on 2018/3/21.
 * Describe：
 */

public class AddKnowledgeDelegate extends AppDelegate {
    private TextView tvContentTitle;
    private TextView tvContentTitleStar;
    private RecyclerView rvRelevant;
    private RecyclerView rvAttachment;
    private TextView catgName;
    private TextView tagName;
    private EditText etTitle;
    public TextWebView textContent;


    @Override
    public int getRootLayoutId() {
        return R.layout.memo_knowledge_add_edit_activity;
    }

    @Override
    public boolean isToolBar() {
        return true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        setLeftIcon(-1);
        setLeftText(R.color.app_blue, "取消");
        setRightMenuTexts(R.color.app_blue, "保存");
        tvContentTitle = get(R.id.tv_content_title);
        tvContentTitleStar = get(R.id.tv_star2);
        rvAttachment = get(R.id.rv_attachment);
        rvRelevant = get(R.id.rv_relevant);
        catgName = get(R.id.tv_catg);
        tagName = get(R.id.tv_tag_content);
        etTitle = get(R.id.et_title);
        textContent = get(R.id.text_content);

        rvRelevant.setLayoutManager(new LinearLayoutManager(getActivity()) {
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


    }

    public void loadUrl() {
        textContent.loadUrl(0, Constants.EMAIL_EDIT_URL);
    }

    public void setRelevantAdapter(TaskItemAdapter adapter) {
        rvRelevant.setAdapter(adapter);

    }

    public void setAttaAdapter(BaseQuickAdapter adapter) {
        rvAttachment.setAdapter(adapter);
    }

    public void setAttaClick(SimpleItemClickListener listener) {
        rvAttachment.addOnItemTouchListener(listener);
    }

    public void setRelvantClick(SimpleItemClickListener listener) {
        rvRelevant.addOnItemTouchListener(listener);
    }


    public void setContentTitle(boolean b, String content) {
        if (b) {
            tvContentTitleStar.setVisibility(View.VISIBLE);
        } else {
            tvContentTitleStar.setVisibility(View.INVISIBLE);
        }
        TextUtil.setText(tvContentTitle, content);
    }

    public void addNewData(List<TaskInfoBean> list) {


    }

    public void setCatg(String name) {
        TextUtil.setText(catgName, name);
    }

    public void setTag(String name) {
        TextUtil.setText(tagName, name);
    }

    public String getTitle() {
        return etTitle.getText().toString();
    }

    public void setDataTitle(String s) {
        etTitle.setText(s);
    }

    public void setKnowledgeTitle(String title) {
        TextUtil.setText(etTitle, title);
    }
}

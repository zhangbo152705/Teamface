package com.hjhq.teamface.project.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.common.bean.ProjectListBean;
import com.hjhq.teamface.common.view.CircularRingPercentageView;
import com.hjhq.teamface.common.view.HorizontalProgressView;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.PersonalTaskResultBean;

import java.util.List;

/**
 * 项目列表适配器
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class TaskGroupAapter extends BaseQuickAdapter<PersonalTaskResultBean.DataBean, BaseViewHolder> {


    public TaskGroupAapter(List<PersonalTaskResultBean.DataBean> data) {
        super(R.layout.project_task_group_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PersonalTaskResultBean.DataBean item) {

        String projectId = item.getProjectId();
        String title = item.getTitle();
        if (TextUtil.isEmpty(projectId)){
            helper.setText(R.id.tv_name,"个人任务");
        }else{
            if (!TextUtil.isEmpty(title)){
                helper.setText(R.id.tv_name,title);
            }
        }

    }
}

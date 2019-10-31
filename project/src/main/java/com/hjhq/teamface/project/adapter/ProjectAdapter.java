package com.hjhq.teamface.project.adapter;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
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

import java.util.List;

/**
 * 项目列表适配器
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class ProjectAdapter extends BaseQuickAdapter<ProjectListBean.DataBean.DataListBean, BaseViewHolder> {
    public ProjectAdapter(List<ProjectListBean.DataBean.DataListBean> data) {
        super(R.layout.project_list_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProjectListBean.DataBean.DataListBean item) {
        CircularRingPercentageView circularRingPercentageView = helper.getView(R.id.circle_iv);
        TextView tvProgress = helper.getView(R.id.tv_progress);
        TextView tv_sum = helper.getView(R.id.tv_sum);

        HorizontalProgressView horizontalProgress = helper.getView(R.id.horizontal_progress);
        int progress = 0;
        int taskCount = TextUtil.parseInt(item.getTask_count());
        int taskCompleteCount = TextUtil.parseInt(item.getTask_complete_count());
        if (ProjectConstants.PROJECT_PROGRESS_STATUS_INPUT.equals(item.getProject_progress_status())) {
            //手动填写
            progress = TextUtil.parseInt(item.getProject_progress_content());
        } else {
            //自动计算
            if (taskCount != 0) {
                progress = (int) Math.round((taskCompleteCount * 100.0 / taskCount));
            }
        }

        //进度
         String progressContent = progress + "%";
        circularRingPercentageView.setProgress(progress);
        SpannableString spannableString = new SpannableString(progressContent);
        spannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, progressContent.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(12, true), progressContent.length() - 1, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvProgress.setText(spannableString);

        int no_begin_number = TextUtil.parseInt(item.getNo_begin_number());
        int stop_num = TextUtil.parseInt(item.getStop_number());
        int doing_num = TextUtil.parseInt(item.getDoing_number());
        int overdue_num = TextUtil.parseInt(item.getOverdue_no_begin_number());

        int doing = 0;
        int stop = 0;
        int over = 0;
        int commplete = 0;

        taskCount = stop_num+no_begin_number+doing_num+overdue_num+taskCompleteCount;
        if (taskCount != 0) {
            stop = (int) Math.round(((stop_num+no_begin_number) * 100.0 / taskCount));
            doing = (int) Math.round(((doing_num) * 100.0 / taskCount));
            over = (int) Math.round(((overdue_num) * 100.0 / taskCount));
            commplete = (int) Math.round(((taskCompleteCount) * 100.0 / taskCount));
        }
        horizontalProgress.setMaxCount(100);
        horizontalProgress.setCurrentCount(commplete,doing,stop,over);
        String str="<font color='#3CBB8'>"+taskCompleteCount+"</font>"+"/ "+"<font color='#008FE5'>"+doing_num+"</font>"+"/ "
                     +"<font color='#666666'>"+(stop_num+no_begin_number)+"</font>"+"/ "+"<font color='#F5222D'>"+overdue_num+"</font>";
        tv_sum.setText(Html.fromHtml(str));
        //是否收藏
        helper.setText(R.id.tv_name, item.getName());

        /*ImageLoader.loadImage(mContext,
                "1".equals(item.getStar_level()) ? R.drawable.project_icon_mark_selecter : R.drawable.project_icon_mark_unselecter,
                helper.getView(R.id.iv_star));*/
        ImageView start =  helper.getView(R.id.iv_star);
        if ( "1".equals(item.getStar_level())){
            ImageLoader.loadImage(mContext, R.drawable.project_icon_mark_selecter,start);
            start.setVisibility(View.VISIBLE);
        }else {
            start.setVisibility(View.INVISIBLE);
        }


        ImageView ivState = helper.getView(R.id.iv_state);
        if ("1".equals(item.getProject_status())) {
            //归档
            ivState.setVisibility(View.VISIBLE);
            ivState.setImageResource(R.drawable.project_icon_finish_state);
        } else if ("2".equals(item.getProject_status())) {
            //是暂停
            ivState.setVisibility(View.VISIBLE);
            ivState.setImageResource(R.drawable.project_icon_stop_state);
        }  else if ("1".equals(item.getDeadline_status())) {
            //是否超期
            ivState.setVisibility(View.VISIBLE);
            ivState.setImageResource(R.drawable.project_icon_over_state);
        } else {
            ivState.setVisibility(View.GONE);
        }

        String tempId = item.getTemp_id();
        ImageView ivTemp = helper.getView(R.id.iv_project_temp);
        String iconName;
        //模板
        if (!TextUtils.isEmpty(item.getPic_url())) {
            ImageLoader.loadImage(mContext, item.getPic_url(), R.drawable.project_temp_bg_1, ivTemp);
        } else {
            if (TextUtils.isEmpty(item.getSystem_default_pic())) {
                if (TextUtil.parseInt(tempId) == 0) {
                    iconName = "project_temp_bg_1";
                } else {
                    iconName = "project_temp_bg_" + tempId;
                }
            } else {
                switch (item.getSystem_default_pic()) {
                    case "one":
                        iconName = "project_sys_default_bg1";
                        break;
                    case "two":
                        iconName = "project_sys_default_bg2";
                        break;
                    case "three":
                        iconName = "project_sys_default_bg3";
                        break;
                    case "four":
                        iconName = "project_sys_default_bg4";
                        break;
                    case "five":
                        iconName = "project_sys_default_bg5";
                        break;
                    case "six":
                        iconName = "project_sys_default_bg6";
                        break;
                    default:
                        iconName = "project_temp_bg_1";
                }
            }
            int drawable = mContext.getResources().getIdentifier(iconName, "drawable", mContext.getPackageName());
            ImageLoader.loadImage(mContext, drawable, ivTemp);
        }
    }
}

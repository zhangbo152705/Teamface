package com.hjhq.teamface.project.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.ProjectPicklistStatusBean;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.common.view.FlowLayout;
import com.hjhq.teamface.project.R;
import com.hjhq.teamface.project.bean.PersonalSubTaskBean;
import com.hjhq.teamface.project.bean.SubTaskBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目列表适配器
 *
 * @author Administrator
 * @date 2018/4/10
 */

public class SubPersionTaskCardApter extends BaseQuickAdapter<PersonalSubTaskBean, BaseViewHolder> {

    boolean isSelect = false;

    public SubPersionTaskCardApter(List<PersonalSubTaskBean> data) {
        super(R.layout.project_task_card_item, data);
    }


    @SuppressWarnings("ResourceType")
    @Override
    protected void convert(BaseViewHolder helper,PersonalSubTaskBean item) {
        TextView tvTaskName = helper.getView(R.id.tv_task_name);
        ImageView ivTaskHead = helper.getView(R.id.iv_head);
        TextView tvTaskTime = helper.getView(R.id.tv_time);
        TextView tvCompleteNumber = helper.getView(R.id.tv_complete_number);
        FlowLayout flowPicklistTag = helper.getView(R.id.flow_picklist_tag);
        TextView tvSubTask = helper.getView(R.id.tv_sub_task);
        CardView cardView = helper.getView(R.id.card_view);
        LinearLayout picklist_status_li = helper.getView(R.id.picklist_status_li);
        TextView picklist_status_text = helper.getView(R.id.picklist_status_text);
        ImageView iv_icon = helper.getView(R.id.iv_icon);


        //完成激活
        boolean completeStatus =  item.getComplete_status().equals("1");
        if (completeStatus){
            tvTaskName.setTextColor(ColorUtils.resToColor(mContext,  R.color.project_task_foot_text_size));
        }else{
            tvTaskName.setTextColor(ColorUtils.resToColor(mContext, R.color.black_17));
        }

        //任务名称
        tvTaskName.setText(item.getName());

        //创建人
       // String createBy = item.getCreate_by();
        //执行人
        if (TextUtil.isEmpty(item.getEmployee_name())){
            ivTaskHead.setVisibility(View.GONE);
        }else {
            ImageLoader.loadCircleImage(mContext,  item.getPicture(), ivTaskHead, item.getEmployee_name());
            ivTaskHead.setVisibility(View.VISIBLE);
        }


        //状态
        ArrayList<ProjectPicklistStatusBean> status = item.getPicklist_status();
        if(!CollectionUtils.isEmpty(status)){
            picklist_status_text.setText(status.get(0).getLabel());
            picklist_status_li.setBackgroundColor(ColorUtils.hexToColor(status.get(0).getColor()));
            if(status.get(0).getValue() != null && status.get(0).getValue().equals("0")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.project_nostart);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#d9d9d9", "#000000"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("1")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.taskcard_state);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#4A90E2", "#000000"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("2")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.project_suspended);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#d9d9d9", "#000000"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("3")){
                iv_icon.setImageResource(com.hjhq.teamface.customcomponent.R.drawable.project_complete);
                picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#36CFC9", "#000000"));
            }
        }
        helper.addOnClickListener(R.id.picklist_status_li);





        //时间
        long deadline = TextUtil.parseLong(item.getEnd_time());
        long startTime = TextUtil.parseLong(item.getStart_time());
        helper.setVisible(R.id.tv_time, deadline != 0L || startTime != 0L);

        String taskTime = DateTimeUtil.AuthToTime(startTime);
        if (deadline != 0L) {
            if (startTime != 0L) {
                taskTime += " 至 ";
            } else {
                taskTime = "截止时间 ";
            }
            String taskDeadline = DateTimeUtil.AuthToTime(deadline);
            taskTime += taskDeadline;
            TextUtil.setText(tvTaskTime, taskTime);
        }

        long overTime = System.currentTimeMillis() - deadline;
        //时间图标字体变换
        if (deadline != 0L && overTime > 0) {
            tvTaskTime.setBackgroundColor(ColorUtils.hexToColor("#F41C0D"));
            tvTaskTime.setTextColor(ColorUtils.hexToColor("#FFFFFFF"));

        } else {
            tvTaskTime.setBackgroundColor(ColorUtils.hexToColor("#D9D9D9"));
            tvTaskTime.setTextColor(ColorUtils.hexToColor("#787878"));
        }

        //子任务
        if (item.getSubtotal() == 0 ) {
            tvSubTask.setVisibility(View.GONE);
        } else {
            tvSubTask.setVisibility(View.VISIBLE);
            int subfinish = item.getSubfinishtotal();
            TextUtil.setText(tvSubTask, subfinish + "/" + item.getSubtotal());
        }

        //标签
        try {
            flowPicklistTag.removeAllViews();
            if (CollectionUtils.isEmpty(item.getPicklist_tag())) {
                flowPicklistTag.setVisibility(View.GONE);
            } else {
                flowPicklistTag.setVisibility(View.VISIBLE);

                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(WindowManager.LayoutParams.WRAP_CONTENT
                        , WindowManager.LayoutParams.WRAP_CONTENT);

                lp.rightMargin = 15;
                for (ProjectLabelBean label : item.getPicklist_tag()) {
                    if (TextUtil.isEmpty(label.getName())) {
                        continue;
                    }
                    TextView view = new TextView(mContext);
                    view.setText(label.getName());
                    view.setTextColor(Color.WHITE);
                    view.setTextSize( 12f);
                    view.setGravity(Gravity.CENTER);
                    view.setBackgroundResource(R.drawable.project_task_tag_stroke);
                    GradientDrawable myGrad = (GradientDrawable) view.getBackground();
                    if ("#FFFFFF" == label.getColour()) {
                        view.setTextColor(ColorUtils.resToColor(mContext, R.color.black_4a));
                    }
                    myGrad.setColor(ColorUtils.hexToColor(label.getColour(), "#000000"));
                    flowPicklistTag.addView(view, lp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

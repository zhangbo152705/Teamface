/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hjhq.teamface.common.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.bean.ProjectPicklistStatusBean;
import com.hjhq.teamface.basis.bean.Row;
import com.hjhq.teamface.basis.bean.RowBean;
import com.hjhq.teamface.basis.bean.TaskInfoBean;
import com.hjhq.teamface.basis.constants.MemoConstant;
import com.hjhq.teamface.basis.constants.ProjectConstants;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.CollectionUtils;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.CustomDataUtil;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.ParseUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.utils.ApproveUtils;
import com.hjhq.teamface.common.view.FlowLayout;
import com.hjhq.teamface.common.view.boardview.DragItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务看板适配器
 *
 * @author Administrator
 */
public class TaskItemAdapter extends DragItemAdapter<TaskInfoBean, TaskItemAdapter.ViewHolder> {
    //查看详情样式
    public static int VIEW_MODE = 0;
    //添加/编辑样式
    public static int EDIT_MODE = 1;
    private OnItemClickListener mOnItemClickListener;
    //0正常界面,1添加界面(可移除)
    private int type = VIEW_MODE;
    private boolean mDragOnLongPress;
    private Context mContext;
    private boolean chooseType = false;

    public interface OnItemClickListener {
        void onItemClick(DragItemAdapter adapter, View view, int position);

        void onItemChildClick(DragItemAdapter adapter, View view, int position);

        void onItemLongClick(DragItemAdapter adapter, View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setChooseType(boolean chooseType) {
        this.chooseType = chooseType;
    }

    public TaskItemAdapter(List<TaskInfoBean> list, boolean dragOnLongPress) {
        mDragOnLongPress = dragOnLongPress;
        if (list == null) list = new ArrayList<>();
        setItemList(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.project_task_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);


        if (type == 1) {
            holder.ivRemove.setVisibility(View.VISIBLE);
            holder.ivRemove.setOnClickListener(v -> {
                getItemList().remove(position);
                notifyDataSetChanged();
                if (getItemCount() == 0) {
                    EventBusUtils.sendEvent(new MessageBean(0, MemoConstant.MEMO_RELEVANT_IS_EMPTY, null));
                }
            });
        } else {
            holder.ivRemove.setVisibility(View.GONE);
        }
        TaskInfoBean item = getItem(position);
        if (chooseType) {
            if (item.isCheck()) {
                holder.ivChoose.setVisibility(View.VISIBLE);
            } else {
                holder.ivChoose.setVisibility(View.GONE);
            }
        } else {
            holder.ivChoose.setVisibility(View.GONE);
        }
        if (mOnItemClickListener != null) {
            holder.mGrabView.setOnClickListener(v -> mOnItemClickListener.onItemClick(this, holder.mGrabView, getPositionForItem(item)));
            holder.ivCheck.setOnClickListener(v -> mOnItemClickListener.onItemChildClick(this, holder.ivCheck, getPositionForItem(item)));
            if (!mDragOnLongPress) {
                holder.mGrabView.setOnLongClickListener(v -> {
                    mOnItemClickListener.onItemLongClick(this, holder.mGrabView, getPositionForItem(item));
                    return false;
                });
            }
        }
        switch (item.getDataType()) {
            case ProjectConstants.DATA_MEMO_TYPE:
                //备忘录
                holder.cardApprove.setVisibility(View.GONE);
                holder.cardTask.setVisibility(View.GONE);
                holder.cardCustom.setVisibility(View.GONE);
                holder.cardMemo.setVisibility(View.VISIBLE);
                holder.cardCustom2.setVisibility(View.GONE);
                holder.cardEmail.setVisibility(View.GONE);
                SpannableString spannableString = new SpannableString("备忘录: " + item.getTitle());
                spannableString.setSpan(new ForegroundColorSpan(ColorUtils.resToColor(mContext, R.color.black_17)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvMemoTitle.setText(spannableString);
                if (item.getCreate_time() > 0) {
                    holder.tvMemoCreateTime.setText(DateTimeUtil.longToStr(item.getCreate_time(), "yyyy-MM-dd HH:mm"));
                }
                ImageLoader.loadCircleImage(mContext, item.getPic_url(), holder.ivMemoHead, item.getCreateObj().getName());
                break;
            case ProjectConstants.DATA_TASK_TYPE:
                //任务
                adapterTask(holder, item);
                break;
            case ProjectConstants.DATA_CUSTOM_TYPE:
                //自定义
                holder.cardApprove.setVisibility(View.GONE);
                holder.cardMemo.setVisibility(View.GONE);
                holder.cardTask.setVisibility(View.GONE);
                holder.cardEmail.setVisibility(View.GONE);
                final Row itemRows = item.getRows();
                if (itemRows != null) {
                    final List<RowBean> row1 = itemRows.getRow1();
                    final List<RowBean> row2 = itemRows.getRow2();
                    final List<RowBean> row3 = itemRows.getRow3();
                    if (CollectionUtils.isEmpty(row1) && CollectionUtils.isEmpty(row2) && CollectionUtils.isEmpty(row3)) {
                        holder.cardCustom.setVisibility(View.VISIBLE);
                        holder.cardCustom2.setVisibility(View.GONE);
                    } else {
                        holder.cardCustom.setVisibility(View.GONE);
                        holder.cardCustom2.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.cardCustom.setVisibility(View.VISIBLE);
                    holder.cardCustom2.setVisibility(View.GONE);
                }

                showCustomData(holder, item);
                break;
            case ProjectConstants.DATA_APPROVE_TYPE:
                //审批
                holder.cardMemo.setVisibility(View.GONE);
                holder.cardTask.setVisibility(View.GONE);
                holder.cardCustom.setVisibility(View.GONE);
                holder.cardCustom2.setVisibility(View.GONE);
                holder.cardApprove.setVisibility(View.VISIBLE);
                holder.cardEmail.setVisibility(View.GONE);
                holder.tvApproveCommitTime.setText("申请时间:" + DateTimeUtil.longToStr(item.getCreate_time(), "yyyy-MM-dd HH:mm"));
                SpannableString approveSpan = new SpannableString("审批: " + item.getBegin_user_name() + "-" + item.getProcess_name());
                approveSpan.setSpan(new ForegroundColorSpan(ColorUtils.resToColor(mContext, R.color.black_17)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvApproveTitle.setText(approveSpan);
                ImageLoader.loadCircleImage(mContext, item.getPicture(), holder.ivApproveHead, item.getBegin_user_name());
                if (!TextUtils.isEmpty(item.getProcess_status())) {
                    holder.tvApproveSataus.setVisibility(View.VISIBLE);
                    TextUtil.setText(holder.tvApproveSataus, ApproveUtils.state2String(item.getProcess_status()));
                    //设置审批状态颜色
                    GradientDrawable myGrad = (GradientDrawable) holder.tvApproveSataus.getBackground();
                    String color = ApproveUtils.state2Color(item.getProcess_status());
                    myGrad.setColor(ColorUtils.hexToColor(color));
                } else {
                    holder.tvApproveSataus.setVisibility(View.INVISIBLE);
                }
                break;
            case ProjectConstants.DATA_EMAIL_TYPE:
                //邮件
                holder.cardMemo.setVisibility(View.GONE);
                holder.cardTask.setVisibility(View.GONE);
                holder.cardCustom.setVisibility(View.GONE);
                holder.cardCustom2.setVisibility(View.GONE);
                holder.cardApprove.setVisibility(View.GONE);
                holder.cardEmail.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(item.getTitle())) {
                    TextUtil.setText(holder.tvEmailContent, "[无主题]");
                } else {
                    TextUtil.setText(holder.tvEmailContent, item.getTitle());
                }
                /*if (TextUtils.isEmpty(item.getTitle())) {
                    TextUtil.setText(holder.tvEmailContent, "[无内容]");
                } else {
                    String content = Html.fromHtml(item.getTitle()).toString();
                    if (TextUtils.isEmpty(content)) {
                        TextUtil.setText(holder.tvEmailContent, "[无内容]");
                    } else {
                        //web端添加的标记
                        content = content.replace("++--+!!!+--++", "");
                        if (content.length() > 25) {
                            content = content.substring(0, 25);
                        }
                        content = content.replace("  ", "").replace("\n", "").replace("\r\n", "").replace("\t", "");
                        content = content.replace("￼", "[图]");
                        TextUtil.setText(holder.tvEmailContent, content);
                        // Log.e("内容",content);
                    }
                }*/
                TextUtil.setText(holder.tvEmailTime, DateTimeUtil.longToStr(item.getCreate_time(), "yyyy-MM-dd HH:mm"));
                ImageLoader.loadHoleImage(holder.ivEmailAvatar.getContext(), "",
                        holder.ivEmailAvatar, item.getText_name());


                break;
            default:
                break;
        }
        holder.itemView.setTag(getItem(position));
    }

    private void showCustomData(ViewHolder holder, TaskInfoBean item) {

        if (holder.cardCustom.getVisibility() == View.VISIBLE) {
            customStyle1(holder, item);
        } else {
            customStyle2(holder, item);
        }
    }

    public void setDragOnLongPress(boolean dragOnLongPress) {
        mDragOnLongPress = dragOnLongPress;
    }

    /**
     * 显示单行
     *
     * @param holder
     * @param item
     */
    private void customStyle1(ViewHolder holder, TaskInfoBean item) {
        List<RowBean> rows = item.getRow();
        if (!CollectionUtils.isEmpty(rows)) {
            holder.tvCustomTitle.setVisibility(View.VISIBLE);
            RowBean rowBean = rows.get(0);
            ParseUtil.setTempValue(holder.tvCustomTitle, rowBean, false);

            String moduleName = item.getModule_name();
            if (TextUtil.isEmpty(moduleName)) {
                moduleName = "自定义模块";
            }
            SpannableString approveSpan = new SpannableString(moduleName + ": " + holder.tvCustomTitle.getText().toString());
            approveSpan.setSpan(new ForegroundColorSpan(ColorUtils.resToColor(mContext, R.color.black_17)), 0, moduleName.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvCustomTitle.setText(approveSpan);
        } else {
            holder.tvCustomTitle.setVisibility(View.INVISIBLE);
        }
        //显示自定义模块图标
        loadImage(item, holder, holder.ivCustomLogo);
        final List<RowBean> list = item.getRow();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if ("personnel_principal".equals(list.get(i).getName())) {
                    final String value = list.get(i).getValue();
                    JSONArray ja = JSONArray.parseArray(value);
                    if (ja != null && ja.size() > 0) {
                        JSONObject jsonObject = ja.getJSONObject(0);
                        String name = jsonObject.getString("name");
                        String url = jsonObject.getString("picture");
                        ImageLoader.loadCircleImage(mContext, url, holder.ivCustomHead, name);
                    }

                }
            }
        }
    }

    /**
     * 按自定义列表数据样式显示
     *
     * @param holder
     * @param item
     */
    private void customStyle2(ViewHolder holder, TaskInfoBean item) {
        String moduleName = item.getModule_name();
        if (TextUtil.isEmpty(moduleName)) {
            moduleName = "自定义模块";
        }
        TextUtil.setText(holder.tvModuleName, moduleName);
        loadImage(item, holder, holder.ivCustomLogo2);
        final Row itemRows = item.getRows();
        if (itemRows != null) {
            final List<RowBean> t1 = itemRows.getRow1();
            final List<RowBean> t2 = itemRows.getRow2();
            final List<RowBean> t3 = itemRows.getRow3();
            if (!CollectionUtils.isEmpty(t1)) {
                //第一行
                holder.tvDataTitle.setVisibility(View.VISIBLE);
                RowBean rowBean = t1.get(0);
                CustomDataUtil.setTempValue(holder.tvDataTitle, rowBean, false);
            } else {
                holder.tvDataTitle.setVisibility(View.GONE);
            }
            if (!CollectionUtils.isEmpty(t2)) {
                //第二行
                holder.llContent.setVisibility(View.VISIBLE);
                holder.llContent1.setVisibility(View.VISIBLE);
                RowBean rowBean1 = t2.get(0);
                TextUtil.setText(holder.tvName1, rowBean1.getLabel());
                CustomDataUtil.setTempValue(holder.tvValue1, rowBean1, false);

                if (t2.size() > 1) {
                    holder.llContent2.setVisibility(View.VISIBLE);
                    RowBean rowBean2 = t2.get(1);
                    TextUtil.setText(holder.tvName2, rowBean2.getLabel());
                    CustomDataUtil.setTempValue(holder.tvValue2, rowBean2, false);
                } else {
                    holder.llContent2.setVisibility(View.GONE);
                }
                if (t2.size() > 2) {
                    holder.llContent3.setVisibility(View.VISIBLE);
                    RowBean rowBean3 = t2.get(2);
                    TextUtil.setText(holder.tvName3, rowBean3.getLabel());
                    CustomDataUtil.setTempValue(holder.tvValue3, rowBean3, false);
                } else {
                    holder.llContent3.setVisibility(View.GONE);
                }
            } else {
                holder.llContent.setVisibility(View.GONE);
            }
            if (!CollectionUtils.isEmpty(t3)) {
                //第三行
                holder.llType.setVisibility(View.VISIBLE);
                holder.tvTag1.setVisibility(View.VISIBLE);
                RowBean rowBean1 = t3.get(0);
                CustomDataUtil.setTempValue(holder.tvTag1, rowBean1, true);

                if (TextUtils.isEmpty(holder.tvTag1.getText())) {
                    holder.tvTag1.setVisibility(View.GONE);
                }
                if (t3.size() > 1) {
                    holder.tvTag2.setVisibility(View.VISIBLE);
                    RowBean rowBean2 = t3.get(1);
                    CustomDataUtil.setTempValue(holder.tvTag2, rowBean2, true);
                    if (TextUtils.isEmpty(holder.tvTag2.getText())) {
                        holder.tvTag2.setVisibility(View.GONE);
                    }
                } else {
                    holder.tvTag2.setVisibility(View.GONE);
                }
                if (t3.size() > 2) {
                    holder.tvTag3.setVisibility(View.VISIBLE);
                    RowBean rowBean3 = t3.get(2);
                    CustomDataUtil.setTempValue(holder.tvTag3, rowBean3, true);
                    if (TextUtils.isEmpty(holder.tvTag3.getText())) {
                        holder.tvTag3.setVisibility(View.GONE);
                    }
                } else {
                    holder.tvTag3.setVisibility(View.GONE);
                }
            } else {
                holder.llType.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 自定义数据模块图标
     *
     * @param item
     * @param holder
     * @param imageView
     */
    private void loadImage(TaskInfoBean item, ViewHolder holder, ImageView imageView) {

        GradientDrawable drawable = (GradientDrawable) ((RelativeLayout) imageView.getParent()).getBackground();
        drawable.setColor(Color.WHITE);

        if ("1".equals(item.getIcon_type())) {//1.网络图片  其他加载本地图片
            ImageLoader.loadRoundImage(imageView.getContext(), item.getIcon_url(),
                    imageView, R.drawable.project_task_item_custom_icon);
        } else{// if ("0".equals(item.getIcon_type()))
            String iconColor = item.getIcon_color();
            drawable.setColor(ColorUtils.hexToColor(iconColor, "#3689E9"));
            String imageUrl = item.getIcon_url();
            if (TextUtil.isEmpty(imageUrl)) {
                ImageLoader.loadRoundImage(mContext, R.drawable.project_task_item_custom_icon, imageView);
            } else {
                //将 - 转换成 _
                String replace = imageUrl.replace("-", "_");
                int resId = mContext.getResources().getIdentifier(replace, "drawable", mContext.getPackageName());
                ImageLoader.loadRoundImage(mContext, resId, imageView, R.drawable.project_task_item_custom_icon);
            }

        }


    }

    /**
     * 适配任务
     *
     * @param holder
     * @param item
     */
    private void adapterTask(ViewHolder holder, TaskInfoBean item) {
        holder.cardApprove.setVisibility(View.GONE);
        holder.cardTask.setVisibility(View.VISIBLE);
        holder.cardCustom.setVisibility(View.GONE);
        holder.cardMemo.setVisibility(View.GONE);
        holder.cardCustom2.setVisibility(View.GONE);
        holder.cardEmail.setVisibility(View.GONE);
        holder.ivApproveStatus.setVisibility(View.GONE);
        //complete_status 0：未完成 1：完成 "complete_status":"1",
        //check_status 0：无需校验 1：需校验 "check_status":"0",
        //passed_status 0：待校验 1：通过 2：驳回 "passed_status":"0",
        if ("1".equals(item.getCheck_status())) {
            holder.ivApproveStatus.setVisibility(View.VISIBLE);
            if ("1".equals(item.getComplete_status())) {
                if ("0".equals(item.getPassed_status())) {
                    holder.ivApproveStatus.setImageResource(R.drawable.project_icon_check_wait);
                } else if ("1".equals(item.getPassed_status())) {
                    holder.ivApproveStatus.setImageResource(R.drawable.project_icon_check_pass);
                } else if ("2".equals(item.getPassed_status())) {
                    holder.ivApproveStatus.setImageResource(R.drawable.project_icon_check_reject);
                } else {
                    holder.ivApproveStatus.setVisibility(View.GONE);
                }
            } else {
                if ("2".equals(item.getPassed_status())) {
                    holder.ivApproveStatus.setImageResource(R.drawable.project_icon_check_reject);
                }
            }
        } else {
            holder.ivApproveStatus.setVisibility(View.GONE);
        }

        //完成激活
        boolean completeStatus = "1".equals(item.getComplete_status());
        holder.ivCheck.setSelected(completeStatus);


        holder.tvTaskName.setTextColor(ColorUtils.resToColor(mContext,
                completeStatus ? R.color.task_foot_text_size : R.color.black_17));
        //任务名称
        holder.tvTaskName.setText(item.getText_name());

        //执行人
        List<Member> members = item.getPersonnel_execution();
        if (!CollectionUtils.isEmpty(members)) {
            holder.ivTaskHead.setVisibility(View.VISIBLE);
            ImageLoader.loadCircleImage(mContext, members.get(0).getPicture(), holder.ivTaskHead, members.get(0).getName());
        } else {
            holder.ivTaskHead.setVisibility(View.GONE);
        }

        //状态
        ArrayList<ProjectPicklistStatusBean> status = item.getPicklist_status();
        if(!CollectionUtils.isEmpty(status)){
            holder.picklist_status_text.setText(status.get(0).getLabel());
            if(status.get(0).getValue() != null && status.get(0).getValue().equals("0")){
                holder.iv_icon.setImageResource(R.drawable.project_nostart);
                holder.picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#EBEDF0"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("1")){
                holder.iv_icon.setImageResource(R.drawable.taskcard_state);
                holder.picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#8db4eb"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("2")){
                holder.iv_icon.setImageResource(R.drawable.project_suspended);
                holder.picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#EBEDF0"));
            }else if(status.get(0).getValue() != null && status.get(0).getValue().equals("3")){
                holder.iv_icon.setImageResource(R.drawable.project_complete);
                holder.picklist_status_li.setBackgroundColor(ColorUtils.hexToColor( "#D6F4E9"));
            }
        }
        //helper.addOnClickListener(R.id.picklist_status_li);

        //优先级
        ArrayList<ProjectPicklistStatusBean> priority = item.getPicklist_priority();
        if(!CollectionUtils.isEmpty(priority)){
            holder.ll_card.setBackgroundColor(ColorUtils.hexToColor(priority.get(0).getColor(), "#FFFFFF"));
        }else {
            holder.ll_card.setBackgroundColor(ColorUtils.hexToColor("#FFFFFF"));
        }

        //激活次数
        //int completeNumber = TextUtil.parseInt(item.getComplete_number());
        //TextUtil.setText(holder.tvCompleteNumber, completeNumber + "");
       // holder.tvCompleteNumber.setVisibility(completeNumber == 0 ? View.GONE : View.VISIBLE);

        //时间
        long deadline = TextUtil.parseLong(item.getDatetime_deadline());
        long startTime = TextUtil.parseLong(item.getDatetime_starttime());

        holder.tvTaskTime.setVisibility(deadline != 0L ? View.VISIBLE : View.GONE);

        String taskTime = DateTimeUtil.AuthToTime(startTime);
        if (deadline != 0) {
            if (startTime != 0) {
                taskTime += " 至 ";
            } else {
                taskTime = "截止时间 ";
            }
            String taskDeadline = DateTimeUtil.AuthToTime(deadline);
            taskTime += taskDeadline;
            TextUtil.setText(holder.tvTaskTime, taskTime);
        }

        long overTime = System.currentTimeMillis() - deadline;
        //超时后 时间变红
        if (deadline != 0L && overTime > 0) {
            holder.tvTaskTime.setTextColor(ColorUtils.hexToColor("#FF3C26"));
        } else {
            holder.tvTaskTime.setTextColor(ColorUtils.hexToColor("#5c5c69"));
        }

        //子任务
        int subtotal = TextUtil.parseInt(item.getSub_task_count());
        int subCompleteCount = TextUtil.parseInt(item.getSub_task_complete_count());
        if (subtotal == 0) {
            holder.tvSubTask.setVisibility(View.GONE);
        } else {
            holder.tvSubTask.setVisibility(View.VISIBLE);
            TextUtil.setText(holder.tvSubTask, subCompleteCount + "/" + subtotal);
        }

        //完成激活

        //标签
        try {
            holder.flowPicklistTag.removeAllViews();
            if (CollectionUtils.isEmpty(item.getPicklist_tag())) {
                holder.flowPicklistTag.setVisibility(View.GONE);
            } else {
                holder.flowPicklistTag.setVisibility(View.VISIBLE);

                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = 5;
                lp.rightMargin = 5;
                lp.topMargin = 5;
                lp.bottomMargin = 5;
                for (ProjectLabelBean label : item.getPicklist_tag()) {
                    if (TextUtil.isEmpty(label.getName())) {
                        continue;
                    }
                    TextView view = new TextView(mContext);
                    view.setText(label.getName());
                    view.setTextColor(Color.WHITE);
                    view.setTextSize(12f);
                    view.setMaxLines(1);
                    view.setEllipsize(TextUtils.TruncateAt.END);
                    view.setGravity(Gravity.CENTER);
                    view.setBackgroundResource(R.drawable.project_task_tag_stroke);
                    GradientDrawable myGrad = (GradientDrawable) view.getBackground();
                    if ("#FFFFFF" == label.getColour()) {
                        view.setTextColor(ColorUtils.resToColor(mContext, R.color.black_4a));
                    }
                    myGrad.setColor(ColorUtils.hexToColor(label.getColour(), "#000000"));
                    holder.flowPicklistTag.addView(view, lp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getUniqueItemId(int position) {
        return getItem(position).getId();
    }

    public TaskInfoBean getItem(int position) {
        return mItemList.get(position);
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        View cardTask;
        View cardApprove;
        View cardMemo;
        View cardCustom;
        View cardCustom2;
        View cardEmail;

        //移除
        ImageView ivRemove;
        //选中
        ImageView ivChoose;

        TextView tvTaskName;
        TextView tvCustomTitle;
        TextView tvMemoTitle;
        TextView tvApproveTitle;
        TextView tvApproveSataus;
        TextView tvApproveCommitTime;
        TextView tvMemoCreateTime;

        ImageView ivCustomHead;
        ImageView ivCustomLogo;
        ImageView ivMemoHead;
        ImageView ivApproveHead;
        ImageView ivApproveStatus;
        ImageView ivTaskHead;

        RelativeLayout rlCustonLogoBg;
        RelativeLayout rlCustonLogoBg2;


        //任务
        TextView tvTaskTime;
        ImageView ivCheck;
        TextView tvCompleteNumber;
        TextView tvSubTask;
        FlowLayout flowPicklistTag;
        TextView picklist_status_text;
        ImageView iv_icon;
        LinearLayout picklist_status_li;
        LinearLayout ll_card;


        //自定义2
        ImageView ivCustomLogo2;
        TextView tvModuleName;
        TextView tvDataTitle;
        LinearLayout llContent;
        LinearLayout llContent1;
        LinearLayout llContent2;
        LinearLayout llContent3;
        TextView tvName1;
        TextView tvName2;
        TextView tvName3;
        TextView tvValue1;
        TextView tvValue2;
        TextView tvValue3;
        View llType;
        TextView tvTag1;
        TextView tvTag2;
        TextView tvTag3;

        //邮件
        TextView tvEmailContent;
        TextView tvEmailTime;
        ImageView ivEmailAvatar;

        ViewHolder(final View itemView) {
            super(itemView, R.id.item_layout, mDragOnLongPress);
            ivRemove = itemView.findViewById(R.id.iv_remove);
            tvTaskName = itemView.findViewById(R.id.tv_task_name);
            tvCustomTitle = itemView.findViewById(R.id.tv_custom_title);
            tvMemoTitle = itemView.findViewById(R.id.tv_memo_title);
            tvApproveTitle = itemView.findViewById(R.id.tv_approve_title);
            tvApproveSataus = itemView.findViewById(R.id.tv_approve_state);
            tvApproveCommitTime = itemView.findViewById(R.id.tv_approve_time);
            tvMemoCreateTime = itemView.findViewById(R.id.tv_memo_time);

            rlCustonLogoBg = itemView.findViewById(R.id.rl_icon);
            rlCustonLogoBg2 = itemView.findViewById(R.id.rl_icon2);
            ivCustomHead = itemView.findViewById(R.id.iv_custom_head);
            ivCustomLogo = itemView.findViewById(R.id.iv_custom_icon);
            ivMemoHead = itemView.findViewById(R.id.iv_memo_head);
            ivApproveHead = itemView.findViewById(R.id.iv_approve_head);
            ivApproveStatus = itemView.findViewById(R.id.iv_state);
            ivTaskHead = itemView.findViewById(R.id.iv_head);

            ivCheck = itemView.findViewById(R.id.iv_check);
            ivChoose = itemView.findViewById(R.id.iv_choose_item);
            tvTaskTime = itemView.findViewById(R.id.tv_time);
            tvCompleteNumber = itemView.findViewById(R.id.tv_complete_number);
            flowPicklistTag = itemView.findViewById(R.id.flow_picklist_tag);
            tvSubTask = itemView.findViewById(R.id.tv_sub_task);
            picklist_status_text = itemView.findViewById(R.id.picklist_status_text);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            picklist_status_li= itemView.findViewById(R.id.picklist_status_li);
            ll_card = itemView.findViewById(R.id.ll_card);
            //自定义2
            ivCustomLogo2 = itemView.findViewById(R.id.iv_custom_icon2);
            tvModuleName = itemView.findViewById(R.id.tv_custom_title2);
            tvDataTitle = itemView.findViewById(R.id.tv_data_title);
            llContent = itemView.findViewById(R.id.ll_content);
            llContent1 = itemView.findViewById(R.id.ll_content1);
            llContent2 = itemView.findViewById(R.id.ll_content2);
            llContent3 = itemView.findViewById(R.id.ll_content3);
            tvName1 = itemView.findViewById(R.id.tv_name1);
            tvName2 = itemView.findViewById(R.id.tv_name2);
            tvName3 = itemView.findViewById(R.id.tv_name3);
            tvValue1 = itemView.findViewById(R.id.tv_value1);
            tvValue2 = itemView.findViewById(R.id.tv_value2);
            tvValue3 = itemView.findViewById(R.id.tv_value3);
            llType = itemView.findViewById(R.id.ll_type);
            tvTag1 = itemView.findViewById(R.id.tv_type_1);
            tvTag2 = itemView.findViewById(R.id.tv_type_2);
            tvTag3 = itemView.findViewById(R.id.tv_type_3);
            //邮件
            tvEmailContent = itemView.findViewById(R.id.tv_email_title);
            tvEmailTime = itemView.findViewById(R.id.tv_email_time);
            ivEmailAvatar = itemView.findViewById(R.id.iv_email_head);


            cardTask = itemView.findViewById(R.id.task_layout);
            cardApprove = itemView.findViewById(R.id.approve_layout);
            cardMemo = itemView.findViewById(R.id.memo_layout);
            cardCustom = itemView.findViewById(R.id.custom_layout);
            cardCustom2 = itemView.findViewById(R.id.custom_layout2);
            cardEmail = itemView.findViewById(R.id.email_layout);
        }

        @Override
        public void onItemClicked(View view) {
//            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }
    }
}

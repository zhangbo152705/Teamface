package com.hjhq.teamface.project.adapter;

import android.text.SpannableString;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.UploadFileBean;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.FileTypeUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.common.R;
import com.hjhq.teamface.common.bean.CommentDetailResultBean;
import com.hjhq.teamface.common.view.RichEditText;
import com.hjhq.teamface.emoji.EmojiUtil;
import com.hjhq.teamface.project.bean.TaskAllDynamicDetailBean;
import com.hjhq.teamface.project.bean.TaskDynamicInformationBean;

import java.io.IOException;
import java.util.List;

/**
 * 评论
 *
 * @author mou
 * @date 2017/4/5
 */

public class TaskAllDynamicAdapter extends BaseMultiItemQuickAdapter<TaskAllDynamicDetailBean, BaseViewHolder> {
    private boolean isShowMore = true;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TaskAllDynamicAdapter(List<TaskAllDynamicDetailBean> data) {
        super(data);
        addItemType(0, R.layout.custom_comment_item_move1);
        addItemType(1, R.layout.custom_comment_item_move2);
        addItemType(2, R.layout.custom_comment_item_move3);
        addItemType(3, R.layout.custom_comment_item_move4);
        addItemType(4, R.layout.project_task_dynamic_status_item);
        addItemType(5, R.layout.item_crm_pool_client_dynamic);
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        return isShowMore ? itemCount : Math.min(itemCount, 5);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskAllDynamicDetailBean item) {
       long time = item.getCreate_time();
        String date = "";
        if (time != 0) {
            date = DateTimeUtil.fromTime(time);
        }
        String name = item.getEmployee_name();
        List<TaskDynamicInformationBean> information = item.getInformation();
        switch (item.getItemType()) {
            //文字类型评论
            case 0:
                //名字
                helper.setText(R.id.item_move1_tv_name, name);
                //头像
                ImageLoader.loadCircleImage(mContext, item.getPicture(), helper.getView(R.id.item_move1_iv_icon2), name);
                helper.addOnLongClickListener(R.id.item_move1_iv_icon2);
                //时间
                helper.setText(R.id.item_move1_tv_time2, date);
                //内容
                try {
                    String content = item.getContent();
                    SpannableString spannable = new SpannableString(content);
                    RichEditText.matchMention(spannable);
                    EmojiUtil.handlerEmojiText2(helper.getView(R.id.item_move1_tv_pubDesc), spannable, mContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            // 图片类型评论
            case 1: {
                if (!information.isEmpty()){
                    TaskDynamicInformationBean uploadFileBean = information.get(0);
                    ImageLoader.loadImage(mContext, uploadFileBean.getFile_url() + "&width=1080", helper.getView(R.id.item_move2_customImage), R.drawable.jmui_pick_picture, R.drawable.jmui_send_error);
                    ImageLoader.loadCircleImage(mContext, item.getPicture(), helper.getView(R.id.item_move2_iv_icon2), name);
                }
                helper.setText(R.id.item_move2_tv_name, name);
                helper.setText(R.id.item_move2_tv_time2, date);
                helper.getView(R.id.item_move2_customImage).setVisibility(View.VISIBLE);
                helper.addOnClickListener(R.id.item_move2_customImage);
                break;
            }
            //语音类型评论
            case 2: {
                if (!information.isEmpty()){
                    TaskDynamicInformationBean uploadFileBean = information.get(0);
                    helper.setText(R.id.item_move3_tv_voive_time, Math.abs(uploadFileBean.getVoiceTime() / 1000) + "s");
                    ImageLoader.loadCircleImage(mContext, item.getPicture(), helper.getView(R.id.item_move3_iv_icon2), name);
                    helper.setText(R.id.item_move3_tv_name, name);
                    helper.setText(R.id.item_move3_tv_time2, date);
                }

                break;
            }
            //文件类型评论
            case 3: {
                if (!information.isEmpty()){
                    TaskDynamicInformationBean uploadFileBean = information.get(0);
                    helper.setText(R.id.item_move4_tv_file_name, uploadFileBean.getFile_name());

                    String fileType = uploadFileBean.getFile_type();
                    int fileTypeIcon = FileTypeUtils.getFileTypeIcon(fileType);
                    ImageLoader.loadImage(mContext, fileTypeIcon, helper.getView(R.id.item_move4_iv_file_type));
                    ImageLoader.loadCircleImage(mContext, item.getPicture(), helper.getView(R.id.item_move4_iv_icon2), name);
                    helper.setText(R.id.item_move4_tv_name, name);
                    helper.setText(R.id.item_move4_tv_time2, date);
                    helper.addOnClickListener(R.id.item_move4_iv_file_type);
                }

                break;
            }
            case 4:{
                ImageLoader.loadCircleImage(mContext, item.getPicture(), helper.getView(R.id.iv_head), item.getEmployee_name());
                helper.setText(R.id.tv_name, item.getEmployee_name());
                helper.setText(R.id.tv_status, "已查看");
                helper.setTextColor(R.id.tv_status, ColorUtils.resToColor(mContext, R.color.gray));
                long parseLong = item.getCreate_time();
                if (parseLong == 0L) {
                    helper.setVisible(R.id.tv_time, false);
                } else {
                    helper.setVisible(R.id.tv_time, true);
                    String time1 = DateTimeUtil.longToStr_YYYY_MM_DD_HH_MM(item.getCreate_time());
                    helper.setText(R.id.tv_time, time1);
                }
                break;
            }
            case 5:{
                long dytimeDate = item.getCreate_time();
                if (dytimeDate != 0) {
                    String dydate = DateTimeUtil.longToStr(dytimeDate, "yyyy年MM月dd日");
                    helper.setVisible(R.id.rl_date, true);
                    helper.setText(R.id.tv_date, dydate);
                } else {
                    helper.setVisible(R.id.rl_date, false);
                }

                //时间
                long dytime = item.getCreate_time();
                if (dytime != 0) {
                    String dydate = "";
                    if (dytimeDate != 0){
                        dydate = DateTimeUtil.longToStr(dytime, "HH:mm");
                    }else {
                        dydate = DateTimeUtil.longToStr(dytime, "yyyy-MM-dd HH:mm");
                    }
                    helper.setText(R.id.tv_time, dydate);
                }

                //人员
                helper.setText(R.id.tv_name, item.getEmployee_name());
                //内容
                helper.setText(R.id.tv_content, item.getContent());

                break;
            }


            default:
                break;
        }
    }


    public void setShowMore(boolean b) {
        isShowMore = b;
    }

    public boolean isShowMore() {
        return isShowMore;
    }
}
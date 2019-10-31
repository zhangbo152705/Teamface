package com.hjhq.teamface.im.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.database.Member;
import com.hjhq.teamface.basis.database.SocketMessage;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.listener.LinkMovementClickMethod;
import com.hjhq.teamface.basis.util.FileIconHelper;
import com.hjhq.teamface.basis.util.FileTypeUtils;
import com.hjhq.teamface.basis.util.FormatTimeUtil;
import com.hjhq.teamface.basis.util.ToastUtils;
import com.hjhq.teamface.basis.util.file.FormatFileSizeUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.emoji.EmojiUtil;
import com.hjhq.teamface.im.IM;
import com.hjhq.teamface.im.R;
import com.hjhq.teamface.im.db.DBManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageListAdapter extends BaseMultiItemQuickAdapter<SocketMessage, BaseViewHolder> {
    private Activity mActivity;
    private int memberNum;
    private String avatarUrl;
    long messageId;
    MediaPlayer mMediaPlayer;
    //请求头
    private Map<String, String> headers;
    AnimationDrawable animationDrawable;
    int drawable = -1;
    ImageView mImageView;
    SeekBar mSeekBar;
    int mProgress = 0;
    SocketMessage mItem;
    private boolean mFromUser = false;

    public MessageListAdapter(List<SocketMessage> data) {
        super(data);
        mMediaPlayer = new MediaPlayer();
        headers = new HashMap<>();
        headers.put("TOKEN", SPHelper.getToken());
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (animationDrawable != null) {
                    animationDrawable.stop();
                    messageId = -1L;
                    mProgress = 0;
                    if (mImageView != null && drawable != -1) {
                        mImageView.setImageResource(drawable);
                    }
                    if (mSeekBar != null) {
                        mSeekBar.setProgress(0);
                    }
                }
            }
        });


        //文本
        addItemType(MsgConstant.TYPE_TEXT_SEND, R.layout.jmui_chat_item_send_text);
        addItemType(MsgConstant.TYPE_TEXT_RECEIVE, R.layout.jmui_chat_item_receive_text);
        //图片
        addItemType(MsgConstant.TYPE_IMAGE_SEND, R.layout.jmui_chat_item_send_image);
        addItemType(MsgConstant.TYPE_IMAGE_RECEIVE, R.layout.jmui_chat_item_receive_image);
        //视频
        addItemType(MsgConstant.TYPE_VIDEO_SEND, R.layout.jmui_chat_item_send_video);
        addItemType(MsgConstant.TYPE_VIDEO_RECEIVE, R.layout.jmui_chat_item_receive_video);
        //语音
        addItemType(MsgConstant.TYPE_VOICE_SEND, R.layout.jmui_chat_item_send_voice);
        addItemType(MsgConstant.TYPE_VOICE_RECEIVE, R.layout.jmui_chat_item_receive_voice);
        //文件 TextView
        if (Build.VERSION.SDK_INT > 20) {
            addItemType(MsgConstant.TYPE_FILE_SEND, R.layout.jmui_chat_item_send_file3);
            addItemType(MsgConstant.TYPE_FILE_RECEIVE, R.layout.jmui_chat_item_receive_file3);
        } else {
            addItemType(MsgConstant.TYPE_FILE_SEND, R.layout.jmui_chat_item_send_file2);
            addItemType(MsgConstant.TYPE_FILE_RECEIVE, R.layout.jmui_chat_item_receive_file2);
        }

        //位置
        addItemType(MsgConstant.TYPE_LOCATION_SEND, R.layout.jmui_chat_item_send_location);
        addItemType(MsgConstant.TYPE_LOCATION_RECEIVE, R.layout.jmui_chat_item_receive_location);
        //自定义

        //未知
        addItemType(MsgConstant.TYPE_UNKNOWN, R.layout.jmui_chat_item_unknown);

        //提醒
        addItemType(MsgConstant.TYPE_NOTIFICATION_SEND, R.layout.jmui_chat_item_send_notification);
    }


    @Override
    protected void convert(BaseViewHolder helper, SocketMessage item) {
        ImageView view;
        if (helper.getAdapterPosition() == 0) {
            item.setShowTime(true);
        } else {
            if (item.getServerTimes() - getData().get(helper.getAdapterPosition() - 1).getServerTimes() > MsgConstant.SHOW_TIME_INTERVAL) {
                item.setShowTime(true);
            } else {
                item.setShowTime(false);
            }
        }
        showMessageTime(helper, item);
        
        switch (item.getItemType()) {
            //发送文本
            case MsgConstant.TYPE_TEXT_SEND:
                initAvatar(helper, R.id.jmui_sender_avatar_iv, item, false);
                helper.addOnClickListener(R.id.jmui_msg_content_send);
                helper.addOnLongClickListener(R.id.jmui_msg_content_send);
                TextView content = helper.getView(R.id.jmui_msg_content_send);
                content.setLinkTextColor(ColorStateList.valueOf(Color.parseColor("#3689E9")));
                helper.addOnClickListener(R.id.jmui_sender_avatar_iv);
                initSendState(helper, R.id.text_send_state, item);
                formatMessage(helper, R.id.jmui_msg_content_send, item);
                break;
            //接收文本
            case MsgConstant.TYPE_TEXT_RECEIVE:
                //initSenderName(helper, item);
                helper.addOnClickListener(R.id.jmui_msg_content_receive);
                helper.addOnLongClickListener(R.id.jmui_msg_content_receive);
                helper.addOnClickListener(R.id.jmui_recever_avatar_iv);
                helper.addOnLongClickListener(R.id.jmui_recever_avatar_iv);
                initAvatar(helper, R.id.jmui_recever_avatar_iv, item, true);
//                helper.setText(R.id.jmui_msg_content_receive, item.getMsgContent());
                formatMessage(helper, R.id.jmui_msg_content_receive, item);
                break;
            //发送图片
            case MsgConstant.TYPE_IMAGE_SEND:
                initSendState(helper, R.id.text_send_state, item);
                initAvatar(helper, R.id.jmui_sender_avatar_iv, item, false);
                showTime(helper, item);
                view = helper.getView(R.id.jmui_send_picture_iv);
                helper.addOnClickListener(R.id.jmui_send_picture_iv);
                helper.addOnClickListener(R.id.jmui_sender_avatar_iv);
                helper.addOnLongClickListener(R.id.jmui_send_picture_iv);
                ImageLoader.loadRoundImage(view.getContext(), item.getFileUrl() + "&width=120", view);
                helper.addOnClickListener(R.id.jmui_send_picture_iv);
                break;
            //接收图片
            case MsgConstant.TYPE_IMAGE_RECEIVE:
                //initSenderName(helper, item);
                initAvatar(helper, R.id.jmui_recever_avatar_iv, item, true);
                showTime(helper, item);
                view = helper.getView(R.id.jmui_receive_picture_iv);

                ImageLoader.loadRoundImage(view.getContext(), item.getFileUrl() + "&width=120", view);
                helper.addOnClickListener(R.id.jmui_receive_picture_iv);
                helper.addOnLongClickListener(R.id.jmui_receive_picture_iv);
                helper.addOnClickListener(R.id.jmui_recever_avatar_iv);
                helper.addOnLongClickListener(R.id.jmui_recever_avatar_iv);
                break;
            //发送视频
            case MsgConstant.TYPE_VIDEO_SEND:
                helper.addOnClickListener(R.id.jmui_send_picture_iv);
                helper.addOnLongClickListener(R.id.jmui_send_picture_iv);
                initSendState(helper, R.id.text_send_state, item);
                initAvatar(helper, R.id.jmui_sender_avatar_iv, item, false);
                showTime(helper, item);
                view = helper.getView(R.id.jmui_send_picture_iv);
                helper.addOnClickListener(R.id.jmui_send_picture_iv);
                helper.addOnClickListener(R.id.jmui_sender_avatar_iv);
                helper.addOnLongClickListener(R.id.jmui_send_picture_iv);
                // ImageLoader.loadVideo(view.getContext(), item.getFileUrl(), view, R.drawable.ic_image, R.drawable.jmui_send_error);
                helper.addOnClickListener(R.id.jmui_send_picture_iv);
                break;
            //接收视频
            case MsgConstant.TYPE_VIDEO_RECEIVE:
                //initSenderName(helper, item);
                initAvatar(helper, R.id.jmui_recever_avatar_iv, item, true);
                showTime(helper, item);
                view = helper.getView(R.id.jmui_send_video_iv);

                // ImageLoader.loadVideo(view.getContext(), item.getFileUrl(), view, R.drawable.ic_image, R.drawable.jmui_send_error);

                helper.addOnClickListener(R.id.jmui_recever_avatar_iv);
                helper.addOnLongClickListener(R.id.jmui_recever_avatar_iv);
                helper.addOnClickListener(R.id.jmui_receive_video_iv);
                helper.addOnLongClickListener(R.id.jmui_receive_video_iv);
                break;
            //发送位置
            case MsgConstant.TYPE_LOCATION_SEND:
                initAvatar(helper, R.id.jmui_sender_avatar_iv, item, false);
                showTime(helper, item);
                helper.setText(R.id.jmui_loc_desc, item.getLocation());
                helper.addOnClickListener(R.id.jmui_sender_avatar_iv);
                break;
            //接收位置
            case MsgConstant.TYPE_LOCATION_RECEIVE:
                //initSenderName(helper, item);
                initAvatar(helper, R.id.jmui_recever_avatar_iv, item, true);
                showTime(helper, item);
                helper.setText(R.id.jmui_loc_desc, item.getLocation());
                helper.addOnClickListener(R.id.jmui_recever_avatar_iv);
                break;
            //发送语音
            case MsgConstant.TYPE_VOICE_SEND:
                initSendState(helper, R.id.text_send_state, item);
                initAvatar(helper, R.id.jmui_sender_avatar_iv, item, false);
                showTime(helper, item);
                // helper.addOnClickListener(R.id.jmui_msg_send_voice);
                //helper.addOnClickListener(R.id.send_voice_iv);
                helper.addOnClickListener(R.id.send_voice_iv);
                helper.addOnClickListener(R.id.jmui_sender_avatar_iv);
                helper.setImageResource(R.id.send_voice_iv, R.drawable.jmui_send_3);
                helper.addOnLongClickListener(R.id.jmui_msg_send_voice);
                helper.setText(R.id.jmui_send_voice_length_tv, item.getDuration() + "\"");
               
                ImageView imageView = helper.getView(R.id.send_voice_iv);
                SeekBar seekBar = helper.getView(R.id.seekbar);
                helper.getView(R.id.send_voice_iv).setOnClickListener(v -> {
                    notifyItemChanged(helper.getAdapterPosition());
                    playOrStop(seekBar, imageView, item);
                });
                if (messageId == item.getMsgID()) {
                    mItem = item;
                    resume(seekBar, imageView, item);
                }
                helper.setIsRecyclable(false);
                break;
            //接收语音
            case MsgConstant.TYPE_VOICE_RECEIVE:
                //initSenderName(helper, item);
                initAvatar(helper, R.id.jmui_recever_avatar_iv, item, true);
                showTime(helper, item);
                helper.setText(R.id.jmui_receive_voice_length_tv, item.getDuration() + "\"");
                helper.addOnClickListener(R.id.receive_voice_iv);
                helper.setImageResource(R.id.receive_voice_iv, R.drawable.jmui_receive_3);
                helper.addOnClickListener(R.id.jmui_recever_avatar_iv);
                helper.addOnLongClickListener(R.id.jmui_recever_avatar_iv);
               
                ImageView imageView2 = helper.getView(R.id.receive_voice_iv);
                SeekBar seekBar2 = helper.getView(R.id.seekbar);
                helper.getView(R.id.receive_voice_iv).setOnClickListener(v -> {
                    playOrStop(seekBar2, imageView2, item);
                    notifyItemChanged(helper.getAdapterPosition());
                });
                if (messageId == item.getMsgID()) {
                    mItem = item;
                    resume(seekBar2, imageView2, item);
                }
                helper.setIsRecyclable(false);
                break;
            //发送文件
            case MsgConstant.TYPE_FILE_SEND:

                initSendState(helper, R.id.text_send_state, item);
                showTime(helper, item);
                helper.setText(R.id.tv_send_file_name, TextUtils.isEmpty(item.getFileName()) ? "未知文件名" : item.getFileName());
                helper.setText(R.id.tv_send_file_sender_name, TextUtils.isEmpty(item.getSenderName()) ? "未知" : item.getSenderName());
                helper.setText(R.id.tv_send_file_size, FormatFileSizeUtil.formatFileSize(item.getFileSize()));
                if (FileTypeUtils.isImage(item.getFileType()) || FileTypeUtils.isVideo(item.getFileType())) {
                    ImageLoader.loadRoundImage(mContext, item.getFileUrl() + "&width=64", helper.getView(R.id.iv_send_file_icon));

                } else {
                    ImageLoader.loadRoundImage(helper.getView(R.id.iv_send_file_icon).getContext(),
                            FileIconHelper.getFileIcon(item.getFileType()), helper.getView(R.id.iv_send_file_icon));
                }

                helper.addOnClickListener(R.id.rl_send_file);
                helper.addOnClickListener(R.id.jmui_sender_avatar_iv);
                helper.addOnLongClickListener(R.id.rl_send_file);
                initAvatar(helper, R.id.jmui_sender_avatar_iv, item, false);
                break;
            //接收文件
            case MsgConstant.TYPE_FILE_RECEIVE:
                //initSenderName(helper, item);
                initAvatar(helper, R.id.jmui_recever_avatar_iv, item, true);
                showTime(helper, item);
                helper.setText(R.id.tv_receive_file_name, TextUtils.isEmpty(item.getFileName()) ? "未知文件名" : item.getFileName());
                helper.setText(R.id.tv_receive_file_sender_name, TextUtils.isEmpty(item.getSenderName()) ? "未知" : item.getSenderName());
                helper.setText(R.id.tv_receive_file_size, FormatFileSizeUtil.formatFileSize(item.getFileSize()));
                if (FileTypeUtils.isImage(item.getFileType()) || FileTypeUtils.isVideo(item.getFileType())) {
                    ImageLoader.loadRoundImage(mContext, item.getFileUrl() + "&width=64", helper.getView(R.id.iv_receive_file_icon));

                } else {
                    ImageLoader.loadRoundImage(helper.getView(R.id.iv_receive_file_icon).getContext(),
                            FileIconHelper.getFileIcon(item.getFileType()),
                            helper.getView(R.id.iv_receive_file_icon));
                }
                helper.addOnClickListener(R.id.rl_receive_file);
                helper.addOnLongClickListener(R.id.rl_receive_file);
                helper.addOnClickListener(R.id.jmui_recever_avatar_iv);
                helper.addOnLongClickListener(R.id.jmui_recever_avatar_iv);

                break;
            //提醒类型
            case MsgConstant.TYPE_NOTIFICATION_SEND:
                helper.setText(R.id.jmui_loc_desc, item.getMsgContent());
                showTime(helper, item);
                break;
            //未知类型消息
            case MsgConstant.TYPE_UNKNOWN:
                break;
            default:
                break;
        }

    }

    private void showTime(BaseViewHolder helper, SocketMessage item) {
        helper.setText(R.id.jmui_send_time_txt, FormatTimeUtil.getNewChatTime(item.getServerTimes()));
    }

    
    private void showMessageTime(BaseViewHolder helper, SocketMessage item) {
        if (item.isShowTime()) {
            helper.setVisible(R.id.jmui_send_time_txt, true);
            showTime(helper, item);
        } else {
            helper.setVisible(R.id.jmui_send_time_txt, false);
        }
    }

    private void playOrStop(SeekBar seekBar, ImageView imageView, SocketMessage item) {
        if (imageView != null) {
            if (imageView.getId() == R.id.send_voice_iv) {
                imageView.setImageResource(R.drawable.jmui_voice_send);
                drawable = R.drawable.jmui_send_3;
            } else if (imageView.getId() == R.id.receive_voice_iv) {
                imageView.setImageResource(R.drawable.jmui_voice_receive);
                drawable = R.drawable.jmui_receive_3;
            }
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
        } else {
            return;
        }
        if (messageId == item.getMsgID()) {
            if (mMediaPlayer.isPlaying()) {
                stop();
                // pause();
            } else {
                play(seekBar, imageView, item);
            }
        } else {
            if (mImageView != null) {
                mImageView.setImageResource(drawable);
            }
            stop();
            if (mSeekBar != null) {
                mSeekBar.setVisibility(View.INVISIBLE);
            }
            play(seekBar, imageView, item);
        }


    }

    public void stop() {
        mProgress = 0;
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        messageId = -1L;
    }

    public void pause() {
        if (mSeekBar != null) {
            mProgress = mSeekBar.getProgress();
        }
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        if (animationDrawable != null) {
            animationDrawable.stop();
        }

        

    }

    
    private void resume(SeekBar seekBar, ImageView imageView, SocketMessage item) {
        if (imageView != null) {
            mImageView = imageView;
            mSeekBar = seekBar;
            //显示进度条
            //seekBar.setVisibility(View.VISIBLE);
            //mSeekBar.setVisibility(View.INVISIBLE);
            seekBar.setMax(mMediaPlayer.getDuration());

            if (imageView.getId() == R.id.send_voice_iv) {
                imageView.setImageResource(R.drawable.jmui_voice_send);
                drawable = R.drawable.jmui_send_3;
            } else if (imageView.getId() == R.id.receive_voice_iv) {
                imageView.setImageResource(R.drawable.jmui_voice_receive);
                drawable = R.drawable.jmui_receive_3;
            }
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            updateProgress(seekBar);
        } else {
            return;
        }
        if (animationDrawable != null) {
            animationDrawable.start();
        }
    }

    
    private void play(SeekBar seekBar, ImageView imageView, SocketMessage item) {
        messageId = item.getMsgID();
        mImageView = imageView;
        mSeekBar = seekBar;
        // seekBar.setVisibility(View.GONE);
        //显示播放进度
        // seekBar.setVisibility(View.VISIBLE);
        //mSeekBar.setVisibility(View.INVISIBLE);
        try {
            mMediaPlayer.reset();
            String fileUrl = item.getFileUrl();
            if (TextUtils.isEmpty(fileUrl)) {
                stop();
                ToastUtils.showError(imageView.getContext(), "播放失败");
                return;
            }
            if (!TextUtils.isEmpty(fileUrl) && !fileUrl.startsWith("http")) {
                fileUrl = SPHelper.getDomain() + fileUrl;
            }
            mMediaPlayer.setDataSource(imageView.getContext(), Uri.parse(fileUrl), headers);
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(mProgress);
            mMediaPlayer.start();
            seekBar.setMax(mMediaPlayer.getDuration());
            seekBar.postDelayed(new UpdateProgress(), 500);
            animationDrawable.start();
            updateProgress(seekBar);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
            ToastUtils.showError(imageView.getContext(), "播放失败");
        }

    }

    private void updateProgress(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mFromUser = true;
                    mProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(mProgress);
                mMediaPlayer.start();
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
                mFromUser = false;
                seekBar.postDelayed(new UpdateProgress(), 500);
            }
        });
    }

    
    private void formatMessage(BaseViewHolder helper, int resId, SocketMessage item) {
        TextView textview = helper.getView(resId);
        SpannableString spannable = new SpannableString(item.getMsgContent());
        try {
            //RichEditText.matchMention(spannable);
            EmojiUtil.handlerEmojiText2(helper.getView(resId), spannable, mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textview.setMovementMethod(new LinkMovementClickMethod());
        textview.setLinkTextColor(ColorStateList.valueOf(Color.parseColor("#3689E9")));
    }

    
    

    
    private void initSendState(BaseViewHolder helper, int id, SocketMessage item) {
        helper.setBackgroundRes(id, R.drawable.im_read_num_transparent_bg);
        helper.setTextColor(id, Color.parseColor("#BBBBC3"));

        helper.setText(id, "");
        //1发送成功,2发送失败,3对方已读,4已撤销,5撤回失败,6发送中,7全部已读

        if (item.getChatType() == MsgConstant.SINGLE) {
            if (item.getSendState() == MsgConstant.RECALLING) {
                helper.setText(id, "正在撤回...");
            } else if (item.getSendState() == MsgConstant.RECALL_FAILURE) {
                helper.setText(id, "撤回失败");
            } else if (item.getSendState() == MsgConstant.SENDING) {
                if (item.getMsgType() == MsgConstant.TEXT) {
                    if (System.currentTimeMillis() - IM.getInstance().getServerTimeOffsetValue() - item.getServerTimes() > MsgConstant.SEND_MSG_TIMEOUT) {
                        helper.setText(id, "发送超时");
                        item.setSendState(MsgConstant.SEND_FAILURE);
                        DBManager.getInstance().saveOrReplace(item);
                    } else {
                        helper.setText(id, "发送中");
                        helper.getView(id).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(helper.getAdapterPosition());
                            }
                        }, 5000);
                    }
                } else {
                    if (!TextUtils.isEmpty(item.getFileUrl()) && System.currentTimeMillis() - IM.getInstance().getServerTimeOffsetValue() - item.getServerTimes() > MsgConstant.SEND_MSG_TIMEOUT) {
                        helper.setText(id, "发送超时");
                        item.setSendState(MsgConstant.SEND_FAILURE);
                        DBManager.getInstance().saveOrReplace(item);
                    }
                    if (TextUtils.isEmpty(item.getFileUrl()) && System.currentTimeMillis() - IM.getInstance().getServerTimeOffsetValue() - item.getServerTimes() > 10 * 60 * 1000) {
                        helper.setText(id, "发送超时");
                        item.setSendState(MsgConstant.SEND_FAILURE);
                        DBManager.getInstance().saveOrReplace(item);
                    } else {
                        helper.setText(id, "发送中");
                        helper.getView(id).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(helper.getAdapterPosition());
                            }
                        }, 5000);
                    }
                }

            } else if (item.getSendState() == 1) {
                helper.setText(id, "未读");
                helper.setTextColor(id, Color.parseColor("#3689E9"));
            } else if (item.getSendState() == MsgConstant.SINGLE_READ) {
                helper.setText(id, "已读");
            } else if (item.getSendState() == 2) {
                helper.setText(id, "发送超时");

            }
        }
        if (item.getChatType() == MsgConstant.GROUP) {

            if (item.getSendState() == MsgConstant.RECALLING) {
                helper.setText(id, "正在撤回...");
            } else if (item.getSendState() == MsgConstant.RECALL_FAILURE) {
                helper.setText(id, "撤回失败");
            } else if (item.getReadnum() > 0) {
                helper.addOnClickListener(id);
                if (item.getReadnum() + 1 >= item.getGroupMemeberNum() || item.getSendState() == MsgConstant.ALL_READ) {
                    helper.setText(id, "全部已读");
                } else {
                    // helper.setBackgroundRes(id, R.drawable.im_read_num_bg);
                    helper.setText(id, item.getReadnum() + "人已读");
                    helper.setTextColor(id, Color.parseColor("#3689E9"));
                }

            } else {
                if (item.getSendState() == 1) {
                    helper.setText(id, "未读");
                    helper.setTextColor(id, Color.parseColor("#3689E9"));
                } else if (item.getSendState() == MsgConstant.SENDING) {
                    if (item.getMsgType() == MsgConstant.TEXT) {
                        if (System.currentTimeMillis() - IM.getInstance().getServerTimeOffsetValue() - item.getServerTimes() > 5000) {
                            helper.setText(id, "发送超时");
                            item.setSendState(MsgConstant.SEND_FAILURE);
                            DBManager.getInstance().saveOrReplace(item);
                        } else {
                            helper.setText(id, "发送中");
                            helper.getView(id).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(helper.getAdapterPosition());
                                }
                            }, 5000);
                        }
                    } else {
                        if (!TextUtils.isEmpty(item.getFileUrl()) && System.currentTimeMillis() - IM.getInstance().getServerTimeOffsetValue() - item.getServerTimes() > 5000) {
                            helper.setText(id, "发送超时");
                            item.setSendState(MsgConstant.SEND_FAILURE);
                            DBManager.getInstance().saveOrReplace(item);
                        } else {
                            helper.setText(id, "发送中");
                            helper.getView(id).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemChanged(helper.getAdapterPosition());
                                }
                            }, 5000);
                        }
                    }
                } else if (item.getSendState() == 3) {
                    helper.setText(id, "已读");
                } else if (item.getSendState() == 2) {
                    helper.setText(id, "发送超时");

                }
            }


        }

    }

    
    private void initAvatar(BaseViewHolder helper, int resId, SocketMessage item, boolean flag) {

        List<Member> members = DBManager.getInstance().getMemberBySignId(item.getOneselfIMID());
        String name = "";
        if (members.size() > 0) {
            ImageLoader.loadCircleImage(helper.getView(resId).getContext(), members.get(0).getPicture(),
                    helper.getView(resId), members.get(0).getEmployee_name());
            name = members.get(0).getEmployee_name();

        } else {
            name = item.getSenderName();
            ImageLoader.loadCircleImage(helper.getView(resId).getContext(), item.getSenderAvatar(),
                    helper.getView(resId), item.getSenderName());
        }
        if (flag) {
            if (item.getChatType() == MsgConstant.GROUP) {
                helper.setVisible(R.id.jmui_display_name_tv, true);
                helper.setText(R.id.jmui_display_name_tv, name);
            } else {
                helper.setVisible(R.id.jmui_display_name_tv, false);
            }
        }


    }

    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }

    
    public void release() {
        mSeekBar = null;
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                messageId = -1L;
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    
    private class UpdateProgress implements Runnable {

        @Override
        public void run() {
            if (mSeekBar != null && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                if (!mFromUser) {
                    mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                }
                if (mMediaPlayer.getDuration() - mMediaPlayer.getCurrentPosition() < 500) {
                    mSeekBar.setProgress(0);
                } else if (mMediaPlayer.isPlaying()) {
                    mSeekBar.postDelayed(new UpdateProgress(), 500);
                }
            }
        }
    }
}
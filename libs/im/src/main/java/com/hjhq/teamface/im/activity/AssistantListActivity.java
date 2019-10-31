package com.hjhq.teamface.im.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjhq.teamface.basis.BaseActivity;
import com.hjhq.teamface.basis.BaseTitleActivity;
import com.hjhq.teamface.basis.bean.BaseBean;
import com.hjhq.teamface.basis.bean.MessageBean;
import com.hjhq.teamface.basis.constants.Constants;
import com.hjhq.teamface.basis.constants.MsgConstant;
import com.hjhq.teamface.basis.database.Conversation;
import com.hjhq.teamface.basis.network.subscribers.ProgressSubscriber;
import com.hjhq.teamface.basis.util.EventBusUtils;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.basis.util.file.SPHelper;
import com.hjhq.teamface.basis.view.recycler.SimpleItemClickListener;
import com.hjhq.teamface.common.utils.CommonUtil;
import com.hjhq.teamface.common.view.EmptyView;
import com.hjhq.teamface.im.ImLogic;
import com.hjhq.teamface.im.R;
import com.hjhq.teamface.im.adapter.ConversationListAdapter;
import com.hjhq.teamface.im.adapter.ConversationListControllerV2;
import com.hjhq.teamface.im.bean.ConversationListBean;
import com.hjhq.teamface.im.db.DBManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AssistantListActivity extends BaseTitleActivity {
    private RecyclerView rvFileList;
    private ArrayList<Conversation> dataList = ConversationListControllerV2.assistantList;
    private ConversationListAdapter mAdapter;
    private long conversationId = -1L;
    private EmptyView mEmptyView;
    private SmartRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getChildView() {
        return R.layout.assistant_list_activity;
    }

    @Override
    protected void initView() {
        super.initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            conversationId = bundle.getLong(MsgConstant.CONVERSATION_ID, -1L);
        }
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setEnableLoadMore(false);
        rvFileList = (RecyclerView) findViewById(R.id.rv_file_list);
        rvFileList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationListAdapter(this, dataList);
        rvFileList.setAdapter(mAdapter);
        mEmptyView = new EmptyView(this);
        mEmptyView.setEmptyImage(R.drawable.empty_view_img);
        mAdapter.setEmptyView(mEmptyView);
        setActivityTitle("消息通知");
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe
    public void onEvent(MessageBean bean) {
        if (bean == null || TextUtils.isEmpty(bean.getTag())) {
            return;
        }
        switch (bean.getTag()) {
            case MsgConstant.UPDATE_ASSISTANT_LIST_INFO:
                mAdapter.notifyDataSetChanged();
                break;
            case MsgConstant.REFRESH_CONVERSATION_LIST_COMPLETE_TAG:
                mRefreshLayout.finishRefresh();
                break;
            /*case MsgConstant.MARK_ONE_ASSISTANT_MSG_READ:
                //收到标记一条一条已读推送(已验证本地未读)
                readAssistantMessage(((PushMessage) bean.getObject()));
                break;
            case MsgConstant.UPDATE_ASSISTANT_PUT_TOP_TAG:
                //更新置顶状态
                Long id = TextUtil.parseLong(((String) bean.getObject())) + MsgConstant.DEFAULT_VALUE;
                updatePinTopState(id, bean.getCode());
                break;
            case MsgConstant.UPDATE_ASSISTANT_NO_BOTHER_TAG:
                //更新免打扰状态
                Long id2 = TextUtil.parseLong(((String) bean.getObject())) + MsgConstant.DEFAULT_VALUE;
                updateNoBotherState(id2, bean.getCode());
                break;*/
        }
    }

   /* @Subscribe
    public void onEvent2(ImMessage bean) {
        if (bean == null || TextUtils.isEmpty(bean.getTag())) {
            return;
        }
        switch (bean.getTag()) {
            case MsgConstant.TYPE_MARK_ALL_ITEM_READ:
                markAllRead((String) bean.getObject());
                break;
           *//* case MsgConstant.RECEIVE_PUSH_MESSAGE:
            case MsgConstant.SHOW_PUSH_MESSAGE:
                //收到推送
                putConvOnTop(((PushMessage) bean.getObject()));
                break;*//*
        }

    }*/

    /**
     * 标记未读数为0
     *
     * @param assistantId
     */
    /*public void markAllRead(String assistantId) {
        //小助手类型1自定义模块助手,2企信助手3审批助手4文件库助手5备忘录助手6邮件小助手7任务小助手
        //getTotalMsgNum为小助手类型
        DBManager.getInstance().markAllPushMessageRead(assistantId);
        EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.MARK_ALL_READ, conversationId));
        for (int i = 0; i < dataList.size(); i++) {
            if (conversationId == dataList.get(i).getConversationId()) {
                dataList.get(i).setUnreadMsgCount(0);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }*/

    /**
     * 小助手数据未读数-1
     *
     * @param object
     */
   /* public void readAssistantMessage(PushMessage object) {
        String assistantId = object.getAssistant_id();
        boolean flag = true;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getConversationId() == TextUtil.parseLong(assistantId) + MsgConstant.DEFAULT_VALUE) {
                dataList.get(i).setUnreadMsgCount(dataList.get(i).getUnreadMsgCount() > 0 ? dataList.get(i).getUnreadMsgCount() - 1 : 0);
                mAdapter.notifyDataSetChanged();
                flag = false;
            }
        }
        if (flag) {
            //本地列表无此小助手
            getData();
        }
    }*/

    /**
     * 收到新的推送,更新小助手在列表中的位置
     *
     * @param message
     */
    /*private void putConvOnTop(PushMessage message) {
        long assistanId = 0;
        try {
            assistanId = TextUtil.parseLong(message.getAssistant_id()) + MsgConstant.DEFAULT_VALUE;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            getData();
            return;
        }
        Conversation c = null;
        Iterator<Conversation> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            Conversation c1 = iterator.next();
            if (c1.getConversationId() == assistanId) {
                c1.setLastMsgDate(TextUtil.parseLong(message.getCreate_time()));
                StringBuilder sb = new StringBuilder();
                if (!TextUtils.isEmpty(message.getAssistant_name())) {
                    sb.append(message.getAssistant_name() + ":");
                }
                if (!TextUtils.isEmpty(message.getPush_content())) {
                    sb.append(message.getPush_content());
                }
                if (!TextUtils.isEmpty(message.getFieldInfo())) {
                    sb.append(message.getFieldInfo());
                }
                String lastmessage = sb.toString();
                if (!TextUtils.isEmpty(lastmessage)) {
                    lastmessage = lastmessage.replace("\n", "").replace("\r", "").replace("\r\n", "");
                }
                c1.setLatestMessage(lastmessage);
                c1.setUpdateTime(System.currentTimeMillis());
                c1.setUnreadMsgCount(c1.getUnreadMsgCount() + 1);
                c = c1;
                if (c.getTopStatus() == 1) {
                    return;
                }
                iterator.remove();
            }
        }
        if (c == null) {
            getData();
            return;
        }
        int topIndex = 0;
        if (c.getTopStatus() == 1) {
            dataList.add(topIndex, c);
        } else {
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getTopStatus() == 1) {
                    topIndex = i + 1;
                }
            }
            dataList.add(topIndex, c);

        }
        sortData();
    }*/

    /**
     * 获取会话列表
     */
    protected void getData() {
        EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.REQUEST_REFRESH_CONVERSATION_LIST_TAG, null));
    }

    /**
     * 排序
     */
    private void sortData() {
        //非置顶按时间排序
        List<Conversation> topC = new ArrayList<Conversation>();
        List<Conversation> notTopC = new ArrayList<Conversation>();

        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getTopStatus() == 1) {
                topC.add(dataList.get(i));
            } else {
                notTopC.add(dataList.get(i));
            }
        }

        Collections.sort(topC, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                return o1.getUpdateTime() < o2.getUpdateTime() ? 1 : -1;
            }
        });
        Collections.sort(notTopC, new Comparator<Conversation>() {
            @Override
            public int compare(Conversation o1, Conversation o2) {
                return o1.getLastMsgDate() < o2.getLastMsgDate() ? 1 : -1;
            }
        });
        dataList.clear();
        dataList.addAll(topC);
        dataList.addAll(notTopC);
        mAdapter.notifyDataSetChanged();
        // EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.UPDATE_ASSISTANT_INFO, dataList));
    }

    /**
     * 更新免打扰状态
     *
     * @param conversationId
     * @param state
     */
    /*public void updateNoBotherState(long conversationId, int state) {
        if (dataList == null) {
            getData();
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getConversationId() == conversationId) {
                dataList.get(i).setNoBother(state);
                mAdapter.notifyDataSetChanged();
                // EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.UPDATE_ASSISTANT_INFO, dataList));
                return;
            }
        }
    }*/

   /* public void updatePinTopState(long conversationId, int state) {
        if (dataList == null) {
            getData();
            return;
        }
        Conversation c = null;
        final Iterator<Conversation> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            final Conversation next = iterator.next();
            if (next.getConversationId() == conversationId) {
                next.setTopStatus(state);
                c = next;
            }
        }
        if (c != null) {
            sortData();
        }
        mAdapter.notifyDataSetChanged();
        // EventBusUtils.sendEvent(new MessageBean(0, MsgConstant.UPDATE_ASSISTANT_INFO, dataList));
    }*/

    /**
     * 小助手数据
     *
     * @param bean
     * @param i
     * @param conversation
     */
    private void setAssistantData(ConversationListBean bean, int i, Conversation conversation) {
        conversation.setConversationType(bean.getData().get(i).getChat_type());
        try {
            conversation.setConversationId(Long.parseLong(bean.getData().get(i).getId()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            conversation.setApplicationId(TextUtil.parseLong(bean.getData().get(i).getApplicationId()));
        } catch (NumberFormatException e) {

        }
        conversation.setOneselfIMID(SPHelper.getUserId());
        conversation.setTitle(bean.getData().get(i).getName());
        conversation.setCompanyId(SPHelper.getCompanyId());
        conversation.setMyId(SPHelper.getUserId());
        //头像
        conversation.setAvatarUrl("");
        conversation.setIcon_type(bean.getData().get(i).getIcon_type());
        conversation.setIcon_color(bean.getData().get(i).getIcon_color());
        conversation.setIcon_url(bean.getData().get(i).getIcon_url());
        //conversation.setLastMsgDate(bean.getData().get(i).getUpdate_time());

        long lastPushTime = TextUtil.parseLong(bean.getData().get(i).getLatest_push_time());
        if (lastPushTime <= 0) {
            lastPushTime = TextUtil.parseLong(bean.getData().get(i).getCreate_time());
        }
        conversation.setLastMsgDate(lastPushTime);
        conversation.setLastMessageType(MsgConstant.NOTIFICATION);
        conversation.setLatestMessage(bean.getData().get(i).getLatest_push_content());
        conversation.setTopStatus(TextUtil.parseInt(bean.getData().get(i).getTop_status(), 0));
        conversation.setNoBother(TextUtil.parseInt(bean.getData().get(i).getNo_bother(), 0));
        conversation.setUnreadMsgCount(TextUtil.parseInt(bean.getData().get(i).getUnread_nums(), 0));
        conversation.setIsHide(TextUtil.parseInt(bean.getData().get(i).getIs_hide(), 0));
        conversation.setUnreadMsgCount(TextUtil.parseInt(bean.getData().get(i).getUnread_nums()));
        //小助手类型1自定义模块助手,2企信助手3审批助手4文件库助手
        //此字段用于标记助手类型
        conversation.setTotalMsgNum(TextUtil.parseInt(bean.getData().get(i).getType()));
        //是否只查看未读,0否,1是
        conversation.setPeoples(bean.getData().get(i).getShow_type());
        conversation.setConversationId(conversation.getConversationId() + MsgConstant.DEFAULT_VALUE);
        try {
            DBManager.getInstance().saveOrReplace(conversation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataList.add(conversation);
        return;
    }

    @Override
    protected void setListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });
        rvFileList.addOnItemTouchListener(new SimpleItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Conversation conversation = dataList.get(position);
                if (conversation.getConversationType() == MsgConstant.SELF_DEFINED) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString(MsgConstant.CONV_TITLE, conversation.getTitle() + "");
                        bundle.putString(MsgConstant.CONVERSATION_ID, (conversation.getConversationId() - MsgConstant.DEFAULT_VALUE) + "");
                        bundle.putString(MsgConstant.APPLICATION_ID, conversation.getApplicationId() + "");
                        bundle.putString(MsgConstant.BEAN_NAME, "");
                        //小助手类型
                        bundle.putString(Constants.DATA_TAG1, conversation.getTotalMsgNum() + "");
                        bundle.putLong(Constants.DATA_TAG2, conversation.getLastMsgDate());
                        //是否查看已读,借用peoples字段使用
                        bundle.putString(MsgConstant.VIEW_READED, conversation.getPeoples());
                        bundle.putString(Constants.DATA_TAG3, conversation.getIcon_type());
                        bundle.putString(Constants.DATA_TAG4, conversation.getIcon_color());
                        bundle.putString(Constants.DATA_TAG5, conversation.getIcon_url());
                        CommonUtil.startActivtiyForResult(mContext, AppAssistantActivity.class, Constants.REQUEST_CODE1, bundle);
                        markAllRead(conversation);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                // mFileList.get(position).delete();
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void initData() {

    }

    /**
     * 如果是企信小助手,将信息标记为全部已读
     *
     * @param conversation
     */

    public void markAllRead(Conversation conversation) {
        //小助手类型1自定义模块助手,2企信助手3审批助手4文件库助手5备忘录助手6邮件小助手
        //getTotalMsgNum为小助手类型
        if (conversation.getTotalMsgNum() == MsgConstant.IM_ASSISTANT) {
            //全部标记为已读
            ImLogic.getInstance().markAllRead(((BaseActivity) mContext),
                    (conversation.getConversationId() - MsgConstant.DEFAULT_VALUE) + "",
                    new ProgressSubscriber<BaseBean>(((BaseActivity) mContext), false) {
                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            EventBusUtils.sendEvent(new MessageBean(1, MsgConstant.MARK_ALL_READ, conversation.getConversationId()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            e.printStackTrace();
                        }
                    });

        }
    }

    @Override
    public void onClick(View view) {
    }


}

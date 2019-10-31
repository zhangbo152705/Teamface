package com.hjhq.teamface.memo.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.bean.KnowledgeBean;

import java.util.Calendar;
import java.util.List;


public class KnowledgeAnswerListAdapter extends BaseQuickAdapter<KnowledgeBean, BaseViewHolder> {


    public KnowledgeAnswerListAdapter(List<KnowledgeBean> data) {
        super(R.layout.memo_knowledge_answer_list_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, KnowledgeBean item) {
        helper.setText(R.id.tv_title, TextUtil.processHtmlText(item.getContent(), ""));
        helper.setText(R.id.tv_name, item.getCreate_by().getEmployee_name());
        final long createTime = TextUtil.parseLong(item.getCreate_time());
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(createTime);
        String format = "";
        if (calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)) {
            format = "MM-dd HH:mm";
        } else {
            format = "yyyy-MM-dd HH:mm";
        }
        helper.setText(R.id.tv_time, DateTimeUtil.longToStr(createTime, format));
        //置顶
        if ("1".equals(item.getTop())) {
            helper.setVisible(R.id.iv_top, true);
        } else {
            helper.setVisible(R.id.iv_top, false);
        }
        ImageView imageView1 = helper.getView(R.id.iv1);
        ImageView imageView2 = helper.getView(R.id.iv2);
        ImageView imageView3 = helper.getView(R.id.iv3);
        ImageView avatar = helper.getView(R.id.avatar);
        //图片的内容先不做处理，因为图片的或文本里面，拿不到
        /*if (!TextUtils.isEmpty(item.getPic_url())) {
            imageView1.setVisibility(View.VISIBLE);
            ImageLoader.loadRoundImage(helper.getConvertView().getContext(),
                    item.getPic_url(), imageView1, 5, R.drawable.ic_image);
        } else {
            imageView1.setVisibility(View.GONE);
        }*/
        ImageLoader.loadCircleImage(avatar.getContext(), item.getCreate_by().getPicture(), avatar, item.getCreate_by().getEmployee_name());


    }


}
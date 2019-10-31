package com.hjhq.teamface.memo.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hjhq.teamface.basis.bean.ProjectLabelBean;
import com.hjhq.teamface.basis.image.ImageLoader;
import com.hjhq.teamface.basis.util.ColorUtils;
import com.hjhq.teamface.basis.util.DateTimeUtil;
import com.hjhq.teamface.basis.util.TextUtil;
import com.hjhq.teamface.memo.R;
import com.hjhq.teamface.memo.bean.KnowledgeBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class KnowledgeListAdapter extends BaseQuickAdapter<KnowledgeBean, BaseViewHolder> {


    public KnowledgeListAdapter(List<KnowledgeBean> data) {
        super(R.layout.memo_knowledge_list_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, KnowledgeBean item) {
        if (item.getTop() != null && item.getTop().equals("1")){
            helper.getView(R.id.top).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.top).setVisibility(View.GONE);
        }
        LinearLayout llTag = helper.getView(R.id.ll_tags);
        llTag.removeAllViews();
        final ArrayList<ProjectLabelBean> label_ids = item.getLabel_ids();
        for (int i = 0; i < label_ids.size(); i++) {
            View view = View.inflate(helper.getConvertView().getContext(), R.layout.memo_knowledge_tag_item, null);
            TextView tx = view.findViewById(R.id.tv);
            TextUtil.setText(tx, label_ids.get(i).getName());
            llTag.addView(view);
        }

        helper.setText(R.id.tv_catg, "" + item.getClassification_name());
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_content, TextUtil.processHtmlText(item.getContent(), ""));
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
        ImageView imageView1 = helper.getView(R.id.iv1);
        ImageView imageView2 = helper.getView(R.id.iv2);
        ImageView imageView3 = helper.getView(R.id.iv3);
        ImageView avatar = helper.getView(R.id.avatar);
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
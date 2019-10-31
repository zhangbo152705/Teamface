package com.hjhq.teamface.project.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjhq.teamface.project.R;

import java.util.List;

/**
 * Created by Administrator on 2019/5/13.
 */

public class ProjectCommonPopupWindowAdapter extends RecyclerView.Adapter<ProjectCommonPopupWindowAdapter.ViewHolder> {

    private List<String> mIconList;
    private ItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;

        public ViewHolder(View view){
            super(view);
            itemName = (TextView) view.findViewById(R.id.item);

        }
    }

    public ProjectCommonPopupWindowAdapter(List<String> iconList,ItemClickListener listener){
        this.mIconList = iconList;
        this.listener = listener;
    }

    public void setData(List<String> iconList){
        this.mIconList = iconList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_commom_popupwindow_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(mIconList.get(position));
        holder.itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIconList.size();
    }

    public interface ItemClickListener {
         void onItemClick(View view,int postion);
    }
}
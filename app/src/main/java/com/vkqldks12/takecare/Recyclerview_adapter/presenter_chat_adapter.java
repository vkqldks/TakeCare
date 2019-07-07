package com.vkqldks12.takecare.Recyclerview_adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vkqldks12.takecare.Recyclerview_model.presenter_chat_model;

import java.util.List;

/**
 * Created by vkqld on 2019-02-14.
 */

public class presenter_chat_adapter extends RecyclerView.Adapter<presenter_chat_adapter.MyViewHolder>{

    private List<presenter_chat_model> presenter_chat_modelList;
    private Context mContext;

    public presenter_chat_adapter(Context applicationContext, List<presenter_chat_model> presenterChatModelList) {
        this.mContext = applicationContext;
        this.presenter_chat_modelList = presenterChatModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.vkqldks12.takecare.R.layout.presenter_chat_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView2.setText(presenter_chat_modelList.get(position).getNick_name());
        holder.textView.setText(presenter_chat_modelList.get(position).getMessege());

    }

    @Override
    public int getItemCount() {
        return presenter_chat_modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView, textView2;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(com.vkqldks12.takecare.R.id.presenter_messege_row);
            textView2 = (TextView)itemView.findViewById(com.vkqldks12.takecare.R.id.presenter_messege_nick);
        }
    }
}

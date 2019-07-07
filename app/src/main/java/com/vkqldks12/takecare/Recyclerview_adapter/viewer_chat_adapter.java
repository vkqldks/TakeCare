package com.vkqldks12.takecare.Recyclerview_adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vkqldks12.takecare.Recyclerview_model.viewer_chat_model;

import java.util.List;

/**
 * Created by vkqld on 2019-02-14.
 */

public class viewer_chat_adapter extends RecyclerView.Adapter<viewer_chat_adapter.MyViewHolder>{

    private List<viewer_chat_model> ViewerChatList;
    private Context context;

    public viewer_chat_adapter(Context mContext, List<viewer_chat_model> viewerChatModels){
        this.context = mContext;
        this.ViewerChatList = viewerChatModels;
    }

    @NonNull
    @Override
    public viewer_chat_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.vkqldks12.takecare.R.layout.viewer_chat_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewer_chat_adapter.MyViewHolder holder, int position) {

        holder.textView2.setText(ViewerChatList.get(position).getNick());
        holder.textView.setText(ViewerChatList.get(position).getMesseage());

    }

    @Override
    public int getItemCount() {
        return ViewerChatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView, textView2;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(com.vkqldks12.takecare.R.id.viewer_messege_row);
            textView2 = (TextView)itemView.findViewById(com.vkqldks12.takecare.R.id.viewer_messege_nick);
        }
    }
}

package com.vkqldks12.takecare.Recyclerview_adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vkqldks12.takecare.Recyclerview_model.vod_messageinfo;

import java.util.List;

/**
 * Created by vkqld on 2019-02-19.
 */

public class vod_message_adapter extends RecyclerView.Adapter<vod_message_adapter.MyViewHolder> {

    private List<vod_messageinfo> vodMessageinfos;
    private Context mContext;

    public vod_message_adapter(Context context, List<vod_messageinfo> vod_messageinfos){
        this.mContext = context;
        this.vodMessageinfos = vod_messageinfos;
    }

    @NonNull
    @Override
    public vod_message_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.vkqldks12.takecare.R.layout.vod_message_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull vod_message_adapter.MyViewHolder holder, int position) {

        holder.textView.setText(vodMessageinfos.get(position).getMessage());
        holder.textView2.setText(vodMessageinfos.get(position).getNick());
    }

    @Override
    public int getItemCount() {
        return vodMessageinfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView, textView2;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(com.vkqldks12.takecare.R.id.vod_chat_messege_row);
            textView2 = (TextView)itemView.findViewById(com.vkqldks12.takecare.R.id.vod_chat_nick_row);
        }
    }
}

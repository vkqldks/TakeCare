package com.vkqldks12.takecare.Recyclerview_adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vkqldks12.takecare.Recyclerview_model.vod_listinfo;
import com.vkqldks12.takecare.VOD.vod_play;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by vkqld on 2019-02-07.
 */

public class vod_list_adapter extends RecyclerView.Adapter<vod_list_adapter.MyViewHolder>{

    private Activity activity;
    private List<vod_listinfo> vodListInfo;
    private Context mContext;

    public vod_list_adapter(Activity activity, Context context ,List<vod_listinfo> vodListInfo) {
        this.activity = activity;
        this.vodListInfo = vodListInfo;
        this.mContext = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.vkqldks12.takecare.R.layout.vod_list_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("userData",Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("userName","");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long name = Long.parseLong(vodListInfo.get(position).getVod_name());
        String transTime = format.format(new Date(name));


        String vod_image = "http://13.209.201.195/vod/"+vodListInfo.get(position).getVod_name()+".png";

        holder.login_id.setText(login);
        holder.vod_Name.setText(transTime);
        Glide.with(mContext)
                .load(vod_image)
                .into(holder.vod_Thumnail);

    }

    @Override
    public int getItemCount() {
        return vodListInfo.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView vod_Thumnail;
        TextView vod_Name, login_id;
        String vodID;

        public MyViewHolder(View itemview) {
            super(itemview);

            vod_Thumnail = (ImageView)itemview.findViewById(com.vkqldks12.takecare.R.id.vod_thumnail);
            vod_Name = (TextView)itemview.findViewById(com.vkqldks12.takecare.R.id.vod_name);
            login_id = (TextView)itemview.findViewById(com.vkqldks12.takecare.R.id.login_id);

            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos !=RecyclerView.NO_POSITION){
                        vod_listinfo clickeditem = vodListInfo.get(pos);

                        Intent intent = new Intent(mContext, vod_play.class);
                        intent.putExtra("position", pos);
                        intent.putExtra("vod_name", vodListInfo.get(pos).getVod_name());
                        intent.putExtra("vod_ID", vodListInfo.get(pos).getVod_id());
                        mContext.startActivity(intent);
                        Toast.makeText(view.getContext(), clickeditem.getVod_name()+"을 누르셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


}

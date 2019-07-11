package com.vkqldks12.takecare.VOD;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.vkqldks12.takecare.R;
import com.vkqldks12.takecare.Recyclerview_adapter.vod_message_adapter;
import com.vkqldks12.takecare.Recyclerview_model.vod_messageinfo;
import com.vkqldks12.takecare.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

public class vod_play extends AppCompatActivity {

    //player
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    //Log
    final private String TAG = "VOD_PlayerActivity";

    //Hls
    final private String video_url = "http://**.***.***.***/vod/";

    int position; //vod 클릭 시 vod_list_adapter를 통해 클릭한 아이템의 포지션 값
    String vodName; //vod 클릭 시 vod_list_adapter를 통해 클릭한 아이템의 이름
    String Real_Vod_Name; //vod 이름 뒤 확장자를 붙인 스트링값(xxxx.m3u8)
    String vodID; //list_adapter에서 넘어온 vod_id값

    RecyclerView recyclerView; //채팅 메세지 출력용 리사이클러뷰
    vod_message_adapter vodMessageAdapter;
    List<vod_messageinfo> vodMessageinfoList = new ArrayList<>(); // vod 전체 메세지 내역을 담고 있는 리스트
    List<vod_messageinfo> chatMessageList = new ArrayList<>(); // 현재 채팅 내역을 담고 있는 리스트
    SharedPreferences sharedPreferences;

    String userName,msg,nickName;
    int MSGTime;

    Thread srtThread;
    boolean srtIsRun = true;

    boolean shouldAutoplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_play);

        shouldAutoplay = true;

        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        vodName = intent.getStringExtra("vod_name");
        vodID = intent.getStringExtra("vod_ID");
        Real_Vod_Name = vodName+".m3u8";


        //ExoPlayer implementation
        //Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();
        //Bis. Create a RenderFactory
        RenderersFactory renderersFactory = new DefaultRenderersFactory(this);

        //Create the Player
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.vod_play_view);

        //Set media controller
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        player.setPlayWhenReady(shouldAutoplay);

        //Bind the Player to the view
        simpleExoPlayerView.setPlayer(player);

        //Set the media source , 인텐트로 클릭한 vod의 제목을 받아온 후 기존 url뒤에 연결시켜줘야한다.
        Uri mp4VideoUri = Uri.parse(video_url+Real_Vod_Name);

        //Measures bandwidth during playback. Can be null if not required
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();

        //Produces DataSource instances through which media data is Loaded
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ThinkingApp"), bandwidthMeterA);

        //Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //for live stream link
        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);

        player.prepare(videoSource);

        sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String userid = sharedPreferences.getString("userName","");

        initViews(userid);
    }

    public void initViews(String name){

        recyclerView = (RecyclerView)findViewById(R.id.vod_chat_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        vodMessageAdapter = new vod_message_adapter(this, chatMessageList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(vodMessageAdapter);
        vodMessageAdapter.notifyDataSetChanged();

        loadJson(name);

    }

    public void loadJson(String mName){

        retrofit2.Call<ResponseBody> call = RetrofitClient
                .getmInstance()
                .getApi()
                .vod_chat_list(mName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s = response.body().string();

                    JSONArray jsonArray = new JSONArray(s);

                    if (s !=null){ //받아오기 성공시
                        Toast.makeText(vod_play.this, "받아오기 성공", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            userName = jsonObject.getString("username");
                            msg = jsonObject.getString("chatMessage");
                            MSGTime = jsonObject.getInt("message_time");
                            nickName = jsonObject.getString("nick_name");

                            vodMessageinfoList.add(new vod_messageinfo(
                                    msg,
                                    userName,
                                    MSGTime,
                                    nickName
                            ));
                        }
                    }
                    else { //채팅 메세지 받아오기 실패시
                        Toast.makeText(vod_play.this, "받아오기 실패", Toast.LENGTH_SHORT).show();
                    }
                }catch (IOException e){
                    Toast.makeText(vod_play.this, "받아오기 실패  IOException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }catch (JSONException e){
                    Toast.makeText(vod_play.this, "받아오기 실패 IOException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                searchChatList();
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(vod_play.this, "받아오기 실패 onFaulure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void searchChatList(){
        srtThread = new Thread(()->{
            while (srtIsRun){
                try {
                    Thread.sleep(100);
                    long times = player.getCurrentPosition();
                    times = (long)Math.round(times/100)*100;

                    for (vod_messageinfo List : vodMessageinfoList){
                        if (List.getChat_time() == times){
                            chatMessageList.add(List);
                        }
                    }
                    runOnUiThread(()->{
                        vodMessageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(vodMessageAdapter.getItemCount()-1);
                    });
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        srtThread.start();
    }

    //android life cycle
    @Override
    protected void onStop() {
        player.release();
        super.onStop();
        Log.v(TAG, "onStop...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
    }

}

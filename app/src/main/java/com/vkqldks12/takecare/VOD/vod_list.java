package com.vkqldks12.takecare.VOD;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.vkqldks12.takecare.R;
import com.vkqldks12.takecare.Recyclerview_adapter.vod_list_adapter;
import com.vkqldks12.takecare.Recyclerview_model.vod_listinfo;
import com.vkqldks12.takecare.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class vod_list extends AppCompatActivity {

    RecyclerView recyclerView;
    com.vkqldks12.takecare.Recyclerview_adapter.vod_list_adapter vod_list_adapter;
    List<vod_listinfo> vod_ListInfo= new ArrayList<>();
    vod_listinfo vodListinfo;
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;

    String username, vod_name, vodID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_list);

        sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String val = sharedPreferences.getString("userName","");

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.vod_refresh);
        recyclerView = (RecyclerView)findViewById(R.id.vod_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        vod_list_adapter = new vod_list_adapter(this, this, vod_ListInfo);

        initViews();

    }

    private void initViews(){

        recyclerView.setAdapter(vod_list_adapter);
        vod_list_adapter.notifyDataSetChanged();

        loadJson();

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                vod_ListInfo.clear();
                Toast.makeText(vod_list.this, "VOD 리스트 갱신 완료", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void loadJson(){
        sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String val = sharedPreferences.getString("userName","");

        Call<ResponseBody> call = RetrofitClient
                .getmInstance()
                .getApi()
                .vod_list(val);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    String s = response.body().string();
                    // {"success":true} 로그인 성공하면 이렇게 나옴
                    JSONArray jsonArray = new JSONArray(s);

                    if (s !=null) { //받아오기 성공시
                        Toast.makeText(vod_list.this, "받아오기 성공 ", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            username = jsonObject.getString("username");
                            vod_name = jsonObject.getString("vod_name");
                            vodID = jsonObject.getString("vodid");

                            vodListinfo = new vod_listinfo();
                            vodListinfo.setVod_name(vod_name);
                            vodListinfo.setVod_id(vodID);

                            vod_ListInfo.add(vodListinfo);
                            recyclerView.setAdapter(vod_list_adapter);

                        }
                    }
                    else { //vod리스트 받아오기 실패시
                        Toast.makeText(vod_list.this, "받아오기 실패 ", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    Toast.makeText(vod_list.this, "받아오기 실패 IOException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    Toast.makeText(vod_list.this, "받아오기 실패 JSONException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(vod_list.this, t.getMessage() + "받아오기 실패 onFailure ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

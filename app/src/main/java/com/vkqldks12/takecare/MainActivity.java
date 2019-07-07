package com.vkqldks12.takecare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vkqldks12.takecare.Ethereum_Token.TokenActivity;
import com.vkqldks12.takecare.VOD.vod_list;
import com.vkqldks12.takecare.broadcaster.BroadCasterActivity_;
import com.vkqldks12.takecare.viewer.ViewerActivity_;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button presenter, viewer, vod, token, test;
    SharedPreferences sharedPreferences;
    String sp_username, sp_nickname;
    TextView loginid, nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        sp_username = sharedPreferences.getString("userName","");
        sp_nickname = sharedPreferences.getString("userEmail","");
        Log.d("TAG","아이디 닉네임 ::::"+sp_nickname+"::::::"+sp_nickname);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header_view = navigationView.getHeaderView(0);

        loginid = (TextView)nav_header_view.findViewById(R.id.LoginID);
        nickname = (TextView)nav_header_view.findViewById(R.id.NickName);

        loginid.setText(sp_username);
        nickname.setText(sp_nickname);

        Log.d("TAG", "테스트" + nickname.getText());

        //깃 테스트

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_presenter){
            Intent intent = new Intent(MainActivity.this, BroadCasterActivity_.class);
                startActivity(intent);
        }else if (id == R.id.nav_viewer){
            Intent intent = new Intent(MainActivity.this, ViewerActivity_.class);
                startActivity(intent);
        }else if (id == R.id.nav_vod_list){
            Intent intent = new Intent(MainActivity.this, vod_list.class);
                startActivity(intent);
        }else if (id == R.id.nav_token){
            sharedPreferences = getSharedPreferences("CurrentWallet", Context.MODE_PRIVATE);
            Intent intent = new Intent(MainActivity.this, TokenActivity.class);
                startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

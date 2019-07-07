package com.vkqldks12.takecare.Splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.vkqldks12.takecare.Login_and_Logout.login;
import com.vkqldks12.takecare.MainActivity;
//import com.nhancv.kurentoandroid.main.MainActivity_;

/*
 * 어플리케이션 시작 시 표시되는 Splash Activity
  * 쉐어드 프리퍼런스에 저장된 아이디값 확인 후
  * 아이디가 존재하면 MainActivity로 이동, 아이디가 없다면 login Activity로 이동
 */

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vkqldks12.takecare.R.layout.activity_splash);

        ImageView imageView = (ImageView) findViewById(com.vkqldks12.takecare.R.id.splashimage);
        Animation ani = AnimationUtils.loadAnimation(this, com.vkqldks12.takecare.R.anim.alpha);
        imageView.startAnimation(ani);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
                    String val = sharedPreferences.getString("userName","");
                    if (val.length() == 0) {
                        Intent intent = new Intent(splash.this, login.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(splash.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}

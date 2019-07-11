package com.vkqldks12.takecare.Login_and_Logout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vkqldks12.takecare.MainActivity;

public class login extends AppCompatActivity {

    EditText nameText, emailText;
    Button checkbtn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.vkqldks12.takecare.R.layout.activity_login);

        nameText=(EditText)findViewById(com.vkqldks12.takecare.R.id.name_text);
        emailText=(EditText)findViewById(com.vkqldks12.takecare.R.id.email_address);
        checkbtn=(Button)findViewById(com.vkqldks12.takecare.R.id.checkbtn);

        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username=nameText.getText().toString();
                final String useremail=emailText.getText().toString();

                sharedPreferences=getSharedPreferences("userData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("userName",username);
                editor.putString("userEmail",useremail);
                editor.apply();

                Intent intent=new Intent(login.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

}

package com.vkqldks12.takecare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.vkqldks12.takecare.R;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class CheckDust extends AppCompatActivity {

    private BluetoothSPP bt;
    private ArcProgress arcProgress;

    private ImageView status_image, Connect;
    private TextView status_text, explain_text;

    private LinearLayout layout, layout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_dust);

        bt = new BluetoothSPP(this);

        arcProgress = (ArcProgress)findViewById(R.id.arc_progress);
        status_image = (ImageView)findViewById(R.id.dust_status_image);
        status_text = (TextView)findViewById(R.id.dust_status_text);
        explain_text = (TextView)findViewById(R.id.explainText);
        layout = (LinearLayout)findViewById(R.id.layout);
        layout2 = (LinearLayout)findViewById(R.id.layout2);
        Connect = (ImageView)findViewById(R.id.connect);

        if (!bt.isBluetoothAvailable()){
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {

                float nNumber = Float.parseFloat(message);
                Log.d("TAG", "수치로 변환한 값::"+nNumber);
                String pm25 = String.format("%.0f", nNumber);

                initProgress(pm25);

            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to"+name+"\n"+address, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Connected lost", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED){
                    bt.disconnect();
                }else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void initProgress(String pm25){
        int pm_25 = Integer.parseInt(pm25);

        if (0<= pm_25 && pm_25 <= 15){
            arcProgress.setFinishedStrokeColor(Color.parseColor("#ffffff"));
            arcProgress.setTextColor(Color.parseColor("#ffffff"));
            layout.setBackgroundColor(Color.parseColor("#006aff"));
            layout2.setBackgroundColor(Color.parseColor("#006aff"));
            status_image.setImageResource(R.drawable.lovee);
            status_text.setText("현재 상태 : 좋음");
            explain_text.setText("매우 좋은 상태 입니다.");
        }else if (15 < pm_25 && pm_25 <= 35){
            arcProgress.setFinishedStrokeColor(Color.parseColor("#ffffff"));
            arcProgress.setTextColor(Color.parseColor("#ffffff"));
            layout.setBackgroundColor(Color.parseColor("#01b6c3"));
            layout2.setBackgroundColor(Color.parseColor("#01b6c3"));
            status_image.setImageResource(R.drawable.goodd);
            status_text.setText("현재 상태 : 보통");
            explain_text.setText("그냥 무난한 상태 입니다.");
        }else if (35 < pm_25 && pm_25 <= 75){
            arcProgress.setFinishedStrokeColor(Color.parseColor("#ffffff"));
            arcProgress.setTextColor(Color.parseColor("#ffffff"));
            layout.setBackgroundColor(Color.parseColor("#ff6600"));
            layout2.setBackgroundColor(Color.parseColor("#ff6600"));
            status_image.setImageResource(R.drawable.sosoo);
            status_text.setText("현재 상태 : 나쁨");
            explain_text.setText("공기가 탁하네요~ 환기가 필요합니다.");
        }else if (75 < pm_25){
            arcProgress.setFinishedStrokeColor(Color.parseColor("#ffffff"));
            arcProgress.setTextColor(Color.parseColor("#ffffff"));
            layout.setBackgroundColor(Color.parseColor("#e60000"));
            layout2.setBackgroundColor(Color.parseColor("#e60000"));
            status_image.setImageResource(R.drawable.badd);
            status_text.setText("현재 상태 : 매우 나쁨");
            explain_text.setText("위험합니다! 빨리 환기 시켜주세요.");
        }
        arcProgress.setProgress(pm_25);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}

package com.vkqldks12.takecare.HeartBits;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.vkqldks12.takecare.R;
import com.vkqldks12.takecare.RetrofitClient;

import java.io.IOException;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeartBits extends AppCompatActivity {

    private BluetoothSPP bt;
    String user = "vkqldks";
    LineChart lineChart;
    Thread thread;
    boolean plotData = true;

    TextView beats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_bits);

        beats = (TextView)findViewById(R.id.txbeats);

        bt = new BluetoothSPP(this);

        lineChart = (LineChart)findViewById(R.id.chart);

        lineChart.setEnabled(true);

        LineData lineData = new LineData();
        lineChart.setData(lineData);

        if (!bt.isBluetoothAvailable()){
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        //블루투스를 통해 아두이노에서 넘어온 데이터를 수신하는 부분
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                TextView textView = findViewById(R.id.txReceive);
                textView.setText(message);

                int bits = Integer.parseInt(message);

                if (plotData){
                    addEntry(bits);
                    plotData = false;
                }

                Call<ResponseBody> call = RetrofitClient
                        .getMinstance()
                        .getApi()
                        .save_heart(user, message);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String s = response.body().string();
                            if (s !=null){
//                                Toast.makeText(MainActivity.this, "입력 성공", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(HeartBits.this, t.getMessage()+"입력 실패", Toast.LENGTH_SHORT).show();
                    }
                });

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

        Button btnconnect = findViewById(R.id.btnConnect);
        btnconnect.setOnClickListener(new View.OnClickListener() {
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

        Button movebtn = findViewById(R.id.btndbconnect);
        movebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HeartBits_detail.class);
                startActivity(intent);
            }
        });
    }

    private void startplot(){
        if (thread !=null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void addEntry(int data){
        LineData data1 = lineChart.getData();
        if (data1 !=null){
            ILineDataSet set = data1.getDataSetByIndex(0);
            if (set == null){
                set = createSet();
                data1.addDataSet(set);
            }
            data1.addEntry(new Entry(set.getEntryCount(), data),0);
            data1.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setMaxVisibleValueCount(200);
            lineChart.moveViewToX(data1.getDataSetCount());
        }
    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "bits");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(10f);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    @Override
    public void onResume() {
        super.onResume();
        startplot();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,BluetoothState.REQUEST_ENABLE_BT);
        }else {
            if (!bt.isServiceAvailable()){
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

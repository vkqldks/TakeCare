package com.vkqldks12.takecare.HeartBits;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeartBits_detail extends AppCompatActivity {

    String user = "vkqldks";
    LineChart lineChart;
    Thread thread;
    boolean plotData = true;

    TextView textView, beats2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_bits_detail);

        lineChart = (LineChart)findViewById(R.id.chart);

        lineChart.setEnabled(true);

        LineData lineData = new LineData();
        lineChart.setData(lineData);

        textView = (TextView)findViewById(R.id.getbits);
        beats2 = (TextView)findViewById(R.id.txbeats2);

        startgetbits();

    }

    public void startgetbits(){
        if (thread !=null){
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    getBits();
                    plotData = true;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void getBits(){
        Call<ResponseBody> call = RetrofitClient
                .getMinstance()
                .getApi()
                .receive_heart(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s =  response.body().string();
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("user_heart_bit");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String heartbit = jsonObject1.getString("heartbits");

                        textView.setText(heartbit);

                        int bits = Integer.parseInt(heartbit);
                        addEntry(bits);
                    }
                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(HeartBits_detail.this, t.getMessage()+"입력 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addEntry(long bits){
        LineData data = lineChart.getData();

        if (data !=null){
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null){
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), bits),0);
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(150);

            lineChart.moveViewToX(data.getEntryCount());
        }
    }

    public LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "DATA");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(10f);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }
}

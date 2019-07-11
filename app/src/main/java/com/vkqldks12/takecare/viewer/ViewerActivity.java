package com.vkqldks12.takecare.viewer;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.vkqldks12.takecare.Recyclerview_adapter.viewer_chat_adapter;
import com.vkqldks12.takecare.Recyclerview_model.viewer_chat_model;
import com.nhancv.webrtcpeer.rtc_plugins.ProxyRenderer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.webrtc.EglBase;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@EActivity(com.vkqldks12.takecare.R.layout.activity_viewer)
public class ViewerActivity extends MvpActivity<ViewerView, ViewerPresenter> implements ViewerView {
    private static final String TAG = ViewerActivity.class.getSimpleName();

    @ViewById(com.vkqldks12.takecare.R.id.vGLSurfaceViewCall)
    protected SurfaceViewRenderer vGLSurfaceViewCall;

    private EglBase rootEglBase;
    private ProxyRenderer remoteProxyRenderer;
    private Toast logToast;

    private String userName;
    private String userEmail;

    android.os.Handler handler;
    String data;
    SocketChannel socketChannel;
    private static final String HOST = "**.***.***.***";
    private static final int PORT = 5001;

    EditText Viewer_messege;
    Button Viewer_sendbtn;
    RecyclerView recyclerView;
    viewer_chat_adapter ViewerChatAdapter;
    List<viewer_chat_model> viewerChatModels = new ArrayList<>();

    @AfterViews
    protected void init() {
        android.content.SharedPreferences shp = getSharedPreferences("userData", Context.MODE_PRIVATE);
        userName = shp.getString("userName","");
        userEmail = shp.getString("userEmail","");
        Log.d("TAG","뷰어 유저네임 제대로 찍히나?"+userName+userEmail);

        //config peer
        remoteProxyRenderer = new ProxyRenderer();
        rootEglBase = EglBase.create();

        vGLSurfaceViewCall.init(rootEglBase.getEglBaseContext(), null);
        vGLSurfaceViewCall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        vGLSurfaceViewCall.setEnableHardwareScaler(true);
        vGLSurfaceViewCall.setMirror(true);
        remoteProxyRenderer.setTarget(vGLSurfaceViewCall);

        presenter.initPeerConfig();
        presenter.setinfo(userName,userEmail);
        presenter.startCall();

        Viewer_messege = (EditText)findViewById(com.vkqldks12.takecare.R.id.viewer_messege);
        Viewer_sendbtn = (Button)findViewById(com.vkqldks12.takecare.R.id.viewer_send_btn);
        recyclerView = (RecyclerView)findViewById(com.vkqldks12.takecare.R.id.viewer_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ViewerChatAdapter = new viewer_chat_adapter(getApplicationContext(), viewerChatModels);
        recyclerView.setAdapter(ViewerChatAdapter);

        handler = new android.os.Handler();

        new Thread(() -> {
            try {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(true);
                socketChannel.connect(new InetSocketAddress(HOST, PORT));

                final String viewerInfo = "viewer/"+userName+"/방송참여/"+userEmail;

                Log.d("TAG","방송 입장시 띄울 메세지와 방송자 정보::"+viewerInfo);

                new SendmsgTask().execute(viewerInfo);

            } catch (Exception ioe) {
                Log.d("TAG", ioe.getMessage() + "::");
                ioe.printStackTrace();

            }
            checkUpdate.start();
        }).start();

        Viewer_sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String return_msg = "none/"+userName+"/"+Viewer_messege.getText().toString()+"/"+userEmail;
                    if (!TextUtils.isEmpty(return_msg)) {
                        new SendmsgTask().execute(return_msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void disconnect() {
        remoteProxyRenderer.setTarget(null);
        if (vGLSurfaceViewCall != null) {
            vGLSurfaceViewCall.release();
            vGLSurfaceViewCall = null;
        }
        finish();
    }

    @NonNull
    @Override
    public ViewerPresenter createPresenter() {
        return new ViewerPresenter(getApplication());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.disconnect();
    }

    @Override
    public void stopCommunication() {
        onBackPressed();
    }

    @Override
    public void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return rootEglBase.getEglBaseContext();
    }

    @Override
    public VideoRenderer.Callbacks getRemoteProxyRenderer() {
        return remoteProxyRenderer;
    }

    private class SendmsgTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                socketChannel
                        .socket()
                        .getOutputStream()
                        .write(strings[0].getBytes("UTF-8")); // 서버로
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(() -> Viewer_messege.setText(""));
        }
    }

    void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                int readByteCount = socketChannel.read(byteBuffer); //데이터받기
                Log.d("readByteCount", readByteCount + "");
                if (readByteCount == -1) { //서버가 정상적으로 Socket의 close()를 호출했을 경우
                    throw new IOException();
                }

                byteBuffer.flip(); // 문자열로 변환
                Charset charset = Charset.forName("UTF-8");
                data = charset.decode(byteBuffer).toString();
                Log.d("receive", "msg :" + data);
                handler.post(showUpdate);
            } catch (IOException e) { //서버가 비정상적으로 종료했을 경우 IOException 발생
                Log.d("getMsg", e.getMessage() + "");
                try {
                    socketChannel.close();
                    break;
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    private Thread checkUpdate = new Thread() {

        public void run() {
            try {
                String line;
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable showUpdate = new Runnable() {

        public void run() {
            String receive = data;

            String Nick = receive.split("/")[0];
            String Msg = receive.split("/")[1];

            Log.d("TAG","넘어온 메세지는 무엇?"+receive);
            viewerChatModels.add(new viewer_chat_model(Nick,Msg));
            ViewerChatAdapter.notifyDataSetChanged();
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

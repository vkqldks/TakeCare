package com.vkqldks12.takecare.Rtc_Peer.kurento;

import android.util.Log;

import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;
import com.nhancv.webrtcpeer.rtc_peer.RTCClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Created by nhancao on 7/18/17.
 */

public class KurentoPresenterRTCClient implements RTCClient {
    private static final String TAG = KurentoPresenterRTCClient.class.getSimpleName();

    private SocketService socketService;

    private String userName;
    private String userEmail;
    private int fake_id = 10;

    public KurentoPresenterRTCClient(SocketService socketService) {
        this.socketService = socketService;
    }

    public void setInfo(String userName, String userEmail){
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public void connectToRoom(String host, BaseSocketCallback socketCallback) {
        socketService.connect(host, socketCallback);
    }

    @Override
    public void sendOfferSdp(SessionDescription sdp) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "presenter");
            obj.put("sdpOffer", sdp.description);
//            obj.put("fake_id", fake_id);
            obj.put("user_name", userName);
//            obj.put("user_email", userEmail);

//            JSONObject infojson = new JSONObject();
//            infojson.put("user_name", userName);
//            infojson.put("user_email", userEmail);
            Log.d("TAG", "BroadCasterPresenter에서 RTCClient로 유저정보가 제대로 넘어왔나??"+obj);

//            obj.put("info", infojson);

            socketService.sendMessage(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp) {
        Log.e(TAG, "sendAnswerSdp: ");
    }

    @Override
    public void sendLocalIceCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "onIceCandidate");
            JSONObject candidate = new JSONObject();
            candidate.put("candidate", iceCandidate.sdp);
            candidate.put("sdpMid", iceCandidate.sdpMid);
            candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            obj.put("candidate", candidate);

            socketService.sendMessage(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {
        Log.e(TAG, "sendLocalIceCandidateRemovals: ");
    }

}

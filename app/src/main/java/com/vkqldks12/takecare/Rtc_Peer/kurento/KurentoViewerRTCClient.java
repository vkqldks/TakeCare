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

public class KurentoViewerRTCClient implements RTCClient {
    private static final String TAG = KurentoViewerRTCClient.class.getSimpleName();

    private SocketService socketService;
    private String userName;
    private String userEmail;
    private int fake_view_id = 10;

    public KurentoViewerRTCClient(SocketService socketService) {
        this.socketService = socketService;
    }

    public void settinginfo(String userName, String userEmail){
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
            obj.put("id", "viewer");
            obj.put("sdpOffer", sdp.description);
//            obj.put("fake_view", fake_view_id);
            obj.put("viewer_username", userName);
//            obj.put("viewer_userEmail", userEmail);

            Log.d("TAG", "ViewerPresenter에서 RTCClient로 유저정보가 제대로 넘어왔나??"+obj);

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

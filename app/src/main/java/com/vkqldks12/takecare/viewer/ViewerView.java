package com.vkqldks12.takecare.viewer;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.webrtc.EglBase;
import org.webrtc.VideoRenderer;


public interface ViewerView extends MvpView {
    void stopCommunication();

    void logAndToast(String msg);

    void disconnect();

    EglBase.Context getEglBaseContext();

    VideoRenderer.Callbacks getRemoteProxyRenderer();
}

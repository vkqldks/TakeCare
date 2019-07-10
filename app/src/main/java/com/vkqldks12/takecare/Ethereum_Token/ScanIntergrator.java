package com.vkqldks12.takecare.Ethereum_Token;

import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by vkqld on 2019-02-28.
 */

public class ScanIntergrator {

    public static IntentIntegrator sIntentIntergrator;

    public ScanIntergrator(Activity activity){
        sIntentIntergrator = new IntentIntegrator(activity);
    }

    public void startScan(){
        sIntentIntergrator.setOrientationLocked(false);
        sIntentIntergrator.setBarcodeImageEnabled(true);
        sIntentIntergrator.initiateScan();
    }
}

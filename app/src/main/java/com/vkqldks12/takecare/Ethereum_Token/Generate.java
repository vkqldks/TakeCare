package com.vkqldks12.takecare.Ethereum_Token;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Created by vkqld on 2019-02-28.
 */

public class Generate {

    public Bitmap Get(String value, int Width, int Heigth){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(value, BarcodeFormat.DATA_MATRIX.QR_CODE, Width, Heigth);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        }catch (WriterException e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
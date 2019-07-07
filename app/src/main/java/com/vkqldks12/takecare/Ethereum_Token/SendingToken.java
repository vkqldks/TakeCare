package com.vkqldks12.takecare.Ethereum_Token;


import android.os.AsyncTask;
import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

/**
 * Created by vkqld on 2019-02-28.
 */

public class SendingToken {

    private Credentials mCredentials;
    private Web3j mWeb3j;
    private String fromAddress;
    private String mValueGasPrice;
    private String mValueGasLimit;

    private CBSendingToken cbSendingToken;

    public SendingToken(Web3j web3j, Credentials credentials, String valueGasPrice, String valueGasLimit){
        mWeb3j = web3j;
        mCredentials = credentials;
        fromAddress = credentials.getAddress();
        mValueGasPrice = valueGasPrice;
        mValueGasLimit = valueGasLimit;
    }

    private BigInteger getGasPrice(){
        return BigInteger.valueOf(Long.valueOf(mValueGasPrice));
    }

    private BigInteger getGasLimit(){
        return BigInteger.valueOf(Long.valueOf(mValueGasLimit));
    }

    public void Send(String smartContractAddress, String toAddress, String valueAmmount){
        new SendToken().execute(smartContractAddress, toAddress, valueAmmount);
    }

    private class SendToken extends AsyncTask<String,Void,TransactionReceipt>{

        @Override
        protected TransactionReceipt doInBackground(String... value) {
            BigInteger amount = BigInteger.valueOf(Long.parseLong(value[2]));
            Log.d("BBBBBBB","스마트 컨트랙트로 넘어가는 값은 어떻게 나타나는가:::::"+amount);

            System.out.println(getGasPrice());
            System.out.println(getGasLimit());

            Jacken jacken = Jacken.load(value[0], mWeb3j, mCredentials, getGasPrice(), getGasLimit());
            try {
                TransactionReceipt result = jacken.transfer(value[1], amount).send();
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(TransactionReceipt result){
            super.onPostExecute(result);
            cbSendingToken.backSendToken(result);
        }
    }

    public void registerCallBackToken(TokenActivity cbSendingToken){
        this.cbSendingToken = cbSendingToken;
    }
}

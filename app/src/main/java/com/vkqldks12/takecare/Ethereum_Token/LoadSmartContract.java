package com.vkqldks12.takecare.Ethereum_Token;


import android.os.AsyncTask;
import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vkqld on 2019-02-26.
 */

public class LoadSmartContract {
    private CBLoadSmartContract cbLoadSmartContract;
    private Web3j mweb3j;
    private Credentials mCredentials;
    private String mSmartContractAddress;
    private BigInteger mGasPrice;
    private BigInteger mGasLimit;

    public LoadSmartContract(Web3j web3j,
                             Credentials credentials,
                             String smartContractAddress,
                             BigInteger gasPrice,
                             BigInteger gasLimit){
        mweb3j = web3j;
        mCredentials = credentials;
        mSmartContractAddress = smartContractAddress;
        mGasPrice = gasPrice;
        mGasLimit = gasLimit;
    }

    public void LoadToken(){
        new Token().execute();
    }

    private class Token extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            try {
                String address = mCredentials.getAddress();
                Log.d("TAG","어드레스:::"+address);

                //토큰 다운로드
                Jacken jacken = Jacken.load(mSmartContractAddress, mweb3j, mCredentials, mGasPrice,mGasLimit);
                String tokenName = jacken.name().send();
                String tokenSymbol = jacken.symbol().send();
                String tokenAddress = jacken.getContractAddress();
                BigInteger totalSupply = jacken.totalSupply().send();
                BigInteger tokenBalance = jacken.balanceOf(address).send();

                String trans = Convert.fromWei(tokenBalance.toString(), Convert.Unit.ETHER).toString();

                Log.d("TTAGG", "토큰 정보들:::"+tokenName+":::"+tokenAddress+":::"+tokenBalance);

                Map<String, String> result = new HashMap<>();
                result.put("tokenname", tokenName);
                result.put("tokensymbol", tokenSymbol);
                result.put("tokenaddress", tokenAddress);
                result.put("tokensupply", totalSupply.toString());
                result.put("tokenbalance", tokenBalance.toString());
//                result.put("tokenbalance", trans);

                return result;
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> result){
            super.onPostExecute(result);
            if (result !=null){
                cbLoadSmartContract.backLoadSmartContract(result);
            }
        }
    }

    public void registerCallBack(CBLoadSmartContract cbLoadSmartContract){
        this.cbLoadSmartContract = cbLoadSmartContract;
    }
}

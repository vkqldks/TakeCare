package com.vkqldks12.takecare.Ethereum_Token;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vkqldks12.takecare.R;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TokenActivity extends AppCompatActivity implements CBLoadSmartContract, CBSendingToken{

    Context context = this;

    private ImageView qr_small;         //qr코드 작은버전
    private ImageView qr_big;           //qr코드 큰 버전(작은 버전의 qr코드를 클릭했을때 뜨는 qr코드)
    private TextView walletAddress;     //지갑 주소
    private TextView etheAccount;       //이더 잔액
    private TextView tokenAccount;      //토큰 잔액
    private TextView receiver_address;
    private Button scan_qr;
    private EditText send_value;
    private Button sendbtn;             //전송 버튼

    private Web3j mweb3j;
    private Jacken jacken;
    Credentials  credentials;
    EthGetBalance ethGetBalance;
    SendingToken sendingToken;
    String exchage;

    DecimalFormat decimalFormat = new DecimalFormat("#,##0");

    int mGasprice = 20;
    int mGasLimit = 300000;

    String mContract = "0xD0486af3Be178Bba2AC1a59ba4A702D13667107e"; //jacken 스마트 컨트랙트 주소


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        qr_small = (ImageView)findViewById(R.id.qr_small);
        walletAddress = (TextView)findViewById(R.id.walletAddress);
        etheAccount = (TextView)findViewById(R.id.etheAccount);
        tokenAccount = (TextView)findViewById(R.id.tokenAccount);
        receiver_address = (TextView)findViewById(R.id.receiver_address);
        scan_qr = (Button)findViewById(R.id.scan_qr);
        send_value = (EditText)findViewById(R.id.send_value);
        sendbtn = (Button)findViewById(R.id.sendbtn);




        mweb3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/v3/b35d2f3bf05d455188510f92e6aee0ce"));
        try {
//            credentials = Credentials.create("D6F5A6131599DAF1A89EDBF5FFAF01B87BE0AD8D01223C8137D13902B4228804"); //이곳에 메타마스크 지갑의 개인키를 입력하면 그 키에 해당하는 지갑에 대한 정보를 가져온다.
                                                                                                                  //현재는 CCTV 지갑의 개인키 사용

            credentials = Credentials.create("F7E027D89F4763D84A7D744365A649876EAF6564E9E45A0D26EEE62E96D66004"); //이곳에 메타마스크 지갑의 개인키를 입력하면 그 키에 해당하는 지갑에 대한 정보를 가져온다.
                                                                                                                  //현재는 test 지갑의 개인키 사용

            Log.d("TAG","주소값은 어떻게 나오나::"+credentials.getAddress());

            ethGetBalance = mweb3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();
            exchage = Convert.fromWei(wei.toString(), Convert.Unit.ETHER).toString();
            Log.d("TAG","지갑 정보는:::"+exchage);
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

        walletAddress.setText(credentials.getAddress()); //메타마스크 지갑 주소
        etheAccount.setText(exchage); //메타마스크 지갑에 보유중인 이더 량
        qr_small.setImageBitmap(new Generate().Get(credentials.getAddress(), 800, 800));

        String Address = credentials.getAddress();
        Log.d("TAGG","주소값은? "+Address);


        getWalletinfo();

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.scan_qr :
                new ScanIntergrator(this).startScan();
                break;
            case R.id.sendbtn :
                sendToken();
                break;
        }
    }

    public String getEtherAddress(){
        return credentials.getAddress();
    }

    public void getWalletinfo(){
        LoadSmartContract loadSmartContract = new LoadSmartContract(mweb3j, credentials, mContract, BigInteger.valueOf(10), BigInteger.valueOf(300000));

        Log.d("TAG", "이곳에서 credentials는 무슨 값인가?::"+credentials);

        loadSmartContract.registerCallBack(this);
        loadSmartContract.LoadToken();
    }

    @Override
    public void backLoadSmartContract(Map<String, String> result) {
//        setTokenAddress(result.get("tokenaddress"));
//        setTokenName(result.get("tokenname"));
//        setTokenSymbol(result.get("tokensymbol"));
        setTokenBalance(result.get("tokenbalance"));
//        setTokenSupply(result.get("tokensupply"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(context, "Result Not Found", Toast.LENGTH_SHORT).show();
            }else {
                receiver_address.setText(result.getContents());
                Log.d("TTTTTTT","수취인 주소::::::::"+result.getContents());
                Toast.makeText(context,"수취인 주소", Toast.LENGTH_SHORT).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode,data);
        }
    }



    public void setTokenBalance(String value){
        tokenAccount.setText(value);
    }

    public void sendToken(){
        String targetAdd = receiver_address.getText().toString();
        String ADD = targetAdd.replaceFirst("ethereum:","");
        Log.d("TAGGGG","받는사람 주소:::::"+ADD);

        Integer trans1 = Integer.valueOf(send_value.getText().toString());
        Log.d("Tag","1차 변환 값은:::::"+trans1);
        BigInteger trans2 = BigInteger.valueOf(trans1);
        Log.d("Tag","2차 변환 값은:::::"+trans2);

        String gasprice = String.valueOf(mGasprice);
        String gaslimit = String.valueOf(mGasLimit);

        sendingToken = new SendingToken(mweb3j, credentials, gasprice, gaslimit);
        sendingToken.registerCallBackToken(this);
        sendingToken.Send(mContract, ADD, send_value.getText().toString());
    }

    @Override
    public void backSendToken(TransactionReceipt result){
        Toast.makeText(context, "토큰 전송 성공!!", Toast.LENGTH_SHORT).show();
        getWalletinfo();
    }

}

package com.sil.ucubesampleapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sil.R;
import com.sil.ucubesampleapplication.dialog.CustomDialog;
import com.sil.ucubesampleapplication.utils.Constants;
import com.sil.ucubesdk.BluetoothConnexionManager;
import com.sil.ucubesdk.POJO.UCubeRequest;
import com.sil.ucubesdk.StatusCallBack;
import com.sil.ucubesdk.UCubeCallBacks;
import com.sil.ucubesdk.UCubeManager;
import com.sil.ucubesdk.UCubeVoidCallBacks;
import com.sil.ucubesdk.payment.TransactionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //    private static final String LICENSEY_KEY = "U5DEA7909FAE37F23463B6C65E387A1CD";
    private static final String LICENSEY_KEY = "rEUHm7gjU6l0EbVbiOng0OWfiBbD4ZBV";
    private static final int REQUEST_ENABLE_BT = 1120;

    UCubeManager uCubeManager;
    UCubeRequest uCubeRequest;
    Button tranxBtn, statusBtn, voidBtn;
    TextView statusTv, responseCodeTv, responseMessageTv;
    String trasactionId = "";
    CustomDialog customDialog;
    private String voidAmt = "";
    private String strRrn = "";
    String bluetoothAdddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private int blu() {
        // 0 = connected, 1 = not connected, 2 = config error, 3 = enable bluetooth;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return 2;
            // Device doesn't support Bluetooth
        } else if (!bluetoothAdapter.isEnabled()) {
            return 3;
        } else {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.e(TAG, "blu: NAME : " + deviceName + " ADDRESS : " + deviceHardwareAddress);

                    if (deviceHardwareAddress.equalsIgnoreCase(bluetoothAdddress)) {
                        return 0;
                    }
                }
            }

            return 1;
        }
    }

    boolean b;

    private void init() {

        voidBtn = findViewById(R.id.void_button);
        tranxBtn = findViewById(R.id.transaction_button);
        statusBtn = findViewById(R.id.status_button);
        statusTv = findViewById(R.id.status_code);
        responseCodeTv = findViewById(R.id.response_code);
        responseMessageTv = findViewById(R.id.response_message);

        uCubeManager = UCubeManager.getInstance(this, LICENSEY_KEY);
        customDialog = new CustomDialog(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bluetoothAdddress = extras.getString(Constants.BLUETOOTH_ADDRESS);
        }

        tranxBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("onClick", "onClick: ");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        b = uCubeManager.isBluetoothConnected(bluetoothAdddress);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "onClick() returned: IS_CONNECTED : " + b);
                                if (b) preReq();
                            }
                        });
                    }
                });
                thread.start();
            }
        });

        statusBtn.setOnClickListener(this);

        voidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trasactionId;
                uCubeRequest = new UCubeRequest();
                trasactionId = uCubeManager.getTransactionId();
                Log.d(TAG, "connectDevice: " + trasactionId);
                uCubeRequest.setUsername("AVI");
                uCubeRequest.setPassword("1230");
                uCubeRequest.setRefCompany("MAHAGRAM");
                uCubeRequest.setMid("442000227364352");
                uCubeRequest.setSession_Id(""); //sha256 hash of <username>:<rrn>:<amt>:<secret key>
                uCubeRequest.setTid("42207331");
                uCubeRequest.setImei("869798039905855");
                uCubeRequest.setImsi("404277270869423");
                uCubeRequest.setTxn_amount(voidAmt);
                uCubeRequest.setTransactionId(strRrn);
                Log.d(TAG, "bluetoothAdddress: " + bluetoothAdddress);
                uCubeRequest.setBt_address(bluetoothAdddress);
                uCubeRequest.setRequestCode(TransactionType.DEBIT); //INQUIRY, DEBIT, WITHDRAWAL
                callVoid();
            }
        });

        //uCubeRequest.setTxn_amount("10");
        //uCubeRequest.setTransactionId("108113022549");

    }

    private void preReq() {
        try {
            uCubeRequest = new UCubeRequest();
            uCubeRequest.setUsername("AVI");
            uCubeRequest.setPassword("1230");
            uCubeRequest.setRefCompany("MAHAGRAM");
            uCubeRequest.setMid("442000227364352");
            uCubeRequest.setTid("42207331");
            uCubeRequest.setImei("869798039905855");
            uCubeRequest.setTransactionId(trasactionId);
            uCubeRequest.setImsi("404277270869423");
            uCubeRequest.setTxn_amount("0");
            Log.d(TAG, "bluetoothAdddress: " + bluetoothAdddress);
            uCubeRequest.setBt_address(bluetoothAdddress);
//            uCubeRequest.setBt_address(null);
//            uCubeRequest.setBt_address("B0:EC:8F:10:43:00");
            uCubeRequest.setRequestCode(TransactionType.INQUIRY); //INQUIRY, DEBIT, WITHDRAWAL

            strRrn = trasactionId;
            voidAmt = "10";

            /*uCubeRequest.setUsername("9773771886");
            uCubeRequest.setPassword("6934");
            uCubeRequest.setRefCompany("mBnK");
            uCubeRequest.setMid("MBNK00000000001");
            uCubeRequest.setTid("MBNK0001");
            uCubeRequest.setTransactionId(trasactionId);
            uCubeRequest.setImei("868922038058448");
            uCubeRequest.setImsi("404909317325621");
            uCubeRequest.setTxn_amount("0");
            uCubeRequest.setBt_address(bluetoothAdddress);
            uCubeRequest.setRequestCode(TransactionType.INQUIRY);*/

            connectDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void connectDevice() {

        showStatusDialog(true);
        trasactionId = uCubeManager.getTransactionId();
        uCubeRequest.setTransactionId(trasactionId); //you can have your own 12digit integer Id or create one using UCubeManager;
        uCubeManager.execute(uCubeRequest, new UCubeCallBacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                hideDialog();
                Log.d(TAG, "successCallback: " + jsonObject);
                try {
                    String status = "Success";
                    int responseCode = -1;
                    JSONObject responseMessage = new JSONObject();

                    if (jsonObject.has("Msg")) {
                        status = jsonObject.getString("Msg");
                    }
                    if (jsonObject.has("ResponseCode")) {
                        responseCode = jsonObject.getInt("ResponseCode");
                    }
                    if (jsonObject.has("Response")) {
                        responseMessage = jsonObject.getJSONObject("Response");
                    }

                    setMessage(status, responseCode, responseMessage.toString());
                } catch (JSONException jsonexception) {
                    jsonexception.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void progressCallback(String message) {
                Log.d(TAG, "progressCallback: " + message);
                updateTransactionMessage(message);
            }

            @Override
            public void failureCallback(JSONObject jsonObject) {
                hideDialog();
                Log.d(TAG, "failureCallback: " + jsonObject);
                try {
                    String status = "Success";
                    int responseCode = -1;

                    if (jsonObject.has("Msg")) {
                        status = jsonObject.getString("Msg");
                    }
                    if (jsonObject.has("ResponseCode")) {
                        responseCode = jsonObject.getInt("ResponseCode");
                    }
                    if (responseCode == 100) {
                        try {
                            JSONObject responseMessage = new JSONObject();
                            if (jsonObject.has("Response")) {
                                responseMessage = jsonObject.getJSONObject("Response");
                                setMessage(status, responseCode, responseMessage.toString());
                            }
                        } catch (JSONException jsonexception) {
                            String responseMessage = "";
                            if (jsonObject.has("Response")) {
                                responseMessage = jsonObject.getString("Response");
                            }
                            setMessage(status, responseCode, responseMessage);
                            jsonexception.printStackTrace();
                        }

                    } else {
                        String responseMessage = "";
                        if (jsonObject.has("Response")) {
                            responseMessage = jsonObject.getString("Response");
                        }
                        setMessage(status, responseCode, responseMessage);
                    }
                } catch (JSONException jsonexception) {
                    jsonexception.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void callVoid() {
        uCubeManager.voidTransaction(uCubeRequest, new UCubeVoidCallBacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.d(TAG, "T successCallback: " + jsonObject.toString());
                Log.d(TAG, "successCallback: " + jsonObject);
                try {
                    String status = "Success";
                    int responseCode = -1;
                    JSONObject responseMessage = new JSONObject();

                    if (jsonObject.has("Msg")) {
                        status = jsonObject.getString("Msg");
                    }
                    if (jsonObject.has("ResponseCode")) {
                        responseCode = jsonObject.getInt("ResponseCode");
                    }
                    if (jsonObject.has("Response")) {
                        responseMessage = jsonObject.getJSONObject("Response");
                    }

                    setMessage(status, responseCode, responseMessage.toString());
                } catch (JSONException jsonexception) {
                    jsonexception.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void progressCallback(String string) {
                Log.d(TAG, "T progressCallback: " + string);
            }

            @Override
            public void failureCallback(JSONObject jsonObject) {
                Log.d(TAG, "failureCallback: " + jsonObject);
                try {
                    String status = "Success";
                    int responseCode = -1;

                    if (jsonObject.has("Msg")) {
                        status = jsonObject.getString("Msg");
                    }
                    if (jsonObject.has("ResponseCode")) {
                        responseCode = jsonObject.getInt("ResponseCode");
                    }
                    if (responseCode == 100) {
                        try {
                            JSONObject responseMessage = new JSONObject();
                            if (jsonObject.has("Response")) {
                                responseMessage = jsonObject.getJSONObject("Response");
                                setMessage(status, responseCode, responseMessage.toString());
                            }
                        } catch (JSONException jsonexception) {
                            String responseMessage = "";
                            if (jsonObject.has("Response")) {
                                responseMessage = jsonObject.getString("Response");
                            }
                            setMessage(status, responseCode, responseMessage);
                            jsonexception.printStackTrace();
                        }

                    } else {
                        String responseMessage = "";
                        if (jsonObject.has("Response")) {
                            responseMessage = jsonObject.getString("Response");
                        }
                        setMessage(status, responseCode, responseMessage);
                    }
                } catch (JSONException jsonexception) {
                    jsonexception.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private void hideDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showStatusDialog(false);
            }
        });
    }

    private void updateTransactionMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (customDialog != null && customDialog.isShowing()) {
                    customDialog.setMessage(message);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.transaction_button:
                connectDevice();
                break;*/
            case R.id.status_button:
                checkStatus();
                break;
        }
    }

    //Check status is applicable only for TransactionType.DEBIT and TransactionType.WITHDRAWAL
    private void checkStatus() {
        uCubeManager.checkStatus(uCubeRequest, new StatusCallBack() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.d(TAG, "checkStatus successCallback: " + jsonObject);
            }

            @Override
            public void failureCallback(JSONObject jsonObject) {
                Log.d(TAG, "checkStatus failureCallback: " + jsonObject);
            }

            @Override
            public void exceptionCallback(String exceptionMessage, UCubeRequest uCubeRequest, JSONObject respObj) {

            }
        });
    }

    private void showStatusDialog(boolean show) {
        if (customDialog != null && customDialog.isShowing() && !show) {
            customDialog.dismiss();
            tranxBtn.setEnabled(true);
            statusBtn.setEnabled(true);
        } else {
            customDialog = new CustomDialog(this);
            customDialog.show();
            tranxBtn.setEnabled(false);
            statusBtn.setEnabled(false);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setMessage(final String status, final int responseCode,
                            final String responseMessage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (statusTv != null && status != null && !status.isEmpty()) {
                    statusTv.setText(status);
                }
                if (responseCodeTv != null) {
                    responseCodeTv.setText(responseCode + "");
                }
                if (responseMessageTv != null && responseMessage != null && !responseMessage.isEmpty()) {
                    responseMessageTv.setText(responseMessage);
                }
            }
        });
    }

    void setMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTv.setText(null);
                statusTv.setText(msg);
            }
        });
    }
}

package com.sil.ucubesample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sil.ucubesample.dialog.CustomDialog;
import com.sil.ucubesample.utils.Constants;
import com.sil.ucubesdk.POJO.UCubeRequest;
import com.sil.ucubesdk.StatusCallBack;
import com.sil.ucubesdk.UCubeCallBacks;
import com.sil.ucubesdk.UCubeManager;
import com.sil.ucubesdk.UCubeVoidCallBacks;
import com.sil.ucubesdk.payment.TransactionType;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String LICENSEY_KEY = "U5DEA7909FAE37F23463B6C65E387A1CD";
    UCubeManager uCubeManager;
    UCubeRequest uCubeRequest;
    Button tranxBtn, statusBtn, voidBtn;
    TextView statusTv, responseCodeTv, responseMessageTv;
    String trasactionId = "";
    CustomDialog customDialog;
    String bluetoothAdddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tranxBtn = findViewById(R.id.transaction_button);
        statusBtn = findViewById(R.id.status_button);
        voidBtn = findViewById(R.id.void_button);
        statusTv = findViewById(R.id.status_code);
        responseCodeTv = findViewById(R.id.response_code);
        responseMessageTv = findViewById(R.id.response_message);

        uCubeManager = UCubeManager.getInstance(this, LICENSEY_KEY);
        customDialog = new CustomDialog(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            bluetoothAdddress = extras.getString(Constants.BLUETOOTH_ADDRESS);
            uCubeRequest = new UCubeRequest();
            uCubeRequest.setUsername("AVI");
            uCubeRequest.setPassword("1230");
            uCubeRequest.setRefCompany("MAHAGRAM");
            uCubeRequest.setMid("442000227364352");
            uCubeRequest.setTid("42207333");
            uCubeRequest.setImei("869798039905855");
            uCubeRequest.setTransactionId(trasactionId);
            uCubeRequest.setImsi("404277270869423");
            uCubeRequest.setTxn_amount("0");
            Log.d(TAG, "bluetoothAdddress: " + bluetoothAdddress);
            uCubeRequest.setBt_address(bluetoothAdddress);
            uCubeRequest.setRequestCode(TransactionType.INQUIRY); //INQUIRY, DEBIT, WITHDRAWAL

            tranxBtn.setOnClickListener(this);
            statusBtn.setOnClickListener(this);
            voidBtn.setOnClickListener(this);
        }
    }

    void callWS() {
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

    void callWSVoid() {

        showStatusDialog(true);
        //trasactionId = uCubeManager.getTransactionId();
        uCubeRequest.setTransactionId(trasactionId); //you can have your own 12digit integer Id or create one using UCubeManager;
        uCubeManager.voidTransaction(uCubeRequest, new UCubeVoidCallBacks() {
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
            public void progressCallback(String s) {
                Log.d(TAG, "progressCallback: " + s);
                updateTransactionMessage(s);
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
        int id = view.getId();
        if (id == R.id.transaction_button) {
            if (uCubeManager.isBluetoothConnected(bluetoothAdddress)) {
                callWS();
            } else {
                Toast.makeText(this, "Please turn on bluetooth device and try again.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.status_button) {
            checkStatus();
        } else if (id == R.id.void_button) {
            if (uCubeManager.isBluetoothConnected(bluetoothAdddress)) {
                callWSVoid();
            } else {
                Toast.makeText(this, "Please turn on bluetooth device and try again.", Toast.LENGTH_SHORT).show();
            }
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
            public void exceptionCallback(String s, UCubeRequest uCubeRequest, JSONObject jsonObject) {

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
    private void setMessage(final String status, final int responseCode, final String responseMessage) {
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
}

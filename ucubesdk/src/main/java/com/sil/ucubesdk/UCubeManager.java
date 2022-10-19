package com.sil.ucubesdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sil.ucubesdk.POJO.UCubeRequest;
import com.sil.ucubesdk.mdm.MDMManager;
import com.sil.ucubesdk.payment.AbstractPaymentService;
import com.sil.ucubesdk.payment.CardReaderType;
import com.sil.ucubesdk.payment.Currency;
import com.sil.ucubesdk.payment.MyPaymentAuth;
import com.sil.ucubesdk.payment.PaymentContext;
import com.sil.ucubesdk.payment.PaymentService;
import com.sil.ucubesdk.payment.RiskManagementTask;
import com.sil.ucubesdk.payment.TransactionType;
import com.sil.ucubesdk.rest.NetworkController;
import com.sil.ucubesdk.rest.RequestParams;
import com.sil.ucubesdk.rest.ResponseListener;
import com.sil.ucubesdk.rest.ResponseParams;
import com.sil.ucubesdk.rpc.MyConst;
import com.sil.ucubesdk.rpc.RPCManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

import retrofit2.Response;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.sil.ucubesdk.AndyUtility.getISOField11;
import static com.sil.ucubesdk.rpc.RPCManager.setRPCManager;


@SuppressLint("StaticFieldLeak")
public class UCubeManager {

    private boolean nfcEnabled;
    private static UCubeManager ourInstance;
    private static final String TAG = UCubeManager.class.getSimpleName();
    private String validateId = null;
    private UCubeRequest UCubeRequest = null;
    private Context context = null;
    private static String ucubeKey = null;
    PaymentService svc;
    private ResourceBundle msgBundle;
    String displayMsg = "";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
    private boolean onlyICC = false;

    public static UCubeManager getInstance(Context context, String key) {
        if (ourInstance == null) {
            ourInstance = new UCubeManager(context);
            ucubeKey = key;
        }

        return ourInstance;
    }

    private UCubeManager() {
    }

    private UCubeManager(Context context) {
        this.context = context;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public ArrayList<BluetoothDevice> openBluetoothPairedList() {
        ArrayList<BluetoothDevice> connectedBTDevice = new ArrayList<>();
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                showToast("Device does not support Bluetooth");
            } else if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled :)
                showToast("Kindly enable Bluetooth");
            } else {
                BluetoothManager manager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
                if (manager != null) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice bt : pairedDevices) {
                            if (bt.getType() == 3) {
                                connectedBTDevice.add(bt);
                            }
                        }
                    }

                    if (connectedBTDevice.size() > 0) {
                        //  openBluetoothSelectionDialog(connectedBTDevice);
                    } else {
                        showToast("No Device Found.\nKindly Connect to New Device");
                    }
                }
            }
            return connectedBTDevice;

        }

        catch (Exception e) {
            e.printStackTrace();
        }
        return connectedBTDevice;
    }


    public boolean isBluetoothConnected(String bluetoothAddress) {

        boolean isConnected = false;

        try {
            Log.d(TAG, "isBluetoothConnected:With Bluetooth Address " + bluetoothAddress);
            if (bluetoothAddress == null) return false;
            if (BluetoothConnexionManager.getInstance().canConnect(bluetoothAddress)) {
                BluetoothConnexionManager.getInstance().connect();
                return true;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void execute(UCubeRequest UCubeRequest, UCubeCallBacks uCubeCallBacks) {
        if (ucubeKey != null && !ucubeKey.isEmpty() && !ucubeKey.trim().isEmpty()) {
            if (context != null) {
                if (UCubeRequest != null && UCubeRequest.isValidRequest()) {
                    this.UCubeRequest = UCubeRequest;
                    try {
                        this.msgBundle = new PropertyResourceBundle(context.getResources().openRawResource(R.raw.ucube_strings));
                    } catch (Exception var6) {
                        LogManager.debug("ERROR", "Unable to load uCube message bundle", var6);
                    }
                    validateId = context.getPackageName();
                    if (validateId != null) {
                        validePackage(UCubeRequest, uCubeCallBacks, null, false);
                    } else {
                        if (uCubeCallBacks != null) {
                            uCubeCallBacks.failureCallback(getFailJSON(Constants.PACKAGE_NAME_ERROR, Constants.PACKAGE_NAME_ERROR_CODE));
                            uCubeCallBacks = null;
                        }
                    }
                } else {
                    if (UCubeRequest != null) {
                        if (uCubeCallBacks != null) {
                            uCubeCallBacks.failureCallback(getFailJSON(UCubeRequest.getErroMsg(), 100));
                            uCubeCallBacks = null;
                        }
                    } else {
                        if (uCubeCallBacks != null) {
                            uCubeCallBacks.failureCallback(getFailJSON(Constants.REQUEST_ERROR, Constants.REQUEST_ERROR_CODE));
                            uCubeCallBacks = null;
                        }
                    }
                }
            } else {
                if (uCubeCallBacks != null) {
                    uCubeCallBacks.failureCallback(getFailJSON(Constants.CONTEXT_ERROR_MSG, Constants.CONTEXT_ERROR_CODE));
                    uCubeCallBacks = null;
                }
            }
        } else {
            if (uCubeCallBacks != null) {
                uCubeCallBacks.failureCallback(getFailJSON(Constants.KEY_ERROR, Constants.KEY_ERROR_CODE));
                uCubeCallBacks = null;
            }
        }

    }

    private void validePackage(final UCubeRequest UCubeRequest, final UCubeCallBacks uCubeCallBacks, final StatusCallBack statusCallBack, final boolean isStatus) {
        //TODO make a network call if success the move forward else give failure callback
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.setRequestcode(Constants.VALIDATE_REQUEST_CODE);
            requestParams.setImei(UCubeRequest.getImei());
            requestParams.setImsi(UCubeRequest.getImsi());
            requestParams.setUsername(UCubeRequest.getUsername());
            requestParams.setCompanyid(validateId);
            requestParams.setKey(ucubeKey);
            requestParams.setSrno(UCubeRequest.getTid());
            if (UCubeRequest.getSession_Id() != null && !UCubeRequest.getSession_Id().isEmpty()) {
                requestParams.setSessionId(UCubeRequest.getSession_Id());
            }
            NetworkController.getInstance().sendRequest(requestParams, new ResponseListener() {
                @Override
                public void onResponseSuccess(Response<ResponseParams> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            if (isStatus) {
                                checkTrasctionStatus(UCubeRequest, statusCallBack);
                            } else {
                                connectDevice(UCubeRequest, uCubeCallBacks);
                            }
                        } else {
                            if (isStatus) {
                                if (statusCallBack != null) {
                                    if (response.body().getMsg() != null && !response.body().getMsg().isEmpty()) {
                                        statusCallBack.failureCallback(getFailJSON(response.body().getMsg(), 100));
                                    } else {
                                        statusCallBack.failureCallback(getFailJSON(Constants.PACKAGE_INVALID, Constants.PACKAGE_INVALID_CODE));
                                    }
                                }
                            } else {
                                if (uCubeCallBacks != null) {
                                    if (response.body().getMsg() != null && !response.body().getMsg().isEmpty()) {
                                        uCubeCallBacks.failureCallback(getFailJSON(response.body().getMsg(), 100));
                                    } else {
                                        uCubeCallBacks.failureCallback(getFailJSON(Constants.PACKAGE_INVALID, Constants.PACKAGE_INVALID_CODE));
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onResponseFailure(Throwable throwable) {
                    if (uCubeCallBacks != null) {
                        throwable.printStackTrace();
                        uCubeCallBacks.failureCallback(getFailJSON(Constants.NETWORK_EXCEPTION, Constants.NETWORK_EXCEPTION_CODE));
                    }
                }
            });


        } catch (Exception e) {
            Log.d(TAG, "validePackage: " + e.toString());
            e.printStackTrace();
        }
    }

    private void checkTrasctionStatus(final UCubeRequest uCubeRequest, final StatusCallBack statusCallBack) {
        try {
            RequestParams requestParams = new RequestParams();
            requestParams.setRequestcode(Constants.TRANSACTION_STATUS);
            if (uCubeRequest.getRequestCode().getCode() == com.sil.ucubesdk.rpc.Constants.DEBIT) {
                requestParams.setOp(Constants.TRANSACTION_SALE);
            } else if (uCubeRequest.getRequestCode().getCode() == com.sil.ucubesdk.rpc.Constants.WITHDRAWAL) {
                requestParams.setOp(Constants.TRANSACTION_WITHDRAW);
            } else {
                statusCallBack.failureCallback(getFailJSON("Transaction Type Not found", Constants.TRANSACTION_ID_MISSING_CODE));
                return;
            }
            requestParams.setImei(uCubeRequest.getImei());
            requestParams.setImsi(uCubeRequest.getImsi());
            requestParams.setUsername(uCubeRequest.getUsername());
            requestParams.setAmt(uCubeRequest.getTxn_amount());
            requestParams.setTipamt("0");
            requestParams.setRemark(uCubeRequest.getRemark());
            requestParams.setMid(uCubeRequest.getMid());
            requestParams.setSrno(uCubeRequest.getTid());
            requestParams.setPassword(uCubeRequest.getPassword());
            requestParams.setCompanyid(validateId);
            requestParams.setKey(ucubeKey);
            if (uCubeRequest.getSession_Id() != null && !uCubeRequest.getSession_Id().isEmpty()) {
                requestParams.setSessionId(uCubeRequest.getSession_Id());
            }
            if (uCubeRequest.getTransactionId() == null || uCubeRequest.getTransactionId().isEmpty()) {
                statusCallBack.failureCallback(getFailJSON("Kindly provide the Transaction Id", Constants.TRANSACTION_ID_MISSING_CODE));
            } else {
                requestParams.setRrn(uCubeRequest.getTransactionId());
            }
            NetworkController.getInstance().sendRequest(requestParams, new ResponseListener() {
                @Override
                public void onResponseSuccess(Response<ResponseParams> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            try {
                                statusCallBack.successCallback(new JSONObject(new Gson().toJson(response.body())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                statusCallBack.failureCallback(getFailJSON(Constants.INITIATE_TRANSACTION_ERROR, Constants.UNKOWN_ERROR));
                            }
                        } else {
                            try {
                                statusCallBack.failureCallback(new JSONObject(new Gson().toJson(response.body())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                statusCallBack.failureCallback(getFailJSON(Constants.INITIATE_TRANSACTION_ERROR, Constants.UNKOWN_ERROR));
                            }
                        }
                    }
                }

                @Override
                public void onResponseFailure(Throwable throwable) {
                    if (statusCallBack != null) {
                        throwable.printStackTrace();
                        statusCallBack.failureCallback(getFailJSON(Constants.NETWORK_EXCEPTION, Constants.NETWORK_EXCEPTION_CODE));
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            statusCallBack.failureCallback(getFailJSON(Constants.INITIATE_TRANSACTION_ERROR, Constants.UNKOWN_ERROR));
        }
    }

    private void connectDevice(UCubeRequest UCubeRequest, UCubeCallBacks uCubeCallBacks) {
        this.saveSettings(UCubeRequest.getBt_address(), uCubeCallBacks);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.nfcEnabled = settings.getBoolean("NFC_enabled_device", false);
        LogManager.initialize(context);
        BluetoothConnexionManager.getInstance().initialize(settings, uCubeCallBacks);
        MDMManager.getInstance().initialize(context);
        RPCManager.getInstance().setConnexionManager(BluetoothConnexionManager.getInstance());
        BluetoothConnexionManager.getInstance().setDeviceAddr(UCubeRequest.getBt_address());
        startTransaction(UCubeRequest, uCubeCallBacks);
        //UIUtils.showProgress(this, "Check device model");
    }

    private void saveSettings(String selectedDevice, UCubeCallBacks uCubeCallBacks) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        if (selectedDevice != null) {
            editor.putString(Constants.BLUETOOTH_MAC_ADDRESS, selectedDevice);
            editor.putString(Constants.BLUETOOTH_DEVICE_NAME, BuildConfig.UCUBE_DEVICE_NAME);
        }

        editor.putString(Constants.MDM_SERVER_URL, BuildConfig.UCUBE_MDM_URL);
        editor.putBoolean(Constants.NFC_ENABLED_DEVICE, this.nfcEnabled);
        editor.putString(Constants.MDM_SERIAL_NUMBER, "");
        editor.putString(Constants.MDM_DEVICE_PART_NUMBER, "");
        editor.putBoolean(Constants.LOG_MANAGER_STATE, true);
        editor.apply();
        BluetoothConnexionManager.getInstance().initialize(settings, uCubeCallBacks);
    }

    private void startTransaction(UCubeRequest uCubeRequest, UCubeCallBacks uCubeCallBacks) {
        try {
            onlyICC = false;
            //swipType = 1:MS, 2:ICC, 3:Both
            startPayment("3", uCubeCallBacks);
        } catch (Exception e) {
            e.printStackTrace();
            if (uCubeCallBacks != null) {
                uCubeCallBacks.failureCallback(getFailJSON(Constants.INITIATE_TRANSACTION_ERROR, Constants.UNKOWN_ERROR));
                uCubeCallBacks = null;
            }
        }
    }

    public String getTransactionId() {
        return AndyUtility.getRRN((getISOField11(6)));
    }

    private void startPayment(String swipType, final UCubeCallBacks uCubeCallBacks) {
        //swipType = 1:MS, 2:ICC,3:Both

        double amount = -1;
        try {
            amount = Double.parseDouble(UCubeRequest.getTxn_amount());
        } catch (Exception e) {
            amount = -1;
        }

        Currency currency = new Currency(356, 2, "INR");
        TransactionType trxType = UCubeRequest.getRequestCode();

        final PaymentContext paymentContext = new PaymentContext();
        if (trxType.getCode() == com.sil.ucubesdk.rpc.Constants.INQUIRY) {
            try {
                amount = Double.parseDouble("0.0");
            } catch (Exception e) {
                amount = -1;
            }
        } else {
            paymentContext.setTransactionType(trxType.getCode());
        }

        paymentContext.setAmount("" + amount);
        paymentContext.setCurrency(currency);
        paymentContext.setPreferredLanguageList(Arrays.asList("en"));
        try {
            paymentContext.setTransactionDate(dateFormat.parse(""));
        } catch (Exception ignored) {
        }

        paymentContext.setMsgBundle(msgBundle);

        final List<CardReaderType> readerList = new ArrayList<>();
        if (swipType.equalsIgnoreCase("1")) {
            readerList.add(CardReaderType.MSR);
            displayMsg = "Swipe Card";
            onlyICC = false;
        } else if (swipType.equalsIgnoreCase("2")) {
            readerList.add(CardReaderType.ICC);
            displayMsg = "Insert Card";
            onlyICC = true;
        } else {
            readerList.add(CardReaderType.MSR);
            readerList.add(CardReaderType.ICC);
            displayMsg = "Insert/Swipe Card";
            onlyICC = false;
        }
        if (uCubeCallBacks != null) {
            uCubeCallBacks.progressCallback(displayMsg);
        }

        paymentContext.setRequestedSecuredTagList(new int[]{Constants.TAG_CARD_DATA_BLOCK});
        paymentContext.setRequestedPlainTagList(new int[]{
                Constants.TAG_MSR_BIN,
                Constants.TAG_5F34,
                Constants.TAG_9F09,
                Constants.TAG_9F1A,
                Constants.TAG_9F1E,
                Constants.TAG_9F35,
                Constants.TAG_9F37,
                Constants.TAG_9F22,
                Constants.TAG_8F,
                Constants.TAG_9F08,
                Constants.TAG_9F36,
                Constants.TAG_9F34,
                Constants.TAG_9F33,
                Constants.TAG_9F10,
                Constants.TAG_5F2A,
                Constants.TAG_95,
                Constants.TAG_9F27,
                Constants.TAG_9A,
                Constants.TAG_9F26,
                Constants.TAG_9F41,
                Constants.TAG_9F02,
                Constants.TAG_9F03,
                Constants.TAG_82,
                Constants.TAG_84,
                Constants.TAG_91,
                Constants.TAG_9C,
                Constants.TAG_9B,
                Constants.TAG_DF03,
                Constants.TAG_9F06}); // add by shankar on 22-02-2017

        paymentContext.setForceOnlinePIN(false);                                                     //pin required

        List<byte[]> tagList = new ArrayList<>();
        tagList.add(new byte[]{(byte) 0x95}); /* TVR */
        tagList.add(new byte[]{(byte) 0x9B}); /* TSI */

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] activatedReaders = new byte[readerList.size()];
                for (int i = 0; i < activatedReaders.length; i++) {
                    activatedReaders[i] = readerList.get(i).getCode();
                }
                svc = new PaymentService(context, paymentContext, uCubeCallBacks, activatedReaders, displayMsg);
                svc.setCardWaitTimeout(Integer.valueOf("60"));                              //cardwait
                svc.setRiskManagementTask(new RiskManagementTask((Activity) context));

                RequestParams r = new RequestParams();
                if (UCubeRequest.getRequestCode().getCode() == com.sil.ucubesdk.rpc.Constants.DEBIT) {
                    r.setRequestcode(Constants.SDK_SALE);
                } else if (UCubeRequest.getRequestCode().getCode() == com.sil.ucubesdk.rpc.Constants.WITHDRAWAL) {
                    r.setRequestcode(Constants.SDK_WITHDRAW);
                } else if (UCubeRequest.getRequestCode().getCode() == com.sil.ucubesdk.rpc.Constants.INQUIRY) {
                    r.setRequestcode(Constants.SDK_ENQUIRY);
                }
                r.setImei(UCubeRequest.getImei());
                r.setImsi(UCubeRequest.getImsi());
                r.setUsername(UCubeRequest.getUsername());
                r.setAmt(UCubeRequest.getTxn_amount());
                r.setTipamt("0");
                r.setRemark(UCubeRequest.getRemark());
                r.setMid(UCubeRequest.getMid());
                r.setSrno(UCubeRequest.getTid());
                r.setPassword(UCubeRequest.getPassword());
                r.setCompanyid(validateId);
                r.setKey(ucubeKey);
                if (UCubeRequest.getSession_Id() != null && !UCubeRequest.getSession_Id().isEmpty()) {
                    r.setSessionId(UCubeRequest.getSession_Id());
                }
                if (UCubeRequest.getTransactionId() != null && !UCubeRequest.getTransactionId().isEmpty()) {
                    r.setRrn(UCubeRequest.getTransactionId());
                }
                if (onlyICC) {
                    try {
                        Log.d(TAG, "handleEvent:  BEFORE");
                        Thread.sleep(3000);
                        Log.d(TAG, "handleEvent:  AFTER");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                svc.setAuthorizationProcessor(new MyPaymentAuth((Activity) context, svc, r));  // here pass req param
                svc.execute(new ITaskMonitor() {
                    @Override
                    public void handleEvent(final TaskEvent event, final Object... params) {
                        final AbstractPaymentService svc = (AbstractPaymentService) params[0];
                        switch (event) {
                            case PROGRESS:
                                switch (paymentContext.getPaymentStatus()) {
                                    case STARTED:
                                        if (uCubeCallBacks != null) {
                                            uCubeCallBacks.progressCallback(String.format("Make payment of %.2f %s", paymentContext.getAmount(), paymentContext.getCurrency().getLabel()));
                                        }
                                        break;
                                }
                                return;

                            case SUCCESS:
                                switch (paymentContext.getPaymentStatus()) {
                                    case APPROVED:
                                        //  setResult(true, "APPROVED", MyConst.getResponsejson(), uCubeCallBacks);
                                        setResult(true, "APPROVED", 00, MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;

                                    case DECLINED:
                                        // setResult(false, "DECLINED", MyConst.getResponsejson(), uCubeCallBacks);
                                        setResult(false, "DECLINED", 100, MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;

                                    case CHIP_REQUIRED:
                                        startPayment("2", uCubeCallBacks);
                                        break;

                                    case CANCELLED:
                                        setResult(false, "TRANSACTION CANCELLED", 104, "Transaction has been cancelled from device.", uCubeCallBacks);
                                        break;

                                    case SWIPE_CARD:
                                        startPayment("1", uCubeCallBacks);
                                        break;

                                    case UNSUPPORTED_CARD:
                                        startPayment("1", uCubeCallBacks);
                                        break;

                                    case CARD_WAIT_FAILED:
                                        setResult(false, "CARD WAIT FAILED", 103, "Connection timed out.", uCubeCallBacks);
                                        break;

                                    case ERROR:
                                        setResult(false, "Error", MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;

                                    case CONN_TIME_OUT:
                                        setResult(false, "TIMED OUT", 105, MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;

                                    case REFUSED_CARD:
                                        setResult(false, "REFUSED CARD", 106, MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;
                                    case REVERSAL:
                                        setResult(false, "REVERSAL", 107, MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;
                                    case TAG_BATTERY_STATE:
                                        setResult(false, "TAG_BATTERY_STATE", 110, MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;
                                    case TAG_POWER_OFF_TIMEOUT:
                                        setResult(false, "TAG_POWER_OFF_TIMEOUT", 111, MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;

                                    default:
                                        setResult(false, "ERROR", MyConst.getJSONResponse(), uCubeCallBacks);
                                        break;
                                }
                                break;

                            case FAILED:
                                if (MyConst.getJSONResponse() != null && !MyConst.getJSONResponse().toString().isEmpty()) {
                                    setResult(MyConst.getJSONResponse(), uCubeCallBacks);
                                } else {
                                    setResult(false, "CONNECTION ERROR", 101, "Device Disconnected, Please try again", uCubeCallBacks);
                                    setRPCManager();
                                }
                                // BluetoothConnexionManager.getInstance().disconnect();
                                //startPayment("1");
                                break;

                            default:
                                return;
                        }
                    }
                });
            }
        }).start();
    }

    private void setResult(boolean status, String message, int responseCode, String responseJson, UCubeCallBacks uCubeCallBacks) {
        try {
            if (uCubeCallBacks != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Msg", message);
                jsonObject.put("ResponseCode", responseCode);
                jsonObject.put("Response", responseJson);
                if (status) {
                    uCubeCallBacks.successCallback(jsonObject);
                    uCubeCallBacks = null;
                } else {
                    uCubeCallBacks.failureCallback(jsonObject);
                    uCubeCallBacks = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResult(boolean status, String message, JSONObject responseJson, UCubeCallBacks uCubeCallBacks) {
        try {
            if (uCubeCallBacks != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Msg", message);
                jsonObject.put("Response", responseJson);
                if (status) {
                    uCubeCallBacks.successCallback(jsonObject);
                    uCubeCallBacks = null;
                } else {
                    uCubeCallBacks.failureCallback(jsonObject);
                    uCubeCallBacks = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResult(boolean status, String message, int responseCode, JSONObject responseJson, UCubeCallBacks uCubeCallBacks) {
        try {
            if (uCubeCallBacks != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Msg", message);
                jsonObject.put("ResponseCode", responseCode);
                jsonObject.put("Response", responseJson);
                if (status) {
                    uCubeCallBacks.successCallback(jsonObject);
                    uCubeCallBacks = null;
                } else {
                    uCubeCallBacks.failureCallback(jsonObject);
                    uCubeCallBacks = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setResult(JSONObject responseJson, UCubeCallBacks uCubeCallBacks) {
        try {
            uCubeCallBacks.failureCallback(responseJson);
            uCubeCallBacks = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getFailJSON(String msg, int responseCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Msg", "Failed");
            jsonObject.put("ResponseCode", responseCode);
            jsonObject.put("Response", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void checkStatus(UCubeRequest UCubeRequest, StatusCallBack statusCallBack) {
        if (ucubeKey != null && !ucubeKey.isEmpty() && !ucubeKey.trim().isEmpty()) {
            if (context != null) {
                if (UCubeRequest != null && UCubeRequest.isValidRequest()) {
                    this.UCubeRequest = UCubeRequest;
                    try {
                        this.msgBundle = new PropertyResourceBundle(context.getResources().openRawResource(R.raw.ucube_strings));
                    } catch (Exception var6) {
                        Log.d("ERROR", "Unable to load uCube message bundle", var6);
                    }
                    validateId = context.getPackageName();
                    if (validateId != null) {
                        validePackage(UCubeRequest, null, statusCallBack, true);
                    } else {
                        if (statusCallBack != null) {
                            statusCallBack.failureCallback(getFailJSON(Constants.PACKAGE_NAME_ERROR, Constants.PACKAGE_NAME_ERROR_CODE));
                            statusCallBack = null;
                        }
                    }
                } else {
                    if (UCubeRequest != null) {
                        if (statusCallBack != null) {
                            statusCallBack.failureCallback(getFailJSON(UCubeRequest.getErroMsg(), 100));
                            statusCallBack = null;
                        }
                    } else {
                        if (statusCallBack != null) {
                            statusCallBack.failureCallback(getFailJSON(Constants.REQUEST_ERROR, Constants.REQUEST_ERROR_CODE));
                            statusCallBack = null;
                        }
                    }
                }
            } else {
                if (statusCallBack != null) {
                    statusCallBack.failureCallback(getFailJSON(Constants.CONTEXT_ERROR_MSG, Constants.CONTEXT_ERROR_CODE));
                    statusCallBack = null;
                }
            }
        } else {
            if (statusCallBack != null) {
                statusCallBack.failureCallback(getFailJSON(Constants.KEY_ERROR, Constants.KEY_ERROR_CODE));
                statusCallBack = null;
            }
        }

    }

    private void validateVoidPackage(final UCubeRequest UCubeRequest, final UCubeVoidCallBacks uCubeVoidCallBacks) {

        try {
            RequestParams requestParams = new RequestParams();
            requestParams.setRequestcode(Constants.VALIDATE_REQUEST_CODE);
            requestParams.setImei(UCubeRequest.getImei());
            requestParams.setImsi(UCubeRequest.getImsi());
            requestParams.setUsername(UCubeRequest.getUsername());
            requestParams.setCompanyid(validateId);
            requestParams.setKey(ucubeKey);
            requestParams.setSrno(UCubeRequest.getTid());
            if (UCubeRequest.getSession_Id() != null && !UCubeRequest.getSession_Id().isEmpty()) {
                requestParams.setSessionId(UCubeRequest.getSession_Id());
            }
            NetworkController.getInstance().sendRequest(requestParams, new ResponseListener() {
                @Override
                public void onResponseSuccess(Response<ResponseParams> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            callVoidTransaction(UCubeRequest, uCubeVoidCallBacks);
                        } else {

                            if (uCubeVoidCallBacks != null) {
                                if (response.body().getMsg() != null && !response.body().getMsg().isEmpty()) {
                                    uCubeVoidCallBacks.failureCallback(getFailJSON(response.body().getMsg(), 100));
                                } else {
                                    uCubeVoidCallBacks.failureCallback(getFailJSON(Constants.PACKAGE_INVALID, Constants.PACKAGE_INVALID_CODE));
                                }
                            }
                        }
                    }
                }

                @Override
                public void onResponseFailure(Throwable throwable) {
                    if (uCubeVoidCallBacks != null) {
                        throwable.printStackTrace();
                        uCubeVoidCallBacks.failureCallback(getFailJSON(Constants.NETWORK_EXCEPTION, Constants.NETWORK_EXCEPTION_CODE));
                    }
                }
            });


        } catch (Exception e) {
            Log.d(TAG, "validePackage: " + e.toString());
            e.printStackTrace();
        }

    }

    private void callVoidTransaction(final UCubeRequest uCubeRequest, final UCubeVoidCallBacks uCubeVoidCallBacks) {

        try {
            RequestParams requestParams = new RequestParams();
            requestParams.setRequestcode("voidSdk");
            requestParams.setUsername(uCubeRequest.getUsername());
            requestParams.setAmt(uCubeRequest.getTxn_amount());
            requestParams.setImei(uCubeRequest.getImei());
            requestParams.setImsi(uCubeRequest.getImsi());
            requestParams.setMid(uCubeRequest.getMid());
            requestParams.setSessionId(uCubeRequest.getSession_Id());
//            requestParams.setSessionId(sha256("" + uCubeRequest.getUsername() + ":" + uCubeRequest.getTransactionId() + ":" + uCubeRequest.getTxn_amount() + ":" + ucubeKey));
            requestParams.setSrno(uCubeRequest.getTid());   //02444411523.
            requestParams.setRrn(uCubeRequest.getTransactionId());
            requestParams.setCompanyid(this.validateId);
            requestParams.setKey(ucubeKey);

            NetworkController.getInstance().sendRequest(requestParams, new ResponseListener() {
                @Override
                public void onResponseSuccess(Response<ResponseParams> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus()) {
                            try {
                                uCubeVoidCallBacks.successCallback(new JSONObject(new Gson().toJson(response.body())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                uCubeVoidCallBacks.failureCallback(getFailJSON(Constants.INITIATE_TRANSACTION_ERROR, Constants.UNKOWN_ERROR));
                            }
                        } else {
                            try {
                                uCubeVoidCallBacks.failureCallback(new JSONObject(new Gson().toJson(response.body())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                uCubeVoidCallBacks.failureCallback(getFailJSON(Constants.INITIATE_TRANSACTION_ERROR, Constants.UNKOWN_ERROR));
                            }
                        }
                    }
                }

                @Override
                public void onResponseFailure(Throwable throwable) {
                    if (uCubeVoidCallBacks != null) {
                        throwable.printStackTrace();
                        uCubeVoidCallBacks.failureCallback(getFailJSON(Constants.NETWORK_EXCEPTION, Constants.NETWORK_EXCEPTION_CODE));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            uCubeVoidCallBacks.failureCallback(getFailJSON(Constants.INITIATE_TRANSACTION_ERROR, Constants.UNKOWN_ERROR));
        }

    }

    public void voidTransaction(UCubeRequest uCubeRequest, UCubeVoidCallBacks uCubeCallBacks) {
        if (ucubeKey != null && !ucubeKey.isEmpty() && !ucubeKey.trim().isEmpty()) {
            if (context != null) {
                if (uCubeRequest != null && uCubeRequest.isValidRequest()) {
                    try {
                        this.msgBundle = new PropertyResourceBundle(context.getResources().openRawResource(R.raw.ucube_strings));
                    } catch (Exception var6) {
                        LogManager.debug("ERROR", "Unable to load uCube message bundle", var6);
                    }
                    validateId = context.getPackageName();
                    if (validateId != null) {
                        validateVoidPackage(uCubeRequest, uCubeCallBacks);
                    } else {
                        if (uCubeCallBacks != null) {
                            uCubeCallBacks.failureCallback(getFailJSON(Constants.PACKAGE_NAME_ERROR, Constants.PACKAGE_NAME_ERROR_CODE));
                            uCubeCallBacks = null;
                        }
                    }
                } else {
                    if (uCubeRequest != null) {
                        if (uCubeCallBacks != null) {
                            uCubeCallBacks.failureCallback(getFailJSON(uCubeRequest.getErroMsg(), 100));
                            uCubeCallBacks = null;
                        }
                    } else {
                        if (uCubeCallBacks != null) {
                            uCubeCallBacks.failureCallback(getFailJSON(Constants.REQUEST_ERROR, Constants.REQUEST_ERROR_CODE));
                            uCubeCallBacks = null;
                        }
                    }
                }
            } else {
                if (uCubeCallBacks != null) {
                    uCubeCallBacks.failureCallback(getFailJSON(Constants.CONTEXT_ERROR_MSG, Constants.CONTEXT_ERROR_CODE));
                    uCubeCallBacks = null;
                }
            }
        } else {
            if (uCubeCallBacks != null) {
                uCubeCallBacks.failureCallback(getFailJSON(Constants.KEY_ERROR, Constants.KEY_ERROR_CODE));
                uCubeCallBacks = null;
            }
        }
    }


    private String sha256(String mainStr) {
        String s = "";

        try {
            /*Hashing.sha256().hashString("AVI:036414257544:3.00:U5DEA7909FAE37F23463B6C65E387A1CD",
                    StandardCharsets.UTF_8).toString();*/

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    mainStr.getBytes(StandardCharsets.UTF_8));

            s = bytesToHex(encodedhash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

package com.sil.ucubesdk.payment;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.sil.ucubesdk.AndyUtility;
import com.sil.ucubesdk.ITaskMonitor;
import com.sil.ucubesdk.MyBERTLV;
import com.sil.ucubesdk.TaskEvent;
import com.sil.ucubesdk.Tools;
import com.sil.ucubesdk.UCubeCallBacks;
import com.sil.ucubesdk.rest.NetworkController;
import com.sil.ucubesdk.rest.RequestParams;
import com.sil.ucubesdk.rest.ResponseListener;
import com.sil.ucubesdk.rest.ResponseParams;
import com.sil.ucubesdk.rpc.Constants;
import com.sil.ucubesdk.rpc.MyConst;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import retrofit2.Response;

import static com.sil.ucubesdk.AndyUtility.getISOField11;


/**
 * Created by shankar.savant on 25-01-2017.
 */

public class MyPaymentAuth implements IAuthorizationTask {

    String tag55 = "", trac2data = "", reader = "";
    String pindata = "";
    Activity activity;
    byte[] authResponse;
    ITaskMonitor monitor;
    PaymentContext paymentContext;
    PaymentService svc;
    RequestParams r;
    int attempt_cnt = 0;

    public MyPaymentAuth(Activity activity, PaymentService svc, RequestParams re) {
        this.activity = activity;
        this.svc = svc;
        this.r = re;
        attempt_cnt = 0;
    }



    @Override
    public byte[] getAuthorizationResponse() {
        return authResponse;
    }

    @Override
    public PaymentContext getContext() {
        return paymentContext;
    }

    @Override
    public void setContext(PaymentContext context) {
        this.paymentContext = context;
    }

    public void execute(ITaskMonitor monitor) {
        this.monitor = monitor;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reader = "" + svc.getContext().getActivatedReader();
                pindata = Tools.bytesToHex(svc.getContext().getOnlinePinBlock());
                Log.d("CallWS", "MyPayAuth");
                Log.d("READER", "" + reader);
                CallWS();
            }
        });
    }

    public void CallWS() {
        Log.d("CallWS", "In Call WS");
        trac2data = MyConst.getTag5025();
        Log.d("tag 5025 length", "" + trac2data.length());
        String tag5512 = "";
        tag5512 = "" + MyConst.getTag5512();
        Log.d("TAG 5512 val", "" + tag5512);
        Log.d("FinallyTrack2 >>", "" + trac2data.substring(12, 60));
        if (reader.equalsIgnoreCase("17")) {
            tag55 = "";
            Map<Integer, byte[]> m = paymentContext.getPlainTagTLV();
            concate("9B", Tools.bytesToHex(m.get(Constants.TAG_9B)));
            Log.d("Monali 9B", "" + Tools.bytesToHex(m.get(Constants.TAG_9B)));
            concate("5F34", "" + Tools.bytesToHex(m.get(Constants.TAG_5F34)));
            concate("9F09", Tools.bytesToHex(m.get(Constants.TAG_9F09)));
            concate("9F1A", Tools.bytesToHex(m.get(Constants.TAG_9F1A)));
            concate("9F1E", Tools.bytesToHex(m.get(Constants.TAG_9F1E)));
            concate("9F35", Tools.bytesToHex(m.get(Constants.TAG_9F35)));
            concate("9F37", Tools.bytesToHex(m.get(Constants.TAG_9F37)));
            //concate("84", "" + Tools.bytesToHex(paymentContext.getTvr()));
            //concate("84", "" + MyConst.getSelected_app_id());
            concate("84", "" + Tools.bytesToHex(paymentContext.getSelectedApplication().getAid()));   ///AID
            concate("9F36", "" + Tools.bytesToHex(m.get(Constants.TAG_9F36)));
            concate("9F34", "" + Tools.bytesToHex(m.get(Constants.TAG_9F34)));
            concate("9F33", "" + Tools.bytesToHex(m.get(Constants.TAG_9F33)));
            concate("9F10", "" + Tools.bytesToHex(m.get(Constants.TAG_9F10)));
            concate("5F2A", "" + Tools.bytesToHex(m.get(Constants.TAG_5F2A)));
            concate("95", "" + Tools.bytesToHex(m.get(Constants.TAG_95)));
            Log.d("Monali TAG_95", "" + Tools.bytesToHex(m.get(Constants.TAG_95)));
            concate("9F27", "" + Tools.bytesToHex(m.get(Constants.TAG_9F27)));
            concate("9A", "" + Tools.bytesToHex(m.get(Constants.TAG_9A)));
            concate("9F26", "" + Tools.bytesToHex(m.get(Constants.TAG_9F26)));
            concate("9F41", "" + Tools.bytesToHex(m.get(Constants.TAG_9F41)));
            concate("9F02", "" + Tools.bytesToHex(m.get(Constants.TAG_9F02)));
            ///AID
            //concate("9F06", "" + MyConst.getSelected_app_id());
            //Monali
            Log.d("9F03", "value: " + Tools.bytesToHex(m.get(Constants.TAG_9F03)));
            if (Tools.bytesToHex(m.get(Constants.TAG_9F03)) == null ||
                    Tools.bytesToHex(m.get(Constants.TAG_9F03)).equals("")) {
                concate("9F03", "" + "000000000000");
            } else {
                concate("9F03", "" + Tools.bytesToHex(m.get(Constants.TAG_9F03)));
            }
            concate("9F06", "" + Tools.bytesToHex(paymentContext.getSelectedApplication().getAid()));   ///AID
            Log.d("Monali 9F06", "" + Tools.bytesToHex(m.get(Constants.TAG_9F06)));
            concate("82", "" + Tools.bytesToHex(m.get(Constants.TAG_82)));
            concate("9C", "" + Tools.bytesToHex(m.get(Constants.TAG_9C)));
            Log.d("TAG5512", "" + tag5512);

            MyConst.m = m;

            if (tag5512.contains("DF3E")) {
                TreeMap<String, String> tlvMap = MyBERTLV.parseTLV("" + tag5512.substring(tag5512.toString().indexOf("DF3E"), tag5512.toString().indexOf("DF3E") + 48));
                pindata = tlvMap.get("DF3E") + "" + tlvMap.get("DF3F");
            } else {
                pindata = "";
            }
            Log.d("EMVDATA : ", "" + tag55);

            if (Tools.bytesToHex(m.get(Constants.TAG_9F27)).equalsIgnoreCase("00")) {
                Log.d("STIVEE", "Declined By 9F27" + Tools.bytesToHex(m.get(Constants.TAG_9F27)));
                end(2);
                MyConst.setStatus("false");
                MyConst.setMsg("Declined by terminal 9F27 00");
                MyConst.setArpc("FAIL");
                paymentContext.setPaymentStatus(PaymentState.DECLINED_BY_9F27);
            } else {
                Log.d("STIVEE", "Accepted By 9F27" + Tools.bytesToHex(m.get(Constants.TAG_9F27)));
                Log.d("FinallyTag5512 >>", "" + tag5512);
                Log.d("Activated reader", "" + reader);
                Log.d("5512>>", "" + paymentContext.getSecuredTagBlock());
                Log.d("PINDATA : ", pindata);
                callPaymentWS();
            }

        } else {
            //MS UCubeRequest
            callPaymentWS();
        }
    }

    private void callPaymentWS() {

      /*  paymentContext.setPaymentStatus(PaymentState.APPROVED);
        end(0);*/

        r.setCarddata("" + trac2data.substring(12, 60));
        r.setKsn("" + Tools.bytesToHex(svc.getContext().getKsn()));
        r.setPindata("" + pindata);
        r.setReader("" + reader);
        r.setTag55("" + tag55);
        if (r.getRrn() == null || r.getRrn().isEmpty() || r.getRrn().trim().isEmpty()) {
            Log.d("To connectDevice", "RRN is Empty so created new");
            r.setRrn(AndyUtility.getRRN((getISOField11(6))));
        }
        MyConst.setTag55("" + tag55);


        NetworkController.getInstance().sendRequest(r, new ResponseListener() {
            @Override
            public void onResponseSuccess(Response<ResponseParams> response) {
                if (response.body() != null) {
                    Log.d("Status Msg", "" + response.body().getMsg() + " Status : " + response.body().getStatus());
                    Log.d("Response From server", "" + new Gson().toJson(response.body()));
                    MyConst.setResponsejson(new Gson().toJson(response.body()));
                    try {
                        MyConst.setJSONResponse(new JSONObject(new Gson().toJson(response.body())));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //MyConst.setJson_response(respStr);
                    //SessionConst.appendLog(activity, "response from server : " + new Gson().toJson(response));
                    //MyConst.appendLog("\n" + "response from server : " + new Gson().toJson(response));
                    // Log.d("response from server", new Gson().toJson(response));
                    if (response.body().getStatus() == true) {
                        paymentContext.setPaymentStatus(PaymentState.APPROVED);
                        String respCode = response.body().getHitachiResCode();
                        if (respCode != null) {
                            Log.d("Response code server", respCode);
                            if (respCode == null || respCode.equals("")) {
                                end(2);
                                MyConst.setStatus("false");
                            } else if (respCode.equals("00")) {
                                end(0);
                                MyConst.setStatus("true");
                            } else if (respCode.equals("05")) {
                                end(1);
                                MyConst.setStatus("false");
                            } else if (respCode.equals("91")) {
                                end(3);
                                MyConst.setStatus("false");
                            } else {
                                end(2);
                                MyConst.setStatus("false");
                            }
                        } else {
                            end(2);
                            MyConst.setStatus("false");
                        }

                    } else {
                        MyConst.isServiceCalled = true;
                        MyConst.setCard_no(response.body().getCardno());
                        MyConst.setDate(response.body().getDate());
                        MyConst.setMsg(response.body().getMsg());
                        MyConst.setRrn(response.body().getRrn());
                        MyConst.setServerRespCode(response.body().getHitachiResCode());
                        MyConst.setArpc("FAIL");
                        MyConst.setMsg(response.body().getMsg());
                        MyConst.setInvoice_no(response.body().getInvoiceNumber());
                        MyConst.setCard_type(response.body().getCardType());
                        MyConst.setBatch_no(response.body().getBatchNo());
                        MyConst.setTvr(response.body().getTvr());
                        MyConst.setTsi(response.body().getTsi());
                        MyConst.setAid(response.body().getAid());
                        MyConst.setAppl_name(response.body().getApplName());

                        String respCode = response.body().getHitachiResCode();
                        if (respCode != null) {
                            Log.d("Response code server", respCode);
                            if (respCode == null || respCode.equals("")) {
                                end(2);
                                MyConst.setStatus("false");
                                paymentContext.setPaymentStatus(PaymentState.DECLINED);
                            } else if (respCode.equals("00")) {
                                end(0);
                                MyConst.setStatus("true");
                            } else if (respCode.equals("05")) {
                                end(1);
                                MyConst.setStatus("false");
                                paymentContext.setPaymentStatus(PaymentState.DECLINED);
                            } else if (respCode.equals("91")) {
                                end(3);
                                MyConst.setStatus("false");
                                paymentContext.setPaymentStatus(PaymentState.DECLINED);
                            } else if (respCode.equals("101")) {
                                end(2);
                                MyConst.setStatus("false");
                                paymentContext.setPaymentStatus(PaymentState.CONN_TIME_OUT);
                            } else {
                                end(2);
                                MyConst.setStatus("false");
                                paymentContext.setPaymentStatus(PaymentState.DECLINED);
                            }
                        } else {
                            end(2);
                            MyConst.setStatus("false");
                        }
                    }
                } else {
                    //MyConst.setJson_response(null);
                    MyConst.setArpc("FAIL");
                    end(2);
                    MyConst.setStatus("false");
                    MyConst.setMsg("unable to call service");
                    paymentContext.setPaymentStatus(PaymentState.CONN_TIME_OUT);
                }
            }

            @Override
            public void onResponseFailure(Throwable t) {
                //MyConst.setJson_response(null);
                if (attempt_cnt < 2) {
                    attempt_cnt++;
                    callPaymentWS();
                } else {
                    Log.e("serviceFail", t.toString());
                    end(2);
                    MyConst.setStatus("false");
                    MyConst.setMsg("unable to call service");
                    paymentContext.setPaymentStatus(PaymentState.CONN_TIME_OUT);
                }
            }
        });

    }

    public void end(int choice) {
        if (paymentContext.getActivatedReader() == Constants.NFC_READER) {
            switch (choice) {
                case 0:
                    authResponse = new byte[]{0x30, 0x30};
                    break;

                case 1:
                    authResponse = new byte[]{0x35, 0x31};
                    break;

                case 2:
                    authResponse = new byte[]{0x50, 0x50};
                    break;
            }

        } else {
            switch (choice) {
                case 0:
                    authResponse = new byte[]{(byte) 0x8A, 0x02, 0x30, 0x30};
                    break;

                case 1:
                    authResponse = new byte[]{(byte) 0x8A, 0x02, 0x30, 0x35};
                    break;

                case 2:
                    authResponse = new byte[]{(byte) 0x8A, 0x02, 0x39, 0x38};
                    break;

                case 3:
                    authResponse = new byte[]{(byte) 0x8A, 0x02, 0x39, 0x31};
                    break;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                monitor.handleEvent(TaskEvent.SUCCESS);
            }
        }).start();
    }

    void concate(String TagNo, String s) {
        try {
            tag55 += TagNo + PaddString(Integer.toHexString(s.length() / 2), "0", 2, true) + s;
            Log.d("Field 55: ", "" + tag55);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String PaddString(String s, String c, int length, Boolean left) {
        // TODO Auto-generated method stub
        String result = s;
        int padd = length - s.length();
        for (int i = 0; i < padd; i++) {
            if (left)
                result = c + result;
            else
                result = result + c;
        }
        return result;
    }
}

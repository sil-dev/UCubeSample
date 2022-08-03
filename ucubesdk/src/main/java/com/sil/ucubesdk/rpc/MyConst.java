package com.sil.ucubesdk.rpc;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by shankar.savant on 31-01-2017.
 */
public class MyConst {
    public static String tvr;
    public static String tsi;
    public static String appname;
    public static String aid;
    public static String tag5025;
    public static String tag5512;
    public static String tag5513;
    public static String tag55;
    public static String tagDF03;
    public static String rrn;
    public static String status;
    public static String printmsg;
    public static String smsreceipt;
    public static String remark;
    public static String msg;
    public static String arpc;
    public static Bitmap receipt;
    public static Bitmap custReceipt;
    public static Bitmap retailReceipt;
    public static Bitmap duplReceipt;
    public static int actioncode;
    public static String tag_5513_getCVM;
    public static String log;
    public static String device_sr_no ="";
    public static Map<Integer, byte[]> m = null;
    public static String card_no;
    public static String auth_id;
    private static short responseStatus = 0;
    private static String serverRespCode = "";
    private static String appl_name ="";
    private static String invoice_no ="";
    private static String card_type ="";
    private static String batch_no ="";
    public static String sessionToken = "";
    public static String respcode;
    public static String balance;
    public static String responsejson;
    public static JSONObject JSONResponse;
    //Monali
    public static boolean isFallBack = false;
    public static boolean isServiceCalled = false;
    public static boolean isCardRemoved = false;
    public  static boolean isWithoutPin = false;

    public static Map<Integer, byte[]> getM() {
        return m;
    }

    public static void setM(Map<Integer, byte[]> m) {
        MyConst.m = m;
    }

    public static int getActioncode() {
        return actioncode;
    }

    public static void setActioncode(int actioncode) {
        MyConst.actioncode = actioncode;
    }

    public static String getCust_VPA() {
        return cust_VPA;
    }

    public static void setCust_VPA(String cust_VPA) {
        MyConst.cust_VPA = cust_VPA;
    }

    private static String cust_VPA ="";

    public static String getCard_type() {
        return card_type;
    }

    public static void setCard_type(String card_type) {
        MyConst.card_type = card_type;
    }

    public static String getInvoice_no() {
        return invoice_no;
    }

    public static void setInvoice_no(String invoice_no) {
        MyConst.invoice_no = invoice_no;
    }

    public static String getAppl_name() {
        return appl_name;
    }

    public static void setAppl_name(String appl_name) {
        MyConst.appl_name = appl_name;
    }

    public static String getServerRespCode() {
        return serverRespCode;
    }

    public static String getTagDF03() {
        return tagDF03;
    }

    public static void setTagDF03(String tagDF03) {
        MyConst.tagDF03 = tagDF03;
    }


    public static void setServerRespCode(String serverRespCode) {
        MyConst.serverRespCode = serverRespCode;
    }

    //Monali
    public static String fallback_tag5025;

    public static String getFallback_tag5025() {
        return fallback_tag5025;
    }

    public static void setFallback_tag5025(String fallback_tag5025) {
        MyConst.fallback_tag5025 = fallback_tag5025;
    }

    public static int getTxnattempt() {
        return txnattempt;
    }

    public static void setTxnattempt(int txnattempt) {
        MyConst.txnattempt = txnattempt;
    }

    private static int txnattempt;

    public static short getResponseStatus() {
        return responseStatus;
    }

    public static void setResponseStatus(short responseStatus) {
        MyConst.responseStatus = responseStatus;
    }

    public static String getAuth_id() {
        return auth_id;
    }

    public static void setAuth_id(String auth_id) {
        MyConst.auth_id = auth_id;
    }

    public static String date;

    public static String getDate() {        return date;
    }

    public static void setDate(String date) {
        MyConst.date = date;
    }

    public static String getCard_no() {
        return card_no;
    }

    public static void setCard_no(String card_no) {
        MyConst.card_no = card_no;
    }

    public static Bitmap getCustReceipt() {
        return custReceipt;
    }

    public static void setCustReceipt(Bitmap custReceipt) {
        MyConst.custReceipt = custReceipt;
    }

    public static Bitmap getRetailReceipt() {
        return retailReceipt;
    }

    public static void setRetailReceipt(Bitmap retailReceipt) {
        MyConst.retailReceipt = retailReceipt;
    }

    public static Bitmap getDuplReceipt() {
        return duplReceipt;
    }

    public static void setDuplReceipt(Bitmap duplReceipt) {
        MyConst.duplReceipt = duplReceipt;
    }

    public static Bitmap getReceipt() {
        return receipt;
    }

    public static void setReceipt(Bitmap receipt) {
        MyConst.receipt = receipt;
    }

    public static String getTag5025() {
        return tag5025;
    }

    public static void setTag5025(String tag5025) {
        MyConst.tag5025 = tag5025;
    }

    public static String getTag5512() {
        return tag5512;
    }

    public static void setTag5512(String tag5512) {
        MyConst.tag5512 = tag5512;
    }

    public static String getTag5513() {
        return tag5513;
    }

    public static void setTag5513(String tag5513) {
        MyConst.tag5513 = tag5513;
    }

    public static String getRrn() {
        return rrn;
    }

    public static void setRrn(String rrn) {
        MyConst.rrn = rrn;
    }

    public static String getStatus() {
        return status;
    }

    public static void setStatus(String status) {
        MyConst.status = status;
    }

    public static String getPrintmsg() {
        return printmsg;
    }

    public static void setPrintmsg(String printmsg) {
        MyConst.printmsg = printmsg;
    }

    public static String getRemark() {
        return remark;
    }

    public static void setRemark(String remark) {
        MyConst.remark = remark;
    }

    public static String getMsg() {
        return msg;
    }

    public static void setMsg(String msg) {
        MyConst.msg = msg;
    }

    public static String getSmsreceipt() {
        return smsreceipt;
    }

    public static void setSmsreceipt(String smsreceipt) {
        MyConst.smsreceipt = smsreceipt;
    }

    public static String getArpc() {
        return arpc;
    }

    public static void setArpc(String arpc) {
        MyConst.arpc = arpc;
    }

    public static String getTag55() {
        return tag55;
    }

    public static void setTag55(String tag55) {
        MyConst.tag55 = tag55;
    }

    public static String getLog() {
        return log;
    }

    public static void setLog(String log) {
        MyConst.log = log;
    }

    public static void appendLog(String lg){
        MyConst.log+=lg;
    }

    public static String getDevice_sr_no() {
        return device_sr_no;
    }

    public static void setDevice_sr_no(String device_sr_no) {
        MyConst.device_sr_no = device_sr_no;
    }

    public static String getBatch_no() {
        return batch_no;
    }

    public static void setBatch_no(String batch_no) {
        MyConst.batch_no = batch_no;
    }

    public static String getRespcode() {
        return respcode;
    }

    public static void setRespcode(String respcode) {
        MyConst.respcode = respcode;
    }

    public static String getBalance() {
        return balance;
    }

    public static void setBalance(String balance) {
        MyConst.balance = balance;
    }

    public static String getTvr() {
        return tvr;
    }

    public static void setTvr(String tvr) {
        MyConst.tvr = tvr;
    }

    public static String getTsi() {
        return tsi;
    }

    public static void setTsi(String tsi) {
        MyConst.tsi = tsi;
    }

    public static String getAppname() {
        return appname;
    }

    public static void setAppname(String appname) {
        MyConst.appname = appname;
    }

    public static String getAid() {
        return aid;
    }

    public static void setAid(String aid) {
        MyConst.aid = aid;
    }

    public static String getResponsejson() {
        return responsejson;
    }

    public static void setResponsejson(String responsejson) {
        MyConst.responsejson = responsejson;
    }

    public static JSONObject getJSONResponse() {
        return JSONResponse;
    }

    public static void setJSONResponse(JSONObject JSONResponse) {
        MyConst.JSONResponse = JSONResponse;
    }
}

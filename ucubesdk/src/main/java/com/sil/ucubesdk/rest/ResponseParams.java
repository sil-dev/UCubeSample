package com.sil.ucubesdk.rest;

import java.util.ArrayList;


public class ResponseParams {

    private boolean status;
    private String msg;
    private String sessionId;

    private String appTxReq;
    private String mpuip;
    private String mpuport;
    private String aid;
    private String tvr;
    private String tsi;

    private String name;
    private String ismerchant;
    private String sessiontimeout;
    private String msisdn;
    private String acctno;
    private String token;
    private String rrn;
    private String cardno;
    private String mobileno;
    private String otp;
    private String arpcdata;
    private String printmsg;
    private String smsreceipt;
    private String merchantname;
    private String companyid;
    private String merchantlocation;
    private String paymenttype;
    private String remark;
    private String ustatus;
    private String groupmid;

    private String amt;
    private String subtotal;
    private String total;

    private String govtax;
    private String servicecharge;
    private String date;
    private String mid;

    private String transdate;
    private String transtype;
    private String transnumber;
    private String posId;
    private String respCode;
    private String reason;
    private String single;
    private String authid;
    private String salesReport;
    private String salesSummary;
    private String successReport;
    private String currency;
    private ArrayList<ResponseParams> transhistory=new ArrayList<>();

    private String log;
    private String srno;
    private String hitachiResCode;

    private String addr1;
    private String addr2;
    private String sd;
    private String ed;
    private String invoiceNumber;
    private String cardType;
    private String applName;
    private String batchNo;
    String posLocation;
    String locationCheck;
    String operatorList;
    String operator;
    String walletBalance;
    private String mkskKeys;
    private String serviceBin;;

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(String operatorList) {
        this.operatorList = operatorList;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getApplName() {
        return applName;
    }

    public void setApplName(String applName) {
        this.applName = applName;
    }

    public String getInvoiceNo() {
        return invoiceNumber;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNumber = invoiceNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getEd() {
        return ed;
    }

    public void setEd(String ed) {
        this.ed = ed;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getHitachiResCode() {
        return hitachiResCode;
    }

    public void setHitachiResCode(String hitachiResCode) {
        this.hitachiResCode = hitachiResCode;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getSrno() {
        return srno;
    }

    public void setSrno(String srno) {
        this.srno = srno;
    }

    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getAcctno() {
        return acctno;
    }
    public void setAcctno(String acctno) {
        this.acctno = acctno;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getRrn() {
        return rrn;
    }
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }
    public String getCardno() {
        return cardno;
    }
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }
    public String getMobileno() {
        return mobileno;
    }
    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }
    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getIsmerchant() {
        return ismerchant;
    }

    public void setIsmerchant(String ismerchant) {
        this.ismerchant = ismerchant;
    }

    public String getSessiontimeout() {
        return sessiontimeout;
    }

    public void setSessiontimeout(String sessiontimeout) {
        this.sessiontimeout = sessiontimeout;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPrintmsg() {
        return printmsg;
    }

    public void setPrintmsg(String printmsg) {
        this.printmsg = printmsg;
    }

    public String getMerchantname() {
        return merchantname;
    }

    public void setMerchantname(String merchantname) {
        this.merchantname = merchantname;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getMerchantlocation() {
        return merchantlocation;
    }

    public void setMerchantlocation(String merchantlocation) {
        this.merchantlocation = merchantlocation;
    }

    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getGovtax() {
        return govtax;
    }

    public void setGovtax(String govtax) {
        this.govtax = govtax;
    }

    public String getServicecharge() {
        return servicecharge;
    }

    public void setServicecharge(String servicecharge) {
        this.servicecharge = servicecharge;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public String getUstatus() {
        return ustatus;
    }

    public void setUstatus(String ustatus) {
        this.ustatus = ustatus;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public ArrayList<ResponseParams> getTranshistory() {
        return transhistory;
    }

    public void setTranshistory(ArrayList<ResponseParams> transhistory) {
        this.transhistory = transhistory;
    }

    public String getTransdate() {
        return transdate;
    }

    public void setTransdate(String transdate) {
        this.transdate = transdate;
    }

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }

    public String getTransnumber() {
        return transnumber;
    }

    public void setTransnumber(String transnumber) {
        this.transnumber = transnumber;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSmsreceipt() {
        return smsreceipt;
    }

    public void setSmsreceipt(String smsreceipt) {
        this.smsreceipt = smsreceipt;
    }

    public String getArpcdata() {
        return arpcdata;
    }

    public void setArpcdata(String arpcdata) {
        this.arpcdata = arpcdata;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public String getAppTxReq() {
        return appTxReq;
    }

    public void setAppTxReq(String appTxReq) {
        this.appTxReq = appTxReq;
    }

    public String getMpuip() {
        return mpuip;
    }

    public void setMpuip(String mpuip) {
        this.mpuip = mpuip;
    }

    public String getMpuport() {
        return mpuport;
    }

    public void setMpuport(String mpuport) {
        this.mpuport = mpuport;
    }

    public String getAuthid() {
        return authid;
    }

    public void setAuthid(String authid) {
        this.authid = authid;
    }

    public String getGroupmid() {
        return groupmid;
    }

    public void setGroupmid(String groupmid) {
        this.groupmid = groupmid;
    }

    public String getSalesReport() {
        return salesReport;
    }

    public void setSalesReport(String salesReport) {
        this.salesReport = salesReport;
    }

    public String getSalesSummary() {
        return salesSummary;
    }

    public void setSalesSummary(String salesSummary) {
        this.salesSummary = salesSummary;
    }

    public String getSuccessReport() {
        return successReport;
    }

    public void setSuccessReport(String successReport) {
        this.successReport = successReport;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getTvr() {
        return tvr;
    }

    public void setTvr(String tvr) {
        this.tvr = tvr;
    }

    public String getTsi() {
        return tsi;
    }

    public void setTsi(String tsi) {
        this.tsi = tsi;
    }

    public String getPosLocation() {
        return posLocation;
    }

    public void setPosLocation(String posLocation) {
        this.posLocation = posLocation;
    }

    public String getLocationCheck() {
        return locationCheck;
    }

    public void setLocationCheck(String locationCheck) {
        this.locationCheck = locationCheck;
    }

    public String getMkskKeys() {
        return mkskKeys;
    }

    public void setMkskKeys(String mkskKeys) {
        this.mkskKeys = mkskKeys;
    }

    public String getServiceBin() {
        return serviceBin;
    }

    public void setServiceBin(String serviceBin) {
        this.serviceBin = serviceBin;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

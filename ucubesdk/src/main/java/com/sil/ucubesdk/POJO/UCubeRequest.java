package com.sil.ucubesdk.POJO;

import com.sil.ucubesdk.payment.TransactionType;

public final class UCubeRequest {
    private String username = null;
    private String password = null;
    private String refCompany = null;
    private String mid = null;
    private String tid = null;
    private String imei = null;
    private String imsi = null;
    private String txn_amount = null;
    private String bt_address = null;
    private String remark = "";
    private String transactionId = "";
    private TransactionType requestCode = null;
    private String erroMsg = null;
    private String session_Id=null;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRefCompany(String refCompany) {
        this.refCompany = refCompany;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public void setTxn_amount(String txn_amount) {
        this.txn_amount = txn_amount;
    }

    public void setBt_address(String bt_address) {
        this.bt_address = bt_address;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setRequestCode(TransactionType requestCode) {
        this.requestCode = requestCode;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRefCompany() {
        return refCompany;
    }

    public String getMid() {
        return mid;
    }

    public String getTid() {
        return tid;
    }

    public String getImei() {
        return imei;
    }

    public String getImsi() {
        return imsi;
    }

    public String getTxn_amount() {
        return txn_amount;
    }

    public String getBt_address() {
        return bt_address;
    }

    public String getRemark() {
        return remark;
    }

    public TransactionType getRequestCode() {
        return requestCode;
    }

    public String getErroMsg() {
        return erroMsg;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }



    public boolean isValidRequest() {
        erroMsg = null;


        if (!isValidString(username)) {
            erroMsg = "Username cannot be Null or Empty";
            return false;
        }
        if (!isValidString(password)) {
            erroMsg = "Password cannot be Null or Empty";
            return false;
        }
        if (!isValidString(refCompany)) {
            erroMsg = "Reference Company cannot be Null or Empty";
            return false;
        }
        if (!isValidString(mid)) {
            erroMsg = "Mid cannot be Null or Empty";
            return false;
        }
        if (!isValidString(tid)) {
            tid = "Tid cannot be Null or Empty";
            return false;
        }
        if (!isValidString(imei)) {
            erroMsg = "IMEI cannot be Null or Empty";
            return false;
        }
        if (!isValidString(imsi)) {
            erroMsg = "IMSI cannot be Null or Empty";
            return false;
        }
        if (!isValidString(txn_amount)) {
            erroMsg = "Amount cannot be Null or Empty";
            return false;
        }
        if (!isValidString(bt_address)) {
            erroMsg = "Bluetooth Address cannot be Null or Empty";
            return false;
        }


        return true;
    }

    public boolean isValidRequest(boolean var1) {
        this.erroMsg = null;
        if (!this.isValidString(this.username)) {
            this.erroMsg = "Username cannot be Null or Empty";
            return false;
        } else if (!this.isValidString(this.password)) {
            this.erroMsg = "Password cannot be Null or Empty";
            return false;
        } else if (!this.isValidString(this.refCompany)) {
            this.erroMsg = "Reference Company cannot be Null or Empty";
            return false;
        } else if (!this.isValidString(this.mid)) {
            this.erroMsg = "Mid cannot be Null or Empty";
            return false;
        } else if (!this.isValidString(this.tid)) {
            this.tid = "Tid cannot be Null or Empty";
            return false;
        } else if (!this.isValidString(this.imei)) {
            this.erroMsg = "IMEI cannot be Null or Empty";
            return false;
        } else if (!this.isValidString(this.imsi)) {
            this.erroMsg = "IMSI cannot be Null or Empty";
            return false;
        } else if (!this.isValidString(this.txn_amount)) {
            this.erroMsg = "Amount cannot be Null or Empty";
            return false;
        } else if (var1 && !this.isValidString(this.transactionId)) {
            this.erroMsg = "Transaction Id cannot be Null or Empty";
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidString(String value) {
        return value != null && !value.isEmpty() && !value.trim().isEmpty();
    }

    public String getSession_Id() {
        return session_Id;
    }

    public void setSession_Id(String session_Id) {
        this.session_Id = session_Id;
    }
}

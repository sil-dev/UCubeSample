/*
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */
package com.sil.ucubesdk.rpc.command;

import android.util.Log;

import com.sil.ucubesdk.MyBERTLV;
import com.sil.ucubesdk.TLV;
import com.sil.ucubesdk.rpc.Constants;
import com.sil.ucubesdk.rpc.MyConst;
import com.sil.ucubesdk.rpc.RPCCommand;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author gbillard on 5/23/16.
 */
public class TransactionFinalizationCommand extends RPCCommand {

    /**
     * 0x00 => no forcing
     * 0x01 => forced after authorization
     * 0x59 ('Y') => voice referral accepted
     * 0x5A ('Z') => voice referral declined
     */
    private byte forceFlag = 0x00;
    private boolean declinedOnline = false;
    /**
     * 0x00 => unable to go online
     * 0x01 => approved
     * 0x02 => declined
     */
    private byte authorizationStatus = 0x02;
    private byte[] authResponseCode;
    private byte[] issuerDataAuth;
    private byte[] issuerScript1;
    private byte[] issuerScript2;
    private byte[] requestedTags;
    private byte[] transactionData;

    public TransactionFinalizationCommand() {
        super(Constants.TRANSACTION_FINAL);

    }

    public TransactionFinalizationCommand(boolean declinedOnline) {
        super(Constants.TRANSACTION_FINAL);
        this.declinedOnline = declinedOnline;
    }

    public void setAuthResponse(byte[] response) {
        setAuthResponse(TLV.parse(response));
    }

    public void setAuthResponse(Map<Integer, byte[]> response) {
        //5513
        Log.d("ARPC : ", "" + MyConst.getArpc());
        //Log.d("(0x91)",""+response.get(0x91));
        setAuthResponseCode(response.get(0x8A));
        if (MyConst.getArpc() != null) {
            Log.d("ARPC : ", MyConst.getArpc());
            if (!MyConst.getArpc().equalsIgnoreCase("FAIL")) {
                TreeMap<String, String> tlvMap = null;
                try {
                    tlvMap = MyBERTLV.parseTLV("" + MyConst.getArpc().substring(3));
                    Log.d("BERTLV:",""+tlvMap.toString());
                    Log.d("Set Arpc", "Yes");
                    if (tlvMap.get("91") != null) {
                        byte[] b = hexStringToByteArray(tlvMap.get("91"));
                        Log.d("InjectARPC", "" + tlvMap.get("91"));
                        setIssuerDataAuth(b);
                    }
                    if (tlvMap.get("71") != null) {
                        Log.d("71:", "" + tlvMap.get("71"));
                        setIssuerScript1(hexStringToByteArray(tlvMap.get("71")));
                    }

                    if (tlvMap.get("72") != null) {
                        Log.d("72:", "" + tlvMap.get("72"));
                        setIssuerScript2(hexStringToByteArray(tlvMap.get("72")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                setRequestedTags(new byte[]{(byte) 0x95,(byte) 0x9B,(byte) 0xDF,(byte) 0x03});
                //setRequestedTags(new byte[]{(byte) 0xDF,(byte) 0x03});
            }
        }
        setRequestedTags(new byte[]{(byte) 0x95,(byte) 0x9B,(byte) 0xDF,(byte) 0x03});
        //setIssuerDataAuth(response.get(0x91));
        //setIssuerScript1(response.get(0x71));
        //setIssuerScript2(response.get(0x72));
        //setRequestedTags(new byte[]{(byte) 0x95,(byte) 0x9B,(byte) 0x9F,(byte) 0x03,(byte) 0x9F,(byte) 0x5B});
    }

    public byte[] getTransactionData() {
        return transactionData;
    }

    public void setForceFlag(byte forceFlag) {
        this.forceFlag = forceFlag;
    }

    public void setAuthResponseCode(byte[] authResponseCode) {
        this.authResponseCode = authResponseCode;

        if (authResponseCode instanceof byte[] && authResponseCode.length == 2) {
            if (authResponseCode[0] == 0x39) {
                authorizationStatus = 0x00;

            } else if (authResponseCode[0] == 0x30 && authResponseCode[1] == 0x30) {
                authorizationStatus = 0x01;

            } else {
                authorizationStatus = 0x02;
            }
        }
    }

    public void setAuthorizationStatus(byte authorizationStatus) {
        this.authorizationStatus = authorizationStatus;
    }

    public void setIssuerDataAuth(byte[] issuerDataAuth) {
        this.issuerDataAuth = issuerDataAuth;
    }

    public void setIssuerScript1(byte[] issuerScript1) {
        this.issuerScript1 = issuerScript1;
    }

    public void setIssuerScript2(byte[] issuerScript2) {
        this.issuerScript2 = issuerScript2;
    }

    public void setRequestedTags(byte[] requestedTags) {
        this.requestedTags = requestedTags;
    }

    @Override
    protected byte[] createPayload() {
        byte[] payload = new byte[1024];
        int offset = 0;

        payload[offset++] = (byte) 0xDF;
        payload[offset++] = 0x15;
        payload[offset++] = 0x01;
        payload[offset++] = (byte) forceFlag;

        payload[offset++] = (byte) 0xDF;
        payload[offset++] = 0x16;
        payload[offset++] = 0x01;
        if (declinedOnline) payload[offset++] = (byte) authorizationStatus;
        else payload[offset++] = 0x02;

        if (authResponseCode != null) {
            payload[offset++] = (byte) 0x8A;
            payload[offset++] = 0x02;
            payload[offset++] = authResponseCode[1];
            payload[offset++] = authResponseCode[1];
        }

        if (issuerDataAuth != null) {
            payload[offset++] = (byte) 0x91;
            payload[offset++] = (byte) issuerDataAuth.length;
            System.arraycopy(issuerDataAuth, 0, payload, offset, issuerDataAuth.length);
            offset += issuerDataAuth.length;
        }

        if (issuerScript1 != null) {
            payload[offset++] = (byte) 0x71;
//            payload[offset++] = (byte) 0x81;
            payload[offset++] = (byte) issuerScript1.length;
            System.arraycopy(issuerScript1, 0, payload, offset, issuerScript1.length);
            offset += issuerScript1.length;
        }

        if (issuerScript2 != null) {
            payload[offset++] = (byte) 0x72;
//            payload[offset++] = (byte) 0x81;
            payload[offset++] = (byte) issuerScript2.length;
            System.arraycopy(issuerScript2, 0, payload, offset, issuerScript2.length);
            offset += issuerScript2.length;
        }

        if (requestedTags != null) {
            payload[offset++] = (byte) 0xC1;
            payload[offset++] = (byte) requestedTags.length;
            System.arraycopy(requestedTags, 0, payload, offset, requestedTags.length);
            offset += requestedTags.length;
        }

        return Arrays.copyOfRange(payload, 0, offset);
    }

    @Override
    protected boolean isValidResponse() {
        return response.getStatus() == 0x07 || response.getStatus() == 0x08;
    }


    private static byte[] hexStringToByteArray(String hexString) {
        if (hexString.length() % 2 != 0) hexString = "0" + hexString;
        final int len = hexString.length();

        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(hexString.charAt(i));
            int l = hexToBin(hexString.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
            }
            out[i / 2] = (byte) (h * 16 + l);
        }
        return out;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }


}

package com.sil.ucubesdk;

import android.util.Log;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by shankar.savant on 6/22/2017.
 */

public class MyDataConverter {

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static byte[] hexStringToByteArray(String hexString) {
        if (hexString.length() % 2 != 0) hexString = "0" + hexString;
        byte[] byteArray = new byte[hexString.length()/2];
        for(int i=0; i<hexString.length(); i+=2){
            int h = hexToByte(hexString.charAt(i));
            int l = hexToByte(hexString.charAt(i + 1));
            if (h == -1 || l == -1) throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
            byteArray[i / 2] = (byte) (h * 16 + l);
        }
        return byteArray;
    }

    public static byte[] hexStringToByteArrayPadRight(String hexString, char padChar, int len) {
        hexString = String.format("%"+len+"s", hexString).replace(" ",padChar+"");
        if (hexString.length() % 2 != 0) hexString =  hexString+padChar;
        byte[] byteArray = new byte[hexString.length()/2];
        for(int i=0; i<hexString.length(); i+=2){
            int h = hexToByte(hexString.charAt(i));
            int l = hexToByte(hexString.charAt(i + 1));
            if (h == -1 || l == -1) throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
            byteArray[i / 2] = (byte) (h * 16 + l);
        }
        return byteArray;
    }

    public static byte[] hexStringToByteArrayPadLeft(String hexString, char padChar, int len) {
        hexString = String.format("%-"+len+"s", hexString).replace(" ",padChar+"");
        if (hexString.length() % 2 != 0) hexString =  padChar+hexString;
        byte[] byteArray = new byte[hexString.length()/2];
        for(int i=0; i<hexString.length(); i+=2){
            int h = hexToByte(hexString.charAt(i));
            int l = hexToByte(hexString.charAt(i + 1));
            if (h == -1 || l == -1) throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
            byteArray[i / 2] = (byte) (h * 16 + l);
        }
        return byteArray;
    }

    public static byte hexPairToByte(String hexString) {
        int h = hexToByte(hexString.charAt(0));
        int l = hexToByte(hexString.charAt(1));
        if (h == -1 || l == -1) throw new IllegalArgumentException("contains illegal character for hexBinary: " + hexString);
        byte byteValue = (byte) (h * 16 + l);
        return byteValue;
    }

    public static int hexToByte(char ch) {
        if ('0' <= ch && ch <= '9') return ch - '0';
        if ('A' <= ch && ch <= 'F') return ch - 'A' + 10;
        if ('a' <= ch && ch <= 'f') return ch - 'a' + 10;
        return -1;
    }



    public static String byteArrayToHexString(byte[] data) {
        StringBuilder s = new StringBuilder(data.length * 2);
        for (byte b : data) {
            s.append(hexCode[(b >> 4) & 0xF]);
            s.append(hexCode[(b & 0xF)]);
        }
        return s.toString();
    }

    public static String xor(String string1, String string2)
    {
        return String.format("%"+string1.length()+"s", new BigInteger(string1, 16).xor(new BigInteger(string2,16)).toString(16)).replace(' ', '0').toUpperCase();
    }


    public static String randomHexString(int len)
    {
        byte[] randomBytes = new byte[len/2+1];
        new SecureRandom().nextBytes(randomBytes);
        return byteArrayToHexString(randomBytes).substring(0,len);
    }

    public static String padRight(String data, char padChar, int len)
    {
        if(data.length()>len) return data;
        else while(data.length() != len)  data += padChar;
        return data;
    }

    public static String padLeft(String data, char padChar, int len)
    {
        if(data.length()>len) return data;
        else while(data.length() != len)  data = padChar + data;
        return data;
    }

    public static String satisfyLeftBCD(String string){
        if(string.length() %2 == 1) return "0"+string;
        else return string;
    }

	/*public static String satisfyRightBCD(String string){
		if(string.length() %2 == 1) return string+"0";
		else return string;
	}*/


    public static String getIsoAmt(String amt){
        Double d = Double.parseDouble(amt)*100;
        long l = d.longValue();
        String amt12 = String.format("%012d", l);
        Log.d("12DigAmt",amt12);
        return amt12;
    }

}

package com.sil.ucubesdk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Patterns;

import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shankar.savant on 20-01-2017.
 */

public class AndyUtility {


    public static Date touchedTime;

    public void touch() {
        touchedTime = new Date();
    }

    public static String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL) {
        if (str.charAt(0) == '.') str = "0" + str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0, up = 0, decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != '.' && after == false) {
                up++;
                if (up > MAX_BEFORE_POINT) return rFinal;
            } else if (t == '.') {
                after = true;
            } else {
                decimal++;
                if (decimal > MAX_DECIMAL)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }

    public static String getLogDate() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            return simpleDateFormat.format(new Date());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTodaysDate() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
            return simpleDateFormat.format(new Date());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDate(String date) {
        try {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
            Date d = simpleDateFormat1.parse(date);
            return simpleDateFormat2.format(d);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getLogDate1() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmm");
            return simpleDateFormat.format(new Date());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Checks if the device is rooted.
     *
     * @return <code>true</code> if the device is rooted, <code>false</code>
     * otherwise.
     */
    public static boolean isRooted() {

// get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

// check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
// ignore
        }

// try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        try {
            int exitValue = Runtime.getRuntime().exec(command).waitFor();
            if (exitValue != 0) return false;
            else return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNullOrEmpty(String string) {
        if (string == null || string.trim().length() == 0 || string.trim().equalsIgnoreCase("null"))
            return true;
        else
            return false;
    }


    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
//                Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    public static String getRRN(String varSTAN) {
        String rrn = new SimpleDateFormat("yyyyDDDHH").format(new Date()).substring(3) + varSTAN;
        System.out.println("RRN : " + rrn);
        return rrn;
    }

    private static String convertDate(String sDate, String sFormat) {
        SimpleDateFormat sp = new SimpleDateFormat(sFormat);
        java.sql.Date dt = java.sql.Date.valueOf(sDate.trim());
        String sRet = sp.format(dt);
        return sRet;
    }

    public static String getISOField11(int length) {
        // field 11 == stan
        String result = "";
        int random;
        while (true) {
            random = (int) ((Math.random() * (10)));
            if (result.length() == 0 && random == 0) {
                random += 1;
                result += random;
            } else if (!result.contains(Integer.toString(random))) {
                result += Integer.toString(random);
            }
            if (result.length() >= length) {
                break;
            }
        }

        return result;
    }

    public static String trimCommaOfString(String string) {
        //  String returnString;
        if (string.contains(",")) {
            return string.replace(",", "");
        } else {
            return string;
        }
    }


    public static boolean isAskPinForMSByCDT(String bin6digit) {
        //TODO As per vinayak sir, we have to check BIN range , if card bin is found in below range then ask PIN if not then do tx using service code
        ArrayList<CDT> list = new ArrayList<>();
        list.add(new CDT(new BigInteger("500000"), new BigInteger("509999")));
        list.add(new CDT(new BigInteger("600000"), new BigInteger("690000")));
        list.add(new CDT(new BigInteger("560000"), new BigInteger("599999")));

        for (int i = 0; i < list.size(); i++) {
            CDT cdt = list.get(i);
            if (new BigInteger(bin6digit).compareTo(cdt.getStartno()) == 1
                    && new BigInteger(bin6digit).compareTo(cdt.getEndno()) == -1) {
                return true;
            } else if (new BigInteger(bin6digit).compareTo(cdt.getStartno()) == 0
                    || new BigInteger(bin6digit).compareTo(cdt.getEndno()) == 0) {
                return true;
            }
        }
        return false;
    }

    public static class CDT {
        BigInteger startno, endno;

        public CDT(BigInteger startno, BigInteger endno) {
            this.startno = startno;
            this.endno = endno;
        }

        public BigInteger getStartno() {
            return startno;
        }

        public void setStartno(BigInteger startno) {
            this.startno = startno;
        }

        public BigInteger getEndno() {
            return endno;
        }

        public void setEndno(BigInteger endno) {
            this.endno = endno;
        }
    }

    public static boolean isValidEmail(CharSequence email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    public static boolean isAllZero(String str) {
        if (str.matches("^[0]+$")) {
            // accept this input
            return true;
        }
        return false;
    }
}


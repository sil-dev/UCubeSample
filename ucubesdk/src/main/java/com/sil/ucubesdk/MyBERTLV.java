package com.sil.ucubesdk;

import java.util.TreeMap;

/**
 * Created by shankar.savant on 6/22/2017.
 */

public class MyBERTLV {
    public static TreeMap<String, String> parseTLV(String string)
    {
        TreeMap<String, String> tlvMap = new TreeMap<String, String>();
        byte[] tlvBytes = null;
        try {
            tlvBytes = MyDataConverter.hexStringToByteArray(string);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String tagName ="";
        String tagValue = "";
        int i=0;
        while(i < tlvBytes.length)
        {
            try {
                tagName = tagName+string.substring(2*i, 2*i+2);
                if((tlvBytes[i] & 0x1F) == 0x1F && tagName.length()<4) {
                    i++;
                    continue;
                }
                else
                {
                    i++;
                    int len = tlvBytes[i];
                    i++;
                    tagValue = string.substring(i*2,  i*2+len*2);
                    i=i+len;
                    tlvMap.put(tagName, tagValue);
                    tagName = "";
                    tagValue = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
        return tlvMap;
    }

}

package com.tencent.qcloud.timchat.widget;

import android.text.TextUtils;

/**
 * Created by peggy on 16/5/26.
 */

public class PhotoUtils {
    public static String getSmall(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        if (!url.contains("!")) {
            return url + "!120x120";
        }else{
            try{
                return url.split("!")[0] +"!120x120";
            }catch (Exception e){
                return url;
            }
        }
    }

    public static String getMiddle(String url) {
        if (TextUtils.isEmpty(url))
            return "";
        if (!url.contains("!")) {
            return url + "!180x180";
        }else{
            try{
                return url.split("!")[0] +"!120x120";
            }catch (Exception e){
                return url;
            }
        }
    }

    public static String getGauss(String photo) {
        if (photo.contains("zoneke-img")){
            if (photo.contains("!")){
                return photo.split("!")[0].concat("!gaussblur");
            }else {
                return photo.concat("!gaussblur");
            }
        }else
            return photo;
    }
}

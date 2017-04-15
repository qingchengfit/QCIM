package com.tencent.qcloud.timchat.chatutils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by edz on 2017/2/10.
 */

public class NetUtil {

    public OnUserSigListener onUserSigListener;
    private String host;

    public NetUtil(String identifier, String host){
        getUsersig(identifier);
    }



    public void getUsersig(String identifier){
        String urlPrefix = "http://c1.qingchengfit.cn/api/im/usersig/?identifier=";
        final String urlWhole = urlPrefix + identifier;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlWhole);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(5000);
                    InputStream inStream = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int current = 0;
                    while ((current = inStream.read(data,0,data.length)) != -1){
                        bos.write(data,0,current);
                    }

                    String strResponse = new String(data,"utf-8");
                    inStream.close();
                    bos.close();
                    onUserSigListener.onSuccessed(strResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setOnUserSigListener(OnUserSigListener onUserSigListener) {
        this.onUserSigListener = onUserSigListener;
    }

    public interface OnUserSigListener{
        void onSuccessed(String userSig);
    }

}

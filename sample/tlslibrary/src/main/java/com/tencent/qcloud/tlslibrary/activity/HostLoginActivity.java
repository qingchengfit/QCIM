package com.tencent.qcloud.tlslibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.helper.SmsContentObserver;
import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.qcloud.tlslibrary.service.Constants;
import com.tencent.qcloud.tlslibrary.service.TLSService;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSUserInfo;

public class HostLoginActivity extends Activity {

    private final static String TAG = "HostLoginActivity";

    private TLSService tlsService;
    private SmsContentObserver smsContentObserver = null;
    //private int login_way = Constants.SMS_LOGIN | Constants.QQ_LOGIN | Constants.WX_LOGIN;
    private int login_way = Constants.SMS_LOGIN;
    final static int STR_ACCOUNT_LOGIN_REQUEST = 10000;
    final static int SMS_REG_REQUEST = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_host_login"));

        Intent intent = getIntent();
        if (Constants.thirdappPackageNameSucc == null)
            Constants.thirdappPackageNameSucc = intent.getStringExtra(Constants.EXTRA_THIRDAPP_PACKAGE_NAME_SUCC);
        if (Constants.thirdappClassNameSucc == null)
            Constants.thirdappClassNameSucc = intent.getStringExtra(Constants.EXTRA_THIRDAPP_CLASS_NAME_SUCC);
        if (Constants.thirdappPackageNameFail == null)
            Constants.thirdappPackageNameFail = intent.getStringExtra(Constants.EXTRA_THIRDAPP_PACKAGE_NAME_FAIL);
        if (Constants.thirdappClassNameFail == null)
            Constants.thirdappClassNameFail = intent.getStringExtra(Constants.EXTRA_THIRDAPP_CLASS_NAME_FAIL);


        tlsService = TLSService.getInstance();

        if ((login_way & Constants.SMS_LOGIN) != 0) { // 短信登录
/*            smsContentObserver = new SmsContentObserver(new Handler(),
                    this,
                    (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "checkCode_hostLogin")),
                    Constants.SMS_LOGIN_SENDER);
            //注册短信变化监听
            this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);*/
        }


        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SETTING_LOGIN_WAY, Constants.SMS_LOGIN);
        editor.commit();
    }




    private void initTLSLogin() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TLSUserInfo userInfo = tlsService.getLastUserInfo();
                if (userInfo != null) {
                    EditText editText = (EditText) HostLoginActivity.this
                            .findViewById(MResource.getIdByName(getApplication(), "id", "phoneNumber_hostLogin"));
                    String phoneNumber = userInfo.identifier;
                    phoneNumber = phoneNumber.substring(phoneNumber.indexOf('-') + 1);
                    editText.setText(phoneNumber);
                }
            }
        });
    }

    //应用调用Andriod_SDK接口时，使能成功接收到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (STR_ACCOUNT_LOGIN_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (SMS_REG_REQUEST == requestCode) {
            if (RESULT_OK == resultCode) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else {
            if (requestCode == com.tencent.connect.common.Constants.REQUEST_API) {
                if (resultCode == com.tencent.connect.common.Constants.RESULT_LOGIN) {
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent == null)     return;

        // 判断是否是从微信登录界面返回的
        int wx_login = intent.getIntExtra(Constants.EXTRA_WX_LOGIN, Constants.WX_LOGIN_NON);
        if (wx_login != Constants.WX_LOGIN_NON) {
            if (wx_login == Constants.WX_LOGIN_SUCCESS) {
                Intent data = new Intent();
                data.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.WX_LOGIN);
                data.putExtra(Constants.EXTRA_WX_LOGIN, Constants.WX_LOGIN_SUCCESS);
                data.putExtra(Constants.EXTRA_WX_OPENID, intent.getStringExtra(Constants.EXTRA_WX_OPENID));
                data.putExtra(Constants.EXTRA_WX_ACCESS_TOKEN, intent.getStringExtra(Constants.EXTRA_WX_ACCESS_TOKEN));
                if (Constants.thirdappPackageNameSucc != null && Constants.thirdappClassNameSucc != null) {
                    data.setClassName(Constants.thirdappPackageNameSucc, Constants.thirdappClassNameSucc);
                    startActivity(data);
                } else {
                    setResult(RESULT_OK, data);
                }
                finish();
            }
            return;
        }

        // 判断是否是从注册界面返回的
        String countryCode = intent.getStringExtra(Constants.COUNTRY_CODE);
        String phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER);

    }

    protected void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
    }
}

package com.tencent.qcloud.tlslibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.service.Constants;
import com.tencent.qcloud.tlslibrary.service.TLSService;

import tencent.tls.platform.TLSUserInfo;

public class IndependentLoginActivity extends Activity {

    private final static String TAG = "IndependentLoginActivity";

    private TLSService tlsService;
    //private int login_way = Constants.USRPWD_LOGIN | Constants.QQ_LOGIN | Constants.WX_LOGIN;
    private int login_way = Constants.USRPWD_LOGIN;

    final static int STR_ACCOUNT_REG_REQUEST = 20001;
    final static int SMS_LOGIN_REQUEST = 20002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_independent_login"));

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

        if ((login_way & Constants.USRPWD_LOGIN) != 0) { // 账号密码登录
            initAccountLoginService();
        }

        SharedPreferences settings = getSharedPreferences(Constants.TLS_SETTING, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.SETTING_LOGIN_WAY, Constants.USRPWD_LOGIN);
        editor.commit();

    }

    private void initTLSLogin() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TLSUserInfo userInfo = tlsService.getLastUserInfo();
                if (userInfo != null) {
                    EditText editText = (EditText) IndependentLoginActivity.this
                            .findViewById(MResource.getIdByName(getApplication(), "id", "username"));
                    editText.setText(userInfo.identifier);
                }
            }
        });
    }

    private void initAccountLoginService() {
        tlsService.initAccountLoginService(this,
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "username")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")));
    }

    //应用调用Andriod_SDK接口时，使能成功接收到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == STR_ACCOUNT_REG_REQUEST) {
            if (RESULT_OK == resultCode) {
                setResult(RESULT_OK, data);
                finish();
            }
        } else if (requestCode == SMS_LOGIN_REQUEST) {
            if (RESULT_OK == resultCode) {
                // 返回 ok 表示短信登录界面的处理是 ok 并不需要此 Activity 做什么
                setResult(RESULT_OK, data);
                finish();
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
        if (intent == null) {
            return;
        }

        // 判断是否是从注册界面返回
        String username = intent.getStringExtra(Constants.USERNAME);
        String password = intent.getStringExtra(Constants.PASSWORD);
        if (username != null && password != null) {
            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "username"))).setText(username);
            ((EditText) findViewById(MResource.getIdByName(getApplication(), "id", "password"))).setText(password);
            findViewById(MResource.getIdByName(getApplication(), "id", "btn_login")).performClick();
        }
    }
}
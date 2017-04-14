package com.tencent.qcloud.tlslibrary.service;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/13.
 */
public class AccountRegisterService {

  private Context context;

  private TLSService tlsService;
  private OnRegisterListener onRegisterListener;
  StrAccRegListener strAccRegListener;

  private String username;
  private String password;

  public AccountRegisterService(Context context, String txt_username, String txt_password) {
    this.context = context;
    this.username = txt_username;
    this.password = txt_password;

    tlsService = TLSService.getInstance();
    strAccRegListener = new StrAccRegListener();
    //String tmp = AccountRegisterService.this.txt_repassword.getText().toString();

    //if (username.length() == 0 || password.length() == 0 || tmp.length() == 0) {
    //  Util.showToast(AccountRegisterService.this.context, "用户名密码不能为空");
    //  return;
    //}
    //
    //if (!password.equals(tmp)) {
    //  Util.showToast(AccountRegisterService.this.context, "两次输入的密码不一致");
    //  return;
    //}
    //
    //if (password.length() < 8) {
    //  Util.showToast(AccountRegisterService.this.context, "密码的长度不能小于8个字符");
    //}

    int result = tlsService.TLSStrAccReg(username, password, strAccRegListener);
    if (result == TLSErrInfo.INPUT_INVALID) {
      Util.showToast(AccountRegisterService.this.context, "IM账号注册失败");
    }
  }

  public void setOnRegisterListener(OnRegisterListener onRegisterListener) {
    this.onRegisterListener = onRegisterListener;
  }

  class StrAccRegListener implements TLSStrAccRegListener {
    @Override public void OnStrAccRegSuccess(TLSUserInfo userInfo) {
      Util.showToast(context, "成功注册 " + userInfo.identifier);
      TLSService.getInstance().setLastErrno(0);
      if (onRegisterListener != null) {
        onRegisterListener.registerSuccess(username, password);
      }
    }

    @Override public void OnStrAccRegFail(TLSErrInfo errInfo) {
      Util.notOK(context, errInfo);
      if (onRegisterListener != null) {
        onRegisterListener.registerFailed();
      }
    }

    @Override public void OnStrAccRegTimeout(TLSErrInfo errInfo) {
      Util.notOK(context, errInfo);
      if (onRegisterListener != null) {
        onRegisterListener.registerTimeOut();
      }
    }
  }

  public interface OnRegisterListener{
    void registerSuccess(String username, String password);

    void registerFailed();

    void registerTimeOut();
  }

}

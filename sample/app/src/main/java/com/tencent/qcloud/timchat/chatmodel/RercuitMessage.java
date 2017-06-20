package com.tencent.qcloud.timchat.chatmodel;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageDraft;
import com.tencent.TIMTextElem;
import com.tencent.qcloud.timchat.adapters.ChatItem;
import com.tencent.qcloud.timchat.chatutils.EmoticonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by fb on 2017/6/13.
 */

public class RercuitMessage extends Message{

  public RercuitMessage(TIMMessage message){
    this.message = message;
  }

  public RercuitMessage(String s){
    message = new TIMMessage();
    TIMCustomElem elem = new TIMCustomElem();
    elem.setData(s.getBytes());
    message.addElement(elem);
  }

  /**
   * 在聊天界面显示消息
   *
   * @param viewHolder 界面样式
   * @param context 显示消息的上下文
   */
  @Override
  public void showMessage(ChatItem.ViewHolder viewHolder, Context context) {
  }

  /**
   * 获取消息摘要
   */
  @Override
  public String getSummary() {
    return "【职位信息】";
  }

  /**
   * 保存消息或消息文件
   */
  @Override
  public void save() {

  }
}

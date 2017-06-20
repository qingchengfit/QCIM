package com.tencent.qcloud.timchat.chatmodel;

import com.tencent.TIMElem;
import com.tencent.TIMElemType;

/**
 * Created by fb on 2017/6/13.
 */

public class RercuitElem extends TIMElem {

  private String name;
  private String path;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  @Override public TIMElemType getType() {
    return TIMElemType.Custom;
  }
}

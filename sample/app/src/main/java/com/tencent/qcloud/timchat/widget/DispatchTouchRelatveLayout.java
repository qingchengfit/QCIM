package com.tencent.qcloud.timchat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by fb on 2017/7/25.
 */

public class DispatchTouchRelatveLayout extends RelativeLayout {

  public DispatchTouchRelatveLayout(Context context) {
    super(context);
  }

  public DispatchTouchRelatveLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DispatchTouchRelatveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if(event.getAction() == MotionEvent.ACTION_DOWN){
      return false;
    }
    return super.onTouchEvent(event);
  }
}

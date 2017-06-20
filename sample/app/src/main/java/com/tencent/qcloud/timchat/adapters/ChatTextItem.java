package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.R2;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.chatmodel.TextMessage;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fb on 2017/5/22.
 */

public class ChatTextItem extends ChatItem<ChatTextItem.ChatTextItemVH> {

  private TextMessage message;
  private Context context;

  /**
   * Constructor
   *
   * @param context The current context.
   */
  public ChatTextItem(Context context, Message message, String avatar,
      OnDeleteMessageItem onDeleteMessageItem) {
    super(context, message, avatar, onDeleteMessageItem);
    this.message = (TextMessage) message;
    this.context = context;
  }

  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public ChatTextItemVH createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater,
      ViewGroup parent) {
    return new ChatTextItemVH(inflater.inflate(getLayoutRes(), parent, false), adapter);
  }

  @Override public void bindViewHolder(FlexibleAdapter adapter, ChatTextItemVH holder, int position,
      List payloads) {
    super.bindViewHolder(adapter, holder, position, payloads);
    boolean hasText = false;
    TIMMessage timMessage = message.getMessage();
    List<TIMElem> elems = new ArrayList<>();
    for (int i = 0; i < timMessage.getElementCount(); ++i){
      elems.add(timMessage.getElement(i));
      if (timMessage.getElement(i).getType() == TIMElemType.Text){
        hasText = true;
      }
    }
    SpannableStringBuilder stringBuilder = getString(elems, context);
    if (!hasText){
      stringBuilder.insert(0," ");
    }
    if (message.isSelf()){
      holder.textRightMessageContent.setText(stringBuilder.toString());
    }else{
      holder.textLeftMessageContent.setText(stringBuilder.toString());
    }
    message.showStatus(holder);
  }

  public static SpannableStringBuilder getString(List<TIMElem> elems, Context context){
    SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
    for (int i = 0; i<elems.size(); ++i){
      switch (elems.get(i).getType()){
        case Face:
          TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
          int startIndex = stringBuilder.length();
          try{
            AssetManager am = context.getAssets();
            InputStream is = am.open(String.format("emoticon/%d.gif", faceElem.getIndex()));
            if (is == null) continue;
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Matrix matrix = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            matrix.postScale(2, 2);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, true);
            ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BASELINE);
            stringBuilder.append(String.valueOf(faceElem.getIndex()));
            stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            is.close();
          }catch (IOException e){

          }
          break;
        case Text:
          TIMTextElem textElem = (TIMTextElem) elems.get(i);
          stringBuilder.append(textElem.getText());
          break;
      }

    }
    return stringBuilder;
  }

  private static int getNumLength(int n){
    return String.valueOf(n).length();
  }

  @Override public int getLayoutRes() {
    return R.layout.item_text_message;
  }

  class ChatTextItemVH extends ChatItem.ViewHolder {
    @BindView(R2.id.text_left_message_content) TextView textLeftMessageContent;
    @BindView(R2.id.text_right_message_content) TextView textRightMessageContent;
    public ChatTextItemVH(View view, FlexibleAdapter adapter) {
      super(view, adapter);
      ButterKnife.bind(this, view);
    }
  }
}

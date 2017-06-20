package com.tencent.qcloud.timchat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

/**
 * Created by fb on 2017/4/26.
 */

public class ChatImageView extends android.support.v7.widget.AppCompatImageView {

  private Paint p;

  public ChatImageView(Context context) {
    super(context);
    init();
  }

  public ChatImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    if (p == null) {
      p = new Paint();
      p.setAntiAlias(true);
      p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }
  }

  public void setBitmap(String url, final Bitmap mask) {

    Glide.with(getContext()).load(url).asBitmap().into(new Target<Bitmap>() {
      @Override public void onLoadStarted(Drawable placeholder) {

      }

      @Override public void onLoadFailed(Exception e, Drawable errorDrawable) {

      }

      @Override
      public void onResourceReady(Bitmap result, GlideAnimation<? super Bitmap> glideAnimation) {
        result = small(result);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        NinePatch np = new NinePatch(mask, mask.getNinePatchChunk(), null);
        Rect rect = new Rect(0, 0, result.getWidth(), result.getHeight());
        np.draw(mCanvas, rect, null);
        mCanvas.drawBitmap(result, 0, 0, paint);
        setImageBitmap(result);
        setScaleType(ScaleType.CENTER);
        paint.setXfermode(null);
        invalidate();
      }

      @Override public void onLoadCleared(Drawable placeholder) {

      }

      @Override public void getSize(SizeReadyCallback cb) {

      }

      @Override public void setRequest(Request request) {

      }

      @Override public Request getRequest() {
        return null;
      }

      @Override public void onStart() {

      }

      @Override public void onStop() {

      }

      @Override public void onDestroy() {

      }
    });
  }

  private static Bitmap small(Bitmap bitmap) {
    Matrix matrix = new Matrix();
    matrix.postScale(0.3f, 0.3f); //长和宽放大缩小的比例
    Bitmap resizeBmp =
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    return resizeBmp;
  }
}

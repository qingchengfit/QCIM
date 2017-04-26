package com.tencent.qcloud.timchat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

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

    private void init(){
        if(p == null) {
            p = new Paint();
            p.setAntiAlias(true);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
    }

    public void setBitmap(Bitmap bitmap, Bitmap mask){

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        NinePatch np = new NinePatch(mask, mask.getNinePatchChunk(), null);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        np.draw(mCanvas, rect, null);
        mCanvas.drawBitmap(bitmap, 0, 0, paint);
        setImageBitmap(result);
        setScaleType(ScaleType.CENTER);
        paint.setXfermode(null);
        invalidate();
    }

}

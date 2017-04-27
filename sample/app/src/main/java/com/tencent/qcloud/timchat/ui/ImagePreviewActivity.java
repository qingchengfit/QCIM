package com.tencent.qcloud.timchat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.widget.TemplateTitle;
import java.io.File;
import java.io.IOException;

import static android.R.attr.scaleX;
import static android.R.attr.scaleY;

public class ImagePreviewActivity extends Activity {

    private static final String TAG = "imagepreview";
    private String path;
    private CheckBox isOri;
    private boolean isSetHead;
    private LinearLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        path = getIntent().getStringExtra("path");
        isSetHead = getIntent().getBooleanExtra("head", false);
        isOri = (CheckBox) findViewById(R.id.isOri);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonPanel);
        if (isSetHead){
            buttonLayout.setVisibility(View.GONE);
        }else{
            buttonLayout.setVisibility(View.VISIBLE);
        }
        TemplateTitle title = (TemplateTitle) findViewById(R.id.imagePreviewTitle);
        title.setMoreTextAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("path", path);
                intent.putExtra("isOri", isOri.isChecked());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        buttonLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                buttonLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                showImage();
            }
        });

    }

    private void showImage(){
        if (path.equals("")) return;
        final File file = new File(path);

        if (file.exists()){

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try{
                int screenwidth = getResources().getDisplayMetrics().widthPixels;
                Matrix mat = new Matrix();
                mat.postScale(scaleX, scaleY);
                Bitmap bitmap = BitmapFactory.decodeFile(path);

                ExifInterface ei =  new ExifInterface(path);
                int roata = 0 ;
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        roata = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        roata = 180;
                        break;
                }
                ImageView imageView = (ImageView) findViewById(R.id.image);
                //imageView.setImageBitmap(fitBitmap(bitmap,screenwidth));
                Glide.with(this).load(file).listener(new RequestListener<File, GlideDrawable>() {
                    @Override public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target,
                        boolean isFromMemoryCache, boolean isFirstResource) {
                        isOri.setText(getString(R.string.chat_image_preview_ori) + "(" + getFileSize(file.length()) + ")");

                        return false;
                    }
                })
                    .transform( new RotateTransformation( this, roata ))
                    .into(imageView);
            }catch (IOException e){
                Toast.makeText(this, getString(R.string.chat_image_preview_load_err), Toast.LENGTH_SHORT).show();
            }
        }else{
            finish();
        }
    }
    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super( context );

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return "rotate" + rotateRotationAngle;
        }
    }
    private String getFileSize(long size){
        StringBuilder strSize = new StringBuilder();
        if (size < 1024){
            strSize.append(size).append("B");
        }else if (size < 1024*1024){
            strSize.append(size/1024).append("K");
        }else{
            strSize.append(size/1024/1024).append("M");
        }
        return strSize.toString();
    }

    /**
     * 缩放Bitmap满屏
     *
     * @param bitmap
     * @param screenWidth
     * @param screenHight
     * @return
     */
    public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
        int screenHight)
    {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) screenWidth / w;
        float scale2 = (float) screenHight / h;
        // scale = scale < scale2 ? scale : scale2;
        matrix.postScale(scale, scale);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 按最大边按一定大小缩放图片
     * */
    public static Bitmap scaleImage(byte[] buffer, float size)
    {
        // 获取原图宽度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
            options);
        // 计算缩放比例
        float reSize = options.outWidth / size;
        if (options.outWidth < options.outHeight)
        {
            reSize = options.outHeight / size;
        }
        // 如果是小图则放大
        if (reSize <= 1)
        {
            int newWidth = 0;
            int newHeight = 0;
            if (options.outWidth > options.outHeight)
            {
                newWidth = (int) size;
                newHeight = options.outHeight * (int) size / options.outWidth;
            } else
            {
                newHeight = (int) size;
                newWidth = options.outWidth * (int) size / options.outHeight;
            }
            bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            bm = scaleImage(bm, newWidth, newHeight);
            if (bm == null)
            {
                Log.e(TAG, "convertToThumb, decode fail:" + null);
                return null;
            }
            return bm;
        }
        // 缩放
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) reSize;
        bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
        if (bm == null)
        {
            Log.e(TAG, "convertToThumb, decode fail:" + null);
            return null;
        }
        return bm;
    }
    /**
     * 检查图片是否超过一定值，是则缩小
     *
     */
    public static Bitmap convertToThumb(byte[] buffer, float size)
    {
        // 获取原图宽度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
            options);
        // 计算缩放比例
        float reSize = options.outWidth / size;
        if (options.outWidth > options.outHeight)
        {
            reSize = options.outHeight / size;
        }
        if (reSize <= 0)
        {
            reSize = 1;
        }
        Log.d(TAG, "convertToThumb, reSize:" + reSize);
        // 缩放
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) reSize;
        if (bm != null && !bm.isRecycled())
        {
            bm.recycle();
            bm = null;
            Log.e(TAG, "convertToThumb, recyle");
        }
        bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
        if (bm == null)
        {
            Log.e(TAG, "convertToThumb, decode fail:" + null);
            return null;
        }
        return bm;
    }

    /**
     * 按新的宽高缩放图片
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight)
    {
        if (bm == null)
        {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
            true);
        if (bm != null & !bm.isRecycled())
        {
            bm.recycle();
            bm = null;
        }
        return newbm;
    }
    /**
     * fuction: 设置固定的宽度，高度随之变化，使图片不会变形
     *
     * @param target
     * 需要转化bitmap参数
     * @param newWidth
     * 设置新的宽度
     * @return
     */
    public static Bitmap fitBitmap(Bitmap target, int newWidth)
    {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        // float scaleHeight = ((float)newHeight) / height;
        int newHeight = (int) (scaleWidth * height);
        matrix.postScale(scaleWidth, scaleWidth);
        // Bitmap result = Bitmap.createBitmap(target,0,0,width,height,
        // matrix,true);
        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix,
            true);
        if (target != null && !target.equals(bmp) && !target.isRecycled())
        {
            target.recycle();
            target = null;
        }
        return bmp;// Bitmap.createBitmap(target, 0, 0, width, height, matrix,
        // true);
    }

    /**
     * 根据指定的宽度平铺图像
     *
     * @param width
     * @param src
     * @return
     */
    public static Bitmap createRepeater(int width, Bitmap src)
    {
        int count = (width + src.getWidth() - 1) / src.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(),
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx)
        {
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }
}

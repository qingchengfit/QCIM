package com.tencent.qcloud.timchat.chatmodel;

import android.content.Context;
import android.drm.DrmStore;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.MyApplication;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.adapters.ChatItem;
import com.tencent.qcloud.timchat.chatutils.FileUtil;
import com.tencent.qcloud.timchat.chatutils.MediaUtil;
import com.tencent.qcloud.timchat.common.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 语音消息数据
 */
public class VoiceMessage extends Message {

    private static final String TAG = "VoiceMessage";

    public VoiceMessage(TIMMessage message){
        this.message = message;
    }


    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param data 语音数据
     */
    public VoiceMessage(long duration,byte[] data){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setData(data);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     */
    public VoiceMessage(long duration,String filePath){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(ChatItem.ViewHolder viewHolder, Context context) {

//        tv.setText(String.valueOf(((TIMSoundElem) message.getElement(0)).getDuration()) + "’");
        clearView(viewHolder);
        LinearLayout linearLayout = new LinearLayout(MyApplication.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        ImageView voiceIcon = new ImageView(MyApplication.getContext());
        voiceIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        voiceIcon.setBackgroundResource(message.isSelf()?R.drawable.right_voice: R.drawable.left_voice);
        final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();

        int height = Util.dpToPx(18f, context.getResources());
        int width = Util.dpToPx(14f, context.getResources());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        voiceIcon.setLayoutParams(lp);
//        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        RelativeLayout layout = getBubbleView(viewHolder);
        linearLayout.setMinimumWidth(Util.dpToPx(64f, context.getResources()));
        int duration = (int)((TIMSoundElem) message.getElement(0)).getDuration();
        if (duration > 3){
            layout.getLayoutParams().width = Util.dpToPx(64f + (duration - 2) * 5, context.getResources());
            if (layout.getLayoutParams().width > Util.dpToPx(212f, context.getResources())){
                layout.getLayoutParams().width = Util.dpToPx(212f, context.getResources());
            }
        }
        if (!message.isSelf()){
//            layout.setPadding(layout.getPaddingLeft(), Util.dpToPx(9f, context.getResources()), 0, Util.dpToPx(9f, context.getResources()));
            viewHolder.leftMessage.setGravity(Gravity.LEFT);
            linearLayout.setGravity(Gravity.LEFT);
            linearLayout.addView(voiceIcon);
            viewHolder.leftMessage.addView(linearLayout);
        }else{
//            layout.setPadding(0, Util.dpToPx(9f, context.getResources()), layout.getPaddingRight(), Util.dpToPx(9f, context.getResources()));
            viewHolder.rightMessage.setGravity(Gravity.RIGHT);
            linearLayout.setGravity(Gravity.RIGHT);
            linearLayout.addView(voiceIcon);
            viewHolder.rightMessage.addView(linearLayout);
        }

//        clearView(viewHolder);
//        getBubbleView(viewHolder).addView(voiceIcon, lp);

        if (message.isSelf()){
            viewHolder.leftVoice.setVisibility(View.GONE);
            viewHolder.rightVoice.setVisibility(View.VISIBLE);
            viewHolder.rightVoice.setText(String.valueOf(((TIMSoundElem) message.getElement(0)).getDuration()) + "’");
        }else{
            viewHolder.leftVoice.setVisibility(View.VISIBLE);
            viewHolder.rightVoice.setVisibility(View.GONE);
            viewHolder.leftVoice.setText(String.valueOf(((TIMSoundElem) message.getElement(0)).getDuration()) + "’");
        }

        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceMessage.this.playAudio(frameAnimatio);

            }
        });
        showStatus(viewHolder);
    }

    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        if (MyApplication.getContext() != null) {
            return MyApplication.getContext().getString(R.string.summary_voice);
        }
        return "";
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private void playAudio(final AnimationDrawable frameAnimatio) {
        TIMSoundElem elem = (TIMSoundElem) message.getElement(0);

        elem.getSound(new TIMValueCallBack<byte[]>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(byte[] bytes) {
                try{
                    File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
                    FileOutputStream fos = new FileOutputStream(tempAudio);
                    fos.write(bytes);
                    fos.close();
                    FileInputStream fis = new FileInputStream(tempAudio);
                    MediaUtil.getInstance().play(fis);
                    frameAnimatio.start();
                    MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
                        @Override
                        public void onStop() {
                            frameAnimatio.stop();
                            frameAnimatio.selectDrawable(0);
                        }
                    });
                }catch (IOException e){

                }
            }
        });
    }
}

package com.tencent.qcloud.timchat.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.viewfeatures.ChatView;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面输入控件
 */
public class ChatInput extends RelativeLayout implements TextWatcher,View.OnClickListener {

    private static final String TAG = "ChatInput";

    private ImageButton btnAdd;
    private ImageView btnKeyboard, btnVoice;
    private EditText editText;
    private boolean isSendVisible,isHoldVoiceBtn;
    private InputMode inputMode = InputMode.NONE;
    private ChatView chatView;
    private LinearLayout morePanel,textPanel;
    private TextView voicePanel;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private TextView textSendPicture, textOpenCarmera, textSelectAlbum, textCancel;
    private AppCompatDialog dialog;
    private View inflate;

    public ChatInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.chat_input, this);
        initView();
    }

    private void initView(){
        textPanel = (LinearLayout) findViewById(R.id.text_panel);
        btnAdd = (ImageButton) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        btnVoice = (ImageView) findViewById(R.id.btn_voice);
        btnVoice.setVisibility(VISIBLE);
        btnVoice.setOnClickListener(this);
        morePanel = (LinearLayout) findViewById(R.id.morePanel);
        LinearLayout BtnImage = (LinearLayout) findViewById(R.id.btn_photo);
        BtnImage.setOnClickListener(this);
        LinearLayout BtnPhoto = (LinearLayout) findViewById(R.id.btn_image);
        BtnPhoto.setOnClickListener(this);
        LinearLayout btnVideo = (LinearLayout) findViewById(R.id.btn_video);
        btnVideo.setOnClickListener(this);
        LinearLayout btnFile = (LinearLayout) findViewById(R.id.btn_file);
        btnFile.setOnClickListener(this);
        //setSendBtn();
        btnKeyboard = (ImageView) findViewById(R.id.btn_keyboard);
        btnKeyboard.setOnClickListener(this);
        voicePanel = (TextView) findViewById(R.id.voice_panel);
        voicePanel.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldVoiceBtn = true;
                        updateVoiceView();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if ((event.getRawY() - y) < -50 ){
                            Toast.makeText(getContext(), "已取消", Toast.LENGTH_SHORT).show();
                        }
                        return true;
//                        break;
                    case MotionEvent.ACTION_UP:
                        isHoldVoiceBtn = false;
                        updateVoiceView();
                        break;
                }
                return true;
            }
        });
        editText = (EditText) findViewById(R.id.input);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    updateView(InputMode.TEXT);
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND){
                    chatView.sendText();
                }
                return true;
            }
        });
        editText.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if(morePanel.getVisibility() == VISIBLE){
                    updateView(InputMode.TEXT);
                }
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override public boolean onKey(View view, int keyboard, KeyEvent keyEvent) {
                if (keyboard == KeyEvent.KEYCODE_ENTER){
                    chatView.sendText();
                }
                return false;
            }
        });
        isSendVisible = editText.getText().length() != 0;

    }

    private void updateView(InputMode mode){
        if (mode == inputMode) return;
        leavingCurrentState();
        switch (inputMode = mode){
            case MORE:
                morePanel.setVisibility(VISIBLE);
                break;
            case TEXT:
                if (editText.requestFocus()){
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case VOICE:
                voicePanel.setVisibility(VISIBLE);
                textPanel.setVisibility(GONE);
                btnVoice.setVisibility(GONE);
                btnKeyboard.setVisibility(VISIBLE);
                break;
        }
    }

    private void leavingCurrentState(){
        switch (inputMode){
            case MORE:
                morePanel.setVisibility(GONE);
                break;
            case TEXT:
                View view = ((Activity) getContext()).getCurrentFocus();
                InputMethodManager imm= (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editText.clearFocus();
                break;
            case VOICE:
                voicePanel.setVisibility(GONE);
                textPanel.setVisibility(VISIBLE);
                btnVoice.setVisibility(VISIBLE);
                btnKeyboard.setVisibility(GONE);
                break;
        }
    }



    private void updateVoiceView(){
        if (isHoldVoiceBtn){
            voicePanel.setText(getResources().getString(R.string.chat_release_send));
            voicePanel.setBackground(getResources().getDrawable(R.drawable.btn_voice_pressed));
            chatView.startSendVoice();
        }else{
            voicePanel.setText(getResources().getString(R.string.chat_press_talk));
            voicePanel.setBackground(getResources().getDrawable(R.drawable.btn_voice_normal));
            chatView.endSendVoice();
        }
    }



    /**
     * 关联聊天界面逻辑
     */
    public void setChatView(ChatView chatView){
        this.chatView = chatView;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isSendVisible = s!=null&&s.length()>0;
        //setSendBtn();
        if (isSendVisible){
            chatView.sending();
        }
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     *
     * @param s
     */
    @Override
    public void afterTextChanged(Editable s) {

    }

    private void setSendBtn(){
        //if (isSendVisible){
        //    btnAdd.setVisibility(GONE);
        //}else{
        //    btnAdd.setVisibility(VISIBLE);
        //}
    }

//    private void prepareEmoticon(){
//        if (emoticonPanel == null) return;
//        for (int i = 0; i < 5; ++i){
//            LinearLayout linearLayout = new LinearLayout(getContext());
//            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
//            for (int j = 0;j < 7; ++j){
//
//                try{
//                    AssetManager am = getContext().getAssets();
//                    final int index = 7*i+j;
//                    InputStream is = am.open(String.format("emoticon/%d.gif", index));
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    Matrix matrix = new Matrix();
//                    int width = bitmap.getWidth();
//                    int height = bitmap.getHeight();
//                    matrix.postScale(3.5f, 3.5f);
//                    final Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
//                            width, height, matrix, true);
//                    ImageView image = new ImageView(getContext());
//                    image.setImageBitmap(resizedBitmap);
//                    image.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
//                    linearLayout.addView(image);
//                    image.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String content = String.valueOf(index);
//                            SpannableString str = new SpannableString(String.valueOf(index));
//                            ImageSpan span = new ImageSpan(getContext(), resizedBitmap, ImageSpan.ALIGN_BASELINE);
//                            str.setSpan(span, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            editText.append(str);
//                        }
//                    });
//                    is.close();
//                }catch (IOException e){
//
//                }
//
//            }
//            emoticonPanel.addView(linearLayout);
//        }
//        isEmoticonReady = true;
//    }

    private void initDialog(){
        dialog = new AppCompatDialog(getContext(),R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_select, null);
        //初始化控件
        textOpenCarmera = (TextView) inflate.findViewById(R.id.open_camera);
        textSelectAlbum = (TextView) inflate.findViewById(R.id.select_from_album);
        textCancel = (TextView) inflate.findViewById(R.id.text_cancel);

        textOpenCarmera.setOnClickListener(this);
        textSelectAlbum.setOnClickListener(this);
        textCancel.setOnClickListener(this);

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        dialog.show();//显示对话框
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Activity activity = (Activity) getContext();
        int id = v.getId();

        if (id == R.id.btn_add){
            if (dialog == null){
                initDialog();
            }else{
                dialog.show();
            }
        }

        if (id == R.id.open_camera){
            if(activity!=null && requestCamera(activity)){
                chatView.sendPhoto();
            }
        }
        if (id == R.id.select_from_album){
            if(activity!=null && requestStorage(activity)){
                chatView.sendImage();
            }
        }
        if (id == R.id.btn_keyboard){
            updateView(InputMode.TEXT);
        }
        if (id == R.id.text_cancel){
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
//        if (id == R.id.btn_voice){
//            if(activity!=null && requestAudio(activity)){
//                updateView(InputMode.VOICE);
//            }
//        }
//        if (id == R.id.btn_video){
//            if (getContext() instanceof FragmentActivity){
//                FragmentActivity fragmentActivity = (FragmentActivity) getContext();
//                if (requestVideo(fragmentActivity)){
//                    VideoInputDialog.show(fragmentActivity.getSupportFragmentManager());
//                }
//            }
//        }
////        if (id == R.id.btnEmoticon){
////            updateView(inputMode == InputMode.EMOTICON? InputMode.TEXT: InputMode.EMOTICON);
////        }
//        if (id == R.id.btn_file){
//            chatView.sendFile();
//        }
    }


    /**
     * 获取输入框文字
     */
    public Editable getText(){
        return editText.getText();
    }

    /**
     * 设置输入框文字
     */
    public void setText(String text){
        editText.setText(text);
    }


    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode){
        updateView(mode);
    }



    public enum InputMode{
        TEXT,
        VOICE,
        EMOTICON,
        MORE,
        VIDEO,
        NONE,
    }

    private boolean requestVideo(Activity activity){
        if (afterM()){
            final List<String> permissionsList = new ArrayList<>();
            if ((activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.CAMERA);
            if ((activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0){
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestCamera(Activity activity){
        if (afterM()){
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestAudio(Activity activity){
        if (afterM()){
            int hasPermission = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestStorage(Activity activity){
        if (afterM()){
            int hasPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean afterM(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}

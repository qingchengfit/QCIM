package com.tencent.qcloud.timchat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.Message;
import com.tencent.qcloud.timchat.common.AppData;
import com.tencent.qcloud.timchat.widget.CircleImageView;
import com.tencent.qcloud.timchat.widget.PhotoUtils;
import com.tencent.qcloud.tlslibrary.helper.Util;

import java.util.List;

/**
 * 聊天界面adapter
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private final String TAG = "ChatAdapter";

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;
    private String avatar;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ChatAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        if (!TextUtils.isEmpty(avatar)){
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
            viewHolder.leftMessage.setMinimumWidth(Util.dpTopx(64f, getContext().getResources()));
            viewHolder.rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
            viewHolder.rightMessage.setMinimumWidth(Util.dpTopx(64f, getContext().getResources()));
            viewHolder.leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            viewHolder.leftVoice = (TextView) view.findViewById(R.id.text_voice_time_left);
            viewHolder.leftVoice.setVisibility(View.GONE);
            viewHolder.rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            viewHolder.sending = (ProgressBar) view.findViewById(R.id.sending);
            viewHolder.error = (ImageView) view.findViewById(R.id.sendError);
            viewHolder.sender = (TextView) view.findViewById(R.id.sender);
            viewHolder.rightDesc = (TextView) view.findViewById(R.id.rightDesc);
            viewHolder.rightVoice = (TextView) view.findViewById(R.id.text_voice_time_right);
            viewHolder.leftVoice.setVisibility(View.GONE);
            viewHolder.systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            viewHolder.senderLayout = (RelativeLayout) view.findViewById(R.id.sendStatus);
            viewHolder.leftAvatar = (CircleImageView) view.findViewById(R.id.leftAvatar);
            viewHolder.rightAvatar = (CircleImageView) view.findViewById(R.id.rightAvatar);
            view.setTag(viewHolder);
        }

        Glide.with(viewHolder.rightAvatar.getContext())
                .load(PhotoUtils.getSmall(AppData.getAvatar(viewHolder.rightAvatar.getContext())))
                .asBitmap()
                .into(viewHolder.rightAvatar);
        Glide.with(viewHolder.leftAvatar.getContext())
                .load(PhotoUtils.getSmall(TextUtils.isEmpty(avatar) ? AppData.getAvatar(viewHolder.rightAvatar.getContext()) : avatar))
                .asBitmap()
                .into(viewHolder.leftAvatar);

        if (position < getCount()){
            final Message data = getItem(position);
            data.showMessage(viewHolder, getContext());
        }

        return view;
    }

    public class ViewHolder{
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public RelativeLayout senderLayout;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public TextView rightDesc;
        public TextView leftVoice;
        public TextView rightVoice;
        public CircleImageView leftAvatar;
        public CircleImageView rightAvatar;
    }
}

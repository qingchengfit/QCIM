package com.tencent.qcloud.timchat.ui.qcchat;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMGroupPendencyItem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.Conversation;
import com.tencent.qcloud.timchat.chatmodel.CustomMessage;
import com.tencent.qcloud.timchat.chatmodel.MessageFactory;
import com.tencent.qcloud.timchat.chatmodel.NomalConversation;
import com.tencent.qcloud.timchat.chatutils.PushUtil;
import com.tencent.qcloud.timchat.presenter.ConversationPresenter;
import com.tencent.qcloud.timchat.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.timchat.presenter.GroupManagerPresenter;
import com.tencent.qcloud.timchat.viewfeatures.ConversationView;
import com.tencent.qcloud.timchat.viewfeatures.FriendshipMessageView;
import com.tencent.qcloud.timchat.viewfeatures.GroupManageMessageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.DividerItemDecoration;

/**
 * 会话列表界面
 */
public class ConversationFragment extends Fragment implements ConversationView,
        FriendshipMessageView,GroupManageMessageView, AddConversationProcessor.OnCreateConversation,
        FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener{

    private final String TAG = "ConversationFragment";

    private View view;
    private List<ConversationFlexItem> flexItemList = new ArrayList<>();
    private FlexibleAdapter flexibleAdapter;
    private RecyclerView listView;
    private ConversationPresenter presenter;
    private GroupManagerPresenter groupManagerPresenter;
    private List<String> groupList;
    private AddConversationProcessor addConversationProcessor;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private OnUnReadMessageListener onUnReadMessageListener;

    public ConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_conversation, container, false);
            listView = (RecyclerView) view.findViewById(R.id.recyclerView);

            addConversationProcessor = new AddConversationProcessor(getContext());
            addConversationProcessor.setOnCreateConversation(this);
            friendshipManagerPresenter = new FriendshipManagerPresenter(this);
            groupManagerPresenter = new GroupManagerPresenter(this);
            presenter = new ConversationPresenter(this);
            presenter.getConversation();
            listView.setLayoutManager(new LinearLayoutManager(getContext()));
            listView.setItemAnimator(new DefaultItemAnimator());
            listView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.divider_card_list));
            listView.setNestedScrollingEnabled(false);
            flexibleAdapter = new FlexibleAdapter(flexItemList, this);
            flexibleAdapter.addListener(this);
            listView.setAdapter(flexibleAdapter);

            registerForContextMenu(listView);
        }
        flexibleAdapter.notifyDataSetChanged();
        return view;

    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
        PushUtil.getInstance().reset();
    }

    public void setOnUnReadMessageListener(OnUnReadMessageListener onUnReadMessageListener) {
        this.onUnReadMessageListener = onUnReadMessageListener;
    }

    /**
     * 初始化界面或刷新界面
     *
     * @param conversationList
     */
    @Override
    public void initView(List<TIMConversation> conversationList) {
        this.flexItemList.clear();
        groupList = new ArrayList<>();
        for (TIMConversation item:conversationList){
            if (item.getIdentifer().equals("新朋友")){
                continue;
            }
            switch (item.getType()){
                case C2C:
                case Group:
                    this.flexItemList.add(new ConversationFlexItem(getContext(), new NomalConversation(item)));
                    groupList.add(item.getPeer());
                    break;
            }
        }
        groupManagerPresenter.getGroupManageLastMessage();
    }

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    @Override
    public void updateMessage(TIMMessage message) {
        if (message == null){
            flexibleAdapter.notifyDataSetChanged();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System){
            groupManagerPresenter.getGroupManageLastMessage();
            return;
        }
        if (MessageFactory.getMessage(message) instanceof CustomMessage) return;
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<ConversationFlexItem> iterator =flexItemList.iterator();
        while (iterator.hasNext()){
            Conversation c = iterator.next().getConversation();
            if (conversation.equals(c)){
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        flexItemList.add(new ConversationFlexItem(getContext(), conversation));
        Collections.sort(flexItemList);
        refresh();
    }

    /**
     * 更新好友关系链消息
     */
    @Override
    public void updateFriendshipMessage() {
        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 删除会话
     *
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<ConversationFlexItem> iterator = flexItemList.iterator();
        while(iterator.hasNext()){
            Conversation conversation = iterator.next().getConversation();
            if (conversation.getIdentify()!=null&&conversation.getIdentify().equals(identify)){
                iterator.remove();
                flexibleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 更新群信息
     *
     * @param info
     */
    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
        for (ConversationFlexItem conversationItem : flexItemList){
            if (conversationItem.getConversation().getIdentify()!=null && conversationItem.getConversation()
                    .getIdentify().equals(info.getGroupInfo().getGroupId())){
                flexibleAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        if (onUnReadMessageListener != null){
            onUnReadMessageListener.onUnReadMessage(getTotalUnreadNum());
        }
        Collections.sort(flexItemList);
        flexibleAdapter.notifyDataSetChanged();
    }

    @Override
    public void createGroup(List<String> datas, List<String> memberList) {
        addConversationProcessor.createGroupWithArg(datas, "");

    }


    /**
     * 获取好友关系链管理系统最后一条消息的回调
     *
     * @param message 最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {
//        if (friendshipConversation == null){
//            friendshipConversation = new FriendshipConversation(message);
//            flexItemList.add(new ConversationFlexItem(getContext(), friendshipConversation));
//        }else{
//            friendshipConversation.setLastMessage(message);
//        }
//        friendshipConversation.setUnreadCount(unreadCount);
//        Collections.sort(flexItemList);
//        refresh();
    }

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     */
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
//        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 获取群管理最后一条系统消息的回调
     *
     * @param message     最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetGroupManageLastMessage(TIMGroupPendencyItem message, long unreadCount) {
//        if (groupManageConversation == null){
//            groupManageConversation = new GroupManageConversation(message);
////            conversationList.add(groupManageConversation);
//            flexItemList.add(new ConversationFlexItem(getContext(), groupManageConversation));
//        }else{
//            groupManageConversation.setLastMessage(message);
//        }
//        groupManageConversation.setUnreadCount(unreadCount);
//        Collections.sort(flexItemList);
//        refresh();
    }

    /**
     * 获取群管理系统消息的回调
     *
     * @param message 分页的消息列表
     */
    @Override
    public void onGetGroupManageMessage(List<TIMGroupPendencyItem> message) {

    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenu.ContextMenuInfo menuInfo) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        Conversation conversation = flexItemList.get(info.position).getConversation();
//        if (conversation instanceof NomalConversation){
//            menu.add(0, 1, Menu.NONE, getString(R.string.conversation_del));
//        }
//    }
//
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        ConversationFlexItem selectItem = flexItemList.get(info.position);
//        NomalConversation conversation = null;
//        if (selectItem != null) {
//            conversation = ((NomalConversation) selectItem.getConversation());
//        }
//        switch (item.getItemId()) {
//            case 1:
//                if (conversation != null){
//                    if (presenter.delConversation((conversation).getType(),
//                            conversation.getIdentify())){
//                        flexItemList.remove(selectItem);
//                        flexibleAdapter.notifyDataSetChanged();
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//        return super.onContextItemSelected(item);
//    }

    /**
     * @return 返回未读消息的总数
     */
    public long getTotalUnreadNum(){
        long num = 0;
        for (ConversationFlexItem item : flexItemList){
            num += item.getConversation().getUnreadNum();
        }
        return num;
    }

    /**
     * 设置全部已读
     */
    public void setAllMessageRead(){
        NomalConversation conversation;
        for(ConversationFlexItem item : flexItemList){
            conversation = (NomalConversation) item.getConversation();
            TIMManager.getInstance().getConversation(conversation.getType(),
                    conversation.getIdentify()).setReadMessage();
        }
    }

    /**
     *
     * @return  返回会话item的数量
     */
    public int getTotalItemCount(){
        return flexItemList.size();
    }

    @Override
    public void onCreateSuccess(String id) {
        if (getActivity() != null) {
            ChatActivity.navToChat(getActivity(), id, TIMConversationType.Group);
        }
    }

    @Override
    public void onCreateFailed(int errorCode, String s) {
        Toast.makeText(getContext(), "创建群组失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemClick(int position) {
        Conversation conversation = flexItemList.get(position).getConversation();
        conversation.navToDetail(getActivity());
//        if (flexItemList.get(position).getConversation() instanceof GroupManageConversation) {
//            groupManagerPresenter.getGroupManageLastMessage();
//        }
        return false;
    }

    public void deleteConversationItem(int position){
        ConversationFlexItem selectItem = flexItemList.get(position);
        NomalConversation conversation = null;
        if (selectItem != null) {
            conversation = ((NomalConversation) selectItem.getConversation());
        }
        if (conversation != null) {
            if (presenter.delConversation((conversation).getType(),
                    conversation.getIdentify())) {
                flexibleAdapter.removeItem(position);
                flexibleAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemLongClick(final int position) {
        onUnReadMessageListener.onLongClickListener(position);
    }

    public interface OnUnReadMessageListener{
        void onUnReadMessage(long count);

        void onLongClickListener(int position);
    }

}

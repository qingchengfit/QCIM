package com.tencent.qcloud.timchat.event;

import com.tencent.qcloud.timchat.ui.qcchat.ConversationBean;

import java.util.Observable;

/**
 * Created by fb on 2017/3/16.
 */

public class AddConversationEvent extends Observable {

    private AddConversationEvent(){
    }

    private static AddConversationEvent instance = new AddConversationEvent();

    public static AddConversationEvent getInstance(){
        return instance;
    }

    public void onAddNewConversation(ConversationBean bean){
        setChanged();
        notifyObservers(bean);
    }

}

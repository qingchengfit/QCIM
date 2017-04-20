//package com.tencent.qcloud.timchat.ui;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//
//import com.tencent.TIMGroupCacheInfo;
//import com.tencent.qcloud.timchat.R;
//import com.tencent.qcloud.timchat.adapters.ProfileSummaryItem;
//import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
//import com.tencent.qcloud.timchat.chatmodel.GroupProfile;
//import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;
//import com.tencent.qcloud.timchat.event.GroupEvent;
//import com.tencent.qcloud.timchat.widget.TemplateTitle;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Observable;
//import java.util.Observer;
//
//import eu.davidea.flexibleadapter.FlexibleAdapter;
//
//public class GroupListActivity extends Activity implements Observer, FlexibleAdapter.OnItemClickListener {
//
//    private RecyclerView listView;
//    private String type;
//    private List<ProfileSummary> list = new ArrayList<>();
//    private List<ProfileSummaryItem> itemList = new ArrayList<>();
//    private FlexibleAdapter flexibleAdapter;
//    private final int CREATE_GROUP_CODE = 100;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_group_list);
//        type = getIntent().getStringExtra("type");
//        setTitle();
//        listView =(RecyclerView) findViewById(R.id.recycler_member_list);
//        list = GroupInfo.getInstance().getGroupListByType(type);
//        for (ProfileSummary profileSummary : list){
////            itemList.add(new ProfileSummaryItem(getApplicationContext(), profileSummary));
//        }
//        flexibleAdapter = new FlexibleAdapter(itemList, this);
//        listView.setLayoutManager(new GridLayoutManager(this, 5));
//        listView.setAdapter(flexibleAdapter);
//
//        TemplateTitle title = (TemplateTitle) findViewById(R.id.groupListTitle);
//        title.setMoreTextAction(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(GroupListActivity.this, CreateGroupActivity.class);
//                intent.putExtra("type", type);
//                startActivityForResult(intent, CREATE_GROUP_CODE);
//            }
//        });
//        GroupEvent.getInstance().addObserver(this);
//
//    }
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        GroupEvent.getInstance().deleteObserver(this);
//    }
//
//    private void setTitle(){
//        TemplateTitle title = (TemplateTitle) findViewById(R.id.groupListTitle);
//        title.setTitleText(GroupInfo.getTypeName(type));
//        title.setMoreImgAction(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(GroupListActivity.this, CreateGroupActivity.class);
//                intent.putExtra("type", type);
//                startActivity(intent);
//            }
//        });
//    }
//
//
//    /**
//     * This method is called if the specified {@code Observable} object's
//     * {@code notifyObservers} method is called (because the {@code Observable}
//     * object has been updated.
//     *
//     * @param observable the {@link Observable} object.
//     * @param data       the data passed to {@link Observable#notifyObservers(Object)}.
//     */
//    @Override
//    public void update(Observable observable, Object data) {
//        if (observable instanceof GroupEvent){
//            if (data instanceof GroupEvent.NotifyCmd){
//                GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
//                switch (cmd.type){
//                    case DEL:
//                        delGroup((String) cmd.data);
//                        break;
//                    case ADD:
//                        addGroup((TIMGroupCacheInfo) cmd.data);
//                        break;
//                    case UPDATE:
//                        updateGroup((TIMGroupCacheInfo) cmd.data);
//                        break;
//                }
//            }
//        }
//    }
//
//    private void delGroup(String groupId){
//        Iterator<ProfileSummary> it = list.iterator();
//        while (it.hasNext()){
//            ProfileSummary item = it.next();
//            if (item.getIdentify().equals(groupId)){
//                it.remove();
//                flexibleAdapter.notifyDataSetChanged();
//                return;
//            }
//        }
//    }
//
//
//    private void addGroup(TIMGroupCacheInfo info){
//        if (info!=null && info.getGroupInfo().getGroupType().equals(type)){
//            GroupProfile profile = new GroupProfile(info);
//            list.add(profile);
//            flexibleAdapter.notifyDataSetChanged();
//        }
//
//    }
//
//    private void updateGroup(TIMGroupCacheInfo info){
//        delGroup(info.getGroupInfo().getGroupId());
//        addGroup(info);
//    }
//
//    @Override
//    public boolean onItemClick(int position) {
//        list.get(position).onClick(GroupListActivity.this);
//        return false;
//    }
//}

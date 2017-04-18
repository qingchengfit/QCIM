package com.tencent.qcloud.timchat.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.adapters.ProfileSummaryItem;
import com.tencent.qcloud.timchat.chatmodel.GroupMemberProfile;
import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;
import com.tencent.qcloud.timchat.presenter.GroupManagerPresenter;
import com.tencent.qcloud.timchat.ui.qcchat.DeleteMemberActivity;
import com.tencent.qcloud.timchat.widget.TemplateTitle;
import com.tencent.qcloud.tlslibrary.helper.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class GroupMemberActivity extends Activity implements TIMValueCallBack<List<TIMGroupMemberInfo>>, FlexibleAdapter.OnItemClickListener {

    List<GroupMemberProfile> list = new ArrayList<>();
    RecyclerView listView;
    TemplateTitle title;
    String groupId,type;
    private TextView tvTitle;
    private TextView btnExit;
    private RelativeLayout rlGroupName;
    private final int MEM_REQ = 100;
    private final int CHOOSE_MEM_CODE = 200;
    private int memIndex;
    private List<String> users = new ArrayList<>();
    private List<TIMGroupMemberInfo> infos = new ArrayList<>();
    private FlexibleAdapter flexibleAdapter;
    private List<ProfileSummaryItem> itemList = new ArrayList<>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        title = (TemplateTitle) findViewById(R.id.group_mem_title);
        groupId = getIntent().getStringExtra("identify");
        type = getIntent().getStringExtra("type");

        rlGroupName = (RelativeLayout) findViewById(R.id.set_group_name);
        tvTitle = (TextView) findViewById(R.id.tv_member_count);
        btnExit = (TextView) findViewById(R.id.btn_exit_group);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupMemberActivity.this);
                builder.setMessage("确定退出群聊？")
                        .setTitle("提示")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
                                    @Override
                                    public void onError(int i, String s) {
                                        Util.showToast(getApplicationContext(), s);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", null);

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.qc_text_grey));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.qc_text_grey));

            }
        });

        listView = (RecyclerView) findViewById(R.id.gridView_group_member);
        listView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        listView.setItemAnimator(new DefaultItemAnimator());
        flexibleAdapter = new FlexibleAdapter(itemList, this);
        listView.setAdapter(flexibleAdapter);

        TIMGroupManager.getInstance().getGroupMembers(groupId, this);

        rlGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupMemberActivity.this, SetGroupNameActivity.class);
                intent.putExtra("id", groupId);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        list.clear();
        if (timGroupMemberInfos == null) return;
        for (TIMGroupMemberInfo item : timGroupMemberInfos){
            users.add(item.getUser());
            infos.add(item);
        }
        TIMFriendshipManager.getInstance().getUsersProfile(users, new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                itemList.clear();
                int index = 0;
                for (TIMUserProfile profile : timUserProfiles) {
                    list.add(new GroupMemberProfile(profile));
                    itemList.add(new ProfileSummaryItem(getApplicationContext(), new GroupMemberProfile(profile)));
                    index++;
                }
                itemList.add(new ProfileSummaryItem(getApplicationContext(), new GroupMemberProfile(GroupMemberProfile.ADD)));
                itemList.add(new ProfileSummaryItem(getApplicationContext(), new GroupMemberProfile(GroupMemberProfile.REMOVE)));
                flexibleAdapter.notifyDataSetChanged();
                tvTitle.setText(getApplicationContext().getString(R.string.title_group_member_count, list.size()));
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (MEM_REQ == requestCode) {
            if (resultCode == RESULT_OK){
                boolean isKick = data.getBooleanExtra("isKick", false);
                if (isKick){
                    list.remove(memIndex);
                    flexibleAdapter.notifyDataSetChanged();
                }else{
                    GroupMemberProfile profile = (GroupMemberProfile) data.getSerializableExtra("data");
                    if (memIndex < list.size() && list.get(memIndex).getIdentify().equals(profile.getIdentify())){
                        GroupMemberProfile mMemberProfile = (GroupMemberProfile) list.get(memIndex);
                        mMemberProfile.setRoleType(profile.getRole());
                        mMemberProfile.setQuietTime(profile.getQuietTime());
                        mMemberProfile.setName(profile.getNameCard());
                        flexibleAdapter.notifyDataSetChanged();
                    }
                }
            }
        }else if (CHOOSE_MEM_CODE == requestCode){
            if (resultCode == RESULT_OK){
                GroupManagerPresenter.inviteGroup(groupId, data.getStringArrayListExtra("select"),
                        new TIMValueCallBack<List<TIMGroupMemberResult>>() {
                            @Override
                            public void onError(int i, String s) {
                                Toast.makeText(GroupMemberActivity.this, getString(R.string.chat_setting_invite_error), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                                TIMGroupManager.getInstance().getGroupMembers(groupId, GroupMemberActivity.this);
                            }
                        });

            }
        }else if (requestCode == 0){
            if (resultCode == RESULT_OK){
                TIMGroupManager.getInstance().getGroupMembers(groupId, this);
            }
        }
    }


    @Override
    public boolean onItemClick(int position) {
        GroupMemberProfile groupMemberProfile = itemList.get(position).getData();
        if (groupMemberProfile.getType() == GroupMemberProfile.NORMAL) {
            memIndex = position;
            Intent intent = new Intent(GroupMemberActivity.this, GroupMemberProfileActivity.class);
            GroupMemberProfile profile = (GroupMemberProfile) list.get(position);
            intent.putExtra("data", profile);
            intent.putExtra("groupId", groupId);
            intent.putExtra("type", type);
            startActivityForResult(intent, MEM_REQ);
        }else if (groupMemberProfile.getType() == GroupMemberProfile.REMOVE){
            Intent intent = new Intent(GroupMemberActivity.this, DeleteMemberActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("member", (Serializable) list);
            intent.putExtra("datas", b);
            intent.putExtra("group", groupId);
            startActivityForResult(intent, 0);
        }else{
            String packageName = getApplication().getPackageName();
            Intent intent = new Intent();
            if (packageName.substring(17,packageName.length()).equals("staffkit")){

            }else{

            }
        }
        return false;
    }
}

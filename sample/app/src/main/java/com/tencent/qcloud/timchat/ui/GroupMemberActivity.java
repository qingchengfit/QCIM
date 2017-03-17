package com.tencent.qcloud.timchat.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.adapters.ProfileSummaryAdapter;
import com.tencent.qcloud.timchat.chatmodel.GroupMemberProfile;
import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;
import com.tencent.qcloud.timchat.presenter.GroupManagerPresenter;
import com.tencent.qcloud.timchat.widget.TemplateTitle;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberActivity extends Activity implements TIMValueCallBack<List<TIMGroupMemberInfo>> {

    ProfileSummaryAdapter adapter;
    List<ProfileSummary> list = new ArrayList<>();
    GridView listView;
    TemplateTitle title;
    String groupId,type;
    private TextView tvTitle;
    private TextView btnExit;
    private RelativeLayout rlGroupName;
    private final int MEM_REQ = 100;
    private final int CHOOSE_MEM_CODE = 200;
    private int memIndex;

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

        listView = (GridView) findViewById(R.id.gridView_group_member);
        adapter = new ProfileSummaryAdapter(this, R.layout.item_group_detail_grid, list);
        listView.setAdapter(adapter);

        TIMGroupManager.getInstance().getGroupMembers(groupId, this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                memIndex = position;
                Intent intent = new Intent(GroupMemberActivity.this, GroupMemberProfileActivity.class);
                GroupMemberProfile profile = (GroupMemberProfile) list.get(position);
                intent.putExtra("data", profile);
                intent.putExtra("groupId", groupId);
                intent.putExtra("type",type);
                startActivityForResult(intent, MEM_REQ);
            }
        });

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
            list.add(new GroupMemberProfile(item));
        }
        adapter.notifyDataSetChanged();
        tvTitle.setText(getApplicationContext().getString(R.string.title_group_member_count, list.size()));

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (MEM_REQ == requestCode) {
            if (resultCode == RESULT_OK){
                boolean isKick = data.getBooleanExtra("isKick", false);
                if (isKick){
                    list.remove(memIndex);
                    adapter.notifyDataSetChanged();
                }else{
                    GroupMemberProfile profile = (GroupMemberProfile) data.getSerializableExtra("data");
                    if (memIndex < list.size() && list.get(memIndex).getIdentify().equals(profile.getIdentify())){
                        GroupMemberProfile mMemberProfile = (GroupMemberProfile) list.get(memIndex);
                        mMemberProfile.setRoleType(profile.getRole());
                        mMemberProfile.setQuietTime(profile.getQuietTime());
                        mMemberProfile.setName(profile.getNameCard());
                        adapter.notifyDataSetChanged();
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
        }
    }




}

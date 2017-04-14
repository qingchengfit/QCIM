package com.tencent.qcloud.timchat.ui.qcchat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberResult;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.adapters.ProfileSummaryItem;
import com.tencent.qcloud.timchat.chatmodel.ProfileSummary;
import com.tencent.qcloud.timchat.widget.TemplateTitle;
import com.tencent.qcloud.tlslibrary.helper.Util;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

/**
 * Created by fb on 2017/4/13.
 */

public class DeleteMemberActivity extends Activity implements ProfileSummaryItem.OnDeleteMemberListener {
    private List<ProfileSummary> dataList = new ArrayList<>();
    private List<ProfileSummaryItem> itemList = new ArrayList<>();
    private RecyclerView gridView;
    private TemplateTitle title;
    private List<String> deleteList = new ArrayList<>();
    private String groupId;
    private FlexibleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_member);

        title = (TemplateTitle) findViewById(R.id.toolbar_title);
        gridView = (RecyclerView) findViewById(R.id.grid_delete_member);

        setToolbar();

        if(getIntent() != null && getIntent().getBundleExtra("datas") != null){
            dataList = (List<ProfileSummary>) getIntent().getBundleExtra("datas").getSerializable("member");
            groupId = getIntent().getStringExtra("group");
        }

        for (ProfileSummary profileSummary : dataList){
            ProfileSummaryItem item = new ProfileSummaryItem(getApplicationContext(), profileSummary);
            item.setOnDeleteMemberListener(this);
            item.setDelete(true);
            itemList.add(item);
        }

        adapter = new FlexibleAdapter(itemList);

        gridView.setLayoutManager(new GridLayoutManager(this, 5));
        gridView.setItemAnimator(new DefaultItemAnimator());
        gridView.setAdapter(adapter);
    }

    private void setToolbar(){
        title.setLeftTxt("取消 ");
        title.setMoreTextContext("完成");
        title.setMoreTextAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteList.size() > 0){
                    TIMGroupManager.getInstance().deleteGroupMember(groupId, deleteList, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
                        @Override
                        public void onError(int i, String s) {
                            Util.showToast(getApplicationContext(), "移出群成员失败，请重试");
                        }

                        @Override
                        public void onSuccess(List<TIMGroupMemberResult> timGroupMemberResults) {
                            Util.showToast(getApplicationContext(), "移出群成员成功");
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }else{
                    finish();
                }
            }
        });
    }

    @Override
    public void onDelete(String position) {
        adapter.removeItem(Integer.valueOf(position));
        deleteList.add(position);
    }
}

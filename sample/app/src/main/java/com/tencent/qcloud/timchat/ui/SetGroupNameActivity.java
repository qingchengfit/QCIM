package com.tencent.qcloud.timchat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.widget.TemplateTitle;

/**
 * Created by fb on 2017/3/16.
 */

public class SetGroupNameActivity extends FragmentActivity {

    private EditText editGroupName;
    private String groupId;
    TemplateTitle title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_group_name);
        if (getIntent() != null) {
            groupId = getIntent().getStringExtra("id");
        }
        editGroupName = (EditText) findViewById(R.id.edit_group_name);
        title = (TemplateTitle) findViewById(R.id.chat_title);

        title.setMoreTextContext("保存");
        title.setMoreTextAction(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TIMGroupManager.getInstance().modifyGroupName(groupId, editGroupName.getText().toString(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }



}

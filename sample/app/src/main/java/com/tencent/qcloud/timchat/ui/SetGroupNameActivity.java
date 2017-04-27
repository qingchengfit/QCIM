package com.tencent.qcloud.timchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.qcloud.timchat.R;
import com.tencent.qcloud.timchat.chatmodel.GroupInfo;
import com.tencent.qcloud.timchat.widget.TemplateTitle;

/**
 * Created by fb on 2017/3/16.
 */

public class SetGroupNameActivity extends FragmentActivity {

    private EditText editGroupName;
    private String groupId;
    private TemplateTitle title;
    private ImageView clear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_group_name);
        if (getIntent() != null) {
            groupId = getIntent().getStringExtra("id");
        }
        editGroupName = (EditText) findViewById(R.id.edit_group_name);
        editGroupName.setText(GroupInfo.getInstance().getGroupName(groupId));
        title = (TemplateTitle) findViewById(R.id.group_mem_title);
        clear = (ImageView) findViewById(R.id.image_clear);

        clear.setVisibility(View.GONE);
        editGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0){
                    clear.setVisibility(View.VISIBLE);
                }else{
                    clear.setVisibility(View.VISIBLE);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGroupName.setText("");
            }
        });

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
                        Intent intent = new Intent();
                        intent.putExtra("groupName", editGroupName.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });

    }



}

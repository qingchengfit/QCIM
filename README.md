# QCIM
使用说明

登录

第一步
new LoginProcessor(Context context, String username, String password);

第二步： 
实现OnLoginListener


设置昵称 
loginProcessor.setUserInfo(String username, String avatarUrl);


添加会话
AddConversationProcessor ac = new AddConversationProcessor(context);
ac.createGroupWithArg(List<String> datas, final String avatarUrl);
回调：OnCreateConversation


获取未读消息数量
OnUnReadMessageListener.onUnReadMessage(long count);
=======
1# QCIM
qingchengIm
>>>>>>> Stashed changes

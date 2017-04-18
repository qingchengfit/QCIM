## 使用说明

- 初始化设置 **（一定要先设置）**

```
//设置AccountType
Constant.setAccountType(int AccountType);  

//设置SDKAppID
COnstant.setAppId(int SDKAppId);

//设置全局Context
MyApplication myApplication = new MyApplication(getApplication());
```

- 登录

```java
new LoginProcessor(Context context, String username, String host, OnLoginListener listener);

loginProcessor.sientInstall();
```

*参数说明* : 

**username:** userId, 一般为  **自定义字段 + userID**;

**host:** 当前服务端环境的host地址，如：c1.qingchengfit.cn。

**listener**: 登录的回调 *OnLoginListener*。

- 清除用户信息

```java
AppData.clear(Context context);
```

- 设置用户信息

```·
loginProcessor.setUserInfo(String username, String avatarUrl);
```

- 添加会话（单聊／群聊）

```Java
AddConversationProcessor ac = new AddConversationProcessor(context); ac.createGroupWithName(List datas, final String avatarUrl);
```

*添加成功的回调*： OnCreateConversation

- 会话消息与会话总数：

```java
//当有新消息时返回会话总数
OnUnReadMessageListener.onUnReadMessage(long count);
//设置当前所有会话消息为已读
ConversationFragment.setAllMessageRead();
//获取当前会话未读消息总数
ConversationFragment.getTotalUnreadNum();
//获取会话列表item总数
ConversationFragment.getTotalItemCount
```
- 会话列表item长按选项

```java
OnUnReadMessageListener.onLongClickListener(int position);

//删除某个位置的item的方法
conversationFragment.deleteConversationItem(int position)
```
- 判断登录状态

```java
LoginProcessor.isLogin();	 //return type boolean
```
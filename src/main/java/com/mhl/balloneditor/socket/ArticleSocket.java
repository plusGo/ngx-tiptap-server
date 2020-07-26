package com.mhl.balloneditor.socket;


import com.alibaba.fastjson.JSONObject;
import com.mhl.balloneditor.model.Article;
import com.mhl.balloneditor.response.ArticleSocketResponse;
import com.mhl.balloneditor.util.JsonUtils;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/article/{draftId}")
@Component
public class ArticleSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    private static int version = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的ArticleSocket对象。
    private static ConcurrentHashMap<String, ArticleSocket> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收sid
    private String sid = "";

    /**
     * 接收userId
     */
    private String draftId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("draftId") String draftId) {
        this.session = session;
        this.draftId = draftId;
        if (webSocketMap.containsKey(draftId)) {
            webSocketMap.remove(draftId);
            webSocketMap.put(draftId, this);
            //加入set中
        } else {
            webSocketMap.put(draftId, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        System.out.println("用户连接:" + draftId + ",当前在线人数为:" + getOnlineCount());

        try {
            final ArticleSocketResponse init = new ArticleSocketResponse("init", JsonUtils.writeValue(new Article("{}", "{}", version)));
            sendMessage(JsonUtils.writeValue(init));
        } catch (IOException e) {
            System.out.println("用户:" + draftId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(draftId)) {
            webSocketMap.remove(draftId);
            //从set中删除
            subOnlineCount();
        }
        System.out.println("用户退出:" + draftId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("用户消息:" + draftId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (message == null) {
            return;
        }
        try {
            final Data postData = JsonUtils.readValue(message, Data.class);

            //解析发送的报文
            JSONObject jsonObject = JSONObject.parseObject(message);
            int remoteVersion = jsonObject.getInteger("version");
            if (remoteVersion != version) {
                return;
            }
            remoteVersion++;
            jsonObject.put("version", remoteVersion);

            webSocketMap.forEach((k, v) -> {
                try {
                    final HashMap<Object, Object> data = new HashMap<>();
                    data.put("update", message);

                    final ArticleSocketResponse update = new ArticleSocketResponse("update", jsonObject);
                    v.sendMessage(JsonUtils.writeValue(update));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            //追加发送人(防止串改)
            jsonObject.put("fromUserId", this.draftId);
            String toUserId = jsonObject.getString("toUserId");
            //传送给对应toUserId用户的websocket
            if (toUserId != null && webSocketMap.containsKey(toUserId)) {
                webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
            } else {
                System.out.println("请求的userId:" + toUserId + "不在该服务器上");
                //否则不在这个服务器上，发送到mysql或者redis
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("用户错误:" + this.draftId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {

        System.out.println("发送消息到:" + userId + "，报文:" + message);
        if (userId != null && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            System.out.println("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ArticleSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ArticleSocket.onlineCount--;
    }

}

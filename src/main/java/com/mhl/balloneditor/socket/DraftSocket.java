package com.mhl.balloneditor.socket;


import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/draft/{clientId}")
@Component
public class DraftSocket {
    private Session session;
    private String clientId;
    private static ConcurrentHashMap<String, DraftSocket> webSocketMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) {
        this.session = session;
        this.clientId = clientId;
        webSocketMap.remove(clientId);
        webSocketMap.put(clientId, this);

        System.out.println(String.format("客户端链接成功，clientId:%s", clientId));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketMap.remove(clientId);
        System.out.println(String.format("客户端断开链接成功，clientId:%s", clientId));
    }

    @OnMessage
    public void onMessage(String data, Session session) {
    }


}

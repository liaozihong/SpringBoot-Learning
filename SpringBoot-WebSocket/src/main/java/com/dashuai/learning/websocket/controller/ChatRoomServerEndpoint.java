package com.dashuai.learning.websocket.controller;

import com.dashuai.learning.websocket.enums.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import static com.dashuai.learning.websocket.utils.WebSocketUtils.*;

/**
 * Chat room server endpoint
 * <p/>
 * Created in 2018.12.04
 * <p/>
 * 服务端点，同@RequestMapping类似
 *
 * @author Liaozihong
 */
@RestController
@ServerEndpoint("/chat-room/{username}")
public class ChatRoomServerEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ChatRoomServerEndpoint.class);

    /**
     * Open session.
     * 连接服务端，打开session
     *
     * @param username the 用户名
     * @param session  the 会话
     */
    @OnOpen
    public void openSession(@PathParam("username") String username, Session session) {
        LIVING_SESSIONS_CACHE.put(username, session);
        String message = "欢迎用户[" + username + "] 来到聊天室！";
        log.info(message);
        sendMessageAll(message);
        sendAllUser();
    }

    /**
     * On message.
     * 向客户端推送消息
     *
     * @param username the 用户名
     * @param message  the 消息
     */
    @OnMessage
    public void onMessage(@PathParam("username") String username, String message) {
        log.info("{}发送消息：{}", username, message);
        sendMessageAll("用户[" + username + "] : " + message);
    }

    /**
     * On close.
     * 连接关闭
     *
     * @param username the 用户名
     * @param session  the 会话
     */
    @OnClose
    public void onClose(@PathParam("username") String username, Session session) {
        //当前的Session 移除
        LIVING_SESSIONS_CACHE.remove(username);
        //并且通知其他人当前用户已经离开聊天室了
        sendMessageAll("用户[" + username + "] 已经离开聊天室了！");
        sendAllUser();
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * On error.
     * 出现错误
     *
     * @param session   the session
     * @param throwable the throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }


    /**
     * On message.
     * 点到点推送消息
     *
     * @param sender  the 发送至
     * @param receive the 接受者
     * @param message the 消息
     */
    @GetMapping("/chat-room/{sender}/to/{receive}")
    public void onMessage(@PathVariable("sender") String sender, @PathVariable("receive") String receive, String message) {
        sendMessage(LIVING_SESSIONS_CACHE.get(receive), MessageType.MESSAGE,
                "[" + sender + "]" + "-> [" + receive + "] : " + message);
        log.info("[" + sender + "]" + "-> [" + receive + "] : " + message);
    }

}

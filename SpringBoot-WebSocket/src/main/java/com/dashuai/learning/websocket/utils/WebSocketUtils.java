package com.dashuai.learning.websocket.utils;

import com.dashuai.learning.utils.json.JSONParseUtils;
import com.dashuai.learning.utils.result.ApiResult;
import com.dashuai.learning.websocket.enums.MessageType;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Web socket utils
 * <p/>
 * Created in 2018.12.03
 * <p/>
 * 推送消息工具类
 *
 * @author Liaozihong
 */
public final class WebSocketUtils {

    /**
     * 模拟存储 websocket session 使用
     */
    public static final Map<String, Session> LIVING_SESSIONS_CACHE = new ConcurrentHashMap<>();

    /**
     * Send 消息至客户端
     *
     * @param message the message
     */
    public static void sendMessageAll(String message) {
        LIVING_SESSIONS_CACHE.forEach((sessionId, session) -> sendMessage(session, MessageType.MESSAGE, message));
    }

    /**
     * Send 所有用户名至客户端
     */
    public static void sendAllUser() {
        LIVING_SESSIONS_CACHE.forEach((sessionId, session) -> sendMessage(session,
                MessageType.USERNAME, LIVING_SESSIONS_CACHE.keySet()));
    }

    /**
     * 发送给指定用户消息
     *
     * @param session 用户 session
     * @param type    the type
     * @param message 发送内容
     */
    public static void sendMessage(Session session, MessageType type, Object message) {
        if (session == null) {
            return;
        }
        final RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (basic == null) {
            return;
        }
        try {
            String data = JSONParseUtils.object2JsonString(ApiResult.prepare().success(message, 200, type.getValue()));
            //向session推送数据
            basic.sendText(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

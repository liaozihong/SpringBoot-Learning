package com.dashuai.learning.websocket.enums;

/**
 * The enum Message type.
 * 推送到客户端的消息类型
 *
 * @author Liaozihong
 */
public enum MessageType {


    /**
     * 用户名.
     */
    USERNAME("username"),

    /**
     * 普通消息.
     */
    MESSAGE("message");

    private String value;


    public String getValue() {
        return value;
    }

    MessageType(String value) {
        this.value = value;
    }
}

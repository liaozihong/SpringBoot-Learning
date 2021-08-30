package com.dashuai.learning.kafka.model;

import java.util.Date;

/**
 * Message
 * <p/>
 * Created in 2018.12.25
 * <p/>
 *
 * @author Liaozihong
 */
public class Message {
    private Integer id;
    private String msg;
    private Date sendTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}

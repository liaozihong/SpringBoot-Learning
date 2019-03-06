package com.dashuai.learning.nsq.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created in 2019.03.05
 *
 * @author Liaozihong
 */
public class NsqMessage implements Serializable {

    @SerializedName("id")
    @JsonProperty("id")
    private Long id;

    /**
     * 相当于nsq消费者的channel名称
     */
    @SerializedName("action")
    @JsonProperty("action")
    private String action;

    @SerializedName("body")
    @JsonProperty("body")
    private String body;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets action.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets action.
     *
     * @param action the action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets body.
     *
     * @param body the body
     */
    public void setBody(String body) {
        this.body = body;
    }
}

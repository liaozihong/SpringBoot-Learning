package com.dashuai.learning.elasticsearch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * User
 * <p/>
 * Created in 2018.11.29
 * <p/>
 * indexName 配置必须是全部小写，不然会出异常。
 *
 * @author Liaozihong
 */
@Document(indexName = "springboot-curd", type = "user")
public class User {
    @Id
    private Integer id;

    @Field(searchAnalyzer = "ik_max_word", analyzer = "ik_smart", type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "date_optional_time")
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }
}

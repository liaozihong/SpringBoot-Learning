package com.dashuai.learnin.elasticsearchadvance.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;

/**
 * 设置setting模板和mapping映射
 * 注意路径记得对应好
 *
 * @author Liaozihong
 */
@Document(indexName = "film-entity", type = "film")
@Setting(settingPath = "/json/film-setting.json")
@Mapping(mappingPath = "/json/film-mapping.json")
public class FilmEntity {

    @Id
    private Long id;
    //    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word", analyzer = "ik_smart")
    private String name;
    private String nameOri;
    private String publishDate;
    private String type;
    private String language;
    private String fileDuration;
    private String director;
    //    @Field(type = FieldType.Date)
    private Date created;

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
     * Gets name ori.
     *
     * @return the name ori
     */
    public String getNameOri() {
        return nameOri;
    }

    /**
     * Sets name ori.
     *
     * @param nameOri the name ori
     */
    public void setNameOri(String nameOri) {
        this.nameOri = nameOri;
    }

    /**
     * Gets publish date.
     *
     * @return the publish date
     */
    public String getPublishDate() {
        return publishDate;
    }

    /**
     * Sets publish date.
     *
     * @param publishDate the publish date
     */
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets language.
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets language.
     *
     * @param language the language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Gets file duration.
     *
     * @return the file duration
     */
    public String getFileDuration() {
        return fileDuration;
    }

    /**
     * Sets file duration.
     *
     * @param fileDuration the file duration
     */
    public void setFileDuration(String fileDuration) {
        this.fileDuration = fileDuration;
    }

    /**
     * Gets director.
     *
     * @return the director
     */
    public String getDirector() {
        return director;
    }

    /**
     * Sets director.
     *
     * @param director the director
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Gets created.
     *
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Sets created.
     *
     * @param created the created
     */
    public void setCreated(Date created) {
        this.created = created;
    }

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

    @Override
    public String toString() {
        return "FilmEntity [id=" + id + ", name=" + name + ", director=" + director + "]";
    }
}
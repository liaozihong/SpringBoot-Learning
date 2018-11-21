package com.dashuai.learning.fastdsf.fastdfs;

/**
 * Fast dfs file
 * <p/>
 * Created in 2018.11.21
 * <p/>
 *
 * @author Liaozihong
 */
public class FastDFSFile {
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件内容
     */
    private byte[] content;
    /**
     * 文件类型
     */
    private String ext;

    private String md5;
    /**
     * 作者
     */
    private String author;

    /**
     * Instantiates a new Fast dfs file.
     *
     * @param name    the name
     * @param content the content
     * @param ext     the ext
     * @param height  the height
     * @param width   the width
     * @param author  the author
     */
    public FastDFSFile(String name, byte[] content, String ext, String height,
                       String width, String author) {
        super();
        this.name = name;
        this.content = content;
        this.ext = ext;
        this.author = author;
    }

    /**
     * Instantiates a new Fast dfs file.
     *
     * @param name    the name
     * @param content the content
     * @param ext     the ext
     */
    public FastDFSFile(String name, byte[] content, String ext) {
        super();
        this.name = name;
        this.content = content;
        this.ext = ext;

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
     * Get content byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Gets ext.
     *
     * @return the ext
     */
    public String getExt() {
        return ext;
    }

    /**
     * Sets ext.
     *
     * @param ext the ext
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    /**
     * Gets md 5.
     *
     * @return the md 5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Sets md 5.
     *
     * @param md5 the md 5
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets author.
     *
     * @param author the author
     */
    public void setAuthor(String author) {
        this.author = author;
    }
}
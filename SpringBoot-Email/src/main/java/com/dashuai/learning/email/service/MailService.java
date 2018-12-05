package com.dashuai.learning.email.service;

import org.thymeleaf.context.Context;

/**
 * The interface Mail service.
 * 发送邮件服务
 *
 * @author Liaozihong
 */
public interface MailService {
    /**
     * Send simple mail.
     *
     * @param to      the 发给谁
     * @param subject the 标题
     * @param content the 邮件内容
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * Send html mail.
     *
     * @param to      the 发给谁
     * @param subject the 邮件标题
     * @param content the html内容
     */
    void sendHtmlMail(String to, String subject, String content);

    /**
     * Send attachments mail.
     * 带附件
     *
     * @param to       the 发给谁
     * @param subject  the 邮件标题
     * @param content  the 邮件内容
     * @param filePath the 附件路径
     */
    void sendAttachmentsMail(String to, String subject, String content, String filePath);

    /**
     * 携带静态资源
     *
     * @param to      the 发给谁
     * @param subject the 邮件标题
     * @param content the 邮件内容
     * @param rscPath the 资源路径
     * @param rscId   the 资源id
     */
    void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId);

    /**
     * 发送模板邮件.
     *
     * @param to       the 发给谁
     * @param subject  the 标题
     * @param template the 邮件模板名称
     * @param context  the 模板所需参数
     */
    void sendTemplateMail(String to, String subject, String template, Context context);
}

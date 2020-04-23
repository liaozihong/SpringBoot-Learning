package com.dashuai.learning.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Request url builder
 * <p/>
 * Created in 2018.12.30
 * <p/>
 *
 * @author Liaozihong
 */
public class RequestUrlBuilder {

    /**
     * 构造请求参数.
     *
     * @param params the 参数
     * @return the string 转化后的Query查询参数
     */
    public static String buildQuery(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder query = new StringBuilder();
        boolean hasParams = false;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (Strings.areNotNullOrEmpty(entry.getKey())) {
                    if (hasParams) {
                        query.append("&");
                    } else {
                        hasParams = true;
                    }
                    query.append(entry.getKey()).append("=").append(URLEncoder.encode(
                            entry.getValue(), "UTF-8"));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return query.toString();
    }

    /**
     * 构造完整url.
     *
     * @param url    the url
     * @param params the 参数
     * @return the string 转化后的url
     * @throws IOException the io exception
     */
    public static String buildRequestUrl(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        // 如果以/结果,去掉/
        if (url.endsWith("/")) {
            builder.append(url, 0, url.length() - 1);
        } else {
            builder.append(url);
        }
        if (!(url.endsWith("?") || url.endsWith("&"))) {
            if (url.contains("?")) {
                builder.append("&");
            } else {
                builder.append("?");
            }
        }
        return builder.append(buildQuery(params)).toString();
    }
}

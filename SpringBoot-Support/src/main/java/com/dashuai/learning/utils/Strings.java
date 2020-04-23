package com.dashuai.learning.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Strings
 * <p/>
 * Created in 2018.09.11
 * <p/>
 *
 * @author Liaozihong
 */
public class Strings {

    /**
     * Is null or empty boolean.
     * 是否为空
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Null to empty string.
     * 是否为null，是转成""
     *
     * @param str the str
     * @return the string
     */
    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * Empty to null string.
     * 是否为null或为""
     *
     * @param str the str
     * @return the string
     */
    public static String emptyToNull(String str) {
        if (str == null) {
            return null;
        } else {
            return str.isEmpty() ? null : str;
        }
    }

    /**
     * 将inpustStream转成String类型
     *
     * @param inputStream the input stream
     * @return the string
     * @throws IOException the io exception
     */
    public static String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Are not empty boolean.
     * 判断多个参数是否有为null的
     *
     * @param values the values
     * @return the boolean
     */
    public static boolean areNotNullOrEmpty(String... values) {
        boolean result = true;
        if (values == null || values.length == 0) {
            result = false;
        } else {
            for (String value : values) {
                result &= !isNullOrEmpty(value);
            }
        }
        return result;
    }
}

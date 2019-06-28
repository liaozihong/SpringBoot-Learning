package com.dashuai.learning.oauth2code.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * In memory qq database
 * <p/>
 * Created in 2019.05.14
 * <p/>
 *
 * @author Liaozihong
 */
public class InMemoryQQDatabase {

    /**
     * The Database.
     */
    public static Map<String, QQAccount> database;

    static {
        database = new HashMap<>();
        database.put("250577914", QQAccount.builder().qq("250577914").nickName("鱼非渔").level("54").build());
        database.put("666666", QQAccount.builder().qq("666666").nickName("下一秒升华").level("31").build());

        QQAccount qqAccount1 = database.get("250577914");
        qqAccount1.setFans(new ArrayList<>());
        for (int i = 0; i < 5; i++) {
            qqAccount1.getFans().add(QQAccount.builder().qq("1000000" + i).nickName("fan" + i).level(i + "").build());
        }

        QQAccount qqAccount2 = database.get("666666");
        qqAccount2.setFans(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            qqAccount2.getFans().add(QQAccount.builder().qq("2000000" + i).nickName("fan" + i).level(i + "").build());
        }
    }

}

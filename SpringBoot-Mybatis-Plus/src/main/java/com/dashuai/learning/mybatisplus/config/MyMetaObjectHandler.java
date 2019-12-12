package com.dashuai.learning.mybatisplus.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created in 2019.12.12
 * 配置自动填充
 *
 * @author Liaozihong
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        LOGGER.info("start insert fill ....");
        this.setFieldValByName("createAt", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LOGGER.info("start update  fill ....");
        this.setFieldValByName("updateAt", new Date(), metaObject);
    }
}

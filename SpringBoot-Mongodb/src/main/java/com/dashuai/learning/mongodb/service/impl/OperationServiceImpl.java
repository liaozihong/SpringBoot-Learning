package com.dashuai.learning.mongodb.service.impl;

import com.dashuai.learning.mongodb.model.People;
import com.dashuai.learning.mongodb.service.OperationService;
import com.dashuai.learning.mongodb.support.MongoDbDao;
import com.dashuai.learning.utils.json.JSONParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import java.util.List;

public class OperationServiceImpl implements OperationService {
    @Autowired
    MongoDbDao mongoDbDao;

    private final static Logger logger = LoggerFactory.getLogger(OperationServiceImpl.class);

    @Override
    public List<People> selectAllPeople() {
        return mongoDbDao.queryAll(People.class);
    }

    @Override
    public boolean insertPeople(People people) {
        try {
            Assert.notNull(people, "实体不能为Null");
            mongoDbDao.insert(people);
            return true;
        } catch (Exception e) {
            logger.warn("添加失败!参数：{}，错误：{}", JSONParseUtils.object2JsonString(people), e);
            return false;
        }
    }

    @Override
    public boolean uploadPeople(People people) {
        try {
            Assert.notNull(people, "实体不能为Null");
            Query query = new Query(new Criteria("name").is(people.getName()));
            Update update = new Update();
            update.set("age", people.getAge());
            update.set("sex", people.getSex());
            mongoDbDao.update(query, update, People.class);
            return true;
        } catch (Exception e) {
            logger.warn("修改失败!参数：{}，错误：{}", JSONParseUtils.object2JsonString(people), e);
            return false;
        }
    }

    @Override
    public boolean deletePeople(String name) {
        try {
            Assert.notNull(name, "Name不能为Null");
            Query query = new Query(new Criteria("name").is(name));
            mongoDbDao.delete(query, People.class);
            return true;
        } catch (Exception e) {
            logger.warn("删除失败!参数：{}，错误：{}", name, e);
            return false;
        }
    }

    @Override
    public People selectPeople(People people) {
        Assert.notNull(people, "实体不能为Null");
        return mongoDbDao.queryBy("name", people.getName(), People.class);
    }
}

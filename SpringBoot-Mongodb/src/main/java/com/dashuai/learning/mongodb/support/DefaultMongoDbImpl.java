package com.dashuai.learning.mongodb.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Default mongo db
 * <p/>
 * Created in 2018.11.08
 * <p/>
 *
 * @author Liaozihong
 */
@Component
public class DefaultMongoDbImpl implements MongoDbDao {
    /**
     * The Template.
     */
    @Autowired
    MongoTemplate template;

    @Override
    public <T> void save(T entity) {
        template.save(entity);
    }

    @Override
    public <T> void save(T entity, String collectionName) {
        template.save(entity, collectionName);
    }

    @Override
    public <T> void insert(T entity) {
        template.insert(entity);
    }

    @Override
    public <T> void insert(Collection<T> entities) {
        template.insertAll(entities);
    }

    @Override
    public <T> void insert(T entity, String collectionName) {
        template.insert(entity, collectionName);
    }

    @Override
    public <T> void insert(Collection<T> entities, String collectionName) {
        template.insert(entities, collectionName);
    }

    @Override
    public <T> void insert(Collection<T> entities, Class<T> entityClass) {
        template.insert(entities, entityClass);
    }

    @Override
    public <T> void delete(T entity) {
        template.remove(entity);
    }

    @Override
    public <T> void delete(T entity, String collectionName) {
        template.remove(entity, collectionName);
    }

    @Override
    public <T> void delete(Query query, Class<T> entityClass) {
        template.remove(query, entityClass);
    }

    @Override
    public <T> void delete(Query query, Class<T> entityClass, String collectionName) {
        if (collectionName == null) {
            template.remove(query, entityClass);
        } else {
            template.remove(query, entityClass, collectionName);
        }
    }

    @Override
    public <T> void deleteBy(String key, Object value, Class<T> entityClass,
                             String collectionName) {
        if (collectionName == null) {
            template.remove(Query.query(Criteria.where(key).is(value)),
                    entityClass);
        } else {
            template.remove(Query.query(Criteria.where(key).is(value)),
                    entityClass, collectionName);
        }
    }

    @Override
    public <T> void deleteBy(String key1, Object value1, String key2, Object value2,
                             Class<T> entityClass, String collectionName) {
        if (collectionName == null) {
            template.remove(Query.query(Criteria.where(key1).is(value1)
                    .andOperator(Criteria.where(key2).is(value2))), entityClass);
        } else {
            template.remove(Query.query(Criteria.where(key1).is(value1)
                    .andOperator(Criteria.where(key2).is(value2))), entityClass, collectionName);
        }
    }

    // ---------------------------UPDATE-------------------------------

    @Override
    public void update(Query query, Update update, String collectionName) {
        template.updateFirst(query, update, collectionName);
    }

    @Override
    public <T> void update(Query query, Update update, Class<T> entityClass) {
        template.updateFirst(query, update, entityClass);
    }

    @Override
    public <T> void update(Query query, Update update, Class<T> entityClass,
                           String collectionName) {
        if (collectionName == null) {
            template.updateFirst(query, update, entityClass);
        } else {
            template.updateFirst(query, update, entityClass, collectionName);
        }
    }

    @Override
    public <T> void updateBy(String key, Object value, Update update, Class<T> entityClass,
                             String collectionName) {
        if (collectionName == null) {
            template.updateFirst(Query.query(Criteria.where(key).is(value)),
                    update, entityClass);
        } else {
            template.updateFirst(Query.query(Criteria.where(key).is(value)),
                    update, entityClass, collectionName);
        }
    }

    @Override
    public <T> void updateBy(String key1, Object value1, String key2, Object value2,
                             Update update, Class<T> entityClass, String collectionName) {
        if (collectionName == null) {
            template.updateFirst(Query.query(Criteria.where(key1).is(value1)
                            .andOperator(Criteria.where(key2).is(value2))),
                    update, entityClass);
        } else {
            template.updateFirst(Query.query(Criteria.where(key1).is(value1)
                            .andOperator(Criteria.where(key2).is(value2))),
                    update, entityClass, collectionName);
        }
    }

    //  ================ 查询 ================
    @Override
    public <T> List<T> queryAll(Class<T> entityClass) {
        return template.findAll(entityClass);
    }

    @Override
    public <T> T query(Query query, Class<T> entityClass) {
        return template.findOne(query, entityClass);
    }

    @Override
    public <T> T query(Query query, Class<T> entityClass, String collectionName) {
        if (collectionName == null) {
            return template.findOne(query, entityClass);
        }
        return template.findOne(query, entityClass, collectionName);
    }

    @Override
    public <T> T queryBy(String key, Object value, Class<T> entityClass,
                         String collectionName) {

        Criteria criteria = Criteria.where(key).is(value);
        if (collectionName == null) {
            return template.findOne(Query.query(criteria), entityClass);
        }
        return template.findOne(Query.query(criteria), entityClass, collectionName);
    }

    @Override
    public <T> T queryBy(String key1, Object value1, String key2, Object value2,
                         Class<T> entityClass, String collectionName) {

        Criteria criteria = Criteria.where(key1).is(value1)
                .andOperator(Criteria.where(key2).is(value2));
        if (collectionName == null) {
            return template.findOne(Query.query(criteria), entityClass);
        }
        return template.findOne(Query.query(criteria), entityClass, collectionName);
    }

    @Override
    public <T> T queryBy(String key1, Object value1,
                         String key2, Object value2, String key3, Object value3,
                         Class<T> entityClass, String collectionName) {

        Criteria criteria = Criteria.where(key1).is(value1)
                .andOperator(Criteria.where(key2).is(value2))
                .andOperator(Criteria.where(key3).is(value3));

        if (collectionName == null) {
            return template.findOne(Query.query(criteria), entityClass);
        }
        return template.findOne(Query.query(criteria), entityClass, collectionName);
    }

    @Override
    public <T> T queryBy(String key1, Object value1,
                         String key2, Object value2, String key3, Object value3,
                         String key4, Object value4, Class<T> entityClass, String collectionName) {
        Criteria criteria = Criteria.where(key1).is(value1)
                .andOperator(Criteria.where(key2).is(value2))
                .andOperator(Criteria.where(key3).is(value3))
                .andOperator(Criteria.where(key4).is(value4));

        if (collectionName == null) {
            return template.findOne(Query.query(criteria), entityClass);
        }
        return template.findOne(Query.query(criteria), entityClass, collectionName);
    }

    @Override
    public <T> T queryBy(String key, Object value, Class<T> entityClass) {
        return queryBy(key, value, entityClass, null);
    }

    @Override
    public long count(Query query, Class entityClass) {
        return template.count(query, entityClass);
    }
}

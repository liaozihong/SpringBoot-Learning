package com.dashuai.learning.mongodb.support;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;

/**
 * The interface Mongo db dao.
 * 通用的mongodb操作处理类
 *
 * @author Liaozihong
 */
public interface MongoDbDao {

    // ---------------------------INSERT-------------------------------

    /**
     * Save.
     *
     * @param <T>    the type parameter
     * @param entity the entity
     */
    <T> void save(T entity);

    /**
     * Save.
     *
     * @param <T>            the type parameter
     * @param entity         the entity
     * @param collectionName the collection name
     */
    <T> void save(T entity, String collectionName);

    /**
     * Insert.
     *
     * @param <T>    the type parameter
     * @param entity the entity
     */
    <T> void insert(T entity);

    /**
     * Insert.
     *
     * @param <T>      the type parameter
     * @param entities the entities
     */
    <T> void insert(Collection<T> entities);

    /**
     * Insert.
     *
     * @param <T>            the type parameter
     * @param entity         the entity
     * @param collectionName the collection name
     */
    <T> void insert(T entity, String collectionName);

    /**
     * Insert.
     *
     * @param <T>            the type parameter
     * @param entities       the entities
     * @param collectionName the collection name
     */
    <T> void insert(Collection<T> entities, String collectionName);

    /**
     * Insert.
     *
     * @param <T>         the type parameter
     * @param entities    the entities
     * @param entityClass the entity class
     */
    <T> void insert(Collection<T> entities, Class<T> entityClass);

    // ---------------------------DELETE-------------------------------

    /**
     * Delete.
     *
     * @param <T>    the type parameter
     * @param entity the entity
     */
    <T> void delete(T entity);

    /**
     * Delete.
     *
     * @param <T>            the type parameter
     * @param entity         the entity
     * @param collectionName the collection name
     */
    <T> void delete(T entity, String collectionName);

    /**
     * Delete.
     *
     * @param <T>         the type parameter
     * @param query       the query
     * @param entityClass the entity class
     */
    <T> void delete(Query query, Class<T> entityClass);

    /**
     * Delete.
     *
     * @param <T>            the type parameter
     * @param query          the query
     * @param entityClass    the entity class
     * @param collectionName the collection name
     */
    <T> void delete(Query query, Class<T> entityClass, String collectionName);

    /**
     * Delete by.
     *
     * @param <T>            the type parameter
     * @param key            the key
     * @param value          the value
     * @param entityClass    the entity class
     * @param collectionName the collection name
     */
    <T> void deleteBy(String key, Object value, Class<T> entityClass,
                      String collectionName);

    /**
     * Delete by.
     *
     * @param <T>            the type parameter
     * @param key1           the key 1
     * @param value1         the value 1
     * @param key2           the key 2
     * @param value2         the value 2
     * @param entityClass    the entity class
     * @param collectionName the collection name
     */
    <T> void deleteBy(String key1, Object value1, String key2, Object value2,
                      Class<T> entityClass, String collectionName);

    // ---------------------------UPDATE-------------------------------

    /**
     * Update.
     *
     * @param query          the query
     * @param update         the update
     * @param collectionName the collection name
     */
    <T> void update(Query query, Update update, String collectionName);

    /**
     * Update.
     *
     * @param <T>         the type parameter
     * @param query       the query
     * @param update      the update
     * @param entityClass the entity class
     */
    <T> void update(Query query, Update update, Class<T> entityClass);

    /**
     * Update.
     *
     * @param <T>            the type parameter
     * @param query          the query
     * @param update         the update
     * @param entityClass    the entity class
     * @param collectionName the collection name
     */
    <T> void update(Query query, Update update, Class<T> entityClass,
                    String collectionName);

    /**
     * Update by.
     *
     * @param <T>            the type parameter
     * @param key            the key
     * @param value          the value
     * @param update         the update
     * @param entityClass    the entity class
     * @param collectionName the collection name
     */
    <T> void updateBy(String key, Object value, Update update, Class<T> entityClass,
                      String collectionName);

    /**
     * Update by.
     *
     * @param <T>            the type parameter
     * @param key1           the key 1
     * @param value1         the value 1
     * @param key2           the key 2
     * @param value2         the value 2
     * @param update         the update
     * @param entityClass    the entity class
     * @param collectionName the collection name
     */
    <T> void updateBy(String key1, Object value1, String key2, Object value2,
                      Update update, Class<T> entityClass,
                      String collectionName);

    // ---------------------------QUERY--------------------------------
    <T> List<T> queryAll(Class<T> entityClass);

    /**
     * Query t.
     *
     * @param <T>         the type parameter
     * @param query       the query
     * @param entityClass the entity class
     * @return the t
     */
    <T> T query(Query query, Class<T> entityClass);

    /**
     * Query t.
     *
     * @param <T>            the type parameter
     * @param query          the query
     * @param entityClass    the entity class
     * @param collectionName the collection name
     * @return the t
     */
    <T> T query(Query query, Class<T> entityClass,
                String collectionName);

    /**
     * Query by t.
     *
     * @param <T>            the type parameter
     * @param key            the key
     * @param value          the value
     * @param entityClass    the entity class
     * @param collectionName the collection name
     * @return the t
     */
    <T> T queryBy(String key, Object value, Class<T> entityClass,
                  String collectionName);

    /**
     * Query by t.
     *
     * @param <T>         the type parameter
     * @param key         the key
     * @param value       the value
     * @param entityClass the entity class
     * @return the t
     */
    <T> T queryBy(String key, Object value, Class<T> entityClass);

    /**
     * Query by t.
     *
     * @param <T>            the type parameter
     * @param key1           the key 1
     * @param value1         the value 1
     * @param key2           the key 2
     * @param value2         the value 2
     * @param entityClass    the entity class
     * @param collectionName the collection name
     * @return the t
     */
    <T> T queryBy(String key1, Object value1, String key2, Object value2,
                  Class<T> entityClass, String collectionName);

    /**
     * Query by t.
     *
     * @param <T>            the type parameter
     * @param key1           the key 1
     * @param value1         the value 1
     * @param key2           the key 2
     * @param value2         the value 2
     * @param key3           the key 3
     * @param value3         the value 3
     * @param entityClass    the entity class
     * @param collectionName the collection name
     * @return the t
     */
    <T> T queryBy(String key1, Object value1, String key2, Object value2,
                  String key3, Object value3, Class<T> entityClass, String collectionName);

    /**
     * Query by t.
     *
     * @param <T>            the type parameter
     * @param key1           the key 1
     * @param value1         the value 1
     * @param key2           the key 2
     * @param value2         the value 2
     * @param key3           the key 3
     * @param value3         the value 3
     * @param key4           the key 4
     * @param value4         the value 4
     * @param entityClass    the entity class
     * @param collectionName the collection name
     * @return the t
     */
    <T> T queryBy(String key1, Object value1, String key2, Object value2,
                  String key3, Object value3, String key4, Object value4,
                  Class<T> entityClass, String collectionName);

    /**
     * Count long.
     * s
     *
     * @param query       the query
     * @param entityClass the entity class
     * @return the long
     */
    <T> long count(Query query, Class<T> entityClass);
}

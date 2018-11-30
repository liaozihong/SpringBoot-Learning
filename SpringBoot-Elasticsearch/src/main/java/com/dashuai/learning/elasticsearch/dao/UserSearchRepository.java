package com.dashuai.learning.elasticsearch.dao;

import com.dashuai.learning.elasticsearch.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * The interface User search repository.
 * SpringBoot 默认帮助我们封装了基本的curl和一些操作，如需增加只需继承ElasticsearchRepository扩展。
 *
 * @author Liaozihong
 */
public interface UserSearchRepository extends ElasticsearchRepository<User, Integer> {

    /**
     * Find by name list.
     *
     * @param name the name
     * @return the list
     */
    List<User> findByName(String name);

    /**
     * 使用 Page<User> users = userSearchRepository.findByName("测试",  PageRequest.of(0, 10)); //分页是从0开始的
     *
     * @param name     the name
     * @param pageable the pageable
     * @return the page
     */
    Page<User> findByName(String name, Pageable pageable);

    /**
     * Find product by id user.
     *
     * @param name the name
     * @return the user
     */
    User findProductById(String name);
}

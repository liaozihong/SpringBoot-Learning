package com.dashuai.learning.elasticsearch.service.impl;

import com.dashuai.learning.elasticsearch.dao.UserSearchRepository;
import com.dashuai.learning.elasticsearch.model.User;
import com.dashuai.learning.elasticsearch.service.UserSearchService;
import com.dashuai.learning.utils.json.JSONParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User search service
 * <p/>
 * Created in 2018.11.29
 * <p/>
 *
 * @author Liaozihong
 */
@Service
public class UserSearchServiceImpl implements UserSearchService {
    /**
     * The User search repository.
     */
    @Autowired
    UserSearchRepository userSearchRepository;

    private final Logger logger = LoggerFactory.getLogger(UserSearchServiceImpl.class);

    @Override
    public List<User> getUserAll() {
        List<User> userList = new ArrayList<>();
        Iterable<User> userIterable = userSearchRepository.findAll();
        userIterable.forEach(userList::add);
        return userList;
    }

    @Override
    public List<User> getUserLimit(int pageNum, int size) {
        Page<User> userPage = userSearchRepository.findAll(PageRequest.of(pageNum, size));
        return userPage.getContent();
    }

    @Override
    public List<User> getUserByName(String name, int pageNum, int size) {
        Page<User> userPage = userSearchRepository.findByName(name, PageRequest.of(pageNum, size));
        return userPage.getContent();
    }

    @Override
    public List<User> getUserByName(String name) {
        return userSearchRepository.findByName(name);
    }

    @Override
    public Boolean insertUser(User user) {
        try {
            userSearchRepository.save(user);
        } catch (Exception e) {
            logger.warn("添加失败!param:{}", JSONParseUtils.object2JsonString(user), e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean updateUser(User user) {
        try {
            userSearchRepository.save(user);
        } catch (Exception e) {
            logger.warn("修改失败!param:{}", JSONParseUtils.object2JsonString(user), e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean dropUser(Integer id) {
        try {
            userSearchRepository.deleteById(id);
        } catch (Exception e) {
            logger.warn("删除失败!param:{}", id, e);
            return false;
        }
        return true;
    }
}

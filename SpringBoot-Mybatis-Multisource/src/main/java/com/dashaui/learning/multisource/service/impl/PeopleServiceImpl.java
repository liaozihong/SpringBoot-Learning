package com.dashaui.learning.multisource.service.impl;

import com.dashaui.learning.multisource.mapper.test.PeopleMapper;
import com.dashaui.learning.multisource.model.test.People;
import com.dashaui.learning.multisource.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * People service
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
@Service
public class PeopleServiceImpl implements PeopleService {
    /**
     * The People mapper.
     */
    @Autowired
    PeopleMapper peopleMapper;

    @Override
    public List<People> selectAll() {
        return peopleMapper.findAll();
    }

    @Override
    public Boolean insertPeople(People people) {
        return peopleMapper.insertSelective(people);
    }
}

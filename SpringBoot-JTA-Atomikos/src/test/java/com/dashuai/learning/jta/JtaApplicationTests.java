package com.dashuai.learning.jta;

import com.dashuai.learning.jta.model.spring.User;
import com.dashuai.learning.jta.model.test.People;
import com.dashuai.learning.jta.service.PeopleService;
import com.dashuai.learning.jta.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JtaApplicationTests {

    @Autowired
    UserService userService;
    @Autowired
    PeopleService peopleService;

    @Test
    @Transactional
    public void contextLoads() {
        User user = new User();
        user.setUserName("你妹哦");
        user.setPassword("我去");
        user.setAge(20);
        userService.insertUser(user);
        People people = new People();
        people.setName("你大爺的");
        people.setAge(50);
        people.setSex("男");
        peopleService.insertPeople(people);
    }
}


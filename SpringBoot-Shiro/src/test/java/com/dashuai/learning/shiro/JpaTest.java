package com.dashuai.learning.shiro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JpaTest {
    @PersistenceContext
    private EntityManager em;

    @Test
    public void test() {

    }
}

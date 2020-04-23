package com.dashuai.learning.jwt;

import com.dashuai.learning.jwt.mapper.RoleMapper;
import com.dashuai.learning.utils.json.JSONParseUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = JwtApplication.class)
@RunWith(SpringRunner.class)
public class MysqlTest {
    @Autowired
    RoleMapper roleMapper;

    @Test
    public void collectionTest() {
        System.out.println(JSONParseUtils.object2JsonString(roleMapper.selectRoleOfPerm(1)));
    }
}

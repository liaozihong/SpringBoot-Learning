package com.dashuai.learning.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dashuai.learning.mybatisplus.user.entity.User;
import com.dashuai.learning.mybatisplus.user.mapper.UserMapper;
import com.dashuai.learning.mybatisplus.user.service.IUserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MybatisPlusApplicationTests {

    @Autowired
    IUserService userService;


    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void testUpdate() {
//        User user = userMapper.selectById(2);
//        assertThat(user.getAge()).isEqualTo(36);
//        assertThat(user.getName()).isEqualTo("keep");
        User user = new User();
        user.setEmail("123@123");
        userMapper.update(
                user, new UpdateWrapper<User>().lambda()
                        .eq(User::getId, 2)
        );
        assertThat(userMapper.selectById(2).getEmail()).isEqualTo("123@123");
    }

    /**
     * 基于乐观锁修改
     */
    @Test
    public void testOptimisticUpdate() {
        User user = userMapper.selectById(2);
        User user2 = new User();
        user2.setEmail("123@1234.com");
        user2.setVersion(user.getVersion());
        user2.setId(2L);
        userMapper.updateById(user2);
        assertThat(userMapper.selectById(2).getEmail()).isEqualTo("123@1234.com");
    }

    @Test
    public void testSelectOne() {
        User user = userMapper.selectById(1L);
        System.out.println(user);
    }

    @Test
    public void testDelete() {
//        assertThat(userMapper.deleteById(3L)).isGreaterThan(0);
        assertThat(userMapper.delete(new QueryWrapper<User>()
                .lambda().eq(User::getName, "Zoey"))).isGreaterThan(0);
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setName("Zoey");
        user.setAge(21);
        user.setEmail("neo@tooool.org");
        user.setId(6L);
        assertThat(userMapper.insert(user)).isGreaterThan(0);
        // 成功直接拿会写的 ID
        assertThat(user.getId()).isNotNull();
    }

    @Test
    public void testPage() {
        System.out.println("----- baseMapper 自带分页 ------");
        //  current 当前页数，一页开始， size 每页条数
        Page<User> page = new Page<>(1, 2);
        IPage<User> userIPage = userMapper.selectPage(page, new QueryWrapper<User>()
                .gt("age", 6));
//        assertThat(page).isSameAs(userIPage);
        System.out.println("总条数 ------> " + userIPage.getTotal());
        System.out.println("当前页数 ------> " + userIPage.getCurrent());
        System.out.println("当前每页显示数 ------> " + userIPage.getSize());
        System.out.println(userIPage.getRecords());
        System.out.println("----- baseMapper 自带分页 ------");
    }
}

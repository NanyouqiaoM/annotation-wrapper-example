package com.xiyu.wrapper.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiyu.wrapper.code.WrapperGenerator;
import com.xiyu.wrapper.example.entity.User;
import com.xiyu.wrapper.example.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class WrapperTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void test() {
        List<Long> roleIdList = new ArrayList<>();
        roleIdList.add(2L);
        roleIdList.add(1L);
        QueryUserParam param = new QueryUserParam();
        param.setNameKeyword("J");
        param.setRoleIdList(roleIdList);
        param.setMaxAge(19);
        List<User> list = list(param);
        System.out.println("----- 普通方式 ------");
        print(list);
        System.out.println("----- 使用 annotation-wrapper-------");
        List<User> list1 = list1(param);
        print(list1);
    }

    /**
     * System.out.println("----- 普通方式 ------");
     *
     * @param queryParam
     * @return
     */
    public List<User> list(QueryUserParam queryParam) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(!CollectionUtils.isEmpty(queryParam.getRoleIdList()), User::getRoleId, queryParam.getRoleIdList());
        wrapper.le(queryParam.getMaxAge() != null, User::getAge, queryParam.getMaxAge());
        wrapper.like(StringUtils.hasText(queryParam.getNameKeyword()), User::getName, queryParam.getNameKeyword());
        //省略其他条件
        return userMapper.selectList(queryParam.lambdaWrapper());
    }

    /**
     * 使用 annotation-wrapper
     *
     * @param queryParam
     * @return
     */
    public List<User> list1(QueryUserParam queryParam) {
        //实现接口
        List<User> plainUsers = userMapper.selectList(queryParam.lambdaWrapper());
        //WrapperGenerator.generateWrapper()生成  两种方式任选其一
        //List<User> plainUsers = userMapper.selectList(WrapperGenerator.generateWrapper(param));
        return plainUsers;
    }

    private <T> void print(List<T> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(System.out::println);
        }
    }
}
package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.User;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface UserService extends IService<User> {

    /**
     * 登录PC
     *
     * @param userName 用户名
     * @param password 用户密码
     * @return 登录用户
     */
    User loginPC(String userName, String password);

    /**
     * 登录APP
     *
     * @param userName 用户名
     * @param password 用户密码
     * @return 登录用户
     */
    User loginAPP(String userName, String password);

    /**
     * PC退出登录
     */
    void loginOutPC();

    /**
     * APP退出登录
     */
    void loginOutAPP();

    /**
     * 新增用户
     *
     * @param user 用户
     */
    void insert(User user);

    /**
     * 删除用户
     *
     * @param userId 用户id
     */
    void delete(Integer userId);

    /**
     * 更新用户
     *
     * @param user 用户
     */
    void update(User user);

    /**
     * 查询除超级管理员之外的所有用户
     *
     * @return 用户集合
     */
    List<User> queryAll();

}

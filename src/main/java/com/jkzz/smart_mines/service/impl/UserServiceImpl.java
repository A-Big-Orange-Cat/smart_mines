package com.jkzz.smart_mines.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.enumerate.impl.RoleEnum;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.mapper.UserMapper;
import com.jkzz.smart_mines.pojo.domain.User;
import com.jkzz.smart_mines.service.UserService;
import com.jkzz.smart_mines.utils.VUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private final UserMapper userMapper;

    @Override
    public User loginPC(String userName, String password) {
        return login(userName, password, "PC");
    }

    @Override
    public User loginAPP(String userName, String password) {
        return login(userName, password, "APP");
    }

    @Override
    public void loginOutPC() {
        StpUtil.logout(StpUtil.getLoginId(), "PC");
    }

    @Override
    public void loginOutAPP() {
        StpUtil.logout(StpUtil.getLoginId(), "APP");
    }

    @Override
    public void insert(User user) {
        VUtil.isTrue(null != selectOneByUserName(user.getUserName(), null))
                .throwAppException(AppExceptionCodeMsg.USER_EXIST);
        VUtil.isTrue(1 != userMapper.insert(user))
                .throwAppException(AppExceptionCodeMsg.FAILURE_INSERT);
    }

    @Override
    public void delete(Integer userId) {
        VUtil.isTrue(1 != userMapper.deleteById(userId))
                .throwAppException(AppExceptionCodeMsg.FAILURE_DELETE);
    }

    @Override
    public void update(User user) {
        VUtil.isTrue(null != selectOneByUserName(user.getUserName(), user.getUserId()))
                .throwAppException(AppExceptionCodeMsg.USER_EXIST);
        VUtil.isTrue(1 != userMapper.updateById(user))
                .throwAppException(AppExceptionCodeMsg.FAILURE_UPDATE);
    }

    @Override
    public List<User> queryAll() {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.ne(User::getRole, RoleEnum.SUPER_ADMIN);
        return userMapper.selectList(lambdaQueryWrapper);
    }

    private User login(String userName, String password, String device) {
        User user = Optional.ofNullable(selectOneByUserName(userName, null))
                .orElseThrow(() -> new AppException(AppExceptionCodeMsg.USER_FILE_LOGIN));
        VUtil.isTrue(!user.getPassword().equals(password))
                .throwAppException(AppExceptionCodeMsg.USER_FILE_LOGIN);
        StpUtil.login(user.getUserId(), device);
        user.setPassword(null);
        return user;
    }

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @param userId   排除的用户id
     * @return 用户
     */
    private User selectOneByUserName(String userName, Integer userId) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserName, userName);
        if (null != userId) {
            lambdaQueryWrapper.ne(User::getUserId, userId);
        }
        return userMapper.selectOne(lambdaQueryWrapper);
    }

}





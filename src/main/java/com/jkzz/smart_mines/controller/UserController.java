package com.jkzz.smart_mines.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.jkzz.smart_mines.pojo.domain.User;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.UserService;
import com.jkzz.smart_mines.verification.group.DeleteGroup;
import com.jkzz.smart_mines.verification.group.InsertGroup;
import com.jkzz.smart_mines.verification.group.LoginGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 用户PC登录
     *
     * @param loginUser 用户名和密码
     * @return 结果数据
     */
    @PostMapping("/loginPC")
    public Resp<Map<String, Object>> loginPC(@Validated(value = LoginGroup.class) @RequestBody User loginUser) {
        User user = userService.loginPC(loginUser.getUserName(), loginUser.getPassword());
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("tokenInfo", StpUtil.getTokenInfo());
        return Resp.success(map);
    }

    /**
     * 用户手机APP登录
     *
     * @param loginUser 用户名和密码
     * @return 结果数据
     */
    @PostMapping("/loginAPP")
    public Resp<Map<String, Object>> loginAPP(@Validated(value = LoginGroup.class) @RequestBody User loginUser) {
        User user = userService.loginAPP(loginUser.getUserName(), loginUser.getPassword());
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("tokenInfo", StpUtil.getTokenInfo());
        return Resp.success(map);
    }

    /**
     * 用户PC注销登录
     *
     * @return 结果数据
     */
    @GetMapping("/loginOutPC")
    public Resp<Object> loginOutPC() {
        userService.loginOutPC();
        return Resp.success();
    }

    /**
     * 用户APP注销登录
     *
     * @return 结果数据
     */
    @GetMapping("/loginOutAPP")
    public Resp<Object> loginOutAPP() {
        userService.loginOutAPP();
        return Resp.success();
    }

    /**
     * 添加用户
     *
     * @param user 用户信息
     * @return 结果数据
     */
    @PostMapping("/insert")
    @SaCheckRole("admin")
    public Resp<Object> insert(@Validated(value = InsertGroup.class) @RequestBody User user) {
        userService.insert(user);
        return Resp.success();
    }

    /**
     * 删除用户
     *
     * @param user 用户id
     * @return 结果数据
     */
    @PostMapping("/delete")
    @SaCheckRole("admin")
    public Resp<Object> delete(@Validated(value = DeleteGroup.class) @RequestBody User user) {
        userService.delete(user.getUserId());
        return Resp.success();

    }

    /**
     * 更新用户信息
     *
     * @param user 要更新的用户信息
     * @return 结果数据
     */
    @PostMapping("/update")
    @SaCheckRole("admin")
    public Resp<Object> update(@Validated(value = UpdateGroup.class) @RequestBody User user) {
        userService.update(user);
        return Resp.success();
    }

    /**
     * 查询所有用户
     *
     * @return 结果数据
     */
    @GetMapping("/queryAll")
    @SaCheckRole("admin")
    public Resp<List<User>> queryAll() {
        List<User> users = userService.queryAll();
        return Resp.success(users);
    }

}

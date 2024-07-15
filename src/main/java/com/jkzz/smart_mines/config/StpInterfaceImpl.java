package com.jkzz.smart_mines.config;

import cn.dev33.satoken.stp.StpInterface;
import com.jkzz.smart_mines.enumerate.impl.RoleEnum;
import com.jkzz.smart_mines.pojo.domain.User;
import com.jkzz.smart_mines.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        int roleCode = Optional.ofNullable(userService.getById(Integer.valueOf(loginId.toString())))
                .map(User::getRole)
                .map(RoleEnum::getValue)
                .orElse(0);
        switch (roleCode) {
            case 0:
                list.add("user");
                break;
            case 1:
                list.add("user");
                list.add("admin");
                break;
            case -1:
                list.add("user");
                list.add("admin");
                list.add("super-admin");
                break;
            default:
                break;
        }
        return list;
    }

}


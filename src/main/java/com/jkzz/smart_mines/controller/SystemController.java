package com.jkzz.smart_mines.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.jkzz.smart_mines.pojo.domain.System;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.SystemService;
import com.jkzz.smart_mines.utils.HAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * 系统控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system")
public class SystemController {

    private final ApplicationContext context;

    private final SystemService systemService;

    /**
     * 验证是否拥有系统使用权限
     *
     * @return 结果数据
     */
    @GetMapping("/verify")
    public Resp<Object> verify() {
        systemService.verify();
        return Resp.success();
    }

    /**
     * 注册系统使用权限
     *
     * @param map cpu序列号
     * @return 结果数据
     */
    @PostMapping("/register")
    public Resp<Object> register(@ApiIgnore @RequestBody Map<String, Object> map) {
        String activationCode = HAUtil.getString(map, "activationCode");
        systemService.register(activationCode);
        return Resp.success();
    }

    /**
     * 更新主界面标题
     *
     * @param system 标题信息
     * @return 结果数据
     */
    @PostMapping("/updateTitle")
    @SaCheckRole("admin")
    public Resp<Object> updateTitle(@RequestBody System system) {
        systemService.updateTitle(system.getTitle());
        return Resp.success();
    }

    /**
     * 更新主界面地图
     *
     * @param file 地图文件
     * @return 结果数据
     */
    @PostMapping("/updateMapPath")
    @SaCheckRole("admin")
    public Resp<Object> updateMapPath(@RequestParam("file") MultipartFile file) {
        systemService.updateMap(file);
        return Resp.success();
    }

    /**
     * 查询主界面标题与地图路径
     *
     * @return 结果数据
     */
    @GetMapping("/query")
    public Resp<System> queryTitle() {
        System systemInfo = systemService.getById(1);
        systemInfo.setCpuId(null);
        try {
            systemInfo.setWebsocketURL("ws://" +
                    InetAddress.getLocalHost().getHostAddress() +
                    ":" +
                    context.getEnvironment().getProperty("server.port"));
        } catch (UnknownHostException e) {
            systemInfo.setWebsocketURL("获取websocketULR失败！");
        }
        return Resp.success(systemInfo);
    }

    @PostMapping("/shutdown")
    public String shutdown(HttpServletRequest request) {
        return systemService.shutdown(request);
    }

}

package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.System;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【system】的数据库操作Service
 * @createDate 2024-07-01 10:10:55
 */
public interface SystemService extends IService<System> {

    /**
     * 验证使用权限
     */
    void verify();

    /**
     * 系统激活
     *
     * @param activationCode 激活码
     */
    void register(String activationCode);

    /**
     * 更新系统主界面标题
     *
     * @param newTitle 系统主界面标题
     */
    void updateTitle(String newTitle);

    /**
     * 更新地图
     *
     * @param file 地图文件
     */
    void updateMap(MultipartFile file);

    /**
     * 关闭服务
     *
     * @param request 请求信息
     * @return 信息
     */
    String shutdown(HttpServletRequest request);

}

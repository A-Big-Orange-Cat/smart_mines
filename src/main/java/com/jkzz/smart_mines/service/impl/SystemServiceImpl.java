package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.enumerate.impl.AppExceptionCodeMsg;
import com.jkzz.smart_mines.exception.AppException;
import com.jkzz.smart_mines.hikvision.PreviewUntil;
import com.jkzz.smart_mines.mapper.SystemMapper;
import com.jkzz.smart_mines.pojo.domain.System;
import com.jkzz.smart_mines.service.SystemService;
import com.jkzz.smart_mines.utils.VUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author Administrator
 * @description 针对表【system】的数据库操作Service实现
 * @createDate 2024-07-01 10:10:55
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemServiceImpl extends ServiceImpl<SystemMapper, System>
        implements SystemService {

    private final ApplicationContext applicationContext;

    private final SystemMapper systemMapper;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Value("${server.port}")
    private int port;
    private static String cpuId;
    private static final String UNKNOWN = "unknown";

    @Override
    public void verify() {
        VUtil.isTrue(!getCpuId().equals(systemMapper.selectById(1).getCpuId()))
                .throwAppException(AppExceptionCodeMsg.SYSTEM_NOT_ACTIVATION);
    }

    @Override
    public void register(String activationCode) {
        VUtil.isTrue(!getActivationCode().equals(activationCode))
                .throwAppException(AppExceptionCodeMsg.SYSTEM_INVALID_ACTIVATION_CODE);
        LambdaUpdateWrapper<System> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(System::getId, 1)
                .set(System::getCpuId, getCpuId());
        VUtil.isTrue(!this.update(lambdaUpdateWrapper))
                .throwAppException(AppExceptionCodeMsg.FAILURE_ACTIVATION);
    }

    @Override
    public void updateTitle(String newTitle) {
        LambdaUpdateWrapper<System> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(System::getId, 1)
                .set(System::getTitle, newTitle);
        VUtil.isTrue(!this.update(lambdaUpdateWrapper))
                .throwAppException(AppExceptionCodeMsg.FAILURE_UPDATE);
    }

    @Override
    public void updateMap(MultipartFile file) {
        if (file.isEmpty()) {
            log.error("地图文件上传失败");
            throw new AppException(AppExceptionCodeMsg.FAILURE_FILE_UPLOAD);
        }

        String filename = "map" + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        String mapPath = java.lang.System.getProperty("user.dir") + "\\Application\\map\\" + filename;
        File map = new File(mapPath);
        VUtil.handler(!map.getParentFile().exists()).handler(() -> {
                    if (!map.getParentFile().mkdirs()) {
                        log.error("创建地图文件父目录失败");
                    }
                }
        );
        try {
            file.transferTo(map);
        } catch (IOException e) {
            log.error("地图文件保存失败，原因：{}", e.getMessage());
            throw new AppException(AppExceptionCodeMsg.FAILURE_FILE_SAVE);
        }

        String path;
        try {
            path = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/map/" + filename;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        LambdaUpdateWrapper<System> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(System::getId, 1)
                .set(System::getMapPath, path);
        VUtil.isTrue(!this.update(lambdaUpdateWrapper))
                .throwAppException(AppExceptionCodeMsg.FAILURE_UPDATE);
        log.info("地图文件上传成功");
    }

    @Override
    public String shutdown(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("127.0.0.1".equals(ip)) {
            new Thread(() -> {
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 正在关闭服务.... >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                PreviewUntil.shutdown();
                threadPoolTaskExecutor.shutdown();
                java.lang.System.exit(SpringApplication.exit(applicationContext));
            }).start();
            return "正在关闭服务....";
        } else {
            return "SB";
        }
    }

    private static String getCpuId() {
        if (null != cpuId) {
            return cpuId;
        }
        //String[] linux = {"dmidecode", "-t", "processor", "|", "grep", "'ID'"};
        String[] windows = {"wmic", "cpu", "get", "ProcessorId"};
        //String property = java.lang.System.getProperty("os.name");
        Process process;

        try {
            //process = Runtime.getRuntime().exec(property.contains("Window") ? windows : linux);
            process = Runtime.getRuntime().exec(windows);
            process.getOutputStream().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scanner sc = new Scanner(process.getInputStream(), "utf-8");
        sc.next();
        cpuId = DigestUtils.md5DigestAsHex(sc.next().getBytes());
        return cpuId;
    }

    private String getActivationCode() {
        String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return DigestUtils.md5DigestAsHex(nowDate.getBytes()).substring(8, 24);
    }

}





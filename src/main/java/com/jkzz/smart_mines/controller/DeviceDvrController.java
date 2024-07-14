package com.jkzz.smart_mines.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.jkzz.smart_mines.pojo.domain.DeviceDvr;
import com.jkzz.smart_mines.pojo.param.DeviceDvrParam;
import com.jkzz.smart_mines.response.Resp;
import com.jkzz.smart_mines.service.DeviceDvrService;
import com.jkzz.smart_mines.verification.group.DeleteGroup;
import com.jkzz.smart_mines.verification.group.InsertGroup;
import com.jkzz.smart_mines.verification.group.SelectGroup;
import com.jkzz.smart_mines.verification.group.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hik")
public class DeviceDvrController {

    @Autowired
    DeviceDvrService deviceDvrService;

    /**
     * 登录并开启预览
     *
     * @param dvr 设备id
     * @return 结果数据
     */
    @PostMapping("/start")
    public Resp<List<DeviceDvr>> startHik(@Validated(value = SelectGroup.class) @RequestBody DeviceDvr dvr) {
        List<DeviceDvr> dvrs = deviceDvrService.queryByDeviceId(dvr.getDeviceId());
        deviceDvrService.start(dvrs);
        return Resp.success(dvrs);
    }

    /**
     * 停止预览并退出登录
     *
     * @param dvrParam 用户句柄列表
     * @return 结果数据
     */
    @PostMapping("/stop")
    public Resp<Object> stopHik(@Validated @RequestBody DeviceDvrParam dvrParam) {
        deviceDvrService.stop(dvrParam.getLUserIdList());
        return Resp.success();
    }

    /**
     * 添加摄像头
     *
     * @param dvr 摄像头信息
     * @return 结果数据
     */
    @PostMapping("/insert")
    @SaCheckRole("admin")
    public Resp<Object> insert(@Validated(value = InsertGroup.class) @RequestBody DeviceDvr dvr) {
        deviceDvrService.insert(dvr);
        return Resp.success();
    }

    /**
     * 删除摄像头
     *
     * @param dvr 摄像头id
     * @return 结果数据
     */
    @PostMapping("/delete")
    @SaCheckRole("admin")
    public Resp<Object> delete(@Validated(value = DeleteGroup.class) @RequestBody DeviceDvr dvr) {
        deviceDvrService.delete(dvr.getDvrId());
        return Resp.success();
    }

    /**
     * 更新摄像头信息
     *
     * @param dvr 要更新的摄像头信息
     * @return 结果数据
     */
    @PostMapping("/update")
    @SaCheckRole("admin")
    public Resp<Object> update(@Validated(value = UpdateGroup.class) @RequestBody DeviceDvr dvr) {
        deviceDvrService.update(dvr);
        return Resp.success();
    }

    /**
     * 按设备id查询摄像头
     *
     * @param dvr 设备id
     * @return 结果数据
     */
    @PostMapping("/queryAllByDeviceId")
    public Resp<List<DeviceDvr>> queryAllByDeviceId(@Validated(value = SelectGroup.class) @RequestBody DeviceDvr dvr) {
        List<DeviceDvr> dvrs = deviceDvrService.queryByDeviceId(dvr.getDeviceId());
        return Resp.success(dvrs);
    }

}

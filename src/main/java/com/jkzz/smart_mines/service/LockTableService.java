package com.jkzz.smart_mines.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jkzz.smart_mines.pojo.domain.LockTable;

import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @description 针对表【lock_table】的数据库操作Service
 * @createDate 2024-07-24 09:13:32
 */
public interface LockTableService extends IService<LockTable> {

    boolean lock(String lockKey);

    void lockTime(String lockKey, int time, TimeUnit timeUnit);

    void unlock(String lockKey);

}

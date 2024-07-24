package com.jkzz.smart_mines.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jkzz.smart_mines.mapper.LockTableMapper;
import com.jkzz.smart_mines.pojo.domain.LockTable;
import com.jkzz.smart_mines.service.LockTableService;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @description 针对表【lock_table】的数据库操作Service实现
 * @createDate 2024-07-24 09:13:32
 */
@Service
public class LockTableServiceImpl extends ServiceImpl<LockTableMapper, LockTable>
        implements LockTableService {

    @Override
    public boolean lock(String lockKey) {
        if (null == this.getById(lockKey)) {
            return this.save(new LockTable(lockKey));
        }
        return false;
    }

    @Override
    public void lockTime(String lockKey, int time, TimeUnit timeUnit) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                unlock(lockKey);
            }
        }, timeUnit.toMillis(time));
    }

    @Override
    public void unlock(String lockKey) {
        this.removeById(lockKey);
    }

}





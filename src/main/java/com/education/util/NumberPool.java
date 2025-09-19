package com.education.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Random;

import com.education.mapper.UserMapper;
import com.education.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class NumberPool {

    private final Queue<Integer> numberQueue = new ConcurrentLinkedQueue<>();
    private final int POOL_SIZE = 1; // 池大小
    private final int MIN = 10000000;
    private final int MAX = 99999999;
    private final Random random = new Random();

    @Autowired
    private UserMapper userMapper; // 继承BaseMapper的UserMapper

    // 初始化池（系统启动时调用）
    @PostConstruct
    public void initPool() {
        refillPool();
    }

    // 补充数字到池
    private void refillPool() {
        while (numberQueue.size() < POOL_SIZE) {
            int number = random.nextInt(MAX - MIN + 1) + MIN;
            String username = "user" + number;

            // 使用MP的条件构造器查询是否存在该数字
            boolean existsInDb = userMapper.exists(
                    new QueryWrapper<User>()
                            .eq("username", username) // "number"是数据库字段名
            );

            // 检查内存队列中是否已存在
            boolean existsInQueue = numberQueue.contains(number);

            if (!existsInDb && !existsInQueue) {
                numberQueue.add(number);
            }
        }
    }

    // 获取唯一数字
    public Integer getNumber() {
        Integer number = numberQueue.poll();
        // 当池内数量不足时，异步补充
        if (numberQueue.size() < 200) {
            new Thread(this::refillPool).start();
        }
        return number;
    }
}
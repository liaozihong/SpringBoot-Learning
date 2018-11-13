package com.dashuai.learning.zookeeper.api;

import com.dashuai.learning.utils.result.ApiResult;
import com.dashuai.learning.zookeeper.lock.DistributedLockByZookeeper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Distributed lock test api
 * <p/>
 * Created in 2018.11.12
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@Api(value = "分布式锁测试接口", tags = "DistributedLockTestApi")
public class DistributedLockTestApi {
    /**
     * The Distributed lock by zookeeper.
     */
    @Autowired
    DistributedLockByZookeeper distributedLockByZookeeper;
    private final static String PATH = "testv3";

    /**
     * Gets lock 1.
     *
     * @return the lock 1
     */
    @GetMapping("/lock1")
    @ApiOperation(value = "获取分布式锁", notes = "获取分布式锁，获取到的期间，其他请求被阻塞，等待上一个释放锁资源",
            response = ApiResult.class)
    public ApiResult getLock1() {
        Boolean flag = false;
        distributedLockByZookeeper.acquireDistributedLock(PATH);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            flag = distributedLockByZookeeper.releaseDistributedLock(PATH);
        }
        flag = distributedLockByZookeeper.releaseDistributedLock(PATH);
        if (flag) {
            return ApiResult.prepare().success("释放锁资源成功!");
        }
        return ApiResult.prepare().error(false, 500, "释放锁资源失败");
    }

    /**
     * Gets lock 2.
     *
     * @return the lock 2
     */
    @GetMapping("/lock2")
    @ApiOperation(value = "获取分布式锁", notes = "获取分布式锁，获取到的期间，其他请求被阻塞，等待上一个释放锁资源",
            response = ApiResult.class)
    public ApiResult getLock2() {
        Boolean flag;
        distributedLockByZookeeper.acquireDistributedLock(PATH);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            flag = distributedLockByZookeeper.releaseDistributedLock(PATH);
        }
        flag = distributedLockByZookeeper.releaseDistributedLock(PATH);
        if (flag) {
            return ApiResult.prepare().success("释放锁资源成功!");
        }
        return ApiResult.prepare().error(false, 500, "释放锁资源失败");
    }
}

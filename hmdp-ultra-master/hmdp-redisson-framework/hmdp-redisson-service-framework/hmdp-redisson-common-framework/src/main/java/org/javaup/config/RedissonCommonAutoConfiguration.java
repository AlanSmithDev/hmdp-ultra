package org.javaup.config;

import org.javaup.handle.RedissonDataHandle;
import org.javaup.locallock.LocalLockCache;
import org.javaup.lockinfo.factory.LockInfoHandleFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: 黑马点评-plus升级版实战项目。添加 阿星不是程序员 微信，添加时备注 点评 来获取项目的完整资料 
 * @description: redisson通用配置
 * @author: 阿星不是程序员
 **/
@AutoConfigureBefore(value = {RedissonAutoConfigurationV2.class, RedissonAutoConfiguration.class})
@EnableConfigurationProperties(RedissonBaseProperties.class)
public class RedissonCommonAutoConfiguration {
    
    private final AtomicInteger executeTaskThreadCount = new AtomicInteger(1);
    
    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties,
                                         RedissonBaseProperties redissonBaseProperties,
                                         Environment environment){
        Config config = new Config();
        String prefix = "redis://";
        Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
        if (method != null && (Boolean)ReflectionUtils.invokeMethod(method, redisProperties)) {
            prefix = "rediss://";
        }
        String host = firstNonBlank(
                environment.getProperty("spring.data.redis.host"),
                environment.getProperty("spring.redis.host"),
                redisProperties.getHost(),
                "localhost"
        );
        Integer port = firstNonNull(
                environment.getProperty("spring.data.redis.port", Integer.class),
                environment.getProperty("spring.redis.port", Integer.class),
                redisProperties.getPort(),
                6379
        );
        Integer database = firstNonNull(
                environment.getProperty("spring.data.redis.database", Integer.class),
                environment.getProperty("spring.redis.database", Integer.class),
                redisProperties.getDatabase(),
                0
        );
        String password = firstNonBlank(
                environment.getProperty("spring.data.redis.password"),
                environment.getProperty("spring.redis.password"),
                redisProperties.getPassword()
        );
        config.useSingleServer()
                .setAddress(prefix + host + ":" + port)
                .setConnectTimeout(1000)
                .setDatabase(database);
        if (StringUtils.hasText(password)) {
            config.useSingleServer().setPassword(password);
        }
        config.setThreads(redissonBaseProperties.getThreads());
        config.setNettyThreads(redissonBaseProperties.getNettyThreads());
        if (Objects.nonNull(redissonBaseProperties.getCorePoolSize()) && 
                Objects.nonNull(redissonBaseProperties.getMaximumPoolSize())) {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    redissonBaseProperties.getCorePoolSize(),
                    redissonBaseProperties.getMaximumPoolSize(),
                    redissonBaseProperties.getKeepAliveTime(),
                    redissonBaseProperties.getUnit(),
                    new LinkedBlockingQueue<>(redissonBaseProperties.getWorkQueueSize()),
                    r -> new Thread(Thread.currentThread().getThreadGroup(), r,
                            "redisson-thread-" + executeTaskThreadCount.getAndIncrement()));
            config.setExecutor(threadPoolExecutor);
        }
        return Redisson.create(config);
    }
    
    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
    
    @SafeVarargs
    private static <T> T firstNonNull(T... values) {
        if (values == null) {
            return null;
        }
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    @Bean
    public RedissonDataHandle redissonDataHandle(RedissonClient redissonClient){
        return new RedissonDataHandle(redissonClient);
    }
    
    @Bean
    public LocalLockCache localLockCache(){
        return new LocalLockCache();
    }
    
    @Bean
    public LockInfoHandleFactory lockInfoHandleFactory(){
        return new LockInfoHandleFactory();
    }
}

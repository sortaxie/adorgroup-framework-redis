package com.adorgroup.framework.redis.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableCaching
public class RedisCacheConfig  extends CachingConfigurerSupport {
    Logger logger = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    //连接超时时间（毫秒）
    @Value("${spring.redis.timeout:10000}")
    private int timeout;

    //连接池中的最大空闲连接
    @Value("${spring.redis.pool.max-idle:8}")
    private int maxIdle;
    //连接池中的最小空闲连接
    @Value("${spring.redis.pool.min-idle:0}")
    private int minIdle;
    //连接池最大阻塞等待时间（使用负值表示没有限制）
    @Value("${spring.redis.pool.max-wait:-1}")
    private long maxWaitMillis;

    @Value("${spring.redis.password:}")
    private String password;
    //设置默认过期时间
    @Value("${spring.redis.expiration:3600}")
    private long defaultExpiration;

    //此注解用于只引用该包,不启用相关redis功能
    @ConditionalOnProperty(prefix = "framework.redis", value = {"enable"}, havingValue = "true",matchIfMissing = true)
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinIdle(minIdle);
       // config.setTestOnBorrow(true);
        //config.setTestWhileIdle(true);
        //config.setTimeBetweenEvictionRunsMillis(30000);
       // config.setMinEvictableIdleTimeMillis(60000);
       // config.setSoftMinEvictableIdleTimeMillis(60000);
        return config;
    }


//    @Bean
//    public RedisHttpSessionConfiguration redisHttpSessionConfiguration(){
//        logger.info("edisHttpSessionConfiguration 注入成功！！");
//        RedisHttpSessionConfiguration redisHttpSessionConfiguration = new RedisHttpSessionConfiguration();
//        redisHttpSessionConfiguration.setMaxInactiveIntervalInSeconds(2);
//        return redisHttpSessionConfiguration;
//    }
    @ConditionalOnProperty(prefix = "framework.redis", value = {"enable"}, havingValue = "true",matchIfMissing = true)
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig());
        redisConnectionFactory.setDatabase(database);
        redisConnectionFactory.setHostName(host);
        redisConnectionFactory.setPort(port);
        redisConnectionFactory.setPassword(password);
        redisConnectionFactory.setTimeout(timeout);
        redisConnectionFactory.setUsePool(true);
        return redisConnectionFactory;
    }

    @ConditionalOnProperty(prefix = "framework.redis", value = {"enable"}, havingValue = "true",matchIfMissing = true)
    @Bean
    public JedisPool redisPoolFactory() {
        logger.info("JedisPool inject success ");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMinIdle(minIdle);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);

        return jedisPool;
    }

//    @Bean
//    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory cf) {
//        RedisTemplate<Object , Object> template = new RedisTemplate<Object,
//                Object>();
//        template.setConnectionFactory(cf);
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new
//                Jackson2JsonRedisSerializer(Object.class) ;
//        ObjectMapper om = new ObjectMapper() ;
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY) ;
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL) ;
//        jackson2JsonRedisSerializer.setObjectMapper(om) ;
//        template.setValueSerializer (jackson2JsonRedisSerializer);
//        template.setKeySerializer (new StringRedisSerializer());
//        template.afterPropertiesSet ();
//        return template ;
//    }

    @ConditionalOnProperty(prefix = "framework.redis", value = {"enable"}, havingValue = "true",matchIfMissing = true)
    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object , Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new
                Jackson2JsonRedisSerializer(Object.class) ;
        ObjectMapper om = new ObjectMapper() ;
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY) ;
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL) ;
        jackson2JsonRedisSerializer.setObjectMapper(om) ;
        template.setValueSerializer (jackson2JsonRedisSerializer);
        template.setKeySerializer (new StringRedisSerializer());
        template.afterPropertiesSet ();
        return template ;
    }


    @ConditionalOnProperty(prefix = "framework.redis", value = {"enable"}, havingValue = "true",matchIfMissing = true)
    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        //默认超时时间,单位秒
        logger.info("defaultExpiration:"+defaultExpiration);
        cacheManager.setDefaultExpiration(defaultExpiration);
        //根据缓存名称设置超时时间,0为不超时
        Map<String,Long> expires = new ConcurrentHashMap<>();
        cacheManager.setExpires(expires);
        return cacheManager;
    }



}
package com.adorgroup.framework.redis.session;

//mport org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


//maxInactiveIntervalInSeconds为SpringSession的过期时间（单位：秒）
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisSessionConfig  {

    //冒号后的值为没有配置文件时，自动装载的默认值
//    @Value("${redis.hostname:localhost}")
//    String hostName;
//    @Value("${redis.port:6379}")
//    int port;

//    @Bean
//    public JedisConnectionFactory connectionFactory() {
//        JedisConnectionFactory connection = new JedisConnectionFactory();
//        connection.setPort(port);
//        connection.setHostName(hostName);
//        return connection;
//    }


}

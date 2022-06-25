package ru.azor.wdc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Value("${redis-host}")
    private String redisHostName;
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisHostName);
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, ConcurrentHashMap<String, String>> redisTemplate() {
        RedisTemplate<String, ConcurrentHashMap<String, String>> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        log.info("Redis hostname: " + jedisConnectionFactory().getHostName());
        return template;
    }
}

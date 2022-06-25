package ru.azor.wdc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.azor.wdc.enums.ErrorValues;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, ConcurrentHashMap<String, String>> redisTemplate;
    @Value("${redis-key}")
    private String redisKey;

    public String getCurrentWeatherFromRedis(String key) {
        try {
            if (key == null) {
                log.info("Redis: " + ErrorValues.NOT_FOUND.name());
                return ErrorValues.NOT_FOUND.name();
            }
            ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                result = redisTemplate.opsForValue().get(redisKey);
            }
            if (result != null && result.containsKey(key)) {
                log.info("Redis: " + result.get(key));
                return result.get(key);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.info("Redis: " + ErrorValues.NOT_FOUND.name());
            return ErrorValues.NOT_FOUND.name();
        }
        log.info("Redis: " + ErrorValues.NOT_FOUND.name());
        return ErrorValues.NOT_FOUND.name();
    }

    public void setCurrentWeatherToRedis(String key, String json) {
        try {
            if (key == null || json == null || key.isBlank() || json.isBlank()) {
                log.error("Set to redis: " + ErrorValues.INVALID_FIELDS.name());
                return;
            }
            ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                result = redisTemplate.opsForValue().get(redisKey);
            }
            assert result != null;
            if (!result.containsKey(key)) {
                result.put(key, json);
                redisTemplate.opsForValue().set(redisKey, result);
                log.info("Save to Redis: " + result.get(key));
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    public Set<String> getAllCitiesAndTimestampsFromRedis() {
        try {
            return Objects.requireNonNull(redisTemplate.opsForValue().get(redisKey))
                    .keySet();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return new HashSet<>();
    }

    public ConcurrentHashMap<String, String> getAllWeatherFromRedis() {
        ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
        try {
            result = redisTemplate.opsForValue().get(redisKey);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    public ConcurrentHashMap<String, String> getAllWeatherByCityNameFromRedis(String city) {
        return getMap(city);
    }

    public ConcurrentHashMap<String, String> getAllWeatherByDateTimeFromRedis(String dateTime) {
        return getMap(dateTime);
    }

    @Scheduled(cron = "${clear-redis-cron}")
    @Async
    public void clearRedisWeatherData() {
        try {
            ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                result = redisTemplate.opsForValue().get(redisKey);
            }
            assert result != null;
            result.clear();
            redisTemplate.opsForValue().set(redisKey, result);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private ConcurrentHashMap<String, String> getMap(String searchLine) {
        searchLine = searchLine.toUpperCase();
        try {
            ConcurrentHashMap<String, String> redisMap = redisTemplate.opsForValue().get(redisKey);
            ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
            for (String key : Objects.requireNonNull(redisMap).keySet()) {
                if (key.contains(searchLine)) {
                    result.put(key, redisMap.get(key));
                }
            }
            return result;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return new ConcurrentHashMap<>();
    }
}

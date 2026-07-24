package com.specsense.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_HOUR = 1;

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value, CACHE_HOUR, TimeUnit.HOURS);
        } catch (Exception e) {
            // Redis error, ignore and continue without cache
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && clazz.isInstance(value)) {
                return (T) value;
            }
        } catch (Exception e) {
            // Redis error, ignore and continue without cache
        }
        return null;
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            // Redis error, ignore
        }
    }

    public void deleteByPattern(String pattern) {
        try {
            redisTemplate.delete(redisTemplate.keys(pattern));
        } catch (Exception e) {
            // Redis error, ignore
        }
    }

    // Cache keys
    public static String keyProducts(String locale) { return "products:" + locale; }
    public static String keyProduct(Long id) { return "product:" + id; }
    public static String keyProductFeatured(String locale) { return "products:featured:" + locale; }
    public static String keyCategories() { return "categories"; }
    public static String keyNews(String locale) { return "news:" + locale; }
    public static String keyNewsFeatured(String locale) { return "news:featured:" + locale; }
    public static String keyApplications(String locale) { return "applications:" + locale; }
    public static String keyServices(String locale) { return "services:" + locale; }
    public static String keyDownloads(String locale) { return "downloads:" + locale; }
    public static String keyCompany() { return "company:1"; }
    public static String keyBrands(String locale) { return "brands:" + locale; }
    public static String keyBrandCategories(String locale) { return "brandCategories:" + locale; }
    public static String keyBanners(String locale) { return "banners:" + locale; }
    public static String keyNavigation(String position) { return "navigation:" + position; }
}

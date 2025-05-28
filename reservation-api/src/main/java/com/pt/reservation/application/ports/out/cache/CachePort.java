package com.pt.reservation.application.ports.out.cache;

public interface CachePort {
    void save(String key, Object value);
    <T> T get(String key, Class<T> type);
    void delete(String key);
}

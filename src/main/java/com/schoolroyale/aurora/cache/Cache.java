package com.schoolroyale.aurora.cache;

import com.schoolroyale.aurora.time.Expirable;

import java.util.Map;
import java.util.Optional;

public interface Cache<K, V> {

    Optional<V> get(K key);

    void put(K key, V value);
    
    Optional<Expirable<V>> getExpirable(K key);

    Map<K, Expirable<V>> asMap();

}

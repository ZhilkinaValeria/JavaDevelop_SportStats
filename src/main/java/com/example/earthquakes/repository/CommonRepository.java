package com.example.earthquakes.repository;

import java.util.Collection;

public interface CommonRepository<T> {
    T save(T domain);
    Iterable<T> save(Collection<T> domains);
    void delete(String id);
    void delete(T domain);
    T findById(String id);
    Iterable<T> findAll();
    boolean exists(String id);
    long count();
}
package com.ceos23.cgv_clone.store.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class LockRepository {

    private final JdbcTemplate jdbcTemplate;

    public LockRepository(@Qualifier("lockDataSource") DataSource lockDataSource) {
        this.jdbcTemplate = new JdbcTemplate(lockDataSource);
    }

    public boolean getLock(String key, int timeoutSeconds) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT GET_LOCK(?, ?)",
                Integer.class,
                key,
                timeoutSeconds
        );
        return result != null && result == 1;
    }

    public void releaseLock(String key) {
        jdbcTemplate.queryForObject(
                "SELECT RELEASE_LOCK(?)",
                Integer.class,
                key
        );
    }
}

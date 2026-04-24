package com.ceos23.cgv_clone.global.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class LockRepository {

    private final DataSource lockDataSource;

    public LockRepository(@Qualifier("lockDataSource") DataSource lockDataSource) {
        this.lockDataSource = lockDataSource;
    }

    public Connection acquireLock(String key, int timeoutSeconds) {
        Connection conn = null;
        try {
            conn = lockDataSource.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("SELECT GET_LOCK(?, ?)")) {
                ps.setString(1, key);
                ps.setInt(2, timeoutSeconds);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 1) {
                        return conn;   // 획득한 커넥션을 그대로 반환 (열린 상태 유지)
                    }
                }
            }
            conn.close();   // 실패 시 반납
            return null;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Failed to acquire lock: " + key, e);
        }
    }

    public void releaseLock(Connection conn, String key) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT RELEASE_LOCK(?)")) {
            ps.setString(1, key);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to release lock: " + key, e);
        } finally {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}


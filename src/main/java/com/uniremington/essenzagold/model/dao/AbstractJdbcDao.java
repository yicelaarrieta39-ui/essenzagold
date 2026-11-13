package com.uniremington.essenzagold.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.dao.DataRetrievalFailureException;

abstract class AbstractJdbcDao {

    protected final JdbcTemplate jdbcTemplate;

    AbstractJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected int insertReturningId(String sql, Object... values) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }
            return statement;
        }, keyHolder);

        Map<String, Object> generatedKeys = keyHolder.getKeys();
        Number generatedId = generatedKeys == null
                ? null
                : generatedKeys.values().stream()
                        .filter(Number.class::isInstance)
                        .map(Number.class::cast)
                        .findFirst()
                        .orElse(null);
        if (generatedId == null) {
            throw new DataRetrievalFailureException("The insert did not return a generated identifier");
        }
        return generatedId.intValue();
    }

    protected static LocalDateTime localDateTime(ResultSet resultSet, String column) throws SQLException {
        var timestamp = resultSet.getTimestamp(column);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    protected static <E extends Enum<E>> E enumValue(Class<E> enumType, String value) {
        return value == null ? null : Enum.valueOf(enumType, value);
    }

    protected static String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }
}

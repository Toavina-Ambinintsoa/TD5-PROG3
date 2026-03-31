package com.example.TD5.repository;

import com.example.TD5.entity.StockMovement;
import com.example.TD5.entity.enums.MovementType;
import com.example.TD5.entity.enums.StockUnit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class StockMovementRepository {

    private final JdbcTemplate jdbc;

    public StockMovementRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StockMovement> findByIngredientIdBetween(Long ingredientId, Instant from, Instant to) {
        String sql = """
                SELECT id, creation_datetime, unit, quantity, type
                FROM stock_movement
                WHERE id_ingredient = ?
                  AND creation_datetime >= ?
                  AND creation_datetime <= ?
                """;
        return jdbc.query(sql, (rs, rowNum) -> new StockMovement(
                rs.getLong("id"),
                rs.getTimestamp("creation_datetime").toInstant(),
                StockUnit.valueOf(rs.getString("unit")),
                rs.getDouble("quantity"),
                MovementType.valueOf(rs.getString("type"))
        ), ingredientId, Timestamp.from(from), Timestamp.from(to));
    }

    public List<StockMovement> saveAll(Long ingredientId, List<StockMovement> movements) {
        String sql = """
                INSERT INTO stock_movement (id_ingredient, creation_datetime, unit, quantity, type)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id, creation_datetime, unit, quantity, type
                """;
        return movements.stream().map(sm ->
                jdbc.queryForObject(sql, (rs, rowNum) -> new StockMovement(
                        rs.getLong("id"),
                        rs.getTimestamp("creation_datetime").toInstant(),
                        StockUnit.valueOf(rs.getString("unit")),
                        rs.getDouble("quantity"),
                        MovementType.valueOf(rs.getString("type"))
                ), ingredientId, Timestamp.from(sm.getCreatedAt()), sm.getUnit().name(), sm.getValue(), sm.getType().name())
        ).toList();
    }
}
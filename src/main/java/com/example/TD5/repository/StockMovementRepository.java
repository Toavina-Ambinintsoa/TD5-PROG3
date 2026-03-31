package com.example.TD5.repository;

import com.example.TD5.DataSource;
import com.example.TD5.entity.StockMovement;
import com.example.TD5.entity.enums.MovementType;
import com.example.TD5.entity.enums.StockUnit;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StockMovementRepository {

    public List<StockMovement> findByIngredientIdBetween(Long ingredientId, Instant from, Instant to) {

        String sql = """
                SELECT id, creation_datetime, unit, quantity, type
                FROM stock_movement
                WHERE id_ingredient = ?
                  AND creation_datetime >= ?
                  AND creation_datetime <= ?
                """;

        List<StockMovement> result = new ArrayList<>();

        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, ingredientId);
            stmt.setTimestamp(2, Timestamp.from(from));
            stmt.setTimestamp(3, Timestamp.from(to));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(new StockMovement(
                        rs.getLong("id"),
                        rs.getTimestamp("creation_datetime").toInstant(),
                        StockUnit.valueOf(rs.getString("unit")),
                        rs.getDouble("quantity"),
                        MovementType.valueOf(rs.getString("type"))
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<StockMovement> saveAll(Long ingredientId, List<StockMovement> movements) {

        String sql = """
                INSERT INTO stock_movement (id_ingredient, creation_datetime, unit, quantity, type)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id, creation_datetime, unit, quantity, type
                """;

        List<StockMovement> result = new ArrayList<>();

        try (Connection conn = DataSource.getConnection()) {

            for (StockMovement sm : movements) {

                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setLong(1, ingredientId);
                stmt.setTimestamp(2, Timestamp.from(sm.getCreatedAt()));
                stmt.setString(3, sm.getUnit().name());
                stmt.setDouble(4, sm.getValue());
                stmt.setString(5, sm.getType().name());

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    result.add(new StockMovement(
                            rs.getLong("id"),
                            rs.getTimestamp("creation_datetime").toInstant(),
                            StockUnit.valueOf(rs.getString("unit")),
                            rs.getDouble("quantity"),
                            MovementType.valueOf(rs.getString("type"))
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
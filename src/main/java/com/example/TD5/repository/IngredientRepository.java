package com.example.TD5.repository;

import com.example.TD5.entity.Ingredient;
import com.example.TD5.entity.StockValue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class IngredientRepository {

    private final JdbcTemplate jdbc;

    public IngredientRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Ingredient> findAll() {
        String sql = "SELECT id, name, category, price FROM ingredient";
        return jdbc.query(sql, (rs, rowNum) -> new Ingredient(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price")
        ));
    }

    public Optional<Ingredient> findById(Long id) {
        String sql = "SELECT id, name, category, price FROM ingredient WHERE id = ?";
        List<Ingredient> result = jdbc.query(sql, (rs, rowNum) -> new Ingredient(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getDouble("price")
        ), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Optional<StockValue> findStockById(Long id, LocalDateTime at, String unit) {
        String sql = """
                SELECT COALESCE(SUM(sm.quantity), 0) AS stock
                FROM stock_movement sm
                WHERE sm.ingredient_id = ?
                  AND sm.unit = ?
                  AND sm.movement_date <= ?
                """;
        Double stock = jdbc.queryForObject(sql, Double.class, id, unit, at);
        return Optional.of(new StockValue(unit, stock != null ? stock : 0.0));
    }
}

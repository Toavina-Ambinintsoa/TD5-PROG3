package com.example.TD5.repository;

import com.example.TD5.entity.Dish;
import com.example.TD5.entity.Ingredient;
import com.example.TD5.entity.enums.DishType;
import com.example.TD5.entity.enums.IngredientType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    private final JdbcTemplate jdbc;

    public DishRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Dish> findAll() {
        String sql = "SELECT id, name, dish_type,selling_price FROM dish";
        List<Dish> dishes = jdbc.query(sql, (rs, rowNum) -> new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("selling_price"),
                        DishType.valueOf(rs.getString("dish_type")),
                        null
                ));
        dishes.forEach(d -> d.setIngredients(findIngredientsByDishId(d.getId())));
        return dishes;
    }

    public Optional<Dish> findById(Long id) {
        String sql = "SELECT id, name, dish_type,selling_price FROM dish WHERE id = ?";
        List<Dish> result = jdbc.query(sql, (rs, rowNum) -> new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("selling_price"),
                        DishType.valueOf(rs.getString("dish_type")),
                        null
                ), id);
        if (result.isEmpty()) return Optional.empty();
        Dish dish = result.get(0);
        dish.setIngredients(findIngredientsByDishId(dish.getId()));
        return Optional.of(dish);
    }

    private List<Ingredient> findIngredientsByDishId(Integer dishId) {
        String sql = """
            SELECT i.id, i.name, i.category, i.price
            FROM ingredient i
            JOIN dish_ingredient di ON di.id_ingredient = i.id
            WHERE di.id_dish = ?
            """;
        return jdbc.query(sql, (rs, rowNum) -> new Ingredient(
                rs.getInt("id"),
                rs.getString("name"),
                IngredientType.valueOf(rs.getString("category")),
                rs.getDouble("price")
        ), dishId);
    }

    public void updateIngredients(Long dishId, List<Ingredient> ingredients) {
        jdbc.update("DELETE FROM dish_ingredient WHERE id_dish = ?", dishId);
        String checkSql = "SELECT COUNT(*) FROM ingredient WHERE id = ?";
        for (Ingredient ing : ingredients) {
            Integer count = jdbc.queryForObject(checkSql, Integer.class, ing.getId());
            if (count != null && count > 0) {
                jdbc.update(
                        "INSERT INTO dish_ingredient (id_dish, id_ingredient) VALUES (?, ?)",
                        dishId, ing.getId()
                );
            }
        }
    }
}

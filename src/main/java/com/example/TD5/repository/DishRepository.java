package com.example.TD5.repository;

import com.example.TD5.DataSource;
import com.example.TD5.entity.Dish;
import com.example.TD5.entity.Ingredient;
import com.example.TD5.entity.enums.DishType;
import com.example.TD5.entity.enums.IngredientType;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DishRepository {

    public List<Dish> findAll() {

        String sql = "SELECT id, name, dish_type, selling_price FROM dish";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Dish dish = new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("selling_price"),
                        DishType.valueOf(rs.getString("dish_type")),
                        null
                );

                dish.setIngredients(findIngredientsByDishId(dish.getId()));
                dishes.add(dish);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dishes;
    }

    public Optional<Dish> findById(Long id) {

        String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
        Dish dish = null;

        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dish = new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("selling_price"),
                        DishType.valueOf(rs.getString("dish_type")),
                        null
                );

                dish.setIngredients(findIngredientsByDishId(dish.getId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(dish);
    }

    private List<Ingredient> findIngredientsByDishId(Integer dishId) {

        String sql = """
            SELECT i.id, i.name, i.category, i.price
            FROM ingredient i
            JOIN dish_ingredient di ON di.id_ingredient = i.id
            WHERE di.id_dish = ?
            """;

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = DataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                ingredients.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        IngredientType.valueOf(rs.getString("category")),
                        rs.getDouble("price")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    public void updateIngredients(Long dishId, List<Ingredient> ingredients) {

        try (Connection conn = DataSource.getConnection()) {

            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM dish_ingredient WHERE id_dish = ?"
            );
            delete.setLong(1, dishId);
            delete.executeUpdate();

            String checkSql = "SELECT COUNT(*) FROM ingredient WHERE id = ?";

            for (Ingredient ing : ingredients) {

                PreparedStatement check = conn.prepareStatement(checkSql);
                check.setInt(1, ing.getId());

                ResultSet rs = check.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {

                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO dish_ingredient (id_dish, id_ingredient) VALUES (?, ?)"
                    );

                    insert.setLong(1, dishId);
                    insert.setInt(2, ing.getId());
                    insert.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package com.example.TD5.controller;

import com.example.TD5.entity.Dish;
import com.example.TD5.entity.Ingredient;
import com.example.TD5.repository.DishRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dishes")
public class DishController {
    private final DishRepository dishRepository;

    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @GetMapping
    public List<Dish> getAll() {
        return dishRepository.findAll();
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<?> updateIngredients(
            @PathVariable Long id,
            @RequestBody(required = false) List<Ingredient> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(400)
                    .body("Request body is mandatory and must contain a list of ingredients.");
        }

        Optional<Dish> dish = dishRepository.findById(id);
        if (dish.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("Dish.id=" + id + " is not found");
        }

        dishRepository.updateIngredients(id, ingredients);
        return ResponseEntity.ok(dishRepository.findById(id).get());
    }
}
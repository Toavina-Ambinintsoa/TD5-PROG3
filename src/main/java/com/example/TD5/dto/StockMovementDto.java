package com.example.TD5.dto;

import com.example.TD5.entity.StockMovement;
import com.example.TD5.entity.enums.MovementType;
import com.example.TD5.entity.enums.StockUnit;

import java.time.Instant;

public class StockMovementDto {
    private Long id;
    private Instant createdAt;
    private StockUnit unit;
    private double value;
    private MovementType type;

    public StockMovementDto(StockMovement sm) {
        this.id = sm.getId();
        this.createdAt = sm.getCreatedAt();
        this.unit = sm.getUnit();
        this.value = sm.getValue();
        this.type = sm.getType();
    }

    public Long getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public StockUnit getUnit() { return unit; }
    public double getValue() { return value; }
    public MovementType getType() { return type; }
}
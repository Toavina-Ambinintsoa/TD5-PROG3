package com.example.TD5.dto;

import com.example.TD5.entity.enums.MovementType;
import com.example.TD5.entity.enums.StockUnit;

public class StockMovementCreationDto {
    private StockUnit unit;
    private double value;
    private MovementType type;

    public StockUnit getUnit() { return unit; }
    public void setUnit(StockUnit unit) { this.unit = unit; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }
}
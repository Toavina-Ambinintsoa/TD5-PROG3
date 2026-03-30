package com.example.TD5.entity;

public class StockValue {
    private String unit;
    private double value;

    public StockValue() {}

    public StockValue(String unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}

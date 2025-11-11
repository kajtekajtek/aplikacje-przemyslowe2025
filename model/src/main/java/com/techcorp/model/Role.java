package com.techcorp.model;

public enum Role {
    CEO(1, 25000),
    VP(2, 18000),
    MANAGER(3, 12000),
    ENGINEER(4, 8000),
    INTERN(5, 3000);
    
    private final int level;
    private final int baseSalary;

    Role(int level, int baseSalary) {
        this.level      = level;
        this.baseSalary = baseSalary;
    }
    
    public int getLevel()      { return level; }
    public int getBaseSalary() { return baseSalary; }
}

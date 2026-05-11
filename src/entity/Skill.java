package entity;

import util.Util;

public class Skill {
    private final String name;

    private final int minDamage;
    private final int maxDamage;

    private final int manaCost;

    private final int baseCooldown;
    private int currentCooldown = 0;

    public Skill(String name, int minDamage, int maxDamage, int manaCost, int baseCooldown) {
        this.name = name;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.manaCost = manaCost;
        this.baseCooldown = baseCooldown;
    }

    public int useSkill() {
        currentCooldown = baseCooldown;
        return Util.rng(minDamage, maxDamage);
    }

    public void reduceCooldown() {
        if (isCooldown()) {
            currentCooldown--;
        }
    }

    public void resetCooldown() {
        currentCooldown = 0;
    }

    public boolean isCooldown() {
        return currentCooldown > 0;
    }

    public String getName() {
        return name;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getBaseCooldown() {
        return baseCooldown;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }
}
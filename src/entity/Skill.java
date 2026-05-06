package entity;

import util.Util;

/**
 * A single combat skill.
 *
 * Thread safety: all mutable state (currentCooldown) is accessed through
 * synchronized methods so Entity can safely call them from the EDT while
 * a render thread reads them concurrently.
 */
public class Skill {

    private final String name;
    private final int minDamage;
    private final int maxDamage;
    private final int manaCost;
    private final int baseCooldown;

    private volatile int currentCooldown = 0;

    public Skill(String name,
                 int minDamage, int maxDamage,
                 int manaCost,
                 int baseCooldown) {
        this.name         = name;
        this.minDamage    = minDamage;
        this.maxDamage    = maxDamage;
        this.manaCost     = manaCost;
        this.baseCooldown = baseCooldown;
    }

    // ── Actions ─────────────────────────────────────────────────────────────

    /** Rolls damage and sets the cooldown. Returns damage dealt. */
    public synchronized int useSkill() {
        currentCooldown = baseCooldown;
        return Util.rng(minDamage, maxDamage);
    }

    public synchronized void reduceCooldown() {
        if (currentCooldown > 0) currentCooldown--;
    }

    public synchronized void resetCooldown() {
        currentCooldown = 0;
    }

    // ── Queries ─────────────────────────────────────────────────────────────

    public boolean isCooldown()     { return currentCooldown > 0; }
    public String  getName()        { return name; }
    public int     getMinDamage()   { return minDamage; }
    public int     getMaxDamage()   { return maxDamage; }
    public int     getManaCost()    { return manaCost; }
    public int     getBaseCooldown(){ return baseCooldown; }
    public int     getCurrentCooldown() { return currentCooldown; }
}

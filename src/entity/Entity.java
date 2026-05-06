package entity;

import util.Util;

/**
 * A battle entity (player or enemy).
 *
 * Thread safety:
 *   HP and mana are declared volatile so the game loop thread can read
 *   them for rendering while the EDT writes them from action handlers
 *   without stale-cache issues.  All compound read-modify-write operations
 *   (takeDamage, regenMana, etc.) are synchronized on `this` so they are
 *   atomic even if called from different threads.
 */
public class Entity {

    protected final String name;
    protected final int baseHP   = 500;
    protected final int baseMana = 200;

    protected volatile int currentHP;
    protected volatile int currentMana;

    protected final Skill skill1;
    protected final Skill skill2;
    protected final Skill skill3;

    public Entity(String name,
                  String skill1Name,
                  String skill2Name,
                  String skill3Name) {
        this.name = name;
        this.currentHP   = baseHP;
        this.currentMana = baseMana;

        skill1 = new Skill(skill1Name,  50,  60,  25, 1);
        skill2 = new Skill(skill2Name,  60,  70,  50, 2);
        skill3 = new Skill(skill3Name, 100, 150, 100, 3);
    }

    // ── Combat actions ──────────────────────────────────────────────────────

    public synchronized void basicAttack(Entity target) {
        int damage = Util.rng(40, 50);
        target.takeDamage(damage);
    }

    public synchronized void takeDamage(int damage) {
        if (isAlive()) {
            currentHP = Math.clamp(currentHP - damage, 0, baseHP);
        }
    }

    public synchronized void regenMana() {
        if (isAlive()) {
            currentMana = Math.clamp(currentMana + 20, 0, baseMana);
        }
    }

    /**
     * Attempt to use a skill by index (1–3).
     * Returns the damage dealt, or -1 if the skill could not be used
     * (on cooldown or not enough mana).
     */
    public synchronized int useSkill(int skillIndex, Entity target) {
        Skill selected = getSkill(skillIndex);
        if (selected == null)                             return -1;
        if (selected.isCooldown())                        return -1;
        if (currentMana < selected.getManaCost())         return -1;

        int damage = selected.useSkill();
        target.takeDamage(damage);
        currentMana -= selected.getManaCost();
        return damage;
    }

    public synchronized void reduceCooldowns() {
        skill1.reduceCooldown();
        skill2.reduceCooldown();
        skill3.reduceCooldown();
    }

    public synchronized void resetCharacter() {
        currentHP   = baseHP;
        currentMana = baseMana;
        skill1.resetCooldown();
        skill2.resetCooldown();
        skill3.resetCooldown();
    }

    // ── Healing (arcade) ────────────────────────────────────────────────────

    public synchronized void healHP(int amount) {
        currentHP = Math.min(currentHP + amount, baseHP);
    }

    public synchronized void healMana(int amount) {
        currentMana = Math.min(currentMana + amount, baseMana);
    }

    // ── Queries ─────────────────────────────────────────────────────────────

    public boolean isAlive() {
        return currentHP > 0;
    }

    public String getName()    { return name; }
    public int getBaseHP()     { return baseHP; }
    public int getBaseMana()   { return baseMana; }
    public int getCurrentHP()  { return currentHP; }
    public int getCurrentMana(){ return currentMana; }

    public Skill getSkill(int index) {
        return switch (index) {
            case 1 -> skill1;
            case 2 -> skill2;
            case 3 -> skill3;
            default -> null;
        };
    }
}

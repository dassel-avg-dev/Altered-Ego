package entity;

import util.Util;

public class Entity {
    protected final String name;

    protected final int baseHP = 500;
    protected final int baseMana = 200;

    protected int currentHP;
    protected int currentMana;

    protected final Skill skill1;
    protected final Skill skill2;
    protected final Skill skill3;

    public Entity(String name, String skill1, String skill2, String skill3) {
        this.name = name;
        this.currentHP = baseHP;
        this.currentMana = baseMana;

        this.skill1 = new Skill(skill1, 50, 60, 25, 1);
        this.skill2 = new Skill(skill2, 60, 70, 50, 2);
        this.skill3 = new Skill(skill3, 100, 150, 100, 3);
    }

    public void takeHP(int amount) {
        if (isAlive()) {
            currentHP = Math.clamp(currentHP - amount, 0, baseHP);
        }
    }

    public void takeMana(int amount) {
        if (isAlive()) {
            currentMana = Math.clamp(currentMana - amount, 0, baseMana);
        }
    }

    public void healHP(int amount) {
        if (isAlive()) {
            currentHP = Math.clamp(currentHP + amount, 0, baseHP);
        }
    }

    public void healMana(int amount) {
        if (isAlive()) {
            currentMana = Math.clamp(currentMana + amount, 0, baseMana);
        }
    }

    public void basicAttack(Entity target) {
        int damage = Util.rng(40, 50);
        target.takeHP(damage);
    }

    public int useSkill(int skillIndex, Entity target) {
        Skill selected = getSkill(skillIndex);
        if (selected == null) return -1;
        if (selected.isCooldown()) return -1;
        if (currentMana < selected.getManaCost()) return -1;

        int damage = selected.useSkill();
        target.takeHP(damage);
        takeMana(selected.getManaCost());
        return damage;
    }

    public void reduceCooldowns() {
        skill1.reduceCooldown();
        skill2.reduceCooldown();
        skill3.reduceCooldown();
    }

    public void resetCharacter() {
        currentHP = baseHP;
        currentMana = baseMana;
        skill1.resetCooldown();
        skill2.resetCooldown();
        skill3.resetCooldown();
    }

    public boolean isAlive() {
        return currentHP > 0;
    }

    public String getName() {
        return name;
    }

    public int getBaseHP() {
        return baseHP;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public Skill getSkill(int index) {
        return switch (index) {
            case 1 -> skill1;
            case 2 -> skill2;
            case 3 -> skill3;
            default -> null;
        };
    }
}
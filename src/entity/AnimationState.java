package entity;

/**
 * All possible animation states for a character sprite.
 * IDLE loops forever; everything else is a one-shot that returns to IDLE.
 */
public enum AnimationState {
    IDLE,
    BASIC_ATTACK,
    TAKE_DAMAGE,
    SKILL_1,
    SKILL_2,
    SKILL_3,
    DEATH
}

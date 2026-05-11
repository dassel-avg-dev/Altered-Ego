package screen.ui;

import graphic.GraphicState;
import entity.Entity;
import entity.Skill;
import graphic.Graphic;
import screen.Screen;
import screen.ScreenBase;
import util.GameBattle;
import entity.EntityState;
import screen.ScreenState;

import javax.swing.*;
import java.awt.*;

/**
 * Arcade battle screen — player fights a gauntlet of enemies one by one.
 *
 * Sprite setup (same convention as Battle.java):
 *   One 2D sprite sheet per character at:
 *     /sprites/<character_enum_name_lowercase>.png
 *   Rows map to AnimationState.values() in order:
 *     row 0 → IDLE
 *     row 1 → BASIC_ATTACK
 *     row 2 → TAKE_DAMAGE
 *     row 3 → SKILL_1
 *     row 4 → SKILL_2
 *     row 5 → SKILL_3
 *     row 6 → DEATH
 *
 *   Edit FRAME_WIDTH, FRAME_HEIGHT, and FRAME_COUNTS to match your sheets.
 *
 * Key arcade difference from Battle:
 *   - Player HP/mana carry over between fights — p1Graphic is only
 *     reloaded on fight 1. p2Graphic reloads every fight as the enemy changes.
 *   - There is no round system — fights are numbered sequentially.
 *   - The turn timer always runs (always CPU vs player).
 */
public class BattleArcade extends ScreenBase {

    // ── Sprite sheet config — edit to match your sheets ─────────────────────
    private static final int   FRAME_WIDTH  = 64;
    private static final int   FRAME_HEIGHT = 64;
    /** Frame counts per row: [idle, basicAtk, takeDmg, sk1, sk2, sk3, death] */
    // Frame counts are now per-character via GameCharacter.getFrameCounts()

    /** On-screen bounds for each sprite: x, y, width, height */
    private static final int[] P1_SPRITE_BOUNDS = {  60, 260, 200, 200 };
    private static final int[] P2_SPRITE_BOUNDS = { 460, 260, 200, 200 };

    // ── Sprite state ─────────────────────────────────────────────────────────
    private final Graphic p1Graphic = new Graphic();
    private final Graphic p2Graphic = new Graphic();

    // ── Battle state ─────────────────────────────────────────────────────────
    private static final int TURN_TIME = 10;

    private Entity  player1;
    private Entity  player2;
    private boolean isPlayer1Turn;
    private boolean battleOver;

    private Timer turnTimer;
    private int   turnTimeLeft;

    // ── UI components ─────────────────────────────────────────────────────────
    private JLabel       p1NameLabel, p1HpLabel, p1ManaLabel;
    private JProgressBar p1HpBar, p1ManaBar;

    private JLabel       p2NameLabel, p2HpLabel, p2ManaLabel;
    private JProgressBar p2HpBar, p2ManaBar;

    private JLabel roundLabel;
    private JLabel arcadeProgressLabel;
    private JLabel countdownLabel;
    private JLabel turnLabel;
    private JLabel combatLog;

    private JButton basicAttackButton;
    private JButton skill1Button;
    private JButton skill2Button;
    private JButton skill3Button;

    // ── Construction ──────────────────────────────────────────────────────────

    public BattleArcade(Screen screen) {
        super(screen);
        // Player 2 always faces left toward player 1
        p2Graphic.setFlipped(true);
    }

    // ── ScreenBase hooks ──────────────────────────────────────────────────────

    @Override
    protected void initializeUI() {
        // ── P1 HUD ────────────────────────────────────────────────────────────
        p1NameLabel = createLabel("P1", 45, 30, 200, 20);
        p1NameLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        p1HpLabel = createLabel("HP: 500/500", 45, 60, 200, 20);
        p1HpLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        p1HpBar = createBar(45, 78, 200, 14, Color.decode("#4caf50"));

        p1ManaLabel = createLabel("MP: 200/200", 45, 100, 200, 20);
        p1ManaLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        p1ManaBar = createBar(45, 118, 200, 14, Color.decode("#2196f3"));

        // ── P2 / Enemy HUD ────────────────────────────────────────────────────
        p2NameLabel = createLabel("Enemy", 465, 30, 200, 20);
        p2NameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        p2HpLabel = createLabel("HP: 500/500", 465, 60, 200, 20);
        p2HpLabel.setHorizontalAlignment(SwingConstants.LEFT);

        p2HpBar = createBar(465, 78, 200, 14, Color.decode("#4caf50"));

        p2ManaLabel = createLabel("MP: 200/200", 465, 100, 200, 20);
        p2ManaLabel.setHorizontalAlignment(SwingConstants.LEFT);

        p2ManaBar = createBar(465, 118, 200, 14, Color.decode("#2196f3"));

        // ── Centre info ───────────────────────────────────────────────────────
        roundLabel = createLabel("Fight 1", 255, 10, 200, 25);
        roundLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 16));
        roundLabel.setForeground(Color.DARK_GRAY);

        arcadeProgressLabel = createLabel("", 255, 35, 200, 20);
        arcadeProgressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        arcadeProgressLabel.setFont(new Font("Arial", Font.BOLD, 13));
        arcadeProgressLabel.setForeground(new Color(0x8B4513));

        countdownLabel = createLabel("", 330, 58, 50, 25);
        countdownLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.BOLD, 16));
        countdownLabel.setForeground(Color.RED);

        turnLabel = createLabel("", 155, 160, 400, 30);
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setForeground(Color.DARK_GRAY);

        combatLog = createLabel("", 55, 475, 600, 30);
        combatLog.setHorizontalAlignment(SwingConstants.CENTER);
        combatLog.setFont(new Font("Arial", Font.ITALIC, 14));
        combatLog.setForeground(Color.DARK_GRAY);

        // ── Action buttons ────────────────────────────────────────────────────
        basicAttackButton = createButton("Basic Attack", 255, 530, 200, 50);
        skill1Button      = createButton("Skill 1",       65, 595, 140, 50);
        skill2Button      = createButton("Skill 2",      285, 595, 140, 50);
        skill3Button      = createButton("Skill 3",      500, 595, 140, 50);

        basicAttackButton.addActionListener(e -> handleAction(0));
        skill1Button     .addActionListener(e -> handleAction(1));
        skill2Button     .addActionListener(e -> handleAction(2));
        skill3Button     .addActionListener(e -> handleAction(3));
    }

    /**
     * Called ~60 fps by ScreenBase's animation timer.
     * Advance both sprite animations every tick.
     */
    @Override
    protected void onAnimationTick() {
        p1Graphic.update();
        p2Graphic.update();
    }

    /**
     * Draw sprites before Swing paints the HUD labels on top.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        p1Graphic.draw(g,
                P1_SPRITE_BOUNDS[0], P1_SPRITE_BOUNDS[1],
                P1_SPRITE_BOUNDS[2], P1_SPRITE_BOUNDS[3]);
        p2Graphic.draw(g,
                P2_SPRITE_BOUNDS[0], P2_SPRITE_BOUNDS[1],
                P2_SPRITE_BOUNDS[2], P2_SPRITE_BOUNDS[3]);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Starts or resumes an arcade fight.
     * GameBattle.resetArcade() must have been called once before the first fight.
     * After each victory, advanceArcadeEnemy() updates playerTwo so this method
     * simply reads the current state.
     * Player HP/mana intentionally carry over between fights.
     */
    public void startBattle() {
        GameBattle battle = screen.getBattle();

        player1 = battle.getEntityOne();
        player2 = battle.getEntityTwo();

        if (player1 == null || player2 == null) {
            combatLog.setText("Error: battle not initialised.");
            return;
        }

        isPlayer1Turn = true;
        battleOver    = false;

        // Player sprite only needs loading on the very first fight
        if (battle.getArcadeEnemiesDefeated() == 0) {
            loadSprites(p1Graphic, battle.getPlayerOne());
            p1Graphic.loopAnimation(GraphicState.IDLE);
        }

        // Enemy changes every fight — always reload
        loadSprites(p2Graphic, battle.getPlayerTwo());
        p2Graphic.loopAnimation(GraphicState.IDLE);

        refreshHUD();
        combatLog.setText("Enemy " + (battle.getArcadeEnemiesDefeated() + 1) + " — Fight!");

        startTurnTimer();
    }

    // ── Sprite loading ────────────────────────────────────────────────────────

    private void loadSprites(Graphic graphic, EntityState character) {
        String path = "/" + character.name().toLowerCase() + ".png";
        graphic.loadAll(path, FRAME_WIDTH, FRAME_HEIGHT, character.getFrameCount());
    }

    // ── Action handling ───────────────────────────────────────────────────────

    private void handleAction(int actionIndex) {
        if (battleOver) return;

        Entity  attacker        = isPlayer1Turn ? player1 : player2;
        Entity  defender        = isPlayer1Turn ? player2 : player1;
        Graphic attackerGraphic = isPlayer1Turn ? p1Graphic : p2Graphic;
        Graphic defenderGraphic = isPlayer1Turn ? p2Graphic : p1Graphic;

        String logMessage;

        if (actionIndex == 0) {
            // ── Basic attack ──────────────────────────────────────────────────
            int hpBefore = defender.getCurrentHP();
            attacker.basicAttack(defender);
            int damage = hpBefore - defender.getCurrentHP();

            attackerGraphic.playAnimation(GraphicState.BASIC_ATTACK);
            defenderGraphic.playAnimation(GraphicState.TAKE_DAMAGE);

            logMessage = attacker.getName() + " used Basic Attack for " + damage + " damage!";
        } else {
            // ── Skill ─────────────────────────────────────────────────────────
            Skill skill = attacker.getSkill(actionIndex);

            if (skill.isCooldown()) {
                combatLog.setText(skill.getName() + " is on cooldown! (" + skill.getCurrentCooldown() + " turns left)");
                return;
            }
            if (attacker.getCurrentMana() < skill.getManaCost()) {
                combatLog.setText("Not enough mana to use " + skill.getName() + "!");
                return;
            }

            int hpBefore = defender.getCurrentHP();
            attacker.useSkill(actionIndex, defender);
            int damage = hpBefore - defender.getCurrentHP();

            GraphicState skillAnim = switch (actionIndex) {
                case 1 -> GraphicState.SKILL_1;
                case 2 -> GraphicState.SKILL_2;
                case 3 -> GraphicState.SKILL_3;
                default -> GraphicState.BASIC_ATTACK;
            };
            attackerGraphic.playAnimation(skillAnim);
            defenderGraphic.playAnimation(GraphicState.TAKE_DAMAGE);

            logMessage = attacker.getName() + " used " + skill.getName() + " for " + damage + " damage!";
        }

        stopTurnTimer();
        attacker.reduceCooldowns();
        attacker.takeMana(20);
        combatLog.setText(logMessage);
        refreshBars();

        if (!defender.isAlive()) {
            handleKnockout(attacker == player1);
            return;
        }

        isPlayer1Turn = !isPlayer1Turn;
        refreshTurnLabel();

        if (!isPlayer1Turn) {
            scheduleCpuTurn();
        } else {
            refreshSkillButtons(player1);
            startTurnTimer();
        }
    }

    // ── Knockout / fight end ──────────────────────────────────────────────────

    /**
     * @param playerWon true  = player knocked out the enemy
     *                  false = enemy knocked out the player
     */
    private void handleKnockout(boolean playerWon) {
        battleOver = true;
        stopTurnTimer();
        setActionButtonsEnabled(false);

        GameBattle battle = screen.getBattle();

        if (playerWon) {
            boolean allCleared = battle.advanceArcadeEnemy();
            int defeated = battle.getArcadeEnemiesDefeated();
            combatLog.setText(player2.getName() + " defeated! Enemies cleared: " + defeated);

            new Timer(2000, e -> {
                screen.changeScreen(allCleared
                        ? ScreenState.RESULT_ARCADE
                        : ScreenState.BATTLE_ARCADE);
                ((Timer) e.getSource()).stop();
            }).start();
        } else {
            combatLog.setText("Defeated by " + player2.getName() + "! Game Over.");
            new Timer(2000, e -> {
                screen.changeScreen(ScreenState.RESULT_ARCADE);
                ((Timer) e.getSource()).stop();
            }).start();
        }
    }

    // ── Turn timer ────────────────────────────────────────────────────────────

    private void startTurnTimer() {
        stopTurnTimer();
        turnTimeLeft = TURN_TIME;
        countdownLabel.setVisible(true);
        countdownLabel.setText(turnTimeLeft + "s");

        turnTimer = new Timer(1000, e -> {
            turnTimeLeft--;
            countdownLabel.setText(turnTimeLeft + "s");
            if (turnTimeLeft <= 0) {
                stopTurnTimer();
                combatLog.setText("Time's up! Auto basic attack.");
                handleAction(0);
            }
        });
        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null && turnTimer.isRunning()) turnTimer.stop();
        countdownLabel.setVisible(false);
    }

    // ── CPU ───────────────────────────────────────────────────────────────────

    private void scheduleCpuTurn() {
        setActionButtonsEnabled(false);
        new Timer(1000, e -> {
            cpuTakeTurn();
            ((Timer) e.getSource()).stop();
        }).start();
    }

    private void cpuTakeTurn() {
        if (battleOver) return;
        for (int i : new int[]{ 3, 2, 1 }) {
            Skill skill = player2.getSkill(i);
            if (!skill.isCooldown() && player2.getCurrentMana() >= skill.getManaCost()) {
                handleAction(i);
                return;
            }
        }
        handleAction(0);
    }

    // ── HUD refresh ───────────────────────────────────────────────────────────

    private void refreshHUD() {
        GameBattle battle    = screen.getBattle();
        int fightNumber      = battle.getArcadeEnemiesDefeated() + 1;
        int total            = battle.getTotalEnemies();

        p1NameLabel.setText(player1.getName());
        p2NameLabel.setText(player2.getName());
        roundLabel.setText("Fight " + fightNumber);
        arcadeProgressLabel.setText("Enemy " + fightNumber + " / " + total);

        refreshBars();
        refreshTurnLabel();
        refreshSkillButtons(player1);
    }

    private void refreshBars() {
        p1HpLabel  .setText("HP: " + player1.getCurrentHP()   + "/" + player1.getBaseHP());
        p1HpBar    .setMaximum(player1.getBaseHP());
        p1HpBar    .setValue(player1.getCurrentHP());
        p1ManaLabel.setText("MP: " + player1.getCurrentMana() + "/" + player1.getBaseMana());
        p1ManaBar  .setMaximum(player1.getBaseMana());
        p1ManaBar  .setValue(player1.getCurrentMana());

        p2HpLabel  .setText("HP: " + player2.getCurrentHP()   + "/" + player2.getBaseHP());
        p2HpBar    .setMaximum(player2.getBaseHP());
        p2HpBar    .setValue(player2.getCurrentHP());
        p2ManaLabel.setText("MP: " + player2.getCurrentMana() + "/" + player2.getBaseMana());
        p2ManaBar  .setMaximum(player2.getBaseMana());
        p2ManaBar  .setValue(player2.getCurrentMana());
    }

    private void refreshTurnLabel() {
        Entity active = isPlayer1Turn ? player1 : player2;
        turnLabel.setText(active.getName() + "'s turn" + (isPlayer1Turn ? "" : " (CPU)"));
    }

    private void refreshSkillButtons(Entity active) {
        setActionButtonsEnabled(true);
        for (int i = 1; i <= 3; i++) {
            Skill   skill  = active.getSkill(i);
            JButton btn    = switch (i) { case 1 -> skill1Button; case 2 -> skill2Button; default -> skill3Button; };
            boolean usable = !skill.isCooldown() && active.getCurrentMana() >= skill.getManaCost();
            btn.setText(skill.isCooldown()
                    ? skill.getName() + " (CD:" + skill.getCurrentCooldown() + ")"
                    : skill.getName() + " (" + skill.getManaCost() + "MP)");
            btn.setEnabled(usable);
        }
    }

    private void setActionButtonsEnabled(boolean enabled) {
        basicAttackButton.setEnabled(enabled);
        skill1Button     .setEnabled(enabled);
        skill2Button     .setEnabled(enabled);
        skill3Button     .setEnabled(enabled);
    }

    // ── ScreenBase stubs ──────────────────────────────────────────────────────

    @Override protected void hideButtons() { }
    @Override protected void showButtons()  { }
}
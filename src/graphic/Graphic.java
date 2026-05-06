package graphic;

import entity.AnimationState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Pure sprite loader and renderer — NOT a JPanel.
 *
 * Responsibilities:
 *   - Load sprite-sheet animations keyed by AnimationState
 *   - Track current frame and advance it via update()
 *   - Draw the current frame via draw()
 *
 * The owning screen calls update() and draw() inside its own paintComponent.
 * No Swing component overhead, no internal Timer.
 *
 * Sprite-sheet format expected:
 *   Horizontal strip — all frames left-to-right in a single row.
 *   e.g. a 4-frame 64×64 sheet is 256×64 px total.
 *
 * Usage:
 *   Graphic g = new Graphic();
 *   g.loadAnimation(AnimationState.IDLE, "/sprites/hero/idle.png", 4, 64, 64);
 *   g.loadAnimation(AnimationState.BASIC_ATTACK, "/sprites/hero/attack.png", 5, 64, 64);
 *
 *   // In paintComponent:
 *   g.update();
 *   g.draw(graphics, x, y, width, height);
 *
 *   // On player action:
 *   g.playOnce(AnimationState.BASIC_ATTACK);  // auto-returns to IDLE when done
 *   g.setState(AnimationState.IDLE);           // switch looping state
 */
public class Graphic {

    // ── Config ─────────────────────────────────────────────────────────────
    /** Ticks between frame advances. Lower = faster animation. */
    private int animationSpeed = 8;

    // ── State ──────────────────────────────────────────────────────────────
    private final Map<AnimationState, BufferedImage[]> animations
            = new EnumMap<>(AnimationState.class);

    private AnimationState currentState = AnimationState.IDLE;
    private int aniTick  = 0;
    private int aniIndex = 0;
    private boolean oneShot = false; // true → play once, then revert to IDLE

    // ── Loading ────────────────────────────────────────────────────────────

    /**
     * Load a sprite-sheet for the given state.
     *
     * @param state       which AnimationState this sheet covers
     * @param path        classpath resource path, e.g. "/sprites/hero/idle.png"
     * @param frameCount  number of frames in the sheet
     * @param frameWidth  width of each frame in pixels
     * @param frameHeight height of each frame in pixels
     */
    public void loadAnimation(AnimationState state,
                              String path,
                              int frameCount,
                              int frameWidth,
                              int frameHeight) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("Sprite sheet not found: " + path);
            }
            BufferedImage sheet = ImageIO.read(is);
            BufferedImage[] frames = new BufferedImage[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }
            animations.put(state, frames);
        } catch (Exception e) {
            System.err.println("[Graphic] Failed to load animation for " + state + ": " + e.getMessage());
        }
    }

    /**
     * Convenience: load all states for a character from a standard folder layout.
     *
     * Expected files under baseFolder:
     *   idle.png, basic_attack.png, take_damage.png,
     *   skill1.png, skill2.png, skill3.png, death.png
     *
     * Each file must be a horizontal strip with the given frameWidth × frameHeight frames.
     *
     * @param baseFolder  e.g. "/sprites/hero"  (no trailing slash)
     * @param frameWidth  width of each frame
     * @param frameHeight height of each frame
     * @param frameCounts array of 7 ints: [idle, basicAtk, takeDmg, sk1, sk2, sk3, death]
     */
    public void loadAllAnimations(String baseFolder,
                                  int frameWidth,
                                  int frameHeight,
                                  int[] frameCounts) {
        String[][] mapping = {
                { "idle.png",         String.valueOf(frameCounts[0]) },
                { "basic_attack.png", String.valueOf(frameCounts[1]) },
                { "take_damage.png",  String.valueOf(frameCounts[2]) },
                { "skill1.png",       String.valueOf(frameCounts[3]) },
                { "skill2.png",       String.valueOf(frameCounts[4]) },
                { "skill3.png",       String.valueOf(frameCounts[5]) },
                { "death.png",        String.valueOf(frameCounts[6]) },
        };
        AnimationState[] states = AnimationState.values();
        for (int i = 0; i < mapping.length; i++) {
            loadAnimation(
                    states[i],
                    baseFolder + "/" + mapping[i][0],
                    Integer.parseInt(mapping[i][1]),
                    frameWidth,
                    frameHeight
            );
        }
    }

    // ── State control ──────────────────────────────────────────────────────

    /**
     * Switch to a looping state (e.g. IDLE).
     * Restarts from frame 0 if the state actually changes.
     */
    public void setState(AnimationState state) {
        if (currentState == state) return;
        currentState = state;
        aniTick  = 0;
        aniIndex = 0;
        oneShot  = false;
    }

    /**
     * Play a one-shot animation (attack, damage, death).
     * Automatically returns to IDLE when the last frame is shown.
     * Ignored if the same state is already playing one-shot.
     */
    public void playOnce(AnimationState state) {
        if (oneShot && currentState == state) return;
        currentState = state;
        aniTick  = 0;
        aniIndex = 0;
        oneShot  = true;
    }

    // ── Per-frame logic ────────────────────────────────────────────────────

    /**
     * Advance the animation by one game tick.
     * Call this once per repaint cycle from the owning screen's paintComponent.
     */
    public void update() {
        BufferedImage[] frames = animations.get(currentState);
        if (frames == null || frames.length == 0) return;

        aniTick++;
        if (aniTick < animationSpeed) return;

        aniTick = 0;
        aniIndex++;

        if (aniIndex >= frames.length) {
            if (oneShot) {
                oneShot = false;
                setState(AnimationState.IDLE); // revert to idle loop
            } else {
                aniIndex = 0; // loop
            }
        }
    }

    /**
     * Draw the current frame at the given screen coordinates.
     * Call after update() inside paintComponent.
     *
     * @param g      Graphics context from paintComponent
     * @param x      left edge in screen pixels
     * @param y      top edge in screen pixels
     * @param width  rendered width  (can differ from frame source size)
     * @param height rendered height
     */
    public void draw(Graphics g, int x, int y, int width, int height) {
        BufferedImage[] frames = animations.get(currentState);
        if (frames == null || aniIndex >= frames.length) return;
        g.drawImage(frames[aniIndex], x, y, width, height, null);
    }

    // ── Accessors ──────────────────────────────────────────────────────────

    public AnimationState getCurrentState() {
        return currentState;
    }

    public boolean isOneShot() {
        return oneShot;
    }

    public boolean hasAnimation(AnimationState state) {
        return animations.containsKey(state);
    }

    /** @param speed ticks per frame — lower is faster (default 8) */
    public void setAnimationSpeed(int speed) {
        this.animationSpeed = Math.max(1, speed);
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }
}

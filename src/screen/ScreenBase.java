package screen;

import graphic.Graphic;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all game screens.
 *
 * Responsibilities:
 *   - Shared factory helpers for labels, buttons, and progress bars.
 *   - A central javax.swing.Timer that fires at ~60 fps and calls
 *     onAnimationTick() on the EDT — subclasses override this to call
 *     graphic.update() for every Graphic they own, then repaint().
 *   - The timer is started/stopped automatically when the panel is
 *     shown or hidden (via addNotify / removeNotify), so only the
 *     currently visible screen is ticking.
 *
 * Thread safety:
 *   Everything in this class and its subclasses runs on the EDT
 *   (button listeners, Timer callbacks, paintComponent).  The game-loop
 *   thread in AlteredEgo only calls repaint(), which is thread-safe.
 */
public abstract class ScreenBase extends JPanel {

    protected final Screen screen;

    // UI component registries (for bulk show/hide)
    protected final List<JLabel>       labels       = new ArrayList<>();
    protected final List<JButton>      buttons      = new ArrayList<>();
    protected final List<JProgressBar> progressBars = new ArrayList<>();

    // Central animation timer — ~60 fps, runs only while this panel is showing
    private static final int ANIMATION_TICK_MS = 16;
    private final Timer animationTimer;

    // ── Construction ─────────────────────────────────────────────────────────

    protected ScreenBase(Screen screen) {
        this.screen = screen;
        setLayout(null);

        animationTimer = new Timer(ANIMATION_TICK_MS, e -> {
            onAnimationTick();
            repaint();
        });

        initializeUI();
    }

    // ── Subclass hooks ────────────────────────────────────────────────────────

    /** Build all buttons, labels, progress bars, and Graphic objects here. */
    protected abstract void initializeUI();

    /**
     * Called ~60 times per second on the EDT while this screen is visible.
     * Override to call graphic.update() for every Graphic this screen owns.
     *
     * Default implementation does nothing (screens without animation are fine).
     */
    protected void onAnimationTick() { }

    // ── Timer lifecycle ───────────────────────────────────────────────────────

    /** Start ticking when the panel is added to a visible window. */
    @Override
    public void addNotify() {
        super.addNotify();
        animationTimer.start();
    }

    /** Stop ticking when the panel is removed (screen swap). */
    @Override
    public void removeNotify() {
        animationTimer.stop();
        super.removeNotify();
    }

    // ── UI factories ──────────────────────────────────────────────────────────

    protected JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = Util.createLabel(text, x, y, width, height);
        add(label);
        labels.add(label);
        return label;
    }

    protected JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = Util.createButton(text, x, y, width, height);
        add(button);
        buttons.add(button);
        return button;
    }

    protected JProgressBar createBar(int x, int y, int width, int height, Color color) {
        JProgressBar bar = Util.createProgressBar(500, x, y, width, height, color);
        add(bar);
        progressBars.add(bar);
        return bar;
    }

    // ── Bulk visibility ───────────────────────────────────────────────────────

    protected void showLabels()  { labels.forEach(l -> l.setVisible(true));  }
    protected void hideLabels()  { labels.forEach(l -> l.setVisible(false)); }
    protected void showButtons() { buttons.forEach(b -> b.setVisible(true)); }
    protected void hideButtons() { buttons.forEach(b -> b.setVisible(false));}
}

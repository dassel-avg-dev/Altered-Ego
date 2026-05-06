package core;

import javax.swing.SwingUtilities;

/**
 * Application entry-point and game-loop owner.
 *
 * Thread model:
 *   - The game loop runs on a dedicated daemon thread ("game-loop").
 *     Its only job is to schedule repaints at a capped FPS.
 *   - All Swing construction and window operations happen on the EDT
 *     via SwingUtilities.invokeLater / invokeAndWait.
 *   - All game-state mutations (button clicks, timers) already run on
 *     the EDT through Swing's event dispatch, so no extra locking is
 *     needed there.
 *
 * The loop uses a fixed-timestep approach:
 *   - Tracks elapsed nanoseconds to fire repaints at exactly FPS_LIMIT.
 *   - Sleeps 1 ms when idle to avoid burning 100 % CPU on a spin-wait.
 */
public class AlteredEgo implements Runnable {

    private static final int    FPS_LIMIT             = 120;
    private static final long   NS_PER_FRAME          = 1_000_000_000L / FPS_LIMIT;
    private static final long   SLEEP_MS_WHEN_IDLE    = 1L;

    private GameWindow gameWindow;

    private Thread          gameThread;
    private volatile boolean running = false;

    // ── Bootstrap ───────────────────────────────────────────────────────────

    public AlteredEgo() {
        // Build the window on the EDT, then start the loop.
        SwingUtilities.invokeLater(() -> {
            gameWindow = new GameWindow();
            startLoop();
        });
    }

    // ── Loop control ────────────────────────────────────────────────────────

    private synchronized void startLoop() {
        if (running) return;
        running    = true;
        gameThread = new Thread(this, "game-loop");
        gameThread.setDaemon(true);   // won't prevent JVM exit
        gameThread.start();
    }

    public synchronized void stopLoop() {
        running = false;
    }

    // ── Runnable ────────────────────────────────────────────────────────────

    @Override
    public void run() {
        long lastFrameNs = System.nanoTime();

        while (running) {
            long nowNs = System.nanoTime();
            long delta = nowNs - lastFrameNs;

            if (delta >= NS_PER_FRAME) {
                lastFrameNs = nowNs - (delta % NS_PER_FRAME); // stay in phase

                // repaint must be posted to the EDT
                SwingUtilities.invokeLater(() -> {
                    if (gameWindow != null) gameWindow.repaint();
                });
            } else {
                // sleep to avoid a hot spin-loop
                try {
                    Thread.sleep(SLEEP_MS_WHEN_IDLE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    // ── Main ────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Ensure the very first Swing object is created on the EDT.
        SwingUtilities.invokeLater(AlteredEgo::new);
    }
}

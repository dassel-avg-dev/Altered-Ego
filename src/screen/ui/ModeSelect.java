package screen.ui;

import audio.Audio;
import graphic.GraphicState;
import graphic.Graphic;
import screen.Screen;
import screen.ScreenBase;
import screen.ModeState;
import screen.ScreenState;

import javax.swing.*;
import java.awt.*;

public class ModeSelect extends ScreenBase {

    // ── Sprite sheet config ──────────────────────────────────────────────────
    private static final int FRAME_WIDTH  = 1024;
    private static final int FRAME_HEIGHT = 1024;
    private static final int FRAME_COUNT  = 6;  // number of frames in the single row

    /** On-screen bounds: x, y, width, height */
    private static final int[] BG_BOUNDS = { 0, 0, 706, 683 };

    // ── Sprite ───────────────────────────────────────────────────────────────
    private Graphic bgGraphic;

    public ModeSelect(Screen screen) {
        super(screen);
    }

    @Override
    protected void initializeUI() {
        bgGraphic = new Graphic();      // ← initialize here, before loadSprites
        loadSprites(bgGraphic);
        bgGraphic.loopAnimation(GraphicState.IDLE);
        JButton pvpButton = createButton("", 150, 190, 405, 72);
        pvpButton.setOpaque(false);
        pvpButton.setContentAreaFilled(false);
        pvpButton.setBorderPainted(false);
        pvpButton.setFocusable(false);
        pvpButton.addActionListener(e -> {
            Audio.stopBGM();
            screen.getBattle().setGameMode(ModeState.VS_COMPUTER);
            screen.changeScreen(ScreenState.SELECT_CHARACTER);
        });

        JButton pveButton = createButton("", 150, 280, 405, 72);
        pveButton.setOpaque(false);
        pveButton.setContentAreaFilled(false);
        pveButton.setBorderPainted(false);
        pveButton.setFocusable(false);
        pveButton.addActionListener(e -> {
            Audio.stopBGM();
            screen.getBattle().setGameMode(ModeState.VS_PLAYER);
            screen.changeScreen(ScreenState.SELECT_CHARACTER);
        });

        JButton arcadeButton = createButton("", 150, 366, 405, 72);
        arcadeButton.setOpaque(false);
        arcadeButton.setContentAreaFilled(false);
        arcadeButton.setBorderPainted(false);
        arcadeButton.setFocusable(false);
        arcadeButton.addActionListener(e -> {
            Audio.stopBGM();
            screen.getBattle().resetSeries();
            screen.getCharacterSelect().resetSelection();
            screen.getBattle().setGameMode(ModeState.ARCADE);
            screen.changeScreen(ScreenState.SELECT_CHARACTER);
        });

        JButton backButton = createButton("", 232, 455, 242, 72);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusable(false);
        backButton.addActionListener(e -> {
            Audio.startBGM("/soundtracks/Beauty_Flow.wav");
            screen.changeScreen(ScreenState.TITLE);
        });
    }

    @Override
    protected void onAnimationTick() {
        bgGraphic.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        bgGraphic.draw(g,
                BG_BOUNDS[0], BG_BOUNDS[1],
                BG_BOUNDS[2], BG_BOUNDS[3]);
    }

    // ── Sprite loading ────────────────────────────────────────────────────────

    private void loadSprites(Graphic graphic) {
        graphic.loadRow("/mode_screen.png", FRAME_WIDTH, FRAME_HEIGHT, FRAME_COUNT);
    }
}
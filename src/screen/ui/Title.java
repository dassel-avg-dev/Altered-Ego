package screen.ui;

import screen.Screen;
import screen.ScreenBase;
import util.GameScreen;
import graphic.Graphic;
import entity.AnimationState;

import javax.swing.*;
import java.awt.*;

public class Title extends ScreenBase {
    private Graphic graphic;
    public Title(Screen screen) {
        super(screen);
    }

    @Override
    protected void initializeUI() {
        JButton playButton = createButton("Play", 105, 452, 200, 50);
        playButton.addActionListener(e -> screen.changeScreen(GameScreen.SELECT_MODE));

        JButton exitButton = createButton("Exit", 410, 452, 200, 50);
        exitButton.addActionListener(e -> System.exit(0));

        graphic = new Graphic();
        graphic.loadAnimation(AnimationState.IDLE,         "/cosmic_dassel_complete_spritesheet.png",         4, 64, 64);
    }
    @Override
    protected void onAnimationTick() {
        graphic.update();   // advances the frame
        // repaint() is called automatically by the base class timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        graphic.draw(g, 100, 100, 100, 100);  // render at desired position/size
    }
}
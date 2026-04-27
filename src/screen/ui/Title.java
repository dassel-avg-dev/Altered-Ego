package screen.ui;

import screen.Screen;
import screen.ScreenBase;
import util.GameScreen;

import javax.swing.*;

public class Title extends ScreenBase {

    public Title(Screen screen) {
        super(screen);
    }

    @Override
    protected void initializeUI() {
        JButton playButton = createButton("Play", 105, 452, 200, 50);
        playButton.addActionListener(e -> screen.changeScreen(GameScreen.SELECT_MODE));

        JButton exitButton = createButton("Exit", 410, 452, 200, 50);
        exitButton.addActionListener(e -> System.exit(0));
    }
}
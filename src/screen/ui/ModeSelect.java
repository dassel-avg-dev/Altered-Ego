package screen.ui;

import screen.Screen;
import screen.ScreenBase;
import util.GameMode;
import util.GameScreen;

import javax.swing.*;

public class ModeSelect extends ScreenBase {

    public ModeSelect(Screen screen) {
        super(screen);
    }

    @Override
    protected void initializeUI() {
        JButton pvpButton = createButton("Player vs Player", 255, 180, 200, 50);
        pvpButton.addActionListener(e -> {
            screen.getBattle().setGameMode(GameMode.VS_PLAYER);
            screen.changeScreen(GameScreen.SELECT_CHARACTER);
        });

        JButton pveButton = createButton("Player vs Computer", 255, 280, 200, 50);
        pveButton.addActionListener(e -> {
            screen.getBattle().setGameMode(GameMode.VS_COMPUTER);
            screen.changeScreen(GameScreen.SELECT_CHARACTER);
        });

        JButton arcadeButton = createButton("Arcade", 255, 380, 200, 50);
        arcadeButton.addActionListener(e -> {
            screen.getBattle().resetSeries();
            screen.getCharacterSelect().resetSelection();
            screen.getBattle().setGameMode(GameMode.ARCADE);
            screen.changeScreen(GameScreen.SELECT_CHARACTER);
        });

        JButton backButton = createButton("Back", 255, 480, 200, 50);
        backButton.addActionListener(e -> screen.changeScreen(GameScreen.TITLE));
    }
}
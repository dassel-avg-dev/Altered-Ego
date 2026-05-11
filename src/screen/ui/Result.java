package screen.ui;

import screen.Screen;
import screen.ScreenBase;
import util.GameBattle;
import entity.EntityState;
import screen.ModeState;
import screen.ScreenState;
import javax.swing.*;
import java.awt.*;

public class Result extends ScreenBase {
    private JLabel winnerLabel;
    private JLabel scoreLabel;

    public Result(Screen screen) {
        super(screen);
    }

    @Override
    protected void initializeUI() {
        winnerLabel = createLabel("", 155, 250, 400, 50);
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        winnerLabel.setForeground(Color.BLACK);

        scoreLabel = createLabel("", 155, 310, 400, 50);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreLabel.setForeground(Color.DARK_GRAY);

        JButton rematchButton = createButton("Rematch", 105, 452, 200, 50);
        rematchButton.addActionListener(e -> {
            GameBattle battle = screen.getBattle();
            ModeState mode = battle.getGameMode();
            EntityState p1 = battle.getPlayerOne();
            EntityState p2 = battle.getPlayerTwo();
            battle.resetSeries();
            battle.setGameMode(mode);
            battle.setPlayerOne(p1);
            battle.setPlayerTwo(p2);
            screen.changeScreen(ScreenState.BATTLE);
        });

        JButton toTitleButton = createButton("Title", 410, 452, 200, 50);
        toTitleButton.addActionListener(e -> screen.changeScreen(ScreenState.TITLE));
    }

    public void showResult() {
        GameBattle state = screen.getBattle();
        String winner = state.getSeriesWinner();
        winnerLabel.setText(winner != null ? winner + " wins!" : "");
        scoreLabel.setText(state.getPlayerOneWins() + " - " + state.getPlayerTwoWins());
    }
}

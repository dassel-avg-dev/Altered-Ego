package screen.ui;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import screen.*;
import util.*;

public class CharacterSelect extends ScreenBase {
    private Map<GameCharacter, JButton> characterButtons;
    private int selectionRound = 1;

    public CharacterSelect(Screen screen) {
        super(screen);
    }

    @Override
    protected void initializeUI() {
        characterButtons = new LinkedHashMap<>();

        int col1 = 105, col2 = 410;
        int yStart = 85;
        int gap = 100;

        GameCharacter[] all = GameCharacter.values();
        for (int i = 0; i < all.length; i++) {
            GameCharacter gc = all[i];
            int x = (i % 2 == 0) ? col1 : col2;
            int y = yStart + ((i / 2) * gap);
            JButton btn = createButton(gc.getName(), x, y, 200, 50);
            btn.addActionListener(e -> onSelection(gc));
            characterButtons.put(gc, btn);
        }

        JButton backButton = createButton("Back", 255, 585, 200, 50);
        backButton.addActionListener(e -> screen.changeScreen(GameScreen.SELECT_MODE));
    }

    private void onSelection(GameCharacter character) {
        GameBattle battle = screen.getBattle();
        GameMode mode = battle.getGameMode();
        boolean isPvP = mode == GameMode.VS_PLAYER;
        boolean isArcade = mode == GameMode.ARCADE;

        if (selectionRound == 1) {
            battle.setPlayerOne(character);
            characterButtons.get(character).setEnabled(false);

            if (isPvP) {
                selectionRound = 2;
            } else if (isArcade) {
                List<GameCharacter> enemies = new ArrayList<>();
                for (GameCharacter gc : GameCharacter.values()) {
                    if (gc != character) enemies.add(gc);
                }
                java.util.Collections.shuffle(enemies);
                battle.setupArcade(enemies);
                screen.changeScreen(GameScreen.BATTLE_ARCADE);
            } else {
                List<GameCharacter> available = new ArrayList<>();
                for (Map.Entry<GameCharacter, JButton> entry : characterButtons.entrySet()) {
                    if (entry.getValue().isEnabled()) available.add(entry.getKey());
                }
                GameCharacter cpuPick = available.get(Util.rng(0, available.size() - 1));
                battle.setPlayerTwo(cpuPick);
                screen.changeScreen(GameScreen.BATTLE);
            }
        } else {
            // PvP round 2 selection
            battle.setPlayerTwo(character);
            screen.changeScreen(GameScreen.BATTLE);
        }
    }

    public void resetSelection() {
        selectionRound = 1;
        for (JButton btn : characterButtons.values()) {
            btn.setEnabled(true);
        }
    }

    @Override
    protected void hideButtons() { }

    @Override
    protected void showButtons() { }
}
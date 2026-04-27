package screen;

import core.*;
import screen.ui.*;
import util.*;

public class Screen {
    private final GameWindow gameWindow;
    private ScreenBase currentScreen;

    private final Title title;
    private final ModeSelect modeSelect;
    private final CharacterSelect characterSelect;
    private final Battle battle;
    private final BattleArcade battleArcade; //for arcade
    private final Result result;
    private final ArcadeResult arcadeResult;   //for arcade

    private final GameBattle gameBattle;

    public Screen(GameWindow gameWindow) {
        this.gameWindow = gameWindow;

        title           = new Title(this);
        modeSelect      = new ModeSelect(this);
        characterSelect = new CharacterSelect(this);
        battle          = new Battle(this);
        battleArcade    = new BattleArcade(this);   //for arcade
        result          = new Result(this);
        arcadeResult    = new ArcadeResult(this);   //for arcade

        gameBattle = new GameBattle();

        currentScreen = title;
        gameWindow.changeWindow(currentScreen);
    }

    public void changeScreen(GameScreen gameScreen) {
        currentScreen = switch(gameScreen) {
            case TITLE            -> title;
            case SELECT_MODE      -> modeSelect;
            case SELECT_CHARACTER -> characterSelect;
            case BATTLE           -> battle;
            case BATTLE_ARCADE    -> battleArcade;   //for arcade
            case RESULT           -> result;
            case ARCADE_RESULT    -> arcadeResult;   //for arcade
        };

        if(gameScreen == GameScreen.TITLE) {
            gameBattle.resetSeries();
        }

        if(gameScreen == GameScreen.BATTLE) {
            battle.startBattle();
        }

        if(gameScreen == GameScreen.BATTLE_ARCADE) {   //for arcade
            battleArcade.startBattle();
        }

        if(gameScreen == GameScreen.RESULT) {
            result.showResult();
        }

        if(gameScreen == GameScreen.ARCADE_RESULT) {   //for arcade
            arcadeResult.showResult();
        }

        gameWindow.changeWindow(currentScreen);
    }

    public GameBattle getBattle() {
        return gameBattle;
    }

    public CharacterSelect getCharacterSelect() {
        return characterSelect;
    }
}
package util;

import entity.Entity;
import entity.EntityPool;
import entity.EntityState;
import screen.ModeState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBattle {
    private final EntityPool entityPool = new EntityPool();
    private final int WINS_REQUIRED = 2;

    private ModeState gameMode;
    private EntityState playerOne;
    private EntityState playerTwo;

    private int playerOneWins = 0;
    private int playerTwoWins = 0;
    private int roundNumber = 1;

    private String roundWinner;
    private String seriesWinner;

    private List<EntityState> arcadeEnemy = new ArrayList<>();
    private int arcadeEnemyIndex = 0;
    private int arcadeEnemiesDefeated = 0;

    private boolean arcadeVictory = false;

    public void resetRound() {
        roundWinner = null;
        seriesWinner = null;

        if (playerOne != null) entityPool.getEntity(playerOne).resetCharacter();
        if (playerTwo != null) entityPool.getEntity(playerTwo).resetCharacter();
    }

    public void resetArcade(List<EntityState> enemies) {
        arcadeEnemy = new ArrayList<>(enemies);
        arcadeEnemyIndex = 0;
        arcadeEnemiesDefeated = 0;

        arcadeVictory = false;

        if (!arcadeEnemy.isEmpty()) playerTwo = arcadeEnemy.getFirst();

        if (playerOne != null) entityPool.getEntity(playerOne).resetCharacter();
        if (playerTwo != null) entityPool.getEntity(playerTwo).resetCharacter();
    }

    public void resetSeries() {
        gameMode = null;
        playerOne = null;
        playerTwo = null;

        playerOneWins = 0;
        playerTwoWins = 0;
        roundNumber = 1;

        roundWinner = null;
        seriesWinner = null;

        arcadeEnemy.clear();
        arcadeEnemyIndex = 0;
        arcadeEnemiesDefeated = 0;

        arcadeVictory = false;
    }

    public boolean recordWin(boolean player1Won) {
        if (player1Won) {
            playerOneWins++;
        } else {
            playerTwoWins++;
        }

        roundWinner = player1Won ? playerOne.getName() : playerTwo.getName();
        roundNumber++;

        if (playerOneWins >= WINS_REQUIRED || playerTwoWins >= WINS_REQUIRED) {
            seriesWinner = roundWinner;
            return true;
        }
        return false;
    }

    public boolean advanceArcadeEnemy() {
        arcadeEnemiesDefeated++;
        arcadeEnemyIndex++;
        Random random = new Random();

        if (arcadeEnemyIndex >= arcadeEnemy.size()) {
            arcadeVictory = true;
            playerTwo = null;
            return true;
        }

        playerTwo = arcadeEnemy.get(arcadeEnemyIndex);
        entityPool.getEntity(playerTwo).resetCharacter();

        Entity p1Entity = entityPool.getEntity(playerOne);

        int healAmount = 50;
        int manaAmount = 20;

        healAmount *= random.nextInt(1, 10);
        manaAmount *= random.nextInt(5, 10);

        p1Entity.healHP(healAmount);
        p1Entity.healMana(manaAmount);

        roundNumber++;
        return false;
    }

    public Entity getEntityOne() {
        return entityPool.getEntity(playerOne);
    }

    public Entity getEntityTwo() {
        return entityPool.getEntity(playerTwo);
    }

    public ModeState getGameMode() {
        return gameMode;
    }

    public void setGameMode(ModeState gameMode) {
        this.gameMode = gameMode;
    }

    public EntityState getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(EntityState player1) {
        this.playerOne = player1;
    }

    public EntityState getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(EntityState player2) {
        this.playerTwo = player2;
    }

    public int getPlayerOneWins() {
        return playerOneWins;
    }

    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getRoundWinner() {
        return roundWinner;
    }

    public String getSeriesWinner() {
        return seriesWinner;
    }

    public List<EntityState> getArcadeEnemy() {
        return arcadeEnemy;
    }

    public void setArcadeEnemy(List<EntityState> arcadeEnemy) {
        this.arcadeEnemy = arcadeEnemy;
    }

    public int getTotalEnemies() {
        return arcadeEnemy.size();
    }

    public int getArcadeEnemiesDefeated() {
        return arcadeEnemiesDefeated;
    }

    public boolean isArcadeVictory() {
        return arcadeVictory;
    }
}
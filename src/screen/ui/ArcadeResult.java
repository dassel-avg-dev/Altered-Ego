package screen.ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import screen.*;
import util.*;

public class ArcadeResult extends ScreenBase {

    private JLabel headlineLabel;
    private JLabel killsLabel;
    private JLabel subLabel;

    // Leaderboard UI
    private JLabel leaderboardTitleLabel;
    private JLabel[] rankLabels;
    private static final int MAX_RANKS = 10;

    // Name input for saving score
    private JTextField nameField;
    private JButton saveButton;
    private JLabel saveLabel;

    private JButton mainMenuButton;

    public ArcadeResult(Screen screen) {
        super(screen);
    }

    @Override
    protected void initializeUI() {
        // ── Result headline ───────────────────────────────────────────────────
        headlineLabel = createLabel("", 100, 20, 510, 55);
        headlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headlineLabel.setFont(new Font("Arial", Font.BOLD, 42));

        killsLabel = createLabel("", 100, 75, 510, 30);
        killsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        killsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        killsLabel.setForeground(Color.DARK_GRAY);

        subLabel = createLabel("", 100, 108, 510, 25);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subLabel.setForeground(Color.GRAY);

        // ── Name entry + save ─────────────────────────────────────────────────
        saveLabel = createLabel("Enter your name:", 155, 145, 200, 25);
        saveLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        saveLabel.setForeground(Color.DARK_GRAY);

        nameField = new JTextField();
        nameField.setBounds(155, 170, 200, 30);
        add(nameField);

        saveButton = createButton("Save Score", 365, 168, 130, 34);
        saveButton.addActionListener(e -> saveScore());

        // ── Leaderboard table ─────────────────────────────────────────────────
        leaderboardTitleLabel = createLabel("🏆  Leaderboard", 155, 215, 400, 28);
        leaderboardTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leaderboardTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        leaderboardTitleLabel.setForeground(new Color(0x5d4037));

        rankLabels = new JLabel[MAX_RANKS];
        for (int i = 0; i < MAX_RANKS; i++) {
            rankLabels[i] = createLabel("", 155, 248 + i * 27, 400, 24);
            rankLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            rankLabels[i].setFont(new Font("Monospaced", Font.PLAIN, 13));
            rankLabels[i].setForeground(Color.DARK_GRAY);
        }

        // ── Main menu button ──────────────────────────────────────────────────
        mainMenuButton = createButton("Main Menu", 255, 540, 200, 45);
        mainMenuButton.addActionListener(e -> {
            screen.getBattle().resetSeries();
            screen.changeScreen(ScreenState.TITLE);
        });
    }

    /**
     * Called by Screen.changeScreen(RESULT_ARCADE) to populate the result view.
     */

    public void showResult() {
        GameBattle battle = screen.getBattle();
        boolean victory   = battle.isArcadeVictory();
        int defeated      = battle.getArcadeEnemiesDefeated();
        int total         = battle.getTotalEnemies();

        if (victory) {
            headlineLabel.setForeground(new Color(0x2e7d32));
            headlineLabel.setText("Victory!");
            killsLabel.setText("All " + total + " enemies defeated!");
            subLabel.setText("You conquered the arcade — impressive.");
        } else {
            headlineLabel.setForeground(new Color(0xb71c1c));
            headlineLabel.setText("Game Over");
            killsLabel.setText("Enemies defeated: " + defeated + " / " + total);
            subLabel.setText("Better luck next time.");
        }

        // Reset save UI
        nameField.setText("");
        nameField.setEnabled(true);
        saveButton.setEnabled(true);
        saveLabel.setText("Enter your name:");

        refreshLeaderboard();
    }

    private void saveScore() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            saveLabel.setText("Please enter a name!");
            return;
        }
        // Sanitise: strip commas so CSV stays clean
        name = name.replace(",", "");

        int kills = screen.getBattle().getArcadeEnemiesDefeated();
        Leaderboard.save(name, kills);

        nameField.setEnabled(false);
        saveButton.setEnabled(false);
        saveLabel.setText("Score saved!");
        refreshLeaderboard();
    }

    private void refreshLeaderboard() {
        List<Leaderboard.Entry> entries = Leaderboard.load();

        // Clear all rows first
        for (JLabel label : rankLabels) {
            label.setText("");
        }

        String[] medals = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < entries.size() && i < MAX_RANKS; i++) {
            Leaderboard.Entry e = entries.get(i);
            String rank   = i < 3 ? medals[i] : " #" + (i + 1);
            String row    = String.format("%-4s %-20s %3d kills", rank, e.name, e.kills);
            rankLabels[i].setText(row);

            // Gold / silver / bronze tint for top 3
            if      (i == 0) rankLabels[i].setForeground(new Color(0xf9a825));
            else if (i == 1) rankLabels[i].setForeground(new Color(0x757575));
            else if (i == 2) rankLabels[i].setForeground(new Color(0x8d6e63));
            else             rankLabels[i].setForeground(Color.DARK_GRAY);
        }
    }

    @Override protected void hideButtons() { }
    @Override protected void showButtons()  { }
}
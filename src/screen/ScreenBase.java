package screen;

import util.Util;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ScreenBase extends JPanel {
    protected Screen screen;
    protected List<JLabel> labels = new ArrayList<>();
    protected List<JButton> buttons = new ArrayList<>();
    protected List<JProgressBar> progressBars = new ArrayList<>();

    public ScreenBase(Screen screen) {
        this.screen = screen;
        setLayout(null);
        initializeUI();
    }

    protected abstract void initializeUI();

    protected JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = Util.createLabel(text, x, y, width, height);
        add(label);
        labels.add(label);
        return label;
    }

    protected JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = Util.createButton(text, x, y, width, height);
        add(button);
        buttons.add(button);
        return button;
    }

    protected JProgressBar createBar(int x, int y, int width, int height, Color color) {
        JProgressBar bar = Util.createProgressBar(x, y, width, height, 500, color);
        add(bar);
        progressBars.add(bar);
        return bar;
    }

    protected void showLabels() {
        for (JLabel label : labels) label.setVisible(true);
    }

    protected void hideLabels() {
        for (JLabel label : labels) label.setVisible(false);
    }

    protected void showButtons() {
        for (JButton button : buttons) button.setVisible(true);
    }

    protected void hideButtons() {
        for (JButton button : buttons) button.setVisible(false);
    }
}
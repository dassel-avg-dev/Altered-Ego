package util;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Util {
    private static final Random random = new Random();

    public static int rng(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum value cannot be greater than maximum value.");
        }
        return random.nextInt(min, max + 1);
    }

    public static JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.BLACK);
        return label;
    }

    // ✅ FIXED BUTTON
    public static JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);

        button.setBounds(x, y, width, height);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);

        // ✅ Thin border (1px instead of 2px)
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setBorderPainted(true);

        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.setBackground(Color.WHITE);

        return button;
    }

    public static JProgressBar createProgressBar(int x, int y, int width, int height, int max, Color color) {
        JProgressBar bar = new JProgressBar(0, max);
        bar.setBounds(x, y, width, height);
        bar.setValue(max);
        bar.setForeground(color);
        bar.setBorderPainted(false);
        return bar;
    }
}
package core;

import screen.Screen;

import javax.swing.*;

/**
 * The main application window.
 *
 * Must be created and accessed on the EDT only.
 * Owns the Screen (which owns all screen panels).
 */
public class GameWindow extends JFrame {

    private final Screen screen;

    public GameWindow() {
        setTitle("Altered Ego");
        setSize(720, 720);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        screen = new Screen(this);

        setVisible(true);
    }

    /**
     * Swap the content pane to a new panel.
     * Must be called on the EDT.
     */
    public void changeWindow(JPanel newPanel) {
        getContentPane().removeAll();
        getContentPane().add(newPanel);
        revalidate();
        repaint();
    }

    public Screen getScreen() {
        return screen;
    }
}

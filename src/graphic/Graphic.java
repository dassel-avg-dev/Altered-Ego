package graphic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class Graphic {
    private final Map<GraphicState, BufferedImage[]> animations = new EnumMap<>(GraphicState.class);

    private GraphicState currentState = GraphicState.IDLE;

    private int animationSpeed = 8;
    private int aniTick = 0;
    private int aniIndex = 0;

    private boolean oneShot = false;
    private boolean flipped = false;

    public void loadRow(String path, int frameWidth, int frameHeight, int frameCount) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("[Graphic] File not found: " + path);
            }
            BufferedImage sheet = ImageIO.read(is);
            BufferedImage[] frames = new BufferedImage[frameCount];
            for (int col = 0; col < frameCount; col++) {
                frames[col] = sheet.getSubimage(col * frameWidth, 0, frameWidth, frameHeight);
            }
            animations.put(GraphicState.IDLE, frames);
        } catch (IOException e) {
            System.err.println("[Graphic] Graphic error: " + e.getMessage());
        }
    }

    public void loadAll(String path, int frameWidth, int frameHeight, int[] frameCount) {
        GraphicState[] states = GraphicState.values();

        if (frameCount.length != states.length) {
            System.err.println("[Graphic] Frame count mismatch: " + path);
            return;
        }

        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new IOException("[Graphic] File not found: " + path);
            }
            BufferedImage sheet = ImageIO.read(is);
            for (int row = 0; row < states.length; row++) {
                int count = frameCount[row];
                BufferedImage[] frames = new BufferedImage[count];
                for (int col = 0; col < count; col++) {
                    frames[col] = sheet.getSubimage(col * frameWidth, row * frameHeight, frameWidth, frameHeight);
                }
                animations.put(states[row], frames);
            }
        } catch (IOException e) {
            System.err.println("[Graphic] Graphic error: " + e.getMessage());
        }
    }

    public void playAnimation(GraphicState state) {
        if (oneShot && currentState == state) {
            return;
        }

        currentState = state;
        aniTick = 0;
        aniIndex = 0;
        oneShot = true;
    }

    public void loopAnimation(GraphicState state) {
        if (currentState == state) {
            return;
        }

        currentState = state;
        aniTick = 0;
        aniIndex = 0;
        oneShot = false;
    }

    public void update() {
        BufferedImage[] frames = animations.get(currentState);

        if (frames == null || frames.length == 0) {
            return;
        }

        aniTick++;

        if (aniTick < animationSpeed) {
            return;
        }

        aniTick = 0;
        aniIndex++;

        if (aniIndex >= frames.length) {
            if (oneShot) {
                oneShot = false;
                loopAnimation(GraphicState.IDLE);
            } else {
                aniIndex = 0;
            }
        }
    }

    public void draw(Graphics g, int x, int y, int width, int height) {
        BufferedImage[] frames = animations.get(currentState);
        if (frames == null || aniIndex >= frames.length) {
            return;
        }

        if (flipped) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(x + width, y);
            g2d.scale(-1, 1);
            g2d.drawImage(frames[aniIndex], 0, 0, width, height, null);
            g2d.dispose();
        } else {
            g.drawImage(frames[aniIndex], x, y, width, height, null);
        }
    }

    public void setAnimationSpeed(int speed) {
        this.animationSpeed = Math.max(1, speed);
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }
}
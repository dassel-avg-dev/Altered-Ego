package audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class Audio {
    private static Clip clip;
    public static void startBGM(String path) {
        stopBGM();
        try (InputStream is = Audio.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("[Audio] File not found: " + path);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(new java.io.BufferedInputStream(is));
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[Audio] Audio error: " + e.getMessage());
        }
    }

    public static void stopBGM() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
        clip = null;
    }
}
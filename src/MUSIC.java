import javax.sound.sampled.*;
import java.io.*;

public class MUSIC {
    private Clip clip;
    private Long currentFrame = 0L;
    private AudioInputStream audioStream;
    private String currentFilePath;

    public void gameMusic(String filePath) {
        try {
            currentFilePath = filePath;

            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            if (audioSrc == null) {
                System.out.println("Audio file not found: " + filePath);
                return;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void playEffectSound(String filePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(filePath);
            if (audioSrc == null) {
                System.out.println("Effect sound not found: " + filePath);
                return;
            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream effectStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip effectClip = AudioSystem.getClip();
            effectClip.open(effectStream);
            effectClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseBackgroundMusic() {
        if (clip != null && clip.isActive()) {
            currentFrame = clip.getMicrosecondPosition();
            clip.stop();

        }
    }

    public void resumeBackgroundMusic() {
        if (clip != null && !clip.isActive()) {
            clip.setMicrosecondPosition(currentFrame != null ? currentFrame : 0);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
            currentFrame = 0l;
        }else {
            System.out.println("clip is null");
        }
    }
}

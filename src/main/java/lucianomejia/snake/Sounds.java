package lucianomejia.snake;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sounds {

    private static final Map<String, Clip> sounds = new HashMap<>();

    public static void loadAllLocalSounds() {
        final File folder = new File("src/main/resources/sounds");
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory: " + folder.getAbsolutePath());
        }

        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            if (!file.isFile() || !file.getName().endsWith(".wav")) {
                continue;
            }

            try {
                final String fileNameWithoutExtension = getFileNameWithoutExtension(file.getName());
                sounds.put(fileNameWithoutExtension, loadSound(file.getAbsolutePath()));
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                System.err.println("Error loading sound file: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public static Clip loadSound(final String filePath) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        final File audioFile = new File(filePath);
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        }
    }

    private static String getFileNameWithoutExtension(final String fileName) {
        final int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex == -1 ? fileName : fileName.substring(0, lastDotIndex);
    }

    public static Clip get(String name) {
        return sounds.get(name);
    }

    public static void playSound(final String name, final float volume) {
        final Clip clip = get(name);
        if (clip == null) {
            System.err.println("Sound not found: " + name);

            return;
        }

        // Adjust the volume
        if (volume < 0f || volume > 1f) {
            System.err.println("Volume must be between 0.0 and 1.0");

            return;
        }

        final FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);

        clip.setFramePosition(0);
        clip.start();
    }

    public static void playSound(final String name) {
        playSound(name, 1f);
    }

    public static void stop(final String name) {
        final Clip clip = get(name);
        if (clip == null) {
            System.err.println("Sound not found: " + name);

            return;
        }

        clip.stop();
        clip.setFramePosition(0);
    }

    public static void cleanup() {
        for (final Clip clip : sounds.values()) {
            clip.close();
        }

        sounds.clear();
    }
}

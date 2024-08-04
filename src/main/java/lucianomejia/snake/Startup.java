package lucianomejia.snake;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.IOException;

public class Startup {

    public static void main(String[] args) {
        System.out.println("Initializing Snake Game...");

        EventQueue.invokeLater(() -> {
            Game game;
            try {
                game = new Game();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }

            game.setVisible(true);
        });
    }
}

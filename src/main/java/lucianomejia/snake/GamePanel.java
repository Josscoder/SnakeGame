package lucianomejia.snake;

import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class GamePanel extends JPanel {

    private final Game game;

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        game.draw(graphics);

        if (game.isPlaying()) {
            return;
        }

        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
    }
}

package lucianomejia.snake;

import lombok.RequiredArgsConstructor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@RequiredArgsConstructor
public class MovementKeyAdapter extends KeyAdapter {

    private final Game game;

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!game.isPlaying() && game.getStartLabel().isVisible()) {
            if (isArrowKey(key) || isWASDKey(key)) {
                game.resume();
                Sounds.stop("game.over");
                Sounds.playSound("ambience");
            }
        } else {
            if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && !game.isRightDirection()) {
                game.setLeftDirection(true);
                game.setUpDirection(false);
                game.setDownDirection(false);
            }

            if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && !game.isLeftDirection()) {
                game.setRightDirection(true);
                game.setUpDirection(false);
                game.setDownDirection(false);
            }

            if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && !game.isDownDirection()) {
                game.setUpDirection(true);
                game.setRightDirection(false);
                game.setLeftDirection(false);
            }

            if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && !game.isUpDirection()) {
                game.setDownDirection(true);
                game.setRightDirection(false);
                game.setLeftDirection(false);
            }
        }
    }

    private boolean isArrowKey(int key) {
        return key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT ||
                key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN;
    }

    private boolean isWASDKey(int key) {
        return key == KeyEvent.VK_W || key == KeyEvent.VK_A ||
                key == KeyEvent.VK_S || key == KeyEvent.VK_D;
    }
}

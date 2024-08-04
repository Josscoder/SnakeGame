package lucianomejia.snake.buttons;

import lucianomejia.snake.Game;

import javax.swing.*;
import java.awt.*;

public class ReloadButton extends JButton {

    public ReloadButton(Game game) {
        super("Reload");

        setFont(new Font("Monserrat", Font.BOLD, 16));
        setFocusPainted(false);
        setBackground(new Color(54, 86, 228));
        setForeground(Color.WHITE);
        addActionListener(game);
        setVisible(false);
    }
}

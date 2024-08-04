package lucianomejia.snake.labels;

import javax.swing.*;
import java.awt.*;

public class StartLabel extends JLabel {

    public StartLabel() {
        super("Press a key to start");

        setFont(new Font("Monserrat", Font.BOLD, 20));
        setForeground(Color.WHITE);
    }
}

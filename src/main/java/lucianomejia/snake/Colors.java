package lucianomejia.snake;

import java.awt.*;
import java.util.Random;

public class Colors {

    public static Color generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }
}

package lucianomejia.snake;

import lombok.Getter;
import lombok.Setter;
import lucianomejia.snake.buttons.ReloadButton;
import lucianomejia.snake.labels.StartLabel;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class Game extends JFrame implements ActionListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int DOT_SIZE = 20;
    private static final int ALL_DOTS = (WIDTH * HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private static final int DELAY = 130;

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    @Getter
    private final ReloadButton reloadButton = new ReloadButton(this);

    @Getter
    private final StartLabel startLabel = new StartLabel();

    private int dots;
    private int appleX;
    private int appleY;

    @Getter
    @Setter
    private boolean leftDirection = false;

    @Getter
    @Setter
    private boolean rightDirection = true;

    @Getter
    @Setter
    private boolean upDirection = false;

    @Getter
    @Setter
    private boolean downDirection = false;

    @Getter
    @Setter
    private boolean playing = false;

    private Timer timer;

    private int score = 0;
    private int highScore = 0;
    private long startTime;

    private final Color DEFAULT_SNAKE_COLOR = new Color(78, 169, 43);

    private Color snakeColor = DEFAULT_SNAKE_COLOR;

    private Timer colorEffectTimer;

    public Game() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        System.out.println("Loading local sounds...");
        Sounds.loadAllLocalSounds();

        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT + 50);
        setResizable(false);
        setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon("src/main/resources/icon.png");
        setIconImage(icon.getImage());

        addKeyListener(new MovementKeyAdapter(this));

        setBackground(Color.BLACK);
        setFocusable(true);

        JPanel gamePanel = new GamePanel(this);
        add(gamePanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawInfo(g);
            }
        };
        infoPanel.setPreferredSize(new Dimension(WIDTH, 50));
        infoPanel.setBackground(Color.ORANGE);
        add(infoPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.ORANGE);
        buttonPanel.add(startLabel);
        buttonPanel.add(reloadButton);

        add(buttonPanel, BorderLayout.SOUTH);

        timer = new Timer(DELAY, this);
        Sounds.stop("game.over");
        Sounds.playSound("ambience");

        reset();
    }

    public void reset() {
        playing = false;
        score = 0;
        startTime = System.currentTimeMillis();
        startLabel.setVisible(true);

        timer.stop();
        snakeColor = Color.GREEN;
        init();
    }

    public void init() {
        dots = 3;

        for (int i = 0; i < dots; i++) {
            x[i] = WIDTH / 2 - i * DOT_SIZE;
            y[i] = HEIGHT / 2;
        }

        placeApple();

        timer = new Timer(DELAY, this);
    }

    public void placeApple() {
        Random random = new Random();
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        int maxX = (WIDTH / DOT_SIZE) / 2;
        int maxY = (HEIGHT / DOT_SIZE) / 2;

        appleX = centerX + (random.nextInt(maxX) - maxX / 2) * DOT_SIZE;
        appleY = centerY + (random.nextInt(maxY) - maxY / 2) * DOT_SIZE;
    }

    public void checkCollectedApple() {
        if (x[0] == appleX && y[0] == appleY) {
            Sounds.playSound("collect");

            dots++;
            score++;
            placeApple();

            if (score % 5 == 0) {
                startRainbowColorEffect();
            }
        }
    }

    public void startRainbowColorEffect() {
        colorEffectTimer = new Timer(100, e -> setSnakeColor(Colors.generateRandomColor()));
        colorEffectTimer.setRepeats(true);
        colorEffectTimer.setCoalesce(true);
        colorEffectTimer.start();

        Timer resetEffectTimer = new Timer(3000, e -> {
            setSnakeColor(DEFAULT_SNAKE_COLOR);
            colorEffectTimer.stop();
        });
        resetEffectTimer.setRepeats(false);
        resetEffectTimer.start();
    }

    public void setSnakeColor(Color snakeColor) {
        this.snakeColor = snakeColor;
        repaint();
    }

    public void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    public void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if (i > 4 && x[0] == x[i] && y[0] == y[i]) {
                playing = false;
                break;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            playing = false;
        }

        if (!playing) {
            timer.stop();
            updateHighScore();
            reloadButton.setVisible(true);
        }
    }

    public void updateHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    public void draw(Graphics graphics) {
        if (playing) {
            drawGame(graphics);
        } else {
            drawGameOver(graphics);
        }
    }

    public void drawGame(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.fillOval(appleX, appleY, DOT_SIZE, DOT_SIZE);

        for (int i = 0; i < dots; i++) {
            graphics.setColor(snakeColor);
            graphics.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    public void drawGameOver(Graphics graphics) {
        if (startLabel.isVisible()) {
            return;
        }

        if (colorEffectTimer != null) {
            colorEffectTimer.stop();
        }

        Sounds.stop("ambience");
        Sounds.playSound("game.over");

        String message = "Game Over!";
        Font font = new Font("Monserrat", Font.BOLD, 60);
        FontMetrics metrics = graphics.getFontMetrics(font);

        graphics.setColor(new Color(210, 1, 3));
        graphics.setFont(font);
        graphics.drawString(message, (WIDTH - metrics.stringWidth(message)) / 2, HEIGHT / 2 - 30);

        reloadButton.setVisible(true);
    }

    public void drawInfo(Graphics graphics) {
        String scoreMessage = "Score: " + score;
        String highScoreMessage = "High Score: " + highScore;
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        String timeMessage = "Time: " + elapsedTime + "s";

        Font font = new Font("Monserrat", Font.BOLD, 15);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);

        graphics.drawString(scoreMessage, 10, 20);
        graphics.drawString(highScoreMessage, 150, 20);
        graphics.drawString(timeMessage, 300, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == reloadButton) {
            reloadButton.setVisible(false);
            reset();
            return;
        }

        if (!playing) {
            Sounds.stop("ambience");
            resume();
            return;
        }

        checkCollectedApple();
        checkCollision();
        move();
        repaint();
    }

    public void resume() {
        playing = true;
        startLabel.setVisible(false);
        startTime = System.currentTimeMillis();
        timer.start();
        repaint();
    }
}

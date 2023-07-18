package lucianomejia.snake;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SnakeGame extends JFrame implements ActionListener {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int DOT_SIZE = 20;
    private static final int ALL_DOTS = (WIDTH * HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private static final int DELAY = 130;

    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];
    private final JButton reloadButton;
    private final JLabel startLabel;
    private final JLabel scorePlusLabel;
    private int dots;
    private int appleX;
    private int appleY;
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = false;
    private Timer timer;
    private int score = 0;
    private int highScore = 0;

    private Color snakeColor = Color.GREEN;

    private Clip ambianceClip;
    private Clip gameOverClip;
    private Clip collectClip;
    private boolean inRainbowEffect;
    private Timer colorEffectTimer;

    public SnakeGame() {
        loadSounds();

        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon("src/main/resources/icon.png");
        setIconImage(icon.getImage());

        addKeyListener(new MyKeyAdapter());

        setBackground(Color.BLACK);
        setFocusable(true);

        JPanel gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        reloadButton = new JButton("Reload");
        reloadButton.setFont(new Font("Monserrat", Font.BOLD, 16));
        reloadButton.setFocusPainted(false);
        reloadButton.setBackground(Color.GREEN);
        reloadButton.setForeground(Color.WHITE);
        reloadButton.addActionListener(this);
        reloadButton.setVisible(false);

        startLabel = new JLabel("Press a key to start");
        startLabel.setFont(new Font("Monserrat", Font.BOLD, 20));
        startLabel.setForeground(Color.WHITE);

        scorePlusLabel = new JLabel();
        scorePlusLabel.setFont(new Font("Monserrat", Font.BOLD, 20));
        scorePlusLabel.setForeground(Color.GREEN);
        scorePlusLabel.setVisible(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(startLabel);
        buttonPanel.add(reloadButton);
        buttonPanel.add(scorePlusLabel);

        add(buttonPanel, BorderLayout.SOUTH);

        timer = new Timer(DELAY, this);
        stopSound(gameOverClip);
        playSound(ambianceClip);

        resetGame();
    }

    private void playSound(Clip clip) {
        clip.setFramePosition(0);
        clip.start();
    }

    private void stopSound(Clip clip) {
        clip.setFramePosition(0);
        clip.stop();
    }

    private void loadSounds() {
        try {
            ambianceClip = loadClip("src/main/resources/sounds/ambience.wav");
            gameOverClip = loadClip("src/main/resources/sounds/game_over.wav");
            collectClip = loadClip("src/main/resources/sounds/collect.wav");
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private Clip loadClip(String filePath) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        File audioFile = new File(filePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }

    private void resetGame() {
        inGame = false;
        score = 0;
        startLabel.setVisible(true);

        timer.stop();
        snakeColor = Color.GREEN;
        initGame();
    }

    private void initGame() {
        dots = 3;

        for (int i = 0; i < dots; i++) {
            x[i] = WIDTH / 2 - i * DOT_SIZE;
            y[i] = HEIGHT / 2;
        }

        placeApple();

        timer = new Timer(DELAY, this);
    }

    private void placeApple() {
        Random random = new Random();
        int maxX = (WIDTH - 2 * DOT_SIZE) / DOT_SIZE;
        int maxY = (HEIGHT - 2 * DOT_SIZE) / DOT_SIZE;

        appleX = (random.nextInt(maxX) + 1) * DOT_SIZE;
        appleY = (random.nextInt(maxY) + 1) * DOT_SIZE;
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            playSound(collectClip);

            dots++;
            score++;
            placeApple();

            if (score % 5 == 0) {
                startRainbowColorEffect();
            } else if (score % 2 == 0 && !inRainbowEffect) {
                changeSnakeColor();
            }

            showScorePlusLabel();
        }
    }

    private void startRainbowColorEffect() {
        colorEffectTimer = new Timer(100, e -> changeSnakeColor());
        colorEffectTimer.setRepeats(true);
        colorEffectTimer.setCoalesce(true);
        colorEffectTimer.start();
        inRainbowEffect = true;

        Timer resetEffectTimer = new Timer(3000, e -> {
            inRainbowEffect = false;
            snakeColor = Color.GREEN;
            colorEffectTimer.stop();
        });
        resetEffectTimer.setRepeats(false);
        resetEffectTimer.start();
    }


    private void changeSnakeColor() {
        snakeColor = generateRandomColor();
        repaint();
    }

    private Color generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }

    private void move() {
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

    private void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if (i > 4 && x[0] == x[i] && y[0] == y[i]) {
                inGame = false;
                break;
            }
        }

        if (x[0] < DOT_SIZE || x[0] >= WIDTH - DOT_SIZE || y[0] < DOT_SIZE || y[0] >= HEIGHT - DOT_SIZE) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
            updateHighScore();
            scorePlusLabel.setVisible(false);
            reloadButton.setVisible(true);
        }
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
        }
    }

    private void showScorePlusLabel() {
        scorePlusLabel.setText("+1 Score");
        scorePlusLabel.setVisible(true);

        Timer scorePlusTimer = new Timer(1000, e -> scorePlusLabel.setVisible(false));
        scorePlusTimer.setRepeats(false);
        scorePlusTimer.start();
    }

    private void draw(Graphics graphics) {
        if (inGame) {
            drawGame(graphics);
        } else {
            drawGameOver(graphics);
        }
    }

    private void drawGame(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.fillOval(appleX, appleY, DOT_SIZE, DOT_SIZE);

        for (int i = 0; i < dots; i++) {
            graphics.setColor(snakeColor);
            graphics.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawGameOver(Graphics graphics) {
        if (startLabel.isVisible()) {
            return;
        }

        if (colorEffectTimer != null) {
            colorEffectTimer.stop();
        }

        stopSound(ambianceClip);
        playSound(gameOverClip);

        String message = "Game Over";
        String scoreMessage = "Score: " + score;
        String highScoreMessage = "High Score: " + highScore;

        Font font = new Font("Monserrat", Font.BOLD, 35);
        FontMetrics metrics = graphics.getFontMetrics(font);

        graphics.setColor(Color.BLACK);
        graphics.setFont(font);
        graphics.drawString(message, (WIDTH - metrics.stringWidth(message)) / 2, HEIGHT / 2 - 30);
        graphics.drawString(scoreMessage, (WIDTH - metrics.stringWidth(scoreMessage)) / 2, HEIGHT / 2);
        graphics.drawString(highScoreMessage, (WIDTH - metrics.stringWidth(highScoreMessage)) / 2, HEIGHT / 2 + 30);

        scorePlusLabel.setVisible(false);
        reloadButton.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == reloadButton) {
            reloadButton.setVisible(false);
            resetGame();
            return;
        }

        if (!inGame) {
            stopSound(ambianceClip);
            continueGame();
            return;
        }

        checkApple();
        checkCollision();
        move();
        repaint();
    }

    private void continueGame() {
        inGame = true;
        startLabel.setVisible(false);
        timer.start();
        repaint();
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            draw(graphics);

            if (inGame) {
                return;
            }

            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (!inGame && startLabel.isVisible()) {
                if (isArrowKey(key) || isWASDKey(key)) {
                    continueGame();
                    stopSound(gameOverClip);
                    playSound(ambianceClip);
                }
            } else {
                if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && !rightDirection) {
                    leftDirection = true;
                    upDirection = false;
                    downDirection = false;
                }

                if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && !leftDirection) {
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                }

                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && !downDirection) {
                    upDirection = true;
                    rightDirection = false;
                    leftDirection = false;
                }

                if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && !upDirection) {
                    downDirection = true;
                    rightDirection = false;
                    leftDirection = false;
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
}
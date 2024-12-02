import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class CowGame extends JPanel implements ActionListener, KeyListener, MouseListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImg;
    Image cowImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image titleImg; // Title image

    // Cow variables (instead of bird)
    int cowX = boardWidth / 8;
    int cowY = boardHeight / 2;
    int cowWidth = 50;
    int cowHeight = 50;

    class Cow {
        int x = cowX;
        int y = cowY;
        int width = cowWidth;
        int height = cowHeight;
        Image img;

        Cow(Image img) {
            this.img = img;
        }
    }

    // Pipe variables
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game variables
    Cow cow;
    int velocityY = 0;
    int gravity = 1;
    int pipeGap = 200;
    int pipeSpeed = -4;
    int level = 1;
    double score = 0;
    double highScore = 0;
    String highScorePlayer = "None";
    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipeTimer;
    Timer levelUpTimer;
    boolean gameOver = false;
    boolean gamePaused = false;
    boolean gameStarted = false;

    // Restart Button
    JButton restartButton;

    // Start Screen variables
    JTextField nameField;
    JButton startButton;
    String playerName = ""; // Stores the player's name

    // Title screen flag
    boolean onTitleScreen = true;

    CowGame() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);  // Add mouse listener to detect clicks

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        cowImg = new ImageIcon(getClass().getResource("/cow.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();
        titleImg = new ImageIcon(getClass().getResource("/title.png")).getImage(); // Title image

        // Initialize cow
        cow = new Cow(cowImg);
        pipes = new ArrayList<>();

        // Place pipes timer
        placePipeTimer = new Timer(1500, e -> placePipes());

        // Game loop timer
        gameLoop = new Timer(1000 / 60, this);

        // Level up timer
        levelUpTimer = new Timer(15000, e -> increaseDifficulty());

        // Restart Button
        restartButton = new JButton("Restart");
        restartButton.setBounds(boardWidth / 4, boardHeight / 2 + 100, boardWidth / 2, 40);
        restartButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        restartButton.setBackground(Color.GREEN);
        restartButton.setForeground(Color.WHITE);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> resetGame());

        this.setLayout(null);
        this.add(restartButton);

        // Name field for entering player's name
        nameField = new JTextField("Enter your name");
        nameField.setBounds(boardWidth / 4, boardHeight / 2 - 30, boardWidth / 2, 30);
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

        // Start button
        startButton = new JButton("Start Game");
        startButton.setBounds(boardWidth / 4, boardHeight / 2 + 20, boardWidth / 2, 40);
        startButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        startButton.setBackground(Color.GREEN);
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> startGame());
    }

    void startGame() {
        playerName = nameField.getText().trim();
        if (playerName.isEmpty() || playerName.equals("Enter your name")) {
            playerName = "Player";
        }
        gameStarted = true;
        onTitleScreen = false;  // Game has started, no longer on title screen

        // Remove start screen components
        this.remove(nameField);
        this.remove(startButton);
        repaint();

        // Start the game timers
        placePipeTimer.start();
        gameLoop.start();
        levelUpTimer.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + pipeGap;
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        if (onTitleScreen) {
            // Draw title screen
            g.drawImage(titleImg, 0, 0, boardWidth, boardHeight, null);  // Title image should fill the screen
        } else {
            drawGame(g);
        }

        if (gameOver) {
            // Game Over Screen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
            String wellPlayedText = "WELL PLAYED!";
            FontMetrics metrics = g.getFontMetrics();
            int x = (boardWidth - metrics.stringWidth(wellPlayedText)) / 2; // Center the "WELL PLAYED!" text
            int y = boardHeight / 3;
            g.drawString(wellPlayedText, x, y);

            g.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
            String highScoreText = "High Score: " + (int) highScore + " by " + highScorePlayer;
            metrics = g.getFontMetrics();
            x = (boardWidth - metrics.stringWidth(highScoreText)) / 2; // Center the high score text
            int y2 = boardHeight / 3 + 40;
            g.drawString(highScoreText, x, y2);
        }
    }

    public void drawGame(Graphics g) {
        // Draw cow
        g.drawImage(cowImg, cow.x, cow.y, cow.width, cow.height, null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw score and level
        g.setColor(Color.WHITE);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        g.drawString("Score: " + (int) score, 10, 50);
        g.drawString("Level: " + level, 10, 90);
        g.drawString("Player: " + playerName, 10, 130);
    }

    public void move() {
        if (gameOver || gamePaused) return;

        velocityY += gravity;
        cow.y += velocityY;
        cow.y = Math.max(cow.y, 0);

        for (Pipe pipe : pipes) {
            pipe.x += pipeSpeed;

            if (!pipe.passed && cow.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(cow, pipe)) {
                gameOver = true;
                // Check if current score is higher than high score
                if (score > highScore) {
                    highScore = score;
                    highScorePlayer = playerName;
                }
            }
        }

        if (cow.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Cow a, Pipe b) {
        Rectangle cowBounds = new Rectangle(a.x, a.y, a.width, a.height);
        Rectangle pipeBounds = new Rectangle(b.x, b.y, b.width, b.height);
        return cowBounds.intersects(pipeBounds);
    }

    void increaseDifficulty() {
        if (level < 5) { // You can set a maximum level if needed
            level++;
            pipeSpeed -= 1; // Increase the speed of pipes
            pipeGap -= 10;  // Reduce the gap between pipes (optional)
            System.out.println("Level up! New level: " + level);
        }
    }

    void resetGame() {
        cow.y = cowY;
        velocityY = 0;
        score = 0;
        pipes.clear();
        gameOver = false;
        gameStarted = false;
        onTitleScreen = true;

        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver && !gamePaused && gameStarted) {
            velocityY = -15; // Make the cow jump
        }

        if (e.getKeyCode() == KeyEvent.VK_P && gameStarted) {
            gamePaused = !gamePaused;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameStarted && onTitleScreen) {
            Rectangle titleRect = new Rectangle(0, 0, boardWidth, boardHeight);
            if (titleRect.contains(e.getPoint())) {
                this.removeAll();
                this.add(nameField);
                this.add(startButton);
                repaint();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}
}


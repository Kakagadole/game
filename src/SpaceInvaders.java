import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SpaceInvaders extends JPanel implements Runnable, KeyListener {
    private LevelManager levelManager = new LevelManager();
    private boolean showLevelText = true;
    private long levelDisplayStartTime;
    MUSIC getBackgroundMusic;




    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    class Block {
        int x, y, width, height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int shipWidth = tileSize * 2;
    int shipHeight = tileSize * 2;
    int shipX = tileSize * columns / 2 - tileSize;
    int shipY = (tileSize * rows - tileSize * 3);
    int shipVelocityX = tileSize;
    Block ship;

    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;
    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;

    Thread gameThread;
    boolean gameOver = false;
    boolean isRunning = true;
    int score = 0;

    ImageIcon backgroundGif = new ImageIcon(getClass().getResource("/BackGround/Black And White Falling GIF by Pi-Slices.gif"));
    Image backgroundImage = backgroundGif.getImage();

    MUSIC backgroundMusic = new MUSIC();
    boolean gameOverSoundPlayed = false;

    public SpaceInvaders(MUSIC music) {
        this.backgroundMusic = music;

        backgroundMusic.gameMusic("/SOUND/space-invaders-classic-arcade-game-116826.wav");


        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        shipImg = new ImageIcon(getClass().getResource("/SpaceShip.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("/alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("/alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("/alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("/alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();



        createAliens();

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        int fps = 60;
        long targetTime = 1000 / fps;

        while (true) {
            if (isRunning) {
                move();
                repaint();

                if (gameOver) {
                    if (!gameOverSoundPlayed) {
                        backgroundMusic.stopMusic();
                        backgroundMusic.playEffectSound("/SOUND/game-over.wav");
                        gameOverSoundPlayed = true;
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            try {
                Thread.sleep(targetTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        draw(g);

        if (gameOver && !gameOverSoundPlayed) {

            backgroundMusic.playEffectSound("/SOUND/game-over-31-179699.wav");
            gameOverSoundPlayed = true;
        }
    }

    public void draw(Graphics g) {
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        g.setColor(Color.red);
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.fillRect(bullet.x, bullet.y, bullet.width / 2, bullet.height * 2);
            }
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if (gameOver) {

            g.setFont(new Font("Arial", Font.BOLD, 64));
            FontMetrics fm = g.getFontMetrics();
            String gameOverText = "GAME OVER";
            int x = (boardWidth - fm.stringWidth(gameOverText)) / 2;
            int y = boardHeight / 2;
            g.drawString(gameOverText, x, y);

            g.setFont(new Font("Arial", Font.PLAIN, 32));
            String scoreText = "Score: " + score;
            int scoreX = (boardWidth - g.getFontMetrics().stringWidth(scoreText)) / 2;
            g.drawString(scoreText, scoreX, y + 50);
        } else {
            g.drawString("Score: " + score, 10, 35);
        }

        if (!isRunning && !gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g.getFontMetrics();
            String pauseText = "Game Paused";
            int x = (boardWidth - fm.stringWidth(pauseText)) / 2;
            int y = boardHeight / 2;
            g.drawString(pauseText, x, y);
        }
    }

    public void move() {
        for (Block alien : alienArray) {
            if (alien.alive) {
                alien.x += alienVelocityX;

                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;
                    for (Block a : alienArray) {
                        a.y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }

        for (Block bullet : bulletArray) {
            bullet.y += bulletVelocityY;

            for (Block alien : alienArray) {
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
            }
        }

        bulletArray.removeIf(b -> b.used || b.y < 0);

        if (alienCount == 0) {
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, columns / 2 - 2);
            alienRows = Math.min(alienRows + 1, rows - 6);
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }

    }

    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        alienX + c * alienWidth,
                        alienY + r * alienHeight,
                        alienWidth,
                        alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            resetGame();
            gameThread = new Thread(this);
            gameThread.start();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0 && isRunning) {
            ship.x -= shipVelocityX;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipVelocityX + ship.width <= boardWidth && isRunning) {
            ship.x += shipVelocityX;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE && isRunning) {
            Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
            backgroundMusic.playEffectSound("/SOUND/laser-zap-90575.wav");
        } else if (e.getKeyCode() == KeyEvent.VK_P) {
            isRunning = !isRunning;
            if (isRunning) {
                backgroundMusic.resumeBackgroundMusic();
            } else {
                backgroundMusic.pauseBackgroundMusic();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            resetGame();
        }
    }

    public void resetGame() {
        ship.x = shipX;
        bulletArray.clear();
        alienArray.clear();
        gameOver = false;
        isRunning = true;
        score = 0;
        alienColumns = 3;
        alienRows = 2;
        alienVelocityX = 1;
        createAliens();
        gameOverSoundPlayed = false;
        backgroundMusic.stopMusic();
        backgroundMusic.gameMusic("/SOUND/space-invaders-classic-arcade-game-116826.wav");
    }
}

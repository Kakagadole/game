import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SpaceInvaders extends JPanel implements Runnable, KeyListener {
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
        float x, y;
        int width, height;
        Image img;
        boolean alive = true;
        boolean used = false;
        boolean canShoot = false;

        Block(float x, float y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int shipWidth = tileSize * 2;
    int shipHeight = tileSize * 2;
    float shipX = tileSize * columns / 2 - tileSize;
    float shipY = (tileSize * rows - tileSize * 3);
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

    ArrayList<Block> alienBulletArray;
    float alienBulletSpeed = 88f; // pixels per second
    float fps = 60f;
    float alienBulletVelocityY = alienBulletSpeed / fps;

    Thread gameThread;
    boolean gameOver = false;
    int score = 0;
    int currentLevel = 0;
    int wavesCompleted = 0; // מעקב אחר מספר הגלים
    boolean isTransitioning = false;
    long transitionStartTime;
    long lastAlienShotTime = 0;

    ImageIcon backgroundGif = new ImageIcon(getClass().getResource("BackGround/Black And White Falling GIF by Pi-Slices.gif"));
    Image backgroundImage = backgroundGif.getImage();

    public SpaceInvaders() {
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
        alienBulletArray = new ArrayList<>();

        createAliens();

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        int fps = 60;
        long targetTime = 1000 / fps;

        while (true) {
            if (!isTransitioning && !gameOver) {
                move();
            }
            repaint();

            if (gameOver) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            } else if (isTransitioning) {
                if (System.currentTimeMillis() - transitionStartTime >= 2000) {
                    isTransitioning = false;
                    currentLevel = 2;
                    alienColumns = 3;
                    alienRows = 2;
                    alienArray.clear();
                    bulletArray.clear();
                    alienBulletArray.clear();
                    createAliens();
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
        if (isTransitioning) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Level 2", boardWidth / 2 - 50, boardHeight / 2);
        } else {
            draw(g);
        }
        // בדיקה לקליעות חייזרים בשלב הראשון (לדיבוג)
        if (currentLevel == 1 && !alienBulletArray.isEmpty()) {
            System.out.println("Warning: Alien bullets in level 1");
        }
    }

    public void draw(Graphics g) {
        g.drawImage(ship.img, (int)ship.x, (int)ship.y, ship.width, ship.height, null);

        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, (int)alien.x, (int)alien.y, alien.width, alien.height, null);
            }
        }

        g.setColor(Color.red);
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.fillRect((int)bullet.x, (int)bullet.y, bullet.width / 2, bullet.height * 2);
            }
        }

        g.setColor(Color.green);
        for (Block alienBullet : alienBulletArray) {
            g.fillRect((int)alienBullet.x, (int)alienBullet.y, alienBullet.width / 2, alienBullet.height * 2);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + score, 10, 35);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    public void move() {
        System.out.println("Current level: " + currentLevel);

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
        }

        for (Block bullet : new ArrayList<>(bulletArray)) {
            if (!bullet.used && bullet.y >= 0) {
                for (Block alien : alienArray) {
                    if (alien.alive && detectCollision(bullet, alien)) {
                        bullet.used = true;
                        alien.alive = false;
                        alienCount--;
                        score += 100;
                        break;
                    }
                }
            }
        }
        bulletArray.removeIf(bullet -> bullet.used || bullet.y < 0);

        if (currentLevel == 2) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAlienShotTime >= 2000) {
                List<Block> activeShooters = new ArrayList<>();
                for (Block alien : alienArray) {
                    if (alien.alive && alien.canShoot) {
                        activeShooters.add(alien);
                    }
                }
                if (!activeShooters.isEmpty()) {
                    Random random = new Random();
                    int index = random.nextInt(activeShooters.size());
                    Block shooter = activeShooters.get(index);
                    float bulletX = shooter.x + shooter.width / 2f - bulletWidth / 2f;
                    float bulletY = shooter.y + shooter.height;
                    Block alienBullet = new Block(bulletX, bulletY, bulletWidth, bulletHeight, null);
                    alienBulletArray.add(alienBullet);
                    lastAlienShotTime = currentTime;
                    System.out.println("Alien shooting in level 2");
                }
            }
        }

        for (Block alienBullet : new ArrayList<>(alienBulletArray)) {
            alienBullet.y += alienBulletVelocityY;
            if (detectCollision(alienBullet, ship)) {
                gameOver = true;
                alienBulletArray.remove(alienBullet);
            } else if (alienBullet.y > boardHeight) {
                alienBulletArray.remove(alienBullet);
            }
        }

        if (alienCount == 0) {
            score += alienColumns * alienRows * 100; // בונוס עבור השלמת גל
            if (currentLevel == 1) {
                wavesCompleted++;
                if (score>= 8200) {
                    // מעבר לשלב 2
                    alienBulletArray.clear(); // ניקוי קליעות חייזרים קיימות
                    isTransitioning = true;
                    transitionStartTime = System.currentTimeMillis();
                } else {
                    // צור גל חדש עבור שלב 1
                    alienColumns = Math.min(alienColumns + 1, columns / 2 - 2);
                    alienRows = Math.min(alienRows + 1, rows - 6);
                    alienArray.clear();
                    bulletArray.clear();
                    alienBulletArray.clear(); // ניקוי קליעות חייזרים
                    createAliens(); // שלב 1, ללא ירי
                }
            } else if (currentLevel == 2) {
                // צור גל חדש עבור שלב 2
                alienColumns = Math.min(alienColumns + 1, columns / 2 - 2);
                alienRows = Math.min(alienRows + 1, rows - 6);
                alienArray.clear();
                bulletArray.clear();
                alienBulletArray.clear();
                createAliens();
                 resetGame();
            }
        }
    }

    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        (float)(alienX + c * alienWidth),
                        (float)(alienY + r * alienHeight),
                        alienWidth,
                        alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
        if (currentLevel == 2) {
            int numShooters = Math.max(1, alienCount / 6);
            List<Block> shuffled = new ArrayList<>(alienArray);
            Collections.shuffle(shuffled);
            for (int i = 0; i < numShooters; i++) {
                if (currentLevel == 2) {
                    shuffled.get(i).canShoot = true;
                }
            }

        }
        // בדיקה לדיבוג: ודא שאין חייזרים יורים בשלב 1
        if (currentLevel == 1) {
            for (Block alien : alienArray) {
                if (alien.canShoot) {
                    System.out.println("Warning: Alien can shoot in level 1");
                }
            }
        }
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (!isTransitioning && !gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
                ship.x -= shipVelocityX;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipVelocityX + ship.width <= boardWidth) {
                ship.x += shipVelocityX;
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                Block bullet = new Block(ship.x + shipWidth * 15f / 32f, ship.y, bulletWidth, bulletHeight, null);
                bulletArray.add(bullet);
                Music music = new Music();
                music.playEffectSound("SOUND/laser-zap-90575.wav");
            } else if (e.getKeyCode() == KeyEvent.VK_R) {
                resetGame();
            }
        }
    }

    public void resetGame() {
        ship.x = shipX;
        bulletArray.clear();
        alienArray.clear();
        gameOver = false;
        score = 0;
        alienColumns = 3;
        alienRows = 2;
        alienVelocityX = 1;
        currentLevel = 1;
        wavesCompleted = 0;
        createAliens();
        move();



    }
}
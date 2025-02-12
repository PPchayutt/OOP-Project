/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Theep
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {
    // Game states
    public static int GAME_WIDTH = 1024;  // เพิ่มจาก 800
    public static int GAME_HEIGHT = 768;
    
    private enum GameState {
        MENU, PLAYING, PAUSED, GAMEOVER
    }
    private GameState currentState = GameState.MENU;
    
    // Game objects
    // Game objects
    private Thread gameThread;
    private Player player;
    private List<Enemy> enemies;
    private Boss boss;
    private boolean running = false;
    private int score = 0;
    private List<PowerUp> powerUps;
    private boolean isBossSpawned = false;
    private int bossDifficulty = 1;
    private float enemySpawnRate = 0.02f;
    private static final int BOSS_SPAWN_SCORE = 1000;
    private int enemyKillRequiredForNextBoss = 20; // จำนวน enemy ที่ต้องฆ่าก่อนขึ้น boss
    private int currentEnemyKilled = 0;
    
    public int getGameWidth() {
        return getWidth();
    }
    
    public int getGameHeight() {
        return getHeight();
    }
    
    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setupInput();
        // ไม่เรียก initializeGame() ที่นี่
    }
    
    
    private void initializeGame() {
        // แก้จาก player = new Player(400, 300, this); เป็น:
        player = new Player(getGameWidth()/2 - 12, getGameHeight()/2 - 12, this);  // ลบครึ่งของขนาด player (25/2) เพื่อให้อยู่กลางจริงๆ
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        boss = null;
        isBossSpawned = false;
    }

    private void setupInput() {
        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(currentState) {
                    case MENU:
                        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                            startGame();
                            currentState = GameState.PLAYING;
                        }
                        break;
                    case PLAYING:
                        switch(e.getKeyCode()) {
                            case KeyEvent.VK_W: player.setPressingUp(true); break;
                            case KeyEvent.VK_S: player.setPressingDown(true); break;
                            case KeyEvent.VK_A: player.setPressingLeft(true); break;
                            case KeyEvent.VK_D: player.setPressingRight(true); break;
                            case KeyEvent.VK_ESCAPE:
                                currentState = GameState.PAUSED;
                                break;
                        }
                        break;
                    case PAUSED:
                        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            currentState = GameState.PLAYING;
                        }
                        break;
                    case GAMEOVER:
                        if(e.getKeyCode() == KeyEvent.VK_R) {
                            resetGame();
                            currentState = GameState.PLAYING;
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(currentState == GameState.PLAYING) {
                    switch(e.getKeyCode()) {
                        case KeyEvent.VK_W: player.setPressingUp(false); break;
                        case KeyEvent.VK_S: player.setPressingDown(false); break;
                        case KeyEvent.VK_A: player.setPressingLeft(false); break;
                        case KeyEvent.VK_D: player.setPressingRight(false); break;
                    }
                }
            }
        });
        
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(currentState == GameState.PLAYING) {
                    player.shoot(e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if(currentState == GameState.PLAYING) {
                    player.shoot(e.getX(), e.getY());
                }
            }
        };
        
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }
    
    private void spawnEnemy() {
        int side = (int)(Math.random() * 4);
        float x = 0, y = 0;

        switch(side) {
            case 0: // top
                x = (float)(Math.random() * getGameWidth());
                y = -20;
                break;
            case 1: // right
                x = getGameWidth() + 20;
                y = (float)(Math.random() * getGameHeight());
                break;
            case 2: // bottom
                x = (float)(Math.random() * getGameWidth());
                y = getGameHeight() + 20;
                break;
            case 3: // left
                x = -20;
                y = (float)(Math.random() * getGameHeight());
                break;
        }

        enemies.add(new Enemy(x, y, player, bossDifficulty));
    }
    
    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();

        // Check enemy collisions with player
        for(Enemy enemy : enemies) {
            if(enemy.getBounds().intersects(playerBounds)) {
                enemy.attackPlayer();
                if(player.isDead()) {
                    gameOver();
                    return;
                }
            }
        }

        // Check boss collisions
        if (isBossSpawned && boss != null) {
            // Boss bullets collision with player
            for (Bullet bossBullet : boss.getBossBullets()) {
                Rectangle bossBulletBounds = bossBullet.getBounds();
                if (bossBulletBounds.intersects(playerBounds)) {
                    player.damage(bossBullet.getDamage());
                    bossBullet.deactivate();

                    if (player.isDead()) {
                        gameOver();
                        return;
                    }
                }
            }

            // Player bullets collision with boss
            List<Bullet> bullets = player.getBullets();
            for(int i = bullets.size() - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                Rectangle bulletBounds = bullet.getBounds();

                if (boss.getBounds().intersects(bulletBounds)) {
                    boss.damage(bullet.getDamage());
                    bullet.deactivate();
                    break;
                }
            }
        }

        // Check bullet collisions with enemies
        List<Bullet> bullets = player.getBullets();
        for(int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            Rectangle bulletBounds = bullet.getBounds();

            for(int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if(enemy.getBounds().intersects(bulletBounds)) {
                    enemy.damage(25);
                    bullet.deactivate();
                    if(enemy.isDead()) {
                        enemies.remove(j);
                        score += 100;
                        currentEnemyKilled++; // นับจำนวน enemy ที่ถูกฆ่า

                        // เพิ่มความยากให้ enemy เมื่อฆ่า enemy ครบจำนวน
                        if (currentEnemyKilled % 10 == 0) {
                            bossDifficulty++; // เพิ่มระดับความยาก
                            enemySpawnRate += 0.005f; // เพิ่มอัตราการสปอน
                        }
                        break;
                    }
                }
            }
        }
    }
    
    private void gameOver() {
        currentState = GameState.GAMEOVER;
        running = false;
    }
    
    private void resetGame() {
        score = 0;
        bossDifficulty = 1; // รีเซ็ตระดับความยาก
        currentEnemyKilled = 0; // รีเซ็ตจำนวน enemy ที่ฆ่า
        enemyKillRequiredForNextBoss = 20; // รีเซ็ตจำนวน enemy ที่ต้องฆ่าก่อนขึ้น boss
        enemySpawnRate = 0.02f; // รีเซ็ตอัตราการสปอน
        player = new Player(getGameWidth()/2 - 12, getGameHeight()/2 - 12, this);
        enemies.clear();
        powerUps.clear();
        boss = null;
        isBossSpawned = false;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
     }

    public void stopGame() {
        running = false;
        try {
            if(gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void setupGame() { 
        player = new Player(GAME_WIDTH/2 - 12, GAME_HEIGHT/2 - 12, this);
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        boss = null;
        isBossSpawned = false;
        score = 0;
        bossDifficulty = 1; // ตั้งค่าระดับความยากเริ่มต้น
        currentEnemyKilled = 0; // ตั้งค่าจำนวน enemy ที่ฆ่าเริ่มต้น
        enemyKillRequiredForNextBoss = 20; // ตั้งค่าจำนวน enemy ที่ต้องฆ่าก่อนขึ้น boss
        enemySpawnRate = 0.02f; // ตั้งค่าอัตราการสปอนเริ่มต้น
        currentState = GameState.MENU;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        
        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            if(delta >= 1) {
                if(currentState == GameState.PLAYING) {
                    update();
                }
                repaint();
                delta--;
            }
        }
    }
    
    private void update() {
        player.update();

        // เช็คเงื่อนไขสำหรับสปอนบอส
        if (!isBossSpawned && currentEnemyKilled >= enemyKillRequiredForNextBoss) {
            spawnBoss();
            currentEnemyKilled = 0; // รีเซ็ตการนับ
        }

        // สปอนศัตรูปกติ
        if (!isBossSpawned) {
            if (Math.random() < enemySpawnRate) {
                spawnEnemy();
            }
        }

        // อัพเดทศัตรู
        for(int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update();

            // แก้ไขการเช็คขอบจอให้ใช้ขนาดจอจริง
            if(enemy.getX() < -200 || enemy.getX() > getGameWidth() + 200 ||
               enemy.getY() < -200 || enemy.getY() > getGameHeight() + 200) {
                enemies.remove(i);
            }
        }

        // อัพเดทบอส
        if (isBossSpawned && boss != null) {
            boss.update();

            // ถ้าบอสตาย
            if (boss.isDead()) {
                boss = null;
                isBossSpawned = false;
                score += 500 * bossDifficulty; // โบนัสคะแนนสำหรับฆ่าบอส
            }
        }

        spawnPowerUp();
        checkPowerUpCollisions();
        checkCollisions();
    }
    
    private void spawnBoss() {
        // สปอนบอสตรงกลางหน้าจอ
        boss = new Boss(getGameWidth()/2 - 25, getGameHeight()/2 - 25, player, bossDifficulty);
        isBossSpawned = true;
        enemies.clear(); // ล้างศัตรูปกติเมื่อบอสเกิด
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch(currentState) {
            case MENU:
                drawMenu(g2d);
                break;
            case PLAYING:
                drawGame(g2d);
                break;
            case PAUSED:
                drawGame(g2d);
                drawPauseScreen(g2d);
                break;
            case GAMEOVER:
                drawGame(g2d);
                drawGameOverScreen(g2d);
                break;
        }
    }

    private void drawMenu(Graphics2D g2d) {
        // Background
        g2d.setColor(new Color(0, 0, 30));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        g2d.setColor(Color.WHITE);
        String title = "SPACE SHOOTER";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 200);

        // Start button
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        String startText = "Press SPACE to Start";
        fm = g2d.getFontMetrics();
        int startX = (getWidth() - fm.stringWidth(startText)) / 2;
        g2d.drawString(startText, startX, 300);

        // Controls
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] controls = {
            "Controls:",
            "WASD - Move",
            "Mouse - Aim and Shoot",
            "ESC - Pause",
            "R - Restart (when game over)"
        };
        
        int y = 400;
        for(String text : controls) {
            fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            g2d.drawString(text, x, y);
            y += 30;
        }
    }
    
    private void spawnPowerUp() {
        if (Math.random() < 0.005) {
            // เพิ่มระยะห่างจากขอบ
            int margin = 50;
            float x = margin + (float)(Math.random() * (getGameWidth() - 2 * margin));
            float y = margin + (float)(Math.random() * (getGameHeight() - 2 * margin));
            PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
            PowerUp.PowerUpType randomType = types[(int)(Math.random() * types.length)];
            powerUps.add(new PowerUp(x, y, randomType));
        }
    }
    
    private void checkPowerUpCollisions() {
        Rectangle playerBounds = player.getBounds();
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            if (powerUp.isActive() && powerUp.getBounds().intersects(playerBounds)) {
                player.applyPowerUp(powerUp);
                powerUps.remove(i);
            }
        }
    }
    
    private void drawGame(Graphics2D g2d) {
        // สร้าง copy ของ lists เพื่อป้องกัน concurrent modification
        ArrayList<Enemy> enemiesCopy = new ArrayList<>(enemies);
        ArrayList<PowerUp> powerUpsCopy = new ArrayList<>(powerUps);

        player.render(g2d);
        for(Enemy enemy : enemiesCopy) {
            enemy.render(g2d);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("HP: " + player.getHealth(), 20, 60);
        
        // แสดงระดับความยาก
        g2d.drawString("Difficulty: " + bossDifficulty, 20, 90);
        
        // เพิ่มแถบแสดงสถานะการรีโหลด
        long currentTime = System.currentTimeMillis();
        long timeSinceLastShot = currentTime - player.getLastShotTime();
        float reloadProgress = Math.min(1.0f, timeSinceLastShot / (float)player.getShootCooldown());

        // วาดแถบรีโหลด
        int reloadBarWidth = 100;
        int reloadBarHeight = 5;
        int reloadBarX = 20;
        int reloadBarY = 110;

        // แถบพื้นหลัง
        g2d.setColor(Color.GRAY);
        g2d.fillRect(reloadBarX, reloadBarY, reloadBarWidth, reloadBarHeight);

        // แถบความคืบหน้า
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(reloadBarX, reloadBarY, (int)(reloadBarWidth * reloadProgress), reloadBarHeight);

        // แสดงข้อความ
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString("RELOAD", reloadBarX + reloadBarWidth + 10, reloadBarY + reloadBarHeight);

        for (PowerUp powerUp : powerUpsCopy) {
            powerUp.render(g2d);
        }
        
        // Render boss if spawned
        if (isBossSpawned && boss != null) {
            boss.render(g2d);
        }
    }
    
    private void drawPauseScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        String text = "PAUSED";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(text, x, y);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        String gameOver = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(gameOver)) / 2;
        g2d.drawString(gameOver, x, getHeight()/2 - 50);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreText = "Score: " + score;
        String restartText = "Press R to Restart";
        fm = g2d.getFontMetrics();
        x = (getWidth() - fm.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, x, getHeight()/2);
        x = (getWidth() - fm.stringWidth(restartText)) / 2;
        g2d.drawString(restartText, x, getHeight()/2 + 50);
    }
}
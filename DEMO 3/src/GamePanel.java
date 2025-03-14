import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    // ค่าคงที่ของเกม
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int FPS = 60;
    
    // สถานะของเกม
    private enum GameState {
        MENU, PLAYING, PAUSED, GAMEOVER
    }
    private GameState currentState = GameState.MENU;
    
    // ส่วนประกอบหลักของเกม
    private Thread gameThread;
    private boolean running = false;
    private Random random = new Random();
    
    // ผู้เล่นและวัตถุในเกม
    private Player player;
    private List<Monster> monsters;
    private Boss boss;
    private List<PlayerBullet> playerBullets;
    private List<EnemyBullet> enemyBullets;
    private List<Powerup> powerups;
    
    // ตัวจัดการระดับ
    private LevelManager levelManager;
    
    // ข้อมูลเกม
    private int score = 0;
    private int currentLevel = 1;
    private int monstersKilled = 0;
    private int monstersRequired = 20;
    private boolean bossSpawned = false;
    
    // timer
    private long lastMonsterSpawnTime = 0;
    private int monsterSpawnInterval = 1000; // มิลลิวินาที
    
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        try {
            // โหลดรูปภาพทั้งหมด
            ImageManager.loadImages();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        setupInput();
    }
    
    // ตั้งค่าการรับ input จากผู้เล่น
    private void setupInput() {
        try {
            InputHandler inputHandler = new InputHandler(this);
            addKeyListener(inputHandler);
            addMouseListener(inputHandler);
            addMouseMotionListener(inputHandler);
        } catch (Exception e) {
            System.err.println("Error setting up input: " + e.getMessage());
        }
    }
    
    // ตัวแปรสำหรับเก็บการกดปุ่ม
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    
    // จัดการกับปุ่มที่กด (public เพื่อให้ InputHandler เรียกใช้ได้)
    public void handleKeyPressed(int keyCode) {
        switch (currentState) {
            case MENU:
                if (keyCode == KeyEvent.VK_SPACE) {
                    startGame();
                }
                break;
                
            case PLAYING:
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        upPressed = true;
                        break;
                    case KeyEvent.VK_S:
                        downPressed = true;
                        break;
                    case KeyEvent.VK_A:
                        leftPressed = true;
                        break;
                    case KeyEvent.VK_D:
                        rightPressed = true;
                        break;
                    case KeyEvent.VK_ESCAPE:
                        togglePause();
                        break;
                }
                break;
                
            case PAUSED:
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
                break;
                
            case GAMEOVER:
                if (keyCode == KeyEvent.VK_R) {
                    setupGame();
                    startGame();
                }
                break;
        }
    }
    
    // จัดการกับปุ่มที่ปล่อย (public เพื่อให้ InputHandler เรียกใช้ได้)
    public void handleKeyReleased(int keyCode) {
        if (currentState == GameState.PLAYING) {
            switch (keyCode) {
                case KeyEvent.VK_W:
                    upPressed = false;
                    break;
                case KeyEvent.VK_S:
                    downPressed = false;
                    break;
                case KeyEvent.VK_A:
                    leftPressed = false;
                    break;
                case KeyEvent.VK_D:
                    rightPressed = false;
                    break;
            }
        }
    }
    
    // อัพเดทการเคลื่อนที่ของผู้เล่น
    private void updatePlayerMovement() {
        if (currentState != GameState.PLAYING || player == null) return;
        
        float targetVelX = 0;
        float targetVelY = 0;
        
        // คำนวณทิศทางการเคลื่อนที่
        if (leftPressed && !rightPressed) {
            targetVelX = -player.getMaxSpeed();
        } else if (rightPressed && !leftPressed) {
            targetVelX = player.getMaxSpeed();
        }
        
        if (upPressed && !downPressed) {
            targetVelY = -player.getMaxSpeed();
        } else if (downPressed && !upPressed) {
            targetVelY = player.getMaxSpeed();
        }
        
        // ปรับแก้ความเร็วเมื่อเคลื่อนที่แนวทแยง
        if (targetVelX != 0 && targetVelY != 0) {
            float factor = 0.7071f; // ประมาณ 1/sqrt(2)
            targetVelX *= factor;
            targetVelY *= factor;
        }
        
        // ส่งค่าให้ player
        player.setTargetVelocity(targetVelX, targetVelY);
    }
    
    // จัดการกับการคลิกเมาส์ (public เพื่อให้ InputHandler เรียกใช้ได้)
    public void handleMousePressed(int x, int y) {
        if (currentState == GameState.PLAYING && player != null) {
            player.shoot(x, y);
        }
    }
    
    // ตั้งค่าเกมใหม่
    public void setupGame() {
        try {
            System.out.println("กำลังตั้งค่าเกมใหม่...");
            
            player = new Player(WIDTH / 2 - 15, HEIGHT / 2 - 15);
            monsters = new ArrayList<>();
            playerBullets = new ArrayList<>();
            enemyBullets = new ArrayList<>();
            powerups = new ArrayList<>();
            
            levelManager = new LevelManager();
            
            score = 0;
            currentLevel = 1;
            monstersKilled = 0;
            monstersRequired = 20;
            bossSpawned = false;
            boss = null;
            
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            
            currentState = GameState.MENU;
            
            System.out.println("ตั้งค่าเกมใหม่เรียบร้อยแล้ว");
        } catch (Exception e) {
            System.err.println("Error setting up game: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // เริ่มเกม
    private void startGame() {
        if (currentState != GameState.PLAYING) {
            currentState = GameState.PLAYING;
            if (!running) {
                startGameLoop();
            }
        }
    }
    
    // เริ่มการทำงานของ game loop
    public void startGameLoop() {
        try {
            if (gameThread == null || !running) {
                // เรียก setupGame() ก่อนเริ่ม thread ถ้า player ยังเป็น null
                if (player == null) {
                    setupGame();
                }

                running = true;
                gameThread = new Thread(this);
                gameThread.start();
                System.out.println("เริ่มการทำงานของ game loop");
            }
        } catch (Exception e) {
            System.err.println("Error starting game loop: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // สลับสถานะหยุดชั่วคราว
    private void togglePause() {
        if (currentState == GameState.PLAYING) {
            currentState = GameState.PAUSED;
        } else if (currentState == GameState.PAUSED) {
            currentState = GameState.PLAYING;
        }
    }
    
    // เมื่อเกมจบ
    private void gameOver() {
        currentState = GameState.GAMEOVER;
    }

    @Override
    public void run() {
        try {
            // ตั้งค่าการจำกัด FPS
            double drawInterval = 1000000000 / FPS;
            double delta = 0;
            long lastTime = System.nanoTime();
            long currentTime;
            
            // game loop หลัก
            while (running) {
                currentTime = System.nanoTime();
                
                delta += (currentTime - lastTime) / drawInterval;
                lastTime = currentTime;
                
                if (delta >= 1) {
                    // อัพเดทและวาดเกม
                    update();
                    repaint();
                    delta--;
                }
                
                // ป้องกัน CPU ทำงานหนักเกินไป
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            System.err.println("Game loop interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in game loop: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // อัพเดทสถานะเกม
    private void update() {
        if (currentState != GameState.PLAYING) {
            return;
        }
        
        try {
            // อัพเดทผู้เล่น
            if (player != null) {
                player.update();
                
                // ตรวจสอบว่าผู้เล่นยังมีชีวิตอยู่หรือไม่
                if (!player.isAlive()) {
                    gameOver();
                    return;
                }
                
                // อัพเดทการเคลื่อนที่ของผู้เล่น
                updatePlayerMovement();
            }
            
            // สร้างมอนสเตอร์
            long currentTime = System.currentTimeMillis();
            if (!bossSpawned && currentTime - lastMonsterSpawnTime > monsterSpawnInterval) {
                spawnMonster();
                lastMonsterSpawnTime = currentTime;
            }
            
            // ตรวจสอบว่าควรจะสร้างบอสหรือไม่
            if (monstersKilled >= monstersRequired && !bossSpawned) {
                spawnBoss();
            }
            
            // อัพเดทมอนสเตอร์
            updateMonsters();
            
            // อัพเดทบอส
            updateBoss();
            
            // อัพเดทกระสุนของผู้เล่น
            updatePlayerBullets();
            
            // อัพเดทกระสุนของศัตรู
            updateEnemyBullets();
            
            // อัพเดทพาวเวอร์อัพ
            updatePowerups();
            
            // ตรวจสอบการชนกัน
            checkCollisions();
        } catch (Exception e) {
            System.err.println("Error updating game: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // วาดกราฟิกของเกม
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        try {
            Graphics2D g2d = (Graphics2D) g;
            
            // เปิดการทำ anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // วาดตามสถานะปัจจุบันของเกม
            switch (currentState) {
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
        } catch (Exception e) {
            System.err.println("Error rendering game: " + e.getMessage());
            e.printStackTrace();
            
            // ในกรณีที่เกิดข้อผิดพลาดในการวาด ให้แสดงข้อความแจ้งเตือน
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("เกิดข้อผิดพลาดในการแสดงผลเกม!", 200, 200);
            g2d.drawString("โปรดตรวจสอบคอนโซล", 200, 230);
        }
    }
    
    // วาดหน้าเมนู
    private void drawMenu(Graphics2D g) {
        // วาดพื้นหลัง
        g.setColor(new Color(0, 0, 30)); // สีฟ้าเข้มเกือบดำ
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // วาดชื่อเกม
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(Color.WHITE);
        String title = "2D Bullet Hell";
        int titleX = (WIDTH - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, titleX, HEIGHT / 3);
        
        // คำแนะนำ
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String startText = "กด SPACE เพื่อเริ่มเกม";
        int startX = (WIDTH - g.getFontMetrics().stringWidth(startText)) / 2;
        g.drawString(startText, startX, HEIGHT / 2);
        
        // คำแนะนำการเล่น
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        String[] instructions = {
            "WASD - เคลื่อนที่",
            "เมาส์คลิกซ้าย - ยิง",
            "ESC - หยุดชั่วคราว"
        };
        
        int y = HEIGHT / 2 + 50;
        for (String text : instructions) {
            int x = (WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
            g.drawString(text, x, y);
            y += 25;
        }
    }
    
    private void drawGame(Graphics2D g) {
        try {
            // วาดพื้นหลัง
            g.drawImage(ImageManager.getImage("background"), 0, 0, WIDTH, HEIGHT, null);

            // วาดพาวเวอร์อัพ
            if (powerups != null) {
                for (Powerup powerup : powerups) {
                    if (powerup != null) {
                        powerup.render(g);
                    }
                }
            }

            // วาดมอนสเตอร์
            if (monsters != null) {
                for (Monster monster : monsters) {
                    if (monster != null) {
                        monster.render(g);
                    }
                }
            }

            // วาดบอส
            if (boss != null && bossSpawned) {
                boss.render(g);
            }

            // วาดกระสุนของศัตรู
            if (enemyBullets != null) {
                for (EnemyBullet bullet : enemyBullets) {
                    if (bullet != null) {
                        bullet.render(g);
                    }
                }
            }

            // วาดผู้เล่น
            if (player != null) {
                player.render(g);
            }

            // วาด UI
            drawUI(g);
        } catch (Exception e) {
            System.err.println("Error in drawGame: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // วาดหน้าจอหยุดชั่วคราว
    private void drawPauseScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150)); // พื้นหลังโปร่งใส
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(Color.WHITE);
        String pauseText = "เกมหยุดชั่วคราว";
        int pauseX = (WIDTH - g.getFontMetrics().stringWidth(pauseText)) / 2;
        g.drawString(pauseText, pauseX, HEIGHT / 2);
        
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String resumeText = "กด ESC เพื่อเล่นต่อ";
        int resumeX = (WIDTH - g.getFontMetrics().stringWidth(resumeText)) / 2;
        g.drawString(resumeText, resumeX, HEIGHT / 2 + 50);
    }
    
    // วาดหน้าจอเกมจบ
    private void drawGameOverScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150)); // พื้นหลังโปร่งใส
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.setColor(Color.RED);
        String gameOverText = "เกมจบ";
        int gameOverX = (WIDTH - g.getFontMetrics().stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, gameOverX, HEIGHT / 2 - 50);
        
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        String scoreText = "คะแนน: " + score;
        int scoreX = (WIDTH - g.getFontMetrics().stringWidth(scoreText)) / 2;
        g.drawString(scoreText, scoreX, HEIGHT / 2);
        
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String restartText = "กด R เพื่อเริ่มใหม่";
        int restartX = (WIDTH - g.getFontMetrics().stringWidth(restartText)) / 2;
        g.drawString(restartText, restartX, HEIGHT / 2 + 50);
    }
        private void drawUI(Graphics2D g) {
        // แสดงคะแนน
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("คะแนน: " + score, 20, 30);
        
        // แสดงพลังชีวิต
        if (player != null) {
            g.drawString("HP: " + player.getHealth(), 20, 60);
            
            // แสดงแถบพลังชีวิต
            g.setColor(Color.RED);
            g.fillRect(120, 45, 100, 15);
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((float) player.getHealth() / player.getMaxHealth() * 100);
            g.fillRect(120, 45, healthBarWidth, 15);
            g.setColor(Color.WHITE);
            g.drawRect(120, 45, 100, 15);
        }
        
        // แสดงระดับ
        g.setColor(Color.WHITE);
        g.drawString("ระดับ: " + currentLevel, 20, 90);
        
        // แสดงจำนวนมอนสเตอร์ที่ฆ่าไป
        g.drawString("มอนสเตอร์: " + monstersKilled + "/" + monstersRequired, 20, 120);
        
        // แสดงจำนวนชีวิตที่เหลือ
        if (player != null) {
            g.drawString("ชีวิต: " + player.getLives(), WIDTH - 100, 30);
        }
    }
    
    // สร้างมอนสเตอร์
    private void spawnMonster() {
        try {
            // สุ่มตำแหน่งรอบขอบจอ
            int x, y;
            int side = random.nextInt(4); // 0=บน, 1=ขวา, 2=ล่าง, 3=ซ้าย
            
            switch (side) {
                case 0: // บน
                    x = random.nextInt(WIDTH - 30);
                    y = -30;
                    break;
                case 1: // ขวา
                    x = WIDTH;
                    y = random.nextInt(HEIGHT - 30);
                    break;
                case 2: // ล่าง
                    x = random.nextInt(WIDTH - 30);
                    y = HEIGHT;
                    break;
                default: // ซ้าย
                    x = -30;
                    y = random.nextInt(HEIGHT - 30);
                    break;
            }
            
            monsters.add(new Monster(x, y, player));
            System.out.println("สร้างมอนสเตอร์ที่ตำแหน่ง (" + x + ", " + y + ")");
        } catch (Exception e) {
            System.err.println("Error spawning monster: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // สร้างบอส
    private void spawnBoss() {
        try {
            boss = new Boss(WIDTH / 2 - 40, 50, currentLevel);
            bossSpawned = true;
            System.out.println("สร้างบอสระดับ " + currentLevel);
        } catch (Exception e) {
            System.err.println("Error spawning boss: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // อัพเดทมอนสเตอร์
    private void updateMonsters() {
        if (monsters == null) return;
        
        try {
            Iterator<Monster> it = monsters.iterator();
            while (it.hasNext()) {
                Monster monster = it.next();
                monster.update();
                
                // ลบมอนสเตอร์ที่ตายแล้ว
                if (!monster.isAlive()) {
                    it.remove();
                    monstersKilled++;
                    score += monster.getPoints();
                    
                    // สุ่มว่าจะดรอปพาวเวอร์อัพหรือไม่
                    if (monster.dropsPowerup()) {
                        spawnPowerup(monster.getX(), monster.getY());
                    }
                    continue;
                }
                
                // มอนสเตอร์ยิงกระสุน
                if (random.nextInt(100) < 2) { // 2% โอกาสต่อเฟรม
                    EnemyBullet bullet = monster.attack();
                    if (bullet != null) {
                        enemyBullets.add(bullet);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating monsters: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // อัพเดทบอส
    private void updateBoss() {
        if (boss == null || !bossSpawned) return;
        
        try {
            boss.update();
            
            // ตรวจสอบว่าบอสตายหรือยัง
            if (!boss.isAlive()) {
                bossSpawned = false;
                score += boss.getPoints();
                currentLevel++;
                monstersKilled = 0;
                monstersRequired = 20 + (currentLevel - 1) * 5; // เพิ่มจำนวนมอนสเตอร์ที่ต้องฆ่า
                spawnPowerup(boss.getX() + boss.getWidth() / 2, boss.getY() + boss.getHeight() / 2);
                boss = null;
                return;
            }
            
            // บอสยิงกระสุน
            if (random.nextInt(100) < 5) { // 5% โอกาสต่อเฟรม
                EnemyBullet bullet = boss.attack();
                if (bullet != null) {
                    enemyBullets.add(bullet);
                }
            }
            
            // บอสยิงกระสุนพิเศษ
            if (random.nextInt(200) < 1) { // 0.5% โอกาสต่อเฟรม
                List<EnemyBullet> bullets = boss.attackSpecial();
                if (bullets != null) {
                    enemyBullets.addAll(bullets);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating boss: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // สร้างพาวเวอร์อัพ
    private void spawnPowerup(float x, float y) {
        try {
            int type = random.nextInt(3); // สุ่มประเภทของพาวเวอร์อัพ
            powerups.add(new Powerup((int) x, (int) y, type));
            System.out.println("สร้างพาวเวอร์อัพประเภท " + type + " ที่ตำแหน่ง (" + x + ", " + y + ")");
        } catch (Exception e) {
            System.err.println("Error spawning powerup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // อัพเดทกระสุนของผู้เล่น
    private void updatePlayerBullets() {
        if (player == null) return;
        
        try {
            playerBullets = player.getBullets(); // ดึงรายการกระสุนจากผู้เล่น
            
            if (playerBullets == null) return;
            
            Iterator<PlayerBullet> it = playerBullets.iterator();
            while (it.hasNext()) {
                PlayerBullet bullet = it.next();
                bullet.update();
                
                // ลบกระสุนที่ไม่ได้ใช้งานแล้ว
                if (!bullet.isActive()) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating player bullets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // อัพเดทกระสุนของศัตรู
    private void updateEnemyBullets() {
        if (enemyBullets == null) return;
        
        try {
            Iterator<EnemyBullet> it = enemyBullets.iterator();
            while (it.hasNext()) {
                EnemyBullet bullet = it.next();
                bullet.update();
                
                // ลบกระสุนที่ไม่ได้ใช้งานแล้ว
                if (!bullet.isActive()) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating enemy bullets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // อัพเดทพาวเวอร์อัพ
    private void updatePowerups() {
        if (powerups == null) return;
        
        try {
            Iterator<Powerup> it = powerups.iterator();
            while (it.hasNext()) {
                Powerup powerup = it.next();
                powerup.update();
                
                // ลบพาวเวอร์อัพที่ไม่ได้ใช้งานแล้ว
                if (!powerup.isActive()) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating powerups: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ตรวจสอบการชนกัน
    private void checkCollisions() {
        if (player == null) return;
        
        try {
            // ตรวจสอบการชนกันระหว่างกระสุนผู้เล่นกับศัตรู
            checkPlayerBulletCollisions();
            
            // ตรวจสอบการชนกันระหว่างกระสุนศัตรูกับผู้เล่น
            checkEnemyBulletCollisions();
            
            // ตรวจสอบการชนกันระหว่างผู้เล่นกับมอนสเตอร์
            checkPlayerMonsterCollisions();
            
            // ตรวจสอบการชนกันระหว่างผู้เล่นกับบอส
            checkPlayerBossCollisions();
            
            // ตรวจสอบการชนกันระหว่างผู้เล่นกับพาวเวอร์อัพ
            checkPlayerPowerupCollisions();
        } catch (Exception e) {
            System.err.println("Error checking collisions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ตรวจสอบการชนกันระหว่างกระสุนผู้เล่นกับศัตรู
    private void checkPlayerBulletCollisions() {
        if (playerBullets == null) return;
        
        // ตรวจสอบการชนกับมอนสเตอร์
        if (monsters != null) {
            for (PlayerBullet bullet : playerBullets) {
                if (bullet != null && bullet.isActive()) {
                    for (Monster monster : monsters) {
                        if (monster != null && monster.isAlive() && bullet.collidesWith(monster)) {
                            monster.takeDamage(bullet.getDamage());
                            bullet.setActive(false);
                            break;
                        }
                    }
                }
            }
        }

        // ตรวจสอบการชนกับบอส
        if (boss != null && bossSpawned) {
            for (PlayerBullet bullet : playerBullets) {
                if (bullet != null && bullet.isActive() && boss.isAlive() && bullet.collidesWith(boss)) {
                    boss.takeDamage(bullet.getDamage());
                    bullet.setActive(false);
                }
            }
        }
    }
    
    // ตรวจสอบการชนกันระหว่างกระสุนศัตรูกับผู้เล่น
    private void checkEnemyBulletCollisions() {
        if (enemyBullets == null || player == null) return;
        
        for (EnemyBullet bullet : enemyBullets) {
            if (bullet != null && bullet.isActive() && player.isAlive() && bullet.collidesWith(player)) {
                player.takeDamage(bullet.getDamage());
                bullet.setActive(false);
            }
        }
    }

    // ตรวจสอบการชนกันระหว่างผู้เล่นกับมอนสเตอร์
    private void checkPlayerMonsterCollisions() {
        if (monsters == null || player == null) return;
        
        for (Monster monster : monsters) {
            if (monster != null && player.isAlive() && monster.isAlive() && player.collidesWith(monster)) {
                // ผู้เล่นและมอนสเตอร์ต่างได้รับความเสียหาย
                player.takeDamage(monster.getDamage());
                monster.takeDamage(20); // มอนสเตอร์ได้รับความเสียหายจากการชน
            }
        }
    }

    // ตรวจสอบการชนกันระหว่างผู้เล่นกับบอส
    private void checkPlayerBossCollisions() {
        if (boss == null || !bossSpawned || player == null) return;
        
        if (player.isAlive() && boss.isAlive() && player.collidesWith(boss)) {
            // ผู้เล่นและบอสต่างได้รับความเสียหาย
            player.takeDamage(boss.getDamage() * 2); // บอสทำความเสียหายมากกว่ามอนสเตอร์
            boss.takeDamage(10); // บอสได้รับความเสียหายจากการชนน้อยกว่า
        }
    }

    // ตรวจสอบการชนกันระหว่างผู้เล่นกับพาวเวอร์อัพ
    private void checkPlayerPowerupCollisions() {
        if (powerups == null || player == null) return;
        
        Iterator<Powerup> it = powerups.iterator();
        while (it.hasNext()) {
            Powerup powerup = it.next();
            if (powerup != null && powerup.isActive() && player.collidesWith(powerup)) {
                // ใช้พาวเวอร์อัพ
                applyPowerup(powerup);
                it.remove();
            }
        }
    }

    // ประยุกต์ใช้พาวเวอร์อัพกับผู้เล่น
    private void applyPowerup(Powerup powerup) {
        if (player == null) return;
        
        switch (powerup.getType()) {
            case 0: // เพิ่มพลังชีวิต
                player.setHealth(player.getHealth() + powerup.getValue());
                break;
            case 1: // เพิ่มความเร็ว
                player.increaseSpeed(powerup.getValue());
                break;
            case 2: // เพิ่มความแรงกระสุน
                player.increaseBulletDamage(powerup.getValue());
                break;
        }
    }
    
    // หยุดเกม
    public void stopGame() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // สำหรับเช็คการชนกันของ GameObject (เพิ่มเติม)
    public Rectangle getBounds() {
        return new Rectangle(0, 0, WIDTH, HEIGHT);
    }
}
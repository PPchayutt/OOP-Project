
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, GameState {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int FPS = 60;

    private Thread gameThread;
    private GameMap gameMap;
    private boolean running = false;

    private LevelManager levelManager;
    private final Random random = new Random();

    private boolean gameOver = false;
    private boolean gamePaused = false;
    private int monsterSpawnTimer = 0;
    private final InputHandler inputHandler;

    private Player player;
    private List<Monster> monsters;
    private List<Boss> bosses;
    private List<PlayerBullet> playerBullets;
    private List<EnemyBullet> enemyBullets;
    private List<Powerup> powerups;

    private final Game game;

    public GamePanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        initGame();

        inputHandler = new InputHandler(this);
        addKeyListener(inputHandler);
        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);
    }

    private void initGame() {
        player = new Player(WIDTH / 2 - 16, HEIGHT - 100, 32, 32, 100, 5);
        monsters = new ArrayList<>();
        bosses = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        powerups = new ArrayList<>();

        levelManager = new LevelManager();
        gameMap = new GameMap("level1"); // สร้างแผนที่ด่าน 1
    }

    public void startGameLoop() {
        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();

            // เริ่มเล่นเพลง Level 1
            SoundManager.playBackgroundMusic("level1_music");
        }
    }

    public void stopGameLoop() {
        running = false;

        // หยุดเพลงเมื่อออกจากเกม
        SoundManager.stopBackgroundMusic();

        try {
            if (gameThread != null) {
                gameThread.join(1000);
            }
        } catch (InterruptedException e) {
            System.err.println("เกิดข้อผิดพลาดขณะหยุด game thread: " + e.getMessage());
        }
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = FPS;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                if (!gamePaused && !gameOver) {
                    update();
                }
                delta--;
            }

            repaint();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.err.println("เกิดข้อผิดพลาดขณะ sleep thread: " + e.getMessage());
            }
        }
    }

    @Override
    public void update() {
        // อัปเดตผู้เล่น (รวมถึงบัฟด้วย)
        player.update();

        // ตรวจสอบว่าผู้เล่นยังมีชีวิตอยู่หรือไม่
        if (!player.isAlive()) {
            gameOver = true;
            return;
        }

        // ตรวจสอบว่ามีบัฟ Stop Time ทำงานอยู่หรือไม่
        boolean stopTimeActive = player.hasStopTimeBuff();

        // ตรวจสอบการยิงของผู้เล่น
        if (inputHandler.isLeftButisDown() && !gamePaused && !gameOver) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - player.getLastShotTime() >= player.getShootCooldown()) {
                playerShoot(inputHandler.getMouseX(), inputHandler.getMouseY());
            }
        }

        // ถ้าไม่มีการหยุดเวลา ให้อัปเดตมอนสเตอร์และบอสตามปกติ
        if (!stopTimeActive) {
            // สปอนมอนสเตอร์
            monsterSpawnTimer++;
            if (monsterSpawnTimer >= levelManager.getMonsterSpawnRate() && bosses.isEmpty()) {
                spawnMonster();
                monsterSpawnTimer = 0;
            }

            // สปอนบอสถ้าสังหารมอนสเตอร์ครบ
            if (levelManager.shouldSpawnBoss() && bosses.isEmpty()) {
                spawnBoss();
                levelManager.bossSpawned();
            }

            // อัปเดตมอนสเตอร์และบอส
            updateMonsters();
            updateBosses();
            updateEnemyBullets();
        }

        // อัปเดตอื่นๆ ที่ไม่เกี่ยวข้องกับเวลาที่หยุด
        updatePlayerBullets();
        updatePowerups();
        checkCollisions();
    }

    private void updateMonsters() {
        // จัดการการชนกันระหว่างมอนสเตอร์
        for (Monster monster : monsters) {
            monster.update();

            int oldX = (int) monster.getX();
            int oldY = (int) monster.getY();

            for (Monster otherMonster : monsters) {
                if (monster != otherMonster && monster.collidesWith(otherMonster)) {
                    monster.setX(oldX);
                    monster.setY(oldY);

                    // เลื่อนตำแหน่งเล็กน้อยเพื่อหลีกเลี่ยงการซ้อนทับ
                    monster.setX(oldX + random.nextInt(10) - 5);
                    monster.setY(oldY + random.nextInt(10) - 5);
                    break;
                }
            }
        }

        // ลบมอนสเตอร์ที่ตายแล้ว
        Iterator<Monster> it = monsters.iterator();
        while (it.hasNext()) {
            Monster monster = it.next();

            // ตรวจสอบว่ามอนสเตอร์มีชีวิตอยู่หรือไม่
            if (!monster.isAlive()) {
                it.remove();
                levelManager.monsterKilled();

                // เพิ่มคะแนน
                player.addScore(monster.getPoints());

                // สุ่มดรอปบัฟหรือไอเทม
                if (monster.dropsPowerup()) {
                    spawnPowerup((int) monster.getX(), (int) monster.getY());
                }
            }

            // ให้มอนสเตอร์โจมตีตามโอกาส
            if (random.nextInt(100) < 2) { // โอกาส 2%
                EnemyBullet bullet = monster.attack();
                if (bullet != null) {
                    enemyBullets.add(bullet);
                }
            }
        }
    }

    private void updateBosses() {
        Iterator<Boss> it = bosses.iterator();
        while (it.hasNext()) {
            Boss boss = it.next();
            boss.update();

            // ตรวจสอบว่าบอสมีชีวิตอยู่หรือไม่
            if (!boss.isAlive()) {
                it.remove();
                levelManager.bossKilled();

                // เพิ่มคะแนน
                player.addScore(boss.getPoints());

                // บอสจะดรอปบัฟเสมอ
                spawnPowerup((int) (boss.getX() + boss.getWidth() / 2), (int) (boss.getY() + boss.getHeight() / 2));
                continue;
            }

            // บอสโจมตีปกติ
            if (random.nextInt(100) < 5) { // โอกาส 5%
                EnemyBullet bullet = boss.attack();
                if (bullet != null) {
                    enemyBullets.add(bullet);
                }
            }

            // บอสโจมตีพิเศษ
            if (random.nextInt(100) < 1) { // โอกาส 1%
                List<EnemyBullet> bullets = boss.attackSpecial();
                if (bullets != null) {
                    enemyBullets.addAll(bullets);
                }
            }
        }
    }

    private void updatePlayerBullets() {
        // ล้างรายการกระสุนเก่าและเพิ่มกระสุนใหม่จากผู้เล่น
        playerBullets.clear();
        playerBullets.addAll(player.getBullets());

        // อัปเดตกระสุนและลบกระสุนที่ไม่ใช้งานแล้ว
        Iterator<PlayerBullet> it = playerBullets.iterator();
        while (it.hasNext()) {
            PlayerBullet bullet = it.next();
            bullet.update();

            if (!bullet.isActive()) {
                it.remove();
            }
        }
    }

    private void updateEnemyBullets() {
        Iterator<EnemyBullet> it = enemyBullets.iterator();
        while (it.hasNext()) {
            EnemyBullet bullet = it.next();
            bullet.update();

            if (!bullet.isActive()) {
                it.remove();
            }
        }
    }

    private void updatePowerups() {
        Iterator<Powerup> it = powerups.iterator();
        while (it.hasNext()) {
            Powerup powerup = it.next();
            powerup.update();

            if (!powerup.isActive()) {
                it.remove();
            }
        }
    }

    private void checkCollisions() {
        // ตรวจสอบการชนระหว่างกระสุนผู้เล่นกับมอนสเตอร์
        checkBulletMonsterCollisions();

        // ตรวจสอบการชนระหว่างกระสุนผู้เล่นกับบอส
        checkBulletBossCollisions();

        // ตรวจสอบการชนระหว่างกระสุนศัตรูกับผู้เล่น
        checkEnemyBulletPlayerCollisions();

        // ตรวจสอบการชนระหว่างมอนสเตอร์กับผู้เล่น
        checkMonsterPlayerCollisions();

        // ตรวจสอบการชนระหว่างบอสกับผู้เล่น
        checkBossPlayerCollisions();

        // ตรวจสอบการชนระหว่างผู้เล่นกับพาวเวอร์อัพ
        checkPowerupCollisions();
    }

    private void checkBulletMonsterCollisions() {
        for (PlayerBullet bullet : playerBullets) {
            for (Monster monster : monsters) {
                if (bullet.isActive() && monster.isAlive() && bullet.collidesWith(monster)) {
                    monster.takeDamage(bullet.getDamage());

                    // ถ้ากระสุนมี knockback ให้ผลักมอนสเตอร์
                    if (bullet.hasKnockback()) {
                        float knockbackStrength = bullet.getKnockbackPower() * 5.0f;
                        float knockbackX = (float) Math.cos(bullet.getAngle()) * knockbackStrength;
                        float knockbackY = (float) Math.sin(bullet.getAngle()) * knockbackStrength;

                        // ผลักมอนสเตอร์ไปตามทิศทาง
                        monster.setX(monster.getX() + knockbackX);
                        monster.setY(monster.getY() + knockbackY);

                        // ป้องกันไม่ให้ออกนอกจอ
                        monster.setX(Math.max(0, Math.min(monster.getX(), WIDTH - monster.getWidth())));
                        monster.setY(Math.max(0, Math.min(monster.getY(), HEIGHT - monster.getHeight())));
                    }

                    bullet.setActive(false);
                }
            }
        }
    }

    private void checkBulletBossCollisions() {
        for (PlayerBullet bullet : playerBullets) {
            for (Boss boss : bosses) {
                if (bullet.isActive() && boss.isAlive() && bullet.collidesWith(boss)) {
                    boss.takeDamage(bullet.getDamage());

                    // บอสโดน knockback น้อยกว่าเนื่องจากขนาดใหญ่
                    if (bullet.hasKnockback()) {
                        float knockbackStrength = bullet.getKnockbackPower() * 2.0f; // บอสถูกผลักน้อยกว่ามอนสเตอร์
                        float knockbackX = (float) Math.cos(bullet.getAngle()) * knockbackStrength;
                        float knockbackY = (float) Math.sin(bullet.getAngle()) * knockbackStrength;

                        boss.setX(boss.getX() + knockbackX);
                        boss.setY(boss.getY() + knockbackY);

                        // ป้องกันไม่ให้ออกนอกจอ
                        boss.setX(Math.max(0, Math.min(boss.getX(), WIDTH - boss.getWidth())));
                        boss.setY(Math.max(0, Math.min(boss.getY(), HEIGHT - boss.getHeight())));
                    }

                    bullet.setActive(false);
                }
            }
        }
    }

    private void checkEnemyBulletPlayerCollisions() {
        for (EnemyBullet bullet : enemyBullets) {
            if (bullet.isActive() && player.isAlive() && bullet.collidesWith(player)) {
                player.takeDamage(bullet.getDamage());
                bullet.setActive(false);
            }
        }
    }

    private void checkMonsterPlayerCollisions() {
        for (Monster monster : monsters) {
            if (player.isAlive() && monster.isAlive() && player.collidesWith(monster)) {
                player.takeDamage(monster.getDamage());
                monster.takeDamage(50);
            }
        }
    }

    private void checkBossPlayerCollisions() {
        for (Boss boss : bosses) {
            if (player.isAlive() && boss.isAlive() && player.collidesWith(boss)) {
                player.takeDamage(boss.getDamage(), true);
                boss.takeDamage(20);
            }
        }
    }

    private void checkPowerupCollisions() {
        Iterator<Powerup> it = powerups.iterator();
        while (it.hasNext()) {
            Powerup powerup = it.next();
            if (powerup.isActive() && player.collidesWith(powerup)) {
                player.addBuff(powerup);
                it.remove();
            }
        }
    }

    private void spawnMonster() {
        int[] pos = levelManager.getRandomMonsterPosition();
        monsters.add(new Monster(pos[0], pos[1], player));
    }

    private void spawnBoss() {
        int[] pos = levelManager.getBossPosition();
        bosses.add(new Boss(pos[0], pos[1], levelManager.getCurrentLevel()));
    }

    private void spawnPowerup(int x, int y) {
        Powerup powerup = Powerup.createRandomPowerup(x, y);
        if (powerup != null) {
            powerups.add(powerup);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    @Override
    public void render(Graphics g) {
        // วาดพื้นหลังและแผนที่
        drawBackground(g);
        gameMap.render(g);

        if (!gameOver) {
            // วาดพาวเวอร์อัพที่ดรอปอยู่
            for (Powerup powerup : powerups) {
                powerup.render(g);
            }

            // วาดกระสุนของผู้เล่น
            for (PlayerBullet bullet : playerBullets) {
                bullet.render(g);
            }

            // วาดกระสุนของศัตรู
            for (EnemyBullet bullet : enemyBullets) {
                bullet.render(g);
            }

            // วาดมอนสเตอร์
            for (Monster monster : monsters) {
                monster.render(g);
            }

            // วาดบอส
            for (Boss boss : bosses) {
                boss.render(g);
            }

            // วาดผู้เล่น
            player.render(g);
        }

        // วาด UI
        drawUI(g);

        // วาดหน้าจอ Game Over หรือ Pause ถ้าจำเป็น
        if (gameOver) {
            drawGameOver(g);
        }

        if (gamePaused) {
            drawPaused(g);
        }
    }

    private void drawBackground(Graphics g) {
        // ดึงภาพพื้นหลังด่าน 1 มาใช้
        Image bgImage = ImageManager.getImage("level1_bg");

        if (bgImage != null) {
            // วาดภาพพื้นหลัง
            g.drawImage(bgImage, 0, 0, WIDTH, HEIGHT, null);
        } else {
            // ถ้าไม่มีภาพพื้นหลัง ใช้การวาดดาวแบบเดิม
            g.setColor(new Color(100, 100, 100));
            for (int i = 0; i < 100; i++) {
                int starX = random.nextInt(WIDTH);
                int starY = random.nextInt(HEIGHT);
                g.fillRect(starX, starY, 1, 1);
            }

            g.setColor(new Color(200, 200, 200));
            for (int i = 0; i < 50; i++) {
                int starX = random.nextInt(WIDTH);
                int starY = random.nextInt(HEIGHT);
                g.fillRect(starX, starY, 2, 2);
            }
        }
    }

    private void drawUI(Graphics g) {
        // แสดงค่าพลังชีวิตปัจจุบัน
        g.setColor(Color.WHITE);
        g.drawString("HP: " + player.getHealth() + "/100", 20, 30);

        // แสดงแถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect(80, 20, player.getHealth() * 2, 15);
        g.setColor(Color.WHITE);
        g.drawRect(80, 20, 200, 15);

        // แสดงจำนวนชีวิตด้วยไอคอนหัวใจ
        int totalLives = 3; // จำนวนชีวิตทั้งหมด
        int remainingLives = player.getLives(); // จำนวนชีวิตที่เหลือ

        Image heartImage = ImageManager.getImage("heart");
        Image brokenHeartImage = ImageManager.getImage("broken_heart");

        // วาดหัวใจปกติสำหรับชีวิตที่เหลืออยู่
        for (int i = 0; i < remainingLives; i++) {
            g.drawImage(heartImage, 20 + (i * 25), 45, 20, 20, null);
        }

        // วาดหัวใจแตกสำหรับชีวิตที่เสียไปแล้ว
        for (int i = remainingLives; i < totalLives; i++) {
            g.drawImage(brokenHeartImage, 20 + (i * 25), 45, 20, 20, null);
        }

        // แสดงข้อมูลเลเวลและคะแนน
        g.setColor(Color.WHITE);
        g.drawString("Level: " + levelManager.getCurrentLevel(), 20, 90);
        g.drawString("Monsters: " + levelManager.getMonstersKilled() + "/" + levelManager.getMonstersToKill(), 20, 110);
        g.drawString("Score: " + player.getScore(), WIDTH - 150, 30);

        // แสดงบัฟที่กำลังใช้งาน
        drawActiveBuffs(g);
    }

    // เพิ่มเมธอดวาดบัฟที่ใช้งานอยู่
    private void drawActiveBuffs(Graphics g) {
        List<Powerup> activeBuffs = player.getActiveBuffs();
        int x = 300; // ตำแหน่งเริ่มต้น
        int y = 20;  // ด้านบนของจอ
        int spacing = 40; // ระยะห่างระหว่างไอคอน

        // วาดพื้นหลังสำหรับพื้นที่แสดงบัฟ
        if (!activeBuffs.isEmpty()) {
            g.setColor(new Color(0, 0, 0, 150)); // สีดำโปร่งใส
            int bgWidth = activeBuffs.size() * spacing + 10;
            g.fillRect(x - 5, y - 5, bgWidth, 40);
            g.setColor(Color.WHITE);
            g.drawRect(x - 5, y - 5, bgWidth, 40);
        }

        for (Powerup buff : activeBuffs) {
            // วาดไอคอน
            g.drawImage(buff.getIcon(), x, y, 30, 30, null);

            // ถ้าเป็นบัฟที่มีระยะเวลา ให้แสดงเวลาที่เหลือ
            if (buff.getDuration() > 0) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                int seconds = buff.getDuration() / 60 + 1; // แปลงเฟรมเป็นวินาที
                g.drawString(seconds + "s", x + 10, y + 45);
            }

            x += spacing; // เลื่อนไปทางขวา
        }
    }

    private void drawPaused(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("PAUSED", WIDTH / 2 - 100, HEIGHT / 2);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Press 'P' to continue", WIDTH / 2 - 80, HEIGHT / 2 + 40);
        g.drawString("Press 'ESC' to return to menu", WIDTH / 2 - 110, HEIGHT / 2 + 70);
    }

    public void movePlayer(int dx, int dy) {
        if (!gameOver && !gamePaused) {
            // เก็บตำแหน่งเดิม
            float oldX = player.getX();
            float oldY = player.getY();

            // ให้ Player เคลื่อนที่
            player.move(dx, dy);

            // ตรวจสอบการชนกับบล็อค
            if (gameMap.checkCollision(player)) {
                // ถ้าชนให้กลับไปตำแหน่งเดิม
                player.setX(oldX);
                player.setY(oldY);
            }
        }
    }

    public void playerShoot(int targetX, int targetY) {
        if (!gameOver && !gamePaused) {
            player.shoot(targetX, targetY);
            playerBullets.addAll(player.getBullets());
        }
    }

    public void togglePause() {
        gamePaused = !gamePaused;
    }

    public void restartGame() {
        initGame();
        gameOver = false;
        gamePaused = false;

        // เริ่มเล่นเพลงใหม่
        SoundManager.playBackgroundMusic("level1_music");
    }

    public void returnToMenu() {
        game.returnToMenu();
    }

    // แสดงหน้าจอ Game Over และปุ่มต่างๆ
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + player.getScore(), WIDTH / 2 - 50, HEIGHT / 2);
        g.drawString("Level: " + levelManager.getCurrentLevel(), WIDTH / 2 - 50, HEIGHT / 2 + 30);

        // วาดปุ่ม "เล่นใหม่"
        g.setColor(new Color(50, 150, 50));
        g.fillRect(WIDTH / 2 - 100, HEIGHT / 2 + 40, 200, 40);
        g.setColor(Color.WHITE);
        g.drawString("เล่นใหม่", WIDTH / 2 - 30, HEIGHT / 2 + 65);

        // วาดปุ่ม "กลับเมนูหลัก"
        g.setColor(new Color(150, 50, 50));
        g.fillRect(WIDTH / 2 - 100, HEIGHT / 2 + 90, 200, 40);
        g.setColor(Color.WHITE);
        g.drawString("กลับเมนูหลัก", WIDTH / 2 - 50, HEIGHT / 2 + 115);
    }

    // จัดการการคลิกปุ่มในหน้า Game Over
    private void handleGameOverButtons(int x, int y) {
        // สร้างพื้นที่ปุ่ม "กลับเมนูหลัก"
        Rectangle menuButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 90, 200, 40);

        // สร้างพื้นที่ปุ่ม "เล่นใหม่"
        Rectangle restartButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 40, 200, 40);

        if (menuButton.contains(x, y)) {
            returnToMenu();
        } else if (restartButton.contains(x, y)) {
            restartGame();
        }
    }

    // รับการคลิกเมาส์
    @Override
    public void handleMouseClick(int x, int y) {
        if (gameOver) {
            handleGameOverButtons(x, y);
        } else if (gamePaused) {
            // อาจเพิ่มปุ่มในหน้า Pause ในอนาคต
        } else {
            // คลิกปกติในเกม
        }
    }

    // รับการกดคีย์บอร์ด
    public void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_P) {
            togglePause();
        } else if (keyCode == KeyEvent.VK_R && gameOver) {
            restartGame();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            returnToMenu();
        }
    }
}

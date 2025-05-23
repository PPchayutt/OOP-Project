
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.awt.geom.AffineTransform;

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
    private List<Enemy> monsters;
    private List<Boss> bosses;
    private List<PlayerBullet> playerBullets;
    private List<EnemyBullet> enemyBullets;
    private List<Powerup> powerups;
    private List<MenuButton> pauseButtons;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    private final Game game;

    private int gameOverEffectTimer = 0;
    private float gameOverPulseValue = 0.0f;
    private boolean gameOverPulseDirection = true;

    private boolean gameWon = false;
    private int gameWonEffectTimer = 0;
    private float gameWonPulseValue = 0.0f;
    private boolean gameWonPulseDirection = true;
    private int finalScore = 0;

    private static int selectedWeaponIndex = -1;
    private static WeaponManager weaponManager;
    private final HotbarUI hotbarUI;
    
    private final GameUIManager uiManager;

    public GamePanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        weaponManager = new WeaponManager();

        initGame();
        initPauseMenu();
        
        uiManager = new GameUIManager(this, player, levelManager, 0);
        hotbarUI = new HotbarUI(player);
        inputHandler = new InputHandler(this);
        addKeyListener(inputHandler);
        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        repaint();
    }

    private void drawUIWithScaling(Graphics g) {
        // ปรับฟอนต์ตามสเกล
        g.setColor(Color.WHITE);
        Font originalFont = g.getFont();
        Font scaledFont = originalFont.deriveFont(originalFont.getSize() * scaleX);
        g.setFont(scaledFont);

        // แสดงค่าพลังชีวิตปัจจุบัน
        g.drawString("HP: " + player.getHealth() + "/100", (int) (20 * scaleX), (int) (30 * scaleY));

        // แสดงแถบพลังชีวิต
        g.setColor(Color.RED);
        g.fillRect((int) (95 * scaleX), (int) (18 * scaleY),
                (int) (player.getHealth() * 2 * scaleX), (int) (15 * scaleY));
        g.setColor(Color.WHITE);
        g.drawRect((int) (95 * scaleX), (int) (18 * scaleY),
                (int) (200 * scaleX), (int) (15 * scaleY));

        // แสดงจำนวนชีวิตด้วยไอคอนหัวใจ
        int totalLives = 3;
        int remainingLives = player.getLives();

        Image heartImage = ImageManager.getImage("heart");
        Image brokenHeartImage = ImageManager.getImage("broken_heart");

        // ปรับขนาดและตำแหน่งของหัวใจตาม scaling
        int heartSize = (int) (20 * scaleX);
        int heartSpacing = (int) (25 * scaleX);
        int heartY = (int) (45 * scaleY);

        for (int i = 0; i < remainingLives; i++) {
            g.drawImage(heartImage, (int) (20 * scaleX) + (i * heartSpacing),
                    heartY, heartSize, heartSize, null);
        }

        for (int i = remainingLives; i < totalLives; i++) {
            g.drawImage(brokenHeartImage, (int) (20 * scaleX) + (i * heartSpacing),
                    heartY, heartSize, heartSize, null);
        }

        // แสดงข้อมูลเลเวลและคะแนน
        g.setColor(Color.WHITE);
        g.drawString("Level: " + levelManager.getCurrentLevel(), (int) (20 * scaleX), (int) (90 * scaleY));
        g.drawString("Monsters: " + levelManager.getMonstersKilled() + "/" + levelManager.getMonstersToKill(),
                (int) (20 * scaleX), (int) (110 * scaleY));
        // แสดงพื้นหลังสำหรับข้อความ Score
        String scoreText = "Score: " + player.getScore();
        int textWidth = g.getFontMetrics().stringWidth(scoreText);
        int textHeight = g.getFontMetrics().getHeight();

        // วาดพื้นหลังสีทึบรองรับข้อความ Score
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect((int) ((WIDTH - 160) * scaleX), (int) (15 * scaleY),
                (int) ((textWidth + 20) * scaleX), (int) ((textHeight + 5) * scaleY));

        // วาดข้อความ Score ทับพื้นหลัง
        g.setColor(Color.WHITE);
        g.drawString(scoreText, (int) ((WIDTH - 150) * scaleX), (int) (30 * scaleY));

        // แสดงบัฟที่กำลังใช้งาน
        drawActiveBuffsWithScaling(g);
    }

    // เพิ่มเมธอดสำหรับวาดบัฟแบบมี scaling
    private void drawActiveBuffsWithScaling(Graphics g) {
        List<Powerup> activeBuffs = player.getActiveBuffs();

        if (activeBuffs.isEmpty()) {
            return;
        }

        int x = (int) (300 * scaleX);  // คงตำแหน่งเดิม
        int y = (int) (20 * scaleY);   // คงตำแหน่งเดิม
        int spacing = (int) (40 * scaleX); // ระยะห่างระหว่างไอคอน

        // วาดพื้นหลังสำหรับพื้นที่แสดงบัฟ
        g.setColor(new Color(0, 0, 0, 150));
        int bgWidth = (int) ((activeBuffs.size() * 40 + 10) * scaleX);
        g.fillRect(x - (int) (5 * scaleX), y - (int) (5 * scaleY), bgWidth, (int) (40 * scaleY));
        g.setColor(Color.WHITE);
        g.drawRect(x - (int) (5 * scaleX), y - (int) (5 * scaleY), bgWidth, (int) (40 * scaleY));

        for (Powerup buff : activeBuffs) {
            // วาดไอคอน
            int iconSize = (int) (30 * scaleX);
            g.drawImage(buff.getIcon(), x, y, iconSize, iconSize, null);

            // ถ้าเป็นบัฟที่มีระยะเวลา ให้แสดงเวลาที่เหลือ
            if (buff.getDuration() > 0) {
                int barWidth = iconSize;
                int barHeight = (int) (5 * scaleY);

                // วาดแถบพื้นหลังสีเทาเข้ม
                g.setColor(new Color(60, 60, 60));
                g.fillRect(x, y + iconSize + 2, barWidth, barHeight);

                int maxDuration = 300; //  5 วินาที (CRAZY)
                if (buff.getCategory() == Powerup.CATEGORY_TEMPORARY) {
                    maxDuration = 600; // 10 วินาที (TEMPORARY)
                }

                // คำนวณความยาวแถบเทียบกับเวลาเต็ม
                int fillWidth = (int) ((float) buff.getDuration() / maxDuration * barWidth);

                // วาดแถบเวลา
                if (buff.getCategory() == Powerup.CATEGORY_CRAZY) {
                    g.setColor(new Color(255, 50, 50));
                } else {
                    g.setColor(new Color(255, 170, 50));
                }
                g.fillRect(x, y + iconSize + 2, fillWidth, barHeight);

                // วาดขอบแถบเวลา
                g.setColor(Color.WHITE);
                g.drawRect(x, y + iconSize + 2, barWidth, barHeight);
            } // ถ้าเป็นบัฟถาวร ให้ตรวจสอบว่ามีจำนวนซ้ำหรือไม่
            else if (buff.getCategory() == Powerup.CATEGORY_PERMANENT) {
                int count = player.getPermanentBuffCount(buff);
                if (count > 1) {
                    // วาดวงกลมสีน้ำเงินเข้มตรงมุมบนขวาของไอคอน
                    g.setColor(new Color(0, 0, 150, 200));
                    int badgeSize = (int) (15 * scaleX);
                    g.fillOval(x + iconSize - badgeSize, y, badgeSize, badgeSize);

                    // วาดตัวเลขสีขาวตรงกลางวงกลม
                    g.setColor(Color.WHITE);
                    Font originalFont = g.getFont();
                    Font scaledFont = originalFont.deriveFont(Font.BOLD, originalFont.getSize() * 0.7f * scaleX);
                    g.setFont(scaledFont);

                    String countText = Integer.toString(count);
                    FontMetrics fm = g.getFontMetrics();
                    int textX = x + iconSize - badgeSize / 2 - fm.stringWidth(countText) / 2;
                    int textY = y + badgeSize / 2 + fm.getAscent() / 2 - 1;

                    g.drawString(countText, textX, textY);

                    g.setFont(originalFont);
                }
            }
            x += spacing; // เลื่อนไปทางขวา
        }
    }

    private void initGame() {
        player = new Player(WIDTH / 2 - 16, HEIGHT - 100, 32, 32, 100, 5);
        monsters = new ArrayList<>();
        bosses = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        powerups = new ArrayList<>();

        player.setWeaponManager(weaponManager);
        weaponManager.addWeapon(WeaponType.SHURIKEN);
        weaponManager.addWeapon(WeaponType.SHOTGUN);
        weaponManager.addWeapon(WeaponType.GATLING_GUN);

        levelManager = new LevelManager();
        gameMap = new GameMap("level1"); // สร้างแผนที่ด่าน 1
    }

    public void startGameLoop() {
        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();

            // เริ่มเล่นเพลง Level 1
            if (!SoundManager.isMusicMuted()) {
                SoundManager.playBackgroundMusic("level1_music");
            }
        }
    }

    public void stopGameLoop() {
        running = false;

        // หยุดเพลงเมื่อออกจากเกม
        SoundManager.stopBackgroundMusic();

        // หยุดเสียงเอฟเฟคทั้งหมดที่อาจกำลังเล่นอยู่
        SoundManager.stopAllEffects();

        try {
            if (gameThread != null) {
                gameThread.join(1000);
            }
        } catch (InterruptedException e) {
            System.err.println("เกิดข้อผิดพลาดขณะหยุด game thread: " + e.getMessage());
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
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

        if (System.currentTimeMillis() % 30000 < 100) {
            System.gc();
        }

        // จำกัดจำนวนกระสุนและเอฟเฟ็กต์
        if (enemyBullets.size() > 200) {
            // ลบกระสุนเก่าเกิน 200 ลูกออกไป
            while (enemyBullets.size() > 150) {
                enemyBullets.remove(0);
            }
        }
        
        uiManager.updateEffects();
        
        // อัพเดทสถานะของ uiManager ตามสถานะเกมปัจจุบัน
        if (gameOver) {
            uiManager.setState(GameUIManager.GameState.GAME_OVER);
            System.out.println("Gameover");
            return; // ออกจากเมธอดเพราะเกมจบแล้ว
        } else if (gameWon) {
            uiManager.setState(GameUIManager.GameState.GAME_WON);
            // อัพเดท finalScore ใน GameWonScreen
            uiManager.updateFinalScore(finalScore);
            System.out.println("Gamewon");
            return; // ออกจากเมธอดเพราะเกมจบแล้ว
        } else if (gamePaused) {
            uiManager.setState(GameUIManager.GameState.PAUSED);
            //System.out.println("Gamepause");
            return; // ออกจากเมธอดเพราะเกมพักอยู่
        } else if (levelManager.isTransitioning()) {
            uiManager.setState(GameUIManager.GameState.LEVEL_TRANSITION);
        } else {
            uiManager.setState(GameUIManager.GameState.PLAYING);
        }

        // เพิ่มการตรวจสอบการเปลี่ยนแผนที่
        if (levelManager.needsMapChange()) {
            clearCurrentLevelResources(); // เรียกเมธอดใหม่เพื่อเคลียร์ทรัพยากร

            try {
                System.out.println("กำลังเปลี่ยนแผนที่เป็นด่าน " + levelManager.getCurrentLevel());

                // สร้างแผนที่ใหม่
                String newMapName = "level" + levelManager.getCurrentLevel();
                gameMap = new GameMap(newMapName);
                System.out.println("สร้างแผนที่ " + newMapName + " สำเร็จ");

                // เปลี่ยนเพลง
                SwingUtilities.invokeLater(() -> {
                    if (!SoundManager.isMusicMuted()) {
                        SoundManager.stopBackgroundMusic(); // หยุดเพลงเก่าก่อน
                        SoundManager.playBackgroundMusic("level" + levelManager.getCurrentLevel() + "_music");
                    }
                });
            } catch (Exception e) {
                System.err.println("เกิดข้อผิดพลาดในการเปลี่ยนแผนที่: " + e.getMessage());
            }
        }

        // อัพเดตผู้เล่น (รวมถึงบัฟด้วย)
        player.update();

        // อัพเดท transition ระหว่างด่าน
        levelManager.updateTransition();

        // ตรวจสอบว่ากำลังอยู่ใน transition หรือไม่
        if (levelManager.isTransitioning()) {
            // ถ้าอยู่ใน transition ให้ล้างมอนเตอร์และกระสุนทั้งหมด
            monsters.clear();
            enemyBullets.clear();
            return; // ข้ามการอัพเดทอื่นๆ
        }

        // ตรวจสอบว่าเพิ่งเปลี่ยนเลเวลหรือไม่
        if (levelManager.isLevelJustChanged()) {
            prepareNextLevel();
            return; // ออกจากการอัพเดทรอบนี้เลย
        }

        // ตรวจสอบว่าผู้เล่นยังมีชีวิตอยู่หรือไม่
        if (!player.isAlive()) {
            if (!gameOver) { // เพิ่มเงื่อนไขให้ทำงานเพียงครั้งเดียวเมื่อเพิ่งตาย
                uiManager.setState(GameUIManager.GameState.GAME_OVER);
                gameOver = true;
                // หยุดเพลงพื้นหลังเมื่อเกมจบ
                SoundManager.stopBackgroundMusic();
            }
            return;
        }

        // ถ้าเกมหยุดชั่วคราว ให้ไม่ต้องอัพเดทสถานะเกม
        if (gamePaused) {
            return;
        }

        if (levelManager.isLevelReadyToPlay()) {
            System.out.println("เริ่มเล่นด่าน " + levelManager.getCurrentLevel() + " แล้ว!");
            // รีเซ็ตสถานะควบคุมตัวละคร
            if (inputHandler != null) {
                // รีเซ็ตสถานะการกดปุ่มที่อาจค้างอยู่
                inputHandler.resetAllInputs();
            }
            player.setVelX(0);
            player.setVelY(0);

            // บังคับให้เริ่มสปอนมอนสเตอร์ทันที
            monsterSpawnTimer = levelManager.getMonsterSpawnRate();
            player.setX(WIDTH / 2 - player.getWidth() / 2);
            player.setY(HEIGHT - 100);

            requestFocusInWindow();
            return;
        }
        if (!gamePaused && !levelManager.isTransitioning()) {
            inputHandler.handleShooting();
        }
        // ตรวจสอบว่ามีบัฟ Stop Time ทำงานอยู่หรือไม่
        boolean stopTimeActive = player.hasStopTimeBuff();

        if (!stopTimeActive) {
            // สปอนมอนสเตอร์
            monsterSpawnTimer++;
            if (monsterSpawnTimer >= levelManager.getMonsterSpawnRate() && monsters.size() < 10 && bosses.isEmpty()) {
                // สปอนมอนสเตอร์เฉพาะเมื่อไม่มีบอส
                spawnMonster();
                monsterSpawnTimer = 0;
            }
            if (monsterSpawnTimer >= levelManager.getMonsterSpawnRate() && monsters.size() < 10 && bosses.isEmpty()) {
                // สปอนมอนสเตอร์เฉพาะเมื่อไม่มีบอส
                spawnMonster();
                monsterSpawnTimer = 0;
            }
            // สปอนบอสถ้าสังหารมอนสเตอร์ครบ
            if (levelManager.shouldSpawnBoss() && bosses.isEmpty()) {
                // ล้างมอนสเตอร์ทั้งหมดก่อนสปอนบอส
                monsters.clear();
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
        weaponManager.update(player, monsters, bosses);
        hotbarUI.updateSlots();
    }

    private void clearCurrentLevelResources() {
        monsters.clear();
        bosses.clear();
        enemyBullets.clear();
        playerBullets.clear();
        powerups.clear();  // เพิ่มเคลียร์พาวเวอร์อัพ

        System.gc();
        monsterSpawnTimer = 0;
    }

    // เมธอดย่อยสำหรับอัพเดทมอนสเตอร์
    private void updateMonsters() {
        for (Enemy enemy : monsters) {
            enemy.update();
            // เพิ่มการหลบหลีกระหว่างมอนสเตอร์
            if (enemy instanceof Monster monster) {
                monster.avoidOtherMonsters(monsters);
            }

            // ตรวจสอบการชนเฉพาะมอนสเตอร์ที่อยู่ใกล้กัน
            for (Enemy otherEnemy : monsters) {
                if (enemy != otherEnemy) {
                    float dx = enemy.getX() - otherEnemy.getX();
                    float dy = enemy.getY() - otherEnemy.getY();
                    float distanceSquared = dx * dx + dy * dy;

                    if (distanceSquared < 2500 && enemy.collidesWith(otherEnemy)) {
                        enemy.setX(enemy.getX() + (dx > 0 ? 5 : -5));
                        enemy.setY(enemy.getY() + (dy > 0 ? 5 : -5));

                        // ป้องกันไม่ให้ออกนอกจอ
                        enemy.setX(Math.max(0, Math.min(enemy.getX(), WIDTH - enemy.getWidth())));
                        enemy.setY(Math.max(0, Math.min(enemy.getY(), HEIGHT - enemy.getHeight())));
                        break;
                    }
                }
            }
        }

        // ลบมอนสเตอร์ที่ตายแล้ว
        Iterator<Enemy> it = monsters.iterator();
        while (it.hasNext()) {
            Enemy enemy = it.next();

            // ตรวจสอบว่ามอนสเตอร์มีชีวิตอยู่หรือไม่
            if (!enemy.isAlive()) {
                it.remove();
                levelManager.monsterKilled();

                // เพิ่มคะแนน
                player.addScore(enemy.getPoints());

                // สุ่มดรอปบัฟหรือไอเทม
                if (enemy.dropsPowerup()) {
                    spawnPowerup((int) enemy.getX(), (int) enemy.getY());
                }
            }

            // ให้มอนสเตอร์โจมตีตามโอกาส
            if (random.nextInt(100) < 2) { // โอกาส 2%
                EnemyBullet bullet = enemy.attack();
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

                dropWeapon();

                // เพิ่มเงื่อนไขตรวจสอบว่าเป็นบอสตัวสุดท้ายหรือไม่
                if (boss instanceof Boss5) {
                    finalScore = player.getScore();
                    gameWon = true;

                    SoundManager.stopBackgroundMusic();
                    return;  // ออกจากเมธอดทันที
                }

                // ดรอปบัฟหลายชิ้นตามระดับของบอส
                int dropCount = Math.min(7, boss.getLevel() * 2 + 1); // เพิ่มจำนวนบัฟ
                for (int i = 0; i < dropCount; i++) {
                    int offsetX = random.nextInt(boss.getWidth()) - boss.getWidth() / 2;
                    int offsetY = random.nextInt(boss.getHeight()) - boss.getHeight() / 2;
                    spawnPowerup((int) (boss.getX() + boss.getWidth() / 2 + offsetX),
                            (int) (boss.getY() + boss.getHeight() / 2 + offsetY));
                }
                continue;
            }

            // บอสโจมตีปกติถี่ขึ้น
            if (random.nextInt(100) < 8) {
                EnemyBullet bullet = boss.attack();
                if (bullet != null) {
                    enemyBullets.add(bullet);
                }
            }

            // บอสโจมตีพิเศษถี่ขึ้น
            if (random.nextInt(100) < 2) {
                List<EnemyBullet> bullets = boss.attackSpecial();
                if (bullets != null) {
                    enemyBullets.addAll(bullets);
                }
            }

            // เพิ่มเงื่อนไขการโจมตีพิเศษสำหรับบอสด่าน 5
            if (boss instanceof Boss5 finalBoss) {

                // เรียกใช้การโจมตีสุดท้ายเมื่อสุ่มได้
                if (finalBoss.isInPhase2() && !finalBoss.isTransforming() && random.nextInt(600) == 0) {
                    List<EnemyBullet> ultimateAttack = finalBoss.executeUltimateAttack();
                    if (ultimateAttack != null && !ultimateAttack.isEmpty()) {
                        enemyBullets.addAll(ultimateAttack);
                    }
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

    private void prepareNextLevel() {
        try {
            // เคลียร์ศัตรูและกระสุนทั้งหมด
            monsters.clear();
            bosses.clear();
            enemyBullets.clear();
            playerBullets.clear();
            powerups.clear(); // เพิ่มเคลียร์พาวเวอร์อัพด้วย

            System.gc();

            // รีเซ็ตตำแหน่งผู้เล่นไปตรงกลางจอ
            player.setX(WIDTH / 2 - player.getWidth() / 2);
            player.setY(HEIGHT - 100);

            // รีเซ็ตความเร็วผู้เล่น
            player.setVelX(0);
            player.setVelY(0);

            // รีเซ็ตตัวนับเวลาการเกิดมอนสเตอร์
            monsterSpawnTimer = 0;

            loadNextLevelResourcesAsync();

            System.out.println("เปลี่ยนไปด่าน " + levelManager.getCurrentLevel());
            repaint();
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการเปลี่ยนด่าน: " + e.getMessage());
        }
    }

    private void loadNextLevelResourcesAsync() {
        new Thread(() -> {
            try {
                // สร้างแผนที่ใหม่ตามด่าน
                if (levelManager.getCurrentLevel() >= 2 && levelManager.getCurrentLevel() <= 5) {
                    final String mapName = "level" + levelManager.getCurrentLevel();

                    SwingUtilities.invokeLater(() -> {
                        gameMap = new GameMap(mapName);

                        // เปลี่ยนเพลง
                        if (!SoundManager.isMusicMuted()) {
                            SoundManager.playBackgroundMusic("level" + levelManager.getCurrentLevel() + "_music");
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("เกิดข้อผิดพลาดในการโหลดด่านใหม่: " + e.getMessage());
            }
        }).start();
    }

    private void checkBulletMonsterCollisions() {
        for (PlayerBullet bullet : playerBullets) {
            for (Enemy enemy : monsters) {
                if (bullet.isActive() && enemy.isAlive() && bullet.collidesWith(enemy)) {
                    enemy.takeDamage(bullet.getDamage());

                    // ถ้ากระสุนมี knockback ให้ผลักมอนสเตอร์
                    if (bullet.hasKnockback()) {
                        float knockbackStrength = bullet.getKnockbackPower() * 5.0f;
                        float knockbackX = (float) Math.cos(bullet.getAngle()) * knockbackStrength;
                        float knockbackY = (float) Math.sin(bullet.getAngle()) * knockbackStrength;

                        // ผลักมอนสเตอร์ไปตามทิศทาง
                        enemy.setX(enemy.getX() + knockbackX);
                        enemy.setY(enemy.getY() + knockbackY);

                        // ป้องกันไม่ให้ออกนอกจอ
                        enemy.setX(Math.max(0, Math.min(enemy.getX(), WIDTH - enemy.getWidth())));
                        enemy.setY(Math.max(0, Math.min(enemy.getY(), HEIGHT - enemy.getHeight())));
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
        for (Enemy enemy : monsters) {
            if (player.isAlive() && enemy.isAlive() && player.collidesWith(enemy)) {
                player.takeDamage(enemy.getDamage());
                enemy.takeDamage(50);
            }
        }
    }

    private void checkBossPlayerCollisions() {
        for (Boss boss : bosses) {
            if (player.isAlive() && boss.isAlive() && player.collidesWith(boss)) {
                int baseDamage = boss.getDamage();

                // คำนวณความเสียหายโดยคำนึงถึงเลือดที่เหลือ
                int actualDamage = baseDamage;
                if (player.getHealth() <= 10) {
                    // ลดความเสียหายลงถ้าเลือดเหลือน้อย
                    actualDamage = Math.max(1, baseDamage / 3);
                }

                player.takeDamage(actualDamage, true);
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
        try {
            // จำกัดจำนวนมอนสเตอร์เพื่อไม่ให้กระตุก
            if (monsters.size() >= 8) {
                return;
            }

            int[] pos = levelManager.getRandomMonsterPosition();
            Monster monster = (Monster) levelManager.spawnMonsterForLevel(pos, player);
            if (monster != null) {
                monsters.add(monster);
            }
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการสปอนมอนสเตอร์: " + e.getMessage());
        }
    }

    private void spawnBoss() {
        int[] pos = levelManager.getBossPosition();
        bosses.add((Boss) levelManager.spawnBossForLevel(pos));
    }

    private void spawnPowerup(int x, int y) {
        // จำกัดจำนวนบัฟในเกม
        if (powerups.size() >= 20) {
            powerups.remove(0); // ลบบัฟเก่าออก
        }

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
        Graphics2D g2d = (Graphics2D) g;

        // ล้างพื้นหลังให้เป็นสีดำเสมอก่อน เพื่อแก้ปัญหาภาพค้าง
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        AffineTransform oldTransform = g2d.getTransform();

        float safeScaleX = Math.min(scaleX, 1.2f);
        float safeScaleY = Math.min(scaleY, 1.2f);
        g2d.scale(safeScaleX, safeScaleY);

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
            for (Enemy enemy : monsters) {
                enemy.render(g);
            }

            // วาดบอส
            for (Boss boss : bosses) {
                boss.render(g);
            }
            // วาดผู้เล่น
            player.render(g);
        }

        g2d.setTransform(oldTransform);

        // วาด UI
        drawUIWithScaling(g);
        
        uiManager.render(g2d, scaleX, scaleY);
        weaponManager.render(g);
        hotbarUI.render(g);
    }

    private void drawBackground(Graphics g) {
        try {
            String bgKey = "level" + levelManager.getCurrentLevel() + "_bg";
            Image bgImage = ImageManager.getImage(bgKey);

            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, WIDTH, HEIGHT, null);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการวาดพื้นหลัง: " + e.getMessage());
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }
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
        if (!gameOver && !gamePaused && !levelManager.isTransitioning()) {
            System.out.println("กำลังยิงในเกม - พิกัด: " + targetX + ", " + targetY);

            // แปลงพิกัดเมาส์ให้อยู่ในระบบพิกัดของเกม
            int scaledX = (int) (targetX / scaleX);
            int scaledY = (int) (targetY / scaleY);

            // ส่งพิกัดที่แปลงแล้วไปให้ player
            player.shoot(scaledX, scaledY);
            playerBullets.addAll(player.getBullets());
        }
    }

    public void togglePause() {
        uiManager.setState(GameUIManager.GameState.PAUSED);
        gamePaused = !gamePaused;
        // ถ้าต้องการหยุดเสียงเกมเมื่อพัก
        if (gamePaused) {
            // บันทึกสถานะเสียงปัจจุบัน
        } else {
            // คืนค่าสถานะเสียงที่บันทึกไว้
        }
    }

    public void restartGame() {
        initGame();
        gameOver = false;
        gamePaused = false;
        gameWon = false;
        weaponManager.clearWeapon();

        // เริ่มเล่นเพลงใหม่
        SoundManager.playBackgroundMusic("level1_music");
    }

    public void returnToMenu() {
        System.out.println("menu");
        gameWon = false;
        game.returnToMenu();
    }

    private void handleGameOverButtons(int x, int y) {
        int scaledX = (int) (x / scaleX);
        int scaledY = (int) (y / scaleY);

        Rectangle restartButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 90, 200, 40);

        Rectangle menuButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 140, 200, 40);

        if (restartButton.contains(scaledX, scaledY)) {
            // เริ่มเกมใหม่
            restartGame();
        } else if (menuButton.contains(scaledX, scaledY)) {
            // กลับไปเมนูหลัก
            returnToMenu();
        }
    }

    @Override
    public void handleMouseClick(int x, int y) {
        int scaledX = (int) (x / scaleX);
        int scaledY = (int) (y / scaleY);

        if (gameOver) {
            handleGameOverButtons(scaledX, scaledY);
        } else if (gameWon) {
            Rectangle menuButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 140, 200, 40);

            if (menuButton.contains(scaledX, scaledY)) {
                returnToMenu();
            }
        } else if (gamePaused) {
            // ปุ่ม Resume
            Rectangle resumeButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 20, 200, 40);
            if (resumeButton.contains(scaledX, scaledY)) {
                togglePause();
                return;
            }

            // ปุ่ม Restart Game
            Rectangle restartButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 70, 200, 40);
            if (restartButton.contains(scaledX, scaledY)) {
                restartGame();
                return;
            }

            // ปุ่ม Main Menu
            Rectangle menuButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 120, 200, 40);
            if (menuButton.contains(scaledX, scaledY)) {
                returnToMenu();
            }
        }
    }

    private void initPauseMenu() {
        pauseButtons = new ArrayList<>();

        // สร้างปุ่ม Resume
        pauseButtons.add(new PauseButton(WIDTH / 2 - 100, HEIGHT / 2 - 30, 200, 40, "Resume",
                new Color(50, 150, 50), Color.WHITE, () -> togglePause()));

        // สร้างปุ่ม Restart Game
        pauseButtons.add(new PauseButton(WIDTH / 2 - 100, HEIGHT / 2 + 20, 200, 40, "Restart Game",
                new Color(70, 130, 180), Color.WHITE, () -> restartGame()));

        // สร้างปุ่ม Main Menu
        pauseButtons.add(new PauseButton(WIDTH / 2 - 100, HEIGHT / 2 + 70, 200, 40, "Main Menu",
                new Color(150, 50, 50), Color.WHITE, () -> returnToMenu()));

    }
    
    public void dropWeapon() {
        // ถ้าชนะบอสด่านแรกจะได้ปืน tier 2
        if (levelManager.getCurrentLevel() == 2) {
            int randomIndex = random.nextInt(weaponManager.tier2Weapon.size());
            weaponManager.addWeapon(weaponManager.tier2Weapon.get(randomIndex));
        } else {
            // ด่านหลังจากนั้นให้ดรอปปืน tier 3
            int randomIndex = random.nextInt(weaponManager.tier3Weapon.size());
            weaponManager.addWeapon(weaponManager.tier3Weapon.get(randomIndex));
        }
    }

    public void placeTurret() {
        // หาตำแหน่งปัจจุบันผู้เล่น
        int playerX = (int) player.getX();
        int playerY = (int) player.getY();
        weaponManager.deployWeapon(WeaponType.TURRET, playerX, playerY);
    }

    public void selectWeapon(int index) {
        Weapon selectedWeapon = null;
        try {
            selectedWeapon = weaponManager.getWeaponByIndex(index);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("ช่องที่เลือกเป็นช่องว่าง");
            return;
        }
        System.out.println(selectedWeapon);
        WeaponType selectedType = selectedWeapon.getType();
        System.out.println(selectedType);
        if (selectedType != null && !gameOver && !gamePaused) {
            if (weaponManager.hasWeapon(selectedType)) {
                if (selectedType == WeaponType.TURRET) {
                    placeTurret();
                } else {
                    weaponManager.activateWeapon(selectedType);
                }
            }
        }
    }

    public static WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public void setSelectedWeaponIndex(int index) {
        selectedWeaponIndex = index;
    }

    public static int getSelectedWeaponIndex() {
        return selectedWeaponIndex;
    }
    
    public GameUIManager getUIManager() {
        return uiManager;
    }

}

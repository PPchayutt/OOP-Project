
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

        InputHandler inputHandler = new InputHandler(this);
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
        }
    }

    public void stopGameLoop() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join(1000); // รอสูงสุด 1 วินาที
            }
        } catch (InterruptedException e) {
        }
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
            }
        }
    }

    @Override
    public void update() {
        player.update();

        if (!player.isAlive()) {
            gameOver = true;
            return;
        }

        monsterSpawnTimer++;
        if (monsterSpawnTimer >= levelManager.getMonsterSpawnRate() && bosses.isEmpty()) {
            spawnMonster();
            monsterSpawnTimer = 0;
        }

        if (levelManager.shouldSpawnBoss() && bosses.isEmpty()) {
            spawnBoss();
            levelManager.bossSpawned();
        }

        updateMonsters();
        updateBosses();
        updatePlayerBullets();
        updateEnemyBullets();
        updatePowerups();
        checkCollisions();
    }

    private void updateMonsters() {
        for (Monster monster : monsters) {
            monster.update();

            int oldX = (int) monster.getX();
            int oldY = (int) monster.getY();

            for (Monster otherMonster : monsters) {
                if (monster != otherMonster && monster.collidesWith(otherMonster)) {
                    monster.setX(oldX);
                    monster.setY(oldY);

                    monster.setX(oldX + random.nextInt(10) - 5);
                    monster.setY(oldY + random.nextInt(10) - 5);
                    break;
                }
            }
        }

        Iterator<Monster> it = monsters.iterator();
        while (it.hasNext()) {
            Monster monster = it.next();
            monster.update();

            if (!monster.isAlive()) {
                it.remove();
                levelManager.monsterKilled();

                player.addScore(monster.getPoints());

                if (monster.dropsPowerup()) {
                    spawnPowerup((int) monster.getX(), (int) monster.getY());
                }
            }
        }
    }

    private void updateBosses() {
        Iterator<Boss> it = bosses.iterator();
        while (it.hasNext()) {
            Boss boss = it.next();
            boss.update();

            if (!boss.isAlive()) {
                it.remove();
                levelManager.bossKilled();

                player.addScore(boss.getPoints());

                spawnPowerup((int) (boss.getX() + boss.getWidth() / 2), (int) (boss.getY() + boss.getHeight() / 2));
                continue;
            }

            if (random.nextInt(100) < 5) {
                EnemyBullet bullet = boss.attack();
                if (bullet != null) {
                    enemyBullets.add(bullet);
                }
            }

            if (random.nextInt(100) < 1) {
                List<EnemyBullet> bullets = boss.attackSpecial();
                if (bullets != null) {
                    enemyBullets.addAll(bullets);
                }
            }
        }
    }

    private void updatePlayerBullets() {
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
        for (PlayerBullet bullet : playerBullets) {
            for (Monster monster : monsters) {
                if (bullet.isActive() && monster.isAlive() && bullet.collidesWith(monster)) {
                    monster.takeDamage(bullet.getDamage());
                    bullet.setActive(false);
                }
            }

            for (Boss boss : bosses) {
                if (bullet.isActive() && boss.isAlive() && bullet.collidesWith(boss)) {
                    boss.takeDamage(bullet.getDamage());
                    bullet.setActive(false);
                }
            }
        }

        for (EnemyBullet bullet : enemyBullets) {
            if (bullet.isActive() && player.isAlive() && bullet.collidesWith(player)) {
                player.takeDamage(bullet.getDamage());
                bullet.setActive(false);
            }
        }

        for (Monster monster : monsters) {
            if (player.isAlive() && monster.isAlive() && player.collidesWith(monster)) {
                player.takeDamage(monster.getDamage());
                monster.takeDamage(50);
            }
        }

        for (Boss boss : bosses) {
            if (player.isAlive() && boss.isAlive() && player.collidesWith(boss)) {
                player.takeDamage(boss.getDamage(), true);
                boss.takeDamage(20);
            }
        }

        Iterator<Powerup> it = powerups.iterator();
        while (it.hasNext()) {
            Powerup powerup = it.next();
            if (powerup.isActive() && player.collidesWith(powerup)) {
                applyPowerup(powerup);
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
        int type = random.nextInt(3);
        powerups.add(new Powerup(x, y, type));
    }

    private void applyPowerup(Powerup powerup) {
        switch (powerup.getType()) {
            case 0 -> {
                int newHealth = player.getHealth() + powerup.getValue();
                player.setHealth(Math.min(newHealth, 100));
            }
            case 1 ->
                player.setSpeed(player.getSpeed() + powerup.getValue());
            case 2 -> {
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    @Override
    public void render(Graphics g) {
        drawBackground(g);
        gameMap.render(g);
        if (!gameOver) {
            for (Powerup powerup : powerups) {
                powerup.render(g);
            }

            for (PlayerBullet bullet : playerBullets) {
                bullet.render(g);
            }

            for (EnemyBullet bullet : enemyBullets) {
                bullet.render(g);
            }

            for (Monster monster : monsters) {
                monster.render(g);
            }

            for (Boss boss : bosses) {
                boss.render(g);
            }

            player.render(g);
        }

        drawUI(g);

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
        g.setColor(Color.WHITE);
        g.drawString("HP: " + player.getHealth() + "/100", 20, 30);

        g.setColor(Color.RED);
        g.fillRect(80, 20, player.getHealth() * 2, 15);

        g.setColor(Color.WHITE);
        g.drawRect(80, 20, 200, 15);

        g.setColor(Color.WHITE);
        g.drawString("Lives: " + player.getLives(), 20, 50);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + levelManager.getCurrentLevel(), 20, 70);

        g.setColor(Color.WHITE);
        g.drawString("Monsters: " + levelManager.getMonstersKilled() + "/" + levelManager.getMonstersToKill(), 20, 90);

        g.setColor(Color.WHITE);
        g.drawString("Score: " + player.getScore(), WIDTH - 150, 30);
    }

    private void drawPaused(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("PAUSED", WIDTH / 2 - 100, HEIGHT / 2);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Press 'P' to continue", WIDTH / 2 - 80, HEIGHT / 2 + 40);
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
            int bulletX = (int) (player.getX() + player.getWidth() / 2 - 4);
            int bulletY = (int) (player.getY() - 8);

            double dx = targetX - bulletX;
            double dy = targetY - bulletY;
            double angle = Math.atan2(dy, dx);

            playerBullets.add(new PlayerBullet(bulletX, bulletY, 8, 8, angle));
        }
    }

    public void togglePause() {
        gamePaused = !gamePaused;
    }

    public void restartGame() {
        initGame();
        gameOver = false;
        gamePaused = false;
    }

    public void returnToMenu() {
        game.returnToMenu();
    }

    // เพิ่มเมธอดสำหรับการจัดการกับปุ่มในหน้า Game Over
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

    // ปรับปรุงเมธอด drawGameOver เพื่อแสดงปุ่ม
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

    // ปรับปรุงเมธอด handleMouseClick ให้ตรวจสอบการคลิกปุ่มในหน้า Game Over
    @Override
    public void handleMouseClick(int x, int y) {
        if (gameOver) {
            handleGameOverButtons(x, y);
        } else if (gamePaused) {
            // ตรวจสอบการคลิกปุ่มในหน้า Pause (ถ้ามี)
        } else {
            // ตรวจสอบการคลิกในเกม (ถ้ามี)
        }
    }

    // เพิ่มเมธอดสำหรับการตอบสนองต่อปุ่มคีย์บอร์ด
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


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    // Game states
    public static int GAME_WIDTH = 1024;
    public static int GAME_HEIGHT = 768;

    private enum GameState {
        MENU, PLAYING, PAUSED, GAMEOVER
    }
    private GameState currentState = GameState.MENU;

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
    private int enemyKillRequiredForNextBoss = 20;
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
    }

    private void setupInput() {
        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (currentState) {
                    case MENU -> {
                        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                            startGame();
                            currentState = GameState.PLAYING;
                        }
                    }
                    case PLAYING -> {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_W ->
                                player.setPressingUp(true);
                            case KeyEvent.VK_S ->
                                player.setPressingDown(true);
                            case KeyEvent.VK_A ->
                                player.setPressingLeft(true);
                            case KeyEvent.VK_D ->
                                player.setPressingRight(true);
                            case KeyEvent.VK_ESCAPE ->
                                currentState = GameState.PAUSED;
                        }
                    }
                    case PAUSED -> {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            currentState = GameState.PLAYING;
                        }
                    }
                    case GAMEOVER -> {
                        if (e.getKeyCode() == KeyEvent.VK_R) {
                            resetGame();
                            currentState = GameState.PLAYING;
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (currentState == GameState.PLAYING) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W ->
                            player.setPressingUp(false);
                        case KeyEvent.VK_S ->
                            player.setPressingDown(false);
                        case KeyEvent.VK_A ->
                            player.setPressingLeft(false);
                        case KeyEvent.VK_D ->
                            player.setPressingRight(false);
                    }
                }
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState == GameState.PLAYING) {
                    player.shoot(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentState == GameState.PLAYING) {
                    player.shoot(e.getX(), e.getY());
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void spawnEnemy() {
        int side = (int) (Math.random() * 4);
        float x = 0, y = 0;

        switch (side) {
            case 0 -> {
                // top
                x = (float) (Math.random() * getGameWidth());
                y = -20;
            }
            case 1 -> {
                // right
                x = getGameWidth() + 20;
                y = (float) (Math.random() * getGameHeight());
            }
            case 2 -> {
                // bottom
                x = (float) (Math.random() * getGameWidth());
                y = getGameHeight() + 20;
            }
            case 3 -> {
                // left
                x = -20;
                y = (float) (Math.random() * getGameHeight());
            }
        }

        enemies.add(new Enemy(x, y, player, bossDifficulty));
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();

        // Check enemy collisions with player
        for (Enemy enemy : enemies) {
            if (enemy.getBounds().intersects(playerBounds)) {
                enemy.attackPlayer();
                if (player.isDead()) {
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
            for (int i = bullets.size() - 1; i >= 0; i--) {
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
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            Rectangle bulletBounds = bullet.getBounds();

            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (enemy.getBounds().intersects(bulletBounds)) {
                    enemy.damage(25);
                    bullet.deactivate();
                    if (enemy.isDead()) {
                        enemies.remove(j);
                        score += 100;
                        currentEnemyKilled++;

                        if (currentEnemyKilled % 10 == 0) {
                            bossDifficulty++;
                            enemySpawnRate += 0.005f;
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
        bossDifficulty = 1;
        currentEnemyKilled = 0;
        enemyKillRequiredForNextBoss = 20;
        enemySpawnRate = 0.02f;
        player = new Player(getGameWidth() / 2 - 12, getGameHeight() / 2 - 12, this);
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
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
        }
    }

    public void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setupGame() {
        player = new Player(GAME_WIDTH / 2 - 12, GAME_HEIGHT / 2 - 12, this);
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        boss = null;
        isBossSpawned = false;
        score = 0;
        bossDifficulty = 1;
        currentEnemyKilled = 0;
        enemyKillRequiredForNextBoss = 20;
        enemySpawnRate = 0.02f;
        currentState = GameState.MENU;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                if (currentState == GameState.PLAYING) {
                    update();
                }
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        player.update();

        if (!isBossSpawned && currentEnemyKilled >= enemyKillRequiredForNextBoss) {
            spawnBoss();
            currentEnemyKilled = 0;
        }

        if (!isBossSpawned) {
            if (Math.random() < enemySpawnRate) {
                spawnEnemy();
            }
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update();

            if (enemy.getX() < -200 || enemy.getX() > getGameWidth() + 200
                    || enemy.getY() < -200 || enemy.getY() > getGameHeight() + 200) {
                enemies.remove(i);
            }
        }

        if (isBossSpawned && boss != null) {
            boss.update();

            if (boss.isDead()) {
                boss = null;
                isBossSpawned = false;
                score += 500 * bossDifficulty;
            }
        }

        spawnPowerUp();
        checkPowerUpCollisions();
        checkCollisions();
    }

    private void spawnBoss() {
        boss = new Boss(getGameWidth() / 2 - 25, getGameHeight() / 2 - 25, player, bossDifficulty);
        isBossSpawned = true;
        enemies.clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (currentState) {
            case MENU ->
                drawMenu(g2d);
            case PLAYING ->
                drawGame(g2d);
            case PAUSED -> {
                drawGame(g2d);
                drawPauseScreen(g2d);
            }
            case GAMEOVER -> {
                drawGame(g2d);
                drawGameOverScreen(g2d);
            }
        }
    }

    private void drawMenu(Graphics2D g2d) {
        // Background
        g2d.setColor(new Color(0, 0, 30));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        g2d.setColor(Color.WHITE);
        String title = "Bird Eye View Shooter";
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
        for (String text : controls) {
            fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            g2d.drawString(text, x, y);
            y += 30;
        }
    }

    private void spawnPowerUp() {
        if (Math.random() < 0.005) {
            int margin = 50;
            float x = margin + (float) (Math.random() * (getGameWidth() - 2 * margin));
            float y = margin + (float) (Math.random() * (getGameHeight() - 2 * margin));
            PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
            PowerUp.PowerUpType randomType = types[(int) (Math.random() * types.length)];
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
        ArrayList<Enemy> enemiesCopy = new ArrayList<>(enemies);
        ArrayList<PowerUp> powerUpsCopy = new ArrayList<>(powerUps);

        player.render(g2d);
        for (Enemy enemy : enemiesCopy) {
            enemy.render(g2d);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("HP: " + player.getHealth(), 20, 60);

        g2d.drawString("Difficulty: " + bossDifficulty, 20, 90);

        long currentTime = System.currentTimeMillis();
        long timeSinceLastShot = currentTime - player.getLastShotTime();
        float reloadProgress = Math.min(1.0f, timeSinceLastShot / (float) player.getShootCooldown());

        int reloadBarWidth = 100;
        int reloadBarHeight = 5;
        int reloadBarX = 20;
        int reloadBarY = 110;

        g2d.setColor(Color.GRAY);
        g2d.fillRect(reloadBarX, reloadBarY, reloadBarWidth, reloadBarHeight);

        g2d.setColor(Color.YELLOW);
        g2d.fillRect(reloadBarX, reloadBarY, (int) (reloadBarWidth * reloadProgress), reloadBarHeight);

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
        g2d.drawString(gameOver, x, getHeight() / 2 - 50);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreText = "Score: " + score;
        String restartText = "Press R to Restart";
        fm = g2d.getFontMetrics();
        x = (getWidth() - fm.stringWidth(scoreText)) / 2;
        g2d.drawString(scoreText, x, getHeight() / 2);
        x = (getWidth() - fm.stringWidth(restartText)) / 2;
        g2d.drawString(restartText, x, getHeight() / 2 + 50);
    }
}

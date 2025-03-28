
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
    private List<Monster> monsters;
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

    public GamePanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        initGame();
        initPauseMenu();

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
        g.fillRect((int) (80 * scaleX), (int) (20 * scaleY),
                (int) (player.getHealth() * 2 * scaleX), (int) (15 * scaleY));
        g.setColor(Color.WHITE);
        g.drawRect((int) (80 * scaleX), (int) (20 * scaleY),
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
        g.drawString("Score: " + player.getScore(), (int) ((WIDTH - 150) * scaleX), (int) (30 * scaleY));

        // แสดงบัฟที่กำลังใช้งาน
        drawActiveBuffsWithScaling(g);
    }

    private void drawGameOverWithScaling(Graphics g) {
        // สร้าง Graphics2D เพื่อใช้เอฟเฟกต์ขั้นสูง
        Graphics2D g2d = (Graphics2D) g;

        // เพิ่มการรองรับ Anti-aliasing เพื่อทำให้ตัวอักษรสวยขึ้น
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // เพิ่มเอฟเฟกต์การเบลอพื้นหลัง
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, (int) (WIDTH * scaleX), (int) (HEIGHT * scaleY));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // วาดกรอบหน้า Game Over พร้อมไล่เฉดสี
        GradientPaint gradient = new GradientPaint(
                (int) ((WIDTH / 2 - 200) * scaleX), (int) ((HEIGHT / 2 - 150) * scaleY), new Color(60, 20, 20),
                (int) ((WIDTH / 2 + 200) * scaleX), (int) ((HEIGHT / 2 + 150) * scaleY), new Color(150, 20, 20)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(
                (int) ((WIDTH / 2 - 200) * scaleX),
                (int) ((HEIGHT / 2 - 150) * scaleY),
                (int) (400 * scaleX),
                (int) (300 * scaleY),
                (int) (20 * scaleX),
                (int) (20 * scaleY)
        );

        // วาดขอบกรอบหนา
        g2d.setStroke(new BasicStroke(3 * scaleX));
        g2d.setColor(new Color(200, 100, 100));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 200) * scaleX),
                (int) ((HEIGHT / 2 - 150) * scaleY),
                (int) (400 * scaleX),
                (int) (300 * scaleY),
                (int) (20 * scaleX),
                (int) (20 * scaleY)
        );

        // เพิ่มเงาให้ข้อความ GAME OVER
        Font gameOverFont = new Font("Arial", Font.BOLD, (int) (50 * scaleX));
        g2d.setFont(gameOverFont);
        g2d.setColor(new Color(20, 0, 0));

        // คำนวณตำแหน่งเพื่อให้ข้อความ GAME OVER อยู่ตรงกลาง
        String gameOverText = "GAME OVER";
        FontMetrics gameOverMetrics = g2d.getFontMetrics(gameOverFont);
        int gameOverWidth = gameOverMetrics.stringWidth(gameOverText);
        int gameOverX = (int) ((WIDTH / 2) * scaleX - gameOverWidth / 2);

        // วาดเงา GAME OVER
        g2d.drawString(gameOverText, gameOverX + (int) (3 * scaleX), (int) ((HEIGHT / 2 - 70) * scaleY) + (int) (3 * scaleX));

        // วาดข้อความ GAME OVER หลัก
        g2d.setColor(new Color(255, 50, 50));
        g2d.drawString(gameOverText, gameOverX, (int) ((HEIGHT / 2 - 73) * scaleY));

        // วาดสถิติผู้เล่น
        Font statsFont = new Font("Arial", Font.BOLD, (int) (24 * scaleX));
        g2d.setFont(statsFont);
        g2d.setColor(Color.WHITE);

        // ตั้งค่าและวาดข้อความสถิติ โดยจัดให้อยู่ในแนวเดียวกัน
        int statsX = (int) ((WIDTH / 2 - 60) * scaleX);
        g2d.drawString("Score: " + player.getScore(), statsX, (int) ((HEIGHT / 2 - 15) * scaleY));
        g2d.drawString("Level: " + levelManager.getCurrentLevel(), statsX, (int) ((HEIGHT / 2 + 20) * scaleY));
        g2d.drawString("Kills: " + levelManager.getMonstersKilled(), statsX, (int) ((HEIGHT / 2 + 55) * scaleY));

        // วาดปุ่ม "เล่นใหม่" พร้อมไล่เฉดสี
        GradientPaint restartGradient = new GradientPaint(
                (int) ((WIDTH / 2 - 100) * scaleX), (int) ((HEIGHT / 2 + 90) * scaleY), new Color(20, 100, 20),
                (int) ((WIDTH / 2 + 100) * scaleX), (int) ((HEIGHT / 2 + 130) * scaleY), new Color(50, 150, 50)
        );
        g2d.setPaint(restartGradient);
        g2d.fillRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 90) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // วาดขอบปุ่ม "เล่นใหม่"
        g2d.setStroke(new BasicStroke(2 * scaleX));
        g2d.setColor(new Color(100, 200, 100));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 90) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // ข้อความสำหรับปุ่ม "เล่นใหม่"
        Font buttonFont = new Font("Arial", Font.BOLD, (int) (20 * scaleX));
        g2d.setFont(buttonFont);
        g2d.setColor(Color.WHITE);

        // คำนวณตำแหน่งเพื่อให้ข้อความอยู่ตรงกลางปุ่ม
        String restartText = "Restart Game";
        FontMetrics metrics = g2d.getFontMetrics(buttonFont);
        int textWidth = metrics.stringWidth(restartText);
        int buttonCenterX = (int) ((WIDTH / 2) * scaleX);
        int textX = buttonCenterX - textWidth / 2;

        // ปรับความสูงให้อยู่ตรงกลางปุ่มตามแนวดิ่ง
        int buttonCenterY = (int) ((HEIGHT / 2 + 90 + 20) * scaleY); // กึ่งกลางความสูงของปุ่ม
        int textHeight = metrics.getHeight();
        int textY = buttonCenterY + (textHeight / 4); // ปรับให้อยู่กึ่งกลางตามแนวดิ่ง

        g2d.drawString(restartText, textX, textY);

        // วาดปุ่ม "กลับเมนูหลัก" พร้อมไล่เฉดสี
        GradientPaint menuGradient = new GradientPaint(
                (int) ((WIDTH / 2 - 100) * scaleX), (int) ((HEIGHT / 2 + 140) * scaleY), new Color(100, 20, 20),
                (int) ((WIDTH / 2 + 100) * scaleX), (int) ((HEIGHT / 2 + 180) * scaleY), new Color(150, 50, 50)
        );
        g2d.setPaint(menuGradient);
        g2d.fillRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 140) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // วาดขอบปุ่ม "กลับเมนูหลัก"
        g2d.setColor(new Color(200, 100, 100));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 140) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // ข้อความสำหรับปุ่ม "กลับเมนูหลัก"
        g2d.setColor(Color.WHITE);
        String menuText = "Main Menu";
        textWidth = metrics.stringWidth(menuText);
        textX = buttonCenterX - textWidth / 2;

        // ปรับความสูงสำหรับปุ่มเมนู
        buttonCenterY = (int) ((HEIGHT / 2 + 140 + 20) * scaleY);
        textY = buttonCenterY + (textHeight / 4);

        g2d.drawString(menuText, textX, textY);
    }

    private void drawPausedWithScaling(Graphics g) {
        // สร้าง Graphics2D เพื่อใช้เอฟเฟกต์ขั้นสูง
        Graphics2D g2d = (Graphics2D) g;

        // เพิ่มการรองรับ Anti-aliasing เพื่อทำให้ตัวอักษรสวยขึ้น
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // เพิ่มเอฟเฟกต์การเบลอพื้นหลัง
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, (int) (WIDTH * scaleX), (int) (HEIGHT * scaleY));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // วาดกรอบหน้า Paused พร้อมไล่เฉดสี
        GradientPaint gradient = new GradientPaint(
                (int) ((WIDTH / 2 - 200) * scaleX), (int) ((HEIGHT / 2 - 150) * scaleY), new Color(60, 20, 20),
                (int) ((WIDTH / 2 + 200) * scaleX), (int) ((HEIGHT / 2 + 150) * scaleY), new Color(150, 20, 20)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(
                (int) ((WIDTH / 2 - 200) * scaleX),
                (int) ((HEIGHT / 2 - 150) * scaleY),
                (int) (400 * scaleX),
                (int) (300 * scaleY),
                (int) (20 * scaleX),
                (int) (20 * scaleY)
        );

        // วาดขอบกรอบหนา
        g2d.setStroke(new BasicStroke(3 * scaleX));
        g2d.setColor(new Color(200, 100, 100));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 200) * scaleX),
                (int) ((HEIGHT / 2 - 150) * scaleY),
                (int) (400 * scaleX),
                (int) (300 * scaleY),
                (int) (20 * scaleX),
                (int) (20 * scaleY)
        );

        // เพิ่มเงาให้ข้อความ PAUSED
        Font pausedFont = new Font("Arial", Font.BOLD, (int) (50 * scaleX));
        g2d.setFont(pausedFont);
        g2d.setColor(new Color(20, 0, 0));

        // คำนวณตำแหน่งเพื่อให้ข้อความ PAUSED อยู่ตรงกลาง
        String pausedText = "PAUSED";
        FontMetrics pausedMetrics = g2d.getFontMetrics(pausedFont);
        int pausedWidth = pausedMetrics.stringWidth(pausedText);
        int pausedX = (int) ((WIDTH / 2) * scaleX - pausedWidth / 2);

        // วาดเงา PAUSED
        g2d.drawString(pausedText, pausedX + (int) (3 * scaleX), (int) ((HEIGHT / 2 - 70) * scaleY) + (int) (3 * scaleX));

        // วาดข้อความ PAUSED หลัก
        g2d.setColor(new Color(255, 50, 50));
        g2d.drawString(pausedText, pausedX, (int) ((HEIGHT / 2 - 73) * scaleY));

        // วาดปุ่ม "Resume" พร้อมไล่เฉดสี
        GradientPaint resumeGradient = new GradientPaint(
                (int) ((WIDTH / 2 - 100) * scaleX), (int) ((HEIGHT / 2 + 20) * scaleY), new Color(20, 100, 20),
                (int) ((WIDTH / 2 + 100) * scaleX), (int) ((HEIGHT / 2 + 60) * scaleY), new Color(50, 150, 50)
        );
        g2d.setPaint(resumeGradient);
        g2d.fillRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 20) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // วาดขอบปุ่ม "Resume"
        g2d.setStroke(new BasicStroke(2 * scaleX));
        g2d.setColor(new Color(100, 200, 100));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 20) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // ข้อความสำหรับปุ่ม "Resume"
        Font buttonFont = new Font("Arial", Font.BOLD, (int) (20 * scaleX));
        g2d.setFont(buttonFont);
        g2d.setColor(Color.WHITE);  // ตั้งค่าสีเป็นขาว

// คำนวณตำแหน่งเพื่อให้ข้อความอยู่ตรงกลางปุ่ม
        String resumeText = "Resume";
        FontMetrics metrics = g2d.getFontMetrics(buttonFont);
        int textWidth = metrics.stringWidth(resumeText);
        int buttonCenterX = (int) ((WIDTH / 2) * scaleX);
        int textX = buttonCenterX - textWidth / 2;

// ปรับความสูงให้อยู่ตรงกลางปุ่มตามแนวดิ่ง
        int buttonCenterY = (int) ((HEIGHT / 2 + 20 + 20) * scaleY); // กึ่งกลางความสูงของปุ่ม
        int textHeight = metrics.getHeight();
        int textY = buttonCenterY + (textHeight / 4); // ปรับให้อยู่กึ่งกลางตามแนวดิ่ง

        g2d.drawString(resumeText, textX, textY);

// วาดปุ่ม "Restart Game" พร้อมไล่เฉดสี
        GradientPaint restartGradient = new GradientPaint(
                (int) ((WIDTH / 2 - 100) * scaleX), (int) ((HEIGHT / 2 + 70) * scaleY), new Color(20, 90, 140),
                (int) ((WIDTH / 2 + 100) * scaleX), (int) ((HEIGHT / 2 + 110) * scaleY), new Color(70, 130, 180)
        );
        g2d.setPaint(restartGradient);
        g2d.fillRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 70) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

// วาดขอบปุ่ม "Restart Game"
        g2d.setColor(new Color(100, 150, 200));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 70) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

// ข้อความสำหรับปุ่ม "Restart Game"
        g2d.setColor(Color.WHITE);  // เพิ่มบรรทัดนี้! ตั้งค่าสีข้อความเป็นขาวอีกครั้ง
        String restartText = "Restart Game";
        textWidth = metrics.stringWidth(restartText);
        textX = buttonCenterX - textWidth / 2;

// ปรับความสูงสำหรับปุ่ม Restart
        buttonCenterY = (int) ((HEIGHT / 2 + 70 + 20) * scaleY);
        textY = buttonCenterY + (textHeight / 4);

        g2d.drawString(restartText, textX, textY);

// วาดปุ่ม "Main Menu" พร้อมไล่เฉดสี
        GradientPaint menuGradient = new GradientPaint(
                (int) ((WIDTH / 2 - 100) * scaleX), (int) ((HEIGHT / 2 + 120) * scaleY), new Color(130, 30, 30),
                (int) ((WIDTH / 2 + 100) * scaleX), (int) ((HEIGHT / 2 + 160) * scaleY), new Color(180, 50, 50)
        );
        g2d.setPaint(menuGradient);
        g2d.fillRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 120) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

// วาดขอบปุ่ม "Main Menu"
        g2d.setColor(new Color(200, 100, 100));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 100) * scaleX),
                (int) ((HEIGHT / 2 + 120) * scaleY),
                (int) (200 * scaleX),
                (int) (40 * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

// ข้อความสำหรับปุ่ม "Main Menu"
        g2d.setColor(Color.WHITE);  // เพิ่มบรรทัดนี้! ตั้งค่าสีข้อความเป็นขาวอีกครั้ง
        String menuText = "Main Menu";
        textWidth = metrics.stringWidth(menuText);
        textX = buttonCenterX - textWidth / 2;

// ปรับความสูงสำหรับปุ่ม Main Menu
        buttonCenterY = (int) ((HEIGHT / 2 + 120 + 20) * scaleY);
        textY = buttonCenterY + (textHeight / 4);

        g2d.drawString(menuText, textX, textY);
    }

    // เพิ่มเมธอดสำหรับวาดบัฟแบบมี scaling
    private void drawActiveBuffsWithScaling(Graphics g) {
        List<Powerup> activeBuffs = player.getActiveBuffs();

        if (activeBuffs.isEmpty()) {
            return;
        }

        int x = (int) (300 * scaleX);  // ตำแหน่งเริ่มต้น
        int y = (int) (20 * scaleY);   // ด้านบนของจอ
        int spacing = (int) (40 * scaleX); // ระยะห่างระหว่างไอคอน

        // วาดพื้นหลังสำหรับพื้นที่แสดงบัฟ
        g.setColor(new Color(0, 0, 0, 150)); // สีดำโปร่งใส
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
                g.setColor(Color.WHITE);
                Font originalFont = g.getFont();
                Font scaledFont = originalFont.deriveFont(originalFont.getSize() * 0.8f * scaleX);
                g.setFont(scaledFont);

                int seconds = buff.getDuration() / 60 + 1; // แปลงเฟรมเป็นวินาที
                g.drawString(seconds + "s", x + (int) (10 * scaleX), y + (int) (45 * scaleY));
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

        levelManager = new LevelManager();
        gameMap = new GameMap("level1"); // สร้างแผนที่ด่าน 1
    }

    public void startGameLoop() {
        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();

            // เริ่มเล่นเพลง Level 1 (ถ้าไม่ได้ปิดเสียง)
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

    // เพิ่มเมธอดนี้ในคลาส GamePanel (ถ้ายังไม่มี)
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
        // อัปเดตผู้เล่น (รวมถึงบัฟด้วย)
        player.update();

        // ตรวจสอบว่าผู้เล่นยังมีชีวิตอยู่หรือไม่
        if (!player.isAlive()) {
            if (!gameOver) { // เพิ่มเงื่อนไขให้ทำงานเพียงครั้งเดียวเมื่อเพิ่งตาย
                gameOver = true;
                // หยุดเพลงพื้นหลังเมื่อเกมจบ
                SoundManager.stopBackgroundMusic();
                // อาจเล่นเสียง game over ถ้ามี
                // SoundManager.playSound("game_over");
            }
            return;
        }

        // ถ้าเกมจบแล้ว ให้อัพเดทเฉพาะเอฟเฟกต์หน้า Game Over
        if (gameOver) {
            // อัพเดทเอฟเฟกต์ของหน้า Game Over
            gameOverEffectTimer++;

            // เอฟเฟกต์กระพริบ (pulse effect)
            if (gameOverPulseDirection) {
                gameOverPulseValue += 0.03f;
                if (gameOverPulseValue >= 1.0f) {
                    gameOverPulseValue = 1.0f;
                    gameOverPulseDirection = false;
                }
            } else {
                gameOverPulseValue -= 0.03f;
                if (gameOverPulseValue <= 0.0f) {
                    gameOverPulseValue = 0.0f;
                    gameOverPulseDirection = true;
                }
            }
            return; // ออกจากเมธอดเพราะเกมจบแล้ว ไม่ต้องอัพเดทสิ่งอื่น
        }

        // ถ้าเกมหยุดชั่วคราว ให้ไม่ต้องอัพเดทสถานะเกม
        if (gamePaused) {
            return;
        }

        // เพิ่มการเรียกใช้งาน handleShooting แทนการเช็คใน InputHandler
        inputHandler.handleShooting();

        // ตรวจสอบว่ามีบัฟ Stop Time ทำงานอยู่หรือไม่
        boolean stopTimeActive = player.hasStopTimeBuff();

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

// เมธอดย่อยสำหรับอัพเดทมอนสเตอร์
    private void updateMonsters() {
        // จัดการการชนกันระหว่างมอนสเตอร์
        for (Monster monster : monsters) {
            monster.update();

            // เก็บตำแหน่งเดิมไว้ก่อนเช็คการชน
            float oldX = monster.getX();
            float oldY = monster.getY();
            boolean hasCollision;
            int maxAttempts = 5; // จำกัดจำนวนครั้งในการพยายามหาตำแหน่งใหม่
            int attempts = 0;

            do {
                hasCollision = false;

                // ตรวจสอบการชนกับมอนสเตอร์ตัวอื่น
                for (Monster otherMonster : monsters) {
                    if (monster != otherMonster && monster.collidesWith(otherMonster)) {
                        hasCollision = true;

                        // เลื่อนตำแหน่งเล็กน้อยเพื่อหลีกเลี่ยงการซ้อนทับ
                        // ใช้ค่าแรงผลักที่แตกต่างกันตามพยายามครั้งที่
                        float pushStrength = 2 + attempts * 2; // เพิ่มแรงผลักในแต่ละรอบ

                        // คำนวณทิศทางที่จะผลักออก (ห่างจากมอนสเตอร์ตัวอื่น)
                        float dirX = monster.getX() - otherMonster.getX();
                        float dirY = monster.getY() - otherMonster.getY();

                        // ถ้าอยู่ตำแหน่งเดียวกันพอดี ให้ผลักในทิศทางสุ่ม
                        if (dirX == 0 && dirY == 0) {
                            dirX = random.nextFloat() * 2 - 1;
                            dirY = random.nextFloat() * 2 - 1;
                        }

                        // ทำให้เป็นเวกเตอร์หนึ่งหน่วย
                        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
                        if (length > 0) {
                            dirX /= length;
                            dirY /= length;
                        }

                        // ผลักมอนสเตอร์ออกไป
                        monster.setX(oldX + dirX * pushStrength);
                        monster.setY(oldY + dirY * pushStrength);

                        // ป้องกันไม่ให้ออกนอกจอ
                        monster.setX(Math.max(0, Math.min(monster.getX(), WIDTH - monster.getWidth())));
                        monster.setY(Math.max(0, Math.min(monster.getY(), HEIGHT - monster.getHeight())));

                        break;
                    }
                }

                attempts++;
            } while (hasCollision && attempts < maxAttempts);

            // ถ้าพยายามหลายครั้งแล้วยังชนอยู่ ก็ยอมให้ชนกัน
            if (hasCollision) {
                monster.setX(oldX);
                monster.setY(oldY);
            }
        }

        // ลบมอนสเตอร์ที่ตายแล้ว และเมธอดอื่นๆ...
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
        // ใช้ Graphics2D เพื่อสามารถทำ scaling ได้
        Graphics2D g2d = (Graphics2D) g;

        // บันทึกการแปลงเดิม
        AffineTransform oldTransform = g2d.getTransform();

        // ทำ scaling
        g2d.scale(scaleX, scaleY);

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

        // คืนค่าการแปลงเดิม
        g2d.setTransform(oldTransform);

        // วาด UI
        drawUIWithScaling(g);

        if (gameOver) {
            // เรียกใช้เมธอดใหม่ที่รองรับ scaling
            drawGameOverWithScaling(g);
        }

        if (gamePaused) {
            drawPausedWithScaling(g);
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
            // แปลงพิกัดเมาส์ให้อยู่ในระบบพิกัดของเกม
            int scaledX = (int) (targetX / scaleX);
            int scaledY = (int) (targetY / scaleY);

            // ส่งพิกัดที่แปลงแล้วไปให้ player
            player.shoot(scaledX, scaledY);
            playerBullets.addAll(player.getBullets());
        }
    }

    public void togglePause() {
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

        // เริ่มเล่นเพลงใหม่
        SoundManager.playBackgroundMusic("level1_music");
    }

    public void returnToMenu() {
        game.returnToMenu();
    }

    private void handleGameOverButtons(int x, int y) {
        // แปลงพิกัดเมาส์ให้เข้ากับ scaling
        int scaledX = (int) (x / scaleX);
        int scaledY = (int) (y / scaleY);

        // สร้างพื้นที่ปุ่ม "เล่นใหม่"
        Rectangle restartButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 90, 200, 40);

        // สร้างพื้นที่ปุ่ม "กลับเมนูหลัก"
        Rectangle menuButton = new Rectangle(WIDTH / 2 - 100, HEIGHT / 2 + 140, 200, 40);

        // ตรวจสอบว่าคลิกที่ปุ่มไหน
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

    public void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_P) {
            togglePause();
        } else if (keyCode == KeyEvent.VK_R && gameOver) {
            restartGame();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            returnToMenu();
        }
    }

    // เพิ่มเมธอดนี้ใน constructor ของ GamePanel (หลังจาก initGame())
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

        // สร้างปุ่มปรับระดับเสียง
        pauseButtons.add(new SoundControlButton(120, HEIGHT - 60, false)); // ปุ่มลดเสียง
        pauseButtons.add(new SoundControlButton(170, HEIGHT - 60, true));  // ปุ่มเพิ่มเสียง
    }
}

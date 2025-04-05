
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameOverScreen implements GameScreen {
    private final int WIDTH;
    private final int HEIGHT;
    private final Player player;
    private final LevelManager levelManager;
    private final GamePanel gamePanel;
    private float pulseValue = 0.0f;
    private boolean pulseDirection = true;
    private final List<GameButton> buttons = new ArrayList<>();
    private GameButton restart, menu;
    private int btnWidth = GameButton.getBtnWidth();
    private int btnHeight = GameButton.getBtnHeight();
    
    private float scaleX = 1.0f;  // เพิ่มตัวแปร scaleX
    private float scaleY = 1.0f;  // เพิ่มตัวแปร scaleY

    public GameOverScreen(int width, int height, Player player, LevelManager levelManager, GamePanel gamePanel) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.player = player;
        this.levelManager = levelManager;
        this.gamePanel = gamePanel;
        
        restart = new GameButton(
                width / 2 - 100, height / 2 + 90, btnWidth, btnHeight, "Restart Game",
                new Color(20, 100, 20), new Color(50, 150, 50),
                e -> gamePanel.togglePause());
        
        menu = new GameButton(
            width / 2 - 100, height / 2 + 140, btnWidth, btnHeight, "Main Menu",
            new Color(100, 20, 20), new Color(150, 50, 50),
            e -> gamePanel.returnToMenu());
        
        buttons.add(restart);
        buttons.add(menu);
    }
    
    @Override
    public void render(Graphics2D g2d, float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        
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

        // วาดปุ่มทั้งหมด
        for (GameButton button : buttons) {
            button.render(g2d, scaleX, scaleY);
        }
    }
    
    public void updatePulse() {
        // เพิ่มเอฟเฟกต์กระพริบ
        if (pulseDirection) {
            pulseValue += 0.03f;
            if (pulseValue >= 1.0f) {
                pulseValue = 1.0f;
                pulseDirection = false;
            }
        } else {
            pulseValue -= 0.03f;
            if (pulseValue <= 0.0f) {
                pulseValue = 0.0f;
                pulseDirection = true;
            }
        }
    }
    
     @Override
    public boolean handleMouseClick(int x, int y) {
        for (GameButton button : buttons) {
            if (button.contains(x, y, scaleX, scaleY)) {
                button.click();
                return true;
            }
        }
        return false;
    }
}
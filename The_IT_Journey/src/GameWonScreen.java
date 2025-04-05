
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GameWonScreen implements GameScreen {
    private final int WIDTH;
    private final int HEIGHT;
    private int finalScore;
    private final GamePanel gamePanel;
    private float pulseValue = 0.0f;
    private boolean pulseDirection = true;
    private final List<GameButton> buttons = new ArrayList<>();
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    public GameWonScreen(int width, int height, int finalScore, GamePanel gamePanel) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.finalScore = finalScore;
        this.gamePanel = gamePanel;
        
        // สร้างปุ่มพร้อม ActionListener
        buttons.add(new GameButton(
            width / 2 - 100, height / 2 + 140, 200, 40, "Main Menu",
            new Color(20, 100, 20), new Color(50, 150, 50),
            e -> gamePanel.returnToMenu()
        ));
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
    public void render(Graphics2D g2d, float scaleX, float scaleY) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // เพิ่มเอฟเฟกต์การเบลอพื้นหลัง
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g2d.setColor(new Color(0, 0, 0));
        g2d.fillRect(0, 0, (int) (WIDTH * scaleX), (int) (HEIGHT * scaleY));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // วาดกรอบหน้า "You Won" พร้อมไล่เฉดสี
        GradientPaint gradient = new GradientPaint(
                (int) ((WIDTH / 2 - 200) * scaleX), (int) ((HEIGHT / 2 - 150) * scaleY), new Color(20, 60, 20),
                (int) ((WIDTH / 2 + 200) * scaleX), (int) ((HEIGHT / 2 + 150) * scaleY), new Color(20, 150, 20)
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
        g2d.setColor(new Color(100, 200, 100));
        g2d.drawRoundRect(
                (int) ((WIDTH / 2 - 200) * scaleX),
                (int) ((HEIGHT / 2 - 150) * scaleY),
                (int) (400 * scaleX),
                (int) (300 * scaleY),
                (int) (20 * scaleX),
                (int) (20 * scaleY)
        );

        // สร้างฟอนต์สำหรับข้อความ YOU WON!
        Font gameWonFont = new Font("Arial", Font.BOLD, (int) (50 * scaleX));
        g2d.setFont(gameWonFont);

        // ข้อความหลัก "YOU WON!"
        String gameWonText = "YOU WON!";
        FontMetrics gameWonMetrics = g2d.getFontMetrics();

        // คำนวณตำแหน่ง X เพื่อให้ข้อความอยู่ตรงกลาง
        int centerX = (int) (WIDTH * scaleX) / 2;
        int gameWonWidth = gameWonMetrics.stringWidth(gameWonText);
        int gameWonX = centerX - gameWonWidth / 2;
        int gameWonY = (int) ((HEIGHT / 2 - 73) * scaleY);

        // วาดเงา "YOU WON!"
        g2d.setColor(new Color(0, 50, 0));
        g2d.drawString(gameWonText, gameWonX + (int) (3 * scaleX), gameWonY + (int) (3 * scaleY));

        // วาดข้อความหลัก "YOU WON!"
        g2d.setColor(new Color(50, 255, 50));
        g2d.drawString(gameWonText, gameWonX, gameWonY);

        // วาดสถิติผู้เล่น
        Font statsFont = new Font("Arial", Font.BOLD, (int) (24 * scaleX));
        g2d.setFont(statsFont);
        g2d.setColor(Color.WHITE);

        // คำนวณตำแหน่งสำหรับข้อความสถิติให้อยู่ตรงกลาง
        FontMetrics statsMetrics = g2d.getFontMetrics();

        // ข้อความสถิติ
        String scoreText = "Final Score: " + finalScore;
        String levelText = "Level Completed: 5";
        String bossText = "All Bosses Defeated!";

        int scoreWidth = statsMetrics.stringWidth(scoreText);
        int levelWidth = statsMetrics.stringWidth(levelText);
        int bossWidth = statsMetrics.stringWidth(bossText);
        
        int scoreX = centerX - scoreWidth / 2;
        int levelX = centerX - levelWidth / 2;
        int bossX = centerX - bossWidth / 2;

        g2d.drawString(scoreText, scoreX, (int) ((HEIGHT / 2 - 15) * scaleY));
        g2d.drawString(levelText, levelX, (int) ((HEIGHT / 2 + 20) * scaleY));
        g2d.drawString(bossText, bossX, (int) ((HEIGHT / 2 + 55) * scaleY));

        // วาดปุ่ม "กลับเมนูหลัก"
        GradientPaint menuGradient = new GradientPaint(
                (int) ((WIDTH / 2 - 100) * scaleX), (int) ((HEIGHT / 2 + 140) * scaleY), new Color(20, 100, 20),
                (int) ((WIDTH / 2 + 100) * scaleX), (int) ((HEIGHT / 2 + 180) * scaleY), new Color(50, 150, 50)
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
        g2d.setColor(new Color(100, 200, 100));
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
        int menuWidth = statsMetrics.stringWidth(menuText);
        int menuX = centerX - menuWidth / 2;
        int menuY = (int) ((HEIGHT / 2 + 165) * scaleY);

        g2d.drawString(menuText, menuX, menuY);
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }
    
    @Override
    public boolean handleMouseClick(int x, int y) {
        return false;
    }
}

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PauseScreen implements GameScreen {
    
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private final int WIDTH;
    private final int HEIGHT;
    private final GamePanel gamePanel;
    private final List<GameButton> buttons = new ArrayList<>();
    private GameButton resume, restart, menu;
    private int btnWidth = GameButton.getBtnWidth();
    private int btnHeight = GameButton.getBtnHeight();

    public PauseScreen(int width, int height, GamePanel gamePanel) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.gamePanel = gamePanel;
        
        resume = new GameButton(
                width / 2 - 100, height / 2 + 20, btnWidth, btnHeight, "Resume",
                new Color(20, 100, 20), new Color(50, 150, 50),
                e -> gamePanel.togglePause());
        
        restart = new GameButton(
                width / 2 - 100, height / 2 + 70, btnWidth, btnHeight, "Restart Game",
                new Color(20, 90, 140), new Color(70, 130, 180),
                e -> gamePanel.restartGame());
        
        menu = new GameButton(
                width / 2 - 100, height / 2 + 120, btnWidth, btnHeight, "Main Menu",
                new Color(130, 30, 30), new Color(180, 50, 50),
                e -> gamePanel.returnToMenu());

        buttons.add(resume);
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

        // วาดปุ่มทั้งหมด
        for (GameButton button : buttons) {
            button.render(g2d, scaleX, scaleY);
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
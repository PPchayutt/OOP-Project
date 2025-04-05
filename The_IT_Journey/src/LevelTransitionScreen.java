
import java.awt.*;

public class LevelTransitionScreen implements GameScreen {
    private final int WIDTH;
    private final int HEIGHT;
    private final LevelManager levelManager;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    public LevelTransitionScreen(int width, int height, LevelManager levelManager) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.levelManager = levelManager;
    }

    @Override
    public void render(Graphics2D g2d, float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        
        // พื้นหลังสีดำ
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRect(0, 0, (int)(WIDTH * scaleX), (int)(HEIGHT * scaleY));
        
        // ข้อความเปลี่ยนด่าน
        g2d.setColor(Color.WHITE);
        Font levelFont = new Font("Arial", Font.BOLD, (int)(50 * scaleX));
        g2d.setFont(levelFont);
        String message = "Level " + levelManager.getCurrentLevel();
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(message);
        
        // เงาสำหรับข้อความ
        g2d.setColor(new Color(20, 20, 50));
        g2d.drawString(message, 
            ((WIDTH * scaleX) - textWidth) / 2 + (3 * scaleX), 
            (HEIGHT * scaleY) / 2 - (30 * scaleY) + (3 * scaleY)
        );
        
        // ข้อความหลัก
        g2d.setColor(new Color(200, 200, 255));
        g2d.drawString(message, 
            ((WIDTH * scaleX) - textWidth) / 2, 
            (HEIGHT * scaleY) / 2 - (30 * scaleY)
        );

        // แสดงข้อความเตรียมตัว
        Font readyFont = new Font("Arial", Font.PLAIN, (int)(24 * scaleX));
        g2d.setFont(readyFont);
        String readyMessage = "Get Ready!";
        metrics = g2d.getFontMetrics();
        int readyWidth = metrics.stringWidth(readyMessage);
        g2d.setColor(new Color(220, 220, 220));
        g2d.drawString(readyMessage, 
                ((WIDTH * scaleX) - readyWidth) / 2, 
                (HEIGHT * scaleY) / 2 + (30 * scaleY)
        );

        // เวลาถอยหลัง
        Font countdownFont = new Font("Arial", Font.BOLD, (int)(70 * scaleX));
        g2d.setFont(countdownFont);
        int seconds = levelManager.getTransitionTimer() / 60 + 1;
        String countdownMessage = seconds + "";
        metrics = g2d.getFontMetrics();
        int countdownWidth = metrics.stringWidth(countdownMessage);
        
        // ตัวเลขนับถอยหลังหลัก
        g2d.setColor(new Color(255, 200, 100));
        g2d.drawString(countdownMessage, 
                ((WIDTH * scaleX) - countdownWidth) / 2, 
                (HEIGHT * scaleY) / 2 + (100 * scaleY)
        );
    }

    @Override
    public boolean handleMouseClick(int x, int y) {
        return false;
    }
}


import java.awt.*;
import java.awt.event.*;

public class GameButton {
    
    private final int x, y, width, height;
    private final String text;
    private final Color gradientStart, gradientEnd;
    private final ActionListener action;
    private static final int btnWidth = 200;
    private static final int btnHeight = 40;

    public GameButton(int x, int y, int width, int height, String text, 
                     Color gradientStart, Color gradientEnd, ActionListener action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.action = action;
    }
    
    public void render(Graphics2D g2d, float scaleX, float scaleY) {
        // วาดปุ่มพร้อมไล่เฉดสี
        GradientPaint buttonGradient = new GradientPaint(
                (int) (x * scaleX), (int) (y * scaleY), gradientStart,
                (int) ((x + width) * scaleX), (int) ((y + height) * scaleY), gradientEnd
        );
        g2d.setPaint(buttonGradient);
        g2d.fillRoundRect(
                (int) (x * scaleX),
                (int) (y * scaleY),
                (int) (width * scaleX),
                (int) (height * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // วาดขอบปุ่ม
        g2d.setStroke(new BasicStroke(2 * scaleX));
        g2d.setColor(gradientEnd.brighter());
        g2d.drawRoundRect(
                (int) (x * scaleX),
                (int) (y * scaleY),
                (int) (width * scaleX),
                (int) (height * scaleY),
                (int) (15 * scaleX),
                (int) (15 * scaleY)
        );

        // ข้อความสำหรับปุ่ม
        Font buttonFont = new Font("Arial", Font.BOLD, (int) (20 * scaleX));
        g2d.setFont(buttonFont);
        g2d.setColor(Color.WHITE);

        // คำนวณตำแหน่งเพื่อให้ข้อความอยู่ตรงกลางปุ่ม
        FontMetrics metrics = g2d.getFontMetrics(buttonFont);
        int textWidth = metrics.stringWidth(text);
        int buttonCenterX = (int) ((x + width / 2) * scaleX);
        int textX = buttonCenterX - textWidth / 2;

        // ปรับความสูงให้อยู่ตรงกลางปุ่มตามแนวดิ่ง
        int buttonCenterY = (int) ((y + height / 2) * scaleY);
        int textHeight = metrics.getHeight();
        int textY = buttonCenterY + (textHeight / 4);

        g2d.drawString(text, textX, textY);
    }
    
    public boolean contains(int mouseX, int mouseY, float scaleX, float scaleY) {
        int scaledX = (int)(mouseX / scaleX);
        int scaledY = (int)(mouseY / scaleY);
        return scaledX >= x && scaledX <= x + width && 
               scaledY >= y && scaledY <= y + height;
    }

    public void click() {
        if (action != null) {
            action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, text));
        }
    }
    
    public static int getBtnWidth() {
        return btnWidth;
    }
    
    public static int getBtnHeight() {
        return btnHeight;
    }
    
}
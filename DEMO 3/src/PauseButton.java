// PauseButton.java
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PauseButton implements MenuButton {
    private int x, y, width, height;
    private String text;
    private Color backgroundColor, textColor;
    private Runnable action;

    public PauseButton(int x, int y, int width, int height, String text, Color backgroundColor, Color textColor, Runnable action) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.action = action;
    }

    @Override
    public void onClick() {
        if (action != null) {
            action.run();
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(backgroundColor);
        g.fillRect(x, y, width, height);
        
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
        
        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        // คำนวณตำแหน่งกึ่งกลางสำหรับข้อความ
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getHeight();
        g.drawString(text, x + (width - textWidth) / 2, y + (height + textHeight) / 2 - 4);
    }

    public void render(Graphics g, float scaleX, float scaleY) {
        int scaledX = (int)(x * scaleX);
        int scaledY = (int)(y * scaleY);
        int scaledWidth = (int)(width * scaleX);
        int scaledHeight = (int)(height * scaleY);
        
        g.setColor(backgroundColor);
        g.fillRect(scaledX, scaledY, scaledWidth, scaledHeight);
        
        g.setColor(Color.WHITE);
        g.drawRect(scaledX, scaledY, scaledWidth, scaledHeight);
        
        g.setColor(textColor);
        Font originalFont = g.getFont();
        Font scaledFont = originalFont.deriveFont(originalFont.getSize2D() * scaleX);
        g.setFont(scaledFont);
        
        // คำนวณตำแหน่งกึ่งกลางสำหรับข้อความแบบมี scale
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getHeight();
        g.drawString(text, scaledX + (scaledWidth - textWidth) / 2, scaledY + (scaledHeight + textHeight) / 2 - 4);
        
        g.setFont(originalFont);
    }

    @Override
    public boolean isClicked(int mouseX, int mouseY) {
        return new Rectangle(x, y, width, height).contains(mouseX, mouseY);
    }
}
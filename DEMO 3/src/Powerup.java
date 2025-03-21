
import java.awt.*;

public class Powerup implements GameObject {

    private float x, y;
    private int width, height;
    private int type;
    private int value;
    private boolean active = true;
    private float fallSpeed = 1f;

    // ประเภทของพาวเวอร์อัพ: 0=เพิ่มพลังชีวิต, 1=เพิ่มความเร็ว, 2=เพิ่มความแรงกระสุน
    public Powerup(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 20;
        this.type = type;

        // กำหนดค่าพาวเวอร์อัพตามประเภท
        switch (type) {
            case 0 ->
                value = 25; // เพิ่มพลังชีวิต 25 หน่วย
            case 1 ->
                value = 1;  // เพิ่มความเร็ว 1 หน่วย
            case 2 ->
                value = 10; // เพิ่มความแรงกระสุน 10 หน่วย
        }
    }

    @Override
    public void update() {
        // ตกลงมาเรื่อยๆ
        y += fallSpeed;

        // หายไปเมื่อตกออกนอกจอ
        if (y > GamePanel.HEIGHT) {
            active = false;
        }
    }

    @Override
    public void render(Graphics g) {
        // สีตามประเภทของพาวเวอร์อัพ
        switch (type) {
            case 0 ->
                g.setColor(Color.GREEN); // เพิ่มพลังชีวิต
            case 1 ->
                g.setColor(Color.CYAN); // เพิ่มความเร็ว
            default ->
                g.setColor(Color.YELLOW); // เพิ่มความแรงกระสุน
        }

        g.fillOval((int) x, (int) y, width, height);

        // ขอบขาวสำหรับทุกประเภท
        g.setColor(Color.WHITE);
        g.drawOval((int) x, (int) y, width, height);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // Getters และ Setters
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}

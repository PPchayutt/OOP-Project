
import java.awt.*;

public class Player extends Entity {

    private int lives = 3;
    private int score = 0;
    private int invincibleTimer = 0;
    
    private float velocityX = 0;
    private float velocityY = 0;
    private final float acceleration = 0.5f;
    private final float deceleration = 0.3f;
    private final float maxSpeed = 5.0f;
    
    
    public Player(int x, int y, int width, int height, int health, int speed) {
        super(x, y, width, height, health, speed);
    }

    @Override
    public void update() {
        if (invincibleTimer > 0) {
            invincibleTimer--;
        }
        
        // อัพเดทตำแหน่งตามความเร็วปัจจุบัน
        x += (int)velocityX;
        y += (int)velocityY;
        
        // ชะลอความเร็วทุกเฟรม
        if (velocityX > 0) {
            velocityX = Math.max(0, velocityX - deceleration);
        } else if (velocityX < 0) {
            velocityX = Math.min(0, velocityX + deceleration);
        }
        
        if (velocityY > 0) {
            velocityY = Math.max(0, velocityY - deceleration);
        } else if (velocityY < 0) {
            velocityY = Math.min(0, velocityY + deceleration);
        }
        
        // จำกัดตำแหน่งไม่ให้ออกนอกจอ
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > 800 - width) x = 800 - width;
        if (y > 600 - height) y = 600 - height;
    }
    
    
    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
        
        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / 100 * width);
        g.fillRect(x, y - 10, healthBarWidth, 5);
        
    }

    public void move(int dx, int dy) {
        // เพิ่มความเร็วตามทิศทางที่กดปุ่ม
        if (dx != 0 || dy != 0) {
            // เมื่อมีการกดปุ่ม เพิ่มความเร็ว
            if (dx < 0) {
                velocityX = Math.max(-maxSpeed, velocityX - acceleration * 2);
            } else if (dx > 0) {
                velocityX = Math.min(maxSpeed, velocityX + acceleration * 2);
            }
            
            if (dy < 0) {
                velocityY = Math.max(-maxSpeed, velocityY - acceleration * 2);
            } else if (dy > 0) {
                velocityY = Math.min(maxSpeed, velocityY + acceleration * 2);
            }
            
            // ปรับความเร็วเมื่อเคลื่อนที่แนวทแยง
            if (dx != 0 && dy != 0) {
                velocityX *= 0.7071f; // ประมาณ 1/sqrt(2)
                velocityY *= 0.7071f;
            }
        }
    }
    
    @Override
    public void takeDamage(int amount) {
        if (invincibleTimer > 0) {
            return;
        }

        super.takeDamage(amount);

        if (!alive) {
            lives--;
            if (lives > 0) {
                alive = true;
                health = 100;
                invincibleTimer = 60;
            }
        } else {
            invincibleTimer = 30;
        }
    }

    public void takeDamage(int amount, boolean isBossDamage) {
        if (isBossDamage) {
            takeDamage(amount * 2);
        } else {
            takeDamage(amount);
        }
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }
}

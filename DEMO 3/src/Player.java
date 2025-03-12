import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Player คือคลาสที่แทนตัวละครหลักที่ควบคุมโดยผู้เล่น
 * รับผิดชอบการจัดการการเคลื่อนที่, การยิง, และสถานะต่างๆ ของผู้เล่น
 */
public class Player extends Entity {

    private int lives = 3;    // จำนวนชีวิตเริ่มต้น
    
    // ค่าความเร็ว
    private float velX = 0;
    private float velY = 0;
    private float targetVelX = 0;
    private float targetVelY = 0;
    private final float acceleration = 0.5f;
    private final float deceleration = 0.3f;
    private final float maxSpeed = 5.0f;
    
    // รายการกระสุน
    private List<PlayerBullet> bullets = new ArrayList<>();
    
    // ช่วงเวลาระหว่างการยิง
    private long lastShotTime = 0;
    private long shootCooldown = 200;  // มิลลิวินาที
    private long currentCooldown = 0;  // เวลาคูลดาวน์ปัจจุบัน
    
    // ช่วงเวลาที่ได้รับอมตะหลังโดนโจมตี
    private int invincibleTime = 0;
    private int maxInvincibleTime = 60;  // เฟรม
    
    // ค่าความเสียหายของกระสุน
    private int bulletDamage = 25;

    /**
     * สร้างผู้เล่นใหม่
     * @param x ตำแหน่ง x เริ่มต้น
     * @param y ตำแหน่ง y เริ่มต้น
     */
    public Player(float x, float y) {
        super(x, y, 30, 30, 100, 5);
        this.maxHealth = 100;
    }

    @Override
    public void update() {
        // การเคลื่อนที่แบบนุ่มนวล
        // เพิ่มความเร็วเข้าหาเป้าหมาย
        if (targetVelX > velX) {
            velX = Math.min(targetVelX, velX + acceleration);
        } else if (targetVelX < velX) {
            velX = Math.max(targetVelX, velX - deceleration);
        }
        
        if (targetVelY > velY) {
            velY = Math.min(targetVelY, velY + acceleration);
        } else if (targetVelY < velY) {
            velY = Math.max(targetVelY, velY - deceleration);
        }
        
        // อัพเดทตำแหน่ง
        x += velX;
        y += velY;
        
        // จำกัดให้อยู่ในหน้าจอ
        x = Math.max(0, Math.min(x, GamePanel.WIDTH - width));
        y = Math.max(0, Math.min(y, GamePanel.HEIGHT - height));
        
        // อัพเดทกระสุน
        updateBullets();
        
        // ลดเวลาอมตะ
        if (invincibleTime > 0) {
            invincibleTime--;
        }
        
        // ลดเวลาคูลดาวน์การยิง
        long currentTime = System.currentTimeMillis();
        currentCooldown = currentTime - lastShotTime;
    }

    /**
     * อัพเดทกระสุนทั้งหมดของผู้เล่น
     */
    private void updateBullets() {
        // อัพเดตแต่ละกระสุน
        for (int i = bullets.size() - 1; i >= 0; i--) {
            PlayerBullet bullet = bullets.get(i);
            bullet.update();
            
            // ลบกระสุนที่ไม่ใช้งานแล้ว
            if (!bullet.isActive()) {
                bullets.remove(i);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        // ถ้าอยู่ในช่วงอมตะให้กะพริบ
        if (invincibleTime <= 0 || invincibleTime % 10 < 5) {
            g.setColor(Color.BLUE);
            g.fillRect((int) x, (int) y, width, height);
            
            // วาดเครื่องหมายเล็ง
            g.setColor(Color.WHITE);
            g.drawOval((int) x + width / 4, (int) y + height / 4, width / 2, height / 2);
            
            // แสดงแถบพลังชีวิต
            g.setColor(Color.RED);
            g.fillRect((int) x, (int) y - 10, width, 3);
            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((float) health / maxHealth * width);
            g.fillRect((int) x, (int) y - 10, healthBarWidth, 3);
        }
        
        // วาดกระสุน
        for (PlayerBullet bullet : bullets) {
            bullet.render(g);
        }
    }
    
    /**
     * ยิงกระสุน
     * @param targetX ตำแหน่ง x เป้าหมาย
     * @param targetY ตำแหน่ง y เป้าหมาย
     */
    public void shoot(int targetX, int targetY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= shootCooldown) {
            // คำนวณทิศทางการยิง
            double angle = Math.atan2(targetY - (y + height / 2), targetX - (x + width / 2));
            
            // สร้างกระสุนใหม่
            PlayerBullet bullet = new PlayerBullet((int) (x + width / 2), (int) (y + height / 2), 8, 8, angle);
            bullet.setDamage(bulletDamage);
            bullets.add(bullet);
            
            // บันทึกเวลาที่ยิง
            lastShotTime = currentTime;
        }
    }
    
    /**
     * ตั้งค่าความเร็วเป้าหมายของผู้เล่น
     * @param targetVelX ความเร็วเป้าหมายในแกน X
     * @param targetVelY ความเร็วเป้าหมายในแกน Y
     */
    public void setTargetVelocity(float targetVelX, float targetVelY) {
        this.targetVelX = targetVelX;
        this.targetVelY = targetVelY;
    }
    
    @Override
    public void takeDamage(int damage) {
        // ถ้าอยู่ในช่วงอมตะให้ไม่รับความเสียหาย
        if (invincibleTime > 0) {
            return;
        }
        
        health -= damage;
        if (health <= 0) {
            health = 0;
            lives--;
            
            if (lives > 0) {
                // ยังมีชีวิตเหลือ ฟื้นฟูพลังชีวิต
                health = maxHealth;
                invincibleTime = maxInvincibleTime;
            } else {
                // หมดชีวิต
                alive = false;
            }
        } else {
            // ได้รับความเสียหายแต่ยังไม่ตาย ให้อมตะชั่วคราว
            invincibleTime = maxInvincibleTime / 2;
        }
    }
    
    /**
     * เพิ่มความเร็วให้กับผู้เล่น
     * @param amount จำนวนที่เพิ่ม
     */
    public void increaseSpeed(int amount) {
        this.speed += amount;
    }
    
    /**
     * เพิ่มความเสียหายของกระสุน
     * @param amount จำนวนที่เพิ่ม
     */
    public void increaseBulletDamage(int amount) {
        this.bulletDamage += amount;
    }
    
    /**
     * ดึงค่ารายการกระสุนทั้งหมด
     * @return รายการกระสุน
     */
    public List<PlayerBullet> getBullets() {
        return bullets;
    }
    
    /**
     * ดึงค่าความเร็วสูงสุด
     * @return ความเร็วสูงสุด
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }
    
    /**
     * ดึงค่าจำนวนชีวิต
     * @return จำนวนชีวิต
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * ดึงค่าเวลาคูลดาวน์การยิง
     * @return เวลาคูลดาวน์ (มิลลิวินาที)
     */
    public long getShootCooldown() {
        return shootCooldown;
    }
    
    /**
     * ดึงค่าเวลาคูลดาวน์ปัจจุบัน
     * @return เวลาคูลดาวน์ปัจจุบัน (มิลลิวินาที)
     */
    public long getCurrentCooldown() {
        return currentCooldown;
    }
}
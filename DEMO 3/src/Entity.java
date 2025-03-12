import java.awt.*;

/**
 * Entity เป็น abstract class พื้นฐานสำหรับวัตถุที่มีชีวิตในเกม
 * ทั้งผู้เล่นและศัตรูสืบทอดจากคลาสนี้
 */
public abstract class Entity implements GameObject, Damageable {
    
    // คุณสมบัติพื้นฐาน - ใช้ protected เพื่อให้คลาสลูกเข้าถึงได้
    protected float x, y;              // ตำแหน่ง
    protected float velX, velY;        // ความเร็วในแกน X และ Y
    protected int width, height;       // ขนาด
    protected int health;              // พลังชีวิต
    protected int maxHealth;           // พลังชีวิตสูงสุด
    protected int speed;               // ความเร็วการเคลื่อนที่พื้นฐาน
    protected boolean alive = true;    // สถานะการมีชีวิต
    
    /**
     * สร้าง Entity ใหม่
     * @param x ตำแหน่ง x เริ่มต้น
     * @param y ตำแหน่ง y เริ่มต้น
     * @param width ความกว้าง
     * @param height ความสูง
     * @param health พลังชีวิตเริ่มต้น
     * @param speed ความเร็วพื้นฐาน
     */
    public Entity(float x, float y, int width, int height, int health, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
    }
    
    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            die();
        }
    }
    
    @Override
    public boolean isAlive() {
        return alive;
    }
    
    @Override
    public void die() {
        alive = false;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
    
    /**
     * ตรวจสอบการชนกับวัตถุอื่น
     * @param other วัตถุที่ต้องการตรวจสอบการชน
     * @return true ถ้าชนกัน, false ถ้าไม่ชนกัน
     */
    public boolean collidesWith(GameObject other) {
        return getBounds().intersects(other.getBounds());
    }
    
    // Getters และ Setters
    
    public float getX() {
        return x;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public float getY() {
        return y;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public float getVelX() {
        return velX;
    }
    
    public void setVelX(float velX) {
        this.velX = velX;
    }
    
    public float getVelY() {
        return velY;
    }
    
    public void setVelY(float velY) {
        this.velY = velY;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
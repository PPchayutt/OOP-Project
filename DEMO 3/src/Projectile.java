import java.awt.*;

/**
 * Projectile เป็น abstract class พื้นฐานสำหรับกระสุนทุกประเภท
 * ทั้งกระสุนของผู้เล่นและศัตรูสืบทอดจากคลาสนี้
 */
public abstract class Projectile implements GameObject {
    
    protected float x, y;          // ตำแหน่ง
    protected int width, height;    // ขนาด
    protected double angle;         // มุมการเคลื่อนที่ (เรเดียน)
    protected int speed;            // ความเร็ว
    protected boolean active = true; // สถานะการทำงาน
    
    /**
     * สร้างกระสุนใหม่
     * @param x ตำแหน่ง x เริ่มต้น
     * @param y ตำแหน่ง y เริ่มต้น
     * @param width ความกว้าง
     * @param height ความสูง
     * @param angle มุมการเคลื่อนที่ (เรเดียน)
     * @param speed ความเร็ว
     */
    public Projectile(int x, int y, int width, int height, double angle, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.speed = speed;
    }
    
    /**
     * อัพเดตการเคลื่อนที่ของกระสุน
     * ลูกเรียกใช้และเพิ่มเติมการตรวจสอบการชนหรือออกจากหน้าจอ
     */
    @Override
    public abstract void update();
    
    /**
     * วาดกระสุนบนหน้าจอ
     * ลูกเรียกใช้และกำหนดการวาดตามรูปร่างของกระสุน
     */
    @Override
    public abstract void render(Graphics g);
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
    
    /**
     * ตรวจสอบว่ากระสุนยังทำงานอยู่หรือไม่
     * @return true ถ้ายังทำงานอยู่, false ถ้าไม่ทำงานแล้ว
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * ตั้งค่าสถานะการทำงานของกระสุน
     * @param active สถานะที่ต้องการตั้งค่า
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * ดึงมุมการเคลื่อนที่ของกระสุน
     * @return มุมการเคลื่อนที่ (เรเดียน)
     */
    public double getAngle() {
        return angle;
    }
    
    /**
     * ตั้งค่ามุมการเคลื่อนที่ของกระสุน
     * @param angle มุมที่ต้องการตั้งค่า (เรเดียน)
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }
    
    /**
     * ดึงความเร็วของกระสุน
     * @return ความเร็วปัจจุบัน
     */
    public int getSpeed() {
        return speed;
    }
    
    /**
     * ตั้งค่าความเร็วของกระสุน
     * @param speed ความเร็วที่ต้องการตั้งค่า
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public boolean collidesWith(GameObject other) {
        return getBounds().intersects(other.getBounds());
    }
}
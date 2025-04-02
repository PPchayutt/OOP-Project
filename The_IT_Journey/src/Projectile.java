
import java.awt.*;

public abstract class Projectile implements GameObject {

    protected float x, y; // ตำแหน่ง
    protected int width, height; // ขนาด
    protected double angle; // มุมการเคลื่อนที่ (เรเดียน)
    protected int speed; // ความเร็ว
    protected boolean active = true; // สถานะการทำงาน

    public Projectile(int x, int y, int width, int height, double angle, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.speed = speed;
    }

    @Override
    public abstract void update();

    @Override
    public abstract void render(Graphics g);

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean collidesWith(GameObject other) {
        return getBounds().intersects(other.getBounds());
    }
}

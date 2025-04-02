
import java.awt.*;

public class PlayerBullet extends Projectile {

    private int damage = 25;
    private boolean knockback = false;
    private int knockbackPower = 1;

    public PlayerBullet(int x, int y, int width, int height, double angle) {
        super(x, y, width, height, angle, 10);
    }

    @Override
    public void update() {
        // เคลื่อนที่ตามทิศทางและความเร็ว
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;

        // ตรวจสอบว่าออกนอกหน้าจอหรือไม่
        if (x < -width || x > GamePanel.WIDTH || y < -height || y > GamePanel.HEIGHT) {
            active = false;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x, (int) y, width, height);
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean hasKnockback() {
        return knockback;
    }

    public void setKnockback(boolean knockback) {
        this.knockback = knockback;
    }

    public int getKnockbackPower() {
        return knockbackPower;
    }

    public void setKnockbackPower(int knockbackPower) {
        this.knockbackPower = knockbackPower;
    }
}

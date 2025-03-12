
import java.awt.*;

public class EnemyBullet extends Projectile {

    private int damage;

    public EnemyBullet(int x, int y, int width, int height, double angle, int speed, int damage) {
        super(x, y, width, height, angle, speed);
        this.damage = damage;
    }

    @Override
    public void update() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;

        if (x < 0 || x > 800 || y < 0 || y > 600) {
            active = false;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);
    }

    public int getDamage() {
        return damage;
    }
}

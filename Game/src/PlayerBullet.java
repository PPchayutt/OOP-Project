
import java.awt.*;

public class PlayerBullet extends Projectile {

    private int damage = 25;

    public PlayerBullet(int x, int y, int width, int height, double angle) {
        super(x, y, width, height, angle, 10);
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
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, width, height);
    }

    public int getDamage() {
        return damage;
    }
}

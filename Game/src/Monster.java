
import java.awt.*;

public class Monster extends Enemy {

    private int movementPattern;
    private int patternCounter = 0;
    private boolean dropsPowerup;

    public Monster(int x, int y) {
        super(x, y, 30, 30, 50, 2, 10, 100);
        this.movementPattern = (int) (Math.random() * 2);
        this.dropsPowerup = Math.random() < 0.2;
    }

    @Override
    public void update() {
        updateCooldowns();

        if (movementPattern == 0) {
            y += speed;
        } else {
            y += speed;
            x += Math.sin(patternCounter * 0.1) * speed;
            patternCounter++;
        }

        if (y > 600 || x < -width || x > 800) {
            alive = false;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);

        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / 50 * width);
        g.fillRect(x, y - 5, healthBarWidth, 3);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(60);
        return new EnemyBullet(x + width / 2 - 4, y + height, 8, 8, Math.PI / 2, 5, damage);
    }

    public boolean dropsPowerup() {
        return dropsPowerup;
    }
}

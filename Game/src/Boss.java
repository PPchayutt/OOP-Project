
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Boss extends Enemy {

    private int phaseCounter = 0;
    private int phase = 0;
    private int moveDirection = 1;
    private int attackPattern = 0;

    public Boss(int x, int y, int level) {
        super(x, y, 80, 80, 200 * level, 1, 20, 1000 * level);
    }

    @Override
    public void update() {
        updateCooldowns();

        phaseCounter++;
        if (phaseCounter >= 300) {
            phaseCounter = 0;
            phase = (phase + 1) % 3;
            attackPattern = (int) (Math.random() * 3);
        }

        if (phase == 0) {
            x += speed * moveDirection;

            if (x <= 0 || x >= 800 - width) {
                moveDirection *= -1;
            }
        } else if (phase == 1) {
            x += Math.cos(phaseCounter * 0.05) * speed;
            y += Math.sin(phaseCounter * 0.05) * speed;

            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x > 800 - width) {
                x = 800 - width;
            }
            if (y > 300) {
                y = 300;
            }
        } else {
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(180, 0, 0));
        g.fillRect(x, y, width, height);

        g.setColor(Color.YELLOW);
        g.fillOval(x + width / 4, y + height / 4, width / 5, height / 5);
        g.fillOval(x + width * 3 / 5, y + height / 4, width / 5, height / 5);

        g.setColor(Color.BLACK);
        g.fillRect(x + width / 4, y + height * 2 / 3, width / 2, height / 8);

        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) health / (200 * (points / 1000)) * width);
        g.fillRect(x, y - 10, healthBarWidth, 5);
    }

    @Override
    public EnemyBullet attack() {
        if (!canAttack()) {
            return null;
        }

        if (attackPattern == 0) {
            resetShootCooldown(30);
            return new EnemyBullet(x + width / 2 - 4, y + height, 8, 8, Math.PI / 2, 3, damage);
        } else if (attackPattern == 1) {
            resetShootCooldown(60);

            double angle = Math.PI / 3 + Math.random() * Math.PI / 3;
            return new EnemyBullet(x + width / 2 - 4, y + height, 8, 8, angle, 2, damage);
        } else {
            resetShootCooldown(15);

            int targetX = 400;
            int targetY = 500;

            double dx = targetX - (x + width / 2);
            double dy = targetY - (y + height / 2);
            double angle = Math.atan2(dy, dx);

            return new EnemyBullet(x + width / 2 - 4, y + height / 2, 8, 8, angle, 3, damage);
        }
    }

    public List<EnemyBullet> attackSpecial() {
        if (!canAttack()) {
            return null;
        }

        resetShootCooldown(120);

        List<EnemyBullet> bullets = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            bullets.add(new EnemyBullet(x + width / 2 - 4, y + height / 2, 8, 8, angle, 2, damage));
        }

        return bullets;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);

        if (health <= 100 && speed == 1) {
            speed = 2;
            damage = damage * 2;
        }
    }
}

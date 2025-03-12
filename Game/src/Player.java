
import java.awt.*;

public class Player extends Entity {

    private int lives = 3;
    private int score = 0;
    private int invincibleTimer = 0;

    public Player(int x, int y, int width, int height, int health, int speed) {
        super(x, y, width, height, health, speed);
    }

    @Override
    public void update() {
        if (invincibleTimer > 0) {
            invincibleTimer--;
        }
    }

    @Override
    public void render(Graphics g) {
        if (invincibleTimer % 10 >= 5) {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);

            g.setColor(Color.GREEN);
            int healthBarWidth = (int) ((double) health / 100 * width);
            g.fillRect(x, y - 10, healthBarWidth, 5);
        }
    }

    public void move(int dx, int dy) {
        if (dx != 0 && dy != 0) {
            x += (dx * speed) / Math.sqrt(2);
            y += (dy * speed) / Math.sqrt(2);
        } else {
            x += dx * speed;
            y += dy * speed;
        }

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > 800 - width) {
            x = 800 - width;
        }
        if (y > 600 - height) {
            y = 600 - height;
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

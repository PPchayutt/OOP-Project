
import java.awt.*;

public abstract class Enemy extends Entity {

    protected int damage;
    protected int points;
    protected int shootCooldown = 0;

    public Enemy(int x, int y, int width, int height, int health, int speed, int damage, int points) {
        super(x, y, width, height, health, speed);
        this.damage = damage;
        this.points = points;
    }

    public abstract EnemyBullet attack();

    public int getPoints() {
        return points;
    }

    public boolean canAttack() {
        return shootCooldown <= 0;
    }

    public void resetShootCooldown(int cooldown) {
        this.shootCooldown = cooldown;
    }

    public void updateCooldowns() {
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    public int getDamage() {
        return damage;
    }
}

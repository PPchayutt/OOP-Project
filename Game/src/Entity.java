
import java.awt.*;

public abstract class Entity extends GameObject implements Damageable {

    protected int health;
    protected int speed;
    protected boolean alive = true;

    public Entity(int x, int y, int width, int height, int health, int speed) {
        super(x, y, width, height);
        this.health = health;
        this.speed = speed;
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void die() {
        alive = false;
        active = false;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}

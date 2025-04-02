
import java.awt.*;

public abstract class Entity implements GameObject, Damageable {

    protected float x, y;
    protected float velX, velY;
    protected int width, height;
    protected int health;
    protected int maxHealth;
    protected int speed;
    protected boolean alive = true; // สถานะการมีชีวิต

    public Entity(float x, float y, int width, int height, int health, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            die();
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void die() {
        alive = false;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean collidesWith(GameObject other) {
        return getBounds().intersects(other.getBounds());
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}

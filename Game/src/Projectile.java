
import java.awt.*;

public abstract class Projectile extends GameObject {

    protected double angle;
    protected int speed;

    public Projectile(int x, int y, int width, int height, double angle, int speed) {
        super(x, y, width, height);
        this.angle = angle;
        this.speed = speed;
    }

    public boolean isActive() {
        return active;
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
}

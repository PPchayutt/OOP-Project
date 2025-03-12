
import java.awt.*;

public class Powerup extends GameObject {

    private int type;
    private int value;

    public Powerup(int x, int y, int type) {
        super(x, y, 20, 20);
        this.type = type;

        switch (type) {
            case 0:
                value = 25;
                break;
            case 1:
                value = 1;
                break;
            case 2:
                value = 10;
                break;
        }
    }

    @Override
    public void update() {
        y += 1;

        if (y > 600) {
            active = false;
        }
    }

    @Override
    public void render(Graphics g) {
        switch (type) {
            case 0:
                g.setColor(Color.GREEN);
                break;
            case 1:
                g.setColor(Color.CYAN);
                break;
            case 2:
                g.setColor(Color.YELLOW);
                break;
        }

        g.fillOval(x, y, width, height);

        g.setColor(Color.WHITE);
        g.drawOval(x, y, width, height);
    }

    public int getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}

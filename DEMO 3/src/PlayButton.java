
import java.awt.Graphics;

public class PlayButton extends AbstractMenuButton {

    private final Game game;

    public PlayButton(int x, int y, Game game) {
        super(x, y, "resources/images/start bar2.png");
        this.game = game;
    }

    @Override
    public void render(Graphics g, float scaleX, float scaleY) {
        int scaledX = (int) (x * scaleX);
        int scaledY = (int) (y * scaleY);
        int scaledWidth = (int) (width * scaleX);
        int scaledHeight = (int) (height * scaleY);

        g.drawImage(buttonImage, scaledX, scaledY, scaledWidth * 2, scaledHeight * 2, null);
    }

    @Override
    public void onClick() {
        game.startGame();
    }
}

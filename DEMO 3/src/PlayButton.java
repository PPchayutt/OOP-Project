
import java.awt.Graphics;

public class PlayButton extends AbstractMenuButton {

    private final Game game;

    public PlayButton(int x, int y, Game game) {
        super(x, y, "resources/images/start bar2.png");
        this.game = game;
    }

    /**
     *
     * @param g
     */
    @Override
    public void render(Graphics g) {
        // วาดปุ่มขนาดใหญ่กว่าภาพต้นฉบับ (ขยาย 1.5 เท่า)
        g.drawImage(buttonImage, x, y, width * 2, height * 2, null);
    }

    @Override
    public void onClick() {
        game.startGame();
    }
}

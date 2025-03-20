public class PlayButton extends AbstractMenuButton {
    private Game game;
    
    public PlayButton(int x, int y, Game game) {
        super(x, y, "resources/images/start bar2.png");
        this.game = game;
    }
    
    @Override
    public void onClick() {
        game.startGame();
    }
}
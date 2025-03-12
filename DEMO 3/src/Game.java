import javax.swing.*;

public class Game {
    private JFrame window;
    private GamePanel gamePanel;
    
    public Game() {
        window = new JFrame("2D Bullet Hell Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        gamePanel = new GamePanel();
        gamePanel.setupGame();
        
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        gamePanel.startGameLoop();
    }
    
    public static void main(String[] args) {
        new Game();
    }
}
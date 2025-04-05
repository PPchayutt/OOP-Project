
import java.awt.*;

public class GameUIManager {
    private final GamePanel gamePanel;
    private final PauseScreen pauseScreen;
    private final GameOverScreen gameOverScreen;
    private final GameWonScreen gameWonScreen;
    private final LevelTransitionScreen levelTransitionScreen;
    
    private GameState currentState = GameState.PLAYING;

    public enum GameState {
        PLAYING, // กำลังเล่นเกม
        PAUSED, // หยุดเกมชั่วคราว
        GAME_OVER, // แพ้
        GAME_WON, // ชนะ
        LEVEL_TRANSITION // กำลังเปลี่ยนด่าน
    }
    
    public GameUIManager(GamePanel gamePanel, Player player, LevelManager levelManager, int finalScore) {
        this.gamePanel = gamePanel;
        
        // สร้างหน้าจอต่างๆ
        this.pauseScreen = new PauseScreen(GamePanel.WIDTH, GamePanel.HEIGHT, gamePanel);
        this.gameOverScreen = new GameOverScreen(GamePanel.WIDTH, GamePanel.HEIGHT, player, levelManager, gamePanel);
        this.gameWonScreen = new GameWonScreen(GamePanel.WIDTH, GamePanel.HEIGHT, finalScore, gamePanel);
        this.levelTransitionScreen = new LevelTransitionScreen(GamePanel.WIDTH, GamePanel.HEIGHT, levelManager);
    }
    
    public void setState(GameState state) {
        this.currentState = state;
    }
    
    public GameState getState() {
        return currentState;
    }

    public void updateFinalScore(int finalScore) {
        if (gameWonScreen instanceof GameWonScreen) {
            gameWonScreen.setFinalScore(finalScore);
        }
    }
    
    public void render(Graphics2D g2d, float scaleX, float scaleY) {
        switch (currentState) {
            case PAUSED -> pauseScreen.render(g2d, scaleX, scaleY);
            case GAME_OVER -> gameOverScreen.render(g2d, scaleX, scaleY);
            case GAME_WON -> gameWonScreen.render(g2d, scaleX, scaleY);
            case LEVEL_TRANSITION -> levelTransitionScreen.render(g2d, scaleX, scaleY);
        }
    }
    
    public void updateEffects() {
        if (currentState == GameState.GAME_OVER) {
            gameOverScreen.updatePulse();
        } else if (currentState == GameState.GAME_WON) {
            gameWonScreen.updatePulse();
        }
    }
    
    public boolean handleMouseClick(int x, int y) {
        return switch (currentState) {
            case PAUSED -> pauseScreen.handleMouseClick(x, y);
            case GAME_OVER -> gameOverScreen.handleMouseClick(x, y);
            case GAME_WON -> gameWonScreen.handleMouseClick(x, y);
            case LEVEL_TRANSITION -> levelTransitionScreen.handleMouseClick(x, y);
            default -> false;
        };
    }

    public GameOverScreen getGameOverScreen() {
        return gameOverScreen;
    }
    
    public GameWonScreen getGameWonScreen() {
        return gameWonScreen;
    }
}
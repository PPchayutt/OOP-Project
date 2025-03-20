import javax.swing.*;
import java.awt.*;

public class Game {

    private JFrame window;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameState currentState;

    public Game() {
        window = new JFrame("The IT Journey");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // สร้าง CardLayout เพื่อสลับระหว่างหน้าต่างๆ
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // สร้างเมนูหลัก
        menuPanel = new MenuPanel(this);
        mainPanel.add(menuPanel, "Menu");
        currentState = menuPanel;
        
        window.add(mainPanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void startGame() {
        // สร้างเกมใหม่และเริ่มเล่น
        if (gamePanel == null) {
            gamePanel = new GamePanel(this);
            mainPanel.add(gamePanel, "Game");
        } else {
            gamePanel.restartGame(); // รีเซ็ตเกมหากมีอยู่แล้ว
        }
        
        // หยุดเพลงและแอนิเมชั่นในเมนู
        menuPanel.cleanup();
        
        // เปลี่ยนไปที่หน้าเกม
        cardLayout.show(mainPanel, "Game");
        gamePanel.requestFocusInWindow(); // สำหรับรับ input
        gamePanel.startGameLoop();
        currentState = gamePanel;
    }
    
    public void returnToMenu() {
        // หยุดเกมและกลับไปที่เมนู
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
        }
        
        cardLayout.show(mainPanel, "Menu");
        menuPanel.requestFocusInWindow();
        menuPanel.playMusic();
        currentState = menuPanel;
    }

    public static void main(String[] args) {
        new Game();
    }
}
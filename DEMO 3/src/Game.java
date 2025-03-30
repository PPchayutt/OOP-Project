
import javax.swing.*;
import java.awt.*;

public class Game {

    // ตัวแปรเดิมที่มีอยู่แล้ว
    private final JFrame window;
    private final MenuPanel menuPanel;
    private GamePanel gamePanel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    // ตั้งค่าขนาดพื้นฐานที่ไม่สามารถเปลี่ยนแปลงได้
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;

    public Game() {
        // สร้างหน้าต่างหลักของเกม
        window = new JFrame("The IT Journey");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false); // ล็อคไม่ให้ปรับขนาดได้

        // สร้าง CardLayout เพื่อสลับระหว่างหน้าต่างๆ
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // โหลดรูปภาพและเสียง
        ImageManager.loadImages();
        SoundManager.loadSounds();

        // สร้างเมนูหลัก
        menuPanel = new MenuPanel(this);
        mainPanel.add(menuPanel, "Menu");

        // เพิ่มพาเนลหลักเข้าไปในหน้าต่าง
        window.add(mainPanel);

        // ตั้งขนาดหน้าต่างเป็นค่าคงที่ 800x600
        window.setSize(BASE_WIDTH, BASE_HEIGHT);
        window.setLocationRelativeTo(null);

        // แสดงหน้าต่าง
        window.setVisible(true);
    }

    // เพิ่มเมธอดรับค่าขนาดต่างๆ แบบคงที่
    public int getBaseWidth() {
        return BASE_WIDTH;
    }

    public int getBaseHeight() {
        return BASE_HEIGHT;
    }

    // คงค่า scale ไว้ที่ 1.0 เสมอ เพื่อไม่ให้มีการปรับขนาด
    public float getScaleX() {
        return 1.0f;
    }

    public float getScaleY() {
        return 1.0f;
    }

    public void startGame() {
        // สร้างเกมใหม่และเริ่มเล่น
        if (gamePanel == null) {
            gamePanel = new GamePanel(this);
            mainPanel.add(gamePanel, "Game");
        } else {
            gamePanel.restartGame(); // รีเซ็ตเกมหากมีอยู่แล้ว
        }

        // หยุดเพลงในเมนู
        SoundManager.stopBackgroundMusic();
        menuPanel.cleanup();

        // เปลี่ยนไปที่หน้าเกม
        cardLayout.show(mainPanel, "Game");
        gamePanel.requestFocusInWindow(); // สำหรับรับ input
        gamePanel.startGameLoop();
    }

    public void returnToMenu() {
        // หยุดเกมและกลับไปที่เมนู
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
        }

        // หยุดเสียงทั้งหมดก่อน
        SoundManager.stopBackgroundMusic();
        SoundManager.stopAllEffects();

        // สลับไปหน้าเมนู
        cardLayout.show(mainPanel, "Menu");
        menuPanel.requestFocusInWindow();

        // รอให้ UI อัพเดทก่อนค่อยเล่นเพลงเมนู
        SwingUtilities.invokeLater(() -> {
            // ไม่ต้องชะลอเวลาด้วย Thread.sleep อีกต่อไป
            // เนื่องจาก invokeLater จะทำงานหลังจาก UI อัพเดทแล้ว
            menuPanel.playMusic();
        });
    }
}

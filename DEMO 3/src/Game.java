
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Game {

    private final JFrame window;
    private final MenuPanel menuPanel;
    private GamePanel gamePanel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private boolean isAdjusting = false;

    public Game() {
        // สร้างหน้าต่างหลักของเกม
        window = new JFrame("The IT Journey");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);

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

        // ตั้งขนาดเริ่มต้นเป็น 4:3
        window.setSize(800, 600);
        window.setLocationRelativeTo(null);

        // เพิ่ม ComponentListener เพื่อรักษาอัตราส่วน 4:3
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!isAdjusting) {
                    isAdjusting = true;

                    // คำนวณอัตราส่วน 4:3
                    int width = window.getWidth();
                    int height = window.getHeight();

                    // ใช้ความกว้างเป็นฐานในการคำนวณความสูง
                    int targetHeight = (width * 3) / 4;

                    // ถ้าความสูงไม่ตรงตามอัตราส่วน 4:3 ให้ปรับ
                    if (Math.abs(height - targetHeight) > 5) {
                        window.setSize(width, targetHeight);
                    }

                    isAdjusting = false;
                }
            }
        });

        // แสดงหน้าต่าง
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
    }

    public void returnToMenu() {
        // หยุดเกมและกลับไปที่เมนู
        if (gamePanel != null) {
            gamePanel.stopGameLoop();
        }

        // เปลี่ยนกลับไปที่หน้าเมนู
        cardLayout.show(mainPanel, "Menu");
        menuPanel.requestFocusInWindow();
        menuPanel.playMusic();
    }

    /**
     * เมธอดหลักสำหรับเริ่มโปรแกรม
     *
     * @param args พารามิเตอร์บรรทัดคำสั่ง (ไม่ได้ใช้)
     */
    public static void main(String[] args) {
        // เริ่มต้นเกมใน Event Dispatch Thread เพื่อความปลอดภัย
        SwingUtilities.invokeLater(() -> {
            new Game();
        });
    }
}

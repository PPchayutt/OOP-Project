// เพิ่ม imports ต่อไปนี้ที่ด้านบนของไฟล์

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game {

    // ตัวแปรเดิมที่มีอยู่แล้ว
    private final JFrame window;
    private final MenuPanel menuPanel;
    private GamePanel gamePanel;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private boolean isAdjusting = false;

    // เพิ่มตัวแปรใหม่สำหรับ fullscreen
    private boolean isFullscreen = false;
    private Rectangle previousWindowBounds;
    private GraphicsDevice device;

    // เพิ่มตัวแปรสำหรับ scaling
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;

    public Game() {
        // สร้างหน้าต่างหลักของเกม
        window = new JFrame("The IT Journey");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);

        // เพิ่มการเข้าถึง GraphicsDevice
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

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
        window.setSize(BASE_WIDTH, BASE_HEIGHT);
        window.setLocationRelativeTo(null);

        // เพิ่ม ComponentListener เพื่อรักษาอัตราส่วน 4:3 และอัพเดท scale factor
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateScaleFactor();

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

    // เพิ่มเมธอดสำหรับอัพเดท scale factor
    private void updateScaleFactor() {
        int width = window.getWidth();
        int height = window.getHeight();

        // คำนวณ scale factors
        scaleX = (float) width / BASE_WIDTH;
        scaleY = (float) height / BASE_HEIGHT;

        // แจ้ง GamePanel และ MenuPanel เพื่ออัพเดท scaling
        if (gamePanel != null) {
            gamePanel.setScale(scaleX, scaleY);
        }
        if (menuPanel != null) {
            menuPanel.setScale(scaleX, scaleY);
        }
    }

    // ไม่ต้องประกาศ method นี้ซ้ำ ถ้ามีอยู่แล้ว
    // เมธอดสำหรับสลับโหมด fullscreen
    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
    
        if (isFullscreen) {
        // จัดเก็บขนาดหน้าต่างปัจจุบันไว้
            previousWindowBounds = window.getBounds();
        
        // วิธีแบบใหม่ - ไม่ใช้ dispose() เพื่อหลีกเลี่ยงจอค้าง
            window.setUndecorated(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // อัพเดท scale factor
            updateScaleFactor();
        
        // ร้องขอโฟกัส
            if (gamePanel != null && gamePanel.isVisible()) {
                gamePanel.requestFocus();
            } else {
                menuPanel.requestFocus();
            }
        } else {
        // กลับไปโหมดปกติ
            window.setUndecorated(false);
        
        // คืนค่าขนาดเดิม
            if (previousWindowBounds != null) {
                window.setBounds(previousWindowBounds);
            } else {
                window.setSize(BASE_WIDTH, BASE_HEIGHT);
                window.setLocationRelativeTo(null);
            }
        
        // อัพเดท scale factor
            updateScaleFactor();
        
        // ร้องขอโฟกัส
            if (gamePanel != null && gamePanel.isVisible()) {
                gamePanel.requestFocus();
            } else {
                menuPanel.requestFocus();
            }
        }
    
    // บังคับให้อัพเดทการแสดงผล
        SwingUtilities.updateComponentTreeUI(window);
        window.validate();
        window.repaint();
    }

    // เพิ่ม getters สำหรับ scale factors
    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
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

    public static void main(String[] args) {
        // เริ่มต้นเกมใน Event Dispatch Thread เพื่อความปลอดภัย
        SwingUtilities.invokeLater(() -> {
            new Game();
        });
    }
}

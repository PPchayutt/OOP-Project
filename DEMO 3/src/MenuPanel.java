import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MenuPanel extends JPanel implements MouseListener, GameState {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final Image backgroundImage;
    private final List<MenuButton> buttons;
    private Clip menuMusic;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;

    public MenuPanel(Game game) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        this.addMouseListener(this);

        // โหลดภาพพื้นหลัง
        this.backgroundImage = new ImageIcon("resources/images/Background_oop.jpg").getImage();

        // สร้างปุ่มต่างๆ
        buttons = new ArrayList<>();

        // ปุ่มเล่นเกม - ตรงกลาง
        int playButtonX = WIDTH / 2 - 100;
        int playButtonY = HEIGHT / 2 - 50;
        buttons.add(new PlayButton(playButtonX, playButtonY, game));

        // ปุ่มเปิด/ปิดเสียง - มุมขวาล่าง
        int soundButtonX = WIDTH - 60;
        int soundButtonY = HEIGHT - 0;
        buttons.add(new SoundButton(soundButtonX, soundButtonY, this));

        buttons.add(new SoundControlButton(WIDTH - 160, HEIGHT - 60, false)); // ปุ่มลดเสียง
        buttons.add(new SoundControlButton(WIDTH - 110, HEIGHT - 60, true));  // ปุ่มเพิ่มเสียง

        // ปุ่มคำอธิบายทักษะ - ตรงกลางล่าง
        int skillButtonX = WIDTH / 2 - 100;
        int skillButtonY = HEIGHT / 2 + 30;
        buttons.add(new SkillButton(skillButtonX, skillButtonY, this));

        // เริ่มเล่นเพลง
        loadAndPlayMusic();

        // เพิ่ม ComponentListener เพื่อรู้เมื่อขนาดพาเนลเปลี่ยน
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // เรียกเมธอดปรับตำแหน่งปุ่มเมื่อขนาดเปลี่ยน
                repositionButtons();
                repaint();
            }
        });
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        repaint();
    }


    public void repositionButtons() {
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();

        // ปรับตำแหน่งปุ่มทั้งหมดให้อยู่ตำแหน่งที่เหมาะสม
        for (MenuButton button : buttons) {
            if (button instanceof PlayButton) {
                // ปุ่ม Play ตรงกลางหน้าจอ
                ((AbstractMenuButton) button).setPosition(panelWidth / 2 - 100, panelHeight / 2 - 50);
            } else if (button instanceof SkillButton) {
                // ปุ่ม Skill ใต้ปุ่ม Play
                ((AbstractMenuButton) button).setPosition(panelWidth / 2 - 100, panelHeight / 2 + 30);
            } else if (button instanceof SoundButton) {
                // ปุ่ม Sound มุมขวาล่าง
                ((AbstractMenuButton) button).setPosition(panelWidth - 60, panelHeight - 60);
            } else if (button instanceof VolumeButton volumeButton) {
                boolean isIncrease = volumeButton.isIncreaseButton();
                if (isIncrease) {
                    ((AbstractMenuButton) button).setPosition(panelWidth - 110, panelHeight - 60);
                } else {
                    ((AbstractMenuButton) button).setPosition(panelWidth - 160, panelHeight - 60);
                }
            }
        }
    }

    private void loadAndPlayMusic() {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("resources/sounds/Menu song update.wav"));
            menuMusic = AudioSystem.getClip();
            menuMusic.open(audioInput);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // จัดการกรณีโหลดไฟล์เพลงไม่สำเร็จ
            System.err.println("ไม่สามารถโหลดไฟล์เพลงได้: " + e.getMessage());
        }
    }

    public void playMusic() {
        if (menuMusic != null && !menuMusic.isRunning()) {
            menuMusic.start();
        }
    }

    public void stopMusic() {
        if (menuMusic != null && menuMusic.isRunning()) {
            menuMusic.stop();
        }
    }

    @Override
    public void update() {
        // อัพเดตสถานะต่างๆ ในเมนู (ถ้ามี)
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        
        // ใช้ scaling
        g2d.scale(scaleX, scaleY);
        
        // วาดพื้นหลัง
        g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
        
        // คืนค่าการแปลงเดิม
        g2d.setTransform(oldTransform);
        
        // วาดปุ่มโดยใช้ scaling
        for (MenuButton button : buttons) {
            if (button instanceof AbstractMenuButton) {
                ((AbstractMenuButton) button).render(g, scaleX, scaleY);
            } else {
                button.render(g);
            }
        }
    }

    @Override
    public void handleMouseClick(int x, int y) {
        // แปลงพิกัดเมาส์ให้ถูกต้องตาม scaling
        int scaledX = (int)(x / scaleX);
        int scaledY = (int)(y / scaleY);
        
        for (MenuButton button : buttons) {
            if (button.isClicked(scaledX, scaledY)) {
                button.onClick();
                break;
            }
        }
    }

    // ส่วนของ MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        handleMouseClick(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // เมื่อออกจากเมนู ควรหยุดเพลง
    public void cleanup() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.close();
        }
    }
}
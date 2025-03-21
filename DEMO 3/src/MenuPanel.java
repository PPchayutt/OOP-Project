
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
        int soundButtonY = HEIGHT - 60;
        buttons.add(new SoundButton(soundButtonX, soundButtonY, this));

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

    /**
     * ปรับตำแหน่งปุ่มตามขนาดของพาเนลปัจจุบัน
     */
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
        // วาดพื้นหลังให้เต็มขนาดพาเนลปัจจุบัน
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);

        // วาดปุ่มทั้งหมด
        for (MenuButton button : buttons) {
            button.render(g);
        }
    }

    @Override
    public void handleMouseClick(int x, int y) {
        for (MenuButton button : buttons) {
            if (button.isClicked(x, y)) {
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

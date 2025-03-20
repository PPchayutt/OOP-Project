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
    
    private Game game;
    private Image backgroundImage;
    private List<MenuButton> buttons;
    private Clip menuMusic;
    
    public MenuPanel(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        this.addMouseListener(this);

        // โหลดภาพพื้นหลัง
        this.backgroundImage = new ImageIcon("resources/images/Background_oop.jpg").getImage();

        // สร้างปุ่มต่างๆ
        buttons = new ArrayList<>();

        // ปุ่มเล่นเกม - ตรงกลาง (ปรับตำแหน่งให้อยู่กลางจอจริงๆ)
        int playButtonX = WIDTH/2 - 60; // ลองปรับตัวเลขตามขนาดปุ่มจริง
        int playButtonY = HEIGHT/2 - 20;
        buttons.add(new PlayButton(playButtonX, playButtonY, game));

        // ปุ่มเปิด/ปิดเสียง - มุมขวาล่าง (ชิดขอบมากขึ้น)
        int soundButtonX = WIDTH - 60;
        int soundButtonY = HEIGHT - 60;
        buttons.add(new SoundButton(soundButtonX, soundButtonY, this));

        // ปุ่มคำอธิบายทักษะ - ตรงกลางล่าง
        int skillButtonX = WIDTH/2 - 60;
        int skillButtonY = HEIGHT - 80;
        buttons.add(new SkillButton(skillButtonX, skillButtonY, this));

        // เริ่มเล่นเพลง
        loadAndPlayMusic();
    }
    private void loadAndPlayMusic() {
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("resources/sounds/Menu song update.wav"));
            menuMusic = AudioSystem.getClip();
            menuMusic.open(audioInput);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
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
        // วาดพื้นหลัง
        g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
        
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
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    // เมื่อออกจากเมนู ควรหยุดเพลง
    public void cleanup() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic.close();
        }
    }
}
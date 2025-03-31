
import java.awt.event.*;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

    private final GamePanel gamePanel;
    private boolean up, down, left, right; // เพิ่มตัวแปรที่ขาดหายไป
    private int mouseX, mouseY;
    private boolean isLeftHold = false;
    private long lastShootTime = 0;

    public InputHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // ตรวจสอบว่าเกมจบแล้วหรือยัง
        boolean isGameOver = gamePanel.isGameOver();

        // การตรวจจับปุ่มสำหรับเคลื่อนที่
        if (key == KeyEvent.VK_W) {
            up = true;
        }
        if (key == KeyEvent.VK_S) {
            down = true;
        }
        if (key == KeyEvent.VK_A) {
            left = true;
        }
        if (key == KeyEvent.VK_D) {
            right = true;
        }
        if (key >= KeyEvent.VK_1 && key <= KeyEvent.VK_9) {
            int weaponIndex = key - KeyEvent.VK_1;
            gamePanel.selectWeapon(weaponIndex);
        }
        if (key == KeyEvent.VK_I && !isGameOver) {
            Player player = gamePanel.getPlayer();
            player.toggleImmortalMode();
        // เล่นเสียงเมื่อเปิด/ปิดโหมดอมตะ (ถ้ามี)
            SoundManager.playSound("get_skill");
        }

        // กดปุ่ม P หรือ ESC เพื่อพักเกม (ทำงานเมื่อเกมไม่จบเท่านั้น)
        if ((key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) && !isGameOver) {
            gamePanel.togglePause();
        }
        updatePlayerMovement();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) {
            up = false;
        }
        if (key == KeyEvent.VK_S) {
            down = false;
        }
        if (key == KeyEvent.VK_A) {
            left = false;
        }
        if (key == KeyEvent.VK_D) {
            right = false;
        }

        updatePlayerMovement();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isLeftHold = true;
        }
    }

    // แก้ไขเมธอด handleShooting ให้สมบูรณ์
    public void handleShooting() {
        if (isLeftHold) {
            long currentTime = System.currentTimeMillis();
            long playerCooldown = gamePanel.getPlayer().getShootCooldown();
            long timeSinceLastShot = currentTime - lastShootTime;

            // ตรวจสอบว่าถึงเวลายิงหรือยัง
            if (timeSinceLastShot >= playerCooldown) {
                System.out.println("ยิงที่พิกัด: " + mouseX + ", " + mouseY);

                // ส่งพิกัดเมาส์ที่แท้จริงไปให้ gamePanel (ไม่ต้องแปลงที่นี่)
                gamePanel.playerShoot(mouseX, mouseY);
                lastShootTime = currentTime;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            isLeftHold = false;
            // เพิ่มบรรทัดนี้เพื่อส่งการคลิกไปที่ GamePanel
            gamePanel.handleMouseClick(e.getX(), e.getY());
        }
    }

    public boolean isLeftButisDown() {
        return isLeftHold;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private void updatePlayerMovement() {
        int dx = 0;
        int dy = 0;

        if (up) {
            dy -= 1;
        }
        if (down) {
            dy += 1;
        }
        if (left) {
            dx -= 1;
        }
        if (right) {
            dx += 1;
        }

        gamePanel.movePlayer(dx, dy);
    }

    public void resetAllInputs() {
        // รีเซ็ตสถานะการกดปุ่มทั้งหมด
        up = false;
        down = false;
        left = false;
        right = false;
        isLeftHold = false;

        // อัพเดตการเคลื่อนที่ผู้เล่นให้หยุด
        updatePlayerMovement();

        System.out.println("รีเซ็ตการควบคุมทั้งหมดแล้ว");
    }
}

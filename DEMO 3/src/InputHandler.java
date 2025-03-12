import java.awt.event.*;

/**
 * InputHandler รับผิดชอบการจัดการอินพุตทั้งหมดจากผู้เล่น
 * เป็นคลาสที่แยกออกมาเพื่อให้โค้ดเป็นระเบียบและง่ายต่อการบำรุงรักษา
 */
public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

    private GamePanel gamePanel;
    private int mouseX, mouseY;

    /**
     * สร้าง InputHandler ใหม่
     * @param gamePanel GamePanel หลักที่จะส่งอินพุตไปให้
     */
    public InputHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        gamePanel.handleKeyPressed(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        gamePanel.handleKeyReleased(key);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ไม่ได้ใช้ แต่ต้องมีเพราะ implements KeyListener
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            gamePanel.handleMousePressed(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // ไม่ได้ใช้ แต่ต้องมีเพราะ implements MouseListener
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // ไม่ได้ใช้ แต่ต้องมีเพราะ implements MouseListener
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // ไม่ได้ใช้ แต่ต้องมีเพราะ implements MouseListener
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // ไม่ได้ใช้ แต่ต้องมีเพราะ implements MouseListener
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        
        // ถ้าลากเมาส์ไปพร้อมกดปุ่มซ้ายอยู่ ก็ให้ยิงต่อเนื่อง
        if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            gamePanel.handleMousePressed(mouseX, mouseY);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
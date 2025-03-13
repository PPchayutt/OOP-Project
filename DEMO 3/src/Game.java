import javax.swing.*;

public class Game {
    private JFrame window;
    private GamePanel gamePanel;
    
    public Game() {
        // สร้างหน้าต่างหลัก
        window = new JFrame("2D Bullet Hell Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        try {
            // สร้าง Game Panel
            gamePanel = new GamePanel();
            
            // เรียกใช้ setupGame เพื่อเตรียมพร้อมสำหรับเกม
            gamePanel.setupGame();
            
            // เพิ่ม Panel เข้าไปในหน้าต่าง
            window.add(gamePanel);
            
            // ปรับขนาดหน้าต่างให้พอดีกับ Panel
            window.pack();
            
            // จัดตำแหน่งหน้าต่างให้อยู่กลางจอ
            window.setLocationRelativeTo(null);
            
            // แสดงหน้าต่าง
            window.setVisible(true);
            
            // ทำให้ Panel ได้รับ focus
            gamePanel.requestFocus();
            
            // เริ่มการทำงานของเกมลูป
            gamePanel.startGameLoop();
            
            System.out.println("เกมเริ่มทำงานเรียบร้อยแล้ว");
        } catch (Exception e) {
            System.err.println("เกิดข้อผิดพลาดในการเริ่มเกม: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // เรียกใช้ SwingUtilities เพื่อสร้าง GUI ใน Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                new Game();
            } catch (Exception e) {
                System.err.println("เกิดข้อผิดพลาดในการสร้างเกม: " + e.getMessage());
                e.printStackTrace();
                
                // แสดงข้อความแจ้งเตือน
                JOptionPane.showMessageDialog(null, 
                    "เกิดข้อผิดพลาดในการเริ่มเกม: " + e.getMessage(), 
                    "ข้อผิดพลาด", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
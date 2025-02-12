/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        // ใช้ SwingUtilities เพื่อแน่ใจว่า GUI จะถูกสร้างใน Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // สร้าง frame และ panel
                JFrame frame = new JFrame("Bird Eye View Shooter");
                GamePanel gamePanel = new GamePanel();
                
                // ตั้งค่าพื้นฐานฟ
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(true);
                
                // เพิ่ม panel เข้าไปใน frame
                frame.add(gamePanel);
                
                // จัดการขนาดและตำแหน่ง
                frame.pack();
                frame.setSize(GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT);
                frame.setLocationRelativeTo(null);
                
                // แสดง frame
                frame.setVisible(true);
                
                // รอให้ frame พร้อมแล้วค่อยเริ่มเกม
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        gamePanel.setupGame();
                        gamePanel.requestFocus();
                        gamePanel.startGame();
                    }
                });
                
                // จัดการ focus เมื่อมีการ resize
                frame.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        gamePanel.requestFocus();
                    }
                });
                
                // จัดการปิดเกมอย่างถูกต้อง
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        gamePanel.stopGame();
                    }
                });
            }
        });
    }
}
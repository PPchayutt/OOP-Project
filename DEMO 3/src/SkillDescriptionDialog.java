import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * หน้าต่างแสดงคำอธิบายสกิลทั้งหมดในเกม
 */
public class SkillDescriptionDialog extends JDialog {

    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private JPanel mainPanel;
    private JScrollPane scrollPane;

    /**
     * สร้างหน้าต่างคำอธิบายสกิล
     *
     * @param parent คอมโพเนนต์หลักที่เรียกใช้หน้าต่างนี้
     */
    public SkillDescriptionDialog(Component parent) {
        // หา Frame หรือ Dialog parent โดยปลอดภัย
        Frame frame = null;
        Dialog dialog = null;
        
        // พยายามหา parent frame
        Component root = SwingUtilities.getRoot(parent);
        if (root instanceof Frame) {
            frame = (Frame) root;
        } else if (root instanceof Dialog) {
            dialog = (Dialog) root;
        }
        
        // เลือกเรียกคอนสตรักเตอร์ที่เหมาะสม
        if (frame != null) {
            this.setTitle("Skills and Power-ups Description");
            this.setModal(true);
            this.setSize(WIDTH, HEIGHT);
            this.setLocationRelativeTo(frame);
        } else {
            this.setTitle("Skills and Power-ups Description");
            this.setModal(true);
            this.setSize(WIDTH, HEIGHT);
            this.setLocationRelativeTo(null);
        }
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // สร้างคอมโพเนนต์ภายในหน้าต่าง
        initComponents();
    }

    /**
     * สร้างคอมโพเนนต์ภายใน
     */
    private void initComponents() {
        // สร้างพาเนลหลัก
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(30, 30, 50)); // พื้นหลังสีเข้ม
        
        // เพิ่มส่วนหัว
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel);
        
        // เพิ่มเส้นคั่น
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(100, 100, 200));
        separator.setMaximumSize(new Dimension(WIDTH, 2));
        mainPanel.add(separator);
        
        // เพิ่มคำอธิบายสกิลเป็นหมวดหมู่
        // เพิ่มคำอธิบายสกิลเป็นหมวดหมู่
        addCategoryHeader(mainPanel, "Crazy buffs", new Color(255, 50, 50));
        addSkillDescription(mainPanel, "crazy_shooting", "Rambo", "Mad Firing", "Shoots multiple bullets in all directions. Increases damage by 2x.");
        addSkillDescription(mainPanel, "stop_time", "Za Warudo!", "Time Freeze", "Freezes monsters and bosses temporarily.");
        
        addCategoryHeader(mainPanel, "Permanent Buffs", new Color(128, 0, 128));
        addSkillDescription(mainPanel, "increase_bullet_damage", "Damage+", "Damage Up", "Increases bullet damage permanently.");
        addSkillDescription(mainPanel, "increase_movement_speed", "Speed+", "Move Faster", "Increases movement speed permanently.");
        addSkillDescription(mainPanel, "increase_shooting_speed", "Fire Rate+", "Rapid Fire", "Reduces cooldown between shots permanently.");
        addSkillDescription(mainPanel, "knockback", "Knockback", "Bullet Push", "Bullets push enemies on hit permanently.");
        addSkillDescription(mainPanel, "plus_more_heart", "Extra Heart", "More Life", "Adds one more life to your character.");
        addSkillDescription(mainPanel, "shoot_multiple_bullets", "Multi Shot", "Multiple Bullets", "Shoots multiple bullets at once permanently.");
        
        addCategoryHeader(mainPanel, "Temporary Buffs", new Color(255, 140, 0));
        addSkillDescription(mainPanel, "increase_bullet_damage(temp)", "Damage Boost", "Power Surge", "Increases bullet damage more than permanent buff but for limited time.");
        addSkillDescription(mainPanel, "increase_movement_speed(temp)", "Speed Boost", "Sprint", "Increases movement speed more than permanent buff but for limited time.");
        addSkillDescription(mainPanel, "increase_shooting_speed(temp)", "Rapid Fire", "Machine Gun", "Reduces cooldown between shots more than permanent buff but for limited time.");
        addSkillDescription(mainPanel, "knockback(temp)", "Super Knockback", "Strong Push", "Pushes enemies harder than permanent buff but for limited time.");
        addSkillDescription(mainPanel, "fires_multiple_bullets(temp)", "Burst Shot", "Bullet Spread", "Shoots more bullets than permanent buff but for limited time.");
        addSkillDescription(mainPanel, "healing", "Healing", "Health Restore", "Instantly restores health.");
        
        // เพิ่มปุ่มปิด
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBackground(new Color(60, 60, 100));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(closeButton);
        
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // สร้าง scroll pane สำหรับรองรับเนื้อหามากๆ
        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // เพิ่มลงในหน้าต่าง
        getContentPane().add(scrollPane);
    }

    /**
     * สร้างพาเนลส่วนหัว
     * 
     * @return JPanel พาเนลส่วนหัว
     */
     private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Skills and Power-ups Description");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Collect buffs to strengthen your character");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        
        return headerPanel;
    }

    /**
     * เพิ่มหัวข้อหมวดหมู่บัฟ
     * 
     * @param container คอนเทนเนอร์ที่จะเพิ่มหัวข้อ
     * @param title หัวข้อหมวดหมู่
     * @param color สีของหัวข้อ
     */
    private void addCategoryHeader(JPanel container, String title, Color color) {
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.setOpaque(false);
        categoryPanel.setMaximumSize(new Dimension(WIDTH, 50));
        
        JLabel categoryLabel = new JLabel(title);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 18));
        categoryLabel.setForeground(color);
        
        categoryPanel.add(Box.createHorizontalStrut(20));
        categoryPanel.add(categoryLabel);
        
        container.add(Box.createVerticalStrut(10));
        container.add(categoryPanel);
    }

    /**
     * เพิ่มคำอธิบายสกิล
     * 
     * @param container คอนเทนเนอร์ที่จะเพิ่มคำอธิบาย
     * @param imageName ชื่อไฟล์รูปภาพ (ไม่รวมพาธ)
     * @param nameEN ชื่อสกิลภาษาอังกฤษ
     * @param nameTH ชื่อสกิลภาษาไทย
     * @param description คำอธิบายสกิล
     */
    private void addSkillDescription(JPanel container, String imageName, String nameEN, String nameTH, String description) {
        JPanel skillPanel = new JPanel();
        skillPanel.setLayout(new BoxLayout(skillPanel, BoxLayout.X_AXIS));
        skillPanel.setOpaque(false);
        skillPanel.setMaximumSize(new Dimension(WIDTH - 40, 80));
        skillPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // โหลดรูปภาพสกิล
        ImageIcon icon = loadSkillIcon(imageName);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setPreferredSize(new Dimension(60, 60));
        iconLabel.setMaximumSize(new Dimension(60, 60));
        
        // สร้างพาเนลสำหรับข้อความ
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        // ชื่อสกิล
        JLabel titleLabel = new JLabel(nameEN + " (" + nameTH + ")");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // คำอธิบาย
        JLabel descLabel = new JLabel("<html><div style='width: 500px;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        
        // เพิ่มคอมโพเนนต์ทั้งหมดลงในพาเนลสกิล
        skillPanel.add(iconLabel);
        skillPanel.add(Box.createHorizontalStrut(15));
        skillPanel.add(textPanel);
        
        // เพิ่มพาเนลสกิลลงในคอนเทนเนอร์
        container.add(skillPanel);
    }

    /**
     * โหลดไอคอนของสกิล
     * 
     * @param imageName ชื่อไฟล์ไม่รวมพาธ
     * @return ImageIcon ไอคอนของสกิล
     */
    private ImageIcon loadSkillIcon(String imageName) {
        // พยายามโหลดจากไฟล์
        String imagePath = "resources/images/" + imageName + ".png";
        ImageIcon icon = new ImageIcon(imagePath);
        
        // ถ้าไม่สามารถโหลดไฟล์ได้ ให้สร้างไอคอนเองตามประเภท
        if (icon.getIconWidth() <= 0) {
            // สร้างรูปภาพว่างเปล่า
            BufferedImage bufferedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            
            // วาดพื้นหลังวงกลม
            if (imageName.contains("crazy")) {
                g2d.setColor(new Color(200, 50, 50));
            } else if (imageName.contains("temp")) {
                g2d.setColor(new Color(200, 130, 50));
            } else {
                g2d.setColor(new Color(130, 50, 200));
            }
            g2d.fillOval(0, 0, 50, 50);
            
            // วาดเส้นขอบ
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(0, 0, 49, 49);
            
            // เติมลักษณะเฉพาะตามชื่อไฟล์
            g2d.setColor(Color.WHITE);
            if (imageName.contains("damage")) {
                drawSymbol(g2d, "D");
            } else if (imageName.contains("speed")) {
                drawSymbol(g2d, "S");
            } else if (imageName.contains("shooting")) {
                drawSymbol(g2d, "F");
            } else if (imageName.contains("knock")) {
                drawSymbol(g2d, "K");
            } else if (imageName.contains("heart") || imageName.contains("healing")) {
                drawSymbol(g2d, "H");
            } else if (imageName.contains("bullet")) {
                drawSymbol(g2d, "M");
            } else if (imageName.contains("crazy")) {
                drawSymbol(g2d, "C");
            } else if (imageName.contains("time")) {
                drawSymbol(g2d, "T");
            }
            
            g2d.dispose();
            
            icon = new ImageIcon(bufferedImage);
        }
        
        return icon;
    }
    
    /**
     * วาดสัญลักษณ์ลงบนกราฟิกส์
     * 
     * @param g2d กราฟิกส์ที่จะวาด
     * @param symbol สัญลักษณ์ที่จะวาด
     */
    private void drawSymbol(Graphics2D g2d, String symbol) {
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (50 - metrics.stringWidth(symbol)) / 2;
        int y = ((50 - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(symbol, x, y);
    }
}
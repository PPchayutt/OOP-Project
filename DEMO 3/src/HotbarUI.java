import java.awt.*;
import java.util.*;

public class HotbarUI {
     // ขนาดเดิม
    private final int original_width = 240;
    private final int original_height = 40;
    // ตัวคูณขยายขนาด 
    private final double multiplier = 1.7; // << ถ้าจะปรับขนาดให้ปรับตรงนี้
    // ขนาดบนจอ
    private final int width = (int)(original_width * multiplier);
    private final int height = (int)(original_height * multiplier);
    // ตำแหน่งบนจอ (กลาง ล่าง)
    private final int x = GamePanel.WIDTH / 2 - (width / 2);
    private final int y = GamePanel.HEIGHT - (height + (int)(25 * multiplier));
    private final int MAX_SLOTS = 8;
    // ขนาดช่อง สำหรับ class HotbarSlot
    private final int margin_x = (int)(10 * multiplier); // เว้นขอบซ้าย/ขวา
    private final int margin_y = (int)(7 * multiplier);  // เว้นขอบบน/ล่าง
    private final int padding = (int)(2 * multiplier);
    private final int total_padding = padding * (MAX_SLOTS - 1);
    private final int slot_width = (int)((width - (margin_x * 2) - total_padding) / MAX_SLOTS);
    private final int slot_height = (int)(height - (margin_y * 2));
    
    private final WeaponManager weaponManager;
    private final ArrayList slots;
    private final Image hotbarImage;
    
    private boolean debugging = false;
    
    private int mouseX, mouseY;
    private String tooltipText = null;
    
    public HotbarUI(Player player) {
        this.slots = new ArrayList();
        this.hotbarImage = ImageManager.getImage("hotbar");
        this.weaponManager = GamePanel.getWeaponManager();
        
        // สร้างช่อง Hotbar
        updateSlots();
        
        //debug();
    }
    
     public void updateSlots() {
        slots.clear();
        Map<Integer, WeaponType> weapons = weaponManager.getAvailableWeapon();
        WeaponType activeWeaponType = weaponManager.getActiveWeaponType();

        int startX = x + margin_x;
        int startY = y + margin_y;
        
        for (int i = 0; i < weapons.size(); i++) {
            int slotX = startX + i * (slot_width + padding);
            int slotY = startY;
            WeaponType weaponType = (WeaponType) weapons.get(i);
            HotbarSlot slot = new HotbarSlot(slotX, slotY, weaponType, slot_width, slot_height, multiplier);
            slot.setActive(activeWeaponType == weaponType) ;
            // กำหนดเวลาที่เหลือสำหรับทุกอาวุธ
            float lifespan = weaponManager.getWeaponLifespanPercentage(weaponType);
            System.out.println(weaponType.getName() + " lifespan: " + lifespan * 100 + "%");
            slot.setLifespan(lifespan);
            slots.add(slot);
        }
       
    }
    
    public void render(Graphics g) {
        // วาดพื้นหลังของ Hotbar
        if (hotbarImage != null) {
            g.drawImage(hotbarImage, x, y,  width, height, null);
        }
        
        WeaponType activeWeaponType = weaponManager.getActiveWeaponType();
        
        if (debugging) {
            // ตีกรอบไว้ดูระยะ hotbar ตอน debug
            g.setColor(Color.RED);
            g.drawRect(x, y, width, height);
            g.setColor(Color.GREEN);
            g.drawLine(GamePanel.WIDTH / 2, y, GamePanel.WIDTH / 2, y + height); // แสดงจุดกึ่งกลางหน้าจอ
        }
        
        // อัปเดตสถานะ isActive ของทุก slot ตาม activeWeaponType
        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = (HotbarSlot) slots.get(i);
            WeaponType slotType = slot.getWeaponType();
            slot.setActive(slotType == activeWeaponType);
        }
        
        // วาดแต่ละช่อง
        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = (HotbarSlot) slots.get(i);
            WeaponType type = slot.getWeaponType();
            int amount = weaponManager.getWeaponCount(type);
            slot.render(g, amount);
        }
        Map<Integer, WeaponType> weapons = weaponManager.getAvailableWeapon();
        for (int i = 0; i < slots.size(); i++) {
            HotbarSlot slot = (HotbarSlot) slots.get(i);
            WeaponType type = slot.getWeaponType();

            // เฉพาะอาวุธที่ไม่ใช่ถาวรและไม่ใช่ป้อมปืน
            if (type != WeaponType.TURRET && type != WeaponType.AK47) {
                float lifespan = weaponManager.getWeaponLifespanPercentage(type);
                System.out.println("อาวุธ " + type.getName() + " lifespan: " + lifespan);

                if (lifespan < 1.0f) {
                    // วาดแถบเวลาด้านล่าง slot
                    int barWidth = (int)(slot_width * 0.8);
                    int barHeight = 5;
                    int barX = slot.getX() + (slot_width - barWidth) / 2;
                    int barY = slot.getY() + slot_height + 3;

                    // พื้นหลัง
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(barX, barY, barWidth, barHeight);

                    // แถบเวลา
                    g.setColor(Color.RED);
                    int remainingWidth = (int)(barWidth * lifespan);
                    g.fillRect(barX, barY, remainingWidth, barHeight);

                    // ขอบ
                    g.setColor(Color.WHITE);
                    g.drawRect(barX, barY, barWidth, barHeight);
                }
            }
        }
    }
    
//    public WeaponType getWeaponTypeAtPosition(int mouseX, int mouseY) {
//        for (int i = 0; i < slots.size(); i++) {
//            HotbarSlot slot = (HotbarSlot) slots.get(i);
//            if (slot.isClicked(mouseX, mouseY)) {
//                return slot.getWeaponType();
//            }
//        }
//        return null;
//    }
    
    public void updateSelection(int selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < slots.size()) {
            for (int i = 0; i < slots.size(); i++) {
                HotbarSlot slot = (HotbarSlot) slots.get(i); // ต้อง cast ชนิดข้อมูล
                slot.setActive(i == selectedIndex);
            }
        }
    }
    
    public double getMultiplier() {
        return multiplier;
    }
    
    private void debug() {
        System.out.println("width : " + width + " , height : " + height);
        System.out.println("slot_height : " + slot_height + " , slot_width : " + slot_width);
        System.out.println("margin_x : " + margin_x + " , margin_y : " + margin_y);
        System.out.println("padding : " + padding);
        debugging = true;
    }
    
}
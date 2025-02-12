/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.awt.Color;
import java.awt.Graphics;

public class Bullet extends Entity {
    private boolean active = true;
    private float speed;
    private int damage;
    private Color color;
    
    public enum BulletType {
        NORMAL(10f, 25, Color.YELLOW),
        RAPID(8f, 15, Color.ORANGE),
        STRONG(7f, 50, Color.RED);
        
        private final float speed;
        private final int damage;
        private final Color color;
        
        BulletType(float speed, int damage, Color color) {
            this.speed = speed;
            this.damage = damage;
            this.color = color;
        }
    }
    
    public Bullet(float startX, float startY, float targetX, float targetY, BulletType type) {
        super(startX, startY);
        this.width = 15;
        this.height = 15;
        this.speed = type.speed;
        this.damage = type.damage;
        this.color = type.color;
        
        // Calculate direction
        float dx = targetX - startX;
        float dy = targetY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            velX = (dx / distance) * speed;
            velY = (dy / distance) * speed;
        }
    }
    
    public int getDamage() {
        return damage;
    }
    
    @Override
    public void update() {
        x += velX;
        y += velY;
        
        // เช็คขอบจอด้วยค่าที่ใหญ่พอ
        if (x < -100 || x > 2000 || y < -100 || y > 2000) {
            active = false;
        }
    }
    
    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.fillOval((int)x, (int)y, width, height);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void deactivate() {
        active = false;
    }
}
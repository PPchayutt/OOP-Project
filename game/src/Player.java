/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    private int health;
    private int maxHealth = 1000;
    private List<Bullet> bullets;
    private long lastShotTime = 0;
    private long shootCooldown = 200; // ปกติยิงทุก 0.2 วินาที
    private Bullet.BulletType currentBulletType = Bullet.BulletType.NORMAL;
    private GamePanel gamePanel;
    // PowerUp timers
    private long rapidFireTimer = 0;
    private long strongBulletTimer = 0;
    private static final long POWERUP_DURATION = 5000; // 5 วินาที
    
    private float acceleration = 0.5f;
    private float deceleration = 0.3f;
    private float maxSpeed = 4.0f;
    private float targetVelX = 0;
    private float targetVelY = 0;
    
    private boolean isPressingLeft = false;
    private boolean isPressingRight = false;
    private boolean isPressingUp = false;
    private boolean isPressingDown = false;
    
    public void setPressingLeft(boolean pressing) {
        isPressingLeft = pressing;
        updateMovement();
    }
            
    public void setPressingRight(boolean pressing) {
        isPressingRight = pressing;
        updateMovement();
    }
    
    public void setPressingUp(boolean pressing) {
        isPressingUp = pressing;
        updateMovement();
    }
    
    public void setPressingDown(boolean pressing) {
        isPressingDown = pressing;
        updateMovement();
    }
    
    private void updateMovement() {
        // จัดการแกน X
        if (isPressingLeft && !isPressingRight) {
            targetVelX = -maxSpeed;
        } else if (isPressingRight && !isPressingLeft) {
            targetVelX = maxSpeed;
        } else {
            targetVelX = 0;
        }
        
        // จัดการแกน Y
        if (isPressingUp && !isPressingDown) {
            targetVelY = -maxSpeed;
        } else if (isPressingDown && !isPressingUp) {
            targetVelY = maxSpeed;
        } else {
            targetVelY = 0;
        }
    }
        
    public Player(float x, float y, GamePanel gamePanel) {
        super(x, y);
        this.gamePanel = gamePanel;
        this.width = 25;
        this.height = 25;
        health = maxHealth;
        bullets = new ArrayList<>();
    }
    public List<Bullet> getBullets() {
        return bullets;
    }
    
    public void damage(int amount) {
        health -= amount;
        if(health < 0) health = 0;
    }
    
    public int getHealth() {
        return health;
    }
    
    public boolean isDead() {
        return health <= 0;
    }
    public float getTargetVelX() {
        return targetVelX;
    }

    public float getTargetVelY() {
        return targetVelY;
    }

    @Override
    public void update() {
        // อัพเดทความเร็วแบบค่อยๆ เปลี่ยน
        if (targetVelX > velX) {
            velX = Math.min(targetVelX, velX + acceleration);
        } else if (targetVelX < velX) {
            velX = Math.max(targetVelX, velX - deceleration);
        }
        
        if (targetVelY > velY) {
            velY = Math.min(targetVelY, velY + acceleration);
        } else if (targetVelY < velY) {
            velY = Math.max(targetVelY, velY - deceleration);
        }

        // อัพเดทตำแหน่ง
        x += velX;
        y += velY;

        // เช็คขอบจอ
        x = Math.max(0, Math.min(x, gamePanel.getGameWidth() - width));
        y = Math.max(0, Math.min(y, gamePanel.getGameHeight() - height));

        // อัพเดทกระสุน
        bullets.forEach(Bullet::update);
        bullets.removeIf(bullet -> !bullet.isActive());

        // อัพเดท PowerUp timers
        long currentTime = System.currentTimeMillis();
        if (rapidFireTimer > 0 && currentTime > rapidFireTimer) {
            rapidFireTimer = 0;
            shootCooldown = 200; // กลับสู่ความเร็วปกติ
        }
        if (strongBulletTimer > 0 && currentTime > strongBulletTimer) {
            strongBulletTimer = 0;
            currentBulletType = Bullet.BulletType.NORMAL;
        }
    }

    @Override
    public void render(Graphics g) {
            // วาดตัวละคร
        g.setColor(Color.BLUE);
        g.fillRect((int)x, (int)y, width, height);

        // วาด HP bar
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y - 5, width, 3);
        g.setColor(Color.GREEN);
        int currentHPWidth = (int)((health / (float)maxHealth) * width);
        g.fillRect((int)x, (int)y - 5, currentHPWidth, 3);

        // เพิ่มแถบรีโหลด
        long currentTime = System.currentTimeMillis();
        long timeSinceLastShot = currentTime - lastShotTime;
        float reloadProgress = Math.min(1.0f, timeSinceLastShot / (float)shootCooldown);

        // วาดแถบรีโหลดใต้ตัวละคร
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)x, (int)y + height + 2, width, 2);
        g.setColor(Color.YELLOW);
        g.fillRect((int)x, (int)y + height + 2, (int)(width * reloadProgress), 2);

        // วาดกระสุน
        ArrayList<Bullet> bulletsCopy = new ArrayList<>(bullets);
        for(Bullet bullet : bulletsCopy) {
            bullet.render(g);
    }

        // แสดงสถานะ PowerUp
        if (rapidFireTimer > 0 || strongBulletTimer > 0) {
            g.setFont(new Font("Arial", Font.BOLD, 10));
            int statusY = (int)y - 10;
            if (rapidFireTimer > 0) {
                g.setColor(Color.YELLOW);
                g.drawString("RF", (int)x, statusY);
            }
            if (strongBulletTimer > 0) {
                g.setColor(Color.RED);
                g.drawString("SB", (int)x + width - 15, statusY);
            }
        }
    }
    
    public void shoot(float targetX, float targetY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= shootCooldown) {
            float centerX = x + width/2;
            float centerY = y + height/2;
            bullets.add(new Bullet(centerX, centerY, targetX, targetY, currentBulletType));
            lastShotTime = currentTime;
        }
    }
    
    public long getLastShotTime() {
        return lastShotTime;
    }

    public long getShootCooldown() {
        return shootCooldown;
    }

    public void applyPowerUp(PowerUp powerUp) {
        long currentTime = System.currentTimeMillis();
        switch(powerUp.getType()) {
            case RAPID_FIRE:
                shootCooldown = 50; // ยิงทุก 0.05 วินาที
                rapidFireTimer = currentTime + POWERUP_DURATION;
                break;
            case STRONG_BULLET:
                currentBulletType = Bullet.BulletType.STRONG;
                strongBulletTimer = currentTime + POWERUP_DURATION;
                break;
            case HEALTH:
                health = Math.min(maxHealth, health + 50);
                break;
        }
    }
    
    public void setVelX(float targetVelX) {
        this.targetVelX = Math.max(-maxSpeed, Math.min(maxSpeed, targetVelX));
    }

    public void setVelY(float targetVelY) {
        this.targetVelY = Math.max(-maxSpeed, Math.min(maxSpeed, targetVelY));
    }
}
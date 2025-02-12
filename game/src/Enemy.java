/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.Color;
import java.awt.Graphics;

public class Enemy extends Entity {
    private int health;
    private float speed;
    private Player target;
    private int attackDamage;
    private long lastAttackTime = 0;
    private long attackCooldown = 500;
    private int difficulty;
    
    public Enemy(float x, float y, Player target, int difficulty) {
        super(x, y);
        this.width = 20;
        this.height = 20;
        this.target = target;
        this.difficulty = difficulty;
        
        // Scale enemy stats based on difficulty
        this.speed = 0.5f + (difficulty * 0.2f);
        this.health = 50 + (difficulty * 20);
        this.attackDamage = 10 + (difficulty * 2);
    }
    
    @Override
    public void update() {
        float dx = target.getX() - x;
        float dy = target.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            velX = (dx / distance) * speed;
            velY = (dy / distance) * speed;
        }
        
        x += velX;
        y += velY;
    }
    
    @Override
    public void render(Graphics g) {
        // เปลี่ยนสีศัตรูตามระดับความยาก
        g.setColor(difficulty == 1 ? Color.RED : 
                   difficulty == 2 ? Color.ORANGE : 
                   Color.DARK_GRAY);
        g.fillRect((int)x, (int)y, width, height);
        
        g.setColor(Color.RED);
        g.fillRect((int)x, (int)y - 5, width, 3);
        g.setColor(Color.GREEN);
        int currentHPWidth = (int)((health / (float)(100 + (difficulty * 20))) * width);
        g.fillRect((int)x, (int)y - 5, currentHPWidth, 3);
    }
    
    public void attackPlayer() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= attackCooldown) {
            target.damage(attackDamage);
            lastAttackTime = currentTime;
        }
    }
    
    public void damage(int amount) {
        health -= amount;
        if(health < 0) health = 0;
    }
    
    public boolean isDead() {
        return health <= 0;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
}
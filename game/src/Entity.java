/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.Rectangle;

public abstract class Entity implements GameObject {
    protected float x, y;
    protected float velX, velY;
    protected int width, height;
    
    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
import java.awt.Graphics;
import java.awt.Rectangle;

public interface GameObject {
    void update();
    void render(Graphics g);
    Rectangle getBounds();
}
package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Pixel {
    
    private int x, y;
    private int size;
    private Color color;
    
    public Pixel(int size) {
        this.size = size;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Color getColor(){
        return color;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    public Rectangle getBound() {
        return new Rectangle(x, y, size, size);
    }
    
    public boolean isCollision(Pixel o) {
        if(o == this) return false;
        return getBound().intersects(o.getBound());
    }
    
    public void render(Graphics2D g2d) {
        g2d.fillRect(x + 1, y + 1, size - 2, size - 2);
    }
}

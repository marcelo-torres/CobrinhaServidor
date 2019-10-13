package model;

import java.awt.Rectangle;

public class Posicao {
    
    private int x, y;
    private final int SIZE;
    
    public Posicao(int size) {
        this.SIZE = size;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
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

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }
    
    public Rectangle getBound() {
        return new Rectangle(x, y, SIZE, SIZE);
    }
    
    public boolean isCollision(Posicao o) {
        if(o == this) return false;
        return getBound().intersects(o.getBound());
    }
    
}

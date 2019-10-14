package model.send;

public class Alimento extends EntidadePosicionavel{
    
    protected char tipo;
    
    public int getX(){
        return vertices.get(0)[0];
    }

    public int getY(){
        return vertices.get(0)[1];
    }
}
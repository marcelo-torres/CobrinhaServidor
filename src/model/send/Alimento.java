package model.send;

import java.io.Serializable;

public class Alimento extends EntidadePosicionavel implements Serializable {
    
    protected char tipo;
    
    public int getX(){
        return vertices.get(0)[0];
    }

    public int getY(){
        return vertices.get(0)[1];
    }
}
package model.send;

import java.io.Serializable;

public class AlimentoBanana extends Alimento implements Serializable{
    
    public AlimentoBanana(int x, int y){
        tipo = 'B';
        setCor('y');
        insertPosicao(x, y);
    }
    
}

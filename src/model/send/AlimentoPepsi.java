package model.send;

import java.io.Serializable;

public class AlimentoPepsi extends Alimento implements Serializable {
    
    public AlimentoPepsi(int x, int y){
        tipo = 'C';
        setCor('b');
        insertPosicao(x, y);
    }
    
}

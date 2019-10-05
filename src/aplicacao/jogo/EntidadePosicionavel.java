package jogo;

import java.awt.Color;
import java.util.ArrayList;

public class EntidadePosicionavel {
    protected ArrayList<Integer[]> vertices = new ArrayList<>();
    protected Color cor;
    
    protected ArrayList getPosicoes(){
        return vertices;
    }
    
    protected void setCor(char c){
        switch(c){
            case 'b':
                cor = new Color(23, 124, 255);
                break;
            case 'y':
                cor =  Color.YELLOW;
                break;
            case 'g':
                cor = Color.GREEN;
                break;
            case 'p':
                cor = Color.PINK;
                break;
            default:
                break;
        }
    }
    
    protected Color getCor(){
        return cor;
    }
    
    protected void insertPosicao(int x, int y){
        vertices.add(new Integer[]{x, y});
    }
    
    protected void apagaPosicoes(){
        vertices.clear();
    }
}

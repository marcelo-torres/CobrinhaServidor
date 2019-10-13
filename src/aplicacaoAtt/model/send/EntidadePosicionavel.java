package model.send;

import java.awt.Color;
import java.util.ArrayList;

public class EntidadePosicionavel {
    protected ArrayList<Integer[]> vertices = new ArrayList<>();
    protected Color cor;
    
    public ArrayList<Integer[]> getPosicoes(){
        return vertices;
    }
    
    public void setCor(char c){
        switch(c){
            case 'b':
                cor = new Color(81, 100, 184);
                break;
            case 'y':
                cor = new Color(218, 230, 67);
                break;
            case 'g':
                cor = new Color(120, 255, 120);
                break;
            case 'p':
                cor = new Color(255, 120, 151);
                break;
            default:
                break;
        }
    }
    
    public Color getCor(){
        return cor;
    }
    
    public void insertPosicao(int x, int y){
        vertices.add(new Integer[]{x, y});
    }
    
    public void apagaPosicoes(){
        vertices.clear();
    }
}

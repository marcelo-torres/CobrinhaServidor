package model.send;

public class Cobra extends EntidadePosicionavel{
    
    public Cobra(char pos, int altura, int largura, int tamanho){
        if(pos == 'e'){
            insertPosicao((largura/4)-tamanho, (altura/2)-tamanho);           
            Integer[] cabeca = vertices.get(0);
            for(int i = 1; i < 15; i++){
                insertPosicao(cabeca[0], cabeca[1] + (i * tamanho));
            }
            setCor('g');
        }
        else{
            insertPosicao(largura-(largura/4)-tamanho, (altura/2)-tamanho);        
            Integer[] cabeca = vertices.get(0);
            for(int i = 1; i < 15; i++){
                insertPosicao(cabeca[0], cabeca[1] + (i * tamanho));
            }
            setCor('p');
        }
    }
}
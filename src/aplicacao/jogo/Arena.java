package jogo;

import java.util.ArrayList;
import java.util.Random;

public class Arena {
    private int altura;
    private int largura;
    private int tamanho;
    private EntidadePosicionavel cobra1;
    private EntidadePosicionavel cobra2;
    private ArrayList<EntidadePosicionavel> alimentos;
    
    private Random randomAlimento;
    
    public Arena(){
    }
    
    public Arena(EntidadePosicionavel c1, EntidadePosicionavel c2, int alt, int lar, int tam){
        cobra1 = c1;
        cobra2 = c2;
        altura = alt;
        largura = lar;
        tamanho = tam;
        alimentos = new ArrayList<EntidadePosicionavel>();
        randomAlimento = new Random();
        for (int i = 0; i < 4; i++) {
            setNovoAlimento();
        }
    }

    protected void setNovoAlimento(){
        int x = 0;
        int y = 0;
        do{
            x = (int)(Math.random() * (largura - tamanho));
            y = (int)(Math.random() * (altura - tamanho));
            x = x - (x % tamanho);
            y = y - (y % tamanho);
        }while(haColisao(x, y));

        alimentos.add(randomAlimento.nextInt(101) > 10 ?
                    new AlimentoBanana(x, y) : new AlimentoPepsi(x, y));
    }
    
    private boolean haColisao(int x, int y){
        for(Integer[] vertice : cobra1.vertices){
            if(vertice[0] == x && vertice[1] == y)
                return true;
        }
        
        for(Integer[] vertice : cobra2.vertices){
            if(vertice[0] == x && vertice[1] == y)
                return true;
        }
        for(EntidadePosicionavel alimento : alimentos){
            if(alimento.vertices.get(0)[0] == x && alimento.vertices.get(0)[1] == y)
                return true;
        }
        return false;
    }
    
    /**
     * @return the altura
     */
    public int getAltura() {
        return altura;
    }

    /**
     * @param altura the altura to set
     */
    public void setAltura(int altura) {
        this.altura = altura;
    }

    /**
     * @return the largura
     */
    public int getLargura() {
        return largura;
    }

    /**
     * @param largura the largura to set
     */
    public void setLargura(int largura) {
        this.largura = largura;
    }

    /**
     * @return the cobra1
     */
    public EntidadePosicionavel getCobra1() {
        return cobra1;
    }

    /**
     * @param cobra1 the cobra1 to set
     */
    public void setCobra1(EntidadePosicionavel cobra1) {
        this.cobra1 = cobra1;
    }

    /**
     * @return the cobra2
     */
    public EntidadePosicionavel getCobra2() {
        return cobra2;
    }

    /**
     * @param cobra2 the cobra2 to set
     */
    public void setCobra2(EntidadePosicionavel cobra2) {
        this.cobra2 = cobra2;
    }

    /**
     * @return the alimentos
     */
    public ArrayList<EntidadePosicionavel> getAlimentos() {
        return alimentos;
    }

    /**
     * @param alimentos the alimentos to set
     */
    public void setAlimentos(ArrayList<EntidadePosicionavel> alimentos) {
        this.alimentos = alimentos;
    }
    
    public int getTamanho(){
        return tamanho;
    }
    
}

package model.send;

import java.io.Serializable;
import java.util.ArrayList;

public class Arena implements Serializable {
    private int altura;
    private int largura;
    private final int tamanho;
    private EntidadePosicionavel cobra1;
    private EntidadePosicionavel cobra2;
    private ArrayList<EntidadePosicionavel> alimentos;
    
    public Arena(int alt, int lar, int tam){
        altura = alt;
        largura = lar;
        tamanho = tam;
        cobra1 = new Cobra('e', alt, lar, tam);
        cobra2 = new Cobra('d', alt, lar, tam);
        alimentos = new ArrayList<>();
    }
    
    public Arena copy(){
        Arena copia = new Arena(altura, largura, tamanho);
        copia.cobra1.apagaPosicoes();
        copia.cobra2.apagaPosicoes();
        
        for(int i = 0; i < cobra1.vertices.size(); i++){
            copia.cobra1.cor = cobra1.cor;
            copia.cobra1.insertPosicao(cobra1.vertices.get(i)[0], cobra1.vertices.get(i)[1]);
        }
        
        for(int i = 0; i < cobra2.vertices.size(); i++){
            copia.cobra2.cor = cobra2.cor;
            copia.cobra2.insertPosicao(cobra2.vertices.get(i)[0], cobra2.vertices.get(i)[1]);
        }
        
        ArrayList<EntidadePosicionavel> copiaAlimentos = new ArrayList<>();
        for(int i = 0; i < alimentos.size(); i++){
            copiaAlimentos.add(new AlimentoBanana(alimentos.get(i).getPosicoes().get(0)[0], alimentos.get(i).getPosicoes().get(0)[1]));
        }        
        
        copia.alimentos = copiaAlimentos;
        
        return copia;
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

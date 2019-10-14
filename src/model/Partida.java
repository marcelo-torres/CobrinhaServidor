 package model;

import model.send.Arena;
import model.send.Alimento;
import model.send.Cobra;
import model.send.AlimentoBanana;
import controller.ControladorPartida;
import java.util.ArrayList;
import model.send.EntidadePosicionavel;
 
@SuppressWarnings("Serial")
 
public class Partida {
    
    // Controlador
    private final ControladorPartida controlador;
    
    // Game Stuff
    Arena arena;
    Posicao cabeca1;
    ArrayList<Posicao> cobra1;
    Posicao cabeca2;
    ArrayList<Posicao> cobra2;
    ArrayList<Posicao> alimentos;
    
    public Partida(Arena ar, ControladorPartida ctr) {
        arena = ar;
        controlador = ctr;
        
        cobra1 = new ArrayList<>();
        Cobra getCobra = (Cobra)arena.getCobra1();
        cabeca1 = new Posicao(arena.getTamanho());
        cabeca1.setPosition(getCobra.getPosicoes().get(0)[0], getCobra.getPosicoes().get(0)[1]);
        cobra1.add(cabeca1);
        for(int i = 1; i < getCobra.getPosicoes().size(); i++){
            Posicao posicao = new Posicao(arena.getTamanho());
            posicao.setPosition(getCobra.getPosicoes().get(i)[0], getCobra.getPosicoes().get(i)[1]);
            cobra1.add(posicao);
        }
        
        cobra2 = new ArrayList<>();
        getCobra = (Cobra)arena.getCobra2();
        cabeca2 = new Posicao(arena.getTamanho());
        cabeca2.setPosition(getCobra.getPosicoes().get(0)[0], getCobra.getPosicoes().get(0)[1]);
        cobra2.add(cabeca2);
        for(int i = 1; i < getCobra.getPosicoes().size(); i++){
            Posicao posicao = new Posicao(arena.getTamanho());
            posicao.setPosition(getCobra.getPosicoes().get(i)[0], getCobra.getPosicoes().get(i)[1]);
            cobra2.add(posicao);
        }
        
        for (int i = 0; i < 4; i++) {
            setNovoAlimento();
        }
        
        alimentos = new ArrayList<>();
        for(int i = 0; i < arena.getAlimentos().size(); i++){
            Alimento alimento = (Alimento)arena.getAlimentos().get(i);
            Posicao posicao = new Posicao(arena.getTamanho());            
            posicao.setPosition(alimento.getX(), alimento.getY());
            alimentos.add(posicao);
        }
        
    }
    
    private void setNovoAlimento(){
        int x = 0;
        int y = 0;
        do{
            x = (int)(Math.random() * (arena.getLargura() - arena.getTamanho()));
            y = (int)(Math.random() * (arena.getAltura() - arena.getTamanho()));
            x = x - (x % arena.getTamanho());
            y = y - (y % arena.getTamanho());
        }while(haColisao(x, y));
        
        Posicao alimento = new Posicao(arena.getTamanho());
        alimento.setPosition(x, y);
        alimentos.add(alimento);
    }
    
    private boolean haColisao(int x, int y){
        for(Posicao posicao : cobra1){
            if(posicao.getX() == x && posicao.getY() == y)
                return true;
        }
        
        for(Posicao posicao : cobra2){
            if(posicao.getX() == x && posicao.getY() == y)
                return true;
        }
        
        for(Posicao posicao : alimentos){
            if(posicao.getX() == x && posicao.getY() == y)
                return true;
        }
        return false;
    }
    
    public void atualizaArena(){
        arena.getCobra1().apagaPosicoes();
        for(int i = 0; i < cobra1.size(); i++){
            arena.getCobra1().insertPosicao(cobra1.get(i).getX(), cobra1.get(i).getY());
        }
        
        arena.getCobra2().apagaPosicoes();
        for(int i = 0; i < cobra2.size(); i++){
            arena.getCobra2().insertPosicao(cobra2.get(i).getX(), cobra2.get(i).getY());
        }
        
        ArrayList<EntidadePosicionavel> eAlimentos = new ArrayList<>();
        for(int i = 0; i < alimentos.size(); i++){
            Posicao alimento = alimentos.get(i);
            eAlimentos.add(new AlimentoBanana(alimento.getX(), alimento.getY()));
        }
        arena.setAlimentos(eAlimentos);
    }
    
    public void moveCobra1(int dx, int dy){
        for(int i = cobra1.size() - 1; i > 0; i--){
            cobra1.get(i).setPosition(cobra1.get(i - 1).getX(), cobra1.get(i - 1).getY());
        }

        cabeca1.move(dx, dy);
    }
    
    public void moveCobra2(int dx, int dy){
        for(int i = cobra2.size() - 1; i > 0; i--){
            cobra2.get(i).setPosition(cobra2.get(i - 1).getX(), cobra2.get(i - 1).getY());
        }

        cabeca2.move(dx, dy);
    }
    
    public int verificaColisaoCobras(){
        if(cabeca1.isCollision(cabeca2) || cabeca2.isCollision(cabeca1))
            return 3;
        
        for(Posicao posicao : cobra1){
            if(posicao.isCollision(cabeca1) || posicao.isCollision(cabeca2)){
                return 1;
            }
        }
        
        for(Posicao posicao : cobra2){
            if(posicao.isCollision(cabeca1) || posicao.isCollision(cabeca2)){
                return 2;
            }
        }
        return 0;
    }
    
    public void processaAlimentos(){
        for(Posicao alimento : alimentos){
            if(alimento.isCollision(cabeca1)){
                consomeAlimento(alimento);
                Posicao e = new Posicao(arena.getTamanho());
                e.setPosition(-100, -100);
                cobra1.add(e);
            }
            
            if(alimento.isCollision(cabeca2)){
                consomeAlimento(alimento);
                Posicao e = new Posicao(arena.getTamanho());
                e.setPosition(-100, -100);
                cobra2.add(e);
            }
            
        }
    }
    
    public void consomeAlimento(Posicao alimento){
        int index = alimentos.indexOf(alimento);
        alimentos.remove(index);
        setNovoAlimento();
    }
    
    public void trataParedes(){
        if (cabeca1.getX() < 0) cabeca1.setX(arena.getLargura() - 10);
        if (cabeca1.getY() < 0) cabeca1.setY(arena.getAltura() - 10);
        if (cabeca1.getX() > arena.getLargura() - 10) cabeca1.setX(0);
        if (cabeca1.getY() > arena.getAltura() - 10) cabeca1.setY(0);
        
        if (cabeca2.getX() < 0) cabeca2.setX(arena.getLargura() - 10);
        if (cabeca2.getY() < 0) cabeca2.setY(arena.getAltura() - 10);
        if (cabeca2.getX() > arena.getLargura() - 10) cabeca2.setX(0);
        if (cabeca2.getY() > arena.getAltura() - 10) cabeca2.setY(0);
    }
}

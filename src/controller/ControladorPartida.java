package controller;

import java.awt.Color;
import localizacoes.ILocal;

import model.Partida;
import model.agentes.IJogadorProtegido;
import model.send.Arena;

public class ControladorPartida implements Runnable, ILocal{
    
    Arena arena;
    Partida partida;
    Thread thread;
    ControladorGeral controladorGeral;
    IJogadorProtegido jogador1;
    IJogadorProtegido jogador2;
    
    // Ações
    private boolean running;
    private boolean gameover;
    
    int codUltimoMov1;
    int codUltimoMov2;
    
    // Game Loop
    private long targetTime;
    
    // Movemento
    private int dx1, dy1, dx2, dy2;
    
    public ControladorPartida(IJogadorProtegido jg1, IJogadorProtegido jg2, ControladorGeral ctr){
        jogador1 = jg1;
        jogador2 = jg2;
        controladorGeral = ctr;
        running = false;
        thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        if(running) return;
        running = true;
        iniciarJogo();
        long startTime;
        long elapsed;
        long wait;
        
        while(running) {
            startTime = System.nanoTime();
            
            update();
            
            elapsed = System.nanoTime() - startTime;
            wait = targetTime - elapsed / 1000000;    
            if(wait > 0) {
                try {
                    Thread.sleep(wait);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void iniciarJogo() {
        arena = new Arena(500, 1000, 10);
        partida = new Partida(arena, this);
        gameover = false;
        dx1 = dy1 = dx2 = dy2 = 0;
        setFPS(10);
        enviarQuadro(jogador1, jogador2, arena);
    }
    
    
    private void enviarQuadro(IJogadorProtegido jogador1, IJogadorProtegido jogador2, Arena arena){
        Color c1 = arena.getCobra1().getCor();
        Color c2 = arena.getCobra2().getCor();
        
        controladorGeral.enviarQuadro(jogador1, arena);
        
        arena.getCobra1().setCor(c2);
        arena.getCobra2().setCor(c1);
        
        controladorGeral.enviarQuadro(jogador2, arena);
        
        arena.getCobra1().setCor(c1);
        arena.getCobra2().setCor(c2);
        
    }
    
    
    public void setFPS(int fps) {
        targetTime = 1000/fps;
    }
    
    public void update(){
        if(gameover){
            running = false;
            return;
        }

        computaCodMovimento();
        
        executaMovimento();
        
        switch(partida.verificaColisaoCobras()){
            //Cobra 1 morreu
            case 1:
                gameover = true;
                controladorGeral.cobraMorreu(jogador1);
                controladorGeral.cobraGanhou(jogador2);                
                break;
            //Cobra 2 morreu
            case 2:
                gameover = true;
                controladorGeral.cobraMorreu(jogador2);
                controladorGeral.cobraGanhou(jogador1);
                break;
            //Empate
            case 3:
                gameover = true;
                controladorGeral.empatou(jogador1);
                controladorGeral.empatou(jogador2);
                break;
            default:
                break;
        }
        
        partida.atualizaArena();
        
        
        
        
        enviarQuadro(jogador1, jogador2, arena);
    }
    
    public void computaCodMovimento(){
        
        //---Cobra1
        //Up
        if(codUltimoMov1==1 && dy1 == 0){
            dy1 = -arena.getTamanho();
            dx1 = 0;
        }
        //Down
        else if(codUltimoMov1==2 && dy1 == 0){
            dy1 = arena.getTamanho();
            dx1 = 0;
        }
        //Left
        else if(codUltimoMov1==3 && dx1 == 0){
            dy1 = 0;
            dx1 = -arena.getTamanho();
        }
        //Right
        else if(codUltimoMov1==4 && dx1 == 0 && dy1 != 0){
            dy1 = 0;
            dx1 = arena.getTamanho();
        }
        
        //---Cobra2
        //Up
        if(codUltimoMov2==1 && dy2 == 0){
            dy2 = -arena.getTamanho();
            dx2 = 0;
        }
        //Down
        else if(codUltimoMov2==2 && dy2 == 0){
            dy2 = arena.getTamanho();
            dx2 = 0;
        }
        //Left
        else if(codUltimoMov2==3 && dx2 == 0){
            dy2 = 0;
            dx2 = -arena.getTamanho();
        }
        //Right
        else if(codUltimoMov2==4 && dx2 == 0 && dy2 != 0){
            dy2 = 0;
            dx2 = arena.getTamanho();
        }
    }
    
    public void executaMovimento(){
        partida.moveCobra1(dx1, dy1);
        partida.moveCobra2(dx2, dy2);
        partida.processaAlimentos();
        partida.trataParedes();
    }
    
    public void cima(IJogadorProtegido jg){
        if(jg == jogador1)
            codUltimoMov1 = 1;
        else
            codUltimoMov2 = 1;
    }
    
    public void baixo(IJogadorProtegido jg){
        if(jg == jogador1)
            codUltimoMov1 = 2;
        else
            codUltimoMov2 = 2;
    }
    
    public void esquerda(IJogadorProtegido jg){
        if(jg == jogador1)
            codUltimoMov1 = 3;
        else
            codUltimoMov2 = 3;
    }
    
    public void direita(IJogadorProtegido jg){
        if(jg == jogador1)
            codUltimoMov1 = 4;
        else
            codUltimoMov2 = 4;
    }

    public boolean finalizarPartida(IJogadorProtegido jogador) {
        gameover = true;
        if(jogador == jogador1){
            controladorGeral.avisaOponenteDesistiu(jogador2);
            
        }
        else{
            controladorGeral.avisaOponenteDesistiu(jogador1);
        }
        controladorGeral.confirmaDesistencia(jogador);
        return true;
    }
    
    
    
    
}

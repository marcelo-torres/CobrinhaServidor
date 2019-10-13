package aplicacao.controller;

import aplicacao.model.Jogador;
import aplicacao.model.Partida;
import aplicacao.model.send.Arena;

public class ControladorDePartida implements Runnable{

    Arena arena;
    Partida partida;
    Thread thread;
    ControladorGeral controladorGeral;
    Jogador jogador1;
    Jogador jogador2;
    
    // Ações
    private boolean running;
    private boolean gameover;
    
    int codUltimoMov1;
    int codUltimoMov2;
    
        
    // Game Loop
    private long targetTime;
    
    // Movemento
    private int dx1, dy1, dx2, dy2;
    
    public ControladorDePartida(Jogador jg1, Jogador jg2, ControladorGeral ctr){
        jogador1 = jg1;
        jogador2 = jg2;
        controladorGeral = ctr;
        thread = new Thread(this);
        thread.start();
    }

    public void iniciarJogo() {
        arena = new Arena(500, 1000, 10);
        partida = new Partida(arena, this);
        gameover = false;
        dx1 = dy1 = dx2 = dy2 = 0;
        setFPS(10);
        controladorGeral.enviarQuadro(jogador1, jogador2, arena);
    }
    
    public void setMovCobra1(int mov){
        codUltimoMov1 = mov;
    }
    
    public void setMovCobra2(int mov){
        codUltimoMov2 = mov;
    }

    public void computaCodMovimento(){
        
        //---Cobra1
        //Up
        if(codUltimoMov1==1 && dy1 == 0){
            dy1 = -arena.getTamanho();
            dx1 = 0;
        }
        //Down
        if(codUltimoMov1==2 && dy1 == 0){
            dy1 = arena.getTamanho();
            dx1 = 0;
        }
        //Left
        if(codUltimoMov1==3 && dx1 == 0){
            dy1 = 0;
            dx1 = -arena.getTamanho();
        }
        //Right
        if(codUltimoMov1==4 && dx1 == 0 && dy1 != 0){
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
        if(codUltimoMov2==2 && dy2 == 0){
            dy2 = arena.getTamanho();
            dx2 = 0;
        }
        //Left
        if(codUltimoMov2==3 && dx1 == 0){
            dy2 = 0;
            dx2 = -arena.getTamanho();
        }
        //Right
        if(codUltimoMov2==4 && dx1 == 0 && dy2 != 0){
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
        
        controladorGeral.enviarQuadro(jogador1, jogador2, arena);
    }
    
    public void setFPS(int fps) {
        targetTime = 1000/fps;
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
}

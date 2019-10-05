package controlador;

import jogo.PainelServidor;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jogo.Arena;
import jogo.Cobra;
import jogo.EntidadePosicionavel;
import telas.Jogo;

public class ControladorJogo extends Controlador{

    Arena arena;
    PainelServidor painel;
    public final int SIZE = 10;
    
    // Ações
    private boolean up, down, right, left, start;
    private boolean gameover;
    
    // Movemento
    private int dx, dy;
    
    public ControladorJogo(GerenciadorDeTelas grc) {
        gerenciador = grc;
    }
    
    public void construirArena(int altura, int largura){
        EntidadePosicionavel cobra1 = new Cobra('e', altura, largura, SIZE);
        EntidadePosicionavel cobra2 = new Cobra('d', altura, largura, SIZE);
        arena = new Arena(cobra1, cobra2, altura, largura, SIZE);
    }
    
    public void inicializarTelaJogo(){
        tela = new Jogo(this);
        tela.setLocationRelativeTo(null);
        tela.setResizable(false);
        
        criarMapaDeComponentes();

        JPanel jpGame = ((JPanel)getComponente("jpn_game"));
        jpGame.setLayout(new CardLayout());
        
        construirArena(500, 1000);
        
        painel = new PainelServidor(arena, this);
        jpGame.add(painel);
        ((JLabel)getComponente("lbl_nome")).setText("Olá, " + gerenciador.nome_jogador);
        
        Iterator it = mapaDeComponentes.values().iterator();
        
        while(it.hasNext()){
            Component c = (Component)it.next();
            c.setEnabled(true);
        }
        
        tela.pack();
        
        tela.setVisible(true);
    }
    
    public void finalizarTelaJogo(){
        gerenciador.exibirInicio();
        tela.setVisible(false);
        tela = null;
    }

    public void iniciarJogo() {
        gameover = false;
        painel.setAllowed(false);
        dx = dy = 0;
        painel.setFPS(10);
        painel.atualizaPainel();
    }
    
    public void update(){
        if(gameover){
            if(start){
                construirArena(arena.getAltura(), arena.getLargura());
                iniciarJogo();
            }
            return;
        }
        
        if(up && dy == 0){
            dy = -SIZE;
            dx = 0;
        }
        if(down && dy == 0){
            dy = SIZE;
            dx = 0;
        }
        if(left && dx == 0 && !down && !up){
            dy = 0;
            dx = -SIZE;
            painel.setAllowed(true);
        }
        if(right && dx == 0 && dy != 0 && !down && !up){
            dy = 0;
            dx = SIZE;
            painel.setAllowed(true);
        }
        
        if(dx != 0 || dy != 0){
            painel.moveCobra1(dx, dy);
            painel.moveCobra2(dx, dy);
        }
        
        switch(painel.verificaColisaoCobras()){
            //Cobra 1 morreu
            case 1:
                gameover = true;
                break;
            //Cobra 2 morreu
            case 2:
                gameover = true;
                break;
            //Empate
            case 3:
                gameover = true;
                break;
            default:
                break;
        }
        
        painel.processaAlimentos();
        
        painel.trataParedes();
    }
    
    public void setStart(boolean b){
        start = b;
    }
    
    public void setUp(boolean b){
        up = b;
    }
    
    public void setDown(boolean b){
        down = b;
    }
    
    public void setLeft(boolean b){
        left = b;
    }
    
    public void setRight(boolean b){
        right = b;
    }
    
    public boolean isGameOver(){
        return gameover;
    }
    
    public boolean isInicio(){
        return dx == 0 && dy == 0;
    }
}

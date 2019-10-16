package controller;

import combinador.Combinador;
import java.util.ArrayList;
import java.util.HashMap;
import localizacoes.Hall;
import localizacoes.ILocal;
import model.Jogador;
import model.agentes.ControladorDePartida;
import model.agentes.IJogador;
import model.send.Arena;
import stub.comunicacao.FilaMonitorada;

public class ControladorGeral {
    

    public void enviarQuadro(IJogador j1, IJogador j2, Arena arena){
        
    }
    
    public void cobraMorreu(IJogador j){
        
        
        //salva banco
    }
    
    public void cobraGanhou(IJogador j){
        
        
        //salva banco
    }
    
    public void empatou(IJogador j){
        
        
        //salva banco
    }
    
    public boolean encerrarPartida(IJogador jogador){
        ILocal local = ((Jogador) jogador).getLocalAtual();
        if(!(local instanceof ControladorDePartida)){
            return false;
        }
        ControladorDePartida cp = (ControladorDePartida) local;
        cp.
        
    }
    
    public void setLocalAtual(IJogador jogador, ILocal novoLocal){
        ((Jogador)jogador).setLocalAtual(novoLocal);
    }
    
    private Hall hall;
    private Combinador combinador;
    Thread trdCombinador;
    private FilaMonitorada<ArrayList<Jogador>> listaIniciar = new FilaMonitorada<ArrayList<Jogador>>(Integer.MAX_VALUE);

    public ControladorGeral() {
            this.hall = new Hall();
            this.combinador = new Combinador(this, 1000);
            this.trdCombinador = new Thread(this.combinador);
            this.trdCombinador.start();
    }

    public void jogadoresCombinados(IJogador jogadorA, IJogador jogadorB) {
            ArrayList<Jogador> novoPar = new ArrayList<Jogador>();
            novoPar.add((Jogador)jogadorA);
            novoPar.add((Jogador)jogadorB);

            listaIniciar.adicionar(novoPar);
    }

    public boolean iniciarPartida(Jogador jogador) {
            return combinador.inserir( jogador);
    }

    public boolean desistirDeIniciar(Jogador jogador) {

            if(this.combinador != jogador.getLocalAtual()) {
                    return false;
            }

            if(!combinador.remover(jogador)) {
                    return false;
            }

            jogador.setLocalAtual(hall);

            return true;
    }

    public void alterarLocal(Jogador jogador) {
            jogador.setLocalAtual(hall);		
    }

    
}

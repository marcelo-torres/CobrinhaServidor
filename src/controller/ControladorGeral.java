package controller;

import combinador.Combinador;
import java.util.ArrayList;
import localizacoes.Hall;
import model.Jogador;
import model.agentes.IJogador;
import model.send.Arena;
import stub.comunicacao.FilaMonitorada;

public class ControladorGeral {
    public void enviarQuadro(Jogador j1, Jogador j2, Arena arena){
        
    }
    
    public void cobraMorreu(Jogador j){
        
    }
    
    public void cobraGanhou(Jogador j){
        
    }
    
    public void empatou(Jogador j){
        
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

    public void iniciarPartida(Jogador jogador) {

            combinador.inserir( jogador);

    }

    public boolean desistirDeIniciar(Jogador jogador) {

            if(this.combinador != jogador.getLocalAtual()) {
                    return false;
            }

            if(!combinador.remover( jogador)) {
                    return false;
            }

            jogador.setLocalAtual(hall);

            return true;

    }

    public void alterarLocal(Jogador jogador) {
            jogador.setLocalAtual(hall);		
    }

    
}

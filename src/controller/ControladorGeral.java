package controller;

import DataBase.AcessoBanco;
import DataBase.DAO;
import combinador.Combinador;
import controller.auxiliares.IniciadorDePartidas;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import localizacoes.Hall;
import localizacoes.ILocal;
import model.Jogador;
import model.agentes.IJogador;
import model.send.Arena;
import stub.comunicacao.FilaMonitorada;

public class ControladorGeral {

    private IniciadorDePartidas iniciadorDePartidas = new IniciadorDePartidas(this);
    private Thread trdIniciadoraDePartidas = new Thread(this.iniciadorDePartidas);
    private DAO dao;
    private Hall hall= new Hall();;
    private Combinador combinador = new Combinador(this, 1000);
    private Thread trdCombinador = new Thread(this.combinador);
    private int jogadoresLigados;
    private Object lockerJogadoresLigados;
    
    private FilaMonitorada<ArrayList<IJogador>> listaIniciar = new FilaMonitorada<ArrayList<IJogador>>(Integer.MAX_VALUE);

    public ControladorGeral() {
        
        try {
            dao = new DAO();
        } catch (SQLException ex) {
            System.out.println("Erro ao criar DAO");
        }
        
        this.trdCombinador.start();
        this.trdIniciadoraDePartidas.start();

    }
    
    public void enviarQuadro(IJogador j1, IJogador j2, Arena arena) {
        
    }

    public void cobraMorreu(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.perdeu();
        dao.incrementaDerrota(jogador.getNome());
        jogador.setLocalAtual(hall);
        
    }

    public void oponenteDesistiu(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.oponenteDesistiu();
        dao.incrementaVitorias(jogador.getNome());
        jogador.setLocalAtual(hall);
        
    }
    
    public boolean cima(Jogador j) {
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.cima(j);
        return true;
        
    }
    
    public boolean baixo(Jogador j) {
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.baixo(j);
        return true;
        
    }
        
    public boolean esquerda(Jogador j) {
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.esquerda(j);
        return true;
        
    }
            
    public boolean direita(Jogador j) {
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.direita(j);
        return true;
        
    }
    
    public void confirmaDesistencia(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.oponenteDesistiu();
        dao.incrementaDerrota(jogador.getNome());
        jogador.setLocalAtual(hall);
        
    }
    
    public void cobraGanhou(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.ganhou();
        dao.incrementaVitorias(jogador.getNome());
        jogador.setLocalAtual(hall);
        
    }

    public void empatou(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.empatou();
        dao.incrementaEmpates(jogador.getNome());
        jogador.setLocalAtual(hall);
        
    }

    public boolean encerrarPartida(IJogador jogador) {
        ILocal local = ((Jogador) jogador).getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        
        ControladorPartida cp = (ControladorPartida) local;
        return cp.finalizarPartida(jogador);

    }

    public void setLocalAtual(IJogador jogador, ILocal novoLocal) {
        Jogador atual = (Jogador) jogador;
        
        if(novoLocal == hall){
            atual.irParaHall();
        }
        else if(novoLocal instanceof ControladorPartida){
            atual.irParaPartida();
        }
        else if(novoLocal == combinador){
            atual.combinando();
        }
        
        ((Jogador) jogador).setLocalAtual(novoLocal);
    }



    public void jogadoresCombinados(IJogador jogadorA, IJogador jogadorB) {

        ArrayList<IJogador> novoPar = new ArrayList<IJogador>();
        novoPar.add(jogadorA);
        novoPar.add(jogadorB);
        listaIniciar.adicionar(novoPar);
    }

    public boolean iniciarPartida(Jogador jogador) {
        if(jogador.getNome() == null || jogador.getNome().isEmpty());
        return combinador.inserir(jogador);
    }

    public boolean desistirDeIniciar(Jogador jogador) {

        if (this.combinador != jogador.getLocalAtual()) {
            return false;
        }

        if (!combinador.remover(jogador)) {
            return false;
        }

        jogador.setLocalAtual(hall);
        return true;
    }
    
    public boolean desistirDoJogo(Jogador jogador) {

        ILocal localAtual = jogador.getLocalAtual();
        
        if (!(localAtual instanceof ControladorPartida)) {
            return false;
        }

        ControladorPartida cp = (ControladorPartida)localAtual;
        cp.finalizarPartida(jogador);
        
        return true;
    }

    public void alterarLocal(Jogador jogador, ILocal local) {
        jogador.setLocalAtual(local);
    }
   

    public void avisaOponenteDesistiu(IJogador jogador) {
        
        Jogador j = (Jogador) jogador;
        j.oponenteDesistiu();
        alterarLocal(j, hall);
        
    }
    
    public ArrayList<IJogador> getPar() {
        return listaIniciar.remover();
    }
    
    public void iniciarPartidaPronta(IJogador jogadorA, IJogador jogadorB){
        
        ControladorPartida novoCP = new ControladorPartida(jogadorA, jogadorB, this);
        setLocalAtual(jogadorB, novoCP);
        setLocalAtual(jogadorA, novoCP);
        
    }
    
    public double getVD(IJogador jogador){
        Jogador j = (Jogador) jogador;
        String nome = j.getNome();
        return dao.getVD(nome);
    }

    public void entrando(Jogador jogador) {
        synchronized(lockerJogadoresLigados){
            jogadoresLigados++;
        }
    }
    
    public void saindo(Jogador jogador) {
        synchronized(lockerJogadoresLigados){
            jogadoresLigados--;
        }
        
        ILocal local = jogador.getLocalAtual();
        
        if(local == hall){
            return;
        }
        
        if(local == combinador){
            if(combinador.remover(jogador)){
                return;
            }
            while(local == combinador){
                try {
                    Thread.sleep(1000);
                    local = jogador.getLocalAtual();
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if(local == hall){
                return;
            }
        }
        
        if (local instanceof ControladorPartida){
            jogador.encerrarPartida();
            
        }
    }
}

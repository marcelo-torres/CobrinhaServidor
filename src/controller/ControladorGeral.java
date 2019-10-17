package controller;

import DataBase.AcessoBanco;
import DataBase.DAO;
import combinador.Combinador;
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

    private DAO dao;
    private Hall hall;
    private Combinador combinador;
    private Thread trdCombinador;
    private FilaMonitorada<ArrayList<IJogador>> listaIniciar = new FilaMonitorada<ArrayList<IJogador>>(Integer.MAX_VALUE);

    public ControladorGeral() {
        this.hall = new Hall();
        this.combinador = new Combinador(this, 1000);
        this.trdCombinador = new Thread(this.combinador);
        this.trdCombinador.start();
        
        try {
            dao = new DAO();
        } catch (SQLException ex) {
            System.out.println("Erro ao criar DAO");
        }
    }
    
    public void enviarQuadro(IJogador j1, IJogador j2, Arena arena) {
        
    }

    public void cobraMorreu(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.perdeu();
        dao.incrementaDerrota(jogador.getNome());
        //jogador.setLocalAtual(hall); tirar hall
        //salva banco
    }

    public void oponenteDesistiu(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.oponenteDesistiu();
        dao.incrementaVitorias(jogador.getNome());
        //salva banco
    }
    
    public void confirmaDesistencia(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.oponenteDesistiu();
        dao.incrementaDerrota(jogador.getNome());
        //salva banco
    }
    
    public void cobraGanhou(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.ganhou();
        dao.incrementaVitorias(jogador.getNome());
        //salva banco
    }

    public void empatou(IJogador j) {
        Jogador jogador = (Jogador)j;
        jogador.empatou();
        dao.incrementaEmpates(jogador.getNome());
        //salva banco
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
        ((Jogador) jogador).setLocalAtual(novoLocal);
    }



    public void jogadoresCombinados(IJogador jogadorA, IJogador jogadorB) {

        ArrayList<IJogador> novoPar = new ArrayList<IJogador>();
        novoPar.add(jogadorA);
        novoPar.add(jogadorB);
        listaIniciar.adicionar(novoPar);
    }

    public boolean iniciarPartida(Jogador jogador) {
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
    }
}

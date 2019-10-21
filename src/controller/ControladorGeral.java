package controller;

import DataBase.DAO;
import combinador.Combinador;
import controller.auxiliares.IniciadorDePartidas;
import java.sql.SQLException;
import java.util.ArrayList;
import localizacoes.Hall;
import localizacoes.ILocal;
import model.agentes.IJogadorVisaoControladorServidor;
import model.send.Arena;
import model.agentes.IJogadorProtegido;
import stub.comunicacao.FilaMonitorada;
public class ControladorGeral {

    private IniciadorDePartidas iniciadorDePartidas = new IniciadorDePartidas(this);
    private Thread trdIniciadoraDePartidas = new Thread(this.iniciadorDePartidas);
    private DAO dao;
    private Hall hall= new Hall();;
    private Combinador combinador = new Combinador(this, 1000);
    private Thread trdCombinador = new Thread(this.combinador);
    private int jogadoresLigados;
    private final Object lockerJogadoresLigados = new Object();
    
    private FilaMonitorada<ArrayList<IJogadorProtegido>> listaIniciar = new FilaMonitorada<ArrayList<IJogadorProtegido>>(Integer.MAX_VALUE);

    public ControladorGeral() {
        
        try {
            dao = new DAO();
        } catch (SQLException ex) {
            System.out.println("Erro ao criar DAO");
        }
        
        this.trdCombinador.start();
        this.trdIniciadoraDePartidas.start();

    }
    
    public void enviarQuadro(IJogadorProtegido jogador, Arena arena) {
        
    }

    public void cobraMorreu(IJogadorProtegido j) {
        IJogadorVisaoControladorServidor jogador = (IJogadorVisaoControladorServidor)j;
        jogador.perdeu();
        dao.incrementaDerrota(jogador.getNome());
        jogador.setLocalAtual(hall);
        
    }

    public void oponenteDesistiu(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        j.oponenteDesistiu();
        dao.incrementaVitorias(j.getNome());
        j.setLocalAtual(hall);
        
    }
    
    public boolean cima(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.cima(j);
        return true;
        
    }
    
    public boolean baixo(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.baixo(j);
        return true;
        
    }
        
    public boolean esquerda(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.esquerda(j);
        return true;
        
    }
            
    public boolean direita(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        ControladorPartida cp = (ControladorPartida) local;
        cp.direita(j);
        return true;
        
    }
    
    public void confirmaDesistencia(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        j.oponenteDesistiu();
        dao.incrementaDerrota(j.getNome());
        j.setLocalAtual(hall);
        
    }
    
    public void cobraGanhou(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        j.ganhou();
        dao.incrementaVitorias(j.getNome());
        j.setLocalAtual(hall);
        
    }

    public void empatou(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        j.empatou();
        dao.incrementaEmpates(j.getNome());
        j.setLocalAtual(hall);
        
    }

    public boolean encerrarPartida(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        
        ILocal local = j.getLocalAtual();
        if (!(local instanceof ControladorPartida)) {
            return false;
        }
        
        ControladorPartida cp = (ControladorPartida) local;
        return cp.finalizarPartida(jogador);

    }

    public void setLocalAtual(IJogadorProtegido jogador, ILocal novoLocal) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        
        
        if(novoLocal == hall){
            j.irParaHall();
        }
        else if(novoLocal instanceof ControladorPartida){
            j.irParaPartida();
        }
        else if(novoLocal == combinador){
            j.combinando();
        }
        
        j.setLocalAtual(novoLocal);
    }



    public void jogadoresCombinados(IJogadorProtegido jogadorA, IJogadorProtegido jogadorB) {
        
        ArrayList<IJogadorProtegido> novoPar = new ArrayList<IJogadorProtegido>();
        novoPar.add(jogadorA);
        novoPar.add(jogadorB);
        listaIniciar.adicionar(novoPar);
        
    }

    public boolean iniciarPartida(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        
        if(j.getNome() == null || j.getNome().isEmpty());
        return combinador.inserir(jogador);
    }

    public boolean desistirDeIniciar(IJogadorProtegido jogador) {

        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        if (this.combinador != j.getLocalAtual()) {
            return false;
        }

        if (!combinador.remover(j)) {
            return false;
        }

        j.setLocalAtual(hall);
        return true;
    }
    
    public boolean desistirDoJogo(IJogadorProtegido jogador) {

        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        
        ILocal localAtual = j.getLocalAtual();
        
        if (!(localAtual instanceof ControladorPartida)) {
            return false;
        }

        ControladorPartida cp = (ControladorPartida)localAtual;
        cp.finalizarPartida(j);
        
        return true;
    }

    private void alterarLocal(IJogadorVisaoControladorServidor jogador, ILocal local) {
        jogador.setLocalAtual(local);
    }
   

    public void avisaOponenteDesistiu(IJogadorProtegido jogador) {
        
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor) jogador;
        j.oponenteDesistiu();
        alterarLocal(j, hall);
        
    }
    
    public ArrayList<IJogadorProtegido> getPar() {
        return listaIniciar.remover();
    }
    
    public void iniciarPartidaPronta(IJogadorProtegido jogadorA, IJogadorProtegido jogadorB){
        IJogadorVisaoControladorServidor jA = (IJogadorVisaoControladorServidor) jogadorA;
        IJogadorVisaoControladorServidor jB = (IJogadorVisaoControladorServidor) jogadorB;
        
        ControladorPartida novoCP = new ControladorPartida(jogadorA, jogadorB, this);
        setLocalAtual(jogadorB, novoCP);
        setLocalAtual(jogadorA, novoCP);
        
        jA.irParaPartida();
        jB.irParaPartida();
        
    }
    
    public double getVD(IJogadorVisaoControladorServidor jogador){
        String nome = jogador.getNome();
        return dao.getVD(nome);
    }

    public void entrando(IJogadorVisaoControladorServidor jogador) {
        synchronized(lockerJogadoresLigados){
            jogadoresLigados++;
        }
    }
    
    public void saindo(IJogadorProtegido jogador) {
        IJogadorVisaoControladorServidor j = (IJogadorVisaoControladorServidor)jogador;
        
        synchronized(lockerJogadoresLigados){
            jogadoresLigados--;
        }
        
        
        
        ILocal local = j.getLocalAtual();
        
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
                    local = j.getLocalAtual();
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if(local == hall){
                return;
            }
        }
        
        if (local instanceof ControladorPartida){
            ControladorPartida ctr = (ControladorPartida) local;
            ctr.finalizarPartida(j);
            
        }
    }
}

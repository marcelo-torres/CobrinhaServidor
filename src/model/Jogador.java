package model;

import controller.ControladorGeral;
import java.util.concurrent.Semaphore;
import model.agentes.IJogador;
import localizacoes.ILocal;
import model.send.Arena;
import controller.ControladorGeralJogador;


public class Jogador implements IJogador {
    
    private String nome;
    private ControladorGeral cg;
    private ILocal localAtual;
    private ControladorGeralJogador controleJogador;

    public Jogador(ControladorGeral cg, ControladorGeralJogador controleJogador) {
        this.cg = cg;
        this.controleJogador = controleJogador;
        this.cg.entrando(this);
    }
    
    public void setNome(String nome) {
        if(nome == null || nome.isEmpty()){
            controleJogador.falha("Nome inválido.");
        }
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
    
    public double getVD(){
        return cg.getVD(this);
    }

    
    
    public void setLocalAtual(ILocal local) {
        this.localAtual = local;
    }

    @Override
    public boolean iniciarPartida() {

        return cg.iniciarPartida(this);
        
    }

    @Override
    public boolean desistirDeProcurarPartida() {
        return cg.desistirDeIniciar(this);
    }

    @Override
    public boolean encerrarPartida() {
        return cg.encerrarPartida(this);
    }

    @Override
    public void andarParaCima() {
        cg.cima(this);
    }

    @Override
    public void andarParaBaixo() {
        cg.baixo(this);
    }

    @Override
    public void andarParaEsquerda() {
       cg.esquerda(this);
    }

    @Override
    public void andarParaDireita() {
        cg.direita(this);
    }

    public void novoQuadro(Arena arena){
        controleJogador.entregarQuadro(arena);
    }

    
    public ILocal getLocalAtual() {
        return localAtual;
    }
	
    public void oponenteDesistiu(){
        controleJogador.adversarioSaiu();
    }

    public void ganhou() {
        controleJogador.ganhou();
    }

    public void empatou() {
        controleJogador.empatou();
    }

    public void perdeu() {
        controleJogador.perdeu();
    }

    public void irParaHall() {
        controleJogador.irParaOHall();
    }

    public void irParaPartida() {
        controleJogador.partidaIniciada();
    }

    public void combinando() {
        controleJogador.procurandoPartida();
    }

    @Override
    public void saindo() {
        cg.saindo(this);
    }
    
}

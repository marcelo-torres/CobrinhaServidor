package model;

import controller.ControladorGeral;

import localizacoes.ILocal;
import model.send.Arena;

import model.agentes.IControladorGeralVisaoAplicacaoServidor;
import model.agentes.IJogadorProtegido;
import model.agentes.IJogadorVisaoStubServidor;
import model.agentes.IJogadorVisaoControladorServidor;


public class Jogador implements IJogadorVisaoStubServidor, IJogadorVisaoControladorServidor, IJogadorProtegido {
    
    private String nome;
    private ControladorGeral cg;
    private ILocal localAtual;
    private IControladorGeralVisaoAplicacaoServidor controleJogador;

    public Jogador(ControladorGeral cg) {
        this.cg = cg;
        this.cg.entrando(this);
    }
    
    public void setControleJogador(IControladorGeralVisaoAplicacaoServidor controleJogador) {
        this.controleJogador = controleJogador;
    }
    
    @Override
    public void iniciarSessao(String nome) {
        if(nome == null || nome.isEmpty()){
            controleJogador.falha("Nome inválido.");
        }
        this.nome = nome;
    }

    @Override
    public String getNome() {
        return nome;
    }
    
    

    @Override
    public double getVD(){
        return cg.getVD(this);
    }

    
    
    @Override
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

    @Override
    public void novoQuadro(Arena arena){
        controleJogador.novoQuadro(arena);
    }

    
    @Override
    public ILocal getLocalAtual() {
        return localAtual;
    }
	
    @Override
    public void oponenteDesistiu(){
        controleJogador.adversarioSaiu();
    }

    @Override
    public void ganhou() {
        controleJogador.ganhou();
    }

    @Override
    public void empatou() {
        controleJogador.empatou();
    }

    @Override
    public void perdeu() {
        controleJogador.perdeu();
    }

    @Override
    public void irParaHall() {
        controleJogador.exibirTelaSessao();
    }

    @Override
    public void irParaPartida() {
        controleJogador.exibirTelaJogo();
    }

    @Override
    public void combinando() {
        controleJogador.procurandoPartida();
    }

    @Override
    public void encerrarSessao() {
        cg.saindo(this);
    }

    
}

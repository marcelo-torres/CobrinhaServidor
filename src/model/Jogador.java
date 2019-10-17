package model;

import controller.ControladorGeral;
import java.util.concurrent.Semaphore;
import model.agentes.IJogador;
import localizacoes.ILocal;
import model.send.Arena;
import controller.ControladorGeralJogador;


public class Jogador implements IJogador {
    //criar contrutor
    private String nome;
    private ControladorGeral cg;
    private ILocal localAtual;
    private ControladorGeralJogador controleJogador;
    

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
    
    public double getVD(){
        return 0.0;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
    }

    @Override
    public void andarParaCima() {
        
    }

    @Override
    public void andarParaBaixo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaEsquerda() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaDireita() {
        
    }

    public void novoQuadro(Arena arena){
        
    }

    
    public ILocal getLocalAtual() {
        return localAtual;
    }
	
    public void oponenteDesistiu(){
        controleJogador.adversarioSaiu();
    }
    
}

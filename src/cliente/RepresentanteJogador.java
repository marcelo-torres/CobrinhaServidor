package cliente;

import model.agentes.IJogador;
import java.net.Socket;
import localizacoes.Local;
import stub.GerenciadorDeCliente;


public class RepresentanteJogador implements Runnable,  IJogador {
    
    private final GerenciadorDeCliente GerenciadorDeCliente;
    private final Socket SOCKET;
    
    public RepresentanteJogador(Socket socket) {
        this.SOCKET = socket;
        this.GerenciadorDeCliente = new GerenciadorDeCliente(this, socket);
    }

    @Override
    public void close() {
    
    }
    

    private void pausar(int tempo) {
        try {
            new Thread().sleep(tempo);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void iniciarPartida() {
        System.out.println("Representante do jogador diz: iniciarPartida");
    }

    @Override
    public void desistirDeProcurarPartida() {
        System.out.println("Representante do jogador diz: desistirDeProcurarPartida");
    }

    @Override
    public void encerrarPartida() {
        System.out.println("Representante do jogador diz: encerrarPartida");
    }

    
    @Override
    public void andarParaCima() {
        System.out.println("Representante do jogador diz: andarParaCima");
    }

    @Override
    public void andarParaBaixo() {
        System.out.println("Representante do jogador diz: andarParaBaixo");
    }

    @Override
    public void andarParaEsquerda() {
        System.out.println("Representante do jogador diz: andarParaEsquerda");
    }

    @Override
    public void andarParaDireita() {
        System.out.println("Representante do jogador diz: andarParaDireita");
    }

    @Override
    public double getVD() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Local getLocalAtual() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLocalAtual(Local local) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

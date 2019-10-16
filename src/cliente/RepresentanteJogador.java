package cliente;

import model.agentes.IJogador;
import java.net.Socket;
import localizacoes.Hall;
import stub.GerenciadorDeCliente;
import localizacoes.ILocal;
import model.send.Arena;


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
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: iniciarPartida");
    }

    @Override
    public void desistirDeProcurarPartida() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: desistirDeProcurarPartida");
    }

    @Override
    public void encerrarPartida() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: encerrarPartida");
    }

    
    @Override
    public void andarParaCima() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaCima");
    }

    @Override
    public void andarParaBaixo() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaBaixo");
    }

    @Override
    public void andarParaEsquerda() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaEsquerda");
    }

    @Override
    public void andarParaDireita() {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: andarParaDireita");
    }

    @Override
    public double getVD() {
        double valor = 666.0;
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: getVD -> retornando o valor: " + valor);
        return valor;
    }

    @Override
    public ILocal getLocalAtual() {
        ILocal valor = new Hall();
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: getLocalAtual -> retornando o local: " + valor);
        return valor;
    }

    @Override
    public void setLocalAtual(ILocal local) {
        System.out.println("    === TESTE SERVIDOR === Chamada recebida do cliente: setLocalAtual -> recebendo o local: " + local);
    }

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        
        GerenciadorDeCliente.voceGanhou();
        GerenciadorDeCliente.vocerPerdeu();
        
        GerenciadorDeCliente.adversarioSaiu();
        GerenciadorDeCliente.irParaOHall();
        GerenciadorDeCliente.logar("<login_do_usuario>");
        GerenciadorDeCliente.falhaAoLogar("<mensagem_de_erro>");
        Arena arena = new Arena(10, 10, 5);
        System.out.println("Enviando quadro..." + arena);
        GerenciadorDeCliente.entregarQuadro(arena);
    }
    

    
}

package Sessao;

import java.net.Socket;

/**
 *
 * @author marcelo
 */
public class Sessao implements Runnable {

    private Socket socketDoCliente;
    
    public Sessao(Socket socketDoCliente) {
        this.socketDoCliente = socketDoCliente;
    }
    
    @Override
    public void run() {
        System.out.println("thread de trabalho (sessao) comeca a executar.");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

package comunicacao;

import Sessao.Sessao;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author marcelo
 */
public class Listener implements Runnable {
    
    private final int PORTA;
    private ServerSocket socketDoServidor = null;
    private boolean escutar = true;
    
    public Listener(int porta) {
        this.PORTA = porta;
    }
    
    @Override
    public void run() {
        this.abrirSocketDoServidor();
        
        while(this.ouvindo()) {
            Socket socketDoCliente = null;
            
            try {
                socketDoCliente = this.socketDoServidor.accept();
            } catch (IOException e) {
                if(this.ouvindo()) {
                    System.out.println("Servidor pausado") ;
                    return;
                }
                throw new FalhaDeComunicacaoEmTempoRealException("Erro ao aceitar conexao", e);
            }
            
            new Thread(new Sessao(socketDoCliente)).start();
        }    
        
    }
    
    
    private synchronized boolean ouvindo() {
        return this.escutar;
    }

    public synchronized void parar() throws IOException {
        this.escutar = false;
        try {
            this.socketDoServidor.close();
        } catch (IOException e) {
            throw new IOException("Erro ao fechar o Listener", e);
        }
    }

    private void abrirSocketDoServidor() {
        try {
            this.socketDoServidor = new ServerSocket(this.PORTA);
        } catch (IOException e) {
            throw new RuntimeException("Nao é possível abrir um socket na porta " + PORTA + ": " + e.getMessage());
        }
    }
    
}

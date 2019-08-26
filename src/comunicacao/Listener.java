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
    
    private int porta;
    private ServerSocket socketDoServidor = null;
    private boolean escutar = true;
    protected Thread runningThread = null;
    
    public Listener(int porta) {
        this.porta = porta;
    }
    
    @Override
    public void run() {
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        this.abrirSocketDoServidor();
        
        while(!this.servidorPausado()) {
            Socket socketDoCliente = null;
            
            try {
                socketDoCliente = this.socketDoServidor.accept();
            } catch (IOException e) {
                if(this.servidorPausado()) {
                    System.out.println("Servidor pausado") ;
                    return;
                }
                throw new RuntimeException("Erro ao aceitar conexao", e);
            }

                new Thread(new Sessao(socketDoCliente)).start();
            }    
        
    }
    
    private synchronized boolean servidorPausado() {
        return !(this.escutar);
    }

    public synchronized void parar(){
        this.escutar = false;
        try {
            this.socketDoServidor.close();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fechar o servidor", e);
        }
    }

    private void abrirSocketDoServidor() {
        try {
            this.socketDoServidor = new ServerSocket(this.porta);
        } catch (IOException e) {
            throw new RuntimeException("Nao eh possivel abrir um socket na porta " + porta, e);
        }
    }
    
}

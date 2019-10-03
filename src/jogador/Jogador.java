package jogador;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import stub.InterpretadorServidor;


public class Jogador implements Runnable, jogo.Jogador {
    
    private final InterpretadorServidor INTERPRETADOR;
    private final Socket SOCKET;
    
    public Jogador(Socket socket) {
        this.SOCKET = socket;
        
        int portaEscutarUDP = -1; // pegaAleatoria
        InetAddress enderecoCliente = socket.getInetAddress();
        int portaTCPCliente = socket.getPort();
        int portaUDPCliente = -1; // indefinida
        this.INTERPRETADOR = new InterpretadorServidor(portaEscutarUDP, enderecoCliente, portaTCPCliente, portaUDPCliente);
    }

    @Override
    public void run() {
        try {
            this.INTERPRETADOR.iniciar(this.SOCKET);
            
            // UMA GRANDE LOGICA VEM AQUI
            
        } catch(IOException ioe) {
            
        } catch(Exception e) {
        
        }
    }

    @Override
    public void iniciarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void desistirDeProcurarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void encerrarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaCima() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

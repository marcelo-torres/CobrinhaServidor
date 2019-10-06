package cliente;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import stub.GerenciadorDeCliente;


public class RepresentanteJogador implements Runnable, cliente.Jogador {
    
    private final GerenciadorDeCliente GerenciadorDeCliente;
    private final Socket SOCKET;
    
    public RepresentanteJogador(Socket socket) {
        this.SOCKET = socket;
        
        int portaEscutarUDP = 1234; // pegaAleatoria
        InetAddress enderecoCliente = socket.getInetAddress();
        int portaTCPCliente = socket.getPort();
        int portaUDPCliente = 1235; // indefinida
        this.GerenciadorDeCliente = new GerenciadorDeCliente(portaEscutarUDP, enderecoCliente, portaTCPCliente, portaUDPCliente);
    }

    @Override
    public void run() {
        try {
            this.GerenciadorDeCliente.iniciar(this.SOCKET);
            this.GerenciadorDeCliente.algumMetodoQueVaiPrecisarUsarConexaoUDP();
            // UMA GRANDE LOGICA VEM AQUI
            
        } catch(IOException ioe) {
            
        } catch(Exception e) {
        
        }
        
        String[] mensagens = {
            "Mensagem TCP 1 do servidor",
            "Mensagem TCP 2 do servidor",
            "Mensagem TCP 3 do servidor",
            "Mensagem TCP 4 do servidor",
            "Mensagem TCP 5 do servidor",
            "Mensagem TCP 6 do servidor",
            "Mensagem TCP 7 do servidor",
        };
        
        for(String mensagem : mensagens) {
            pausar(100);
            this.GerenciadorDeCliente.enviarMensagemTCPLembrarDeApagarEsteMetodo(mensagem.getBytes());
        }
        
        String[] mensagensUDP = {
            "Mensagem UDP 1 do servidor",
            "Mensagem UDP 2 do servidor",
            "Mensagem UDP 3 do servidor",
            "Mensagem UDP 4 do servidor",
            "Mensagem UDP 5 do servidor",
            "Mensagem UDP 6 do servidor",
            "Mensagem UDP 7 do servidor",
        };
        
        for(String mensagem : mensagensUDP) {
            pausar(100);
            this.GerenciadorDeCliente.enviarMensagemUDPLembrarDeApagarEsteMetodo(mensagem.getBytes());
        }
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

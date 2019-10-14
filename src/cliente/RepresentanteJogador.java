package cliente;

import java.net.Socket;
import stub.GerenciadorDeCliente;
import model.agentes.Jogador;


public class RepresentanteJogador implements Runnable, Jogador {
    
    private final GerenciadorDeCliente GerenciadorDeCliente;
    private final Socket SOCKET;
    
    public RepresentanteJogador(Socket socket) {
        this.SOCKET = socket;
        this.GerenciadorDeCliente = new GerenciadorDeCliente(this, socket);
    }

    @Override
    public void close() {
    
    }
    
    @Override
    public void run() {

        
        /*String[] mensagens = {
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
        }*/
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
    
    
}

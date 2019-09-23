package Sessao;

import comunicacao.Comunicador;
import comunicacao.Comunicador.Modo;
import comunicacao.ComunicadorTCP;
import comunicacao.ComunicadorUDP;
import comunicacao.Interpretador;
import comunicacao.Mensageiro;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author marcelo
 */
public class Sessao implements Runnable {

    private final Mensageiro MENSAGEIRO;
    private final Interpretador INTERPRETADOR;
    
    private Socket socketDoCliente;
    
    private Thread.UncaughtExceptionHandler gerenciadorDeException = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread th, Throwable ex) {
            System.out.println("Uncaught exception: " + ex);
            ex.printStackTrace();
        }
    };
    
    public Sessao(Socket socketDoCliente) {
        this.socketDoCliente = socketDoCliente;
        this.INTERPRETADOR = new Interpretador();
        
        int portaEscutarUDP = -1;
        InetAddress enderecoCliente = obterEnderecoDoCliente();
        int portaTCPServidor = 0;
        int portaUDPServidor = 1235; // eh preciso receber do cliente
        
        this.MENSAGEIRO = new Mensageiro(this.INTERPRETADOR, portaEscutarUDP, enderecoCliente, portaTCPServidor, portaUDPServidor);
    }
    
    @Override
    public void run() {
        
        System.out.println("Iniciando sessao");
        
        
        
        try {
            this.MENSAGEIRO.iniciarTCP(socketDoCliente);
            //this.COMUNICADOR_TCP.iniciar(socketDoCliente);
            //this.COMUNICADOR_UDP.iniciar(enderecoCliente, portaDeEscutaDoCliente);
        } catch(IOException ioe) {
            System.out.println("[Sessao] Erro ao iniciar o comunicador: ");
            ioe.printStackTrace();
        }
        
        Sessao sessao = this;
        
        new Thread() {
            Sessao s = sessao;
            @Override
            public void run() {
                s.teste1_tcp();
                
            }
        }.start();
        
        new Thread() {
            Sessao s = sessao;
            @Override
            public void run() {
                s.teste2_tcp();
            }
        }.start();
        
        new Thread() {
            Sessao s = sessao;
            @Override
            public void run() {
                s.teste3_udp();
            }
        }.start();
        

        
    }
    
    // Testte
    private InetAddress obterEnderecoDoCliente() {
        InetAddress enderecoCliente = null;
        try {
            enderecoCliente = InetAddress.getByName("127.0.0.1");
        } catch(UnknownHostException uhe) {
            throw new RuntimeException("Erro: " + uhe.getMessage());
        }
        return enderecoCliente;
    }
    
    // Teste
    private void pausar(int tempo) {
        try {
            new Thread().sleep(tempo);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void teste1_tcp() {
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
            this.MENSAGEIRO.enviarMensagemTCP(mensagem.getBytes());
        }
    }
    
    public void teste2_tcp() {
        String[] mensagens = new String[]{
            "Mensagem 8 do servidor",
            "Mensagem 9 do servidor",
            "Mensagem 10 do servidor",
        };
        
        for(String mensagem : mensagens) {
            pausar(300);
            this.MENSAGEIRO.enviarMensagemTCP(mensagem.getBytes());
        }
    }
    
    public void teste3_udp() {
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
            this.MENSAGEIRO.enviarMensagemUDP(mensagem.getBytes());
        }
    }
}

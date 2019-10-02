package stub.comunicacao;

import Logger.Logger;
import static Logger.Logger.Tipo.INFO;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Gerencia uma comunicacao TCP entre o cliente e o servidor. O envio e o
 * recebimento sao divididos entre duas threads diferentes.
 */
public class ComunicadorUDP extends Comunicador implements Closeable {
    
    /**
     * Responsavel pelo recebimento de mensagens. Quando uma mensagem eh
     * recebida eh inserida na fila de recebimento.
     */
    private static class Receptor extends ThreadEscrava { 
    
        private final DatagramSocket SOCKET;
        private final int TAMANHO_DA_MENSAGEM;
        private final Mensageiro MENSAGEIRO;
        
        public Receptor(DatagramSocket socket,
                int tamanhoDaMensagem, 
                Mensageiro mensageiro) 
                throws IOException {
            
            this.SOCKET = socket;
            this.TAMANHO_DA_MENSAGEM = tamanhoDaMensagem;
            this.MENSAGEIRO = mensageiro;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {
                try {
                    byte[] mensagem = new byte[this.TAMANHO_DA_MENSAGEM]; 
                    DatagramPacket pacote = new DatagramPacket(mensagem, mensagem.length);
                    
                    // Loop de escuta
                    do {
                        try {
                            this.SOCKET.receive(pacote);
                            break;
                        } catch(SocketTimeoutException ste) {
                            if(!this.emExecucao()) break;
                        } catch(java.net.SocketException se) {
                            throw new FalhaDeComunicacaoEmTempoRealException("Erro no socket: " + se.getMessage());
                        }
                    } while(true);
                    
                    if(this.emExecucao()) {
                        byte[] dados = pacote.getData();
                        if(dados != null) {
                            this.MENSAGEIRO.inserirFilaRecebimento(dados);
                        }
                    }
                } catch(EOFException eofe) { 
                    throw new FalhaDeComunicacaoEmTempoRealException("Conexao fechada: " + eofe.getMessage());
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                    throw new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel receber a mensagem: " + ioe.getMessage());
                }
            }
        }
    }
    
    
    /**
     * Faz o envio da mensagem para o destino, retirando as mensagens da fila
     * acessada atraves de um objeto da classe Mensageiro.
     */
    private static class Enviador extends ThreadEscrava implements Runnable {
        
        private final DatagramSocket SOCKET;
        private final InetAddress ENDERECO_SERVIDOR;
        private final int PORTA_SERVIDOR;
        private final Mensageiro MENSAGEIRO;
        
        public Enviador(DatagramSocket socket,
                InetAddress enderecoServidor,
                int porta, 
                Mensageiro mensageiro) 
                throws IOException {
            
            this.SOCKET = socket;
            this.ENDERECO_SERVIDOR = enderecoServidor;
            this.PORTA_SERVIDOR = porta;
            this.MENSAGEIRO = mensageiro;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {    
                byte[] mensagem = this.MENSAGEIRO.removerFilaEnvioUDP();
                if(mensagem != null) {
                    try {
                        DatagramPacket pacote = new DatagramPacket(
                                                        mensagem,
                                                        mensagem.length,
                                                        this.ENDERECO_SERVIDOR,
                                                        this.PORTA_SERVIDOR);
                                                    
                        this.SOCKET.send(pacote);
                    } catch(IOException ioe) {
                        throw new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel enviar a mensagem: " + ioe.getMessage());
                    }
                }
            }
        }
    }
    
    /* ###################################################################### */
    
    private DatagramSocket socket;
    
    private InetAddress enderecoServidor;
    private int portaServidor;
    private final int PORTA_ESCUTA;
    
    private Thread threadReceptor;
    private Thread threadEnviador;
    private Receptor receptor;
    private Enviador enviador; 
    private UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
    
    private final int TAMANHO_DA_MENSAGEM;
    private final int TEMPO_LIMITE_ESCUTA = 100; //ms
    
    public ComunicadorUDP(Modo modo,
            Mensageiro mensageiro,
            UncaughtExceptionHandler gerenciadorDeException,
            int tamanhoDaMensagem,
            int PORTA_ESCUTA) {
        
        super(modo, mensageiro);
        
        this.validarArgumentos(gerenciadorDeException, tamanhoDaMensagem);
        this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
        this.TAMANHO_DA_MENSAGEM = tamanhoDaMensagem;
        this.PORTA_ESCUTA = PORTA_ESCUTA;
    }
    
    private void validarArgumentos(
            UncaughtExceptionHandler gerenciadorDeException,
            int tamanhoDaMensagem) {
        if(gerenciadorDeException == null) {
            throw new IllegalArgumentException("O gerenciador de exception nao pode ser nulo");
        }
        if(tamanhoDaMensagem < 0) {
            throw new IllegalArgumentException("O tamanho da mensagem nao pode ser menor do que 1");
        }
    }
    
    @Override
    public void iniciar(InetAddress enderecoServidor, int portaServidor) throws IOException {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Iniciando comunicador.");
        if(this.PORTA_ESCUTA < 1) {
            this.socket = new DatagramSocket();
        } else {
            this.socket = new DatagramSocket(this.PORTA_ESCUTA);
        }
        this.socket.setSoTimeout(this.TEMPO_LIMITE_ESCUTA);
        
        this.enderecoServidor = enderecoServidor;
        this.portaServidor = portaServidor;
        this.prepararThreadsDeComunicacao();
        if(super.MODO == Modo.CLIENTE) {
            Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Iniciando thread de recepcao 1");
            this.threadReceptor.start();
        } else {
            Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Iniciando thread de envio 2");
            this.threadEnviador.start();
        }
    }

    public void encerrarComunicacao() {
        this.receptor.pararExecucao();
        this.enviador.pararExecucao();
    }
    
    @Override
    public void close() throws IOException {
        this.socket.close();
        this.threadReceptor.interrupt();
        this.threadEnviador.interrupt();
    }
    
    private void prepararThreadsDeComunicacao() throws IOException {
        try {
            this.enviador = new Enviador(
                    this.socket, 
                    this.enderecoServidor, 
                    this.portaServidor, 
                    super.MENSAGEIRO);
            
            this.receptor = new Receptor(
                    this.socket, 
                    this.TAMANHO_DA_MENSAGEM, 
                    super.MENSAGEIRO);
                
            this.criarThreadReceptor();
            this.criarThreadEnviador();
        } catch(IOException ioe) {
            throw new IOException("Nao eh possivel criar as threads de comunicacao: " + ioe.getMessage());
        }
    }
    
    private void criarThreadReceptor() {
        this.threadReceptor = new Thread(this.receptor);
        this.threadReceptor.setUncaughtExceptionHandler(GERENCIADOR_DE_EXCEPTION);
        this.threadReceptor.setName("Receptor_UDP");
    }
    
    private void criarThreadEnviador() {
        this.threadEnviador = new Thread(this.enviador);
        this.threadEnviador.setUncaughtExceptionHandler(GERENCIADOR_DE_EXCEPTION);
        this.threadEnviador.setName("Enviador_UDP");
    }
}
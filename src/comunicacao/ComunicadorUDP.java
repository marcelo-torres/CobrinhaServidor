package comunicacao;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ComunicadorUDP extends Comunicador implements Closeable {
    
    /**
     * Responsavel pelo recebimento de mensagens. Quando uma mensagem eh
     * recebida encaminha para o ReceptorDeMensagem associado.
     */
    private static class Receptor implements Runnable { 
    
        private final DatagramSocket SOCKET;
        private final int TAMANHO_DA_MENSAGEM;
        private final ReceptorDeMensagem<byte[]> RECEPTOR_DE_MENSAGEM;
    
        private boolean executando;
        
        public Receptor(DatagramSocket socket,
                int tamanhoDaMensagem, 
                ReceptorDeMensagem<byte[]>  receptorDeMensagem) 
                throws IOException {
            
            this.SOCKET = socket;
            this.TAMANHO_DA_MENSAGEM = tamanhoDaMensagem;
            this.RECEPTOR_DE_MENSAGEM = receptorDeMensagem;
            
            this.executando = true;
        }
        
        @Override
        public void run() {
            
            while(this.emExecucao()) {
                
                try {
                    byte[] mensagem = new byte[this.TAMANHO_DA_MENSAGEM]; 
                    DatagramPacket pacote = new DatagramPacket(mensagem, mensagem.length);
                    this.SOCKET.receive(pacote);
                    this.RECEPTOR_DE_MENSAGEM.receberMensagem(pacote.getData());
                } catch(EOFException eofe) { 
                    throw new FalhaDeComunicacaoEmTempoRealException("Conexão fechada: " + eofe.getMessage());
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                    throw new FalhaDeComunicacaoEmTempoRealException("Não foi possível receber a mensagem: " + ioe.getMessage());
                }
            }
        }
        
        public synchronized boolean emExecucao() {
            return this.executando;
        }
        
        public synchronized void pararExecucao() {
            this.executando = false;
        }
    }
    
    
    /**
     * Faz o envio da mensagem para o destino. O metodo run() executa enquanto
     * houverem mensagens na fila aguardando para serem enviadas. Tambem eh
     * possivel que o metodo run() seja interrompido pelos metodo
     * pararExecucao() ou close().
     */
    private static class Enviador implements Runnable {
        
        private final DatagramSocket SOCKET;
        private final InetAddress ENDERECO_SERVIDOR;
        private final int PORTA_SERVIDOR;
        private final GerenciadorDeFilaDeMensagens MENSAGENS_PARA_ENVIAR;
        
        private boolean executando;
        
        public Enviador(DatagramSocket socket,
                InetAddress enderecoServidor,
                int porta, 
                GerenciadorDeFilaDeMensagens mensagensParaEnviar) 
                throws IOException {
            
            this.SOCKET = socket;
            this.ENDERECO_SERVIDOR = enderecoServidor;
            this.PORTA_SERVIDOR = porta;
            this.MENSAGENS_PARA_ENVIAR = mensagensParaEnviar;
        }
        
        @Override
        public void run() {
            this.executando = (this.MENSAGENS_PARA_ENVIAR.tamanho() > 0);
            
            while(this.emExecucao()) {    
                byte[] mensagem = this.MENSAGENS_PARA_ENVIAR.remover();
                if(mensagem != null) {
                    try {
                        DatagramPacket pacote = new DatagramPacket(
                                                        mensagem,
                                                        mensagem.length,
                                                        this.ENDERECO_SERVIDOR,
                                                        this.PORTA_SERVIDOR);
                                                    
                        this.SOCKET.send(pacote);
                    } catch(IOException ioe) {
                        throw new FalhaDeComunicacaoEmTempoRealException("Não foi possível enviar a mensagem: " + ioe.getMessage());
                    }
                } 
                
                // Dorme para dar tempo de outras mensagens serem colocadas na fila
                this.esperar(10);
                
                this.executando = (this.MENSAGENS_PARA_ENVIAR.tamanho() > 0);
            }
        }
        
        public synchronized boolean emExecucao() {
            return this.executando;
        }
        
        public synchronized void pararExecucao() {
            this.executando = false;
        }
        
        private void esperar(int tempo) {
            try {
                new Thread().sleep(tempo);
            } catch(InterruptedException ie) {
                // Nao faz nada
            }
        }
    }
    
    /* ###################################################################### */
    
    private DatagramSocket socket;
    
    private InetAddress enderecoServidor;
    private int portaServidor;
    private final int PORTA_CLIENTE;
    
    private Thread threadReceptor;
    private Thread threadEnviador;
    private Receptor receptor;
    private Enviador enviador; 
    private UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
    
    private final int TAMANHO_DA_MENSAGEM;
    
    public ComunicadorUDP(Modo modo,
            ReceptorDeMensagem<byte[]> receptorDeMensagem,
            UncaughtExceptionHandler gerenciadorDeException,
            int tamanhoDaFilaDeEnvio,
            int tamanhoDaMensagem,
            int portaCliente) {
        
        super(modo, receptorDeMensagem, 10);
        
        if(gerenciadorDeException == null) {
            throw new IllegalArgumentException("O gerenciador de exception não pode ser nulo");
        }
        this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
        
        if(tamanhoDaMensagem < 0) {
            throw new IllegalArgumentException("O tamanho da mensagem não pode ser menor do que 1");
        }
        this.TAMANHO_DA_MENSAGEM = tamanhoDaMensagem;
        this.PORTA_CLIENTE = portaCliente;
    }
    
    @Override
    public void iniciar(InetAddress enderecoServidor, int portaServidor) throws IOException {
        if(this.PORTA_CLIENTE < 1) {
            this.socket = new DatagramSocket();
        } else {
            this.socket = new DatagramSocket(this.PORTA_CLIENTE);
        }
        
        this.enderecoServidor = enderecoServidor;
        this.portaServidor = portaServidor;
        this.prepararThreadsDeComunicacao();
        if(super.MODO == Modo.CLIENTE) {
            this.threadReceptor.start();
        }
        this.aberto = true;
    }
    
    @Override
    public synchronized void enviarMensagem(byte[] mensagem) {
        super.MENSAGENS_PARA_ENVIAR.inserir(mensagem);
        
        // Uma thread de envio permanece ativa apenas enquanto existem mensagens
        // na fila ou ate ser interrompida.
        if(!this.threadEnviador.isAlive()) {
            this.criarThreadEnviador();
            this.threadEnviador.start();
        }
    }

    @Override
    public void close() throws IOException {
        this.aberto = false;
        this.socket.close();
    }
    
    private void prepararThreadsDeComunicacao() throws IOException {
        try {
            this.enviador = new Enviador(
                    this.socket, 
                    this.enderecoServidor, 
                    this.portaServidor, 
                    super.MENSAGENS_PARA_ENVIAR);
            
            this.receptor = new Receptor(
                    this.socket, 
                    this.TAMANHO_DA_MENSAGEM, 
                    super.RECEPTOR_DE_MENSAGEM);
                
            this.criarThreadReceptor();
            this.criarThreadEnviador();
        } catch(IOException ioe) {
            throw new IOException("Não é possível criar as threads de comunicação: " + ioe.getMessage());
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

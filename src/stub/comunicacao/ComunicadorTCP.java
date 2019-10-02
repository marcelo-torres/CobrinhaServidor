package stub.comunicacao;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import static Logger.Logger.Tipo.INFO;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Gerencia uma comunicacao TCP entre o cliente e o servidor. O envio e o
 * recebimento sao divididos entre duas threads diferentes.
 */
public class ComunicadorTCP extends Comunicador implements Closeable {
    
    /**
     * Responsavel pelo recebimento de mensagens. Quando uma mensagem eh
     * recebida eh inserida na fila de recebimento.
     */
    private static class Receptor extends ThreadEscrava implements Closeable { 
    
        private final ObjectInputStream ENTRADA;
        private final Mensageiro MENSAGEIRO;
        private final ControladorKeepAlive CONTROLADOR_KEEP_ALIVE;
        
        public Receptor(
                ObjectInputStream entrada, 
                Mensageiro mensageiro,
                ControladorKeepAlive controladorKeepAlive) {
            
            this.ENTRADA = entrada;
            this.MENSAGEIRO = mensageiro;
            this.CONTROLADOR_KEEP_ALIVE = controladorKeepAlive;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {   
                try {                  
                    MensagemComunicador envelope = (MensagemComunicador) this.ENTRADA.readObject();
                    this.CONTROLADOR_KEEP_ALIVE.incrementarQuantidadeDeMensagensRecebidas();
                    
                    switch(envelope.getTipoMensagem()) {
                        case KEEP_ALIVE:
                            Logger.registrar(INFO, "Um Keep Alive chegou.");
                            break;
                            
                        case MENSAGEM_COMUM:
                            byte[] mensagem = envelope.getConteudo();
                            this.MENSAGEIRO.inserirFilaRecebimento(mensagem);
                            break;
                        default:
                            throw new FalhaDeComunicacaoEmTempoRealException("Tipo de mensagem desconhecida");
                    }
                } catch(EOFException eofe) { 
                    throw new FalhaDeComunicacaoEmTempoRealException("Conexao fechada: " + eofe.getMessage());
                } catch(IOException ioe) {
                    throw new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel receber a mensagem: " + ioe.getMessage());
                } catch(ClassNotFoundException cnfe) {
                    throw new RuntimeException("Classe nao encontrada: " + cnfe.getMessage());
                }
            }
        }

        @Override
        public void close() throws IOException {
            if(super.emExecucao()) super.pararExecucao();
            this.ENTRADA.close();
        }
    }
    
    
    /**
     * Faz o envio da mensagem para o destino, retirando as mensagens da fila
     * acessada atraves de um objeto da classe Mensageiro
     */
    private static class Enviador extends ThreadEscrava implements Closeable {
        
        private final EnviadorTCP ENVIADOR_TCP;
        private final Mensageiro MENSAGEIRO;
        
        public Enviador(
                EnviadorTCP enviadorTCP, 
                Mensageiro mensageiro) {
            
            this.ENVIADOR_TCP = enviadorTCP;
            this.MENSAGEIRO = mensageiro;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {
                byte[] mensagem = this.MENSAGEIRO.removerFilaEnvioTCP();
                if(mensagem != null) {
                    try {
                        MensagemComunicador envelope = new MensagemComunicador(TipoMensagem.MENSAGEM_COMUM, mensagem);
                        this.ENVIADOR_TCP.enviar(envelope);
                    } catch(IOException ioe) {
                        throw new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel enviar a mensagem: " + ioe.getMessage());
                    }
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            if(super.emExecucao()) super.pararExecucao();
            this.ENVIADOR_TCP.close();
        }
    }
    
    /* ###################################################################### */
    
    private Socket socket;  
    
    private Thread threadReceptor;
    private Thread threadEnviador;
    private Receptor receptor;
    private Enviador enviador;
    private EnviadorKeepAlive enviadorKeepAlive;
    private ControladorKeepAlive controladorKeepAlive;
    private UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
    
    private final int TEMPO_MS_LIMITE_ESPERA_FECHAR_CONEXAO = (4)/*S*/ * (1000)/*MS*/;
    private final int RECEBIMENTO_TEMPO_MS_LIMITE_KEEP_ALIVE = (10)/*S*/ * (1000)/*MS*/;
    private final int RECEBIMENTO_QUANTIDADE_MENSAGENS_KEEP_ALIVE = 2;
    
    private final int ENVIO_TEMPO_MS_LIMITE_KEEP_ALIVE = (10)/*S*/ * (1000)/*MS*/;
    private final int ENVIO_QUANTIDADE_MENSAGENS_KEEP_ALIVE = 5;
    
    public ComunicadorTCP(Modo modo,
            Mensageiro mensageiro,
            UncaughtExceptionHandler gerenciadorDeException) {

            super(Comunicador.Modo.SERVIDOR, mensageiro);
            
            this.validarArgumentos(gerenciadorDeException);
            this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
    }
    
    private void validarArgumentos(UncaughtExceptionHandler gerenciadorDeException) {
        if(gerenciadorDeException == null) {
            throw new IllegalArgumentException("O gerenciador de exception não pode ser nulo");
        }
    }
    
    
    @Override
    public void iniciar(InetAddress enderecoServidor, int portaServidor) throws IOException {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Iniciando comunicador.");
        Socket socketNovo = this.abrirSocket(enderecoServidor, portaServidor);
        this.carregarSocket(socketNovo);
        this.prepararThreadsDeComunicacao();
        this.iniciarThreadDeComunicacao();
        this.controladorKeepAlive.iniciar();
        this.enviadorKeepAlive.iniciar();
    }
    
    public void iniciar(Socket socketNovo) throws IOException {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Iniciando comunicador.");
        this.carregarSocket(socketNovo);
        this.prepararThreadsDeComunicacao();
        this.iniciarThreadDeComunicacao();
        this.controladorKeepAlive.iniciar();
        this.enviadorKeepAlive.iniciar();
    }
    
    public void encerrarConexao() {
        /*this.controladorKeepAlive.encerrar();
        try {
            this.enviador.enviarPedidoFechamentoDaConexao();
            new Thread().sleep(this.TEMPO_MS_LIMITE_ESPERA_FECHAR_CONEXAO);
        } catch(InterruptedException ie) {
            Logger.registrar(ERRO, "Erro ao aguardar o tempo de encerrar a conexao: " + ie.getMessage(), ie);
        } catch(IOException ioe) {
            Logger.registrar(ERRO, ioe.getMessage(), ioe);
            ioe.printStackTrace();
        }
        */
        this.enviador.pararExecucao();
        this.controladorKeepAlive.encerrar();
        this.enviadorKeepAlive.encerrar();
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.enviador.close();
            this.receptor.close();
            this.socket.close();
        } catch(SocketException sk) {
            Logger.registrar(ERRO, sk.getMessage(), sk);
        } catch(IOException ioe) {
            Logger.registrar(ERRO, ioe.getMessage(), ioe);
        }
            
        this.threadEnviador.interrupt();
        this.threadReceptor.interrupt();
    }
    
    private Socket abrirSocket(InetAddress enderecoServidor, int portaServidor) throws IOException {
        Socket novoSocket = null;
        try {
            novoSocket = new Socket(enderecoServidor, portaServidor);
        } catch (IOException ioe) {
            throw new IOException ("Nao eh possivel se conectar ao servidor", ioe);
        }
        return novoSocket;
    }
    
    private void carregarSocket(Socket socket) {
        if(socket == null) {
            throw new IllegalArgumentException("O socket nao pode ser nulo");
        }
        if(socket.isClosed()) {
            throw new IllegalArgumentException("O socket nao pode estar fechado");
        }
        this.socket = socket;
    }
    
    private void prepararThreadsDeComunicacao() throws IOException {
        ObjectOutputStream saida = null;
        ObjectInputStream entrada = null;
        
        try {
            if(null == super.MODO) {
                throw new RuntimeException("Nao eh possivel prepararar as threads de comunicacacao");
            } else switch (super.MODO) {
                case SERVIDOR:
                    saida = new ObjectOutputStream(socket.getOutputStream());
                    entrada = new ObjectInputStream(socket.getInputStream());
                    break;
                case CLIENTE:
                    entrada = new ObjectInputStream(socket.getInputStream());
                    saida = new ObjectOutputStream(socket.getOutputStream());
                    break;
                default:
                    throw new RuntimeException("Nao eh possivel prepararar as threads de comunicacacao");
            }
        } catch(IOException ioe) {
            throw new IOException("Nao eh possivel estabelecer a comunicacao: " + ioe.getMessage());
        }
        
        EnviadorTCP enviadorTCP = new EnviadorTCP(saida);
        
        this.enviadorKeepAlive = new EnviadorKeepAlive(this.GERENCIADOR_DE_EXCEPTION, enviadorTCP, this.ENVIO_QUANTIDADE_MENSAGENS_KEEP_ALIVE, this.ENVIO_TEMPO_MS_LIMITE_KEEP_ALIVE);
        this.controladorKeepAlive = new ControladorKeepAlive(this.GERENCIADOR_DE_EXCEPTION,this.RECEBIMENTO_TEMPO_MS_LIMITE_KEEP_ALIVE, this.RECEBIMENTO_QUANTIDADE_MENSAGENS_KEEP_ALIVE);
        
        this.enviador = new Enviador(enviadorTCP, super.MENSAGEIRO);
        this.receptor = new Receptor(entrada, super.MENSAGEIRO, this.controladorKeepAlive);
        
        this.threadEnviador = this.criarThread(this.enviador, "Receptor_TCP", GERENCIADOR_DE_EXCEPTION);
        this.threadReceptor = this.criarThread(this.receptor, "Enviador_TCP", GERENCIADOR_DE_EXCEPTION);
    }
    
    private Thread criarThread(
            Runnable runnable,
            String nome,
            UncaughtExceptionHandler uncaughtExceptionHandler) {
        
        Thread thread = new Thread(runnable, nome);
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        
        return thread;
    }
    
    private void iniciarThreadDeComunicacao() {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Iniciando thread de envio.");
        this.threadEnviador.start();
        Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Iniciando thread de recepcao.");
        this.threadReceptor.start();
    }
}
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
    private static class Receptor extends Comunicador.ThreadEscrava implements Closeable { 
    
        private final ObjectInputStream ENTRADA;
        private final Mensageiro MENSAGEIRO;
        private final Comunicador.ControladorKeepAlive CONTROLADOR_KEEP_ALIVE;
        private final Enviador ENVIADOR;
        
        public Receptor(
                ObjectInputStream entrada, 
                Mensageiro mensageiro,
                Comunicador.ControladorKeepAlive controladorKeepAlive,
                Enviador enviador) {
            
            this.ENTRADA = entrada;
            this.MENSAGEIRO = mensageiro;
            this.CONTROLADOR_KEEP_ALIVE = controladorKeepAlive;
            this.ENVIADOR = enviador;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {   
                try {                  
                    Comunicador.MensagemComunicador envelope = (Comunicador.MensagemComunicador) this.ENTRADA.readObject();
                    this.CONTROLADOR_KEEP_ALIVE.incrementarQuantidadeDeMensagensRecebidas();
                    
                    switch(envelope.getTipoMensagem()) {
                        case KEEP_ALIVE:
                            Logger.registrar(INFO, "Um Keep Alive chegou.");
                            break;
                            
                        case MENSAGEM_COMUM:
                            byte[] mensagem = envelope.getConteudo();
                            this.MENSAGEIRO.inserirFilaRecebimento(mensagem);
                            break;
                            
                        case PEDIR_FECHAMENTO_CONEXAO:
                            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Pedido de fechamento recebido. Enviando ack de fechamento");
                            this.ENVIADOR.enviarAckFechamento();
                            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Encerrando thread de recepcao");
                            super.pararExecucao();
                            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Encerrando thread de envio");
                            this.ENVIADOR.pararExecucao();
                            break;
                            
                        case ACK_FECHAR_CONEXAO:
                            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Ack de fechamento recebido, parando thread de recebimento");
                            super.pararExecucao();
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
    private static class Enviador extends Comunicador.ThreadEscrava implements Closeable {
       
        private final ComunicadorTCP COMUNICADOR;
        private final Comunicador.SynchronizedObjectOutputStreamWrapper WRAPPED_OUTPUT;
        private final Mensageiro MENSAGEIRO;
        
        public Enviador(
                ComunicadorTCP comunicador,
                Comunicador.SynchronizedObjectOutputStreamWrapper enviadorTCP, 
                Mensageiro mensageiro) {
            
            this.COMUNICADOR = comunicador;
            this.WRAPPED_OUTPUT = enviadorTCP;
            this.MENSAGEIRO = mensageiro;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {
                byte[] mensagem = this.MENSAGEIRO.removerFilaEnvioTCP();
                if(mensagem != null) {
                    try {
                        Comunicador.MensagemComunicador envelope = new Comunicador.MensagemComunicador(this.COMUNICADOR.incrementarEObterNumeroDeSequenciaDeEnvio(), Comunicador.TipoMensagem.MENSAGEM_COMUM, mensagem);
                        this.WRAPPED_OUTPUT.writeAndFlush(envelope);
                    } catch(IOException ioe) {
                        throw new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel enviar a mensagem: " + ioe.getMessage());
                    }
                }
            }
        }
        
        @Override
        public void close() throws IOException {
            if(super.emExecucao()) super.pararExecucao();
            this.WRAPPED_OUTPUT.close();
        }
        
        public void enviarIniciarFechamentoDeConexao() throws IOException {
            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Enviando fechamento de conexao");
            Comunicador.MensagemComunicador envelope = new Comunicador.MensagemComunicador(this.COMUNICADOR.incrementarEObterNumeroDeSequenciaDeEnvio(), Comunicador.TipoMensagem.PEDIR_FECHAMENTO_CONEXAO, new byte[0]);
            this.WRAPPED_OUTPUT.writeAndFlush(envelope);
            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Encerrando thread de envio");
            super.pararExecucao();
        }
        
        public void enviarAckFechamento() throws IOException {
            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Encerrando keep alive");
            this.COMUNICADOR.encerrarKeepAlive();
            Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Enviando ack de fechamento");
            Comunicador.MensagemComunicador envelope = new Comunicador.MensagemComunicador(this.COMUNICADOR.incrementarEObterNumeroDeSequenciaDeEnvio(), Comunicador.TipoMensagem.ACK_FECHAR_CONEXAO, new byte[0]);
            this.WRAPPED_OUTPUT.writeAndFlush(envelope);
        }
    }
    
    /* ###################################################################### */
    
    private Socket socket;  
    
    private Thread threadReceptor;
    private Thread threadEnviador;
    private Receptor receptor;
    private Enviador enviador;
    private Comunicador.EnviadorKeepAlive enviadorKeepAlive;
    private Comunicador.ControladorKeepAlive controladorKeepAlive;
    private UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
    
    private final int TEMPO_MS_LIMITE_ESPERA_FECHAR_CONEXAO = (8)/*S*/ * (1000)/*MS*/;
    private final int RECEBIMENTO_TEMPO_MS_LIMITE_KEEP_ALIVE = (10)/*S*/ * (1000)/*MS*/;
    private final int RECEBIMENTO_QUANTIDADE_MENSAGENS_KEEP_ALIVE = 2;
    
    private final int ENVIO_TEMPO_MS_LIMITE_KEEP_ALIVE = (10)/*S*/ * (1000)/*MS*/;
    private final int ENVIO_QUANTIDADE_MENSAGENS_KEEP_ALIVE = 5;
    
    public ComunicadorTCP(Comunicador.Modo modo,
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
    
    /**
     * Inicia o comunicador criando um socket, criando e iniciando as threads de
     * comunicacao, criando um controlador de keep alive e criando um enviador
     * de keep alive.
     * 
     * @param enderecoServidor Endereco do destinatario
     * @param portaServidor Porta de escuta do destinatario
     * @throws IOException Problema ao estabelecer a conexao
     */
    //@Override
    public void iniciar(InetAddress enderecoServidor, int portaServidor) throws IOException {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Iniciando comunicador.");
        Socket socketNovo = this.abrirSocket(enderecoServidor, portaServidor);
        this.carregarSocket(socketNovo);
        this.iniciarServicosBasicos();
        this.estaAberto = true;
    }
    
    /**
     * Inicia o comunicador carregando o socket, criando e iniciando as threads 
     * de comunicacao, criando um controlador de keep alive e criando um enviador
     * de keep alive.
     * 
     * @param socketNovo Socket a ser usado na conexao
     * @throws IOException Problema ao estabelecer a conexao
     */
    public void iniciar(Socket socketNovo) throws IOException {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Iniciando comunicador.");
        this.carregarSocket(socketNovo);
        this.iniciarServicosBasicos();
        this.estaAberto = true;
    }

    /**
     * Retorna a porta em que o Socket esta escutando ou -1 caso o Socket nao
     * tenha sido construido.
     * @return Numero da porta escutada pelo Socket TCP.
     */
    @Override
    public int getPortaDeEscuta() {
        if(this.socket == null) {
            return -1;
        }
        return this.socket.getPort();
    }
    
    
    /**
     * Encerra o servico de keep alive.
     */
    public void encerrarKeepAlive() {
        this.enviadorKeepAlive.encerrar();
        this.controladorKeepAlive.encerrar();
    }
    
    /**
     * Solicita o encerremanto da execucao das threads de envio e recebimento, e
     * encerra as tarefas de envio de keep alive e monitoramento do keep alive.
     * O encerramento das threads pode nao ser imediato.
     */
    public void encerrarConexao() {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_TCP"}, "Encerrando keep alive");
        this.encerrarKeepAlive();

        try {
            if(this.enviador.emExecucao()) {
                this.enviador.enviarIniciarFechamentoDeConexao();
            }
            new Thread().sleep(this.TEMPO_MS_LIMITE_ESPERA_FECHAR_CONEXAO);
        } catch(InterruptedException ie) {
            Logger.registrar(ERRO, "Erro ao aguardar o tempo de encerrar a conexao: " + ie.getMessage(), ie);
        } catch(IOException ioe) {
            Logger.registrar(ERRO, ioe.getMessage(), ioe);
            ioe.printStackTrace();
        }
        
        /*
            redundancia de fechamento: caso as threads nao sejam paradas pelos
            outros metodos que compoem o protocolo de fechamento, havera um
            fechamento pela forca bruta.
        */
        this.enviador.pararExecucao();
        this.receptor.pararExecucao();
    }
    
    /**
     * Fecha a comunicacao de forma definitiva e interrompe as threads.
     */
    @Override
    public void close() {
        this.estaAberto = false;
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
    
    private void iniciarServicosBasicos() throws IOException {
        this.prepararThreadsDeComunicacao();
        this.iniciarThreadDeComunicacao();
        this.controladorKeepAlive.iniciar();
        this.enviadorKeepAlive.iniciar();
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
        
        Comunicador.SynchronizedObjectOutputStreamWrapper enviadorTCP = new Comunicador.SynchronizedObjectOutputStreamWrapper(saida);
        
        this.enviadorKeepAlive = new Comunicador.EnviadorKeepAlive(this, this.GERENCIADOR_DE_EXCEPTION, enviadorTCP, this.ENVIO_QUANTIDADE_MENSAGENS_KEEP_ALIVE, this.ENVIO_TEMPO_MS_LIMITE_KEEP_ALIVE);
        this.controladorKeepAlive = new Comunicador.ControladorKeepAlive(this.GERENCIADOR_DE_EXCEPTION,this.RECEBIMENTO_TEMPO_MS_LIMITE_KEEP_ALIVE, this.RECEBIMENTO_QUANTIDADE_MENSAGENS_KEEP_ALIVE);
        
        this.enviador = new Enviador(this, enviadorTCP, super.MENSAGEIRO);
        this.receptor = new Receptor(entrada, super.MENSAGEIRO, this.controladorKeepAlive, enviador);
        
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
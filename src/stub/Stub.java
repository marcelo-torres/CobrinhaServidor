package stub;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import static Logger.Logger.Tipo.INFO;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import aplicacao.jogo.ErroApresentavelException;
import java.net.Socket;
import stub.comunicacao.Comunicador;
import stub.comunicacao.Mensageiro;

public abstract class Stub implements Closeable {
    
    private final GerenciadorDeException GERENCIADOR_DE_EXCEPTION;
    protected final Mensageiro MENSAGEIRO;
    protected final Interpretador INTERPRETADOR = new Interpretador();
    
    private Receptor receptor;
    private Thread threadDeRecepcao;
    
    public Stub(Comunicador.Modo modo,
                InetAddress enderecoDoServidor,
                int portaTCPDoServidor) {
        
        this.GERENCIADOR_DE_EXCEPTION = new GerenciadorDeException(this);
        
        this.MENSAGEIRO = new Mensageiro(
                modo,
                enderecoDoServidor,
                portaTCPDoServidor,
                this.GERENCIADOR_DE_EXCEPTION);
    }
    
    public abstract void receberMensagem(byte[] mensagem);
    
    protected void iniciar(Socket socket) {
        try {
            this.MENSAGEIRO.iniciarTCP(socket);
            this.iniciarServicoDeRecepcao();
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"STUB"}, "Erro ao tentar iniciar a comunicaca: " + ioe.getMessage(), ioe);
            throw new ErroApresentavelException("Nao foi possivel iniciar a comunicacao com o servidor");
        }
    }
    
    protected void iniciar() {
        try {
            this.MENSAGEIRO.iniciarTCP();
            this.iniciarServicoDeRecepcao();
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"STUB"}, "Erro ao tentar iniciar a comunicaca: " + ioe.getMessage(), ioe);
            throw new ErroApresentavelException("Nao foi possivel iniciar a comunicacao com o servidor");
        }
    }
    
    @Override
    public void close() {
        this.MENSAGEIRO.close();
        this.receptor.parar();
        this.threadDeRecepcao.interrupt();
    }
    
    
    /* ###################################################################### */
    
    protected void prepararThreadDeEntrega() {
        this.receptor = new Receptor(this, this.MENSAGEIRO);
        this.threadDeRecepcao = new Thread(this.receptor);
        this.threadDeRecepcao.setName("Entrega_Mensagem");
    }
    
    protected void iniciarServicoDeRecepcao() {
        if(this.receptor == null || this.threadDeRecepcao == null) {
            this.prepararThreadDeEntrega();
            this.threadDeRecepcao.start();
        }
    }
    
    
    
    /* ############################## CLASSES ############################### */
    
    /**
     * Possui como funcao retirar mensagens da fila de recebimento e entregar
     * ao ControladorDeConexao.
     */
    public static class Receptor implements Runnable {
        
        protected final Stub STUB;
        protected final Mensageiro MENSAGEIRO;
        
        protected boolean emEexecucao = false;
        
        public Receptor(Stub stub, Mensageiro mensageiro) {
            this.STUB = stub;
            this.MENSAGEIRO = mensageiro;
        }
        
        
        @Override
        public void run() {
            this.emEexecucao = true;
            while(this.emExecucao()) {
                byte[] mensagem = this.MENSAGEIRO.removerFilaRecebimento();
                if(mensagem != null) {
                    this.STUB.receberMensagem(mensagem);
                }
            }
        }
        
        
        public synchronized boolean emExecucao() {
            return emEexecucao;
        }
        
        public synchronized void parar() {
            this.MENSAGEIRO.fecharFilaRecebimento();
            this.emEexecucao = false;
        }
    }
    
    
    /**
     * Gerencia a abertura da comunicacao UDP com um outro host. A abertura da
     * comunicacao deve ser sincronizada. 
     */
    public class GerenciadorDeConexaoUDPRemota {
    
        private final Semaphore SEMAFORO_ATICAO_UDP = new Semaphore(0);
        private final Mensageiro MENSAGEIRO;
        private final InetAddress ENDERECO_DO_SERVIDOR;
        private final Interpretador INTERPRETADOR;
        
        private boolean hostProntoParaReceberUDP = false;
        private boolean iniciouProcessoDeAbertura = false;
        
        public GerenciadorDeConexaoUDPRemota(Mensageiro mensageiro, InetAddress enderecoServidor, Interpretador interpretador) {
            this.MENSAGEIRO = mensageiro;
            this.ENDERECO_DO_SERVIDOR = enderecoServidor;
            this.INTERPRETADOR = interpretador;
        }
        
        
        /* ############################ ABERTURA ############################ */
        
        /**
         * Abre o socket UDP local e envia para o host remoto um pedido de
         * abertura do socket UDP junto com o numero da porta de escuta UDP
         * que foi aberta.
         */
        public void iniciarPedidoDeAberturaUDP() {
            try {
                if(!this.MENSAGEIRO.comunicadorUDPEstaAberto()) {
                    this.MENSAGEIRO.iniciarUDP(-1);
                    
                    int portaDeEscutaServidor = this.MENSAGEIRO.getPortaEscutaUDP();
                    byte[] mensagem = this.INTERPRETADOR.codificarAtenderPedidoInicioDeAberturaUDP(portaDeEscutaServidor);
                    
                    this.MENSAGEIRO.inserirFilaEnvioTCPNaFrente(mensagem);
                    this.iniciouProcessoDeAbertura = true;
                }
            } catch(IOException ioe) {
                Logger.registrar(ERRO, new String[]{"STUB"}, "Erro ao tentar iniciar a comunicacao.", ioe);
                this.SEMAFORO_ATICAO_UDP.release();
                throw new RuntimeException("Nao foi possivel iniciar a comunicacao com o host");
            }
        }

        /**
         * Atende ao pedido de outro host de abertura do socket UDP local.
         * Recebe como parametro o numero da porta de escuta UDP do outro host.
         * No final envia um ack que possui o numero da porta que acabou de 
         * ser aberta.
         * 
         * @param portaUDPServidor Porta de escuta UDP do outro host (o que
         *                          iniciou o processo de abertura)
         */
        public void atenderPedidoInicioDeAberturaUDP(int portaUDPServidor) {
            try {
                if(!this.MENSAGEIRO.comunicadorUDPEstaAberto()) {
                    this.MENSAGEIRO.iniciarUDP(-1);
                }
                
                int portaDeEscutaServidor = this.MENSAGEIRO.getPortaEscutaUDP();
                byte[] mensagem = this.INTERPRETADOR.codificarContinuarAberturaUDP(portaDeEscutaServidor);
                
                this.MENSAGEIRO.inserirFilaEnvioTCPNaFrente(mensagem);
                this.MENSAGEIRO.definirDestinatario(ENDERECO_DO_SERVIDOR, portaUDPServidor);
                if(!this.iniciouProcessoDeAbertura) {
                    this.hostProntoParaReceberUDP = true;
                }
            } catch(IOException ioe) {
                Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro ao tentar iniciar a comunicacao.", ioe);
                this.SEMAFORO_ATICAO_UDP.release();
                throw new RuntimeException("Nao foi possivel iniciar a comunicacao com o servidor");
            }
        }

        /**
         * Quando um host iniciar o processo de abrir o socket UDP atraves do
         * metodo iniciarPedidoDeAberturaUDP, o processo termina com este metodo.
         * Este metodo funciona como um ack.
         * 
         * @param portaUDPServidor Numero da porta de escuta UDP aberta pelo host
         *                          remoto.
         */
        public void continuarAberturaUDP(int portaUDPServidor) {
            this.MENSAGEIRO.definirDestinatario(this.ENDERECO_DO_SERVIDOR, portaUDPServidor);
            this.hostProntoParaReceberUDP = true;
            this.SEMAFORO_ATICAO_UDP.release();
            this.iniciouProcessoDeAbertura = false;
            this.hostProntoParaReceberUDP = true;
        }
        
        public void aguardarComunicacaoSerEstabelecida() throws InterruptedException {
            this.SEMAFORO_ATICAO_UDP.acquire();
        }
        
        
        /* ########################### FECHAMENTO ########################### */
        
        public void iniciarFechamentoConexaoUDP() {
        
        }
        
        public void fecharConexaoUDP() {
        
        }
    }
    
    
    /**
     * Gerenciador de exceptions nao capturadas por metodos, isto eh, que
     * ocorreram em outras threads.
     */
    public class GerenciadorDeException implements Thread.UncaughtExceptionHandler {
    
        private final Stub STUB;
        
        public GerenciadorDeException(Stub controlador) {
            this.STUB = controlador;
        }
        
        @Override
        public void uncaughtException(Thread th, Throwable ex) {
            Logger.registrar(ERRO, new String[]{"STUB"}, "Erro na comunicacao: " + ex.getMessage());
            Logger.registrar(INFO, new String[]{"STUB"}, "Fechando STUB devido a falha de comunicacao");
            this.STUB.close();
        }
        
    }
}
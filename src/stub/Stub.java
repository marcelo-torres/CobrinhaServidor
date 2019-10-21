package stub;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import static Logger.Logger.Tipo.INFO;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import model.ErroApresentavelException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import static stub.Stub.GerenciadorDeConexaoUDPRemota.Estado.ATIVADO;
import static stub.Stub.GerenciadorDeConexaoUDPRemota.Estado.ATIVANDO;
import static stub.Stub.GerenciadorDeConexaoUDPRemota.Estado.DESATIVADO;
import stub.comando.Comando;
import stub.comunicacao.Comunicador;
import stub.comunicacao.GerenciadorDePortas;
import stub.comunicacao.Mensageiro;

public abstract class Stub implements Closeable {
    
    private final GerenciadorDeException GERENCIADOR_DE_EXCEPTION;
    protected final Mensageiro MENSAGEIRO;
    protected final Interpretador INTERPRETADOR = new Interpretador(this);
    protected final GerenciadorDePortas GERENCIADOR_DE_PORTAS;
    
    private Receptor receptor;
    private Thread threadDeRecepcao;
    
    public Stub(Comunicador.Modo modo,
                GerenciadorDePortas gerenciadorDePortas,
                InetAddress enderecoDoServidor,
                int portaTCPDoServidor) {
        
        this.GERENCIADOR_DE_EXCEPTION = new GerenciadorDeException(this);
        
        this.MENSAGEIRO = new Mensageiro(
                modo,
                enderecoDoServidor,
                portaTCPDoServidor,
                this.GERENCIADOR_DE_EXCEPTION);
        
        this.GERENCIADOR_DE_PORTAS = gerenciadorDePortas;
    }
    
    public abstract void receberMensagem(byte[] mensagem);
    
    protected abstract LinkedList<Comando> criarComandosNecessarios(); 
    
    protected abstract void devolverRetorno(byte[] mensagemRetorno);
    
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
            //this.MENSAGEIRO.fecharFilaRecebimento();
            this.emEexecucao = false;
        }
    }
    
    
    /**
     * Gerencia a abertura da comunicacao UDP com um outro host. A abertura da
     * comunicacao deve ser sincronizada. 
     */
    public static class GerenciadorDeConexaoUDPRemota {
    
        public static enum Estado {
            ATIVANDO,
            ATIVADO,
            DESATIVADO
        }
        
        private final Semaphore SEMAFORO_ATIVACAO_UDP = new Semaphore(0);
        private final Mensageiro MENSAGEIRO;
        private final InetAddress ENDERECO_DO_SERVIDOR;
        private final Interpretador INTERPRETADOR;
        
        //private boolean hostProntoParaReceberUDP = false;
        //private boolean iniciouProcessoDeAbertura = false;
        //private boolean encerramentoSolicitado = false;
        
        private final GerenciadorDePortas GERENCIADOR_DE_PORTAS;
        private Estado estado = DESATIVADO;
        
        public GerenciadorDeConexaoUDPRemota(
                Mensageiro mensageiro,
                InetAddress enderecoServidor,
                Interpretador interpretador,
                GerenciadorDePortas gerenciadorDePortas) {
            this.MENSAGEIRO = mensageiro;
            this.ENDERECO_DO_SERVIDOR = enderecoServidor;
            this.INTERPRETADOR = interpretador;
            
            this.GERENCIADOR_DE_PORTAS = gerenciadorDePortas;
        }
        
        
        /* ############################ ABERTURA ############################ */
        
        /**
         * Abre o socket UDP local e envia para o host remoto um pedido de
         * abertura do socket UDP junto com o numero da porta de escuta UDP
         * que foi aberta.
         */
        public void iniciarPedidoDeAberturaUDP() {
            int porta = GERENCIADOR_DE_PORTAS.getPorta();
            try {
                if(!this.MENSAGEIRO.comunicadorUDPEstaAberto()) {
                    this.MENSAGEIRO.iniciarUDP(porta);
                    
                    int portaDeEscutaServidor = this.MENSAGEIRO.getPortaEscutaUDP();
                    byte[] mensagem = this.INTERPRETADOR.codificarAtenderPedidoInicioDeAberturaUDP(portaDeEscutaServidor);
                    this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
                    //this.iniciouProcessoDeAbertura = true;
                    this.estado = ATIVANDO;
                    this.SEMAFORO_ATIVACAO_UDP.acquire();
                }
            } catch(IOException ioe) {
                Logger.registrar(ERRO, new String[]{"STUB"}, "Erro ao tentar iniciar a comunicacao: " + ioe.getMessage(), ioe);
                this.SEMAFORO_ATIVACAO_UDP.release();
                throw new RuntimeException("Nao foi possivel iniciar a comunicacao com o host");
            } catch(InterruptedException ie) {
                Logger.registrar(ERRO, new String[]{"STUB"}, "Espera no iniciarPedidoDeAberturaUDP() interrompida", ie);
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
                    this.MENSAGEIRO.iniciarUDP(GERENCIADOR_DE_PORTAS.getPorta());
                }
                
                switch(this.estado) {
                    case DESATIVADO:
                        int portaDeEscutaServidor = this.MENSAGEIRO.getPortaEscutaUDP();
                        byte[] mensagem = this.INTERPRETADOR.codificarContinuarAberturaUDP(portaDeEscutaServidor);
                        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
                        this.MENSAGEIRO.definirDestinatario(ENDERECO_DO_SERVIDOR, portaUDPServidor);
                        this.estado = ATIVADO;
                        break;
                    case ATIVANDO:
                        throw new RuntimeException("Erro stub: n deveria estar ativando");
                        //break;
                    case ATIVADO:
                        throw new RuntimeException("Erro stub: n deveria estar ativado");

                        //break;
                    default:
                        throw new RuntimeException("Estado desconhecido");
                }

                /*if(!this.iniciouProcessoDeAbertura) {
                    this.hostProntoParaReceberUDP = true;
                }*/
            } catch(IOException ioe) {
                Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro ao tentar iniciar a comunicacao.", ioe);
                this.SEMAFORO_ATIVACAO_UDP.release();
                ioe.printStackTrace();
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
            /*if(!this.iniciouProcessoDeAbertura) {
                // Caso o processo de abertura nao esteja mais aberto a mensagem
                // deve ser ignorada.
                return;
            }*/
            if(this.estado != ATIVANDO) {
                return;
            }
            
            this.MENSAGEIRO.definirDestinatario(this.ENDERECO_DO_SERVIDOR, portaUDPServidor);
            this.SEMAFORO_ATIVACAO_UDP.release();
            //this.iniciouProcessoDeAbertura = false;
            //this.hostProntoParaReceberUDP = true;
            this.estado = ATIVADO;
        }
        
        public void aguardarComunicacaoSerEstabelecida() throws InterruptedException {
            //this.SEMAFORO_ATIVACAO_UDP.acquire();
        }
        
        public boolean conexaoEstaEstabelecida() {
            //return this.hostProntoParaReceberUDP;
            return (this.estado == ATIVADO);
        }
        
        
        /* ########################### FECHAMENTO ########################### */
        
        public void iniciarFechamentoConexaoUDP() {
            //this.hostProntoParaReceberUDP = false;
            //this.iniciouProcessoDeAbertura = false;
            this.estado = DESATIVADO;
            this.MENSAGEIRO.encerrarUDP();
            
            byte[] mensagem = this.INTERPRETADOR.codificarFecharConexaoUDP();  
            this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
        }
        
        public void fecharConexaoUDP() {
            /*if(this.iniciouProcessoDeAbertura) {
                // Caso o processo de abertura esteja em andamento e tenha sido
                // iniciado por este host, o pedido de fechamento de conexao
                // deve ser ignorado para evitar inconsistencia
                return;
            }
            this.hostProntoParaReceberUDP = false;
            this.iniciouProcessoDeAbertura = false;*/
            this.estado = DESATIVADO;
            this.GERENCIADOR_DE_PORTAS.liberarPorta(this.MENSAGEIRO.getPortaEscutaUDP());
            this.MENSAGEIRO.encerrarUDP();
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
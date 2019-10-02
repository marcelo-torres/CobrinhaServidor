package stub.comunicacao;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Fornece variaveis e metodos basicos para o funcionamento de um comunicador,
 * cuja funcao eh gerenciar o envio e recebimento concorrentes de mensagens.
 */
public abstract class Comunicador {
    
    /**
     * Modo de execucao de um comunicador.
     */
    public enum Modo {
        SERVIDOR(1), CLIENTE(2);
        
        private final int MODO;
        
        Modo(int valorOpcao){
            MODO = valorOpcao;
        }
        
        public int getModo() {
            return this.MODO;
        }
    }
    
    protected enum TipoMensagem {
        KEEP_ALIVE(0),
        MENSAGEM_COMUM(1),
        FECHAR_CONEXAO(2),
        PEDIR_FECHAMENTO_CONEXAO(3);
        
        private final int TIPO_MENSAGEM;
        
        TipoMensagem(int tipoMensagem) {
            TIPO_MENSAGEM = tipoMensagem;
        }
        
        public int getTipoMensagem() {
            return this.TIPO_MENSAGEM;
        }
    }
    
    protected static class MensagemComunicador implements Serializable {
        
        private final TipoMensagem TIPO_MENSAGEM;
        private final byte[] CONTEUDO;
        
        public MensagemComunicador(TipoMensagem tipoMensagem, byte[] conteudo) {
            this.TIPO_MENSAGEM = tipoMensagem;
            this.CONTEUDO = conteudo;
        }
        
        public TipoMensagem getTipoMensagem() {
            return this.TIPO_MENSAGEM;
        }
        
        public byte[] getConteudo() {
            return this.CONTEUDO;
        }
    }
    
    /**
     * Fornce metodos para gerenciar o estado de execucao de um thread de envio
     * ou recebimento.
     */
    protected abstract static class ThreadEscrava implements Runnable {
        
        private boolean executando = false;
        
        public synchronized void executar() {
            this.executando = true;
        }
        
        public synchronized void pararExecucao() {
            this.executando = false;
        }
        
        public synchronized boolean emExecucao() {
            return this.executando;
        }
    }
    
    protected static class EnviadorTCP implements Closeable {
    
        ObjectOutputStream SAIDA;
        
        public EnviadorTCP(ObjectOutputStream saida) {
            this.SAIDA = saida;
        }
        
        public synchronized void enviar(Object objeto) throws IOException {
            this.SAIDA.writeObject(objeto);
            this.SAIDA.flush();
        }
        
        @Override
        public synchronized void close() throws IOException {
            this.SAIDA.close();
        }
    }
    
    protected static class TarefaEnviarKeepAlive extends TimerTask {
    
        private final UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
        private final EnviadorTCP ENVIADOR_TCP;
        
        public TarefaEnviarKeepAlive(
                EnviadorTCP enviadorTCP,
                UncaughtExceptionHandler gerenciadorDeException) {
            this.ENVIADOR_TCP = enviadorTCP;
            this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
        }
        
        @Override
        public void run() {
            try {
                MensagemComunicador mensagemKeepAlive = new MensagemComunicador(TipoMensagem.KEEP_ALIVE, null);
                this.ENVIADOR_TCP.enviar(mensagemKeepAlive);
            } catch(IOException ioe) {
                FalhaDeComunicacaoEmTempoRealException exception = new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel mandar a mensagem de keep alive: " + ioe.getMessage(), ioe);
                this.GERENCIADOR_DE_EXCEPTION.uncaughtException(Thread.currentThread(), exception);
            } catch(Exception e) {
                this.GERENCIADOR_DE_EXCEPTION.uncaughtException(Thread.currentThread(), e);     
            }
        }
    }
    
    protected static class EnviadorKeepAlive {
    
        private final UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
        private final EnviadorTCP ENVIADOR_TCP;
        private final int INTERVALO_ENVIO;
        
        private ScheduledExecutorService scheduler;
        private ScheduledFuture tarefasRestantes;
        
        public EnviadorKeepAlive(
            UncaughtExceptionHandler gerenciadorDeException,
            EnviadorTCP enviadorTCP,
            int quantidadeDeMensagens,
            int intervaloDeTempo) {
        
            this.validarParametros(gerenciadorDeException, enviadorTCP, quantidadeDeMensagens, intervaloDeTempo);
            
            this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
            this.ENVIADOR_TCP = enviadorTCP;
            this.INTERVALO_ENVIO = (intervaloDeTempo / quantidadeDeMensagens);
            
            this.scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        
        public void iniciar() {
            if(this.tarefasRestantes != null) {
                this.tarefasRestantes.cancel(false);
            }
            
            long delayInicial = 0;
            this.tarefasRestantes = this.scheduler.scheduleAtFixedRate(
                    new TarefaEnviarKeepAlive(this.ENVIADOR_TCP, GERENCIADOR_DE_EXCEPTION),
                    delayInicial,
                    this.INTERVALO_ENVIO,
                    TimeUnit.MILLISECONDS);
        }
        
        public void encerrar() {
            this.tarefasRestantes.cancel(false);
            this.scheduler.shutdownNow();
        }
        
        private void validarParametros(
            UncaughtExceptionHandler gerenciadorDeException,
            EnviadorTCP enviadorTCP,
            int quantidadeDeMensagens,
            int intervaloDeTempo) {
        
            if(gerenciadorDeException == null) {
                throw new IllegalArgumentException("O gerenciador de exceptions nao pode ser nulo");
            }
            
            if(enviadorTCP == null) {
                throw new IllegalArgumentException("O ObjectOutputStream nao pode ser nulo");
            }
            
            if(quantidadeDeMensagens <= 0) {
                throw new IllegalArgumentException("A quantidade de mensagens a ser enviada deve ser > 0");
            }
            
            if(intervaloDeTempo <= 0) {
                throw new IllegalArgumentException("O intervalo de tempo deve ser > 0");
            }
        }
    }
    
    protected static class TarefaValidarConexao extends TimerTask {

        private UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
        private final ControladorKeepAlive CONTROLADOR;
        
        public TarefaValidarConexao(
                UncaughtExceptionHandler gerenciadorDeException,
                ControladorKeepAlive controlador) {
            this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
            this.CONTROLADOR = controlador;
        }
        
        @Override
        public void run() {
            if(!this.CONTROLADOR.podeReiniciar()) {
                FalhaDeComunicacaoEmTempoRealException exception = new FalhaDeComunicacaoEmTempoRealException("Conexao perdida (KeepAlive)");
                this.GERENCIADOR_DE_EXCEPTION.uncaughtException(Thread.currentThread(), exception);
            }
        }
    }
    
    protected static class ControladorKeepAlive {
        
        private UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
        
        private final int LIMITE_TEMPO;
        private final int QUANTIDADE_MENSAGENS; 
        private int quantidadeDeMensagensRecebidas = 0;
        
        private ScheduledExecutorService scheduler;
        private ScheduledFuture tarefasRestantes;
        
        /**
         * 
         * @param limiteDeTempo Tempo limite em ms
         * @param quantidadeDeMensagens Quantidade de mensagens para receber dentro do intervalo
         */
        public ControladorKeepAlive(UncaughtExceptionHandler gerenciadorDeException, int limiteDeTempo, int quantidadeDeMensagens) {
            this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
            this.LIMITE_TEMPO = limiteDeTempo;
            this.QUANTIDADE_MENSAGENS = quantidadeDeMensagens;
            this.scheduler = Executors.newSingleThreadScheduledExecutor();
            //this.scheduler = Executors.newScheduledThreadPool(1);
        }
        
        public void iniciar() {
            this.reiniciar();
        }
        
        public void encerrar() {
            this.tarefasRestantes.cancel(false);
            this.scheduler.shutdownNow();
        }
        
        public synchronized void incrementarQuantidadeDeMensagensRecebidas() {
            this.quantidadeDeMensagensRecebidas++;
            if(this.podeReiniciar()) {
                this.reiniciar();
            }
        }
        
        public synchronized boolean podeReiniciar() {
            return (this.quantidadeDeMensagensRecebidas >= this.QUANTIDADE_MENSAGENS);
        }
        
        private synchronized void reiniciar() {
            if(this.tarefasRestantes != null) {
                this.tarefasRestantes.cancel(false);
            }
            
            this.quantidadeDeMensagensRecebidas = 0; 
            this.tarefasRestantes = this.scheduler.schedule(
                    new TarefaValidarConexao(GERENCIADOR_DE_EXCEPTION, this),
                    this.LIMITE_TEMPO,
                    TimeUnit.MILLISECONDS);
        }
    }

    protected final Modo MODO;
    protected final Mensageiro MENSAGEIRO;
    
    public Comunicador(Modo modo, Mensageiro mensageiro) {
        this.MODO = modo;
        this.MENSAGEIRO = mensageiro;
    }
    
    public abstract void iniciar(InetAddress enderecoServidor, int portaServidor) throws IOException;
}

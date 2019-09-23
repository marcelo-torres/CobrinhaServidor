package comunicacao;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Comunicador {
    
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
                FalhaDeComunicacaoEmTempoRealException exception = new FalhaDeComunicacaoEmTempoRealException("Não é possível se conectar (KeepAlive)");
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
            this.scheduler = Executors.newScheduledThreadPool(1);
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
    
    protected static class ThreadEscrava {
        
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
        RECEBER_MENSAGEM(1),
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

    protected final Modo MODO;
    protected final FilaMonitorada<byte[]> FILA_ENVIO_MENSAGENS;
    protected final FilaMonitorada<byte[]> FILA_RECEBIMENTO_MENSAGENS;
    
    public Comunicador(Modo modo,
            FilaMonitorada<byte[]> filaDeEnvioDeMensagens,
            FilaMonitorada<byte[]> filaDeRecebimentoDeMensagens) {
        
        if(modo == null
                || filaDeEnvioDeMensagens == null
                || filaDeRecebimentoDeMensagens == null) {
            throw new IllegalArgumentException("Não é possível criar o comunicador, parâmetro nulo");
        }
        
        this.MODO = modo;
        this.FILA_ENVIO_MENSAGENS = filaDeEnvioDeMensagens;
        this.FILA_RECEBIMENTO_MENSAGENS = filaDeRecebimentoDeMensagens;
    }
    
    public abstract void iniciar(InetAddress enderecoServidor, int portaServidor) throws IOException;
    
    public abstract void enviarMensagem(byte[] mensagem);
}

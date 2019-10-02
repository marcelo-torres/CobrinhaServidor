package stub.comunicacao;

import stub.InterpretadorServidor;
import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import static Logger.Logger.Tipo.INFO;
import stub.comunicacao.Comunicador.Modo;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Mensageiro implements Closeable {
    
    public static class Entregador implements Runnable {
        
        protected final InterpretadorServidor INTERPRETADOR;
        protected final Mensageiro MENSAGEIRO;
        
        protected boolean emEexecucao = false;
        
        public Entregador(InterpretadorServidor interpretador, Mensageiro mensageiro) {
        
            this.INTERPRETADOR = interpretador;
            this.MENSAGEIRO = mensageiro;
        }
        
        
        @Override
        public void run() {
            this.emEexecucao = true;
            while(this.emExecucao()) {
                byte[] mensagem = this.MENSAGEIRO.removerFilaRecebimento();
                if(mensagem != null) {
                    this.INTERPRETADOR.receberMensagem(mensagem);
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
    
    
    
    private final InterpretadorServidor INTERPRETADOR;
    private final ComunicadorTCP COMUNICADOR_TCP;
    private final ComunicadorUDP COMUNICADOR_UDP;
    
    private final int TAMANHO_FILA_RECEBIMENTO = 200;
    private final FilaMonitorada<byte[]> FILA_RECEBIMENTO_MENSAGENS;
    private final int TAMANHO_FILA_ENVIO_TCP = 100;
    private final FilaMonitorada<byte[]> FILA_ENVIO_MENSAGENS_TCP;  
    private final int TAMANHO_FILA_ENVIO_UDP = 100;
    private final FilaMonitorada<byte[]> FILA_ENVIO_MENSAGENS_UDP;
    
    private final int TAMANHO_MENSAGEM_UDP = 1024;
    
    private final InetAddress ENDERECO_SERVIDOR;
    private final int PORTA_TCP_SERVIDOR;
    private final int PORTA_UDP_SERVIDOR;
    
    private Entregador entregador;
    private Thread threadDeEntrega;
    
    public Mensageiro(
            InterpretadorServidor interpretador,
            Modo modo,
            int portaEscutarUDP,
            InetAddress enderecoDoServidor,
            int portaTCPDoServidor,
            int portaUDPDoServidor) {
    
        this.INTERPRETADOR = interpretador;
        
        this.ENDERECO_SERVIDOR = enderecoDoServidor;
        this.PORTA_TCP_SERVIDOR = portaTCPDoServidor;
        this.PORTA_UDP_SERVIDOR = portaUDPDoServidor;
        
        this.FILA_RECEBIMENTO_MENSAGENS = new FilaMonitorada<>(this.TAMANHO_FILA_RECEBIMENTO);
        this.FILA_ENVIO_MENSAGENS_TCP = new FilaMonitorada<>(this.TAMANHO_FILA_ENVIO_TCP);
        this.FILA_ENVIO_MENSAGENS_UDP = new FilaMonitorada<>(this.TAMANHO_FILA_ENVIO_UDP);
        
        this.COMUNICADOR_TCP = new ComunicadorTCP(
                modo,
                this,
                this.gerenciadorDeException);
        
        this.COMUNICADOR_UDP = new ComunicadorUDP(
                modo,
                this,
                this.gerenciadorDeException,
                this.TAMANHO_MENSAGEM_UDP,
                portaEscutarUDP);
    }
    
    public void iniciarTCP() throws IOException {
        this.COMUNICADOR_TCP.iniciar(this.ENDERECO_SERVIDOR, this.PORTA_TCP_SERVIDOR);
        this.iniciarServicoEntrega();
    }
    
    public void iniciarTCP(Socket socket) throws IOException {
        this.COMUNICADOR_TCP.iniciar(socket);
        this.iniciarServicoEntrega();
    }
    
    public void iniciarUDP() throws IOException {
        this.COMUNICADOR_UDP.iniciar(this.ENDERECO_SERVIDOR, this.PORTA_UDP_SERVIDOR);
        this.iniciarServicoEntrega();
    }
    
    @Override
    public void close() {
        try {
            this.COMUNICADOR_TCP.encerrarConexao();
            this.COMUNICADOR_UDP.encerrarComunicacao();
            try {
            new Thread().sleep(1000);
            } catch(Exception e) {}

            this.COMUNICADOR_TCP.close();
            this.COMUNICADOR_UDP.close();
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"MENSAGEIRO"}, "Erro ao fechar", ioe);
        }
        
        this.entregador.parar();
        if(this.threadDeEntrega.isAlive()) {
            this.threadDeEntrega.interrupt();
        }
    }
    
    
    
    public void inserirFilaRecebimento(byte[] mensagem) {
        this.FILA_RECEBIMENTO_MENSAGENS.adicionar(mensagem);
    }

    public byte[] removerFilaRecebimento() {
        return this.FILA_RECEBIMENTO_MENSAGENS.remover();
    }
    
    public void fecharFilaRecebimento() {
        this.FILA_RECEBIMENTO_MENSAGENS.fechar();
    }


    public void inserirFilaEnvioTCP(byte[] mensagem) {
        this.FILA_ENVIO_MENSAGENS_TCP.adicionar(mensagem);
    }
    
    public void inserirFilaEnvioTCPNaFrente(byte[] mensagem) {
        this.FILA_ENVIO_MENSAGENS_TCP.colocarNoInicio(mensagem);
    }

    public byte[] removerFilaEnvioTCP() {
        return this.FILA_ENVIO_MENSAGENS_TCP.remover();
    }
    
    public void fecharFilaEnvioTCP() {
        this.FILA_ENVIO_MENSAGENS_TCP.fechar();
    }


    public void inserirFilaEnvioUDP(byte[] mensagem) {
        this.FILA_ENVIO_MENSAGENS_UDP.adicionar(mensagem);
    }
    
    public void inserirFilaEnvioUDPNaFrente(byte[] mensagem) {
        this.FILA_ENVIO_MENSAGENS_UDP.colocarNoInicio(mensagem);
    }

    public byte[] removerFilaEnvioUDP() {
        return this.FILA_ENVIO_MENSAGENS_UDP.remover();
    }
    
    public void fecharFilaEnvioUDP() {
        this.FILA_ENVIO_MENSAGENS_UDP.fechar();
    }
    
    
    
    private void prepararThreadDeEntrega() {
        this.entregador = new Entregador(this.INTERPRETADOR, this);
        this.threadDeEntrega = new Thread(this.entregador);
        this.threadDeEntrega.setName("Entrega_Mensagem");
    }
    
    private void iniciarServicoEntrega() {
        if(this.entregador == null || this.threadDeEntrega == null) {
            this.prepararThreadDeEntrega();
            this.threadDeEntrega.start();
        }
    }
    
    
    private Mensageiro mensageiroAuxiliar = this;
    private Thread.UncaughtExceptionHandler gerenciadorDeException = new Thread.UncaughtExceptionHandler() {
        Mensageiro mensageiro = mensageiroAuxiliar;
        public void uncaughtException(Thread th, Throwable ex) {
            System.out.println("[LOG][ERRO] - Erro na conexão: " + ex.getMessage() + " [ENCERRANDO CONEXAO]");
            mensageiro.close();
        }
    };
    
}
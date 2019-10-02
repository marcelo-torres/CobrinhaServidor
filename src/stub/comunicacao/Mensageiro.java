package stub.comunicacao;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import stub.comunicacao.Comunicador.Modo;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Gerencia suas filas monitoradas privadas que sao usadas como buffer de
 * mensagens recebidas e para serem enviadas. Possui uma fila de recebimento 
 * unica (ou seja, para TCP e UDP) no qual as mensagens a serem entregues
 * para a aplicacao destinataria do sistema de entregas de mensagens por redes.
 * As entregas sao realizadas por uma thread iniciada assim que alguma conexao
 * for iniciar e so sera encerrada quando o metodo close() for chamado.
 * 
 * As filas de armazenamento para enviar mensagens via TCP e UDP sao separadas. 
 * Cabe a aplicacao que sua esta classe decidir em qual fila inserir a mensagem.
 * 
 * @author marcelo
 */
public class Mensageiro implements Closeable {
    
    public static class Entregador implements Runnable {
        
        protected final Destinatario INTERPRETADOR;
        protected final Mensageiro MENSAGEIRO;
        
        protected boolean emEexecucao = false;
        
        public Entregador(Destinatario interpretador, Mensageiro mensageiro) {
        
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
    
    
    
    private final Destinatario INTERPRETADOR;
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
            Destinatario interpretador,
            Modo modo,
            int portaEscutarUDP,
            InetAddress enderecoDoServidor,
            int portaTCPDoServidor,
            int portaUDPDoServidor,
            Thread.UncaughtExceptionHandler gerenciadorDeException) {
    
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
                gerenciadorDeException);
        
        this.COMUNICADOR_UDP = new ComunicadorUDP(
                modo,
                this,
                gerenciadorDeException,
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
    
    public void encerrarTCP() throws IOException {
        this.COMUNICADOR_TCP.encerrarConexao();
        this.COMUNICADOR_TCP.close();
    }
    
    
    public void iniciarUDP() throws IOException {
        this.COMUNICADOR_UDP.iniciar(this.ENDERECO_SERVIDOR, this.PORTA_UDP_SERVIDOR);
        this.iniciarServicoEntrega();
    }
    
    public void encerrarUDP() throws IOException {
        this.COMUNICADOR_UDP.encerrarComunicacao();
        this.COMUNICADOR_UDP.close();
    }
    

    
    @Override
    public void close() {
        try {
            this.encerrarTCP();
            this.encerrarUDP();
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
}
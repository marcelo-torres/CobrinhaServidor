package stub.comunicacao;

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
 */
public class Mensageiro implements Closeable {
    
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
    
    /**
     * Cria as filas de mensagem e os comunicadores.
     * 
     * @param modo Modo de execucao dos comunicadores
     * @param portaEscutarUDP Porta para escutar UDP na maquina em questao
     * @param enderecoDoServidor Endereco do servidor com ambos os sockets
     * @param portaTCPDoServidor Porta de escuta TCP no servidor
     * @param portaUDPDoServidor Porta de escuta UDP no servidor
     * @param gerenciadorDeException Handler de exception lancadas em threads
     */
    public Mensageiro(
            Modo modo,
            int portaEscutarUDP,
            InetAddress enderecoDoServidor,
            int portaTCPDoServidor,
            int portaUDPDoServidor,
            Thread.UncaughtExceptionHandler gerenciadorDeException) {
        
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
                this.TAMANHO_MENSAGEM_UDP);
    }
    
    /**
     * Inicia uma conexao TCP com host de endereco e porta definidos no 
     * construtor.
     * 
     * @throws IOException Erro ao iniciar a conexao.
     */
    public void iniciarTCP() throws IOException {
        this.COMUNICADOR_TCP.iniciar(this.ENDERECO_SERVIDOR, this.PORTA_TCP_SERVIDOR);
    }
    
    /**
     * Mantem uma conexao estabelecida com o socket passado por parametro.
     * 
     * @param socket Socket com a conexao
     * @throws IOException Erro ao iniciar a conexao.
     */
    public void iniciarTCP(Socket socket) throws IOException {
        this.COMUNICADOR_TCP.iniciar(socket);
    }
    
    /**
     * Encerra a conexao TCP e fecha o comunicador TCP.
     */
    public void encerrarTCP() {
        this.COMUNICADOR_TCP.encerrarConexao();
        this.COMUNICADOR_TCP.close();
    }
    
    
    /**
     * Configura um comunicador UDP com host de endereco e porta definidos no 
     * construtor, e inicia a thread de entrega de mensagens ao destinatario.
     * 
     * @throws IOException Erro ao configurar o socket
     */
    public void iniciarUDP(int portaDeEscuta) throws IOException {
        this.COMUNICADOR_UDP.iniciar(portaDeEscuta);
        
    }
    
    public void definirDestinatario(InetAddress enderecoDoServidor, int portaDeEscutaUDPDoServidor) {
        this.COMUNICADOR_UDP.definirDestinatario(ENDERECO_SERVIDOR, PORTA_TCP_SERVIDOR);
    }
    
    /**
     * Encerra a conexao TCP e fecha o comunicador TCP.
     */
    public void encerrarUDP() {
        this.COMUNICADOR_UDP.encerrarComunicacao();
        this.COMUNICADOR_UDP.close();
    }
    
    
    public boolean comunicadorTCPEstaAberto() {
        return this.COMUNICADOR_TCP.estaAberto();
    }
    
    public boolean comunicadorUDPEstaAberto() {
        return this.COMUNICADOR_UDP.estaAberto();
    }
    

    public int getPortaEscutaTCP() {
        return this.COMUNICADOR_TCP.getPortaDeEscuta();
    }
    
    public int getPortaEscutaUDP() {
        return this.COMUNICADOR_UDP.getPortaDeEscuta();
    }
    
    
    @Override
    public void close() {
        this.encerrarTCP();
        this.encerrarUDP();
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
}
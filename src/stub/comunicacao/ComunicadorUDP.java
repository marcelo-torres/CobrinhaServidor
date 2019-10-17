package stub.comunicacao;

import Logger.Logger;
import static Logger.Logger.Tipo.INFO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Gerencia uma comunicacao TCP entre o cliente e o servidor. O envio e o
 * recebimento sao divididos entre duas threads diferentes.
 */
public class ComunicadorUDP extends Comunicador implements Closeable {
    
    /**
     * Responsavel pelo recebimento de mensagens. Quando uma mensagem eh
     * recebida eh inserida na fila de recebimento.
     */
    private static class Receptor extends ThreadEscrava { 
    
        private long numeroDeSequencia = 0;
        private final DatagramSocket SOCKET;
        private final int TAMANHO_DA_MENSAGEM;
        private final Mensageiro MENSAGEIRO;
        
        public Receptor(DatagramSocket socket,
                int tamanhoDaMensagem, 
                Mensageiro mensageiro) 
                throws IOException {
            
            this.SOCKET = socket;
            this.TAMANHO_DA_MENSAGEM = tamanhoDaMensagem;
            this.MENSAGEIRO = mensageiro;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {
                try {
                    byte[] mensagem = new byte[this.TAMANHO_DA_MENSAGEM]; 
                    DatagramPacket pacote = new DatagramPacket(mensagem, mensagem.length);
                    
                    // Loop de escuta
                    do {
                        try {
                            this.SOCKET.receive(pacote);
                            break;
                        } catch(SocketTimeoutException ste) {
                            if(!this.emExecucao()) break;
                        } catch(java.net.SocketException se) {
                            throw new FalhaDeComunicacaoEmTempoRealException("Erro no socket: " + se.getMessage());
                        }
                    } while(true);
                    
                    if(this.emExecucao()) {
                        byte[] dados = pacote.getData();
                        if(dados != null) {
                            MensagemComunicador mensagemComunicador = (MensagemComunicador) this.converterParaObjeto(dados);
                            
                            if(this.numeroDeSequencia <= mensagemComunicador.getNumeroDeSequencia()) {
                                this.numeroDeSequencia = mensagemComunicador.getNumeroDeSequencia();
                                this.MENSAGEIRO.inserirFilaRecebimento(mensagemComunicador.getConteudo());
                            }
                        }
                    }
                } catch(EOFException eofe) { 
                    throw new FalhaDeComunicacaoEmTempoRealException("Conexao fechada: " + eofe.getMessage());
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                    throw new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel receber a mensagem: " + ioe.getMessage());
                }
            }
        }
        
        // Retirado de <https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array>
        public Object converterParaObjeto(byte[] bytes) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                Object objeto = in.readObject();
                return objeto;
            } catch (IOException ioe) {
                return null;
            } catch (ClassNotFoundException cnfe) {
                return null;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }
            }
        }
    }
    
    
    /**
     * Faz o envio da mensagem para o destino, retirando as mensagens da fila
     * acessada atraves de um objeto da classe Mensageiro.
     */
    private static class Enviador extends ThreadEscrava implements Runnable {
        
        private final Comunicador COMUNICADOR;
        private final DatagramSocket SOCKET;
        private final Mensageiro MENSAGEIRO;
        
        public Enviador(
                Comunicador comunicador,
                DatagramSocket socket,
                Mensageiro mensageiro) 
                throws IOException {
            
            this.COMUNICADOR = comunicador;
            this.SOCKET = socket;
            this.MENSAGEIRO = mensageiro;
        }
        
        @Override
        public void run() {
            super.executar();
            while(super.emExecucao()) {    
                byte[] mensagem = this.MENSAGEIRO.removerFilaEnvioUDP();
                if(mensagem != null) {
                    try {
                        MensagemComunicador mensagemComunicador = new MensagemComunicador(this.COMUNICADOR.incrementarEObterNumeroDeSequenciaDeEnvio(), TipoMensagem.MENSAGEM_COMUM, mensagem);
                        byte[] mensagemEnvio = this.converterParaBytes(mensagemComunicador);
                        DatagramPacket pacote = new DatagramPacket(
                                                        mensagemEnvio,
                                                        mensagemEnvio.length);     
                        this.SOCKET.send(pacote);
                    } catch(IOException ioe) {
                        throw new FalhaDeComunicacaoEmTempoRealException("Nao foi possivel enviar a mensagem: " + ioe.getMessage());
                    }
                }
            }
        }
        
        // Retirado de <https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array>
        private byte[] converterParaBytes(Serializable objeto) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(objeto);
                out.flush();
                byte[] yourBytes = bos.toByteArray();
                return yourBytes;
            } catch (IOException ioe) {
                return null;
            } finally {
                try {
                    bos.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
            }
        }
    }
    
    /* ###################################################################### */
    
    private DatagramSocket socket;
    
    private Thread threadReceptor;
    private Thread threadEnviador;
    private Receptor receptor;
    private Enviador enviador; 
    private final UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
    
    private final int TAMANHO_DA_MENSAGEM;
    private final int TEMPO_LIMITE_ESCUTA = 100; //ms
    
    /**
     * Constroi o objeto sem iniciar nenhum tipo de comportamento.
     * 
     * @param modo Modo de operacao
     * @param mensageiro Mensageiro de onde as mensagens serao tiradas e entregues
     * @param gerenciadorDeException Handler de exceptions lancadas a partir das threads
     * @param tamanhoDaMensagem Tamanho em bytes da mensagem a ser enviada
     */
    public ComunicadorUDP(Modo modo,
            Mensageiro mensageiro,
            UncaughtExceptionHandler gerenciadorDeException,
            int tamanhoDaMensagem) {
        
        super(modo, mensageiro);
        
        this.validarArgumentos(gerenciadorDeException, tamanhoDaMensagem);
        this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
        this.TAMANHO_DA_MENSAGEM = tamanhoDaMensagem;
    }
    
    private void validarArgumentos(
            UncaughtExceptionHandler gerenciadorDeException,
            int tamanhoDaMensagem) {
        if(gerenciadorDeException == null) {
            throw new IllegalArgumentException("O gerenciador de exception nao pode ser nulo");
        }
        if(tamanhoDaMensagem < 0) {
            throw new IllegalArgumentException("O tamanho da mensagem nao pode ser menor do que 1");
        }
    }
    
    /**
     * Configura o comunicador criando e configurando o socket, e criando as
     * threads de comunicacao e iniciando a execucacao da thread de recepcao.
     * 
     * @param portaDeEscuta A porta de recebimento das mensagens. Caso valor < 1
     *                      a porta eh escolhida aleatoriamente pelo proprio
     *                      construtor do socket
     * @throws IOException Erro ao configurar o socket
     */
    //@Override
    public void iniciar(int portaDeEscuta) throws IOException {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Iniciando comunicador.");
        
        if(portaDeEscuta < 1) {
            this.socket = new DatagramSocket();
        } else {
            this.socket = new DatagramSocket(portaDeEscuta);
        }
        this.socket.setSoTimeout(this.TEMPO_LIMITE_ESCUTA);
        Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Iniciando socket na porta " + this.socket.getLocalPort());
                
        this.prepararThreadsDeComunicacao();
        Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Iniciando thread de recepcao");
        this.threadReceptor.start();

        this.estaAberto = true;
    }

    public void definirDestinatario(InetAddress enderecoServidor, int portaServidor) {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Definindo destino padrao: " + enderecoServidor + ":" + portaServidor);
        this.socket.connect(enderecoServidor, portaServidor);
        Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Iniciando thread de envio");
        this.threadEnviador.start();
    }
    
    /**
     * Retorna a porta em que o Socket esta escutando ou -1 caso o Socket nao
     * tenha sido construido.
     * @return Numero da porta escutada pelo Socket TCP.
     */
    @Override
    public int getPortaDeEscuta() {
        /**
         *  [!] IMPORTANTE
         *  A constante this.PORTA_ESCUTA eh a porta de escuta definida pelo 
         *  usuario, que caso seja inferior a -1 significa que o socket recebera
         *  um porta aleatoria. Sendo assim, seu valor nao dever ser retornado, 
         *  mas sim o do Socket caso seja diferente de nulo ou -1 caso contrario.
         */
        if(this.socket == null) {
            return -1;
        }
        return this.socket.getLocalPort();
    }
    
    /**
     * Fecha o socket a interrompe as threads
     */
    @Override
    public void close() {
        Logger.registrar(INFO, new String[]{"COMUNICADOR_UDP"}, "Fechando comunicador");
        this.estaAberto = false;
        this.receptor.pararExecucao();
        this.enviador.pararExecucao();
        this.socket.disconnect();
        this.socket.close();
        this.threadReceptor.interrupt();
        this.threadEnviador.interrupt();
    }
    
    private void prepararThreadsDeComunicacao() throws IOException {
        try {
            this.enviador = new Enviador(this, this.socket, super.MENSAGEIRO);
            
            this.receptor = new Receptor(
                    this.socket, 
                    this.TAMANHO_DA_MENSAGEM, 
                    super.MENSAGEIRO);
                
            this.criarThreadReceptor();
            this.criarThreadEnviador();
        } catch(IOException ioe) {
            throw new IOException("Nao eh possivel criar as threads de comunicacao: " + ioe.getMessage());
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
package comunicacao;

import static comunicacao.Comunicador.TipoMensagem.FECHAR_CONEXAO;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import static comunicacao.Comunicador.TipoMensagem.PEDIR_FECHAMENTO_CONEXAO;

/**
 * Gerencia uma comunicacao TCP entre o cliente e o servidor. As mensagens a
 * serem enviadas sao salvas em uma lista ligada thread-safe.
 */
public class ComunicadorTCP extends Comunicador implements Closeable {
    
    /**
     * Responsavel pelo recebimento de mensagens. Quando uma mensagem eh
     * recebida encaminha para o ReceptorDeMensagem associado.
     */
    private static class Receptor implements Runnable, Closeable { 
    
        private final ObjectInputStream ENTRADA;
        private final ReceptorDeMensagem<byte[]> RECEPTOR_DE_MENSAGEM;
        private final Enviador ENVIADOR;
    
        private boolean executando;
        
        public Receptor(ObjectInputStream entrada, 
                ReceptorDeMensagem<byte[]>  receptorDeMensagem,
                Enviador enviador) 
                throws IOException {
            
            this.ENTRADA = entrada;
            this.RECEPTOR_DE_MENSAGEM = receptorDeMensagem;
            this.ENVIADOR = enviador;
            
            this.executando = true;
        }
        
        @Override
        public void run() {
            
            while(this.emExecucao()) {
                
                try {
                    TipoMensagem controle = (TipoMensagem) this.ENTRADA.readObject(); 
                    if(controle.equals(FECHAR_CONEXAO)) {
                        this.pararExecucao();
                        continue;
                    } else if(controle.equals(PEDIR_FECHAMENTO_CONEXAO)) {
                        this.pararExecucao();
                        this.ENVIADOR.enviarFechamentoDaConexao();
                        continue;
                    }
                        
                    byte[] mensagem = (byte[]) this.ENTRADA.readObject();
                    this.RECEPTOR_DE_MENSAGEM.receberMensagem(mensagem);
                } catch(EOFException eofe) { 
                    throw new FalhaDeComunicacaoEmTempoRealException("Conexão fechada: " + eofe.getMessage());
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                    throw new FalhaDeComunicacaoEmTempoRealException("Não foi possível receber a mensagem: " + ioe.getMessage());
                } catch(ClassNotFoundException cnfe) {
                    throw new RuntimeException("Classe não encontrada: " + cnfe.getMessage());
                }
            }
        }
        
        public synchronized boolean emExecucao() {
            return this.executando;
        }
        
        public synchronized void pararExecucao() {
            this.executando = false;
        }

        @Override
        public void close() throws IOException {
            this.pararExecucao();
            this.ENTRADA.close();
        }
    }
    
    
    /**
     * Faz o envio da mensagem para o destino. O metodo run() executa enquanto
     * houverem mensagens na fila aguardando para serem enviadas. Tambem eh
     * possivel que o metodo run() seja interrompido pelos metodo
     * pararExecucao() ou close().
     */
    private static class Enviador implements Runnable, Closeable {
        
        private final ObjectOutputStream SAIDA;
        private final GerenciadorDeFilaDeMensagens MENSAGENS_PARA_ENVIAR;
        
        private boolean executando;
        
        public Enviador(ObjectOutputStream saida, 
                GerenciadorDeFilaDeMensagens mensagensParaEnviar) 
                throws IOException {
            
            this.SAIDA = saida;
            this.MENSAGENS_PARA_ENVIAR = mensagensParaEnviar;
        }
        
        @Override
        public void run() {
            this.executando = (this.MENSAGENS_PARA_ENVIAR.tamanho() > 0);
            
            while(this.emExecucao()) {    
                byte[] mensagem = this.MENSAGENS_PARA_ENVIAR.remover();
                if(mensagem != null) {
                    try {
                        this.SAIDA.writeObject(TipoMensagem.RECEBER_MENSAGEM);
                        this.SAIDA.writeObject(mensagem);
                        this.SAIDA.flush();
                    } catch(IOException ioe) {
                        throw new FalhaDeComunicacaoEmTempoRealException("Não foi possível enviar a mensagem: " + ioe.getMessage());
                    }
                } 
                
                // Dorme para dar tempo de outras mensagens serem colocadas na fila
                this.esperar(10);
                
                this.executando = (this.MENSAGENS_PARA_ENVIAR.tamanho() > 0);
            }
        }
        
        public synchronized boolean emExecucao() {
            return this.executando;
        }
        
        public synchronized void pararExecucao() {
            this.executando = false;
        }
        
        @Override
        public void close() throws IOException {
            this.pararExecucao();
            this.SAIDA.close();
        }
        
        public void enviarPedidoFechamentoDaConexao() throws IOException {
            if(this.emExecucao()) {
                this.SAIDA.writeObject(PEDIR_FECHAMENTO_CONEXAO);
                this.SAIDA.flush();
            }
        }
        
        public void enviarFechamentoDaConexao() throws IOException {
            if(this.emExecucao()) {
                this.SAIDA.writeObject(FECHAR_CONEXAO);
                this.SAIDA.flush();
            }
        }
        
        private void esperar(int tempo) {
            try {
                new Thread().sleep(tempo);
            } catch(InterruptedException ie) {
                // Nao faz nada
            }
        }
    }
    
    /* ###################################################################### */
    
    private Socket socket;  
    
    private Thread threadReceptor;
    private Thread threadEnviador;
    private Receptor receptor;
    private Enviador enviador; 
    private UncaughtExceptionHandler GERENCIADOR_DE_EXCEPTION;
    
    public ComunicadorTCP(Modo modo,
            ReceptorDeMensagem<byte[]> receptorDeMensagem,
            UncaughtExceptionHandler gerenciadorDeException,
            int tamanhoDaFilaDeEnvio) {

            super(Comunicador.Modo.SERVIDOR, receptorDeMensagem, tamanhoDaFilaDeEnvio);
            
            if(gerenciadorDeException == null) {
                throw new IllegalArgumentException("O gerenciador de exception não pode ser nulo");
            }
            this.GERENCIADOR_DE_EXCEPTION = gerenciadorDeException;
    }
    
    
    @Override
    public void iniciar(InetAddress enderecoServidor, int portaServidor) throws IOException {
        Socket socket = this.abrirSocket(enderecoServidor, portaServidor);
        this.carregarSocket(socket);
        this.prepararThreadsDeComunicacao();
        this.threadReceptor.start();
        super.aberto = true;
    }
    
    public void iniciar(Socket socket) throws IOException {
        this.carregarSocket(socket);
        this.prepararThreadsDeComunicacao();
        this.threadReceptor.start();
        super.aberto = true;
    }
    
    public void encerrarConexao() throws IOException {
        this.enviador.enviarFechamentoDaConexao();
    }
    
    @Override
    public void close() throws IOException {
        if(super.aberto) {
            this.aberto = false;
            
            try {
                this.enviador.close();
                this.receptor.close();
            } catch(SocketException se) {
            
            } catch(IOException ioe) {
            
            }
            
            this.socket.close();

            this.threadEnviador.interrupt();
            this.threadReceptor.interrupt();
        }
        super.aberto = false;
    }
    
    @Override
    public synchronized void enviarMensagem(byte[] mensagem) {
        this.MENSAGENS_PARA_ENVIAR.inserir(mensagem);
        
        // Uma thread de envio permanece ativa apenas enquanto existem mensagens
        // na fila ou ate ser interrompida.
        if(!this.threadEnviador.isAlive()) {
            this.criarThreadEnviador();
            this.threadEnviador.start();
        }
    }

    
    private Socket abrirSocket(InetAddress enderecoServidor, int portaServidor) throws IOException {
        Socket socket = null;
        try {
            socket = new Socket(enderecoServidor, portaServidor);
        } catch (IOException ioe) {
            throw new IOException ("Nao eh possivel se conectar ao servidor", ioe);
        }
        return socket;
    }
    
    private void carregarSocket(Socket socket) {
        if(socket == null) {
            throw new IllegalArgumentException("O socket não pode ser nulo");
        }
        
        if(socket.isClosed()) {
            throw new IllegalArgumentException("O socket não pode estar fechado");
        }
        
        this.socket = socket;
    }
    
    private void prepararThreadsDeComunicacao() throws IOException {
        try {
            ObjectOutputStream saida = null;
            ObjectInputStream entrada = null;
            
            if(null == super.MODO) {
                throw new RuntimeException("Não é possível prepararar as threads de comunicacação");
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
                    throw new RuntimeException("Não é possível prepararar as threads de comunicacação");
            }
            
            this.enviador = new Enviador(saida, super.MENSAGENS_PARA_ENVIAR);
            this.receptor = new Receptor(entrada, super.RECEPTOR_DE_MENSAGEM, this.enviador);
            
            this.criarThreadEnviador();
            this.criarThreadReceptor();
        } catch(IOException ioe) {
            throw new IOException("Não é possível criar as threads de comunicação: " + ioe.getMessage());
        }
    }
    
    private void criarThreadReceptor() {
        this.threadReceptor = new Thread(this.receptor);
        this.threadReceptor.setUncaughtExceptionHandler(GERENCIADOR_DE_EXCEPTION);
        this.threadReceptor.setName("Receptor_TCP");
    }
    
    private void criarThreadEnviador() {
        this.threadEnviador = new Thread(this.enviador);
        this.threadEnviador.setUncaughtExceptionHandler(GERENCIADOR_DE_EXCEPTION);
        this.threadEnviador.setName("Enviador_TCP");
    }
}
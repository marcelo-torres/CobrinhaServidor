package stub;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import static Logger.Logger.Tipo.INFO;
import cliente.Jogador;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import stub.comunicacao.Comunicador;
import stub.comunicacao.Mensageiro;

public class GerenciadorDeCliente implements Jogador, Closeable {
    
    private final Semaphore SEMAFORO_ATICAO_UDP = new Semaphore(0);
    
    private final GerenciadorDeException GERENCIADOR_DE_EXCEPTION;
    private final InterpretadorServidor INTERPRETADOR = new InterpretadorServidor();
    private final Mensageiro MENSAGEIRO;

    private final InetAddress ENDERECO_DO_SERVIDOR;
    
    private Receptor receptor;
    private Thread threadDeRecepcao;
    
    private Pattern PADRAO_NUMERO = Pattern.compile("\\d+");
    private boolean hostProntoParaReceberUDP = false;
    
    public GerenciadorDeCliente(
            int portaEscutarUDP,
            InetAddress enderecoDoServidor,
            int portaTCPDoServidor,
            int portaUDPDoServidor) {
    
        this.GERENCIADOR_DE_EXCEPTION = new GerenciadorDeException(this);
        
        this.MENSAGEIRO = new Mensageiro(
                Comunicador.Modo.CLIENTE,
                portaEscutarUDP,
                enderecoDoServidor,
                portaTCPDoServidor,
                portaUDPDoServidor,
                this.GERENCIADOR_DE_EXCEPTION);
        
        this.ENDERECO_DO_SERVIDOR = enderecoDoServidor;
    }
    
    
    public void receberMensagem(byte[] mensagem) {
        if(mensagem == null) {
            System.out.println("[!] Mano, vc ta jogando uma mensagem nula no interpretador! O que vc tem na cabeça tiw? Programa direito zeh mane");
        }
        
        String mensagemInterpretada = this.INTERPRETADOR.interpretar(mensagem);
        
        String codigo = mensagemInterpretada.substring(0, 3);
        String conteudo = mensagemInterpretada.substring(4, mensagemInterpretada.length());
        
        switch(codigo) {
            case "MSG":
                System.out.println("[Interpretador] Mensagem recebida: " + conteudo);
                break;
            case "COM":
                if(conteudo.startsWith("UDP_ABRIR")) {
                    Matcher matcher = PADRAO_NUMERO.matcher(conteudo);
                    if(matcher.find()) {
                        String numero = matcher.group(0);
                        int portaDoOutroLado = Integer.valueOf(numero);
                        this.atenderPedidoInicioDeAberturaUDP(portaDoOutroLado);
                    } else {
                        throw new RuntimeException("Mensagem indecifravel");
                    }
                } else if(conteudo.startsWith("UDP_ABERTO")) {
                    Matcher matcher = PADRAO_NUMERO.matcher(conteudo);
                    if(matcher.find()) {
                        String numero = matcher.group(0);
                        int portaDoOutroLado = Integer.valueOf(numero);
                        this.continuarAberturaUDP(portaDoOutroLado);
                    } else {
                        throw new RuntimeException("Mensagem indecifravel");
                    }
                } else {
                    Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Tem um comando estranho aqui ooo: " + conteudo);
                    // nao faz nada
                }
                
                break;
            default:
                System.out.println("[Interpretador] Mensagem ESTRANHA recebida: " + mensagemInterpretada);
        }
    }
    
    private void iniciarPedidoDeAberturaUDP() {
        try {
            if(!this.MENSAGEIRO.comunicadorUDPEstaAberto()) {
                this.MENSAGEIRO.iniciarUDP(-1);
                String mensagem = "COM UDP_ABBRIR " + this.MENSAGEIRO.getPortaEscutaUDP();
                this.MENSAGEIRO.inserirFilaEnvioTCPNaFrente(mensagem.getBytes());
            }
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro ao tentar iniciar a comunicacao.", ioe);
            this.SEMAFORO_ATICAO_UDP.release();
            throw new RuntimeException("Nao foi possivel iniciar a comunicacao com o servidor");
        }
    }
    
    private void atenderPedidoInicioDeAberturaUDP(int portaUDPServidor) {
        try {
            if(!this.MENSAGEIRO.comunicadorUDPEstaAberto()) {
                this.MENSAGEIRO.iniciarUDP(-1);
            }
            String mensagem = "COM UDP_ABERTO " + this.MENSAGEIRO.getPortaEscutaUDP();
            this.MENSAGEIRO.inserirFilaEnvioTCPNaFrente(mensagem.getBytes());
            this.MENSAGEIRO.definirDestinatario(ENDERECO_DO_SERVIDOR, portaUDPServidor);
            this.hostProntoParaReceberUDP = true;
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro ao tentar iniciar a comunicacao.", ioe);
            this.SEMAFORO_ATICAO_UDP.release();
            throw new RuntimeException("Nao foi possivel iniciar a comunicacao com o servidor");
        }
    }
    
    private void continuarAberturaUDP(int portaUDPServidor) {
        this.MENSAGEIRO.definirDestinatario(this.ENDERECO_DO_SERVIDOR, portaUDPServidor);
        this.hostProntoParaReceberUDP = true;
        this.SEMAFORO_ATICAO_UDP.release();
    }
    
    
    
    @Override
    public void close() {
        this.MENSAGEIRO.close();
        this.receptor.parar();
        this.threadDeRecepcao.interrupt();
    }
    
    
    
    public void iniciar(Socket socket) throws Exception {
        try {
            this.MENSAGEIRO.iniciarTCP(socket);
            this.iniciarServicoDeRecepcao();
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro ao tentar iniciar a comunicacao.");
            throw new Exception("Nao foi possivel iniciar a comunicacao com o servidor");
        }
    } 
    
    private void prepararThreadDeEntrega() {
        this.receptor = new Receptor(this, this.MENSAGEIRO);
        this.threadDeRecepcao = new Thread(this.receptor);
        this.threadDeRecepcao.setName("Entrega_Mensagem");
    }
    
    private void iniciarServicoDeRecepcao() {
        if(this.receptor == null || this.threadDeRecepcao == null) {
            this.prepararThreadDeEntrega();
            this.threadDeRecepcao.start();
        }
    }
    
    
    
    /* ########################### CHAMADAS DE RPC ########################## */
    
    @Override
    public void iniciarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void desistirDeProcurarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void encerrarPartida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaCima() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaBaixo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaEsquerda() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void andarParaDireita() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    /* ############################## CLASSES ############################### */
    
    /**
     * Possui como funcao retirar mensagens da fila de recebimento e entregar
     * ao ControladorDeConexao.
     */
    public static class Receptor implements Runnable {
        
        protected final GerenciadorDeCliente GERENCIADOR_DE_CLIENTE;
        protected final Mensageiro MENSAGEIRO;
        
        protected boolean emEexecucao = false;
        
        public Receptor(GerenciadorDeCliente gerenciadorDeCliente, Mensageiro mensageiro) {
            this.GERENCIADOR_DE_CLIENTE = gerenciadorDeCliente;
            this.MENSAGEIRO = mensageiro;
        }
        
        
        @Override
        public void run() {
            this.emEexecucao = true;
            while(this.emExecucao()) {
                byte[] mensagem = this.MENSAGEIRO.removerFilaRecebimento();
                if(mensagem != null) {
                    this.GERENCIADOR_DE_CLIENTE.receberMensagem(mensagem);
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
     * Gerenciador de exceptions nao capturadas por metodos, isto eh, que
     * ocorreram em outras threads.
     */
    public class GerenciadorDeException implements Thread.UncaughtExceptionHandler {
    
        private final GerenciadorDeCliente GERENCIADOR_DE_CLIENTE;
        
        public GerenciadorDeException(GerenciadorDeCliente controlador) {
            this.GERENCIADOR_DE_CLIENTE = controlador;
        }
        
        @Override
        public void uncaughtException(Thread th, Throwable ex) {
            Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro na comunicacao: " + ex.getMessage());
            Logger.registrar(INFO, new String[]{"INTERPRETADOR"}, "Encerrando devido a falha de comunicacao");
            this.GERENCIADOR_DE_CLIENTE.close();
        }
        
    }
}
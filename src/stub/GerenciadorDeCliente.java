package stub;

import Logger.Logger;
import static Logger.Logger.Tipo.ERRO;
import static Logger.Logger.Tipo.INFO;
import cliente.Jogador;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import stub.comunicacao.Comunicador;
import stub.comunicacao.Mensageiro;

public class GerenciadorDeCliente implements Jogador, Closeable {
    
    private final GerenciadorDeException GERENCIADOR_DE_EXCEPTION;
    private final InterpretadorServidor INTERPRETADOR = new InterpretadorServidor();
    private final Mensageiro MENSAGEIRO;

    private Receptor receptor;
    private Thread threadDeRecepcao;
    
    
    public GerenciadorDeCliente(
            int portaEscutarUDP,
            InetAddress enderecoDoServidor,
            int portaTCPDoServidor,
            int portaUDPDoServidor) {
    
        this.GERENCIADOR_DE_EXCEPTION = new GerenciadorDeException(this);
        
        this.MENSAGEIRO = new Mensageiro(
                Comunicador.Modo.SERVIDOR,
                portaEscutarUDP,
                enderecoDoServidor,
                portaTCPDoServidor,
                portaUDPDoServidor,
                this.GERENCIADOR_DE_EXCEPTION);
    }
    
    
    public void receberMensagem(byte[] mensagem) {
        if(mensagem == null) {
            System.out.println("[!] Mano, vc ta jogando uma mensagem nula no interpretador! O que vc tem na cabeça tiw? Programa direito zeh mane");
        }
        System.out.println("[Interpretador] Mensagem recebida: " + new String(mensagem));
    }
    
    @Override
    public void close() {
        this.MENSAGEIRO.close();
        throw new UnsupportedOperationException();
    }
    
    
    
    public void iniciar(Socket socket) throws Exception {
        try {
            this.MENSAGEIRO.iniciarTCP(socket);
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro ao tentar iniciar a comunicacao.");
            throw new Exception("Nao foi possivel iniciar a comunicacao com o servidor");
        }
    }
    
    
    public void enviarMensagemTCPLembrarDeApagarEsteMetodo(byte[] mensagem) {
        this.MENSAGEIRO.inserirFilaEnvioTCP(mensagem);
    }
    
    public void enviarMensagemUDPLembrarDeApagarEsteMetodo(byte[] mensagem) {
        this.MENSAGEIRO.inserirFilaEnvioUDP(mensagem);
    }
    
    public void algumMetodoQueVaiPrecisarUsarConexaoUDP() {
        try {
            this.MENSAGEIRO.iniciarUDP();
        } catch(IOException ioe) {
            // Transparencia total eh impossivel
            throw new RuntimeException("Nao foi possivel executar o metodo algumMetodoQueVaiPrecisarUsarConexaoUDP");
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
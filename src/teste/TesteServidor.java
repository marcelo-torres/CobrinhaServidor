package teste;

import comunicacao_geral.GerenciadorDeConexoes;
import comunicacao_geral.Listener;
import controller.ControladorGeral;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TesteServidor {
 
    public static void main(String[] args) {
        String enderecoServidorBind = "127.0.0.1";
        int PORTA_DE_ESCUTA = 2573;
        if(args.length >= 2) {
            enderecoServidorBind = args[0];
            PORTA_DE_ESCUTA = Integer.valueOf(args[1]);
        }
        
        Servidor servidor = new Servidor(enderecoServidorBind, PORTA_DE_ESCUTA);
        new Thread(servidor).start();
    }
    
    public static class Servidor implements Runnable {
 
        private final int PORTA;
        private final String ENDERECO_SERVIDOR_BIND;
        private ListenerTeste listener;
        private GerenciadorDeConexoesTeste controladorGeral;

        public Servidor(String enderecoServidorBind, int porta) {
            this.ENDERECO_SERVIDOR_BIND = enderecoServidorBind;
            this.PORTA = porta;
            this.controladorGeral = new GerenciadorDeConexoesTeste();
        }

        @Override
        public void run() {
            InetAddress enderecoServidor = null;
            try {
                enderecoServidor = InetAddress.getByName(ENDERECO_SERVIDOR_BIND);
            } catch(UnknownHostException uhe) {
                throw new RuntimeException("Erro: " + uhe.getMessage());
            }     
            
            this.listener = new ListenerTeste(enderecoServidor, this.PORTA, new GerenciadorDeConexoesTeste());
            this.listener.run();
        }
    }    
}
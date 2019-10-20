package teste;

import comunicacao_geral.GerenciadorDeConexoes;
import comunicacao_geral.Listener;
import controller.ControladorGeral;

public class TesteServidor {
 
    public static void main(String[] args) {
        int PORTA_DE_ESCUTA = 2573;
        Servidor servidor = new Servidor(PORTA_DE_ESCUTA);
        new Thread(servidor).start();
    }
    
    public static class Servidor implements Runnable {
 
        private final int PORTA;
        private Listener listener;
        private GerenciadorDeConexoesTeste controladorGeral;

        public Servidor(int porta) {
            this.PORTA = porta;
            this.controladorGeral = new GerenciadorDeConexoesTeste();
        }

        @Override
        public void run() {
            this.listener = new Listener(this.PORTA, new GerenciadorDeConexoesTeste());
            this.listener.run();
        }
    }    
}
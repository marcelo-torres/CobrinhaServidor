import comunicacao_geral.Listener;
import comunicacao_geral.GerenciadorDeConexoes;

public class Servidor implements Runnable {
 
    private final int PORTA;
    private Listener listener;
 
    
    public Servidor(int porta) {
        this.PORTA = porta;
    }
    
    
    @Override
    public void run() {
        this.listener = new Listener(this.PORTA, new GerenciadorDeConexoes());
        this.listener.run();
    }
    
    public static void main(String[] args) {
    
        Servidor servidor = new Servidor(2573);
        new Thread(servidor).start();
        
    }
    
}

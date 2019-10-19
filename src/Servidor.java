import comunicacao_geral.Listener;
import comunicacao_geral.GerenciadorDeConexoes;
import controller.ControladorGeral;

public class Servidor implements Runnable {
 
    private final int PORTA;
    private Listener listener;
    private ControladorGeral controladorGeral;
 
    
    public Servidor(int porta) {
        this.PORTA = porta;
        this.controladorGeral = new ControladorGeral();
    }
    
    
    @Override
    public void run() {
        this.listener = new Listener(this.PORTA, new GerenciadorDeConexoes(this.controladorGeral));
        this.listener.run();
    }
    
    public static void main(String[] args) {
        
        Servidor servidor = new Servidor(2573);
        new Thread(servidor).start();
        
    }
    
}

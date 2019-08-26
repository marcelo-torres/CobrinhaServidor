
import comunicacao.Listener;

public class Servidor implements Runnable {
 
    private int porta;
    private Listener listener;
 
    public Servidor(int porta) {
        this.porta = porta;
    }
            
    @Override
    public void run() {
        this.listener = new Listener(this.porta);
        this.listener.run();
    }
    
    public static void main(String[] args) {
    
        Servidor servidor = new Servidor(1234);
        new Thread(servidor).start();

    }
    
}

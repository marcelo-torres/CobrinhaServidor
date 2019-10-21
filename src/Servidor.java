import comunicacao_geral.Listener;
import comunicacao_geral.GerenciadorDeConexoes;
import controller.ControladorGeral;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Servidor implements Runnable {
 
    private final InetAddress ENDERECO_ESCUTA;
    private final int PORTA;
    private Listener listener;
    private ControladorGeral controladorGeral;
 
    
    public Servidor(InetAddress enderecoEscuta, int porta) {
        this.ENDERECO_ESCUTA = enderecoEscuta;
        this.PORTA = porta;
        this.controladorGeral = new ControladorGeral();
    }
    
    
    @Override
    public void run() {
        this.listener = new Listener(this.ENDERECO_ESCUTA, this.PORTA, new GerenciadorDeConexoes(this.controladorGeral));
        this.listener.run();
    }
    
    public static void main(String[] args) {
        
        /* ########################### ALTERE AQUI ########################## */
        
        String enderecoEscutaServidorString = "127.0.0.1";
        int portaDeEscuta = 2573;
        
        /* ################################################################## */
        
        InetAddress enderecoEscutaServidor = null;
        try {
            enderecoEscutaServidor = InetAddress.getByName(enderecoEscutaServidorString);
        } catch(UnknownHostException uhe) {
            throw new RuntimeException("Erro: " + uhe.getMessage());
        } 
        
        Servidor servidor = new Servidor(enderecoEscutaServidor, portaDeEscuta);
        new Thread(servidor).start();
        
    }
}
package comunicacao;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

public class Interpretador implements ReceptorDeMensagem<byte[]>{
    
    private ComunicadorTCP COMUNICADOR_TCP;
    private ComunicadorUDP COMUNICADOR_UDP;
    
    public Interpretador() {
    
    }
    
    public void definirComunicador(ComunicadorTCP comunicadorTCP, 
            ComunicadorUDP comunicadorUDP) {
        
        this.COMUNICADOR_TCP = comunicadorTCP;
        this.COMUNICADOR_UDP = comunicadorUDP;
    }

    @Override
    public void receberMensagem(byte[] mensagem) {
        System.out.println("[Interpretador] mensagem recebida: " + new String(mensagem));
    }
}

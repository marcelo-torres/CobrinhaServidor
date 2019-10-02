package stub.comunicacao;

import java.io.Closeable;
import java.net.InetAddress;

/**
 * Representa o destinatario final do sistema de entrega de mensagens oriundas
 * da rede.
 */
public abstract class Destinatario implements Closeable  {
    
    protected Mensageiro MENSAGEIRO;
    
    public Destinatario(
            Comunicador.Modo modo,
            int portaEscutarUDP,
            InetAddress enderecoDoServidor,
            int portaTCPDoServidor,
            int portaUDPDoServidor,
            Thread.UncaughtExceptionHandler gerenciadorDeException) {
        
        this.MENSAGEIRO = new Mensageiro(
                this,
                modo,
                portaEscutarUDP,
                enderecoDoServidor,
                portaTCPDoServidor,
                portaUDPDoServidor,
                gerenciadorDeException);
    }
    

    /**
     * Gerencia o recebimento das mensagens por parte da aplicacao destinataria.
     * 
     * @param mensagem Mensagem a ser recebida em bytes
     */
    public abstract void receberMensagem(byte[] mensagem);
    
}

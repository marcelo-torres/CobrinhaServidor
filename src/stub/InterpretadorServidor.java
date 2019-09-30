package stub;

import Logger.Logger;
import stub.comunicacao.Comunicador;
import stub.comunicacao.Mensageiro;
import static Logger.Logger.Tipo.ERRO;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class InterpretadorServidor implements Closeable {
 
    private final Mensageiro MENSAGEIRO;
    
    public InterpretadorServidor(
            int portaEscutarUDP,
            InetAddress enderecoDoServidor,
            int portaTCPDoServidor,
            int portaUDPDoServidor) {
        
        this.MENSAGEIRO = new Mensageiro(
                this,
                Comunicador.Modo.SERVIDOR,
                portaEscutarUDP,
                enderecoDoServidor,
                portaTCPDoServidor,
                portaUDPDoServidor);
    }
    
    public void iniciar(Socket socket) throws Exception {
        try {
            this.MENSAGEIRO.iniciarTCP(socket);
        } catch(IOException ioe) {
            Logger.registrar(ERRO, new String[]{"INTERPRETADOR"}, "Erro ao tentar iniciar a comunicacao.");
            throw new Exception("Nao foi possivel iniciar a comunicacao com o servidor");
        }
    }
    
    @Override
    public void close() {
        this.MENSAGEIRO.close();
    }
    
    public void receberMensagem(byte[] mensagem) {
        if(mensagem == null) {
            System.out.println("[!] Mano, vc ta jogando uma mensagem nula no interpretador! O que vc tem na cabeça tiw? Programa direito zeh mane");
        }
        System.out.println("[Interpretador] Mensagem recebida: " + new String(mensagem));
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
    
    
    /* ########################### CHAMADAS DE RPC ########################## */
    
    public void encerrarPartida() {
    
    }
}

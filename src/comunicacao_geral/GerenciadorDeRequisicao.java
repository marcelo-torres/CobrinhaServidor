package comunicacao_geral;

import java.net.Socket;

public interface GerenciadorDeRequisicao {
    
    public void gerenciarRequisicao(Socket socket);
    
}
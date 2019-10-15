package stub.comando.controlador_de_partida;

import stub.comando.Parametros;

public class FalhaAoLogarParametros extends Parametros {
    
    private String mensagem;
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    public String getMensagem() {
        return mensagem;
    }
}

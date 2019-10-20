package stub.comando.jogador;

import stub.comando.Parametros;

public class IniciarSessaoParametros extends Parametros {
    
    private String nomeJogador;
    
    public void setNomeJogador(String nomeJogador) {
        this.nomeJogador = nomeJogador;
    }
    
    public String getNomeJogador() {
        return this.nomeJogador;
    }
    
}


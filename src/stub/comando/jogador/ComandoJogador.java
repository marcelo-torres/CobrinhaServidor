package stub.comando.jogador;

import aplicacao.jogo.Jogador;
import stub.comando.Comando;

public abstract class ComandoJogador extends Comando {

    protected final Jogador JOGADOR;
    
    public ComandoJogador(String codigo, Jogador jogador) {
        super(codigo);
        this.JOGADOR = jogador;
    }
    
}
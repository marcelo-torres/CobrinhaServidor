package stub.comando.jogador;

import aplicacao.model.agentes.Jogador;
import stub.comando.Comando;

public abstract class ComandoJogador extends Comando {

    protected final Jogador JOGADOR;
    
    public ComandoJogador(String codigo, Jogador jogador) {
        super(codigo);
        this.JOGADOR = jogador;
    }
    
}
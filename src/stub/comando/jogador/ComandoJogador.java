package stub.comando.jogador;

import stub.comando.Comando;
import model.agentes.Jogador;

public abstract class ComandoJogador extends Comando {

    protected final Jogador JOGADOR;
    
    public ComandoJogador(String codigo, Jogador jogador) {
        super(codigo);
        this.JOGADOR = jogador;
    }
    
}
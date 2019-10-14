package stub.comando.jogador;

import model.agentes.IJogador;
import stub.comando.Comando;

public abstract class ComandoJogador extends Comando {

    protected final IJogador JOGADOR;
    
    public ComandoJogador(String codigo, IJogador jogador) {
        super(codigo);
        this.JOGADOR = jogador;
    }
    
}
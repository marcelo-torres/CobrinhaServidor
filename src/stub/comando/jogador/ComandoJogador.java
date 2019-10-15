package stub.comando.jogador;

import stub.comando.Comando;
import model.agentes.IJogador;

public abstract class ComandoJogador extends Comando {

    protected final IJogador JOGADOR;
    
    public ComandoJogador(String codigo, IJogador jogador) {
        super(codigo);
        this.JOGADOR = jogador;
    }
    
}
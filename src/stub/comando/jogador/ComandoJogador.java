package stub.comando.jogador;

import stub.comando.Comando;
import model.agentes.IJogador;

public abstract class ComandoJogador extends Comando {

    protected final IJogador JOGADOR;
    
    public ComandoJogador(String codigo, boolean possuiParametro, IJogador jogador) {
        super(codigo, possuiParametro);
        this.JOGADOR = jogador;
    }
    
}
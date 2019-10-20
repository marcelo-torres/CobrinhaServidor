package stub.comando.jogador;

import stub.comando.Comando;
import model.agentes.IJogadorVisaoStubServidor;

public abstract class ComandoJogador extends Comando {

    protected final IJogadorVisaoStubServidor JOGADOR;
    
    public ComandoJogador(String codigo, boolean possuiRetorno, IJogadorVisaoStubServidor jogador) {
        super(codigo, possuiRetorno);
        this.JOGADOR = jogador;
    }
    
}
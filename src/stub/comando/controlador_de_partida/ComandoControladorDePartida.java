package stub.comando.controlador_de_partida;

import aplicacao.jogo.ControladorDePartida;
import stub.comando.Comando;

public abstract class ComandoControladorDePartida extends Comando {
    
    protected final ControladorDePartida CONTROLADOR_PARTIDA;
    
    public ComandoControladorDePartida(String codigo, ControladorDePartida controladorPartida) {
        super(codigo);
        this.CONTROLADOR_PARTIDA = controladorPartida;
    }
    
}
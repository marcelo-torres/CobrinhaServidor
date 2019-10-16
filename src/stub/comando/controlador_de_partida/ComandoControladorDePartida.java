package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Comando;

public abstract class ComandoControladorDePartida extends Comando {
    
    protected final ControladorDePartida CONTROLADOR_PARTIDA;
    
    public ComandoControladorDePartida(String codigo, boolean possuiRetorno, ControladorDePartida controladorPartida) {
        super(codigo, possuiRetorno);
        this.CONTROLADOR_PARTIDA = controladorPartida;
    }
    
}
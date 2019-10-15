package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class VocePerdeu extends ComandoControladorDePartida {
    
    public VocePerdeu(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    @Override
    public void executar(Parametros parametros) {
        super.CONTROLADOR_PARTIDA.vocerPerdeu();
    }
}

package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class VocePerdeu extends ComandoControladorDePartida {
    
    public VocePerdeu(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, false, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        super.CONTROLADOR_PARTIDA.vocerPerdeu();
        return null;
    }
}

package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class VoceGanhou extends ComandoControladorDePartida {
    
    public VoceGanhou(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, false, controladorPartida);
    }
    
    @Override
    public Object executar(Parametros parametros) {
        super.CONTROLADOR_PARTIDA.voceGanhou();
        return null;
    }
}
package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class AdversarioSaiu extends ComandoControladorDePartida {

    public AdversarioSaiu(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    @Override
    public void executar(Parametros parametros) {
        super.CONTROLADOR_PARTIDA.adversarioSaiu();
    }
}
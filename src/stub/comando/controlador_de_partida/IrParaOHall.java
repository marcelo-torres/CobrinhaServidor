package stub.comando.controlador_de_partida;

import model.agentes.ControladorDePartida;
import stub.comando.Parametros;

public class IrParaOHall extends ComandoControladorDePartida {
    
    public IrParaOHall(String codigo, ControladorDePartida controladorPartida) {
        super(codigo, controladorPartida);
    }
    
    @Override
    public void executar(Parametros parametros) {
        super.CONTROLADOR_PARTIDA.irParaOHall();
    }
}
